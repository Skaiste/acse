package skaiste.API;

import skaiste.API.fetchers.CodeFetcher;
import skaiste.API.fetchers.ComboFetcher;
import skaiste.API.models.Bucket;
import skaiste.API.models.CodeModel;
import skaiste.API.models.MatchingBlock;
import skaiste.API.models.MatchingResult;
import skaiste.ASTparser.SuffixTree;
import skaiste.ASTparser.SuffixTreeNode;
import skaiste.ASTparser.SuffixTreeNodeStub;
import skaiste.ASTparser.SyntaxTree;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Matcher {

    private List<CodeModel> data;
    private CodeModel query;

    public Matcher(List<CodeModel> data, CodeModel query) {
        this.data = data;
        this.query = query;
    }

    public List<MatchingResult> matchSyntax() {
        List<MatchingResult> matches = new ArrayList<>();
        for (CodeModel d : data) {
            // check if they match, if they don't remove from the list
            if (matchtwo(d, query) && d.getMatch().getMatchingLines().size() > 0)
                matches.add(d.getMatch());
        }
        // sort the data
        data.sort((o1, o2) -> {
            if (o1.getMatch().getNoOfMatchingParts() > o2.getMatch().getNoOfMatchingParts())
                return -1;
            else if (o1.getMatch().getNoOfMatchingParts() < o2.getMatch().getNoOfMatchingParts())
                return 1;
            else
                return 0;
        });
        return matches;
    }

    private boolean matchtwo(CodeModel d, CodeModel q){
        MatchingResult mr = new MatchingResult(d.getOriginalCode(), q.getOriginalCode());
        d.setMatchingResult(mr);
        //return matchCodes(d.getCode(), q.getCode(), mr);
        return true;
    }

    private boolean matchCodes(SyntaxTree d, SyntaxTree q, MatchingResult mr) {
        // when the rule names match
        int e = 0;
        if (d.getNodeName().equals(q.getNodeName()) && d.getChildNodeHash() == q.getChildNodeHash()) {
            // if both of them are values
            if (d.getNodes() == null && q.getNodes() == null) {
                if (isNodeEOF(q) && isNodeEOF(d)){
                    return false;
                }
                else if (isNodeEOF(q) && !isNodeEOF(d)){
                    mr.moveDataCursor(d.getValue(), false);
                    return false;
                }
                else if (!isNodeEOF(q) && isNodeEOF(d)){
                    mr.moveQueryCursor(q.getValue(), false);
                    return false;
                }
                mr.moveDataCursor(d.getValue(), true);
                mr.moveQueryCursor(q.getValue(), true);
                mr.matchCurrentLines();
                return true;
            }
            boolean matches = false;
            for (int i = 0; i < d.getNodes().size() && i < q.getNodes().size(); i++){
                if (matchCodes(d.getNodes().get(i), q.getNodes().get(i), mr))
                    matches = true;
            }
            return matches;
        }
        else {
            // if any of the trees have no nodes
            if (d.getNodes() == null || q.getNodes() == null) {
                if (d.getNodes() == null) {
                    mr.moveDataCursor(d.getValue(),false);
                }
                if (q.getNodes() == null) {
                    mr.moveQueryCursor(q.getValue(), false);
                }
                return false;
            }
            else {
                boolean matches = false;
                for (SyntaxTree st : d.getNodes()) {
                    // check if the tags match
                    boolean doTagsMatch = intersection(st.getTags(), q.getTags()).size() == q.getTags().size();
                    if (matchCodes(st, q, mr))
                        matches = true;
                }
                return matches;
            }
        }
    }

    private boolean isNodeEOF(SyntaxTree st) {
        return st.getValue().equals("<EOF>");
    }

    private ArrayList<String> intersection(List<String> a,  List<String> b) {
        List<String> x, y, intersection = new ArrayList<>();
        if (a.size() < b.size()) {
            x = a; y = b;
        }
        else {
            x = b; y = a;
        }

        for (String tag : x)
            if (y.contains(tag))
                intersection.add(tag);

        return (ArrayList<String>)intersection;
    }



    // new implementation
    private Bucket bucket;
    private ArrayList<ComboFetcher> comboFetchers;
    private CodeFetcher codeFetcher;

    public Matcher(ArrayList<ComboFetcher> comboFetchers, CodeFetcher codeFetcher, CodeModel query) {
        this.comboFetchers = comboFetchers;
        this.codeFetcher = codeFetcher;
        bucket = new Bucket(query.getCode());
        this.query = query;
    }

    public ArrayList<MatchingBlock> newMatching() {
        boolean dataHasntEnded = true;
        while (dataHasntEnded) {
            dataHasntEnded = false;
            for (ComboFetcher cf : comboFetchers) {
                // check if there are some data left to go through
                if (!cf.isThereAnyMoreLeft() && cf.getSize() == 0) continue;
                dataHasntEnded = true;

                // get query nodes
                ArrayList<SuffixTreeNode> queryNodes = new ArrayList<>();
                for (UUID id : cf.getOriginNodeIds())
                    if (!bucket.isNodeAlreadyInBucket(id))
                        queryNodes.add(query.getCode().getNode(id));

                // get data nodes
                SuffixTreeNodeStub stns = cf.pop();
                CodeModel codeModel = codeFetcher.getCode(stns.getTreeId());
                ArrayList<SuffixTreeNode> dataNodes = new ArrayList<>();
                for (UUID id : stns.getNodeIds())
                    dataNodes.add(codeModel.getCode().getNode(id));

                // match nodes
                for (SuffixTreeNode queryNode : queryNodes){
                    for (SuffixTreeNode dataNode : dataNodes){
                        int similarity = matchNodes(queryNode, dataNode, query.getCode(), codeModel.getCode());
                        if (similarity > 0)
                            bucket.addToBucket(stns.getTreeId(), queryNode.getId(), dataNode.getId(), dataNode.getWeight(), similarity);
                    }
                }
            }
        }
        bucket.removeSubnodesWithinResults();
        ArrayList<MatchingBlock> matchingBlocks = bucket.convertBucketsToMatchingBlocks(query, codeFetcher);
        // the bucket should be filled with something
        return matchingBlocks;
    }

    // if nodes are end nodes and their values match give 2 points for similarity
    // if they don't match but their names match, give 1 point for similarity
    private int matchNodes(SuffixTreeNode q, SuffixTreeNode d, SuffixTree qt, SuffixTree dt) {
        int similarity = 0;

        // check if names match
        if (!q.getName().equals(d.getName())) return 0;
        // check if weight matches
        if (q.getWeight() != d.getWeight()) return 0;

        // check if they are end nodes TODO include filters
        if (q.getValue() != null && d.getValue() != null) {
            if (!q.getValue().equals(d.getValue())) return 1;
            return 2;
        }

        // check if they have children
        if (q.getChildren().size() > 0 && d.getChildren().size() > 0) {
            ArrayList<SuffixTreeNode> qchildren = qt.getChildNodes(q.getId());
            ArrayList<SuffixTreeNode> dchildren = dt.getChildNodes(d.getId());
            for (int i = 0; i < qchildren.size() && i < dchildren.size(); i++){
                int childSimilarity = matchNodes(qchildren.get(i), dchildren.get(i), qt, dt);
                if (childSimilarity == 0) return 0;
                similarity += childSimilarity;
            }
        }

        return similarity;
    }
}

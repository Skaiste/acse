package skaiste.API;

import skaiste.API.models.CodeModel;
import skaiste.API.models.MatchingResult;
import skaiste.ASTparser.SyntaxTree;

import java.util.ArrayList;
import java.util.List;

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
}

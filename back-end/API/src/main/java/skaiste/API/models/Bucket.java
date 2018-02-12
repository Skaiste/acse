package skaiste.API.models;

import skaiste.API.fetchers.CodeFetcher;
import skaiste.ASTparser.SuffixTree;
import skaiste.ASTparser.SuffixTreeNode;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

public class Bucket {

    private HashMap<UUID, ArrayList<BucketEntry>> bucket;

    private SuffixTree queryTree;
    private ArrayList<UUID> subnodesAlreadyIn;

    public Bucket(SuffixTree queryTree) {
        bucket = new HashMap<>();
        this.queryTree = queryTree;
        subnodesAlreadyIn = new ArrayList<>();
    }

    public void addToBucket(UUID tree, BucketEntry bucketEntry) {
        if (!bucket.containsKey(tree)) {
            bucket.put(tree, new ArrayList<>());
        }
        ArrayList<BucketEntry> bucketEntries = bucket.get(tree);
        bucketEntries.add(bucketEntry);
        updateSubnodes(bucketEntry.getQueryNode());
    }

    public void addToBucket(UUID tree, UUID queryNode, UUID dataNode, int weight, int similarity){
        if (!bucket.containsKey(tree)) {
            bucket.put(tree, new ArrayList<>());
        }
        ArrayList<BucketEntry> bucketEntries = bucket.get(tree);
        bucketEntries.add(new BucketEntry(queryNode, dataNode, weight, similarity));
        updateSubnodes(queryNode);
    }

    private void updateSubnodes(UUID id) {
        // get node
        SuffixTreeNode node = queryTree.getNode(id);

        // add node id to list
        if (!subnodesAlreadyIn.contains(id))
            subnodesAlreadyIn.add(id);

        // go through children and do the same
        for (UUID childId : node.getChildren())
            updateSubnodes(childId);
    }

    public boolean isNodeAlreadyInBucket(UUID id) {
        return subnodesAlreadyIn.contains(id);
    }

    public void removeSubnodesWithinResults() {

        Iterator it = bucket.entrySet().iterator();
        while (it.hasNext()) {
            // sort entries
            Map.Entry<UUID, ArrayList<BucketEntry>> hashMapEntry = (Map.Entry<UUID, ArrayList<BucketEntry>>) it.next();
            ArrayList<BucketEntry> entries = hashMapEntry.getValue();
            Collections.sort(entries, comparing(BucketEntry::getWeight).reversed());

            // go through each checking if there is a sub-node in the bucket and remove it
            for (int i = 0; i < entries.size(); i++){
                BucketEntry entry = entries.get(i);
                SuffixTreeNode node = queryTree.getNode(entry.getQueryNode());
                if (node.getChildren().size() > 0)
                    for (UUID id : node.getChildren())
                        removeFoundSubnodes(id, entries);
            }
        }
    }

    private void removeFoundSubnodes(UUID current, ArrayList<BucketEntry> entries) {
        for (BucketEntry e : entries){
            if (e.getQueryNode().equals(current)){
                entries.remove(e);
                break;
            }
        }
        SuffixTreeNode node = queryTree.getNode(current);
        if (node.getChildren().size() > 0)
            for (UUID id : node.getChildren())
                removeFoundSubnodes(id, entries);
    }

    public ArrayList<MatchingBlock> convertBucketsToMatchingBlocks(CodeModel qmodel, CodeFetcher codeFetcher) {
        ArrayList<MatchingBlock> matchingBlocks = new ArrayList<>();

        // create a list that link tree ids to similarity sums
        HashMap<Integer, UUID> sortingHat = new HashMap<>();
        Iterator it = bucket.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, ArrayList<BucketEntry>> hashMapEntry = (Map.Entry<UUID, ArrayList<BucketEntry>>) it.next();
            int similaritySum = 0;
            for (BucketEntry be : hashMapEntry.getValue()){
                similaritySum += be.getSimilarity();
            }
            sortingHat.put(similaritySum, hashMapEntry.getKey());
        }

        Iterator it1 = sortingHat.entrySet().iterator();
        while (it1.hasNext()) {
            UUID treeId = ((Map.Entry<Integer, UUID>) it1.next()).getValue();
            ArrayList<BucketEntry> entries = bucket.get(treeId);
            // get code model from code fetcher
            CodeModel dmodel = codeFetcher.getCode(treeId);
            // sort bucket entries
            sortBucketEntriesByPosition(entries, dmodel.getCode());
            MatchingBlock matchingBlock = new MatchingBlock(qmodel.getOriginalCode(), dmodel.getOriginalCode(), entries, qmodel.getCode(), dmodel.getCode());
            matchingBlocks.add(matchingBlock);
        }

        return matchingBlocks;
    }

    private ArrayList<BucketEntry> sortBucketEntriesByPosition(ArrayList<BucketEntry> bucketEntries, SuffixTree tree) {
        bucketEntries.sort(new Comparator<BucketEntry>() {
            @Override
            public int compare(BucketEntry o1, BucketEntry o2) {
                int p1 = tree.getNode(o1.getDataNode()).getPosition();
                int p2 = tree.getNode(o2.getDataNode()).getPosition();
                return (p1 > p2) ? 1 : (p1 < p2) ? -1 : 0;
            }
        });
        return bucketEntries;
    }
}

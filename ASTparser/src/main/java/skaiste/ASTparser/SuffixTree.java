package skaiste.ASTparser;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.*;

public class SuffixTree {
    // have a pointer to root node and list of nodes
    SuffixTreeNode rootNode;
    Map<UUID, SuffixTreeNode> nodes;

    public SuffixTree (ParseTree tree) {
        nodes = new HashMap<>();
        rootNode = parseTree(tree, null);
    }

    public SuffixTree() {
        nodes = new HashMap<>();
    }

    public void addSuffixTreeNode(SuffixTreeNode stn) {
        nodes.put(stn.getId(), stn);
    }

    public SuffixTree(SuffixTreeNode rootNode, Map<UUID, SuffixTreeNode> nodes) {
        this.rootNode = rootNode;
        this.nodes = nodes;
    }

    private SuffixTreeNode parseTree(ParseTree pt, UUID parent) {
        while (pt.getChildCount() == 1) {
            pt = pt.getChild(0);
        }
        // create node
        SuffixTreeNode stn = new SuffixTreeNode(pt, parent);
        // create array of node names for hash
        ArrayList<String> childNodeNames = new ArrayList<>();
        int weight = 0;
        for (int i = 0; i < pt.getChildCount(); i++) {
            SuffixTreeNode child = parseTree(pt.getChild(i), stn.getId());
            childNodeNames.addAll(child.getNodeNames());
            stn.addChild(child.getId());
            // add onto weight
            if (child.getValue() != null) weight++;
            else weight += child.getWeight();
        }
        // set weight
        stn.setWeight(weight);

        // generate node names
        stn.generateHash(childNodeNames);

        // add node
        nodes.put(stn.getId(), stn);
        return stn;
    }

    public ArrayList<Integer> getHashList(){
        ArrayList<Integer> list = new ArrayList<>();

        Iterator it = nodes.entrySet().iterator();
        while (it.hasNext()) {
            int hash = ((SuffixTreeNode)it.next()).getHash();
            if (!list.contains(hash))
                list.add(hash);
        }

        return list;
    }

    public ArrayList<Integer> getHashList(int weightMinimum){
        ArrayList<Integer> list = new ArrayList<>();

        Iterator it = nodes.entrySet().iterator();
        while (it.hasNext()) {
            SuffixTreeNode tstn = ((SuffixTreeNode)it.next());
            if (!list.contains(tstn.getHash()) && tstn.getWeight() >= weightMinimum)
                list.add(tstn.getHash());
        }

        return list;
    }

    public boolean compareWithOriginal(String original) {
        String spaceless = convertToSpacelessString(rootNode);
        // make sure that all spaces are removed
        spaceless = spaceless.replaceAll("\\s+","");

        // remove comments if exist
        while (original.contains("/*")){
            int start = original.indexOf("/*");
            int end = original.indexOf("*/") + 2;
            original = original.substring(0, start) + original.substring(end);
        }
        while (original.contains("//")) {
            int start = original.indexOf("//");
            int end = original.indexOf("\n", start);
            original = original.substring(0, start) + original.substring(end);
        }
        // remove all spaces
        String origSpaceless = original.replaceAll("\\s+","");

        // check if there's anything left in both codes
        boolean neitherAreEmpty = !origSpaceless.isEmpty() && !spaceless.isEmpty();

        // return comparison
        return origSpaceless.equals(spaceless) && neitherAreEmpty;
    }

    private String convertToSpacelessString(SuffixTreeNode stn) {
        String s = "";

        if (stn.getValue() != null && !stn.getValue().equals("<EOF>")){
            s += stn.getValue();
        }
        else if (stn.getChildren().size() > 0) {
            for (SuffixTreeNode child : getChildNodes(stn.getId()))
                s += convertToSpacelessString(child);
        }

        return s;
    }

    public void setRootNode(SuffixTreeNode rootNode) {
        this.rootNode = rootNode;
    }

    public SuffixTreeNode getRootNode() {
        return rootNode;

    }

    public SuffixTreeNode getNode(UUID id) {
        if (nodes.containsKey(id))
            return nodes.get(id);
        return null;
    }

    public ArrayList<SuffixTreeNode> getChildNodes(UUID id) {
        if (nodes.containsKey(id)){
            SuffixTreeNode mainNode = nodes.get(id);
            // if it is end node return null, as if it has no children
            if (mainNode.getValue() != null)
                return null;

            ArrayList<SuffixTreeNode> children = new ArrayList<>();
            for (UUID childId : mainNode.getChildren())
                children.add(getNode(childId));

            return children;
        }
        return null;
    }

    public Map<UUID, SuffixTreeNode> getNodes() {
        return nodes;
    }

    public void setNodes(Map<UUID, SuffixTreeNode> nodes) {
        this.nodes = nodes;
    }
}

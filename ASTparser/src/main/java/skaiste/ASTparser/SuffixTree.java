package skaiste.ASTparser;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.*;

public class SuffixTree {
    private UUID id;
    // have a pointer to root node and list of nodes
    private SuffixTreeNode rootNode;
    private Map<UUID, SuffixTreeNode> nodes;

    private int counter = 0;

    public SuffixTree (ParseTree tree) {
        id = UUID.randomUUID();
        nodes = new HashMap<>();
        rootNode = parseTree(tree, null);
    }

    public SuffixTree() {
        id = UUID.randomUUID();
        nodes = new HashMap<>();
    }

    public void addSuffixTreeNode(SuffixTreeNode stn) {
        stn.setPosition(counter++);
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
        // create node && set position
        SuffixTreeNode stn = new SuffixTreeNode(pt, parent);
        stn.setPosition(counter++);
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
            int hash = ((Map.Entry<UUID, SuffixTreeNode>)it.next()).getValue().getHash();
            if (!list.contains(hash))
                list.add(hash);
        }

        return list;
    }

    public ArrayList<Integer> getHashList(int weightMinimum){
        ArrayList<Integer> list = new ArrayList<>();

        Iterator it = nodes.entrySet().iterator();
        while (it.hasNext()) {
            SuffixTreeNode tstn = ((Map.Entry<UUID, SuffixTreeNode>)it.next()).getValue();
            if (!list.contains(tstn.getHash()) && tstn.getWeight() >= weightMinimum)
                list.add(tstn.getHash());
        }

        return list;
    }

    public HashMap<Integer, SuffixTreeNodeStub> getHashListWithStubs(int weightMinimum){
        HashMap<Integer, SuffixTreeNodeStub> list = new HashMap<>();

        Iterator it = nodes.entrySet().iterator();
        while (it.hasNext()) {
            SuffixTreeNode stn = ((Map.Entry<UUID, SuffixTreeNode>)it.next()).getValue();
            if (!list.containsKey(stn.getHash()) && stn.getWeight() >= weightMinimum)
                list.put(stn.getHash(), new SuffixTreeNodeStub(id, stn.getId()));
            else if (list.containsKey(stn.getHash()) && stn.getWeight() >= weightMinimum)
                list.get(stn.getHash()).addNodeId(stn.getId());
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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCodeRepresentation() {
        return getCodeRepresentationOfNode(getRootNode());
    }
    public String getCodeRepresentationOfNode(SuffixTreeNode stn) {
        String s = "";

        if (stn.getValue() != null) {
            return stn.getValue();
        }

        for (SuffixTreeNode node : getChildNodes(stn.getId()))
            s += getCodeRepresentationOfNode(node) + " ";

        return s;
    }
}

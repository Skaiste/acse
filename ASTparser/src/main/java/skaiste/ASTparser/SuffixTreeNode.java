package skaiste.ASTparser;

import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.UUID;

public class SuffixTreeNode {

    private UUID id;
    private UUID parent;
    private ArrayList<UUID> children;
    private String name;
    private String value;
    private int hash;

    public void setNodeNames(ArrayList<String> nodeNames) {
        this.nodeNames = nodeNames;
    }

    private int weight;
    private ArrayList<String> nodeNames;


    public SuffixTreeNode(ParseTree t, UUID parent) {
        id = UUID.randomUUID();
        this.parent = parent;
        children = new ArrayList<UUID>();
        // if it is the end node
        if (t.getChildCount() == 0) {
            value = t.getText();
            name = getClass(t.getParent());
        }
        else
            name = getClass(t);
    }

    public SuffixTreeNode(UUID id, UUID parent, ArrayList<UUID> children, String name, String value, int hash, int weight) {
        this.id = id;
        this.parent = parent;
        this.children = children;
        this.name = name;
        this.value = value;
        this.hash = hash;
        this.weight = weight;
    }

    public void addChild(UUID id) {
        children.add(id);
    }

    public void generateHash(ArrayList<String> childNodeNames) {
        // add the node name to child node names
        if (!childNodeNames.contains(this.name))
            childNodeNames.add(this.name);
        // sort node names
        childNodeNames.sort(String::compareToIgnoreCase);

        // join names into one string & create hash
        String list = String.join("", childNodeNames);
        hash = list.hashCode();

        nodeNames = childNodeNames;
    }

    public UUID getParent() {
        return parent;
    }

    public void setParent(UUID parent) {
        this.parent = parent;
    }

    public ArrayList<UUID> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<UUID> children) {
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getHash() {
        return hash;
    }

    public void setHash(int hash) {
        this.hash = hash;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public ArrayList<String> getNodeNames() {
        return nodeNames;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    private String getClass(Object o) {
        return (o.getClass().toString().split("\\$")[1]).split("Context")[0];
    }
}

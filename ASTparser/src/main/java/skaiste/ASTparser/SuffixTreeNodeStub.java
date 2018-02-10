package skaiste.ASTparser;

import java.util.ArrayList;
import java.util.UUID;

public class SuffixTreeNodeStub {
    private UUID treeId;
    private ArrayList<UUID> nodeIds;

    public SuffixTreeNodeStub(UUID treeId, UUID nodeId) {
        this.treeId = treeId;
        this.nodeIds = new ArrayList<>();
        nodeIds.add(nodeId);
    }

    public void addNodeId(UUID nodeId) {
        nodeIds.add(nodeId);
    }

    public SuffixTreeNodeStub(UUID treeId, ArrayList<UUID> nodeIds) {
        this.treeId = treeId;
        this.nodeIds = nodeIds;
    }

    public SuffixTreeNodeStub () {}

    public UUID getTreeId() {
        return treeId;
    }

    public void setTreeId(UUID treeId) {
        this.treeId = treeId;
    }

    public ArrayList<UUID> getNodeIds() {
        return nodeIds;
    }

    public void setNodeIds(ArrayList<UUID> nodeIds) {
        this.nodeIds = nodeIds;
    }

}

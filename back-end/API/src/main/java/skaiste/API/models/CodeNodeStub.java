package skaiste.API.models;

import java.util.ArrayList;

public class CodeNodeStub {
    private String codeId;
    private ArrayList<String> nodeIds;

    public CodeNodeStub(String codeId) {
        this.codeId = codeId;
        nodeIds = new ArrayList<>();
    }

    public void addNodeId(String nodeId) {
        nodeIds.add(nodeId);
    }

    public String getCodeId() {
        return codeId;
    }

    public void setCodeId(String codeId) {
        this.codeId = codeId;
    }

    public ArrayList<String> getNodeIds() {
        return nodeIds;
    }

    public void setNodeIds(ArrayList<String> nodeIds) {
        this.nodeIds = nodeIds;
    }
}

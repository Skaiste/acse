package skaiste.API.models;

import java.util.ArrayList;

public class NodeNameCombo {
    private String id;
    private ArrayList<CodeNodeStub> codelist;

    public NodeNameCombo(String hash) {
        this.id = hash;
        codelist = new ArrayList<>();
    }

    public void addCodeNode(CodeNodeStub cns) {
        codelist.add(cns);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<CodeNodeStub> getCodelist() {
        return codelist;
    }

    public void setCodelist(ArrayList<CodeNodeStub> codelist) {
        this.codelist = codelist;
    }
}

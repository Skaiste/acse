package skaiste.API.models;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import skaiste.ASTparser.SuffixTreeNodeStub;

import java.util.ArrayList;

public class NodeNameCombo {
    private int id;
    private ArrayList<SuffixTreeNodeStub> codelist;

    public NodeNameCombo() {}

    public NodeNameCombo(int hash) {
        this.id = hash;
        codelist = new ArrayList<>();
    }

    public NodeNameCombo(int id, ArrayList<SuffixTreeNodeStub> codelist) {
        this.id = id;
        this.codelist = codelist;
    }

    public void addCodeNode(SuffixTreeNodeStub cns) {
        codelist.add(cns);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<SuffixTreeNodeStub> getCodelist() {
        return codelist;
    }

    public void setCodelist(ArrayList<SuffixTreeNodeStub> codelist) {
        this.codelist = codelist;
    }
}

package skaiste.API.fetchers;

import skaiste.API.services.ComboService;
import skaiste.ASTparser.SuffixTreeNodeStub;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ComboFetcher {

    private int hash;
    private int cursor;
    private int N = 10;
    private boolean anyMoreLeft = true;

    private ArrayList<SuffixTreeNodeStub> combos;
    private ArrayList<UUID> nodeIds;

    private ComboService comboService;

    public ComboFetcher(ComboService comboService, int hash) {
        this.comboService = comboService;
        this.hash = hash;
        cursor = 0;
        combos = new ArrayList<>();
        fetch();
    }

    public ComboFetcher(ComboService comboService, Map.Entry<Integer, SuffixTreeNodeStub> stubEntry) {
        this.comboService = comboService;
        hash = stubEntry.getKey();
        cursor = 0;
        nodeIds = stubEntry.getValue().getNodeIds();
        combos = new ArrayList<>();
        fetch();
    }

    private void fetch() {
        List<SuffixTreeNodeStub> newCombos = comboService.getComboCodeList(hash, N, cursor);
        combos.addAll(newCombos);
        // check if there could be more combos
        if (newCombos.size() < N)
            anyMoreLeft = false;
    }

    public SuffixTreeNodeStub pop() {
        if (combos.size() == 0)
            fetch();
        if (combos.size() == 0 && !anyMoreLeft)
            return null;

        SuffixTreeNodeStub stns = combos.get(0);
        combos.remove(stns);
        return stns;
    }

    public SuffixTreeNodeStub glance() {
        if (combos.size() == 0)
            fetch();
        if (combos.size() == 0 && !anyMoreLeft)
            return null;
        return combos.get(0);
    }

    public SuffixTreeNodeStub glance(int index) {
        if (combos.size() <= index)
            fetch();
        if (combos.size() <= index && !anyMoreLeft)
            return null;
        return combos.get(index);
    }

    public boolean isThereAnyMoreLeft() {
        return anyMoreLeft;
    }

    public int getSize() { return combos.size(); }
}

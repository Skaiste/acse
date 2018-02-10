package skaiste.API.fetchers;

import skaiste.API.models.CodeModel;
import skaiste.API.services.CodeService;
import skaiste.ASTparser.SuffixTreeNodeStub;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CodeFetcher {

    private int amountToFetch = 20;

    private CodeService codeService;

    private ArrayList<ComboFetcher> comboFetchers;
    private ArrayList<CodeModel> codeModels;

    public CodeFetcher (CodeService codeService, ArrayList<ComboFetcher> comboFetchers) {
        this.codeService = codeService;
        this.comboFetchers = comboFetchers;
        codeModels = new ArrayList<>();
        fetch();
    }

    public void fetch() {
        // make sure that there are more code models left
        if (!isThereAnyMoreLeft()) return;

        // get a list of ids from combo fetchers
        ArrayList<UUID> codeIds = new ArrayList<>();
        int iteration = 0;
        boolean moreToGo = true;
        while (codeIds.size() < amountToFetch && isThereAnyMoreLeft() && moreToGo) {
            moreToGo = false;
            for (ComboFetcher cf : comboFetchers) {
                if (codeIds.size() >= amountToFetch) break;
                UUID treeId = cf.glance(iteration).getTreeId();
                // check if code is already fetched
                if (isCodeAlreadyFetched(treeId)) continue;
                // check if it is already in the list
                if (codeIds.contains(treeId)) continue;
                codeIds.add(treeId);
                moreToGo = true;
            }
            iteration++;
        }
        // request code service for a list of code with these ids
        List<CodeModel> newCodeModels = codeService.getCodeModelsWithId(codeIds);

        // add new Code models
        codeModels.addAll(newCodeModels);
    }

    public CodeModel getCode(UUID id) {
        CodeModel cm = codeModels.stream().filter(x -> x.getCode().getId() == id).findFirst().get();
        if (cm == null) {
            fetch(); // should be guaranteed that will get from the first fetch
            cm = codeModels.stream().filter(x -> x.getCode().getId() == id).findFirst().get();
        }
        return cm;
    }

    public boolean isThereAnyMoreLeft() {
        int size = 0;
        for (ComboFetcher cf : comboFetchers) {
            size += cf.getSize();
        }
        return size > 0;
    }

    public boolean isCodeAlreadyFetched(UUID id) {
        return codeModels.stream().anyMatch(x -> x.getCode().getId() == id);
    }
}

package skaiste.API.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import skaiste.API.Matcher;
import skaiste.API.fetchers.CodeFetcher;
import skaiste.API.fetchers.ComboFetcher;
import skaiste.API.models.CodeModel;
import skaiste.API.models.MatchingBlock;
import skaiste.API.models.ResponseMessage;
import skaiste.API.services.CodeService;
import skaiste.API.services.ComboService;
import skaiste.ASTparser.SuffixTreeNodeStub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@RestController
public class StorageController {

    @Autowired
    CodeService codeService;
    @Autowired
    ComboService comboService;

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/storecode")
    @RequestMapping(method= RequestMethod.POST, value="/storecode")
    public ResponseMessage storeCode(@RequestParam(value="code") String code) {
        CodeModel model = new CodeModel(code);
        if (!model.isCodeValid()) {
            return new ResponseMessage(false, "The code is incorrect!");
        }
        // store code in the database
        codeService.saveModel(model);

        // get combo hashes and store them somewhere
        HashMap<Integer, SuffixTreeNodeStub> hashes = model.getCode().getHashListWithStubs(2);
        comboService.insertCombos(hashes);

        // send a successful message
        return new ResponseMessage(true, "The code was successfully parsed and stored!");
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/searchcode")
    @RequestMapping(method= RequestMethod.POST, value="/searchcode")
    public ResponseMessage searchCode(@RequestParam(value="querycode") String querycode) {
        CodeModel querymodel = new CodeModel(querycode);
        if (!querymodel.isCodeValid() || querymodel.getCode().getNodes().size() == 0) {
            return new ResponseMessage(false, "The code is incorrect!");
        }

        // create combo fetchers
        Map<Integer, SuffixTreeNodeStub> hashlist = querymodel.getCode().getHashListWithStubs(2);
        ArrayList<ComboFetcher> comboFetchers = new ArrayList<>();
        Iterator it = hashlist.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, SuffixTreeNodeStub> entry = (Map.Entry<Integer, SuffixTreeNodeStub>)it.next();
            comboFetchers.add(new ComboFetcher(comboService, entry));
        }

        // create code fetcher
        CodeFetcher codeFetcher = new CodeFetcher(codeService, comboFetchers);

        // create matcher & match
        Matcher matcher = new Matcher(comboFetchers, codeFetcher,querymodel);
        ArrayList<MatchingBlock> matchingBlocks = matcher.newMatching();

        // send a successful message
        return new ResponseMessage(true, "The code was successfully found!", matchingBlocks);
    }
}

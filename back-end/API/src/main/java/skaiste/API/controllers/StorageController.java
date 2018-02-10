package skaiste.API.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import skaiste.API.fetchers.CodeFetcher;
import skaiste.API.fetchers.ComboFetcher;
import skaiste.API.models.CodeModel;
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
        //System.out.println(querymodel.getCode());

        Map<Integer, SuffixTreeNodeStub> hashlist = querymodel.getCode().getHashListWithStubs(2);
        ArrayList<ComboFetcher> comboFetchers = new ArrayList<>();
        Iterator it = hashlist.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, SuffixTreeNodeStub> entry = (Map.Entry<Integer, SuffixTreeNodeStub>)it.next();
            comboFetchers.add(new ComboFetcher(comboService, entry));
        }

        CodeFetcher codeFetcher = new CodeFetcher(codeService, comboFetchers);

//        if (!querymodel.isCodeValid() || querymodel.getCode().getNodes() == null) {
//            return new ResponseMessage(false, "The code is incorrect!");
//        }
//        // get data from repository that matches tags of the query
//        List<CodeModel> list = codeRepository.findAllByTagsContaining(querymodel.getCode().getTags());
//        //for (CodeModel c : list)
//        //    System.out.println(c.getCode());
//
//        // match the data and get the result
//        Matcher matcher = new Matcher(list, querymodel);
//        List<MatchingResult> matchedData = matcher.matchSyntax();
//
//        // if no matches are found
//        if (matchedData.size() == 0)
//            return new ResponseMessage(false, "No matches were found!");
//
//        // send a successful message
//        return new ResponseMessage(true, "The code was successfully found!", matchedData);
        return new ResponseMessage(true, "YESY!");
    }
}

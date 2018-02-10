package skaiste.API.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import skaiste.API.models.NodeNameCombo;
import skaiste.API.repositories.ComboRepository;
import skaiste.ASTparser.SuffixTreeNodeStub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class ComboService {

    @Autowired
    ComboRepository comboRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    public void insertCombos(HashMap<Integer, SuffixTreeNodeStub> list){
        // gather all keys, try to get any already existing keys out
        ArrayList<Integer> hashlist = new ArrayList<Integer>();
        hashlist.addAll(list.keySet());
        List<NodeNameCombo> existing = comboRepository.findAllByIdIn(hashlist);

        // edit existing combos & save them
        for (NodeNameCombo combo : existing){
            combo.addCodeNode(list.get(combo.getId()));
            comboRepository.save(combo);
            hashlist.remove(new Integer(combo.getId()));
        }
        // create new combos & save them
        for (int h : hashlist){
            NodeNameCombo combo = new NodeNameCombo(h);
            combo.addCodeNode(list.get(h));
            comboRepository.save(combo); // TODO figure out a way to add multiple at once
        }
    }

    public List<SuffixTreeNodeStub> getComboCodeList(int hash, int limit, long skip) {

        MatchOperation matchStage = Aggregation.match(new Criteria("_id").is(hash));
        UnwindOperation unwindStage = Aggregation.unwind("codelist");
        ReplaceRootOperation replaceRootStage = Aggregation.replaceRoot("codelist");
        SkipOperation skipStage = Aggregation.skip(skip);
        LimitOperation limitStage = Aggregation.limit(limit);

        Aggregation aggregation = Aggregation.newAggregation(matchStage, unwindStage, replaceRootStage, skipStage,limitStage);
        AggregationResults<SuffixTreeNodeStub> output = mongoTemplate.aggregate(
                aggregation, "nodeNameCombo", SuffixTreeNodeStub.class);

        List<SuffixTreeNodeStub> list = output.getMappedResults();

        return list;
    }
}

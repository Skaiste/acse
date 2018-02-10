package skaiste.API.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import skaiste.API.models.NodeNameCombo;
import skaiste.ASTparser.SuffixTreeNodeStub;

import java.util.List;

public interface ComboRepository extends MongoRepository<NodeNameCombo, String> {
    List<NodeNameCombo> findAllByIdIn(List<Integer> ids);
}

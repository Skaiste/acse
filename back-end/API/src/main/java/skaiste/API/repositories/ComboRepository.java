package skaiste.API.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import skaiste.API.models.NodeNameCombo;

public interface ComboRepository extends MongoRepository<NodeNameCombo, String> {
}

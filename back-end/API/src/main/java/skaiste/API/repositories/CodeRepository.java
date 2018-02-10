
package skaiste.API.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import skaiste.API.models.CodeModel;

public interface CodeRepository extends MongoRepository<CodeModel, String>{
//	@Query("{'code': {$elemMatch: { 'tags': { $in: ?0 }}}}")
//	List<CodeModel> findByCodeTags(List<String> tags);
//
//	List<CodeModel> findAllByTagsContaining(List<String>tags);
}

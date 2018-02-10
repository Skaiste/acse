package skaiste.API.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import skaiste.API.models.CodeModel;
import skaiste.API.repositories.CodeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CodeService {

    @Autowired
    CodeRepository codeRepository;

    public void saveModel(CodeModel cm) {
        codeRepository.save(cm);
    }

    public List<CodeModel> getCodeModelsWithId(ArrayList<UUID> ids) {
        return codeRepository.findCodeModelsById(ids);
    }

}

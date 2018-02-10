package skaiste.API.mappers;

import skaiste.API.models.CodeModel;
import skaiste.ASTparser.*;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.core.convert.converter.Converter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class CodeModelWriteMapper implements Converter<CodeModel, DBObject> {
    @Override
    public DBObject convert(CodeModel codeModel) {
        DBObject dbObject = new BasicDBObject();
        dbObject.put("_id", codeModel.getCode().getId());
        //dbObject.put("language", codeModel.getLanguage());
        dbObject.put("timestamp", convertDate(codeModel.getTimestamp()));
        dbObject.put("code", convertCode(codeModel.getCode()));
        dbObject.put("originalcode", codeModel.getOriginalCode());
        //dbObject.put("tags", codeModel.getTags());
        return dbObject;
    }

    private DBObject convertCode(SuffixTree st) {
        DBObject document = new BasicDBObject();
        document.put("id", st.getId());
        document.put("rootNodeId", st.getRootNode().getId());

        BasicDBList nodes = new BasicDBList();
        Iterator it = st.getNodes().entrySet().iterator();
        while (it.hasNext()) {
            SuffixTreeNode stn = ((Map.Entry<UUID,SuffixTreeNode>)it.next()).getValue();
            nodes.add(convertSuffixTreeNode(stn));
        }
        document.put("nodes", nodes);

        return document;
    }

    private DBObject convertSuffixTreeNode(SuffixTreeNode stn) {
        DBObject document = new BasicDBObject();

        document.put("id", stn.getId());
        document.put("name", stn.getName());
        if (stn.getValue() != null)
            document.put("value", stn.getValue());
        else {
            BasicDBList children = new BasicDBList();
            for (UUID cid : stn.getChildren())
                children.add(cid);
            document.put("children", children);
        }
        document.put("parent", stn.getParent());
        document.put("hash", stn.getHash());
        document.put("weight", stn.getWeight());

        return document;
    }

    public Date convertDate(LocalDateTime ldt) {
        Instant instant = ldt.toInstant(ZoneOffset.UTC);
        Date date = Date.from(instant);
        return date;
    }
}

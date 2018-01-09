package skaiste.API;

import com.khubla.antlr4example.SyntaxTree;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.core.convert.converter.Converter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.UUID;

public class CodeModelWriteMapper implements Converter<CodeModel, DBObject> {
    @Override
    public DBObject convert(CodeModel codeModel) {
        DBObject dbObject = new BasicDBObject();
        dbObject.put("_id", UUID.randomUUID());
        dbObject.put("language", codeModel.getLanguage());
        dbObject.put("timestamp", convertDate(codeModel.getTimestamp()));
        dbObject.put("code", convertCode(codeModel.getCode()));
        dbObject.put("originalcode", codeModel.getOriginalCode());
        dbObject.put("tags", codeModel.getTags());
        return dbObject;
    }

    private DBObject convertCode(SyntaxTree st) {
        DBObject document = new BasicDBObject();
        document.put("nodeName", st.getNodeName());
        if (st.getValue() != null)
            document.put("value", st.getValue());
        if (st.getNodes() != null) {
            BasicDBList nodes = new BasicDBList();
            for (SyntaxTree n : st.getNodes())
                nodes.add(convertCode(n));
            document.put("nodes", nodes);
        }
        document.put("tags", st.getTags());
        document.put("hash", st.getChildNodeHash());
        return document;
    }

    public Date convertDate(LocalDateTime ldt) {
        Instant instant = ldt.toInstant(ZoneOffset.UTC);
        Date date = Date.from(instant);
        return date;
    }
}

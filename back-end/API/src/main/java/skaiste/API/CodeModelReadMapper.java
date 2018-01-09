package skaiste.API;

import com.khubla.antlr4example.SyntaxTree;
import com.mongodb.DBObject;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class CodeModelReadMapper implements Converter<DBObject, CodeModel> {

    @Override
    public CodeModel convert(DBObject dbObject) {
        String language = dbObject.containsField("language") ? (String)dbObject.get("language") : "";
        LocalDateTime timestamp = dbObject.containsField("timestamp") ? convertDate((Date)dbObject.get("timestamp")) : null;
        SyntaxTree code = dbObject.containsField("code") ? convertSyntaxTree((DBObject)dbObject.get("code")) : null;
        String origCode = dbObject.containsField("originalcode") ? (String)dbObject.get("originalcode") : "";

        return new CodeModel(language, timestamp, code, origCode);
    }

    public SyntaxTree convertSyntaxTree(DBObject dbObject) {
        ArrayList<SyntaxTree> treenodes = null;
        if (dbObject.containsField("nodes")) {
            List<DBObject> childnodes = (List<DBObject>) dbObject.get("nodes");
            treenodes = new ArrayList<>();
            for (DBObject o : childnodes) {
                treenodes.add(convertSyntaxTree(o));
            }
        }
        ArrayList<String> tags = null;
        if (dbObject.containsField("tags")) {
            tags = (ArrayList<String>) dbObject.get("tags");
        }
        String nodeName = dbObject.containsField("nodeName") ? (String)dbObject.get("nodeName") : "";
        String value = dbObject.containsField("value") ? (String)dbObject.get("value") : "";
        int hash = dbObject.containsField("hash") ? (int)dbObject.get("hash") : 0;

        return new SyntaxTree(treenodes, tags, nodeName, value, hash);
    }

    private LocalDateTime convertDate(Date date) {
        Instant instant = Instant.ofEpochMilli(date.getTime());
        LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
        return ldt;
    }
}

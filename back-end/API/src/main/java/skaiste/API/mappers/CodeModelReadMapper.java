package skaiste.API.mappers;

import com.mongodb.DBObject;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import skaiste.API.models.CodeModel;
import skaiste.ASTparser.SuffixTree;
import skaiste.ASTparser.SuffixTreeNode;
import skaiste.ASTparser.SyntaxTree;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class CodeModelReadMapper implements Converter<DBObject, CodeModel> {

    @Override
    public CodeModel convert(DBObject dbObject) {
        //String language = dbObject.containsField("language") ? (String)dbObject.get("language") : "";
        LocalDateTime timestamp = dbObject.containsField("timestamp") ? convertDate((Date)dbObject.get("timestamp")) : null;
        SuffixTree code = dbObject.containsField("code") ? convertSuffixTree((DBObject)dbObject.get("code")) : null;
        String origCode = dbObject.containsField("originalcode") ? (String)dbObject.get("originalcode") : "";

        return new CodeModel(timestamp, code, origCode);
    }

    public SuffixTree convertSuffixTree(DBObject dbObject) {
        SuffixTree st = new SuffixTree();
        st.setId(dbObject.containsField("id") ? (UUID)dbObject.get("id") : UUID.fromString("0"));

        if (dbObject.containsField("nodes")) {
            List<DBObject> nodes = (List<DBObject>) dbObject.get("nodes");
            for (DBObject o : nodes) {
                st.addSuffixTreeNode(convertSuffixTreeNode(o));
            }
        }
        if (dbObject.containsField("rootNodeId")) {
            UUID rootNodeId = (UUID)dbObject.get("rootNodeId");
            st.setRootNode(st.getNode(rootNodeId));
        }

        return st;
    }

    public SuffixTreeNode convertSuffixTreeNode(DBObject dbObject) {
        UUID id = dbObject.containsField("id") ? (UUID)dbObject.get("id") : UUID.fromString("0");
        UUID parent = dbObject.containsField("parent") ? (UUID)dbObject.get("parent") : UUID.fromString("0");
        ArrayList<UUID> children = new ArrayList<>();
        if (dbObject.containsField("children")) {
            List<Object> childrenNodes = (List<Object>) dbObject.get("children");
            for (Object chld : childrenNodes) {
                children.add((UUID)chld);
            }
        }
        String name = dbObject.containsField("name") ? (String)dbObject.get("name") : "";
        String value = dbObject.containsField("value") ? (String)dbObject.get("value") : null;
        int hash = dbObject.containsField("hash") ? (int)dbObject.get("hash") : 0;
        int weight = dbObject.containsField("weight") ? (int)dbObject.get("weight") : 0;

        return new SuffixTreeNode(id, parent, children, name, value, hash, weight);
    }

    private LocalDateTime convertDate(Date date) {
        Instant instant = Instant.ofEpochMilli(date.getTime());
        LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
        return ldt;
    }
}

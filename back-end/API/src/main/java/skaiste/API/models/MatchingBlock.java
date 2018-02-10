package skaiste.API.models;

import skaiste.ASTparser.SuffixTree;
import skaiste.ASTparser.SuffixTreeNode;

import java.awt.*;
import java.util.ArrayList;
import java.util.UUID;

public class MatchingBlock {
    private String queryCode, dataCode;
    private ArrayList<Point> queryMatchingBlock, dataMatchingBlock;

    public MatchingBlock(String queryCode, String dataCode, ArrayList<BucketEntry> bucketEntries, SuffixTree qt, SuffixTree dt) {
        this.queryCode = removeComments(queryCode);
        this.dataCode = removeComments(dataCode);

        queryMatchingBlock = new ArrayList<>();
        dataMatchingBlock = new ArrayList<>();

        for (BucketEntry entry : bucketEntries) {
            Point queryPoint = new Point(-1, -1);
            queryPoint.y = bucketToBlocks(entry.getQueryNode(), qt, 0, this.queryCode, queryPoint);
            queryMatchingBlock.add(queryPoint);

            Point dataPoint = new Point(-1, -1);
            dataPoint.y = bucketToBlocks(entry.getDataNode(), dt, 0, this.dataCode, dataPoint);
            dataMatchingBlock.add(dataPoint);
        }

        // TODO sort blocks by positions
    }

    private int bucketToBlocks(UUID nodeId, SuffixTree qt, int cursor, String code, Point p) {

        SuffixTreeNode node = qt.getNode(nodeId);

        if (cursor >= code.length()) return cursor;

        // TODO fix this
        // if end node
        if (node.getValue() != null) {
            // skip spaces first
            while (Character.isWhitespace(code.charAt(cursor)))
                cursor++;

            // check if the start was marked as a wrong start
            if (p.x != -1 && !code.startsWith(node.getValue(), cursor)) p.x = -1;

            // move until start is found, if necessary
            while (!code.startsWith(node.getValue(), cursor))
                cursor++;

            // initialise the start of block
            if (code.startsWith(node.getValue(), cursor)) {
                p.x = cursor;
                cursor += node.getValue().length();
            }
        } // if has children
        else {
            for (UUID id : node.getChildren()){
                cursor = bucketToBlocks(id, qt, cursor, code, p);
            }
        }

        return cursor;
    }

    private String removeComments(String s) {
        // remove comments if exist
        while (s.contains("/*")){
            int start = s.indexOf("/*");
            int end = s.indexOf("*/") + 2;
            s = s.substring(0, start) + s.substring(end);
        }
        while (s.contains("//")) {
            int start = s.indexOf("//");
            int end = s.indexOf("\n", start);
            s = s.substring(0, start) + s.substring(end);
        }
        return s;
    }
}

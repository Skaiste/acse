package skaiste.API.models;

import java.awt.*;
import java.util.ArrayList;

public class MatchingResult {
    private ArrayList<String> dataLines;
    private ArrayList<String> queryLines;
    private ArrayList<MatchingLinePart> dataMatchingLineParts, queryMatchingLineParts;

    private ArrayList<Point> matchingLines;

    private Point datacursor, querycursor;

    public MatchingResult(String datacode, String querycode) {
        dataLines = splitLines(datacode);
        queryLines = splitLines(querycode);
        dataMatchingLineParts = new ArrayList<>();
        queryMatchingLineParts = new ArrayList<>();
        datacursor = new Point();
        querycursor = new Point();
        matchingLines = new ArrayList<>();
    }

    public void moveDataCursor(String part, boolean match) {
        moveCursor(datacursor, part, dataLines, dataMatchingLineParts, match);
    }

    public void moveQueryCursor(String part, boolean match) {
        moveCursor(querycursor, part, queryLines, queryMatchingLineParts, match);
    }

    public void matchCurrentLines() {
        // get the latest line parts
        MatchingLinePart dlp = dataMatchingLineParts.get(dataMatchingLineParts.size()-1);
        MatchingLinePart qlp = queryMatchingLineParts.get(queryMatchingLineParts.size()-1);
        Point match = new Point(dlp.getPosition().x, qlp.getPosition().x);

        // find line with the same query
        Point previous = null;
        for (Point p : matchingLines)
            if (p.y == match.y)
                previous = p;
        if (previous != null && previous.x > match.x) {
            int index = matchingLines.indexOf(previous);
            matchingLines.set(index, match);
        }
        // check if it already exists
        else if (!matchingLines.contains(match) && previous == null)
            matchingLines.add(match);
    }

    private ArrayList<String> splitLines(String str) {
        ArrayList<String> array = new ArrayList<>();
        String[] tmp = str.split("\\r?\\n");
        for (String line : tmp)
            array.add(line);
        return array;
    }

    private void moveCursor(Point cursor, String part, ArrayList<String> lines, ArrayList<MatchingLinePart> lineparts, boolean match) {
        // get the line
        if (lines.size() <= cursor.x) return;
        String line = lines.get(cursor.x);

        // move the cursor to skip space characters
        skipWhitespaces(cursor, lines);
        line = lines.get(cursor.x);
        // move the cursor to skip comments
        while (line.startsWith("/*",cursor.y) || line.startsWith("//", cursor.y)){
            if (line.startsWith("/*",cursor.y)) {
                int endpos = line.indexOf("*/", cursor.y) + 2;
                updateCursorTo(endpos, cursor, lines);
            }
            if (line.startsWith("//", cursor.y)) {
                updateCursorToNextLine(cursor, lines);
            }
            line = lines.get(cursor.x);
        }

        // check if the part from the cursor matches the part
        boolean partMatches = line.startsWith(part, cursor.y);
        // if the part doesn't match, exit
        if (!partMatches) return;
        // create a line part & add it to the list
        MatchingLinePart linePart = new MatchingLinePart(part, (Point)cursor.clone(), match);
        lineparts.add(linePart);

        // move the cursor by part
        updateCursorTo(cursor.y + part.length(), cursor, lines);
    }

    private void updateCursorTo(int y, Point cursor, ArrayList<String> lines) {
        // check if it should go to next line
        if (lines.get(cursor.x).length() <= y){
            cursor.x++;
            cursor.y = 0;
        }
        else {
            cursor.y = y;
        }
        skipWhitespaces(cursor, lines);
    }

    private void updateCursorToNextLine(Point cursor, ArrayList<String> lines) {
        cursor.x++;
        cursor.y = 0;
        skipWhitespaces(cursor, lines);
    }

    private void skipWhitespaces(Point cursor, ArrayList<String> lines) {
        while((cursor.x < lines.size() && cursor.y >= lines.get(cursor.x).length()) ||
                (cursor.x < lines.size() && cursor.y < lines.get(cursor.x).length() &&
                        Character.isWhitespace(lines.get(cursor.x).charAt(cursor.y)))) {
            if (cursor.y < lines.get(cursor.x).length() && Character.isWhitespace(lines.get(cursor.x).charAt(cursor.y))) {
                cursor.y++;
            }
            if (cursor.y >= lines.get(cursor.x).length()) {
                cursor.x++;
                cursor.y = 0;
            }
        }
    }

    public ArrayList<MatchingLinePart> getDataMatchingLineParts() {
        return dataMatchingLineParts;
    }

    public ArrayList<MatchingLinePart> getQueryMatchingLineParts() {
        return queryMatchingLineParts;
    }

    public ArrayList<Point> getMatchingLines() {
        return matchingLines;
    }

    public ArrayList<String> getDataLines() {
        return dataLines;
    }

    public ArrayList<String> getQueryLines() {
        return queryLines;
    }

    public Point getDatacursor() {
        return datacursor;
    }

    public Point getQuerycursor() {
        return querycursor;
    }

    public int getNoOfMatchingParts() {
        int counter = 0;
        for (MatchingLinePart mlp : dataMatchingLineParts)
            if (mlp.isMatching())
                counter++;
        return counter;
    }

}

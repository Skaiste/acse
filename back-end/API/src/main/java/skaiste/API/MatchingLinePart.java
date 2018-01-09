package skaiste.API;

import java.awt.*;

public class MatchingLinePart {
    private String part;
    private boolean isMatching;
    private Point position;


    public MatchingLinePart(String part, Point position, boolean isMatching){
        this.part = part;
        this.position = position;
        this.isMatching = isMatching;
    }

    public Point getPosition() {
        return position;
    }

    public String getPart() {
        return part;
    }

    public boolean isMatching() {
        return isMatching;
    }
}

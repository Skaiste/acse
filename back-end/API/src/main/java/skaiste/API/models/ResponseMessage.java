package skaiste.API.models;

import java.util.List;

public class ResponseMessage {
    private String text;
    private String responseCode;
    private MatchingBlock[] matches;

    public ResponseMessage(boolean successful, String message) {
        text = message;
        responseCode = successful ? "200 OK" : "400 Error";
        matches = null;
    }

    public ResponseMessage(boolean successful, String message, List<MatchingBlock> matches) {
        text = message;
        responseCode = successful ? "200 OK" : "400 Error";
        this.matches = matches.toArray(new MatchingBlock[matches.size()]);
    }

    public String getText() {
        return text;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public MatchingBlock[] getMatches() {
        return matches;
    }
}

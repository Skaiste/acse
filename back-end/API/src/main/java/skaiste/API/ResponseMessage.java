package skaiste.API;

import java.util.List;

public class ResponseMessage {
    private String text;
    private String responseCode;
    private MatchingResult[] matches;

    public ResponseMessage(boolean successful, String message) {
        text = message;
        responseCode = successful ? "200 OK" : "400 Error";
        matches = null;
    }

    public ResponseMessage(boolean successful, String message, List<MatchingResult> matches) {
        text = message;
        responseCode = successful ? "200 OK" : "400 Error";
        this.matches = matches.toArray(new MatchingResult[matches.size()]);
    }

    public String getText() {
        return text;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public MatchingResult[] getMatches() {
        return matches;
    }
}

package dev.yourserver.simpleguide;

public class Suggestion {
    public final String title;
    public final String hint;
    public final String structureKey; // optional
    public Suggestion(String title, String hint, String structureKey) {
        this.title = title;
        this.hint = hint;
        this.structureKey = structureKey;
    }
}

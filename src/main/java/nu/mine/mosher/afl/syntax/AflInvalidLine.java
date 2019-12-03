package nu.mine.mosher.afl.syntax;



public class AflInvalidLine implements AflItem {
    public final String message;

    AflInvalidLine(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "INVALID";
    }
}

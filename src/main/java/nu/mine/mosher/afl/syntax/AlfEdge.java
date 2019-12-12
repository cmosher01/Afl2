package nu.mine.mosher.afl.syntax;



public class AlfEdge {
    private final String lab;
    private final Step dest;

    public AlfEdge(final String lab, final Step dest) {
        this.lab = lab;
        this.dest = dest;
    }

    public Step dest() {
        return this.dest;
    }

    public String label() {
        return this.lab;
    }
}

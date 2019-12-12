package nu.mine.mosher.afl.syntax;



import java.util.*;



public class Step {
    private final AflSymbol sym;
    private final String lab;
    private final Set<AlfEdge> edges = new HashSet<>();

    public Step(final AflSymbol sym, final String lab) {
        this.sym = sym;
        this.lab = lab;
    }

    public void addEdge(final AlfEdge edge) {
        this.edges.add(edge);
    }

    public String label() {
        return this.lab;
    }

    public AflSymbol symbol() {
        return this.sym;
    }

    public Set<AlfEdge> edges() {
        return Collections.unmodifiableSet(this.edges);
    }
}

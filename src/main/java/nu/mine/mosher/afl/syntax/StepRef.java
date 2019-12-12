package nu.mine.mosher.afl.syntax;



import java.util.*;



@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class StepRef {
    private final AflSymbol sym;
    private final Optional<ID> id;
    private final String lab;
    private final String out;
    private final Optional<Optional<ID>> to;

    public StepRef(final AflSymbol sym, final Optional<ID> id, final String lab, final String out, final Optional<Optional<ID>> to) {
        this.sym = Objects.requireNonNull(sym);
        this.id = Objects.requireNonNull(id);
        this.lab = Objects.requireNonNull(lab);
        this.out = Objects.requireNonNull(out);
        this.to = Objects.requireNonNull(to);
    }

    public Step stepOf() {
        return new Step(this.sym, this.lab);
    }

    public Optional<ID> id() {
        return this.id;
    }

    public Optional<Optional<ID>> to() {
        return this.to;
    }

    public String out() {
        return this.out;
    }
}

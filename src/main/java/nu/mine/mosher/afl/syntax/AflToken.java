package nu.mine.mosher.afl.syntax;



import java.util.Objects;
import java.util.Optional;



@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class AflToken implements AflItem {
    private final AflSymbol symbol;
    private final Optional<AflId> id;
    private final Optional<String> output;

    AflToken(final AflSymbol symbol, final Optional<AflId> id, final Optional<String> output) {
        this.symbol = Objects.requireNonNull(symbol);
        this.id = id;
        this.output = output;
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();

        sb.append(this.symbol.toString());
        sb.append(",");
        if (this.id.isPresent()) {
            sb.append('\"');
            sb.append(this.id.get().toString().replace("\"","\"\""));
            sb.append('\"');
        }
        if (this.output.isPresent()) {
            sb.append(",");
            sb.append('\"');
            sb.append(this.output.get().replace("\"","\"\""));
            sb.append('\"');
        }

        return sb.toString();
    }
}

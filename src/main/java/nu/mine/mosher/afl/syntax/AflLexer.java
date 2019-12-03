package nu.mine.mosher.afl.syntax;



import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;



@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class AflLexer {
    private static final String IGNORE = "#";

    public static Stream<AflItem> lex(final BufferedReader in) {
        try {
            return in.lines().map(String::trim).map(AflLexer::strip).map(AflLexer::tokenize);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    private static String strip(final String line) {
        return line.startsWith(IGNORE) ? "" : line;
    }

    private static AflItem tokenize(final String line) {
        if (line.isEmpty()) {
            return new AflDelimiter();
        }

        final char c = line.charAt(0);
        final Optional<AflSymbol> sym = AflSymbol.startingWith(c);
        if (!sym.isPresent()) {
            return new AflInvalidLine(line);
        }

        return tokenize(sym.get(), line.substring(1));
    }

    private static AflItem tokenize(final AflSymbol sym, final String rest) {
        final StringBuilder id = new StringBuilder();
        final StringBuilder out = new StringBuilder();

        boolean inId = true;
        for (final char c : rest.toCharArray()) {
            if (inId) {
                if (c == sym.end()) {
                    inId = false;
                } else {
                    id.append(c);
                }
            } else {
                out.append(c);
            }
        }
        if (inId) {
            return new AflInvalidLine(rest);
        }

        final String sId = id.toString().trim();
        final Optional<AflId> aflId = sId.isEmpty() ? Optional.empty() : Optional.of(new AflId(sId));
        final String sOut = out.toString().trim();
        final Optional<String> aflOut = sOut.isEmpty() ? Optional.empty() : Optional.of(sOut);

        return new AflToken(sym, aflId, aflOut);
    }
}

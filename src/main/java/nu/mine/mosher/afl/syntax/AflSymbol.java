package nu.mine.mosher.afl.syntax;



import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;



@SuppressWarnings("unused")
public enum AflSymbol {
    TERMINAL('(', ')'),
    PROCESS('[', ']'),
    IO('/'),
    DECISION('<', '>'),
    PREDEFINED('|'),
    DOCUMENT('~'),
    STORAGE('{', '}'),
    COMMENT('C'),
    MANUAL_INPUT('$'),
    MANUAL_OPERATION('`'),
    DISPLAY('@');



    private static final Map<Character, AflSymbol> map = Arrays.stream(values()).collect(Collectors.toMap(AflSymbol::start, Function.identity()));

    private final char start;
    private final char end;

    AflSymbol(final char c) {
        this(c, c);
    }

    AflSymbol(final char start, final  char end) {
        this.start = start;
        this.end = end;
    }

    public char start() {
        return this.start;
    }

    public char end() {
        return this.end;
    }

    public static Optional<AflSymbol> startingWith(final char start) {
        return Optional.ofNullable(map.get(start));
    }
}

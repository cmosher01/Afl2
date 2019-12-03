package nu.mine.mosher.afl;



import nu.mine.mosher.afl.syntax.AflItem;
import nu.mine.mosher.afl.syntax.AflLexer;
import nu.mine.mosher.gnopt.Gnopt;
import nu.mine.mosher.io.LogFiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;



public final class Afl {
    private static final Logger log;

    static {
        System.setProperty("org.slf4j.simpleLogger.showDateTime", "true");
        System.setProperty("org.slf4j.simpleLogger.dateTimeFormat", "yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        System.setProperty("org.slf4j.simpleLogger.logFile", LogFiles.getLogFileOf(Afl.class).getPath());
        System.err.println(System.getProperty("org.slf4j.simpleLogger.logFile"));
        log = LoggerFactory.getLogger(Afl.class);
    }

    public static void main(final String... args) throws Gnopt.InvalidOption, IOException {
        log.info("begin main application");
        final AflOptions opt = Gnopt.process(AflOptions.class, args);

        Stream<AflItem> input = Stream.empty();
        for (final URL url : opt.input) {
            input = Stream.concat(input, AflLexer.lex(reader(url)));
        }

        if (opt.intermediate) {
            input.forEach(System.out::println);
        }

        System.out.flush();
    }

    private static BufferedReader reader(URL url) throws IOException {
        return new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
    }
}

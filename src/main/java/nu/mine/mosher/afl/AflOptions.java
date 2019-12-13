package nu.mine.mosher.afl;


import java.io.*;
import java.nio.file.*;
import java.util.*;



@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class AflOptions {
    public boolean help;
    public Path input;
    public PrintStream output = System.out;

    public void __(Optional<String> s) {
        if (Objects.nonNull(this.input)) {
            throw new IllegalArgumentException("cannot specify more than one input file");
        }
        this.input = Paths.get(s.get());
    }

    public void output(Optional<String> s) throws FileNotFoundException {
        if (!s.isPresent()) {
            throw new IllegalArgumentException("missing output file");
        }
        this.output = new PrintStream(Paths.get(s.get()).toFile());
    }

    public void help(Optional<String> s) {
        this.help = true;
        System.out.println("Usage: afl2 [OPTION]... AFL-FILE");
        System.out.println("Converts an AFL file to a Graphviz dot digraph.");
        System.out.println("Options:");
        System.out.println("  --output=FILE  dot file (default is standard output)");
        System.out.println("  --help  prints this help message");
    }
}

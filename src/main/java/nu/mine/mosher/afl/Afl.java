package nu.mine.mosher.afl;



import nu.mine.mosher.afl.syntax.*;
import nu.mine.mosher.gnopt.Gnopt;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;



public class Afl {
    public static void main(final String... args) throws IOException, nu.mine.mosher.afl.syntax.Afl.InvalidReference, Gnopt.InvalidOption {
        final AflOptions opt = Gnopt.process(AflOptions.class, args);
        if (opt.help) {
            return;
        }
        if (Objects.isNull(opt.input)) {
            throw new IllegalArgumentException("missing input file");
        }



        final BufferedReader afl = new BufferedReader(new InputStreamReader(new FileInputStream(opt.input.toFile()), StandardCharsets.UTF_8));
        final List<Step> flowchart = nu.mine.mosher.afl.syntax.Afl.parse(afl);
        afl.close();



        final PrintStream dot = opt.output;
        printDot(flowchart, dot);
        if (dot.checkError()) {
            throw new IOException("error writing output file");
        }
        dot.close();
    }



    private static void printDot(final List<Step> flowchart, PrintStream dot) {
        final Map<Step, UUID> stepToId = toStepMap(flowchart);

        dot.println("digraph {");

        stepToId.keySet().forEach(step -> {
            final String node = stepToId.get(step).toString();

            dot.printf("%s [label=%s, shape=%s, style=filled, fillcolor=cornsilk, fontname=Helvetica];\n",
                quoted(node),
                quoted(step.label()),
                shapeOf(step.symbol()));

            step.edges().forEach(e -> dot.printf("%s -> %s [taillabel=%s, arrowhead=onormal, fontname=Helvetica];\n",
                quoted(node),
                quoted(stepToId.get(e.dest()).toString()),
                quoted(e.label())));
        });

        dot.println("}");
    }
    private static Map<Step, UUID> toStepMap(final List<Step> flowchart) {
        final Map<Step, UUID> m = new HashMap<>();
        flowchart.forEach(step -> m.putIfAbsent(step, UUID.randomUUID()));
        return Collections.unmodifiableMap(m);
    }


    private static String shapeOf(AflSymbol sym) {
        switch (sym) {
            case PROCESS: return "box";
            case PREDEFINED: return "box3d";
            case TERMINAL: return "ellipse";
            case IO: return "parallelogram";
            case DECISION: return "diamond";
            case DOCUMENT: return "note";
            case STORAGE: return "cylinder";
            case COMMENT: return "plain";
            case MANUAL_INPUT: return "house";
            case MANUAL_OPERATION: return "invtrapezium";
            case DISPLAY: return "octagon";
        }
        return "box";
    }



    private static String quoted(final String s) {
        return "\""+s.replace("\\","\\\\").replace("\"", "\\\"")+"\"";
    }
}

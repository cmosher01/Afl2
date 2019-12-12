package nu.mine.mosher.afl;



import nu.mine.mosher.afl.syntax.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;



public class Afl {
    public static void main(String[] args) throws IOException, nu.mine.mosher.afl.syntax.Afl.InvalidReference {
        final BufferedReader afl = new BufferedReader(new InputStreamReader(new FileInputStream(Paths.get(args[0]).toFile()), StandardCharsets.UTF_8));

        System.out.println("digraph {");
        final Map<Step, UUID> stepToId = new HashMap<>();
        nu.mine.mosher.afl.syntax.Afl.parse(afl).forEach(s -> stepToId.putIfAbsent(s, UUID.randomUUID()));

        stepToId.keySet().forEach(s -> {
            System.out.print(quoted(stepToId.get(s).toString()));

            System.out.print(" [");

            System.out.print("label=");
            System.out.print(quoted(s.label()));

            System.out.print(" ,");
            System.out.print("shape="+shapeOf(s.symbol()));

            System.out.print(" ,");
            System.out.print("style=filled");
            System.out.print(" ,");
            System.out.print("fillcolor=cornsilk");

            System.out.print(" ,");
            System.out.print("fontname=Helvetica");


            System.out.print("]");

            System.out.println(";");

            s.edges().forEach(e -> {
                System.out.print(quoted(stepToId.get(s).toString()));
                System.out.print(" -> ");
                System.out.print(quoted(stepToId.get(e.dest()).toString()));

                System.out.print(" [");
                System.out.print("taillabel=");
                System.out.print(quoted(e.label()));

                System.out.print(" ,");
                System.out.print("fontname=Helvetica");

                System.out.print(" ,");
                System.out.print("arrowhead=onormal");

                System.out.print("]");

                System.out.println(";");
            });
        });
        System.out.println("}");
        System.out.flush();;
    }

    private static String shapeOf(AflSymbol sym) {
        switch (sym) {
            case PROCESS: return "box";
            case PREDEFINED: return "box3d";
            case TERMINAL: return "circle";
            case IO: return "parallelogram";
            case DECISION: return "diamond";
            case DOCUMENT: return "note";
            case STORAGE: return "cylinder";
            case COMMENT: return "plain";
            case MANUAL_INPUT: return "house";
            case MANUAL_OPERATION: return "invtrapezium";
            case DISPLAY: return "cds";
        }
        return "box";
    }



    private static String quoted(final String s) {
        return "\""+s.replace("\"", "\\\"")+"\"";
    }
}

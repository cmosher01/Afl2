package nu.mine.mosher.afl.syntax;



import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.*;
import java.util.*;



public class Afl {
    public static List<Step> parse(final BufferedReader afl) throws IOException, InvalidReference {
        final CharStream streamIn = CharStreams.fromReader(afl);
        final AflLexer lexer = new AflLexer(streamIn);
        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        final AflParser parser = new AflParser(tokens);
        final ParseTree tree = parser.flowchart();
        final List<StepRef> refs = new FlowChartVisitor().visit(tree);
        return Collections.unmodifiableList(link(refs));
    }

    public static class InvalidReference extends Throwable {
        private InvalidReference(final ID id) {
            super("invalid reference: "+id);
        }
    }

    private static List<Step> link(final List<StepRef> refs) throws InvalidReference {
        final List<Step> steps = new ArrayList<>();

        final Map<ID, Step> defs = new HashMap<>();

        // first create all steps with IDs, to allow for forward and backward references
        refs.stream().filter(r -> r.id().isPresent()).forEach(r -> defs.computeIfAbsent(r.id().get(), k -> r.stepOf()));

        Step next = null;

        final ListIterator<StepRef> i = refs.listIterator(refs.size());
        while (i.hasPrevious()) {
            final StepRef ref = i.previous();

            final Step step;
            if (ref.id().isPresent()) {
                step = defs.get(ref.id().get());
                // TODO warn if labels both exist but don't match
            } else {
                step = ref.stepOf();
            }
            steps.add(step);

            if (ref.to().isPresent()) {
                final Optional<ID> dest = ref.to().get();
                if (dest.isPresent()) {
                    if (!defs.containsKey(dest.get())) {
                        throw new InvalidReference(dest.get());
                    }
                    step.addEdge(new AlfEdge(ref.out(), defs.get(dest.get())));
                }
            } else if (Objects.nonNull(next)) {
                step.addEdge(new AlfEdge(ref.out(), next));
            }

            next = step;
        }

        Collections.reverse(steps);

        return steps;
    }
}

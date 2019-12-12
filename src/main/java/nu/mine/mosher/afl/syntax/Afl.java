package nu.mine.mosher.afl.syntax;



import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;



public class Afl {
    public static List<Step> parse(final BufferedReader afl) throws IOException, InvalidReference {
        final CharStream streamIn = CharStreams.fromReader(afl);
        final AflLexer lexer = new AflLexer(streamIn);
        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        final AflParser parser = new AflParser(tokens);
        final ParseTree tree = parser.flowchart();
        final List<StepRef> refs = new FlowChartV().visit(tree);
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
        for (int i = refs.size()-1; 0 <= i ; --i) {
            final StepRef ref = refs.get(i);

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



    private static class FlowChartV extends AflParserBaseVisitor<List<StepRef>> {
        @Override
        public List<StepRef> visitFlowchart(final AflParser.FlowchartContext ctx) {
            return ctx.step().stream().map(s -> s.accept(new StepV())).collect(Collectors.toList());
        }
    }



    private static class StepV extends AflParserBaseVisitor<StepRef> {
        @Override
        public StepRef visitStep(final AflParser.StepContext ctx) {
            final AflSymbol sym = ctx.symbol().accept(new SymbolV());
            Optional<Optional<ID>> alt_to;
            if (Objects.isNull(ctx.ato)) {
                alt_to = Optional.empty();
            } else {
                if (Objects.isNull(ctx.to)) {
                    // given > null
                    alt_to = Optional.of(Optional.empty());
                } else {
                    // given > id
                    alt_to = Optional.of(Optional.of(new ID(ctx.to.getText())));
                }
            }
            return new StepRef(sym, id(ctx.id), str(ctx.s), str(ctx.o), alt_to);
        }
    }



    private static class SymbolV extends AflParserBaseVisitor<AflSymbol> {
        @Override
        public AflSymbol visitTrm(final AflParser.TrmContext ctx) {
            return AflSymbol.TERMINAL;
        }

        @Override
        public AflSymbol visitPrc(final AflParser.PrcContext ctx) {
            return AflSymbol.PROCESS;
        }

        @Override
        public AflSymbol visitIo(final AflParser.IoContext ctx) {
            return AflSymbol.IO;
        }

        @Override
        public AflSymbol visitDec(final AflParser.DecContext ctx) {
            return AflSymbol.DECISION;
        }

        @Override
        public AflSymbol visitDef(final AflParser.DefContext ctx) {
            return AflSymbol.PREDEFINED;
        }

        @Override
        public AflSymbol visitDoc(final AflParser.DocContext ctx) {
            return AflSymbol.DOCUMENT;
        }

        @Override
        public AflSymbol visitSto(final AflParser.StoContext ctx) {
            return AflSymbol.STORAGE;
        }

        @Override
        public AflSymbol visitCom(final AflParser.ComContext ctx) {
            return AflSymbol.COMMENT;
        }

        @Override
        public AflSymbol visitMin(final AflParser.MinContext ctx) {
            return AflSymbol.MANUAL_INPUT;
        }

        @Override
        public AflSymbol visitMop(final AflParser.MopContext ctx) {
            return AflSymbol.MANUAL_OPERATION;
        }

        @Override
        public AflSymbol visitDsp(final AflParser.DspContext ctx) {
            return AflSymbol.DISPLAY;
        }
    }



    private static Optional<ID> id(final Token id) {
        return Objects.isNull(id) ? Optional.empty() : Optional.of(new ID(id.getText()));
    }

    private static String str(final List<AflParser.StrContext> rs) {
        return rs.stream().map(ParseTree::getText).collect(Collectors.joining(" "));
    }
}

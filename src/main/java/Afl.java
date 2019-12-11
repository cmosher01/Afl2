import com.google.common.graph.*;
import edu.uci.ics.jung.layout.algorithms.KKLayoutAlgorithm;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.renderers.*;
import nu.mine.mosher.afl.syntax.AflSymbol;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;



@SuppressWarnings("UnstableApiUsage")
public class Afl
{
    public static void main(String[] args) throws IOException, InvalidReference
    {
        final CharStream streamIn = CharStreams.fromFileName(args[0]);
        final AflLexer lexer = new AflLexer(streamIn);
        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        final AflParser parser = new AflParser(tokens);

        final ParseTree tree = parser.flowchart();
//        System.out.println();
//        System.out.println(tree.toStringTree(parser));

        final List<StepRef> refs = new FlowChartV().visit(tree);
//        System.out.println();
//        refs.forEach(System.out::println);

        final List<Step> steps = link(refs);


        System.out.println("digraph {");
        final Map<Step, UUID> stepToId = new HashMap<>();
        steps.forEach(s -> stepToId.putIfAbsent(s, UUID.randomUUID()));

        stepToId.keySet().forEach(s -> {
            System.out.print(quoted(stepToId.get(s).toString()));

            System.out.print(" [");

            System.out.print("label=");
            System.out.print(quoted(s.lab));

            System.out.print(" ,");
            System.out.print("shape="+shapeOf(s.sym));

            System.out.print(" ,");
            System.out.print("style=filled");
            System.out.print(" ,");
            System.out.print("fillcolor=cornsilk");

            System.out.print(" ,");
            System.out.print("fontname=Helvetica");


            System.out.print("]");

            System.out.println(";");

            s.edges.forEach(e -> {
                System.out.print(quoted(stepToId.get(s).toString()));
                System.out.print(" -> ");
                System.out.print(quoted(stepToId.get(e.dest).toString()));

                System.out.print(" [");
                System.out.print("taillabel=");
                System.out.print(quoted(e.lab));

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








        //        System.out.println();
//        steps.forEach(System.out::println);

//        final MutableNetwork<Step, UqEdge> g =
//            NetworkBuilder
//                .directed()
//                .allowsSelfLoops(true)
//                .allowsParallelEdges(true)
//                .nodeOrder(ElementOrder.unordered())
//                .edgeOrder(ElementOrder.unordered())
//                .build();
//
//        {
//            final Set<Step> sss = new HashSet<>();
//            steps.forEach(s -> {
//                if (!sss.contains(s)) {
//                    sss.add(s);
//                    g.addNode(s);
//                }
//            });
//        }
//        {
//            final Set<Step> sss = new HashSet<>();
//            steps.forEach(s -> {
//                if (!sss.contains(s)) {
//                    sss.add(s);
//                    s.edges.forEach(e -> g.addEdge(s, e.dest, new UqEdge(e.lab)));
//                }
//            });
//        }
//
//        show(g);
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

    private static class UqEdge {
        private final String s;
        UqEdge(String x) { s = x; }
        public String toString() { return s; }
    }

    private static void show(final Network<Step, UqEdge> graph) {
        final VisualizationImageServer<Step, UqEdge> vv = new VisualizationImageServer<>(graph, new KKLayoutAlgorithm<>(), new Dimension(800, 800));
        vv.getRenderer().setNodeRenderer(new GradientNodeRenderer<>(vv, Color.white, Color.red, Color.white, Color.blue, false));
        vv.getRenderer().getNodeLabelRenderer().setPositioner(new BasicNodeLabelRenderer.InsidePositioner());
        vv.getRenderer().getNodeLabelRenderer().setPosition(Renderer.NodeLabel.Position.AUTO);
        vv.getRenderContext().setEdgeDrawPaintFunction(e -> Color.lightGray);
        vv.getRenderContext().setArrowFillPaintFunction(e -> Color.lightGray);
        vv.getRenderContext().setArrowDrawPaintFunction(e -> Color.lightGray);
        vv.getRenderContext().setNodeLabelFunction(Object::toString);
        vv.getRenderContext().setEdgeLabelFunction(Objects::toString);
        final Image image = vv.getImage(new Point2D.Double(400, 400), new Dimension(800, 800));



        // create a frame to hold the graph
        final JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(new JLabel(new ImageIcon(image)));
        frame.pack();
        frame.setVisible(true);
    }

    private static class AlfEdge {
        private final String lab;
        private final Step dest;

        private AlfEdge(final String lab, final Step dest) {
            this.lab = lab;
            this.dest = dest;
        }
        @Override
        public String toString() {
            return lab+":"+dest.lab;
        }
    }

    public static class Step {
        private final AflSymbol sym;
        private final String lab;
        private final Set<AlfEdge> edges = new HashSet<>();

        public Step(final AflSymbol sym, final String lab) {
            this.sym = sym;
            this.lab = lab;
        }

        public void addEdge(final AlfEdge edge) {
            this.edges.add(edge);
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
//            sb.append(super.toString());
//            sb.append("|");
//            sb.append(sym);
//            sb.append("|");
//            sb.append(lab);
//            sb.append("|[");
//            sb.append(edges.stream().map(Object::toString).collect(Collectors.joining(",")));
//            sb.append("]");
            sb.append(sym.toString(), 0, 1);
            sb.append(' ');
            sb.append(lab);
            return sb.toString();
        }
    }

    private static List<Step> link(final List<StepRef> refs) throws InvalidReference {
        final List<Step> steps = new ArrayList<>();

        final Map<ID, Step> defs = new HashMap<>();

        // first create all steps with IDs, to allow for forward and backward references
        refs.stream().filter(r -> r.id.isPresent()).forEach(r -> defs.computeIfAbsent(r.id.get(), (k) -> r.stepOf()));

        Step next = null;
        for (int i = refs.size()-1; 0 <= i ; --i) {
            final StepRef ref = refs.get(i);

            final Step step;
            if (ref.id.isPresent()) {
                step = defs.get(ref.id.get());
                // TODO warn if labels both exist but don't match
            } else {
                step = ref.stepOf();
            }
            steps.add(step);

            if (ref.to.isPresent()) {
                final Optional<ID> dest = ref.to.get();
                if (dest.isPresent()) {
                    if (!defs.containsKey(dest.get())) {
                        throw new InvalidReference(dest.get());
                    }
                    step.addEdge(new AlfEdge(ref.out, defs.get(dest.get())));
                }
            } else if (Objects.nonNull(next)) {
                step.addEdge(new AlfEdge(ref.out, next));
            }

            next = step;
        }

        Collections.reverse(steps);

        return steps;
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static class StepRef {
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

        @Override
        public String toString() {
            return sym+"|"+(id.isPresent()?"<"+id.get()+">":"NULL")+"|"+lab+"|"+out+"|"+(to.isPresent()?"<"+to.get()+">":"NULL");
        }

        public Step stepOf() {
            return new Step(this.sym, this.lab);
        }
    }

    public static class ID {
        private final String id;

        public ID(final String id) {
            this.id = Objects.requireNonNull(id);
            if (this.id.isEmpty()) {
                throw new IllegalArgumentException("ID cannot be empty");
            }
        }

        @Override
        public boolean equals(final Object object) {
            if (!(object instanceof ID)) {
                return false;
            }
            final ID that = (ID)object;
            return this.id.equals(that.id);
        }

        @Override
        public int hashCode() {
            return this.id.hashCode();
        }

        @Override
        public String toString() {
            return this.id;
        }
    }

    public static class FlowChartV extends AflParserBaseVisitor<List<StepRef>> {
        @Override
        public List<StepRef> visitFlowchart(AflParser.FlowchartContext ctx) {
            return ctx.step().stream().map(s -> s.accept(new StepV())).collect(Collectors.toList());
        }
    }

    private static class StepV extends AflParserBaseVisitor<StepRef> {
        @Override
        public StepRef visitStep(AflParser.StepContext ctx) {
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
        public AflSymbol visitTrm(AflParser.TrmContext ctx) {
            return AflSymbol.TERMINAL;
        }

        @Override
        public AflSymbol visitPrc(AflParser.PrcContext ctx) {
            return AflSymbol.PROCESS;
        }

        @Override
        public AflSymbol visitIo(AflParser.IoContext ctx) {
            return AflSymbol.IO;
        }

        @Override
        public AflSymbol visitDec(AflParser.DecContext ctx) {
            return AflSymbol.DECISION;
        }

        @Override
        public AflSymbol visitDef(AflParser.DefContext ctx) {
            return AflSymbol.PREDEFINED;
        }

        @Override
        public AflSymbol visitDoc(AflParser.DocContext ctx) {
            return AflSymbol.DOCUMENT;
        }

        @Override
        public AflSymbol visitSto(AflParser.StoContext ctx) {
            return AflSymbol.STORAGE;
        }

        @Override
        public AflSymbol visitCom(AflParser.ComContext ctx) {
            return AflSymbol.COMMENT;
        }

        @Override
        public AflSymbol visitMin(AflParser.MinContext ctx) {
            return AflSymbol.MANUAL_INPUT;
        }

        @Override
        public AflSymbol visitMop(AflParser.MopContext ctx) {
            return AflSymbol.MANUAL_OPERATION;
        }

        @Override
        public AflSymbol visitDsp(AflParser.DspContext ctx) {
            return AflSymbol.DISPLAY;
        }
    }

    private static Optional<ID> id(final Token id) {
        return Objects.isNull(id) ? Optional.empty() : Optional.of(new ID(id.getText()));
    }

    private static String str(final List<AflParser.StrContext> rs) {
        return rs.stream().map(ParseTree::getText).collect(Collectors.joining(" "));
    }

    private static class InvalidReference extends Throwable {
        private InvalidReference(final ID id) {
            super("invalid reference: "+id);
        }
    }

    private static String quoted(final String s) {
        return "\""+s.replace("\"", "\\\"")+"\"";
    }
}

package nu.mine.mosher.afl.syntax;



import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.*;
import java.util.stream.Collectors;



class FlowChartVisitor extends AflParserBaseVisitor<List<StepRef>> {
    @Override
    public List<StepRef> visitFlowchart(final AflParser.FlowchartContext ctx) {
        return ctx.step().stream().map(s -> s.accept(new StepVisitor())).collect(Collectors.toList());
    }



    private static class StepVisitor extends AflParserBaseVisitor<StepRef> {
        @Override
        public StepRef visitStep(final AflParser.StepContext ctx) {
            final AflSymbol sym = ctx.symbol().accept(new SymbolVisitor());
            return new StepRef(sym, id(ctx.id), str(ctx.s), str(ctx.o), dest(ctx.ato, ctx.to));
        }
    }



    private static class SymbolVisitor extends AflParserBaseVisitor<AflSymbol> {
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



    private static Optional<Optional<ID>> dest(final Token ato, final Token to) {
        return Objects.isNull(ato) ? Optional.empty() : Optional.of(id(to));
    }

    private static Optional<ID> id(final Token id) {
        return Objects.isNull(id) ? Optional.empty() : Optional.of(new ID(id.getText()));
    }

    private static String str(final List<AflParser.StrContext> rs) {
        return rs.stream().map(ParseTree::getText).collect(Collectors.joining(" "));
    }
}

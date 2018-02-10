package skaiste.ASTparser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class ASTparser extends BaseErrorListener {

    private int mistakeCounter = 0;

    public SyntaxTree parseFromString(String code) throws Exception
    {
        CharStream input = CharStreams.fromString(code);

        CLexer lexer = new CLexer(input);

        CommonTokenStream tokens = new CommonTokenStream(lexer);

        CParser parser = new CParser(tokens);
        ParseTree tree = parser.compilationUnit();

        return new SyntaxTree(tree);
    }

    public SuffixTree parseSuffixFromString(String code) throws Exception
    {
        CharStream input = CharStreams.fromString(code);

        CLexer lexer = new CLexer(input);

        CommonTokenStream tokens = new CommonTokenStream(lexer);

        CParser parser = new CParser(tokens);
        ParseTree tree = parser.compilationUnit();

        return new SuffixTree(tree);
    }

    public SyntaxTree parseFromStringDeep(String code) throws Exception
    {
        CharStream input = CharStreams.fromString(code);

        CLexer lexer = new CLexer(input);

        CommonTokenStream tokens = new CommonTokenStream(lexer);

        CParser parser = new CParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(this);


        boolean noMistakes = false;
        SyntaxTree tree = new SyntaxTree(parser.compilationUnit());
        String[] rules = parser.getRuleNames();
        if (getMistakes() == 0 && tree.compareWithOriginal(input))
            noMistakes = true;
        for (int i = 0; i < rules.length && !noMistakes; i++) {
            //System.out.println(rules[i]);
            if (rules[i].equals("nestedParenthesesBlock")) continue;
            // reset parser
            parser.reset();
            // run next rule
            Method method = parser.getClass().getMethod(rules[i]);
            try {
                ParseTree parseTree = (ParseTree) method.invoke(parser);
                tree = new SyntaxTree(parseTree);
            } catch (Exception e) { mistakeCounter++; }

            // check for mistakes
            if (getMistakes() == 0 && tree.compareWithOriginal(input))
                noMistakes = true;
        }
        // check if the tree was parsed correctly
        if (!noMistakes)
            return null;

        return tree;
    }
    public SuffixTree parseSuffixFromStringDeep(String code) throws Exception
    {
        CharStream input = CharStreams.fromString(code);

        CLexer lexer = new CLexer(input);

        CommonTokenStream tokens = new CommonTokenStream(lexer);

        CParser parser = new CParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(this);

        boolean noMistakes = false;
        SuffixTree tree = new SuffixTree(parser.compilationUnit());
        String[] rules = parser.getRuleNames();
        if (getMistakes() == 0 && tree.compareWithOriginal(code))
            noMistakes = true;
        for (int i = 0; i < rules.length && !noMistakes; i++) {
            //System.out.println(rules[i]);
            if (rules[i].equals("nestedParenthesesBlock")) continue;
            // reset parser
            parser.reset();
            // run next rule
            Method method = parser.getClass().getMethod(rules[i]);
            try {
                ParseTree parseTree = (ParseTree) method.invoke(parser);
                tree = new SuffixTree(parseTree);
            } catch (Exception e) { mistakeCounter++; }

            // check for mistakes
            if (getMistakes() == 0 && tree.compareWithOriginal(code))
                noMistakes = true;
        }
        // check if the tree was parsed correctly
        if (!noMistakes)
            return null;

        return tree;
    }

    public static void printTreeToFile(String fileName, SyntaxTree t) throws IOException {
        FileWriter fileWriter = new FileWriter(fileName);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.print(t.toJSONstring());
        printWriter.close();
    }

    public static String[] getNodeNames() {
        CharStream input = CharStreams.fromString("");
        CLexer lexer = new CLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CParser parser = new CParser(tokens);
        return parser.getRuleNames();
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e)
            throws ParseCancellationException {
        mistakeCounter++;
        //System.out.println("line " + line + ":" + charPositionInLine + " " + msg);
        //throw new ParseCancellationException("line " + line + ":" + charPositionInLine + " " + msg);
    }

    private int getMistakes() {
        int m = mistakeCounter;
        mistakeCounter = 0;
        return m;
    }

    private void sleep(long timeout) {
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

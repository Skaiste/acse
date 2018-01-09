package skaiste.ASTparser;

import java.io.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.Interval;

public class Main
{
    public static void main( String[] args) throws Exception
    {
        if (args.length != 2) {
            System.out.println("Missing arguments: [input-filename (c)] [output-filename (json)]");
            return;
        }
        String inputName = args[0];
        String outputName = args[1];

        CharStream input = CharStreams.fromFileName(inputName);

        ASTparser parser = new ASTparser();
        SyntaxTree st = parser.parseFromStringDeep(input.getText(new Interval(0, input.size())));
        printTreeToFile(outputName, st);
    }

    public static void printTreeToFile(String fileName, SyntaxTree t) throws IOException {
        FileWriter fileWriter = new FileWriter(fileName);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.print(t.toJSONstring());
        printWriter.close();
    }
}
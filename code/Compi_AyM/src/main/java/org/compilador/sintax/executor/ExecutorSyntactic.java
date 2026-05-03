package org.compilador.sintax.executor;

import org.compilador.exception.LexicalException;
import org.compilador.exception.SyntacticException;
import org.compilador.lexer.lexicalAnalyzer.LexicalAnalyzer;
import org.compilador.sintax.SyntacticAnalyzer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ExecutorSyntactic {

    public static void main(String[] args) {

        //String sourceFile = "/home/aida/Documentos/Compi/prueba.txt";
        String sourceFile = "C:/Users/Usuario/Documents/MARTI/prueba compi.txt";
        String source = readSourceFile(sourceFile);

        String result = runSyntacticAnalysis(source);

        System.out.println(result);
    }

    public static String runSyntacticAnalysis(String source) {
        LexicalAnalyzer lexer = new LexicalAnalyzer(source);
        SyntacticAnalyzer parser = new SyntacticAnalyzer(lexer);

        try {
            parser.SynAnalyzer();
            return "CORRECTO: ANALISIS SINTACTICO\n El programa es sintacticamente valido.";

        } catch (LexicalException e) {
            return e.formatError();

        } catch (SyntacticException e) {
            return e.formatError();
        }
    }

    private static String readSourceFile(String path) {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(path));
            return new String(bytes);
        } catch (IOException e) {
            System.err.println("ERROR: No se pudo leer el archivo '" + path + "': " + e.getMessage());
            System.exit(1);
            return null;
        }
    }
}
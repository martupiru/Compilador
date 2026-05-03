package org.compilador.lexer.executor;
import org.compilador.exception.LexicalException;
import org.compilador.lexer.lexicalAnalyzer.LexicalAnalyzer;
import org.compilador.lexer.token.Token;
import org.compilador.lexer.token.TokenType;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
public class Executor {

   public static void main(String[] args) {

       String sourceFile = "C:/Users/Usuario/Documents/MARTI/prueba compi.txt";
       //String sourceFile = "/home/aida/Documentos/Compi/prueba.txt";

       String outputFile = null;

       String source = readSourceFile(sourceFile);
       String result = runLexicalAnalysis(source);
       writeOutput(result, outputFile);
   }

    public static String runLexicalAnalysis(String source) {
        LexicalAnalyzer lexer = new LexicalAnalyzer(source);
        List<Token> tokens = new ArrayList<>();

        try {
            Token token;
            do {
                token = lexer.nextToken();
                tokens.add(token);
            } while (token.getType() != TokenType.EOF);

            return formatSuccess(tokens);

        } catch (LexicalException e) {
            return e.formatError();
        }
    }
    private static String formatSuccess(List<Token> tokens) {
        StringBuilder sb = new StringBuilder();
        sb.append("CORRECTO: ANALISIS LEXICO\n");
        sb.append("| TOKEN | LEXEMA | NUMERO DE LINEA (NUMERO DE COLUMNA) |\n");

        for (Token t : tokens) {
            sb.append(t.toString()).append("\n");
        }

        return sb.toString().trim();
    }
    private static void writeOutput(String content, String outputFile) {
        if (outputFile == null) {
            System.out.println(content);
        } else {
            try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
                writer.println(content);
            } catch (IOException e) {
                System.err.println("ERROR: No se pudo escribir en '" + outputFile + "': " + e.getMessage());
                System.exit(1);
            }
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


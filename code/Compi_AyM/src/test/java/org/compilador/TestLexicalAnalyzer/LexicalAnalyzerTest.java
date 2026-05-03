package org.compilador.TestLexicalAnalyzer;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.compilador.lexer.executor.Executor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


/**
 * Tests para el Analizador Lexico de tinyS.
 * Tecnica empleada: caja negra por clases de equivalencia.
 * Los archivos fuente (.s) se encuentran en la carpeta tc/.
 * Se cubren:
 *  Casos validos:tokens reconocidos correctamente "CORRECTO: ANALISIS LEXICO"
 *  Casos de error: LexicalException lanzada ante entrada invalida "ERROR: LEXICO"
 * Archivos fuente usados: ver carpeta /tc
 * @author Martina Nahman
 */

public class LexicalAnalyzerTest extends TestCase {

    public LexicalAnalyzerTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(LexicalAnalyzerTest.class);
    }

    // ---------------------------------------------------------------
    // Auxiliar: lee el archivo .s y ejecuta el analisis lexico
    // ---------------------------------------------------------------
    private String analizar(String rutaRelativa) {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(rutaRelativa));
            String source = new String(bytes);
            return Executor.runLexicalAnalysis(source);
        } catch (IOException e) {
            fail("No se pudo leer el archivo: " + rutaRelativa);
            return null;
        }
    }

    // ===============================================================
    // CASOS EXITOSOS
    // ===============================================================

    /**
     * LEX-OK-01: programa minimo valido, solo start vacio.
     * El lexer debe tokenizar sin error y retornar CORRECTO.
     * Archivo: tc/lex_ok_01.s
     */
    public void testLexOk01_StartVacio() {
        String resultado = analizar("tc/etapa1_lexico/lex_ok_01.s");
        assertTrue(
                "Se esperaba CORRECTO pero fue: " + resultado,
                resultado.startsWith("CORRECTO: ANALISIS LEXICO")
        );
    }

    /**
     * LEX-OK-02: clase con atributos pub/privados, constructor y metodo con retorno.
     * Verifica keywords: class, impl, pub, fn, ret, new.
     * Archivo: tc/lex_ok_02.s
     */
    public void testLexOk02_ClaseConstructorMetodo() {
        String resultado = analizar("tc/etapa1_lexico/lex_ok_02.s");
        assertTrue(
                "Se esperaba CORRECTO pero fue: " + resultado,
                resultado.startsWith("CORRECTO: ANALISIS LEXICO")
        );
    }

    /**
     * LEX-OK-03: literales enteros, cadenas con escapes validos (\n \t \"),
     * operadores aritmeticos/logicos y comentarios de linea y multilinea.
     * Archivo: tc/lex_ok_03.s
     */
    public void testLexOk03_LiteralesOperadoresComentarios() {
        String resultado = analizar("tc/etapa1_lexico/lex_ok_03.s");
        assertTrue(
                "Se esperaba CORRECTO pero fue: " + resultado,
                resultado.startsWith("CORRECTO: ANALISIS LEXICO")
        );
    }

    /**
     * LEX-OK-04: herencia, metodo estatico (st fn), while y llamada estatica (Clase.met()).
     * Archivo: tc/lex_ok_04.s
     */
    public void testLexOk04_HerenciaMetodoEstaticoWhile() {
        String resultado = analizar("tc/etapa1_lexico/lex_ok_04.s");
        assertTrue(
                "Se esperaba CORRECTO pero fue: " + resultado,
                resultado.startsWith("CORRECTO: ANALISIS LEXICO")
        );
    }

    /**
     * LEX-OK-05: arrays, for-in, if-else anidado, nil y ret sin expresion.
     * Verifica: Array, for, in, if, else, nil, void.
     * Archivo: tc/lex_ok_05.s
     */
    public void testLexOk05_ArraysForIfElseNil() {
        String resultado = analizar("tc/etapa1_lexico/lex_ok_05.s");
        assertTrue(
                "Se esperaba CORRECTO pero fue: " + resultado,
                resultado.startsWith("CORRECTO: ANALISIS LEXICO")
        );
    }

    // ===============================================================
    // CASOS DE ERROR
    // ===============================================================

    /**
     * LEX-ERR-01: cadena sin cerrar (salto de linea dentro del string).
     * El lexer lanza LexicalException -> resultado empieza con ERROR: LEXICO.
     * Archivo: tc/lex_err_01.s
     */
    public void testLexErr01_CadenaSinCerrar() {
        String resultado = analizar("tc/etapa1_lexico/lex_err_01.s");
        assertTrue(
                "Se esperaba ERROR: LEXICO pero fue: " + resultado,
                resultado.startsWith("ERROR: LEXICO")
        );
    }

    /**
     * LEX-ERR-02: caracter invalido '@' fuera de comentario o string.
     * Archivo: tc/lex_err_02.s
     */
    public void testLexErr02_CaracterInvalido() {
        String resultado = analizar("tc/etapa1_lexico/lex_err_02.s");
        assertTrue(
                "Se esperaba ERROR: LEXICO pero fue: " + resultado,
                resultado.startsWith("ERROR: LEXICO")
        );
    }

    /**
     * LEX-ERR-03: comentario multilinea sin cerrar (falta *\/).
     * Archivo: tc/lex_err_03.s
     */
    public void testLexErr03_ComentarioSinCerrar() {
        String resultado = analizar("tc/etapa1_lexico/lex_err_03.s");
        assertTrue(
                "Se esperaba ERROR: LEXICO pero fue: " + resultado,
                resultado.startsWith("ERROR: LEXICO")
        );
    }

    /**
     * LEX-ERR-04: secuencia de escape invalida dentro de string (ej: \z).
     * Archivo: tc/lex_err_04.s
     */
    public void testLexErr04_EscapeInvalido() {
        String resultado = analizar("tc/etapa1_lexico/lex_err_04.s");
        assertTrue(
                "Se esperaba ERROR: LEXICO pero fue: " + resultado,
                resultado.startsWith("ERROR: LEXICO")
        );
    }

    /**
     * LEX-ERR-05: pipe suelto '|' que no forma el operador '||'.
     * Archivo: tc/lex_err_05.s
     */
    public void testLexErr05_PipeSuelto() {
        String resultado = analizar("tc/etapa1_lexico/lex_err_05.s");
        assertTrue(
                "Se esperaba ERROR: LEXICO pero fue: " + resultado,
                resultado.startsWith("ERROR: LEXICO")
        );
    }
}
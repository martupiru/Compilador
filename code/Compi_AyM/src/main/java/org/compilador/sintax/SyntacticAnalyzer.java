package org.compilador.sintax;

import org.compilador.exception.SyntacticException;
import org.compilador.exception.LexicalException;
import org.compilador.lexer.lexicalAnalyzer.LexicalAnalyzer;
import org.compilador.lexer.token.Token;
import org.compilador.lexer.token.TokenType;

public class SyntacticAnalyzer {

    private LexicalAnalyzer lexer;
    private Token currentToken;
    private Token lookaheadToken;
    private boolean hasLookahead = false;

    public SyntacticAnalyzer(LexicalAnalyzer lexer) {
        this.lexer = lexer;
        this.currentToken = lexer.nextToken();
    }

    /**
     * Retorna el tipo del token actual.
     * @return TokenType actual
     * @author Aida Laricchia y Martina Nahman
     */

    private TokenType current() {
        return currentToken.getType();
    }

    /**
     * Comprueba si el token actual coincide con el tipo esperado y avanza al siguiente.
     * Consume el lookaheadToken si existe, de lo contrario pide uno nuevo al lexer.
     *
     * @param expected El tipo de token esperado.
     * @throws LexicalException Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * @author Aida Laricchia y Martina Nahman
     */

    private void match(TokenType expected) {
        if (current() == expected) {
            if (hasLookahead) {
                currentToken = lookaheadToken;
                hasLookahead = false;
            } else {
                currentToken = lexer.nextToken();
            }
        } else {
            throw new SyntacticException(
                    "Se esperaba " + expected + ", pero se encontro " + current()
                            + " ('" + currentToken.getLexema() + "')",
                    currentToken.getLine(),
                    currentToken.getColumn()
            );
        }
    }
    /**
     * Lee el siguiente token sin consumirlo para resolver ambigüedades LL(1).
     *
     * @throws LexicalException Excepción ocasionada por un error léxico
     * @author Aida Laricchia y Martina Nahman
     */
    private void peekToken() {
        if (!hasLookahead) {
            lookaheadToken = lexer.nextToken();
            hasLookahead = true;
        }
    }

    public void SynAnalyzer() throws LexicalException, SyntacticException{
        program();

    }

    private boolean esTipo() {
        return current() == TokenType.TYPE_STR   ||  // Str
                current() == TokenType.TYPE_BOOL  ||  // Bool
                current() == TokenType.TYPE_INT   ||  // Int
                current() == TokenType.ID_CLASS      ||  // MiClase (idclass)
                current() == TokenType.TYPE_ARRAY;    // Array
    }

    // <program> ::= <Lista-Definiciones> <Start>
    // PRIMEROS: class, impl, start
    public void program() {
        listaDefiniciones();
        start();
        match(TokenType.EOF);
    }

    // <Lista-Definiciones> ::= <class> <Lista-Definiciones>
    //                        | <Impl> <Lista-Definiciones>
    //                        | Empty
    // PRIMEROS: class, impl, lambda   SIGUIENTES: start
    public void listaDefiniciones() {
        if (current() == TokenType.KW_CLASS) {
            classDef();
            listaDefiniciones();
        } else if (current() == TokenType.KW_IMPL) {
            implDef();
            listaDefiniciones();
        }
        // lambda: siguiente es start, salimos
    }

    // <Start> ::= start <Bloque-Metodo>
    // PRIMEROS: start
    public void start() {
        if (current() == TokenType.ID_START) {
            match(TokenType.ID_START);
            bloqueMetodo();
        } else {
            throw new SyntacticException(
                    "Se esperaba 'start', se encontro " + current(),
                    currentToken.getLine(), currentToken.getColumn()
            );
        }
    }

    // <class> ::= class idclass <class-Fact>
    // PRIMEROS: class
    public void classDef() {
        match(TokenType.KW_CLASS);
        match(TokenType.ID_CLASS);
        classDefFact();
    }

    // <class-Fact> ::= <Herencia> { <NAtributos> }
    //               | { <NAtributos> }
    // PRIMEROS: : , {
    public void classDefFact() {
        if (current() == TokenType.DOSPUNTOS) {
            herencia();
            match(TokenType.ILLAVE);
            nAtributos();
            match(TokenType.DLLAVE);
        } else if (current() == TokenType.ILLAVE) {
            match(TokenType.ILLAVE);
            nAtributos();
            match(TokenType.DLLAVE);
        } else {
            throw new SyntacticException(
                    "Se esperaba ':' o '{', se encontro " + current(),
                    currentToken.getLine(), currentToken.getColumn()
            );
        }
    }

    // <Herencia> ::= : <Tipo>
    // PRIMEROS: :
    public void herencia() {
        match(TokenType.DOSPUNTOS);
        tipo();
    }

    // <Impl> ::= impl idclass { <NMiembro> }
    // PRIMEROS: impl
    public void implDef() {
        match(TokenType.KW_IMPL);
        match(TokenType.ID_CLASS);
        match(TokenType.ILLAVE);
        nMiembro();
        match(TokenType.DLLAVE);
    }

    // <NMiembro> ::= <Miembro> <NMiembro> | Empty
    // PRIMEROS: fn, st, . , lambda   SIGUIENTES: }
    public void nMiembro() {
        if (current() == TokenType.KW_FN  || current() == TokenType.KW_ST  || current() == TokenType.PUNTO) {
            miembro();
            nMiembro();
        }
        // lambda: siguiente es }, salimos
    }

    // <NAtributos> ::= <Atributo> <NAtributos> | Empty
    // PRIMEROS: Str, Bool, Int, idclass, Array, pub, lambda   SIGUIENTES: }
    public void nAtributos() {
        if (esTipo() || current() == TokenType.KW_PUB) {
            atributo();
            nAtributos();
        }
        // lambda: siguiente es }, salimos
    }

    public void miembro() {
        if (current() == TokenType.KW_FN || current() == TokenType.KW_ST) {
            miembro();

        }else if (current() == TokenType.PUNTO) {
            constructor();
        }else{
            throw new SyntacticException(
                    "Se esperaba un miembro (fn, st o '.'), se encontro " + current(),
                    currentToken.getLine(), currentToken.getColumn()
            );
        }
    }

    public void bloqueMetodo(){
        match(TokenType.ILLAVE);
        nDecleclaracionVarLocales();
        nSentencia();
        match(TokenType.DLLAVE);
    }

    public void atributo() {
        if (esTipo()) {
            tipo();
            listaDeclaracionVariables();
            match(TokenType.PUNTOCOMA);
        } else if (current() == TokenType.KW_PUB) {
            visibilidad();
            tipo();
            listaDeclaracionVariables();
            match(TokenType.PUNTOCOMA);

        }else{
            throw new SyntacticException(
                    "Se esperaba un miembro (fn, st o '.'), se encontro " + current(),
                    currentToken.getLine(), currentToken.getColumn()
            );
        }
    }

    public void constructor() {
        // ACÁ VA EXCEPCION??
        match(TokenType.PUNTO);
        argumentosFormales();
        bloqueMetodo();
    }

}

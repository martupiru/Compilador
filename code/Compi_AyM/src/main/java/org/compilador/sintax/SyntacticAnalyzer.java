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
//PRIMEROSSSSSSSSSS
    private boolean esTipo() {
        return current() == TokenType.TYPE_STR   ||  // Str
                current() == TokenType.TYPE_BOOL  ||  // Bool
                current() == TokenType.TYPE_INT   ||  // Int
                current() == TokenType.ID_CLASS      ||  // MiClase (idclass)
                current() == TokenType.TYPE_ARRAY;    // Array
    }


    /** PRIMEROS de TipoPrimitivo: Str, Bool, Int */
    private boolean esTipoPrimitivo() {
        return current() == TokenType.TYPE_STR  ||
                current() == TokenType.TYPE_BOOL ||
                current() == TokenType.TYPE_INT;
    }

    /** PRIMEROS de Literal: nil, true, false, intLiteral, StrLiteral */
    private boolean esLiteral() {
        return current() == TokenType.KW_NIL   ||
                current() == TokenType.KW_TRUE  ||
                current() == TokenType.KW_FALSE ||
                current() == TokenType.LIT_INT  ||
                current() == TokenType.LIT_STR;
    }

    /** PRIMEROS de Primario: (, self, id, idclass, new */
    private boolean esPrimeroPrimario() {
        return current() == TokenType.IPAREN     ||
                current() == TokenType.KW_SELF    ||
                current() == TokenType.ID_MET_AT  ||
                current() == TokenType.ID_CLASS   ||
                current() == TokenType.KW_NEW;
    }

    /** PRIMEROS de Expresion: +,-,!,++,--,nil,true,false,intLit,StrLit,(,self,id,idclass,new */
    private boolean esPrimeroExpresion() {
        return current() == TokenType.OP_SUM   ||
                current() == TokenType.OP_REST   ||
                current() == TokenType.OP_NOT     ||
                current() == TokenType.OP_INC     || //++
                current() == TokenType.OP_DEC     || //--
                current() == TokenType.KW_NIL     ||
                current() == TokenType.KW_TRUE    ||
                current() == TokenType.KW_FALSE   ||
                current() == TokenType.LIT_INT    ||
                current() == TokenType.LIT_STR    ||
                current() == TokenType.IPAREN     ||
                current() == TokenType.KW_SELF    ||
                current() == TokenType.ID_MET_AT  ||
                current() == TokenType.ID_CLASS   ||
                current() == TokenType.KW_NEW;
    }

    /** PRIMEROS de Sentencia: ; if while for ret id self ( { */
    private boolean esPrimeroSentencia() {
        return current() == TokenType.PUNTOCOMA  ||
                current() == TokenType.KW_IF      ||
                current() == TokenType.KW_WHILE   ||
                current() == TokenType.KW_FOR     ||
                current() == TokenType.KW_RET     ||
                current() == TokenType.ID_MET_AT  ||
                current() == TokenType.KW_SELF    ||
                current() == TokenType.IPAREN     ||
                current() == TokenType.ILLAVE;
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

    public void tipo(){
        if (esTipoPrimitivo()){
            tipoPrimitivo();
        } else if (current()==TokenType.ID_CLASS) {
            tipoReferencia();
        } else if (current()==TokenType.TYPE_ARRAY) {
            tipoArreglo();
        }else {
            throw new SyntacticException(
                    "Se esperaba ':' o '{', se encontro " + current(),
                    currentToken.getLine(), currentToken.getColumn()
            );
        }
    }
    public void tipoPrimitivo(){
        if (current()==TokenType.TYPE_STR){
            match(TokenType.TYPE_STR);
        } else if (current()==TokenType.TYPE_BOOL) {
            match(TokenType.TYPE_BOOL);
        } else if (current()==TokenType.TYPE_INT) {
            match(TokenType.TYPE_INT);

        }//no hay error pq ya esta en tipo entonces si el lambda tira error ahi
    }
    public void tipoReferencia(){
        if (current()==TokenType.ID_CLASS){
            match(TokenType.ID_CLASS);
        }
    }
    public void tipoArreglo(){
        if (current()==TokenType.TYPE_ARRAY){
            match(TokenType.TYPE_ARRAY);
            tipoPrimitivo();
        }
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
            metodo();

        }else if (current() == TokenType.PUNTO) {
            constructor();
        }else{
            throw new SyntacticException(
                    "Se esperaba un miembro (fn, st o '.'), se encontro " + current(),
                    currentToken.getLine(), currentToken.getColumn()
            );
        }
    }
    public void metodo(){
            if (current()==TokenType.KW_FN){
                match(TokenType.KW_FN);
                tipoMetodoFact();
                match(TokenType.ID_MET_AT);
                argumentosFormales();
                bloqueMetodo();
            } else if (current()==TokenType.KW_ST) {
                formaMetodo();
                match(TokenType.KW_FN);
                tipoMetodoFact();
                match(TokenType.ID_MET_AT);
                argumentosFormales();
                bloqueMetodo();
            }else{
                throw new SyntacticException(
                        "Se esperaba un miembro (fn, st o '.'), se encontro " + current(),
                        currentToken.getLine(), currentToken.getColumn()
                );
            }
    }
    public void tipoMetodoFact(){
        if (esTipo()||current()==TokenType.TYPE_VOID){
            tipoMetodo();
        }//tipo metodoFact produce lambda por eso no hay exception
    }
    public void visibilidad(){
        match(TokenType.KW_PUB); //la exeption la maneja el match
    }
    public void formaMetodo(){
        match(TokenType.KW_ST); //la exeption la maneja el match
    }
    public void nSentencia(){
        if(esPrimeroSentencia()){
            sentencia();
            nSentencia();
        }//sin exception por lambda
    }


    public void bloqueMetodo(){
        match(TokenType.ILLAVE);
        nDeclaracionVarLocales();
        nSentencia();
        match(TokenType.DLLAVE);
    }
    public void nDeclaracionVarLocales(){
        if (esTipo()){
            declaracionVarLocal();
            nDeclaracionVarLocales();
        }
    }
    public void declaracionVarLocal(){
        if(esTipo()){
            tipo();
            listaDeclaracionVariables();
        }
    }
    public void listaDeclaracionVariables(){
        match(TokenType.ID_MET_AT);
        listaDeclaracionVariablesFact();
    }
    public void listaDeclaracionVariablesFact(){
        match(TokenType.COMA);
        listaDeclaracionVariables();
    }
    public void argumentosFormales(){
        match(TokenType.IPAREN);
        //⟨Argumentos-Formales⟩ ::= ( ⟨Lista-Argumentos-Formales⟩ ) | ()
        //<Lista-Argumentos-Formales> ::= <Argumento-Formal> <Lista-Argumentos-Formales-Fact>
        //<Argumento-Formal> ::= <Tipo> idMetAt
        if (esTipo()){ //por los primeros de argumento formal q son los primeros de tipo
            listaArgumentosFormales();
        }
        match(TokenType.DPAREN);
    }
    public void listaArgumentosFormales(){
        if(esTipo()){
            argumentoFormal();
            listaArgumentosFormalesFact();
        }else{
            throw new SyntacticException(
                    "Se esperaba un miembro (fn, st o '.'), se encontro " + current(),
                    currentToken.getLine(), currentToken.getColumn()
            );
        }
    }
    public void listaArgumentosFormalesFact(){
        if(current()==TokenType.COMA){
            match(TokenType.COMA);
            listaArgumentosFormales();
        }
    }//sin exception por lambda
    public void argumentoFormal(){
        tipo();
        match(TokenType.ID_MET_AT);
    }
    public void tipoMetodo(){
        if (esTipo()){
            tipo();
        } else if (current()==TokenType.TYPE_VOID) {
            match(TokenType.TYPE_VOID);
        }else {
            throw new SyntacticException(
                    "Se esperaba un miembro (fn, st o '.'), se encontro " + current(),
                    currentToken.getLine(), currentToken.getColumn()
            );
        }
    }
    public void sentencia(){
        if(current()==TokenType.PUNTOCOMA){
            match(TokenType.PUNTOCOMA);
        } else if (current()==TokenType.ID) {

        } else if () {

        }
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

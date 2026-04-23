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
    public void sentencia(){
        if (current()==TokenType.PUNTOCOMA){
            match(TokenType.PUNTOCOMA);
        } else if (current()==TokenType.KW_IF) {
            match(TokenType.KW_IF);
            match(TokenType.IPAREN);
            expresion();
            match(TokenType.DPAREN);
            sentenciaElseFact();
        }else if(current()==TokenType.KW_WHILE){
            match(TokenType.KW_WHILE);
            match(TokenType.IPAREN);
            expresion();
            match(TokenType.DPAREN);
            sentencia();
        }else if (current()==TokenType.KW_FOR){
            match(TokenType.KW_FOR);
            match(TokenType.IPAREN);
            tipoPrimitivo();
            match(TokenType.ID_MET_AT);
            match(TokenType.KW_IN);
            match(TokenType.ID_MET_AT);
            match(TokenType.DPAREN);
            sentencia();
        }else if(current()==TokenType.KW_RET){
            match(TokenType.KW_RET);
            expresionFact();
            match(TokenType.PUNTOCOMA);
          //primeros de asignacion
        } else if (current()==TokenType.ID_MET_AT ||current()== TokenType.KW_SELF) {
            asignacion();
            match(TokenType.PUNTOCOMA);
        } else if (current()==TokenType.IPAREN) {
            sentenciaSimple();
            match(TokenType.PUNTOCOMA);
        } else if (current()==TokenType.ILLAVE) {
            bloque();
        }//else {
           // throw new SyntacticException(
             //       "Se esperaba ':' o '{', se encontro " + current(),
               //     currentToken.getLine(), currentToken.getColumn()
            //);}
            //!!!!!!!!!!!!!poner error

    }
    public void expresionFact() {
        if(esPrimeroExpresion()) {
            expresion();
        }//no hay error en el else pq acepta lambda
    }

    public void sentenciaElseFact(){
        if(current()==TokenType.KW_ELSE){
            match(TokenType.KW_ELSE);
            sentencia();
        }//no hay error pq hacepta lambda
    }
    public void bloque(){
        match(TokenType.ILLAVE);
        nSentencia();
        match(TokenType.DLLAVE);
    }
    public void asignacion(){
        if (current()==TokenType.ID_MET_AT){
            accesoVarSimple();
            match(TokenType.ASIGN);
            expresion();
        }else if(current()==TokenType.KW_SELF){
            accesoSelfSimple();
            match(TokenType.ASIGN);
            expresion();
        }else{
            Exception//!!!!!!!!!!!!!!!
        }
    }
    public void accesoVarSimple(){
        match(TokenType.ID_MET_AT);
        accesoVarSimpleFact();
    }
    // <AccesoVar-Simple-Fact> ::= <NEncadenado-Simple>
    //                           | [ <Expresion> ]
    // PRIMEROS: . [ lambda   SIGUIENTES: =
    // ================================================================
    public void accesoVarSimpleFact() {
        if (current() == TokenType.ICORCHETE) {
            match(TokenType.ICORCHETE);
            expresion();
            match(TokenType.DCORCHETE);
        } else {
            nEncadenadoSimple();
        }
    }
    // ================================================================
    // <AccesoSelf-Simple> ::= self <NEncadenado-Simple>
    // ================================================================
    public void accesoSelfSimple() {
        match(TokenType.KW_SELF);
        nEncadenadoSimple();
    }
    // ================================================================
    // <NEncadenado-Simple> ::= <Encadenado-Simple> <NEncadenado-Simple> | Empty
    // PRIMEROS: . lambda   SIGUIENTES: =
    // ================================================================
    public void nEncadenadoSimple() {
        if (current() == TokenType.PUNTO) {
            encadenadoSimple();
            nEncadenadoSimple();
        }
        // lambda
    }
    // ================================================================
    // <Encadenado-Simple> ::= . id
    // ================================================================
    public void encadenadoSimple() {
        match(TokenType.PUNTO);
        match(TokenType.ID_MET_AT);
    }
    public void sentenciaSimple() {
        match(TokenType.IPAREN);
        expresion();
        match(TokenType.DPAREN);
    }
///EXPRESION
public void expresion() {
    expOr();
}

    // <ExpOr> ::= <ExpAnd> <ExpOrRec>
    public void expOr() {
        expAnd();
        expOrRec();
    }

    // <ExpOrRec> ::= || <ExpAnd> <ExpOrRec> | Empty
    // PRIMEROS: || lambda   SIGUIENTES: ) ; ] ,
    public void expOrRec() {
        if (current() == TokenType.OP_OR) {
            match(TokenType.OP_OR);
            expAnd();
            expOrRec();
        }
        // lambda
    }

    // <ExpAnd> ::= <ExpIgual> <ExpAndRec>
    public void expAnd() {
        expIgual();
        expAndRec();
    }

    // <ExpAndRec> ::= && <ExpIgual> <ExpAndRec> | Empty
    // PRIMEROS: && lambda   SIGUIENTES: ) ; ] , ||
    public void expAndRec() {
        if (current() == TokenType.OP_AND) {
            match(TokenType.OP_AND);
            expIgual();
            expAndRec();
        }
        // lambda
    }
    // <ExpIgual> ::= <ExpCompuesta> <ExpIgualRec>
    public void expIgual() {
        expCompuesta();
        expIgualRec();
    }

    // <ExpIgualRec> ::= <OpIgual> <ExpCompuesta> <ExpIgualRec> | Empty
    // PRIMEROS: == != lambda   SIGUIENTES: ) ; ] , || &&
    public void expIgualRec() {
        if (current() == TokenType.OP_EQUAL || current() == TokenType.OP_NOT_EQUAL) {
            opIgual();
            expCompuesta();
            expIgualRec();
        }
        // lambda
    }

    // ================================================================
    // <ExpCompuesta> ::= <ExpAd> <OpCompuesto> <ExpAd>
    //                  | <ExpAd>
    // PRIMEROS: +,-,!,++,--,nil,true,false,intLit,StrLit,(,self,id,idclass,new
    // ================================================================
    public void expCompuesta() {
        expAd();
        if (current() == TokenType.OP_LESS       ||
                current() == TokenType.OP_GREATER    ||
                current() == TokenType.OP_LESS_EQ    ||
                current() == TokenType.OP_GREATER_EQ) {
            opCompuesto();
            expAd();
        }
        // si no hay opCompuesto -> produccion <ExpAd> sola, salimos
    }

    // ================================================================
    // <ExpAd> ::= <ExpMul> <ExpAdRec>
    // PRIMEROS: +,-,!,++,--,nil,true,false,intLit,StrLit,(,self,id,idclass,new
    // ================================================================
    public void expAd() {
        expMul();
        expAdRec();
    }

    // ================================================================
    // <ExpAdRec> ::= <OpAd> <ExpMul> <ExpAdRec> | Empty
    // PRIMEROS: + - lambda
    // SIGUIENTES: < > <= >= ) ; ] , || && == !=
    // ================================================================
    public void expAdRec() {
        if (current() == TokenType.OP_SUM || current() == TokenType.OP_REST) {
            opAd();
            expMul();
            expAdRec();
        }
        // lambda
    }

    // ================================================================
    // <ExpMul> ::= <ExpUn> <ExpMulRec>
    // PRIMEROS: +,-,!,++,--,nil,true,false,intLit,StrLit,(,self,id,idclass,new
    // ================================================================
    public void expMul() {
        expUn();
        expMulRec();
    }

    // ================================================================
    // <ExpMulRec> ::= <OpMul> <ExpUn> <ExpMulRec> | Empty
    // PRIMEROS: * / lambda
    // SIGUIENTES: < > <= >= ) ; ] , || && == !=
    // ================================================================
    public void expMulRec() {
        if (current() == TokenType.OP_MULT || current() == TokenType.OP_DIV) {
            opMul();
            expUn();
            expMulRec();
        }
        // lambda
    }

    // ================================================================
    // <ExpUn> ::= <OpUnario> <ExpUn> | <Operando>
    // PRIMEROS: +,-,!,++,--,nil,true,false,intLit,StrLit,(,self,id,idclass,new
    // ================================================================
    public void expUn() {
        if (current() == TokenType.OP_SUM  ||
                current() == TokenType.OP_REST ||
                current() == TokenType.OP_NOT   ||
                current() == TokenType.OP_INC   ||
                current() == TokenType.OP_DEC) {
            opUnario();
            expUn();
        } else if (esLiteral() || esPrimeroPrimario()) {
            operando();
        } else {
            throw new SyntacticException(
                    "Se esperaba expresion unaria u operando, se encontro " + current(),
                    currentToken.getLine(), currentToken.getColumn()
            );
        }
    }



//OPERADORES
public void opIgual() {
    if (current() == TokenType.OP_EQUAL) {
        match(TokenType.OP_EQUAL);
    } else if (current() == TokenType.OP_NOT_EQUAL) {
        match(TokenType.OP_NOT_EQUAL);
    } else {
        throw new SyntacticException(
                "Se esperaba == o !=, se encontro " + current(),
                currentToken.getLine(), currentToken.getColumn()
        );
    }
}

    public void opCompuesto() {
        if (current() == TokenType.OP_LESS) {
            match(TokenType.OP_LESS);
        } else if (current() == TokenType.OP_GREATER) {
            match(TokenType.OP_GREATER);
        } else if (current() == TokenType.OP_LESS_EQ) {
            match(TokenType.OP_LESS_EQ);
        } else if (current() == TokenType.OP_GREATER_EQ) {
            match(TokenType.OP_GREATER_EQ);
        } else {
            throw new SyntacticException(
                    "Se esperaba operador compuesto (<, >, <=, >=), se encontro " + current(),
                    currentToken.getLine(), currentToken.getColumn()
            );
        }
    }
    public void opAd() {
        if (current() == TokenType.OP_SUM) {
            match(TokenType.OP_SUM);
        } else if (current() == TokenType.OP_REST) {
            match(TokenType.OP_REST);
        } else {
            throw new SyntacticException(
                    "Se esperaba + o -, se encontro " + current(),
                    currentToken.getLine(), currentToken.getColumn()
            );
        }
    }

    public void opUnario() {
        if (current() == TokenType.OP_SUM) {
            match(TokenType.OP_SUM);
        } else if (current() == TokenType.OP_REST) {
            match(TokenType.OP_REST);
        } else if (current() == TokenType.OP_NOT) {
            match(TokenType.OP_NOT);
        } else if (current() == TokenType.OP_INC) {
            match(TokenType.OP_INC);
        } else if (current() == TokenType.OP_DEC) {
            match(TokenType.OP_DEC);
        } else {
            throw new SyntacticException(
                    "Se esperaba operador unario (+,-,!,++,--), se encontro " + current(),
                    currentToken.getLine(), currentToken.getColumn()
            );
        }
    }

    public void opMul() {
        if (current() == TokenType.OP_MULT) {
            match(TokenType.OP_MULT);
        } else if (current() == TokenType.OP_DIV) {
            match(TokenType.OP_DIV);
        } else {
            throw new SyntacticException(
                    "Se esperaba * o /, se encontro " + current(),
                    currentToken.getLine(), currentToken.getColumn()
            );
        }
    }
    public void operando() {
        if (esLiteral()) {
            literal();
        } else if (esPrimeroPrimario()) {
            primario();
            encadenadoEmpty();
        } else {
            throw new SyntacticException(
                    "Se esperaba un operando, se encontro " + current(),
                    currentToken.getLine(), currentToken.getColumn()
            );
        }
    }

    // <Literal> ::= nil | true | false | intLiteral | StrLiteral
    public void literal() {
        if (current() == TokenType.KW_NIL) {
            match(TokenType.KW_NIL);
        } else if (current() == TokenType.KW_TRUE) {
            match(TokenType.KW_TRUE);
        } else if (current() == TokenType.KW_FALSE) {
            match(TokenType.KW_FALSE);
        } else if (current() == TokenType.LIT_INT) {
            match(TokenType.LIT_INT);
        } else if (current() == TokenType.LIT_STR) {
            match(TokenType.LIT_STR);
        } else {
            throw new SyntacticException(
                    "Se esperaba un literal, se encontro " + current(),
                    currentToken.getLine(), currentToken.getColumn()
            );
        }
    }
    // ================================================================
    // <Primario> ::= <ExpresionParentizada> | <AccesoSelf> | <AccesoVar>
    //              | <Llamada-Metodo> | <Llamada-Metodo-Estatico> | <Llamada-Constructor>
    // PRIMEROS: (, self, id, idclass, new
    // ================================================================
    public void primario() {
        if (current() == TokenType.IPAREN) {
            expresionParentizada();
        } else if (current() == TokenType.KW_SELF) {
            accesoSelf();
        } else if (current() == TokenType.ID_CLASS) {
            llamadaMetodoEstatico();
        } else if (current() == TokenType.KW_NEW) {
            llamadaConstructor();
        } else if (current() == TokenType.ID_MET_AT) {
            // id -> AccesoVar o Llamada-Metodo segun lo que siga
            //OJOOOOOOOOOOOOOO ACAAAAAAAAAA REVISAR Q PASA
            accesoVarOLlamadaMetodo();
        } else {
            throw new SyntacticException(
                    "Se esperaba primario, se encontro " + current(),
                    currentToken.getLine(), currentToken.getColumn()
            );
        }
    }
    public void accesoVarOLlamadaMetodo() {
        match(TokenType.ID_MET_AT);
        if (current() == TokenType.IPAREN) {
            // <Llamada-Metodo> -> id <Argumentos-Actuales> <Encadenado-Empty>
            argumentosActuales();
            encadenadoEmpty();
        } else if (current()==TokenType.PUNTO || current()==TokenType.ICORCHETE) {
            accesoVarFact();
        }else {
            error
        }
    }



    public void expresionParentizada() {
        match(TokenType.IPAREN);
        expresion();
        match(TokenType.DPAREN);
        encadenadoEmpty();
    }

    // <AccesoSelf> ::= self <Encadenado-Empty>
    public void accesoSelf() {
        match(TokenType.KW_SELF);
        encadenadoEmpty();
    }
    public void accesoVarFact() {
        if (current() == TokenType.ICORCHETE) {
            // produccion: [ <Expresion> ] <Encadenado-Empty>
            match(TokenType.ICORCHETE);
            expresion();
            match(TokenType.DCORCHETE);
            encadenadoEmpty();
        } else if (current() == TokenType.PUNTO) {
            // produccion: <Encadenado>
            encadenado();
        }else{
            error
        }
    }

    // <Llamada-Metodo-Estatico> ::= idclass . <Llamada-Metodo> <Encadenado-Empty>
    public void llamadaMetodoEstatico() {
        match(TokenType.ID_CLASS);
        match(TokenType.PUNTO);
        llamadaMetodo();
        encadenadoEmpty();
    }

    // <Llamada-Metodo> ::= id <Argumentos-Actuales> <Encadenado-Empty>
    public void llamadaMetodo() {
        match(TokenType.ID_MET_AT);
        argumentosActuales();
        encadenadoEmpty();
    }

    // <Llamada-Constructor> ::= new <Llamada-Constructor-Fact>
    public void llamadaConstructor() {
        match(TokenType.KW_NEW);
        llamadaConstructorFact();
    }
    // ================================================================
    // <Llamada-Constructor-Fact> ::= <Tipo-Primitivo> [ <Expresion> ]
    //                              | idclass <Argumentos-Actuales> <Encadenado-Empty>
    // PRIMEROS: Str, Bool, Int, idclass
    // ================================================================
    public void llamadaConstructorFact() {
        if (current() == TokenType.ID_CLASS) {
            match(TokenType.ID_CLASS);
            argumentosActuales();
            encadenadoEmpty();
        } else if (esTipoPrimitivo()) {
            tipoPrimitivo();
            match(TokenType.ICORCHETE);
            expresion();
            match(TokenType.DCORCHETE);
        } else {
            throw new SyntacticException(
                    "Se esperaba idclass o tipo primitivo despues de 'new', se encontro " + current(),
                    currentToken.getLine(), currentToken.getColumn()
            );
        }
    }
    // <Argumentos-Actuales> ::= ( <Lista-Expresiones> ) | ()
    public void argumentosActuales() {
        match(TokenType.IPAREN);
        if (esPrimeroExpresion()) {
            listaExpresiones();
        }
        match(TokenType.DPAREN);
    }

    // <Lista-Expresiones> ::= <Expresion> <Lista-Expresiones-Fact>
    public void listaExpresiones() {
        expresion();
        listaExpresionesFact();
    }

    // <Lista-Expresiones-Fact> ::= , <Lista-Expresiones> | Empty
    // PRIMEROS: , lambda   SIGUIENTES: )
    public void listaExpresionesFact() {
        if (current() == TokenType.COMA) {
            match(TokenType.COMA);
            listaExpresiones();
        }
        // lambda
    }
    // <Encadenado> ::= . <Encadenado-Fact>
    public void encadenado() {
        match(TokenType.PUNTO);
        encadenadoFact();
    }

    // <Encadenado-Fact> ::= <Llamada-Metodo-Encadenado> | <Acceso-Var>
    // Ambos empiezan con id entonces aca hicimos dos reglas: EncadenadoFact y LlamadaMetodoEncadenado
    public void encadenadoFact() {
        if (current() == TokenType.ID_MET_AT) {
            match(TokenType.ID_MET_AT);
            //Por LlamadaMetodoEncadenado debemos esperar un (
            if (current()==TokenType.IPAREN){
                argumentosActuales();
                encadenadoEmpty();
            //Por AccesoVar esperamos un . o [
            } else if (current()==TokenType.ICORCHETE ||current()==TokenType.PUNTO ) {
                accesoVarFact();
            }
        } else {
            throw new SyntacticException(
                    "Se esperaba id despues de '.', se encontro " + current(),
                    currentToken.getLine(), currentToken.getColumn()
            );
        }
    }


    // <Encadenado-Empty> ::= <Encadenado> | Empty
    // PRIMEROS: . lambda
    public void encadenadoEmpty() {
        if (current() == TokenType.PUNTO) {
            encadenado();
        }
        // lambda
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

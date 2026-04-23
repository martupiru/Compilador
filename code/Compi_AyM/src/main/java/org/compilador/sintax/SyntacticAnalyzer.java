package org.compilador.sintax;

import org.compilador.exception.SyntacticException;
import org.compilador.exception.LexicalException;
import org.compilador.lexer.lexicalAnalyzer.LexicalAnalyzer;
import org.compilador.lexer.token.Token;
import org.compilador.lexer.token.TokenType;

/**
 * Analizador sintáctico compi
 * @author Aida Laricchia y Martina Nahman
 */
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
     */

    private void match(TokenType expected) throws LexicalException, SyntacticException {
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


    //* debemos llamarlo en el executor
    public void SynAnalyzer() throws LexicalException, SyntacticException{
        program();

    }
//PRIMEROSSSSSSSSSS

    /**
     * Verifica si el token actual es un tipo válido (Str, Bool, Int, idclass, Array).
     *
     * @return true si el token actual es un tipo
     */
    private boolean esTipo() {
        return current() == TokenType.TYPE_STR   ||  // Str
                current() == TokenType.TYPE_BOOL  ||  // Bool
                current() == TokenType.TYPE_INT   ||  // Int
                current() == TokenType.ID_CLASS      ||  // MiClase (idclass)
                current() == TokenType.TYPE_ARRAY;    // Array
    }


    /**
     * Verifica si el token actual es un tipo primitivo (Str, Bool, Int).
     *
     * @return true si el token actual es un tipo primitivo
     */
    private boolean esTipoPrimitivo() {
        return current() == TokenType.TYPE_STR  ||
                current() == TokenType.TYPE_BOOL ||
                current() == TokenType.TYPE_INT;
    }

    /**
     * Verifica si el token actual es un literal (nil, true, false, intLiteral, StrLiteral).
     *
     * @return true si el token actual es un literal
     */
    private boolean esLiteral() {
        return current() == TokenType.KW_NIL   ||
                current() == TokenType.KW_TRUE  ||
                current() == TokenType.KW_FALSE ||
                current() == TokenType.LIT_INT  ||
                current() == TokenType.LIT_STR;
    }
    /**
     * Verifica si el token actual puede iniciar un Primario (, self, id, idclass, new).
     *
     * @return true si el token actual es primero de Primario
     */
    private boolean esPrimeroPrimario() {
        return current() == TokenType.IPAREN     ||
                current() == TokenType.KW_SELF    ||
                current() == TokenType.ID_MET_AT  ||
                current() == TokenType.ID_CLASS   ||
                current() == TokenType.KW_NEW;
    }

    /**
     * Verifica si el token actual puede iniciar una Expresión.
     *
     * @return true si el token actual es primero de Expresión
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
    /**
     * Verifica si el token actual puede iniciar una Sentencia.
     *
     * @return true si el token actual es primero de Sentencia
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

    /**
     * Punto de entrada de la gramática. Parsea el programa completo.
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <program> ::= <Lista-Definiciones> <Start> -
     */
    // PRIMEROS: class, impl, start
    public void program() throws LexicalException, SyntacticException {
        listaDefiniciones();
        start();
        match(TokenType.EOF);
    }

    /**
     * Parsea cero o más definiciones de clase e implementación.
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <Lista-Definiciones> ::= <class> <Lista-Definiciones> | <Impl> <Lista-Definiciones> | Empty -
     * PRIMEROS: class, impl, lambda   SIGUIENTES: start
     */
    public void listaDefiniciones() throws LexicalException, SyntacticException {
        if (current() == TokenType.KW_CLASS) {
            classDef();
            listaDefiniciones();
        } else if (current() == TokenType.KW_IMPL) {
            implDef();
            listaDefiniciones();
        }
        // lambda: siguiente es start, salimos
    }

    /**
     * Parsea el bloque principal del programa.
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <Start> ::= start <Bloque-Metodo> -
     * PRIMEROS: start
     */
    public void start() throws LexicalException, SyntacticException{
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

    /**
     * Parsea la definición de una clase.
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <class> ::= class idclass <class-Fact> -
     * PRIMEROS: class
     */
    public void classDef() throws LexicalException, SyntacticException {
        match(TokenType.KW_CLASS);
        match(TokenType.ID_CLASS);
        classDefFact();
    }

    /**
     * Parsea el cuerpo de una clase, con o sin herencia.
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <class-Fact> ::= <Herencia> { <NAtributos> } | { <NAtributos> } -
     * PRIMEROS: : , {
     */
    public void classDefFact() throws LexicalException, SyntacticException {
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

    /**
     * Parsea la cláusula de herencia de una clase.
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <Herencia> ::= : <Tipo> -
     * PRIMEROS: :
     */
    public void herencia() throws LexicalException, SyntacticException {
        match(TokenType.DOSPUNTOS);
        tipo();
    }

    /**
     * Parsea un tipo (primitivo, referencia o arreglo).
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <Tipo> ::= <Tipo-Primitivo> | <Tipo-Referencia> | <Tipo-Arreglo> -
     */
    public void tipo() throws LexicalException, SyntacticException {
        if (esTipoPrimitivo()){
            tipoPrimitivo();
        } else if (current()==TokenType.ID_CLASS) {
            tipoReferencia();
        } else if (current()==TokenType.TYPE_ARRAY) {
            tipoArreglo();
        } else {
            throw new SyntacticException(
                    "Se esperaba un tipo (Str, Bool, Int, idclass, Array), se encontro " + current(),
                    currentToken.getLine(), currentToken.getColumn()
            );
        }
    }

    /**
     * Parsea un tipo primitivo (Str, Bool o Int).
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <Tipo-Primitivo> ::= Str | Bool | Int -
     */
    public void tipoPrimitivo() throws LexicalException, SyntacticException {
        if (current()==TokenType.TYPE_STR){
            match(TokenType.TYPE_STR);
        } else if (current()==TokenType.TYPE_BOOL) {
            match(TokenType.TYPE_BOOL);
        } else if (current()==TokenType.TYPE_INT) {
            match(TokenType.TYPE_INT);

        }//no hay error pq ya esta en tipo entonces si es lambda tira error ahi
    }

    /**
     * Parsea un tipo referencia (idclass).
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <Tipo-Referencia> ::= idclass -
     */
    public void tipoReferencia() throws LexicalException, SyntacticException {
        if (current()==TokenType.ID_CLASS){
            match(TokenType.ID_CLASS);
        }
    }

    /**
     * Parsea un tipo arreglo (Array seguido de tipo primitivo).
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <Tipo-Arreglo> ::= Array <Tipo-Primitivo> -
     */
    public void tipoArreglo() throws LexicalException, SyntacticException {
        if (current()==TokenType.TYPE_ARRAY){
            match(TokenType.TYPE_ARRAY);
            tipoPrimitivo();
        }
    }

    /**
     * Parsea una sentencia del lenguaje.
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <Sentencia> ::= ; | <Asignacion>; | <Sentencia-Simple>; | if (...) <Sentencia-Else-Fact>
     *                 | while (...) <Sentencia> | for (...) <Sentencia> | <Bloque> | ret <Expresion-Fact>; -
     */
    public void sentencia() throws LexicalException, SyntacticException {
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
        } else {
            throw new SyntacticException(
                    "Se esperaba una sentencia, se encontro " + current()
                            + " ('" + currentToken.getLexema() + "')",
                    currentToken.getLine(), currentToken.getColumn()
            );
        }
    }

    /**
     * Parsea una expresión opcional (puede ser vacía).
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <Expresion-Fact> ::= <Expresion> | Empty -
     */
    public void expresionFact() throws LexicalException, SyntacticException {
        if(esPrimeroExpresion()) {
            expresion();
        }//no hay error en el else pq acepta lambda
    }

    /**
     * Parsea la rama else opcional de un if.
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <Sentencia-Else-Fact> ::= else <Sentencia> | Empty -
     */
    public void sentenciaElseFact() throws LexicalException, SyntacticException {
        if(current()==TokenType.KW_ELSE){
            match(TokenType.KW_ELSE);
            sentencia();
        }//no hay error pq hacepta lambda
    }

    /**
     * Parsea un bloque de sentencias entre llaves.
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <Bloque> ::= { <NSentencia> } -
     */
    public void bloque() throws LexicalException, SyntacticException {
        match(TokenType.ILLAVE);
        nSentencia();
        match(TokenType.DLLAVE);
    }

    /**
     * Parsea una asignación (a variable simple o a self).
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <Asignacion> ::= <AccesoVar-Simple> = <Expresion> | <AccesoSelf-Simple> = <Expresion> -
     */
    public void asignacion() throws LexicalException, SyntacticException {
        if (current()==TokenType.ID_MET_AT){
            accesoVarSimple();
            match(TokenType.ASIGN);
            expresion();
        }else if(current()==TokenType.KW_SELF){
            accesoSelfSimple();
            match(TokenType.ASIGN);
            expresion();
        } else {
            throw new SyntacticException(
                    "Se esperaba id o self para una asignacion, se encontro " + current()
                            + " ('" + currentToken.getLexema() + "')",
                    currentToken.getLine(), currentToken.getColumn()
            );
        }
    }

    /**
     * Parsea el acceso a una variable simple (id seguido de encadenado o índice).
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <AccesoVar-Simple> ::= id <AccesoVar-Simple-Fact> -
     */
    public void accesoVarSimple() throws LexicalException, SyntacticException {
        match(TokenType.ID_MET_AT);
        accesoVarSimpleFact();
    }

    /**
     * Parsea el sufijo de un acceso a variable simple (encadenado o acceso por índice).
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <AccesoVar-Simple-Fact> ::= <NEncadenado-Simple> | [ <Expresion> ] -
     * PRIMEROS: . [ lambda   SIGUIENTES: =
     */
    public void accesoVarSimpleFact() throws LexicalException, SyntacticException {
        if (current() == TokenType.ICORCHETE) {
            match(TokenType.ICORCHETE);
            expresion();
            match(TokenType.DCORCHETE);
        } else {
            nEncadenadoSimple();
        }
    }

    /**
     * Parsea un acceso simple a través de self.
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <AccesoSelf-Simple> ::= self <NEncadenado-Simple> -
     */
    public void accesoSelfSimple() throws LexicalException, SyntacticException {
        match(TokenType.KW_SELF);
        nEncadenadoSimple();
    }

    /**
     * Parsea cero o más encadenados simples.
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <NEncadenado-Simple> ::= <Encadenado-Simple> <NEncadenado-Simple> | Empty -
     * PRIMEROS: . lambda   SIGUIENTES: =
     */
    public void nEncadenadoSimple() throws LexicalException, SyntacticException {
        if (current() == TokenType.PUNTO) {
            encadenadoSimple();
            nEncadenadoSimple();
        }
        // lambda
    }

    /**
     * Parsea un encadenado simple (punto seguido de id).
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <Encadenado-Simple> ::= . id -
     */
    public void encadenadoSimple() throws LexicalException, SyntacticException {
        match(TokenType.PUNTO);
        match(TokenType.ID_MET_AT);
    }

    /**
     * Parsea una sentencia simple (expresión entre paréntesis).
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <Sentencia-Simple> ::= ( <Expresion> ) -
     */
    public void sentenciaSimple() throws LexicalException, SyntacticException {
        match(TokenType.IPAREN);
        expresion();
        match(TokenType.DPAREN);
    }

    ///EXPRESIONES

    /**
     * Parsea una expresión completa.
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <Expresion> ::= <ExpOr> -
     */
    public void expresion() throws LexicalException, SyntacticException {
        expOr();
    }

    /**
     * Parsea una expresión OR.
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <ExpOr> ::= <ExpAnd> <ExpOrRec> -
     */
    public void expOr() throws LexicalException, SyntacticException {
        expAnd();
        expOrRec();
    }

    /**
     * Parsea la parte recursiva de una expresión OR.
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <ExpOrRec> ::= || <ExpAnd> <ExpOrRec> | Empty -
     * PRIMEROS: || lambda   SIGUIENTES: ) ; ] ,
     */
    public void expOrRec() throws LexicalException, SyntacticException {
        if (current() == TokenType.OP_OR) {
            match(TokenType.OP_OR);
            expAnd();
            expOrRec();
        }
        // lambda
    }

    /**
     * Parsea una expresión AND.
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <ExpAnd> ::= <ExpIgual> <ExpAndRec> -
     */
    public void expAnd() throws LexicalException, SyntacticException {
        expIgual();
        expAndRec();
    }

    /**
     * Parsea la parte recursiva de una expresión AND.
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <ExpAndRec> ::= && <ExpIgual> <ExpAndRec> | Empty -
     * PRIMEROS: && lambda   SIGUIENTES: ) ; ] , ||
     */
    public void expAndRec() throws LexicalException, SyntacticException {
        if (current() == TokenType.OP_AND) {
            match(TokenType.OP_AND);
            expIgual();
            expAndRec();
        }
        // lambda
    }

    /**
     * Parsea una expresión de igualdad.
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <ExpIgual> ::= <ExpCompuesta> <ExpIgualRec> -
     */
    public void expIgual() throws LexicalException, SyntacticException {
        expCompuesta();
        expIgualRec();
    }

    /**
     * Parsea la parte recursiva de una expresión de igualdad.
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <ExpIgualRec> ::= <OpIgual> <ExpCompuesta> <ExpIgualRec> | Empty -
     * PRIMEROS: == != lambda   SIGUIENTES: ) ; ] , || &&
     */
    public void expIgualRec() throws LexicalException, SyntacticException {
        if (current() == TokenType.OP_EQUAL || current() == TokenType.OP_NOT_EQUAL) {
            opIgual();
            expCompuesta();
            expIgualRec();
        }
        // lambda
    }

    /**
     * Parsea una expresión compuesta (comparación relacional opcional).
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <ExpCompuesta> ::= <ExpAd> <OpCompuesto> <ExpAd> | <ExpAd> -
     * PRIMEROS: +,-,!,++,--,nil,true,false,intLit,StrLit,(,self,id,idclass,new
     */
    public void expCompuesta() throws LexicalException, SyntacticException {
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

    /**
     * Parsea una expresión aditiva.
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <ExpAd> ::= <ExpMul> <ExpAdRec> -
     * PRIMEROS: +,-,!,++,--,nil,true,false,intLit,StrLit,(,self,id,idclass,new
     */
    public void expAd() throws LexicalException, SyntacticException {
        expMul();
        expAdRec();
    }

    /**
     * Parsea la parte recursiva de una expresión aditiva.
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <ExpAdRec> ::= <OpAd> <ExpMul> <ExpAdRec> | Empty -
     * PRIMEROS: + - lambda
     * SIGUIENTES: < > <= >= ) ; ] , || && == !=
     */
    public void expAdRec() throws LexicalException, SyntacticException {
        if (current() == TokenType.OP_SUM || current() == TokenType.OP_REST) {
            opAd();
            expMul();
            expAdRec();
        }
        // lambda
    }

    /**
     * Parsea una expresión multiplicativa.
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <ExpMul> ::= <ExpUn> <ExpMulRec> -
     * PRIMEROS: +,-,!,++,--,nil,true,false,intLit,StrLit,(,self,id,idclass,new
     */
    public void expMul() throws LexicalException, SyntacticException {
        expUn();
        expMulRec();
    }

    /**
     * Parsea la parte recursiva de una expresión multiplicativa.
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <ExpMulRec> ::= <OpMul> <ExpUn> <ExpMulRec> | Empty -
     * PRIMEROS: * / lambda
     * SIGUIENTES: < > <= >= ) ; ] , || && == !=
     */
    public void expMulRec() throws LexicalException, SyntacticException {
        if (current() == TokenType.OP_MULT || current() == TokenType.OP_DIV) {
            opMul();
            expUn();
            expMulRec();
        }
        // lambda
    }

    /**
     * Parsea una expresión unaria u operando.
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <ExpUn> ::= <OpUnario> <ExpUn> | <Operando> -
     * PRIMEROS: +,-,!,++,--,nil,true,false,intLit,StrLit,(,self,id,idclass,new
     */
    public void expUn() throws LexicalException, SyntacticException {
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
                    "Se esperaba expresion unaria u operando, se encontro " + current()
                            + " ('" + currentToken.getLexema() + "')",
                    currentToken.getLine(), currentToken.getColumn()
            );
        }
    }


    //OPERADORES!!!


    /**
     * Parsea un operador de igualdad (== o !=).
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <OpIgual> ::= == | != -
     */
    public void opIgual() throws LexicalException, SyntacticException {
        if (current() == TokenType.OP_EQUAL) {
            match(TokenType.OP_EQUAL);
        } else if (current() == TokenType.OP_NOT_EQUAL) {
            match(TokenType.OP_NOT_EQUAL);
        } else {
            throw new SyntacticException(
                    "Se esperaba == o !=, se encontro " + current()
                            + " ('" + currentToken.getLexema() + "')",
                    currentToken.getLine(), currentToken.getColumn()
            );
        }
    }

    /**
     * Parsea un operador compuesto de comparación (< > <= >=).
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <OpCompuesto> ::= < | > | <= | >= -
     */
    public void opCompuesto() throws LexicalException, SyntacticException {
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
                    "Se esperaba operador compuesto (<, >, <=, >=), se encontro " + current()
                            + " ('" + currentToken.getLexema() + "')",
                    currentToken.getLine(), currentToken.getColumn()
            );
        }
    }

    /**
     * Parsea un operador aditivo (+ o -).
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <OpAd> ::= + | - -
     */
    public void opAd() throws LexicalException, SyntacticException {
        if (current() == TokenType.OP_SUM) {
            match(TokenType.OP_SUM);
        } else if (current() == TokenType.OP_REST) {
            match(TokenType.OP_REST);
        } else {
            throw new SyntacticException(
                    "Se esperaba + o -, se encontro " + current()
                            + " ('" + currentToken.getLexema() + "')",
                    currentToken.getLine(), currentToken.getColumn()
            );
        }
    }

    /**
     * Parsea un operador unario (+, -, !, ++, --).
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <OpUnario> ::= + | - | ! | ++ | -- -
     */
    public void opUnario() throws LexicalException, SyntacticException {
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
                    "Se esperaba operador unario (+, -, !, ++, --), se encontro " + current()
                            + " ('" + currentToken.getLexema() + "')",
                    currentToken.getLine(), currentToken.getColumn()
            );
        }
    }

    /**
     * Parsea un operador multiplicativo (* o /).
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <OpMul> ::= * | / -
     */
    public void opMul()  throws LexicalException, SyntacticException{
        if (current() == TokenType.OP_MULT) {
            match(TokenType.OP_MULT);
        } else if (current() == TokenType.OP_DIV) {
            match(TokenType.OP_DIV);
        } else {
            throw new SyntacticException(
                    "Se esperaba * o /, se encontro " + current()
                            + " ('" + currentToken.getLexema() + "')",
                    currentToken.getLine(), currentToken.getColumn()
            );
        }
    }

    /**
     * Parsea un operando (literal o primario con encadenado opcional).
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <Operando> ::= <Literal> | <Primario> <Encadenado-Empty> -
     */
    public void operando() throws LexicalException, SyntacticException {
        if (esLiteral()) {
            literal();
        } else if (esPrimeroPrimario()) {
            primario();
            encadenadoEmpty();
        } else {
            throw new SyntacticException(
                    "Se esperaba un operando, se encontro " + current()
                            + " ('" + currentToken.getLexema() + "')",
                    currentToken.getLine(), currentToken.getColumn()
            );
        }
    }

    /**
     * Parsea un literal (nil, true, false, intLiteral, StrLiteral).
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <Literal> ::= nil | true | false | intLiteral | StrLiteral -
     */
    public void literal() throws LexicalException, SyntacticException{
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
                    "Se esperaba un literal, se encontro " + current()
                            + " ('" + currentToken.getLexema() + "')",
                    currentToken.getLine(), currentToken.getColumn()
            );
        }
    }

    /**
     * Parsea un primario: expresión parentizada, acceso a self, acceso a variable,
     * llamada a método, llamada a método estático o llamada a constructor.
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <Primario> ::= <ExpresionParentizada> | <AccesoSelf> | <AccesoVar>
     *                | <Llamada-Metodo> | <Llamada-Metodo-Estatico> | <Llamada-Constructor> -
     * PRIMEROS: (, self, id, idclass, new
     */
    public void primario() throws LexicalException, SyntacticException {
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
            accesoVarOLlamadaMetodo();
        } else {
            throw new SyntacticException(
                    "Se esperaba primario, se encontro " + current()
                            + " ('" + currentToken.getLexema() + "')",
                    currentToken.getLine(), currentToken.getColumn()
            );
        }
    }

    /**
     * Parsea un id que puede ser acceso a variable o llamada a método según el token siguiente.
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <AccesoVar> ::= id <AccesoVar-Fact> | <Llamada-Metodo> ::= id <Argumentos-Actuales> <Encadenado-Empty> -
     */
    public void accesoVarOLlamadaMetodo() throws LexicalException, SyntacticException {
        match(TokenType.ID_MET_AT);
        if (current() == TokenType.IPAREN) {
            // <Llamada-Metodo> -> id <Argumentos-Actuales> <Encadenado-Empty>
            argumentosActuales();
            encadenadoEmpty();
        } else if (current()==TokenType.PUNTO || current()==TokenType.ICORCHETE) {
            accesoVarFact();
        } else {
            throw new SyntacticException(
                    "Se esperaba '(', '.' o '[' despues de id, se encontro " + current()
                            + " ('" + currentToken.getLexema() + "')",
                    currentToken.getLine(), currentToken.getColumn()
            );
        }
    }


    /**
     * Parsea una expresión entre paréntesis seguida de un encadenado opcional.
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <ExpresionParentizada> ::= ( <Expresion> ) <Encadenado-Empty> -
     */
    public void expresionParentizada() throws LexicalException, SyntacticException {
        match(TokenType.IPAREN);
        expresion();
        match(TokenType.DPAREN);
        encadenadoEmpty();
    }

    /**
     * Parsea un acceso a self seguido de un encadenado opcional.
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <AccesoSelf> ::= self <Encadenado-Empty> -
     */
    public void accesoSelf() throws LexicalException, SyntacticException {
        match(TokenType.KW_SELF);
        encadenadoEmpty();
    }

    /**
     * Parsea el sufijo de un acceso a variable (índice o encadenado).
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <AccesoVar-Fact> ::= <Encadenado> | [ <Expresion> ] <Encadenado-Empty> -
     */
    public void accesoVarFact() throws LexicalException, SyntacticException {
        if (current() == TokenType.ICORCHETE) {
            // produccion: [ <Expresion> ] <Encadenado-Empty>
            match(TokenType.ICORCHETE);
            expresion();
            match(TokenType.DCORCHETE);
            encadenadoEmpty();
        } else if (current() == TokenType.PUNTO) {
            // produccion: <Encadenado>
            encadenado();
        } else {
            throw new SyntacticException(
                    "Se esperaba '.' o '[' en acceso a variable, se encontro " + current()
                            + " ('" + currentToken.getLexema() + "')",
                    currentToken.getLine(), currentToken.getColumn()
            );
        }
    }

    /**
     * Parsea una llamada a método estático (idclass . método).
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <Llamada-Metodo-Estatico> ::= idclass . <Llamada-Metodo> <Encadenado-Empty> -
     */
    public void llamadaMetodoEstatico() throws LexicalException, SyntacticException {
        match(TokenType.ID_CLASS);
        match(TokenType.PUNTO);
        llamadaMetodo();
        encadenadoEmpty();
    }

    /**
     * Parsea una llamada a método (id seguido de argumentos actuales y encadenado opcional).
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <Llamada-Metodo> ::= id <Argumentos-Actuales> <Encadenado-Empty> -
     */
    public void llamadaMetodo()  throws LexicalException, SyntacticException{
        match(TokenType.ID_MET_AT);
        argumentosActuales();
        encadenadoEmpty();
    }

    /**
     * Parsea una llamada a constructor (new seguido del tipo o clase).
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <Llamada-Constructor> ::= new <Llamada-Constructor-Fact> -
     */
    public void llamadaConstructor() throws LexicalException, SyntacticException {
        match(TokenType.KW_NEW);
        llamadaConstructorFact();
    }

    /**
     * Parsea el tipo instanciado en una llamada a constructor.
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <Llamada-Constructor-Fact> ::= <Tipo-Primitivo> [ <Expresion> ] | idclass <Argumentos-Actuales> <Encadenado-Empty> -
     * PRIMEROS: Str, Bool, Int, idclass
     */
    public void llamadaConstructorFact() throws LexicalException, SyntacticException {
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
                    "Se esperaba idclass o tipo primitivo despues de 'new', se encontro " + current()
                            + " ('" + currentToken.getLexema() + "')",
                    currentToken.getLine(), currentToken.getColumn()
            );
        }
    }

    /**
     * Parsea los argumentos actuales de una llamada (lista de expresiones entre paréntesis).
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <Argumentos-Actuales> ::= ( <Lista-Expresiones> ) | () -
     */
    public void argumentosActuales() throws LexicalException, SyntacticException {
        match(TokenType.IPAREN);
        if (esPrimeroExpresion()) {
            listaExpresiones();
        }
        match(TokenType.DPAREN);
    }

    /**
     * Parsea una lista de expresiones separadas por coma.
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <Lista-Expresiones> ::= <Expresion> <Lista-Expresiones-Fact> -
     */
    public void listaExpresiones()  throws LexicalException, SyntacticException{
        expresion();
        listaExpresionesFact();
    }

    /**
     * Parsea el resto opcional de una lista de expresiones.
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <Lista-Expresiones-Fact> ::= , <Lista-Expresiones> | Empty -
     * PRIMEROS: , lambda   SIGUIENTES: )
     */
    public void listaExpresionesFact() throws LexicalException, SyntacticException {
        if (current() == TokenType.COMA) {
            match(TokenType.COMA);
            listaExpresiones();
        }
        // lambda
    }

    /**
     * Parsea un encadenado (punto seguido de su contenido).
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <Encadenado> ::= . <Encadenado-Fact> -
     */
    public void encadenado() throws LexicalException, SyntacticException {
        match(TokenType.PUNTO);
        encadenadoFact();
    }

    /**
     * Parsea el contenido de un encadenado: llamada a método encadenado o acceso a variable.
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <Encadenado-Fact> ::= <Llamada-Metodo-Encadenado> | <AccesoVar> -
     */
    // Ambos empiezan con id entonces aca hicimos dos reglas: EncadenadoFact y LlamadaMetodoEncadenado
    public void encadenadoFact() throws LexicalException, SyntacticException {
        if (current() == TokenType.ID_MET_AT) {
            match(TokenType.ID_MET_AT);
            //Por LlamadaMetodoEncadenado debemos esperar un (
            if (current()==TokenType.IPAREN){
                argumentosActuales();
                encadenadoEmpty();
            //Por AccesoVar esperamos un . o [
            } else if (current()==TokenType.ICORCHETE ||current()==TokenType.PUNTO ) {
                accesoVarFact();
            } else {
                throw new SyntacticException(
                        "Se esperaba '(', '[' o '.' despues de id en encadenado, se encontro " + current()
                                + " ('" + currentToken.getLexema() + "')",
                        currentToken.getLine(), currentToken.getColumn()
                );
            }
        } else {
            throw new SyntacticException(
                    "Se esperaba id despues de '.', se encontro " + current()
                            + " ('" + currentToken.getLexema() + "')",
                    currentToken.getLine(), currentToken.getColumn()
            );
        }
    }

    /**
     * Parsea un encadenado opcional (puede ser vacío).
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <Encadenado-Empty> ::= <Encadenado> | Empty -
     * PRIMEROS: . lambda
     */
    public void encadenadoEmpty() throws LexicalException, SyntacticException {
        if (current() == TokenType.PUNTO) {
            encadenado();
        }
        // lambda
    }

    /**
     * Parsea una implementación de clase (impl).
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <Impl> ::= impl idclass { <NMiembro> } -
     * PRIMEROS: impl
     */
    public void implDef() throws LexicalException, SyntacticException {
        match(TokenType.KW_IMPL);
        match(TokenType.ID_CLASS);
        match(TokenType.ILLAVE);
        nMiembro();
        match(TokenType.DLLAVE);
    }

    /**
     * Parsea cero o más miembros de una implementación.
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <NMiembro> ::= <Miembro> <NMiembro> | Empty -
     * PRIMEROS: fn, st, . , lambda   SIGUIENTES: }
     */
    public void nMiembro() throws LexicalException, SyntacticException {
        if (current() == TokenType.KW_FN  || current() == TokenType.KW_ST  || current() == TokenType.PUNTO) {
            miembro();
            nMiembro();
        }
        // lambda: siguiente es }, salimos
    }

    /**
     * Parsea cero o más atributos de una clase.
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <NAtributos> ::= <Atributo> <NAtributos> | Empty -
     * PRIMEROS: Str, Bool, Int, idclass, Array, pub, lambda   SIGUIENTES: }
     */
    public void nAtributos()  throws LexicalException, SyntacticException{
        if (esTipo() || current() == TokenType.KW_PUB) {
            atributo();
            nAtributos();
        }
        // lambda: siguiente es }, salimos
    }

    /**
     * Parsea un miembro de una implementación (método o constructor).
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <Miembro> ::= <Metodo> | <Constructor> -
     */
    public void miembro()  throws LexicalException, SyntacticException{
        if (current() == TokenType.KW_FN || current() == TokenType.KW_ST) {
            metodo();

        }else if (current() == TokenType.PUNTO) {
            constructor();
        } else {
            throw new SyntacticException(
                    "Se esperaba un miembro (fn, st o '.'), se encontro " + current()
                            + " ('" + currentToken.getLexema() + "')",
                    currentToken.getLine(), currentToken.getColumn()
            );
        }
    }

    /**
     * Parsea la definición de un método (con o sin forma estática).
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <Metodo> ::= fn <Tipo-Metodo-Fact> idMetAt <Argumentos-Formales> <Bloque-Metodo>
     *              | <Forma-Metodo> fn <Tipo-Metodo-Fact> idMetAt <Argumentos-Formales> <Bloque-Metodo> -
     */
    public void metodo() throws LexicalException, SyntacticException {
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
            } else {
                throw new SyntacticException(
                        "Se esperaba fn o st para definir un metodo, se encontro " + current()
                                + " ('" + currentToken.getLexema() + "')",
                        currentToken.getLine(), currentToken.getColumn()
                );
            }
    }

    /**
     * Parsea el tipo de retorno de un método si existe (puede ser vacío).
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <Tipo-Metodo-Fact> ::= <Tipo-Metodo> | Empty -
     */
    public void tipoMetodoFact() throws LexicalException, SyntacticException {
        if (esTipo()||current()==TokenType.TYPE_VOID){
            tipoMetodo();
        }//tipo metodoFact produce lambda por eso no hay exception
    }

    /**
     * Parsea la visibilidad pública de un atributo.
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <Visibilidad> ::= pub -
     */
    public void visibilidad() throws LexicalException, SyntacticException {
        match(TokenType.KW_PUB); //la exeption la maneja el match
    }

    /**
     * Parsea la forma estática de un método.
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <Forma-Metodo> ::= st -
     */
    public void formaMetodo() throws LexicalException, SyntacticException {
        match(TokenType.KW_ST); //la exeption la maneja el match
    }

    /**
     * Parsea cero o más sentencias.
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <NSentencia> ::= <Sentencia> <NSentencia> | Empty -
     */
    public void nSentencia() throws LexicalException, SyntacticException {
        if(esPrimeroSentencia()){
            sentencia();
            nSentencia();
        }//sin exception por lambda
    }

    /**
     * Parsea el bloque de un método (declaraciones locales y sentencias).
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <Bloque-Metodo> ::= { <NDecl-Var-Loc> <NSentencia> } -
     */
    public void bloqueMetodo() throws LexicalException, SyntacticException {
        match(TokenType.ILLAVE);
        nDeclaracionVarLocales();
        nSentencia();
        match(TokenType.DLLAVE);
    }

    /**
     * Parsea cero o más declaraciones de variables locales.
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <NDecl-Var-Loc> ::= <Decl-Var-Locales> <NDecl-Var-Loc> | Empty -
     */
    public void nDeclaracionVarLocales() throws LexicalException, SyntacticException {
        if (esTipo()){
            declaracionVarLocal();
            nDeclaracionVarLocales();
        }
    }

    /**
     * Parsea una declaración de variable local.
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <Decl-Var-Locales> ::= <Tipo> <Lista-Declaracion-Variables> ; -
     */
    public void declaracionVarLocal() throws LexicalException, SyntacticException {
        if(esTipo()){
            tipo();
            listaDeclaracionVariables();
            match(TokenType.PUNTOCOMA);
        }
    }

    /**
     * Parsea una lista de variables declaradas (al menos un id).
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <Lista-Declaracion-Variables> ::= idMetAt <Lista-Declaracion-Variables-Fact> -
     */
    public void listaDeclaracionVariables() throws LexicalException, SyntacticException {
        match(TokenType.ID_MET_AT);
        listaDeclaracionVariablesFact();
    }

    /**
     * Parsea el resto opcional de una lista de declaraciones de variables.
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <Lista-Declaracion-Variables-Fact> ::= , <Lista-Declaracion-Variables> | Empty -
     */
    public void listaDeclaracionVariablesFact() throws LexicalException, SyntacticException {
        if (current()==TokenType.COMA){
            match(TokenType.COMA);
            listaDeclaracionVariables();
        }

    }

    /**
     * Parsea los argumentos formales de un método entre paréntesis.
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <Argumentos-Formales> ::= ( <Lista-Argumentos-Formales> ) | () -
     */
    public void argumentosFormales() throws LexicalException, SyntacticException {
        match(TokenType.IPAREN);
        //<Lista-Argumentos-Formales> ::= <Argumento-Formal> <Lista-Argumentos-Formales-Fact>
        //<Argumento-Formal> ::= <Tipo> idMetAt
        if (esTipo()){ //por los primeros de argumento formal q son los primeros de tipo
            listaArgumentosFormales();
        }
        match(TokenType.DPAREN);
    }

    /**
     * Parsea una lista de argumentos formales.
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <Lista-Argumentos-Formales> ::= <Argumento-Formal> <Lista-Argumentos-Formales-Fact> -
     */
    public void listaArgumentosFormales() throws LexicalException, SyntacticException {
        if(esTipo()){
            argumentoFormal();
            listaArgumentosFormalesFact();
        } else {
            throw new SyntacticException(
                    "Se esperaba un tipo para el argumento formal, se encontro " + current()
                            + " ('" + currentToken.getLexema() + "')",
                    currentToken.getLine(), currentToken.getColumn()
            );
        }
    }

    /**
     * Parsea el resto opcional de una lista de argumentos formales.
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <Lista-Argumentos-Formales-Fact> ::= , <Lista-Argumentos-Formales> | Empty -
     */
    public void listaArgumentosFormalesFact() throws LexicalException, SyntacticException {
        if(current()==TokenType.COMA){
            match(TokenType.COMA);
            listaArgumentosFormales();
        }
    }//sin exception por lambda


    /**
     * Parsea un argumento formal (tipo seguido de id).
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <Argumento-Formal> ::= <Tipo> idMetAt -
     */
    public void argumentoFormal() throws LexicalException, SyntacticException {
        tipo();
        match(TokenType.ID_MET_AT);
    }

    /**
     * Parsea el tipo de retorno de un método (tipo o void).
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <Tipo-Metodo> ::= <Tipo> | void -
     */
    public void tipoMetodo() throws LexicalException, SyntacticException {
        if (esTipo()){
            tipo();
        } else if (current()==TokenType.TYPE_VOID) {
            match(TokenType.TYPE_VOID);
        } else {
            throw new SyntacticException(
                    "Se esperaba un tipo de retorno o void, se encontro " + current()
                            + " ('" + currentToken.getLexema() + "')",
                    currentToken.getLine(), currentToken.getColumn()
            );
        }
    }

    /**
     * Parsea un atributo de clase (con o sin visibilidad pública).
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <Atributo> ::= <Tipo> <Lista-Declaracion-Variables> ; | <Visibilidad> <Tipo> <Lista-Declaracion-Variables> ; -
     */
    public void atributo() throws LexicalException, SyntacticException {
        if (esTipo()) {
            tipo();
            listaDeclaracionVariables();
            match(TokenType.PUNTOCOMA);
        } else if (current() == TokenType.KW_PUB) {
            visibilidad();
            tipo();
            listaDeclaracionVariables();
            match(TokenType.PUNTOCOMA);

        } else {
            throw new SyntacticException(
                    "Se esperaba un atributo (tipo o pub), se encontro " + current()
                            + " ('" + currentToken.getLexema() + "')",
                    currentToken.getLine(), currentToken.getColumn()
            );
        }
    }

    /**
     * Parsea el constructor de una clase (punto seguido de argumentos y bloque).
     *
     * @throws LexicalException   Excepción ocasionada por un error léxico
     * @throws SyntacticException Excepción ocasionada por un error sintáctico
     * - <Constructor> ::= . <Argumentos-Formales> <Bloque-Metodo> -
     */
    public void constructor() throws LexicalException, SyntacticException {
        match(TokenType.PUNTO);
        argumentosFormales();
        bloqueMetodo();
    }

}

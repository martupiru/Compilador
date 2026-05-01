package org.compilador.lexer.lexicalAnalyzer;

import org.compilador.exception.LexicalException;
import org.compilador.lexer.token.Token;
import org.compilador.lexer.token.TokenType;

import java.util.HashMap;
import java.util.Map;

public class LexicalAnalyzer {
    private static final Map<String, TokenType> KEYWORDS = new HashMap<>();

    static {
        KEYWORDS.put("class",  TokenType.KW_CLASS);
        KEYWORDS.put("impl",   TokenType.KW_IMPL);
        KEYWORDS.put("if",     TokenType.KW_IF);
        KEYWORDS.put("else",   TokenType.KW_ELSE);
        KEYWORDS.put("while",  TokenType.KW_WHILE);
        KEYWORDS.put("for",    TokenType.KW_FOR);
        KEYWORDS.put("in",     TokenType.KW_IN);
        KEYWORDS.put("ret",    TokenType.KW_RET);
        KEYWORDS.put("new",    TokenType.KW_NEW);
        KEYWORDS.put("fn",     TokenType.KW_FN);
        KEYWORDS.put("st",     TokenType.KW_ST);
        KEYWORDS.put("pub",    TokenType.KW_PUB);
        KEYWORDS.put("self",   TokenType.KW_SELF);
        KEYWORDS.put("true",   TokenType.KW_TRUE);
        KEYWORDS.put("false",  TokenType.KW_FALSE);
        KEYWORDS.put("nil",    TokenType.KW_NIL);
        KEYWORDS.put("div",    TokenType.KW_DIV);
        // TIPOS PRIMITIVOS
        KEYWORDS.put("Int",   TokenType.TYPE_INT);
        KEYWORDS.put("Str",   TokenType.TYPE_STR);
        KEYWORDS.put("Bool",  TokenType.TYPE_BOOL);
        //TIPOS ESPECIALES
        KEYWORDS.put("void",  TokenType.TYPE_VOID);
        KEYWORDS.put("Array", TokenType.TYPE_ARRAY);
    }

    private final String source;
    private int actual_pos;
    private int actual_line;
    private int actual_colum;
    //para no tener columnas en negativo
    private int lastValidLine   = 1;
    private int lastValidColumn = 1;

    public LexicalAnalyzer(String source) {
        this.source = source; //archivo
        this.actual_pos    = 0;
        this.actual_line   = 1;
        this.actual_colum = 1;
    }
    public Token nextToken() {
        skipWhitespaceAndComments();

        if (isEOF()) {
            return new Token(TokenType.EOF, "EOF", lastValidLine, lastValidColumn);
        }

        char c = peek();

        // --- Identificadores y palabras reservadas ---
        if (Character.isLetter(c) || c == '_') {
            return readIdentifierOrKeyword();
        }

        // --- Literales enteros ---
        if (Character.isDigit(c)) {
            return readIntLiteral();
        }

        // --- Literales cadena ---
        if (c == '"') {
            return readStringLiteral();
        }

        // --- Operadores y puntuacion ---
        return readOperatorOrPunctuation();
    }
    //lectura tokens
    private Token readIdentifierOrKeyword() {
        int startLine   = actual_line;
        int startColumn = actual_colum;
        StringBuilder sb = new StringBuilder();

        while (!isEOF() && (Character.isLetterOrDigit(peek()) || peek() == '_')) {
            sb.append(advance());
        }

        String word = sb.toString();

        // Buscar en tabla de keywords
        TokenType type = KEYWORDS.get(word);
        if (type != null) {
            return new Token(type, word, startLine, startColumn);
        }

        // Identificador especial: start
        if (word.equals("start")) {
            return new Token(TokenType.ID_START, word, startLine, startColumn);
        }

        // Identificador de clase (empieza con Mayuscula)
        if (Character.isUpperCase(word.charAt(0))) {
            return new Token(TokenType.ID_CLASS, word, startLine, startColumn);
        }
        // Identificador de metodo/atributo (empieza con minuscula)
        return new Token(TokenType.ID_MET_AT, word, startLine, startColumn);
    }

    private Token readIntLiteral() {
        int startLine   = actual_line;
        int startColumn = actual_colum;
        StringBuilder sb = new StringBuilder();

        while (!isEOF() && Character.isDigit(peek())) {
            sb.append(advance());
        }

        return new Token(TokenType.LIT_INT, sb.toString(), startLine, startColumn);
    }
    private Token readStringLiteral() {
        int startLine   = actual_line;
        int startColumn = actual_colum;
        StringBuilder sb = new StringBuilder();

        advance(); // consumir la " inicial
        sb.append('"');

        while (!isEOF() && peek() != '"') {
            char c = peek();

            if (c == '\n' || c == '\r') {
                throw new LexicalException(
                        "CADENA SIN CERRAR: salto de linea dentro de una cadena",
                        actual_line, actual_colum);
            }

            if (c == '\\') {
                // Secuencia de escape
                advance(); // consumir '\'
                if (isEOF()) {
                    throw new LexicalException(
                            "CADENA SIN CERRAR: EOF dentro de una cadena",
                            actual_line, actual_colum);
                }
                char esc = advance();
                switch (esc) {
                    case 'n':  sb.append("\\n");  break;
                    case 't':  sb.append("\\t");  break;
                    case 'r':  sb.append("\\r");  break;
                    case '\\': sb.append("\\\\"); break;
                    case '"':  sb.append("\\\""); break;
                    case '\'': sb.append("\\'");  break;
                    default:
                        throw new LexicalException(
                                "SECUENCIA DE ESCAPE NO VALIDA: \\" + esc,
                                actual_line, actual_colum);
                }
            } else {
                sb.append(advance());
            }

            if (sb.length() > 1026) { // 1024 chars + las dos comillas
                throw new LexicalException(
                        "CADENA DEMASIADO LARGA: supera 1024 caracteres",
                        startLine, startColumn);
            }
        }

        if (isEOF()) {
            throw new LexicalException(
                    "CADENA SIN CERRAR: se llego a EOF sin encontrar comilla de cierre",
                    startLine, startColumn);
        }

        advance(); // consumir la " final
        sb.append('"');

        return new Token(TokenType.LIT_STR, sb.toString(), startLine, startColumn);
    }

    private Token readOperatorOrPunctuation() {
        int startLine   = actual_line;
        int startColumn = actual_colum;
        char c = advance();

        switch (c) {
            case '(': return makeToken(TokenType.IPAREN, "(");
            case ')': return makeToken(TokenType.DPAREN,   ")");
            case '{': return makeToken(TokenType.ILLAVE,   "{");
            case '}': return makeToken(TokenType.DLLAVE,   "}");
            case '[': return makeToken(TokenType.ICORCHETE, "[");
            case ']': return makeToken(TokenType.DCORCHETE, "]");
            case ';': return makeToken(TokenType.PUNTOCOMA, ";");
            case ',': return makeToken(TokenType.COMA,     ",");
            case '.': return makeToken(TokenType.PUNTO,       ".");
            case ':': return makeToken(TokenType.DOSPUNTOS,     ":");

            case '+':
                if (!isEOF() && peek() == '+') { advance(); return new Token(TokenType.OP_INC, "++", startLine, startColumn); }
                return new Token(TokenType.OP_SUM,  "+", startLine, startColumn);

            case '-':
                if (!isEOF() && peek() == '-') { advance(); return new Token(TokenType.OP_DEC, "--", startLine, startColumn); }
                return new Token(TokenType.OP_REST, "-", startLine, startColumn);

            case '*': return new Token(TokenType.OP_MULT, "*", startLine, startColumn);
            case '/': return new Token(TokenType.OP_DIV,  "/", startLine, startColumn);

            case '<':
                if (!isEOF() && peek() == '=') { advance(); return new Token(TokenType.OP_LESS_EQ,    "<=", startLine, startColumn); }
                return new Token(TokenType.OP_LESS, "<", startLine, startColumn);

            case '>':
                if (!isEOF() && peek() == '=') { advance(); return new Token(TokenType.OP_GREATER_EQ, ">=", startLine, startColumn); }
                return new Token(TokenType.OP_GREATER, ">", startLine, startColumn);

            case '=':
                if (!isEOF() && peek() == '=') { advance(); return new Token(TokenType.OP_EQUAL,  "==", startLine, startColumn); }
                return new Token(TokenType.ASIGN, "=", startLine, startColumn);

            case '!':
                if (!isEOF() && peek() == '=') { advance(); return new Token(TokenType.OP_NOT_EQUAL, "!=", startLine, startColumn); }
                return new Token(TokenType.OP_NOT, "!", startLine, startColumn);

            case '&':
                if (!isEOF() && peek() == '&') { advance(); return new Token(TokenType.OP_AND, "&&", startLine, startColumn); }
                throw new LexicalException("CARACTER NO VALIDO: '&' suelto", startLine, startColumn);

            case '|':
                if (!isEOF() && peek() == '|') { advance(); return new Token(TokenType.OP_OR, "||", startLine, startColumn); }
                throw new LexicalException("CARACTER NO VALIDO: '|' suelto", startLine, startColumn);

            default:
                throw new LexicalException(
                        "CARACTER NO VALIDO: '" + c + "'",
                        startLine, startColumn);
        }
    }
    private void skipWhitespaceAndComments() {
        while (!isEOF()) {
            char c = peek();

            // '\u000B' ASCI \v
            if (c == ' ' || c == '\t' || c == '\r' || c == '\n' || c == '\u000B') {
                advance();
                continue;
            }

            // Comentario de linea //
            if (c == '/' && peekNext() == '/') {
                skipLineComment();
                continue;
            }

            // Comentario multilinea /* ... */
            if (c == '/' && peekNext() == '*') {
                skipBlockComment();
                continue;
            }

            break; // no es whitespace ni comentario
        }
    }

    /** Salta desde // hasta el fin de linea. */
    private void skipLineComment() {
        while (!isEOF() && peek() != '\n') {
            advance();
        }
    }

    /** Salta desde /* hasta la primera aparicion de * /. */
    private void skipBlockComment() {
        int startLine   = actual_line;
        int startColumn = actual_colum;
        advance(); // consume '/'
        advance(); // consume '*'

        while (!isEOF()) {
            if (peek() == '*' && peekNext() == '/') {
                advance(); // consume '*'
                advance(); // consume '/'
                return;
            }
            advance();
        }

        // Si llegamos aqui el comentario nunca se cerro
        throw new LexicalException(
                "COMENTARIO MULTILINEA SIN CERRAR: falta */",
                startLine, startColumn);
    }
    //mirar sin consumir los carzcteres
    private char peek() {
        return source.charAt(actual_pos);
    }
    //siguiente sin consumirlo
    private char peekNext() {
        //si llagamos al final retorna caracter nulo  '\0'
        if (actual_pos + 1 >= source.length()) return '\0';
        return source.charAt(actual_pos + 1);
    }
    //igual al next pero le podes mandar posiciones ----!!!
    private char peekNext(int offset) {
        if (actual_pos + offset >= source.length()) return '\0';
        return source.charAt(actual_pos + offset);
    }

    private char advance() {
        char c = source.charAt(actual_pos);
        actual_pos++;
        lastValidLine   = actual_line;
        lastValidColumn = actual_colum;
        if (c == '\n') {
            actual_line++;
            actual_colum = 1;
        } else {
            actual_colum++;
        }
        return c;
    }

    private boolean isEOF() {
        return actual_pos >= source.length();
    }

    private Token makeToken(TokenType type, String lexema) {
        return new Token(type, lexema, actual_line, actual_colum - lexema.length());
    }
}

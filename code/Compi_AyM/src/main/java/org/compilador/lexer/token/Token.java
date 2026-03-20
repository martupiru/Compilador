package org.compilador.lexer.token;

public class Token {

    private final TokenType type;
    private final String lexema;
    private final int line;
    private final int colum;

    public Token(TokenType type, String lexema, int line, int colum) {
        this.type   = type;
        this.lexema = lexema;
        this.line   = line;
        this.colum = colum;
    }

    public TokenType getType()  { return type;   }
    public String getLexema()   { return lexema; }
    public int getLine()        { return line;   }
    public int getColum()      { return colum; }

    /*
     | TOKEN | LEXEMA | LINEA N (COLUMNA C) |
     */
    @Override
    public String toString() {
        return "| " + type.name() + " | " + lexema + " | LINEA " + line + " (COLUMNA " + colum + ") |";
    }
}


package org.compilador.exception;

public class LexicalException extends RuntimeException {

    private final int line;
    private final int column;

    public LexicalException(String description, int line, int column) {
        super(description);
        this.line = line;
        this.column = column;
    }
    public int getLine()   { return line;   }
    public int getColumn() { return column; }

    public String formatError() {
        return "ERROR: LEXICO\n"
                + "| NUMERO DE LINEA (NUMERO DE COLUMNA) | DESCRIPCION: |\n"
                + String.format("| LINEA %d (COLUMNA %d) | %s |",
                line, column, getMessage());
    }
}

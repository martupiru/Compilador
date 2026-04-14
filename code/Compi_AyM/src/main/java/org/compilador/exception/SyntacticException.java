package org.compilador.exception;

public class SyntacticException extends RuntimeException {
    private final int line;
    private final int column;

    public SyntacticException(String description, int line, int column) {
        super(description);
        this.line = line;
        this.column = column;
    }

    public int getLine()   { return line;   }
    public int getColumn() { return column; }

    public String formatError() {
        return "ERROR: SINTACTICO\n"
                + "| NUMERO DE LINEA (NUMERO DE COLUMNA) | DESCRIPCION: |\n"
                + String.format("| LINEA %d (COLUMNA %d) | %s |",
                line, column, getMessage());
    }
}

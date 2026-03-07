package org.compilador.token;

public enum TokenType {

    //Palabras reservadas
    KW_CLASS,       // class
    KW_IMPL,        // impl
    KW_IF,          // if
    KW_ELSE,        // else
    KW_WHILE,       // while
    KW_FOR,         // for
    KW_IN,          // in
    KW_RET,         // ret
    KW_NEW,         // new
    KW_FN,          // fn
    KW_ST,          // st
    KW_PUB,         // pub
    KW_SELF,        // self
    KW_TRUE,        // true
    KW_FALSE,       // false
    KW_NIL,         // nil
    KW_DIV,         // div

    //Tipos primitivos
    TYPE_INT,
    TYPE_STR,
    TYPE_BOOL,
    TYPE_VOID,
    TYPE_ARRAY,

    //Identificadores
    ID_CLASS,       //empieza con MAYUSCULA
    ID_MET_AT,      //empieza con minuscula
    ID_START,

    //Literales
    LIT_INT,
    LIT_STR,
    //!!!!!!!!Preguntar
    LIT_TRUE,
    LIT_FALSE,

    //Operadores aritmeticos
    OP_PLUS,        // +
    OP_MINUS,       // -
    OP_MULT,        // *
    OP_DIV,         // /
    OP_INC,         // ++
    OP_DEC,         // --

    //Operadores comparacion
    OP_LESS,
    OP_LESS_EQ,
    OP_GREATER,
    OP_GREATER_EQ,
    OP_EQUAL,
    OP_NOT_EQUAL,

    //Casteo
    //!!!!!!!!!!!!! ⟨OpUnario⟩ ::= + | - | ! | ++ | -- | (Int)
    //Preguntar si va aca
    CAST_INT,       // (Int)

    //Operadores logicos
    OP_AND,         // &&
    OP_OR,          // ||
    OP_NOT,         // !

    //Asignacion
    ASSIGN,         // =

    //Puntuacion y delimitadores
    LPAREN,         // (
    RPAREN,         // )
    LBRACE,         // {
    RBRACE,         // }
    LBRACKET,       // [
    RBRACKET,       // ]
    SEMICOLON,      // ;
    COMMA,          // ,
    DOT,            // .
    COLON,          // :

    //Fin de archivo
    EOF
}

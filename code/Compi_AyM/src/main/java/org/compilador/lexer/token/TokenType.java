package org.compilador.lexer.token;

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
    //KW_DIV,         // div - cambios en el manual

    //Tipos primitivos
    TYPE_INT,
    TYPE_STR,
    TYPE_BOOL,
    TYPE_VOID,
    TYPE_ARRAY,
    //TYPE_IO, //IO : (IO.out_str("hola mundo"))

    //Identificadores
    ID_CLASS,       //empieza con MAYUSCULA
    ID_MET_AT,      //empieza con minuscula
    ID_START, //palabra start

    //Literales
    LIT_INT,
    LIT_STR,
    //!!!!!!!!Preguntar
    LIT_TRUE,
    LIT_FALSE,

    //Operadores aritmeticos
    OP_SUM,        // +
    OP_REST,       // -
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

    //Operadores logicos
    OP_AND,         // &&
    OP_OR,          // ||
    OP_NOT,         // !

    //Asignacion
    ASIGN,         // =

    //Puntuacion y delimitadores
    IPAREN,         // (
    DPAREN,         // )
    ILLAVE,         // {
    DLLAVE,         // }
    ICORCHETE,       // [
    DCORCHETE,       // ]
    PUNTOCOMA,      // ;
    COMA,          // ,
    PUNTO,            // .
    DOSPUNTOS,          // :

    //Fin de archivo
    EOF
}

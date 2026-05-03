// Test lexico/sintactico con ERROR 02
// Error lexico: caracter invalido @ fuera de comentario

class MiClase {
    Int valor@campo;
}

impl MiClase {
    .() {
        valor@campo = 0;
    }
}

start {
}
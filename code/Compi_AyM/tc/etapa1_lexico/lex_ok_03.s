// Test lexico/sintactico exitoso 03
// Prueba de literales, comentarios y operadores
/* Este es un comentario
   multilinea con #, $, %, &, @, !, ?, n */

class Calculadora {
    pub Int resultado;
}

impl Calculadora {
    .() {
        resultado = 0;
    }
    fn Int sumar(Int a, Int b) {
        ret a + b;
    }
    fn Bool esMayor(Int x, Int y) {
        ret x > y;
    }
}

start {
    Calculadora c;
    Int r;
    Bool b;
    c = new Calculadora();
    r = c.sumar(10, 20);
    b = c.esMayor(5, 3);
    // resultado: true
}
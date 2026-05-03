// Test lexico/sintactico exitoso 02
// Clase simple con constructor y start basico
class Animal {
    Int edad;
    pub Str nombre;
}

impl Animal {
    .(Int e) {
        edad = e;
        nombre = "sin nombre";
    }
    fn Int getEdad() {
        ret edad;
    }
}

start {
    Animal a;
    a = new Animal(5);
}
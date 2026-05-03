// Test sintactico OK 11
// Encadenado con metodos
// Verifica: llamadas encadenadas a.selfReturn().selfReturn()

class A {
}

impl A {
.() {}

fn A selfReturn() {
ret self;
}
}

start {
A a;
a = new A();
(a.selfReturn().selfReturn());
}
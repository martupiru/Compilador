// Test sintactico ERROR 09
// Error: llamada a metodo sin parentesis
// Se espera: error en llamada

class A {}

impl A {
fn Int get() {
ret 1;
}
}

start {
A a;
a = new A();
(a.get);
}
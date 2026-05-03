// Test lexico/sintactico exitoso 04
// Herencia, metodos estaticos, for y while

class Figura {
    pub Int lados;
}

impl Figura {
    .() {
        lados = 0;
    }
    fn Int getLados() {
        ret lados;
    }
}

class Poligono : Figura {
    Int perimetro;
}

impl Poligono {
    .(Int l, Int p) {
        lados = l;
        perimetro = p;
    }
    st fn imprimirTipo() {
        (IO.out_str("Poligono"));
    }
    fn Int getPerimetro() {
        ret perimetro;
    }
}

start {
    Poligono p;
    Int i;
    p = new Poligono(4, 100);
    i = 0;
    while (i < p.getLados()) {
        (IO.out_int(i));
        (++i);
    }
    (Poligono.imprimirTipo());
}
// Test lexico/sintactico exitoso 05
// Arrays, if-else anidado, expresiones complejas, nil y ret sin expresion

class Contenedor {
    pub Int valor;
}

impl Contenedor {
    .(Int v) {
        valor = v;
    }
    fn Bool esPositivo() {
        if (valor > 0) {
            ret true;
        } else {
            ret false;
        }
    }
    fn void resetear() {
        valor = 0;
        ret;
    }
}

start {
    Array Int nums;
    Contenedor c;
    Int x;
    Bool resultado;
    nums = new Int[3];
    nums[0] = 10;
    nums[1] = -5;
    nums[2] = 0;
    for (Int n in nums) {
        (IO.out_int(n));
    }
    c = new Contenedor(42);
    resultado = c.esPositivo();
    if (resultado == true) {
        (IO.out_str("positivo"));
    } else {
        (IO.out_str("no positivo"));
    }
    c = nil;
}
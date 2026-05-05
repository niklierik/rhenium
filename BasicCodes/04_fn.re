fun Sum(a: I32, b: I32): I32 {
    return a + b;
}

fun Dif(a: I32, b: I32): I32 {
    return a - b;
}

fun Mul(a: I32, b: I32): I32 {
    return a * b;
}

fun Div(a: I32, b: I32): I32 {
    return a / b;
}

fun PrintArithemitcOps(a: I32, b: I32) {
    Console.WriteLine(f"a = {a}, b = {b}");
    Console.WriteLine(f"Sum: {Sum(a, b)}");
    Console.WriteLine(f"Dif: {Dif(a, b)}");
    Console.WriteLine(f"Mul: {Mul(a, b)}");
    Console.WriteLine(f"Div: {Div(a, b)}");
}

entry Main {
    PrintArithemitcOps(2, 4);
}
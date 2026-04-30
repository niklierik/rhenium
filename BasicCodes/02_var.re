entry Main {
    // Mutable vs Immutable primitives
    let mutableVar: I32 = 0;
    mutableVar = 1;

    let typeInferredVar = 0;
    // typeInferredVar = "asd"; // ERROR: String cannot be assigned to an I32
    typeInferredVar = 2;

    const immutableVar: I32 = 3;
    // immutableVar = 2; // ERROR: Value of 'immutableVar' cannot be reassigned, because it is immutable.

    // (?) WARNING: 'signedByte' is immutable, but its value is never reassigned.
    let signedByte: I8 = -128;
    const unsignedByte: U8 = 255;

    const signedShort: I16 = -32768;
    const unsignedShort: U16 = 65535;

    const signedInt: I32 = -(2^31);
    const unsignedInt: U32 = 2^32;

    const signedLong: I64 = -2^63;
    const unsignedLong: U64 = 2^64;
}
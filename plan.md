# Rhenium programozási nyelv és fordítóprogram készítése - Tématerv

Feladat egy programozási nyelv megtervezése és ahhoz egy keresztfordító program elkészítése.

## A nyelv

A programozási nyelv erősen típusos, objektumorientált (?) programozási nyelv lenne, azonban manuális, tulajdonjog alapú memóriakezeléssel. Szintaktikája C típusú nyelvekéhez hasonlítana, ezen belül fő inspiráció a C#, Typescript és a Kotlin.

A nyelv ezen elemekkel rendelkezne, amelyek más programozási nyelvekből ismerősek lehetnek:

- változók, *immutable* (nem változtatható) és *mutable* (változtatható) deklaráció
- aritmetikai, logikai kifejezések
- szelekciós vezérlés / feltételes utasítások (`if` - `else if` - `else`)
- ismétléses vezérlés / ciklusok (`while`, `for`, `foreach`, `loop`, `repeat`)
- függvények

- struktúrák: létrehozott példányok a *stack*-en élnek, függvénynek átadáskor másolva lesz alapértelmezetten (C# alapján), a példány tulajdonosát nem kell kezelni

- osztályok: létrehozott példányok a *heap*-en élnek, a példány tulajdonosát kezelni kell
    - mezők (*fields*)
    - metódusok
    - tulajdonságok (*properties*) 
    - nem lenne öröklődés ([composition over inheritance](https://en.wikipedia.org/wiki/Composition_over_inheritance) elv miatt), csak interfész megvalósítás

- interfészek

- tulajdonosalapú (*ownership*) memóriakezeléshez szükséges nyelvi elemek
    - ha a tulajdonos (*owner*) megsemmisül, a hozzátartozó, még élő objektumok is megszűnnek (rekúrzívan)
    - az objektumok felfoghatók erőforrásokként (*resource*) is
    - explicit utasítások tulajdon jog átadására (`gives`), és elvételére (`take`)
        - `gives`: függvény visszatérhet az objektum tulajdonjogával, ilyenkor a hívó félnek el kell vállalnia az objektum tulajdonosának kezelését 
        - `take`: függvény megígérheti, hogy átveszi egy objektum tulajdonjogát, azonban a függvény implementációjában az elvállalt objektumot kezelni is kell (pl. tovább passzolhatja más függvénynek)
    - `using`: ezzel a kulcsszóval létrehozott változókban élő objektumok az adott scope-hoz kötik az élettartalmukat, ezzel a kulcsszóval véglegessé válik egy példány tulajdonosa (C#-ban az erőforrások kezelésére használt kulcsszú, Java-ban `try-with` szintaxissal ismert)
    - ha egy létrehozott példányt nem vesz át egy függvény `take` utasítással, vagy nem lesz `using`-gal deklarált változó értékének adva, akkor lényegében memória szivárgás történne, amelyet a fordítóprogramnak fel kell ismernie, és jeleznie

- generikus programozás
    - minimális, csak típusparaméter, megszórítások nélkül

- C interop
    - függvények, osztályokhoz C implementáció kötése
    - Pointer közvetlen használata, malloc és free utasításokhoz hozzáférés (`alloc`, `free`)

- standard könyvtár
    - *String* (csak ASCII karakterekkel)
    - *Array*
    - *List*, *Set*, *Dictionary*
    - *Optional*
    - *Console*
    - *FileReader*, *FileWriter*
    - explicit hibakezelés [Result pattern](https://en.wikipedia.org/wiki/Result_type) alapján

### Hátralévő időtől függő, opcionális feature-ök

- *union type* és *[pattern match](https://learn.microsoft.com/en-us/dotnet/csharp/fundamentals/functional/pattern-matching)*-ing

## A fordítóprogram 

### Architektúra terv

[Architektúra](architecture.md)

## Technológia

### Fordítóprogram

- ANTLR
- Kotlin
- Gradle

### Generált kód

- C
- CMake
- Clang a fő cél, konfigurálható GCC-vel fordítás is

## Ütemterv

- 2026 Szeptember:
    - Kifejezések
    - Változók típusokkal
    - *Ideiglenes kiíratás*
    - Típus analízis
    - C kód generálásra infrastruktúra
- Október:
    - Scope-ok
    - `if` - `else if` - `else`
    - `for`, `while`
    - Függvények
    - Struktúrák
- November:
    - Interfészek
    - Osztályok
    - Tulajdonjog-módosítók (`take`, `gives`)
    - `using`
- December:
    - Generikus programozás
    - C interop
    - Standard lib
- 2027 Január:
    - Példa szoftver: Torpedó
    - Felfedezett hibák javítása
- Február-Május:
    - Dolgozat írása

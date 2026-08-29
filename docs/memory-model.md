# Memory model

Memory management is turned into **resource management**. Instantiating a class creates a resource,
every resource has an owner, and destroying an owner destroys its resources — recursively.

## Ownership keywords

- **`gives`** — a function returns ownership of an object; the caller must then manage it.
- **`take`** — a parameter takes ownership; the implementation must then place it somewhere, for
  example by passing it on.
- **`using`** — binds an object's lifetime to the enclosing scope and settles its owner for good.
  Comparable to `using` in C# or `try`-with-resources in Java.

```
fun CreateCar(model: String): gives Car {
    return Car(brand, model);
}

fun Store(take car: Car): void {
    optionalCar.Store(car);
}

using factory = CarFactory(brand);
```

## Resource states

A resource is in one of four states:

| State | Meaning |
| --- | --- |
| Resource | an object owned in this context |
| Takable resource | ownership was promised but not yet placed — **never a valid final state** |
| Reference | a shared resource whose ownership cannot be modified here |
| View | an immutable reference |

## Leak detection

An instance that is never `take`n and never bound by a `using` variable would leak. **The compiler is
required to detect this and report it**, rather than letting it through.

class CarFactory(
    brand: Ref<String>
) {
    init {
        Console.WriteLine(f"{brand} Factory created.")
    }

    fun CreateCar(model: String): grant Car {
        return Car(brand, model);
    }

    delete {
        Console.WriteLine(f"{brand} destroyed.");
    }
}

class Car(
    brand: Ref<String>,
    model: Ref<String>
) {
    mutable speed: F64;

    mutate fun Accelerate(): void {
        speed += 1;
    }

    mutate fun Brake(): void {
        speed -= 1;
    }
}

class Garage {
    using optionalCar: Optional<Car> = empty;

    fun Store(take car: Car): void {
        optionalCar.Store(car);
    }

    fun Retrieve(): grant Car {
        return optionalCar.Retrieve();
    }
}

exec Main {
    using brand = Console.ReadLine();
    using factory = CarFactory(brand);

    using model = Console.ReadLine();
    let car = factory.CreateCar(model);

    using garage = Garage();
    garage.Store(car);
}
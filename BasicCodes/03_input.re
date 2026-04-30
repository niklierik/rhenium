/*
reads a raw line of string and makes you the owner of the memory
*/
entry Echo {
    Console.WriteLine("Echo program:");
    using text: String = Console.ReadLine();
    Console.WriteLine(text);
}

/*
scanf wrapper
*/
entry Scan {
    Console.WriteLine("a + b program");
    Console.WriteLine("Waiting for a and b:");
    let a: I32 = 0;
    let b: I32 = 0;

    let aPtr: Ptr<I32> = address(a);
    Console.Scan("%d %d", aPtr, address(b));

    let sum = a + b;

    Console.WriteLine(f"{a} + {b} = {sum}");
}

/*
Prompts are higher level
It knows what to expect from the user, and it reprompts the user if the input is invalid
(for example, if waiting for number, and user types 'text' it reprompts until the user gives back a number)
*/
entry Prompts {
    Console.WriteLine("a + b program"); 
    
    using prompt = Prompt();

    let a = prompt.I32("A: ");
    let b = prompt.I32("B: ");
    
    let sum = a + b;
    Console.WriteLine(f"{a} + {b} = {sum}");

    Console.WriteLine();
    Console.WriteLine();
    Console.WriteLine("echo program");
    
    using text: String = prompt.String("echo: ");

    Console.WriteLine();
    Console.WriteLine(text); 
}
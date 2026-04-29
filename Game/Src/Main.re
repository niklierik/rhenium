namespace Rhenium.Examples.Game;

entry Main {
    let seed = 12345;
    
    using game = Game();

    game.Load();
    let shouldQuit = false;
    
    while (!shouldQuit) {
        game.Render();
    }
}

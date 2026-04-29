namespace Rhenium.Examples.Game;

class Map(
    using take tiles: Array<Tile>,
    width: I32,
    height: I32
) {
    let matrixView = TableView(tiles, width, height);

    init {
        
    }

    mutate fun Generate(): void {
        
    }
}
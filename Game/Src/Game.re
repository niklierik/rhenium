namespace Rhenium.Examples.Game;

class Game() {
    using map: Map;
    using textureRegistry: TextureRegistry = TextureRegistry();

    init {
        using random = Random();
        using mapGenerator = MapGenerator(random);

        map = mapGenerator.Generate();
    }

    mutate fun Load() {
        textureRegistry.Load();
    }

    mutate fun Render() {
    }
}
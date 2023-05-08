package io.show.game.world;

public class MediumWorld {
    private static final int WIDTH = Constants.MAP_MEDIUM_WIDTH;
    private static final int HEIGHT = Constants.MAP_MEDIUM_HEIGHT;

    private final Generator generator = new Generator(Generator.MapSize.medium, HEIGHT, WIDTH);

    public int[][] Map;

    MediumWorld() {
        Map = generator.generate();
    }
}

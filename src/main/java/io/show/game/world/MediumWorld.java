package io.show.game.world;

public class MediumWorld {
    private static final int WIDTH = Constants.MAP_MEDIUM_WIDTH;
    private static final int HEIGHT = Constants.MAP_MEDIUM_HEIGHT;

    private Generator generator = new Generator();

    public int[][] Map;

    MediumWorld(){
        Map = generator.generate();
    }
}

package io.show.game.world;

import vendor.opensimplex2.OpenSimplex2;

import java.util.Random;

public class Orelikelikelyness {
    private Random random = new Random();
    long heightseed1 = random.nextLong();
    long heightseed2 = random.nextLong();
    long likelynessseed = random.nextLong();
    long whichoreseed = random.nextLong();

    public int[][] setOres(int[][] Map) {
        float xOff = 0, xOff2 = 0;
        for (int i = 0; i < Map[0].length; i++) {
            int height = Math.round(OpenSimplex2.noise2(heightseed1, xOff, 0)) * Map[1].length / 2;
            float likelyness = OpenSimplex2.noise2(likelynessseed, xOff, 0);
            int ore = Math.round((5 - 3) * OpenSimplex2.noise2(whichoreseed, xOff2, 0) + 3);
            if (Map[i][height] == 1 && (likelyness < 0.45 || likelyness > 0.65)) Map[i][height] = ore;
            xOff += 0.001;
            xOff2 += 0.0005;
        }
        for (int i = 0; i < Map[0].length; i++) {
            int height = Math.round(OpenSimplex2.noise2(heightseed1, xOff, 0)) * Map[1].length / 2 + Map[1].length / 2;
            float likelyness = OpenSimplex2.noise2(likelynessseed, xOff, 0);
            int ore = Math.round((5 - 3) * OpenSimplex2.noise2(whichoreseed, xOff2, 0) + 3);
            if (Map[i][height] == 1 && (likelyness < 0.45 || likelyness > 0.65)) Map[i][height] = ore;
            xOff += 0.001;
            xOff2 += 0.0005;
        }
        return Map;
    }

}

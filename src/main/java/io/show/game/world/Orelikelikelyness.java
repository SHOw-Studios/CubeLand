package io.show.game.world;

import vendor.opensimplex2.OpenSimplex2;

public class Orelikelikelyness {
    long m_HeightSeed1;
    long m_HeightSeed2;
    long m_LikelinessSeed;
    long m_WhichOreSeed;

    public Orelikelikelyness(long heightseed1, long heightseed2, long likelinessSeed, long whichOreSeed) {
        m_HeightSeed1 = heightseed1;
        m_HeightSeed2 = heightseed2;
        m_LikelinessSeed = likelinessSeed;
        m_WhichOreSeed = whichOreSeed;
    }

    public int[][][] setOres(int[][][] Map) {
        float xOff = 0, xOff2 = 0;
        for (int i = 0; i < Map[0].length; i++) {
            int height = Math.round(OpenSimplex2.noise2(m_HeightSeed1, xOff, 0)) * Map[1].length / 2;
            float likeliness = OpenSimplex2.noise2(m_LikelinessSeed, xOff, 0);
            int ore = Math.round((5 - 3) * OpenSimplex2.noise2(m_WhichOreSeed, xOff2, 0) + 3);
            if (Map[i][height][0] == 1 && (likeliness < 0.45 || likeliness > 0.65)) Map[i][height][0] = ore;
            xOff += 0.001;
            xOff2 += 0.0005;
        }
        for (int i = 0; i < Map[0].length; i++) {
            int height = Math.round(OpenSimplex2.noise2(m_HeightSeed2, xOff, 0)) * Map[1].length / 2 + Map[1].length / 2;
            float likeliness = OpenSimplex2.noise2(m_LikelinessSeed, xOff, 0);
            int ore = Math.round((5 - 3) * OpenSimplex2.noise2(m_WhichOreSeed, xOff2, 0) + 3);
            if (Map[i][height][0] == 1 && (likeliness < 0.45 || likeliness > 0.65)) Map[i][height][0] = ore;
            xOff += 0.001;
            xOff2 += 0.0005;
        }
        return Map;
    }

}

package io.show.game.world;

import vendor.opensimplex2.OpenSimplex2;

/**
 * This Class does nothing at the moment its just implemented to be needed later and not finished
 * later all of this should work like this:
 * if a Tree should be placed on a position, the highest Block on this Width is searched and if it is earth,
 * a hardcoded Tree will be set. The hardcoded Tree function doesn't work at the moment and will maybe be implemented later.
 */

public class TreeGenerator {
    public TreeGenerator(World world) {
        m_HeightSeed = world.getTreeHeightSeed();
        m_LikelinessSeed = world.getTreeLikelinessSeed();
    }

    private long m_HeightSeed;
    private long m_LikelinessSeed;
    private int[][][] Map;

    public int[][][] generateTrees(int[][][] map, int pos) {
        int lastTree = 5;
        for (int i = 0; i < map[0].length; i++) {
            double xOff = 0.001 * pos;
            float likeliness = OpenSimplex2.noise2(m_LikelinessSeed, xOff, 0);
            int height = Math.round((4 - 8) * OpenSimplex2.noise2(m_HeightSeed, xOff, 0) + 4);
            if ((likeliness > 0.65 || likeliness < 0.35) && lastTree > 1) {
                lastTree = 0;
                for (int j = 0; j < map[1].length; j++) {
                    if (map[i][j][0] == 8) {
                        map = setTree(map, i, j, height);
                    }
                }
            } else lastTree++;
        }
        return map;
    }

    public int[][][] setTree(int[][][] Map, int treeXpos, int treeYpos, int height) {
        for (int i = 0; i < height; i++) {
            Map[treeXpos][treeYpos + i][1] = 6;
        }
        return Map;
    }

}

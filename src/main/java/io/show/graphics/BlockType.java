package io.show.graphics;

public class BlockType {

    public int textureIndex;
    public boolean isLiquid;
    public boolean isTransparent;

    public BlockType(int textureIndex, boolean isLiquid, boolean isTransparent) {
        this.textureIndex = textureIndex;
        this.isLiquid = isLiquid;
        this.isTransparent = isTransparent;
    }
}

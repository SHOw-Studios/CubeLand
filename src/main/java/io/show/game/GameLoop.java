package io.show.game;

import imgui.ImGui;
import io.show.game.world.Chunk;
import io.show.game.world.Constants;
import io.show.game.world.World;
import io.show.graphics.*;
import io.show.storage.Storage;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.TimeStep;
import org.dyn4j.dynamics.contact.ContactConstraint;
import org.dyn4j.geometry.*;
import org.dyn4j.world.ContactCollisionData;
import org.dyn4j.world.PhysicsWorld;
import org.dyn4j.world.listener.ContactListener;
import org.dyn4j.world.listener.ContactListenerAdapter;
import org.dyn4j.world.listener.StepListener;
import org.joml.Vector2f;
import org.joml.Vector2fc;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.List;

import io.show.game.physics.PlayerPhysics;


/**
 * @author Ilian O.
 */
public class GameLoop {
    private static int block_lava, block_lava_surface, block_water, block_water_surface, block_dirt, block_grass_block, block_grass_wall, block_leaves, block_liane_wall, block_wood, block_wood_panel, block_coal_ore, block_diamond_ore, block_lapis_ore, block_stone, block_air;

    /**
     * relict of The physics experiment
     */
    private boolean is(Body body, Object... types) {
        for (Object type : types) {
            if (body.getUserData() == type) {
                return true;
            }
        }
        return false;
    }

    /**
     * Initialises the Game.
     * It adds the BlockTextures, generates a World, does dynamic Chunk loading, Player movements and when to do which animations
     */
    public void init() {
        // get the graphics instance
        final Graphics g = Graphics.getInstance();

        int offset = 0;

        final String[] textures = new String[]{ // just a temporary list of paths

                "res/textures/block/liquid/lava.bmp",

                "res/textures/block/liquid/lava_surface.bmp",

                "res/textures/block/liquid/water.bmp",

                "res/textures/block/liquid/water_surface.bmp",

                "res/textures/block/overworld/dirt.bmp",

                "res/textures/block/overworld/grass_block.bmp",

                "res/textures/block/overworld/grass_wall.bmp",

                "res/textures/block/overworld/leaves.bmp",

                "res/textures/block/overworld/liane_wall.bmp",

                "res/textures/block/overworld/wood.bmp",

                "res/textures/block/panel/wood_panel.bmp",

                "res/textures/block/underworld/coal_ore.bmp",

                "res/textures/block/underworld/diamond_ore.bmp",

                "res/textures/block/underworld/lapis_ore.bmp",

                "res/textures/block/underworld/stone.bmp"

        };

        //sets opacities anything but water is 1.0
        final float[] opacities = new float[]{1.0f, 1.0f, 0.7f, 0.7f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f};

        try {
            for (int id = 0; id < textures.length; id++) // go through every path and register its bitmap into the graphics object
                g.registerBitmap(new Bitmap(ImageIO.read(new File(textures[id])), opacities[id]));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        block_air = g.registerBlockType(null); // air
        block_lava = g.registerBlockType(new BlockType(0, true, false));
        block_lava_surface = g.registerBlockType(new BlockType(1, true, false));
        block_water = g.registerBlockType(new BlockType(2, true, true));
        block_water_surface = g.registerBlockType(new BlockType(3, true, true));
        block_dirt = g.registerBlockType(new BlockType(4, false, false));
        block_grass_block = g.registerBlockType(new BlockType(5, false, false));
        block_grass_wall = g.registerBlockType(new BlockType(6, false, false));
        block_leaves = g.registerBlockType(new BlockType(7, false, true));
        block_liane_wall = g.registerBlockType(new BlockType(8, false, true));
        block_wood = g.registerBlockType(new BlockType(9, false, false));
        block_wood_panel = g.registerBlockType(new BlockType(10, false, false));
        block_coal_ore = g.registerBlockType(new BlockType(11, false, false));
        block_diamond_ore = g.registerBlockType(new BlockType(12, false, false));
        block_lapis_ore = g.registerBlockType(new BlockType(13, false, false));
        block_stone = g.registerBlockType(new BlockType(14, false, false));

        // generate a texture atlas out of the registered textures to upload them to the gpu
        g.generateTextureAtlas(16, 16);

        int[] graphicArr = {block_lava, block_lava_surface, block_water, block_water_surface, block_dirt, block_grass_block, block_grass_wall, block_leaves, block_liane_wall, block_wood, block_wood_panel, block_coal_ore, block_diamond_ore, block_lapis_ore, block_stone, block_air};

        World world = new World(World.MapSize.SMALL, graphicArr);
        //Tries reading world. If not possible just creates a new one
        try {
            io.show.storage.World storageWorld = io.show.storage.Storage.readWorld("savedworld");
            world = new World(storageWorld, graphicArr);
        } catch (IOException err) {
            System.out.println(err);
        }

        //makes world Array for Chunks 0 to 6
        int[][][] map = world.makeWorldArray(0, 6);

        //initialises World in Graphics
        g.generateWorldMesh(map, offset * Chunk.getWidth(), map[0][0].length, map[0].length, map.length);

        //lastChunk is needed to know when Chunk is left
        int lastChunk = (int) (g.getPlayerPosition().x() / 16);

        //Set Player and Camera to start Position
        g.setPlayerPosition(new Vector2f(3.5f * Chunk.getWidth(), world.getHeight() / 2));
        g.setCameraPosition(g.getPlayerPosition());
        g.updateCamera();
        g.updatePlayer();
        g.setPlayerLayer(1);
        g.getDebugInfoWindow().logf("PlayerLayer: %d \n", g.getPlayerLayer());


        // this is the main loop, it stops when the graphics' window closes
        Vector2f lastPlayerPos = new Vector2f(10f, 10f);
        PlayerPhysics playerPhysics = new PlayerPhysics(g);
        while (g.loopOnce()) {

            float dx = lastPlayerPos.x * 1000 - g.getPlayerPosition().x * 1000;
            float dy = lastPlayerPos.y * 1000 - g.getPlayerPosition().y * 1000;
            if (dx < 0) {
                dx = dx * -1;
            }
            if (dy < 0) {
                dy = dy * -1;
            }
            Vector2f deltaPos = new Vector2f(dx, dy);

            lastPlayerPos = g.getPlayerPosition();
            //To check for Keyboard inputs and set movement and animations accordingly
            if (!ImGui.getIO().getWantCaptureKeyboard()) {
                final float speed = 15.0f;
                boolean move = false;
                if (Input.getKey(Input.KeyCode.W) || Input.getKey(Input.KeyCode.UP)) {
                    playerPhysics.addYSpeed(speed);
                    move = true;
                }
                if (Input.getKey(Input.KeyCode.S) || Input.getKey(Input.KeyCode.DOWN)) {
                    playerPhysics.addYSpeed(-speed);
                    move = true;
                }
                if (Input.getKey(Input.KeyCode.A) || Input.getKey(Input.KeyCode.LEFT)) {
                    playerPhysics.addXSpeed(-speed);
                    g.getPlayer().setLookingLeft(true);
                    if (g.getPlayer().getCurrentAnimation() != Player.ANIM_RUN)
                        g.getPlayer().setCurrentAnimation(Player.ANIM_RUN);
                    move = true;
                }
                if (Input.getKey(Input.KeyCode.D) || Input.getKey(Input.KeyCode.RIGHT)) {
                    playerPhysics.addXSpeed(speed);
                    g.getPlayer().setLookingLeft(false);
                    if (g.getPlayer().getCurrentAnimation() != Player.ANIM_RUN)
                        g.getPlayer().setCurrentAnimation(Player.ANIM_RUN);
                    move = true;
                }
                if (Input.getKeyPress(Input.KeyCode.LEFT_CONTROL)) {
                    if (g.getPlayerLayer() == 0) g.setPlayerLayer(1);
                    else g.setPlayerLayer(0);
                    //get Debug info about player layer. Was needed before, but now it's a good example of what it can do
                    g.getDebugInfoWindow().logf("PlayerLayer: %d\n", g.getPlayerLayer());
                }
                //Dynamic Chunk loading
                if ((int) (g.getPlayerPosition().x() / Chunk.getWidth()) < lastChunk) {
                    world.addChunk(lastChunk - 4);
                    offset--;
                    lastChunk = world.getChunkIndexAtPos((int) g.getPlayerPosition().x());
                    map = world.makeWorldArray(lastChunk - 3, lastChunk + 3);
                    g.generateWorldMesh(map, offset * Chunk.getWidth(), map[0][0].length, map[0].length, map.length);
                }
                if ((int) (g.getPlayerPosition().x() / Chunk.getWidth()) > lastChunk) {
                    world.addChunk(lastChunk + 4);
                    offset++;
                    lastChunk = world.getChunkIndexAtPos((int) g.getPlayerPosition().x());
                    map = world.makeWorldArray(lastChunk - 3, lastChunk + 3);
                    g.generateWorldMesh(map, offset * Chunk.getWidth(), map[0][0].length, map[0].length, map.length);
                }
                if (!move) if (g.getPlayer().getCurrentAnimation() != Player.ANIM_IDLE) {
                    playerPhysics.setSpeed(new Vector2f(0, 0));
                    g.getPlayer().setCurrentAnimation(Player.ANIM_IDLE);
                }

            }

            //Gravity
//            playerPhysics.addYSpeed((float) Constants.GRAVITY);

            playerPhysics.update();

            g.setCameraPosition(g.getPlayerPosition());
            g.updateCamera();

            g.getDebugInfoWindow().logf("PlayerPos: %f, %f \tDeltaPos %f, %f\n", g.getPlayerPosition().x(), g.getPlayerPosition().y(), deltaPos.x, deltaPos.y);

        }
        //Tries saving the world If not possible it prints an error.
        try {
            io.show.storage.Storage.writeWorld(new io.show.storage.World(world, "savedworld"));
        } catch (IOException err) {
            System.err.println(err);
        }
        // do not forget to destroy all resources after you are done using them
        g.destroy();
    }
}
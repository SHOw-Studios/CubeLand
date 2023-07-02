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


/**
 * @author Ilian O.
 */
public class GameLoop {
    private static int block_lava, block_lava_surface, block_water, block_water_surface, block_dirt, block_grass_block, block_grass_wall, block_leaves, block_liane_wall, block_wood, block_wood_panel, block_coal_ore, block_diamond_ore, block_lapis_ore, block_stone, block_air;

    private static final Object BLOCK = new Object();

//    private static int block(int x, int y, int z, float level, float scale) {
//        float surface = level + SimplexNoise.noise(z * scale, y * scale, x * scale) * 10.0f;
//        float d = surface - y;
//        if (d <= 1 && d > 0) return block_grass_block;
//        if (d <= 2 && d > 0) return block_dirt;
//        if (y < surface) return block_stone;
//        d = level - y;
//        if (d <= 1 && d > 0) return block_water_surface;
//        if (d > 0) return block_water;
//        return block_air;
//    }

    private boolean is(Body body, Object... types) {
        for (Object type : types) {
            if (body.getUserData() == type) {
                return true;
            }
        }
        return false;
    }

    public void init() {
        // get the graphics instance
        final Graphics g = Graphics.getInstance();

        int offset = 0;
        //TODO set Player pos to avg terrain height

        // you can register graphs that get drawn by ImGui as an overlay
        /*
        g.registerGraph((x) -> x * x, 100, -1.0f, 1.0f, 0.0f, 1.0f);
        g.registerGraph((x) -> (float) Math.sqrt(1.0f - x * x), 100, -1.0f, 1.0f, 0.0f, 1.0f);
        g.registerGraph((x) -> (float) -Math.sqrt(1.0f - x * x), 100, -1.0f, 1.0f, 0.0f, 1.0f);
        g.registerGraph(new float[]{0.0f, 0.454f, 0.142f, 0.654f, 0.1534f, 0.13f, 0.92f, 0.155f, 1.0f}, 0.0f, 1.0f, 0.0f, 1.0f);
        */

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
        // World world = Storage.getWorld();
        World world = new World(World.MapSize.SMALL, graphicArr);

        int[][][] map = world.makeWorldArray(0, 6);

        g.generateWorldMesh(map, offset * Chunk.getWidth(), map[0][0].length, map[0].length, map.length);

        int lastChunk = (int) (g.getPlayerPosition().x() / 16);

        g.setPlayerPosition(new Vector2f(3.5f * Chunk.getWidth(), world.getHeight() / 2));
        g.setCameraPosition(g.getPlayerPosition());
        g.updateCamera();
        g.updatePlayer();
        g.setPlayerLayer(1);
        g.getDebugInfoWindow().logf("PlayerLayer: %d \n", g.getPlayerLayer());

        //Physics?
        /*
        org.dyn4j.world.World<Body> dynworld = new org.dyn4j.world.World<Body>();
        Body player = new Body();
        player.addFixture(Geometry.createRectangle(1.0, 2.0));
        player.translate(new Vector2(g.getPlayerPosition().x(), g.getPlayerPosition().y()));
        player.setMass(MassType.NORMAL);
        dynworld.addBody(player);

        for (int i = 0; i < map[g.getPlayerLayer()].length; i++) {
            for (int j = 0; j < map[g.getPlayerLayer()][i].length; j++) {
                if (map[g.getPlayerLayer()][i][j] != 2 && map[g.getPlayerLayer()][i][j] != 3 && map[g.getPlayerLayer()][i][j] != 15) {
                    Body block = new Body();
                    block.addFixture(Geometry.createRectangle(1.0, 1.0));
                    block.translate(j, i); // translate to appropriate world coordinates
                    block.setUserData(BLOCK);
                    dynworld.addBody(block);
                }
            }
        }
        dynworld.addStepListener(new StepListener<Body>() {
            @Override
            public void begin(TimeStep step, PhysicsWorld<Body, ?> world) {
                begin(step, world);
                boolean isGround = false;
                List<ContactConstraint<Body>> contacts = world.getContacts(player);
                for (ContactConstraint<Body> cc : contacts) {
                    if (is(cc.getOtherBody(player), BLOCK) && cc.isEnabled()) {
                        isGround = true;
                    }
                }
                if (!isGround) {
                    isGround = false;
                }
            }

            @Override
            public void updatePerformed(TimeStep step, PhysicsWorld<Body, ?> world) {

            }

            @Override
            public void postSolve(TimeStep step, PhysicsWorld<Body, ?> world) {

            }

            @Override
            public void end(TimeStep step, PhysicsWorld<Body, ?> world) {

            }

        });
        dynworld.addContactListener(new ContactListenerAdapter<Body>() {
            @Override
            public void collision(ContactCollisionData<Body> collision) {
                ContactConstraint<Body> cc = collision.getContactConstraint();
                trackIsOnGround(cc);
                super.collision(collision);
            }
        });
*/

        // this is the main loop, it stops when the graphics' window closes
        while (g.loopOnce()) {
//            g.setPlayerPosition(g.getPlayerPosition().add((float) dynworld.getBody(0).getChangeInPosition().x, (float) dynworld.getBody(0).getChangeInPosition().y));
            /*
            for (int i = 1; i < dynworld.getBodyCount(); i++) {
                dynworld.removeBody(i);
            }
*/
            if (!ImGui.getIO().getWantCaptureKeyboard()) {
                final float speed = Input.getDeltaTime() * 15.0f;
                boolean move = false;
                if (Input.getKey(Input.KeyCode.W) || Input.getKey(Input.KeyCode.UP)) {
                    g.movePlayer(new Vector2f(0, speed));
                    move = true;
                }
                if (Input.getKey(Input.KeyCode.S) || Input.getKey(Input.KeyCode.DOWN)) {
                    g.movePlayer(new Vector2f(0, -speed));
                    move = true;
                }
                if (Input.getKey(Input.KeyCode.A) || Input.getKey(Input.KeyCode.LEFT)) {
                    g.movePlayer(new Vector2f(-speed, 0));
                    g.getPlayer().setLookingLeft(true);
                    if (g.getPlayer().getCurrentAnimation() != Player.ANIM_RUN)
                        g.getPlayer().setCurrentAnimation(Player.ANIM_RUN);
                    move = true;
                }
                if (Input.getKey(Input.KeyCode.D) || Input.getKey(Input.KeyCode.RIGHT)) {
                    g.movePlayer(new Vector2f(speed, 0));
                    g.getPlayer().setLookingLeft(false);
                    if (g.getPlayer().getCurrentAnimation() != Player.ANIM_RUN)
                        g.getPlayer().setCurrentAnimation(Player.ANIM_RUN);
                    move = true;
                }
                /*
                if (Input.getKey(Input.KeyCode.SPACE)) {
                    dynworld.getBody(0).clearForce();
                    dynworld.getBody(0).applyForce(new Vector2(0, 10));
                }*/
                if (Input.getKeyPress(Input.KeyCode.LEFT_CONTROL)) {
                    if (g.getPlayerLayer() == 0) g.setPlayerLayer(1);
                    else g.setPlayerLayer(0);
                    g.getDebugInfoWindow().logf("PlayerLayer: %d\n", g.getPlayerLayer());
                }
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
                if (!move) if (g.getPlayer().getCurrentAnimation() != Player.ANIM_IDLE)
                    g.getPlayer().setCurrentAnimation(Player.ANIM_IDLE);

            }

//            for (int i = ((int) g.getPlayerPosition().y() - 5 + offset * Chunk.getWidth()); i < ((int) g.getPlayerPosition().y() + 5 + offset * Chunk.getWidth()); i++) {
//                for (int j = ((int) g.getPlayerPosition().x() - 5); i < ((int) g.getPlayerPosition().x() + 5); j++) {
//                    if (0 <= i && i < map[0].length && 0 <= j && j < map[0][0].length)
//                        if (map[g.getPlayerLayer()][i][j] != 15 && map[g.getPlayerLayer()][i][j] != 2) {
//                            Body body = new Body();
//                            body.addFixture(Geometry.createRectangle(1, 1));
//                            body.translate(j, i);
//                            body.setMass(MassType.INFINITE);
//                            dynworld.addBody(body);
//                        }
//                }
//            }
//            dynworld.update(1000);

            g.setCameraPosition(g.getPlayerPosition());
            g.updateCamera();

            g.getDebugInfoWindow().logf("PlayerPos: %f, %f\n", g.getPlayerPosition().x(), g.getPlayerPosition().y());
        }

        // do not forget to destroy all resources after you are done using them
        g.destroy();
    }
}
package com.ktarrant.cloudWarfare;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.ktarrant.cloudWarfare.player.Player;

import java.util.ArrayList;

public class MainGdxGame extends ApplicationAdapter {
    private static final int VELOCITY_ITERATIONS = 6;
    private static final int POSITION_ITERATIONS = 2;
    private static final float TIME_STEP = 1 / 60.0f;
    private static final float MAP_WIDTH = 50.0f;
    private static final float MAP_HEIGHT = 50.0f;
    private static final float MAP_KILL_MARGIN = 50.0f;
    private static final float MIN_VIEW_WIDTH = 25.0f;
    private static final Vector2 START_POS = new Vector2(0.0f, 10.0f);

    ArrayList<Player> playerList;
    Player curPlayer;

    SpriteBatch batch;
    Texture img;
    WorldManager.TestWorld testWorld;
    float accumulator = 0;
    Box2DDebugRenderer debugRenderer;
    OrthographicCamera camera;

    @Override
    public void create() {

        // Old stuff TODO: Get rid of this
        batch = new SpriteBatch();
        img = new Texture("badlogic.jpg");

        // Set up Box2D
        Box2D.init();

        // Set up the camera view
        camera = new OrthographicCamera();
        camera.setToOrtho(false, MIN_VIEW_WIDTH,
                MIN_VIEW_WIDTH * Gdx.graphics.getHeight() / Gdx.graphics.getWidth());

        // Create some demo objects to play with
        testWorld = WorldManager.createDemoWorld(MAP_WIDTH);
        playerList = new ArrayList<Player>();
        curPlayer = new Player(testWorld.world, camera, START_POS);
        playerList.add(curPlayer);

        // Create a renderer that annotates the objects
        debugRenderer = new Box2DDebugRenderer(
                true, 	// boolean drawBodies
                true, 	// boolean drawJoints
                false, 	// boolean drawAABBs
                true, 	// boolean drawInactiveBodies
                true, 	// boolean drawVelocities
                true  	// boolean drawContacts
        );

        // Set up the control processing
        Gdx.input.setInputProcessor(new GestureDetector(curPlayer));
    }

    private void checkBounds() {
        for (Player player : playerList) {
            Vector2 pos = player.getPlayerBody().getPosition();
            if ((pos.x < ((-MAP_WIDTH / 2.0) - MAP_KILL_MARGIN)) ||
                ((pos.x > (MAP_WIDTH / 2.0) + MAP_KILL_MARGIN)) ||
                (pos.y < -MAP_KILL_MARGIN) || (pos.y > MAP_HEIGHT + MAP_KILL_MARGIN)) {

                // Reset the player and put them back in the start position
                player.getPlayerBody().setAngularVelocity(0.0f);
                player.getPlayerBody().setLinearVelocity(0.0f, 0.0f);
                player.getPlayerBody().setTransform(START_POS, 0.0f);
            }
        }
    }

    private void doPhysicsStep(float deltaTime) {
        // fixed time step
        // max frame time to avoid spiral of death (on slow devices)
        float frameTime = Math.min(deltaTime, 0.25f);
        accumulator += frameTime;
        while (accumulator >= TIME_STEP) {
            testWorld.world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            accumulator -= TIME_STEP;
        }
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Check if the current player fell off the map
        checkBounds();

        // Update the Box2D world
        doPhysicsStep(Gdx.graphics.getDeltaTime());

        if (playerList != null && playerList.size() > 0) {
            camera.position.set(curPlayer.getPlayerBody().getPosition(), 0);
            camera.update();
        }
        debugRenderer.render(testWorld.world, camera.combined);
    }

    @Override
    public void dispose() {
        if (playerList != null) {
            for (Player player : playerList) {
                player.dispose();
            }
        }

        WorldManager.dispose(testWorld);
    }
}

package com.ktarrant.cloudWarfare;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;

import java.util.ArrayList;

public class MainGdxGame extends ApplicationAdapter implements GestureDetector.GestureListener {
    private static final int VELOCITY_ITERATIONS = 6;
    private static final int POSITION_ITERATIONS = 2;
    private static final float TIME_STEP = 1 / 60.0f;
    private static final float MAP_WIDTH = 50.0f;
    private static final float VIEW_WIDTH = 50.0f;

    ArrayList<PlayerManager.TestPlayer> playerList;
    PlayerManager.TestPlayer curPlayer;

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
        debugRenderer = new Box2DDebugRenderer();
        debugRenderer.setDrawBodies(true);

        // Create some demo objects to play with
        testWorld = WorldManager.createDemoWorld(MAP_WIDTH);
        playerList = new ArrayList<PlayerManager.TestPlayer>();
        curPlayer = PlayerManager.addDemoPlayerToWorld(testWorld, MAP_WIDTH);
        playerList.add(curPlayer);

        // Set up the camera view
        camera = new OrthographicCamera();
        camera.setToOrtho(true, MAP_WIDTH, MAP_WIDTH * Gdx.graphics.getHeight() / Gdx.graphics.getWidth());
        // camera.setToOrtho(true, Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getHeight() * 0.1f);

        // Set up the control processing
        Gdx.input.setInputProcessor(new GestureDetector(this));
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

        // Update the Box2D world
        doPhysicsStep(Gdx.graphics.getDeltaTime());

        if (playerList != null && playerList.size() > 0) {
            camera.position.set(curPlayer.playerBody.getPosition(), 0);
            camera.update();
        }
        debugRenderer.render(testWorld.world, camera.combined);
    }

    @Override
    public void dispose() {
        if (playerList != null) {
            for (PlayerManager.TestPlayer testPlayer : playerList) {
                PlayerManager.dispose(testPlayer);
            }
        }

        WorldManager.dispose(testWorld);
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        Vector3 worldCoor = camera.unproject(new Vector3(x, y, 0.0f));
        Vector2 playerPos = curPlayer.playerBody.getPosition();
        float curAng = MathUtils.atan2(worldCoor.y - playerPos.y, worldCoor.x - playerPos.x);
        curPlayer.playerBody.applyLinearImpulse(
                curPlayer.jumpPower * MathUtils.cos(curAng),
                curPlayer.jumpPower * MathUtils.sin(curAng),
                curPlayer.playerBody.getPosition().x,
                curPlayer.playerBody.getPosition().y,
                true); // wake the player body
        return true;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }
}

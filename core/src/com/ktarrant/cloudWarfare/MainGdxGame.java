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

public class MainGdxGame extends ApplicationAdapter implements GestureDetector.GestureListener {
    private static final int VELOCITY_ITERATIONS = 6;
    private static final int POSITION_ITERATIONS = 2;
    private static final float TIME_STEP = 1 / 60.0f;
    private static final float MAP_WIDTH = 50.0f;
    private static final float VIEW_WIDTH = 50.0f;

    SpriteBatch batch;
    Texture img;
    WorldManager.TestWorld testWorld;
    float accumulator = 0;
    Box2DDebugRenderer debugRenderer;
    OrthographicCamera camera;

    @Override
    public void create() {
        batch = new SpriteBatch();
        img = new Texture("badlogic.jpg");
        Box2D.init();
        debugRenderer = new Box2DDebugRenderer();
        debugRenderer.setDrawBodies(true);
        testWorld = WorldManager.createDemoWorld(MAP_WIDTH);
        camera = new OrthographicCamera();
        camera.setToOrtho(true, MAP_WIDTH, MAP_WIDTH * Gdx.graphics.getHeight() / Gdx.graphics.getWidth());
        // camera.setToOrtho(true, Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getHeight() * 0.1f);

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

        camera.position.set(testWorld.playerBody.getPosition(), 0);
        camera.update();

        debugRenderer.render(testWorld.world, camera.combined);
    }

    @Override
    public void dispose() {
        WorldManager.dispose(testWorld);
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        Vector3 worldCoor = camera.unproject(new Vector3(x, y, 0.0f));
        Vector2 playerPos = testWorld.playerBody.getPosition();
        float curAng = MathUtils.atan2(worldCoor.y - playerPos.y, worldCoor.x - playerPos.x);
        testWorld.playerBody.applyForceToCenter(
                testWorld.jumpPower * MathUtils.cos(curAng),
                testWorld.jumpPower * MathUtils.sin(curAng),
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

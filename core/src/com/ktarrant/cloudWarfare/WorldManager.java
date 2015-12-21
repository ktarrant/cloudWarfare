package com.ktarrant.cloudWarfare;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by ktarrant1 on 12/20/15.
 */
public class WorldManager {

    public static class TestWorld {
        public World world = null;

        public BodyDef groundBodyDef = null;
        public Body groundBody = null;
        public PolygonShape groundBox = null;
        public Fixture groundFixture = null;
    }

    public static void addDemoPlatformsToWorld(TestWorld testWorld, float stageWidth) {
        // Create our body definition
        testWorld.groundBodyDef = new BodyDef();
        // Set its world position
        testWorld.groundBodyDef.position.set(new Vector2(0, 0));

        // Create a body from the defintion and add it to the world
        testWorld.groundBody = testWorld.world.createBody(testWorld.groundBodyDef);

        // Create a polygon shape
        testWorld.groundBox = new PolygonShape();
        // Set the polygon shape as a box which is twice the size of our view port and 20 high
        // (setAsBox takes half-width and half-height as arguments)
        testWorld.groundBox.setAsBox(stageWidth, 1.0f);
        // Create a fixture from our polygon shape and add it to our ground body
        testWorld.groundFixture = testWorld.groundBody.createFixture(testWorld.groundBox, 0.0f);
    }

    public static TestWorld createDemoWorld(float width) {
        TestWorld rv = new TestWorld();

        // Create the world
        rv.world = new World(new Vector2(0, -10.0f), true);

        addDemoPlatformsToWorld(rv, width);

        return rv;
    }

    public static void dispose(TestWorld testWorld) {
        // Clean up after ourselves
        testWorld.groundBox.dispose();
    }
}

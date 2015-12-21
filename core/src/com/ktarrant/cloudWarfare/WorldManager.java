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

        public BodyDef playerBodyDef = null;
        public Body playerBody = null;
        public CircleShape playerCircleShape = null;
        public FixtureDef playerFixtureDef = null;
        public Fixture playerFixture = null;

        public BodyDef groundBodyDef = null;
        public Body groundBody = null;
        public PolygonShape groundBox = null;
        public Fixture groundFixture = null;
    }

    public static void addDemoPlayerToWorld(TestWorld testWorld, float stageWidth) {
        // First we create a body definition
        testWorld.playerBodyDef = new BodyDef();
        // We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
        testWorld.playerBodyDef.type = BodyDef.BodyType.DynamicBody;
        // Set our body's starting position in the world
        testWorld.playerBodyDef.position.set(
                stageWidth * 0.5f,
                -5.0f);

        // Create a circle shape and set its radius to 6
        testWorld.playerCircleShape = new CircleShape();
        testWorld.playerCircleShape.setRadius(0.6f);

        // Create a fixture definition to apply our shape to
        testWorld.playerFixtureDef = new FixtureDef();
        testWorld.playerFixtureDef.shape = testWorld.playerCircleShape;
        testWorld.playerFixtureDef.density = 0.5f;
        testWorld.playerFixtureDef.friction = 0.1f;
        // testWorld.playerFixtureDef.restitution = 0.6f; // Make it bounce a little bit

        // Create our body in the world using our body definition
        testWorld.playerBody = testWorld.world.createBody(testWorld.playerBodyDef);

        // Create our fixture and attach it to the body
        testWorld.playerFixture = testWorld.playerBody.createFixture(testWorld.playerFixtureDef);
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
        rv.world = new World(new Vector2(0, 10.0f), true);


        addDemoPlayerToWorld(rv, width);
        addDemoPlatformsToWorld(rv, width);

        return rv;
    }

    public static void dispose(TestWorld testWorld) {
        // Remember to dispose of any shapes after you're done with them!
        // BodyDef and FixtureDef don't need disposing, but shapes do.
        testWorld.playerCircleShape.dispose();

        // Clean up after ourselves
        testWorld.groundBox.dispose();
    }
}

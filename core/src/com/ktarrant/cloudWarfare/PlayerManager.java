package com.ktarrant.cloudWarfare;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by ktarrant1 on 12/20/15.
 */
public class PlayerManager {

    public static class TestPlayer {
        public float jumpPower = 10.0f;

        public WorldManager.TestWorld testWorld = null;
        public BodyDef playerBodyDef = null;
        public Body playerBody = null;
        public CircleShape playerCircleShape = null;
        public FixtureDef playerFixtureDef = null;
        public Fixture playerFixture = null;
    }

    public static TestPlayer addDemoPlayerToWorld(WorldManager.TestWorld testWorld, float stageWidth) {
        TestPlayer rv = new TestPlayer();

        rv.testWorld = testWorld;

        // First we create a body definition
        rv.playerBodyDef = new BodyDef();
        // We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
        rv.playerBodyDef.type = BodyDef.BodyType.DynamicBody;
        // Set our body's starting position in the world
        rv.playerBodyDef.position.set(
                stageWidth * 0.5f,
                -5.0f);

        // Create a circle shape and set its radius to 6
        rv.playerCircleShape = new CircleShape();
        rv.playerCircleShape.setRadius(0.6f);

        // Create a fixture definition to apply our shape to
        rv.playerFixtureDef = new FixtureDef();
        rv.playerFixtureDef.shape = rv.playerCircleShape;
        rv.playerFixtureDef.density = 0.5f;
        rv.playerFixtureDef.friction = 0.1f;
        // rv.playerFixtureDef.restitution = 0.6f; // Make it bounce a little bit

        // Create our body in the world using our body definition
        rv.playerBody = testWorld.world.createBody(rv.playerBodyDef);

        // Create our fixture and attach it to the body
        rv.playerFixture = rv.playerBody.createFixture(rv.playerFixtureDef);

        return rv;
    }

    public static void dispose(TestPlayer player) {
        player.playerCircleShape.dispose();
    }
}

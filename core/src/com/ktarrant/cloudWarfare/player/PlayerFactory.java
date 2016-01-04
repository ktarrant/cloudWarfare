package com.ktarrant.cloudWarfare.player;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;
import java.util.EnumMap;

/**
 * Created by ktarrant1 on 12/27/15.
 */
public class PlayerFactory {
    public final static float DEFAULT_DEAD_ZONE_ANGLE = MathUtils.PI / 6.0f;
    public static final float DEFAULT_JUMP_POWER = 0.75f;
    public static final float DEFAULT_RUN_POWER = 0.5f;

    public static Player createNewPlayer(World world) {
        Player newPlayer = new Player();

        // First we create a body definition
        newPlayer.bodyDef = new BodyDef();
        // We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
        newPlayer.bodyDef.type = BodyDef.BodyType.DynamicBody;

        // Create a circle shape and set its radius to 6
        newPlayer.circleShape = new CircleShape();
        newPlayer.circleShape.setRadius(0.6f);

        // Create a fixture definition to apply our shape to
        newPlayer.fixtureDef = new FixtureDef();
        newPlayer.fixtureDef.shape = newPlayer.circleShape;
        newPlayer.fixtureDef.density = 0.2f;
        newPlayer.fixtureDef.friction = 1.0f;
        // rv.newPlayerFixtureDef.restitution = 0.6f; // Make it bounce a little bit

        // Create our body in the world using our body definition
        newPlayer.body = world.createBody(newPlayer.bodyDef);

        // Create our fixture and attach it to the body
        newPlayer.fixture = newPlayer.body.createFixture(newPlayer.fixtureDef);

        // Set up the player
        newPlayer.contactBodies = new ArrayList<Body>();
        newPlayer.setState(Player.PlayerState.AIR_ACTIVE);

        return newPlayer;
    }
}

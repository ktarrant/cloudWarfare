package com.ktarrant.cloudWarfare.player;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.ktarrant.cloudWarfare.world.BodyComponent;
import com.ktarrant.cloudWarfare.world.WorldSystem;

import java.util.ArrayList;

/**
 * Created by ktarrant1 on 12/27/15.
 */
public class PlayerFactory {
    public static final float DEFAULT_MAX_STAMINA = 1.0f;
    public static final BodyDef playerBodyDef;
    static {
        playerBodyDef = new BodyDef();
        // We set our body to dynamic, for something like ground which doesn't move we would set it
        // to StaticBody
        playerBodyDef.type = BodyDef.BodyType.DynamicBody;
    }
    public static final CircleShape circleShape;
    static {
        // Create a circle shape and set its radius to 6
        circleShape = new CircleShape();
        circleShape.setRadius(0.6f);
    }
    public static final FixtureDef playerFixtureDef;
    static {
        // Create a fixture definition to apply our shape to
        playerFixtureDef = new FixtureDef();
        playerFixtureDef.shape = circleShape;
//        playerFixtureDef.density = 0.2f;
//        playerFixtureDef.friction = 1.0f;
//        playerFixtureDef.restitution = 0.6f; // Make it bounce a little bit
    }

    //    AIR_ACTIVE      ('A', false, 0.15f, 0.0f, true,  1.0f),
    //    AIR_INACTIVE    ('I', false, 0.15f, 0.0f, false, 1.0f),
    //    FOOT_ACTIVE     ('F', true,  0.65f, 1.0f, true,  10.0f),
    //    FOOT_INACTIVE   ('L', true,  0.65f, 1.0f, false, 5.0f);
    // newBodyComponent.setState(PlayerStateComponent.AIR_ACTIVE);

    public static BodyComponent createPlayerBodyComponent(World world) {
        BodyComponent newBodyComponent = new BodyComponent();

        newBodyComponent.bodyDef = playerBodyDef;
        newBodyComponent.shape = circleShape;
        newBodyComponent.fixtureDef = playerFixtureDef;

        // Create our body in the world using our body definition
        newBodyComponent.body = world.createBody(newBodyComponent.bodyDef);
        // Create our fixture and attach it to the body
        newBodyComponent.fixture = newBodyComponent.body.createFixture(newBodyComponent.fixtureDef);

        return newBodyComponent;
    }

    public static PlayerStateComponent createPlayerStateComponent() {
        PlayerStateComponent stateComp = new PlayerStateComponent();

        // Select AIR_ACTIVE for now
        stateComp.stateIcon = 'A';
        stateComp.isOnFoot = false;
        stateComp.linearDamping = 0.15f;
        stateComp.angularDamping = 0.0f;
        stateComp.isFixedRotation = true;
        stateComp.staminaRegenRate = 1.0f;

        return stateComp;
    }

    public static PlayerStaminaComponent createPlayerStaminaComponent() {
        PlayerStaminaComponent staminaComponent = new PlayerStaminaComponent();
        staminaComponent.maxStamina = DEFAULT_MAX_STAMINA;
        staminaComponent.stamina = staminaComponent.maxStamina;
        return staminaComponent;
    }

    public static Entity createPlayerEntity(World world) {
        Entity entity = new Entity();
        entity.add(createPlayerBodyComponent(world));
        entity.add(createPlayerStaminaComponent());
        entity.add(createPlayerStateComponent());
        return entity;
    }
}

package com.ktarrant.cloudWarfare.player;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.ktarrant.cloudWarfare.world.BodyComponent;
import com.ktarrant.cloudWarfare.world.WorldComponent;

/**
 * Created by ktarrant1 on 12/27/15.
 */
public class PlayerFactory {
    public static final Vector2 DEFAULT_START_POS = new Vector2(0.0f, 10.0f);
    public static final float DEFAULT_MAX_STAMINA = 1.0f;
    public static final BodyDef playerBodyDef;
    static {
        playerBodyDef = new BodyDef();
        // We set our body to dynamic, for something like ground which doesn't move we would set it
        // to StaticBody
        playerBodyDef.type = BodyDef.BodyType.DynamicBody;
        playerBodyDef.position.set(DEFAULT_START_POS);
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
        playerFixtureDef.density = 0.2f;
        playerFixtureDef.friction = 1.0f;
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

    public static PlayerComponent createPlayerComponent(Entity worldEntity) {
        PlayerComponent playerComponent = new PlayerComponent();
        playerComponent.maxStamina = DEFAULT_MAX_STAMINA;
        playerComponent.stamina = playerComponent.maxStamina;
        playerComponent.worldEntity = worldEntity;
        return playerComponent;
    }

    public static Entity createPlayerEntity(Entity worldEntity) {
        WorldComponent worldComp = worldEntity.getComponent(WorldComponent.class);
        Entity entity = new Entity();
        entity.add(createPlayerBodyComponent(worldComp.world));
        entity.add(createPlayerComponent(worldEntity));
        entity.add(createPlayerStateComponent());
        return entity;
    }
}

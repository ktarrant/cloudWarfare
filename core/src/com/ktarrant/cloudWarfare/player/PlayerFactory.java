package com.ktarrant.cloudWarfare.player;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.ktarrant.cloudWarfare.world.BodyComponent;
import com.ktarrant.cloudWarfare.world.FilterCategory;
import com.ktarrant.cloudWarfare.world.WorldComponent;
import com.ktarrant.cloudWarfare.world.WorldFactory;

/**
 * Created by ktarrant1 on 12/27/15.
 */
public class PlayerFactory {
    public static final Vector2 DEFAULT_START_POS =
            new Vector2(0.0f, WorldFactory.PLATFORM_POS_Y + 10.0f);
    public static final float DEFAULT_MAX_STAMINA = 1.0f;

    public static final BodyDef PLAYER_TORSO_BODYDEF;
    static {
        PLAYER_TORSO_BODYDEF = new BodyDef();
        // We set our rootBody to dynamic, for something like ground which doesn't move we would set it
        // to StaticBody
        PLAYER_TORSO_BODYDEF.type = BodyDef.BodyType.DynamicBody;
        PLAYER_TORSO_BODYDEF.position.set(DEFAULT_START_POS);
    }
    public static final CircleShape PLAYER_TORSO_SHAPE;
    static {
        // Create a circle torsoShape and set its radius to 6
        PLAYER_TORSO_SHAPE = new CircleShape();
        PLAYER_TORSO_SHAPE.setRadius(0.6f);
    }
    public static final FixtureDef PLAYER_TORSO_FIXTUREDEF;
    static {
        // Create a rootFixture definition to apply our torsoShape to
        PLAYER_TORSO_FIXTUREDEF = new FixtureDef();
        PLAYER_TORSO_FIXTUREDEF.shape = PLAYER_TORSO_SHAPE;
        PLAYER_TORSO_FIXTUREDEF.density = 0.2f;
        PLAYER_TORSO_FIXTUREDEF.friction = 1.0f;
        PLAYER_TORSO_FIXTUREDEF.filter.categoryBits = FilterCategory.PLAYER_1.categoryBits;
        PLAYER_TORSO_FIXTUREDEF.filter.categoryBits = FilterCategory.PLAYER_1.maskBits;
//        PLAYER_TORSO_FIXTUREDEF.restitution = 0.6f; // Make it bounce a little bit
    }

    public static BodyComponent createPlayerBodyComponent(Entity worldEntity) {
        WorldComponent worldComp = worldEntity.getComponent(WorldComponent.class);
        BodyComponent newBodyComponent = new BodyComponent(worldEntity);

        // Create our rootBody in the world using our torsoBody definition
        newBodyComponent.rootBody = worldComp.world.createBody(PLAYER_TORSO_BODYDEF);
        // Create our rootFixture and attach it to the rootBody
        newBodyComponent.rootFixture =
                newBodyComponent.rootBody.createFixture(PLAYER_TORSO_FIXTUREDEF);
//
//        DistanceJointDef defJoint = new DistanceJointDef ();
//        defJoint.length = PLAYER_TORSO_SHAPE.getRadius() * 1.1f;
//        defJoint.initialize(
//                newBodyComponent.rootBody,
//                newBodyComponent.headBody,
//                new Vector2(0,0), new Vector2(128, 0));
//
//        // Returns subclass Joint.
//        DistanceJoint joint = (DistanceJoint) worldComp.world.createJoint(defJoint);

        return newBodyComponent;
    }

    public static PlayerComponent createPlayerComponent() {
        PlayerComponent playerComponent = new PlayerComponent();
        playerComponent.maxStamina = DEFAULT_MAX_STAMINA;
        playerComponent.stamina = playerComponent.maxStamina;
        playerComponent.state = PlayerState.AIR_ACTIVE;
        return playerComponent;
    }

    public static Entity createPlayerEntity(Entity worldEntity) {
        WorldComponent worldComp = worldEntity.getComponent(WorldComponent.class);
        Entity entity = new Entity();
        entity.add(createPlayerBodyComponent(worldEntity));
        entity.add(createPlayerComponent());
        return entity;
    }
}

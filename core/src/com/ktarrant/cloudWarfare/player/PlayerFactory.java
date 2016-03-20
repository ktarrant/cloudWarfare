package com.ktarrant.cloudWarfare.player;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.physics.box2d.joints.WeldJoint;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
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
        PLAYER_TORSO_FIXTUREDEF.filter.maskBits = FilterCategory.PLAYER_1.maskBits;
//        PLAYER_TORSO_FIXTUREDEF.restitution = 0.6f; // Make it bounce a little bit
    }
    public static final BodyDef PLAYER_HEAD_BODYDEF;
    static {
        PLAYER_HEAD_BODYDEF = new BodyDef();
        // We set our rootBody to dynamic, for something like ground which doesn't move we would set it
        // to StaticBody
        PLAYER_HEAD_BODYDEF.type = BodyDef.BodyType.DynamicBody;
        PLAYER_HEAD_BODYDEF.fixedRotation = true;
        PLAYER_HEAD_BODYDEF.position.set(DEFAULT_START_POS.cpy().add(0, 1.0f));
    }
    public static final CircleShape PLAYER_HEAD_SHAPE;
    static {
        // Create a circle torsoShape and set its radius to 6
        PLAYER_HEAD_SHAPE = new CircleShape();
        PLAYER_HEAD_SHAPE.setRadius(0.3f);
    }
    public static final FixtureDef PLAYER_HEAD_FIXTUREDEF;
    static {
        // Create a rootFixture definition to apply our torsoShape to
        PLAYER_HEAD_FIXTUREDEF = new FixtureDef();
        PLAYER_HEAD_FIXTUREDEF.shape = PLAYER_HEAD_SHAPE;
        PLAYER_HEAD_FIXTUREDEF.density = 0.2f;
        PLAYER_HEAD_FIXTUREDEF.friction = 1.0f;
        PLAYER_HEAD_FIXTUREDEF.filter.categoryBits = FilterCategory.PLAYER_1.categoryBits;
        PLAYER_HEAD_FIXTUREDEF.filter.maskBits = FilterCategory.PLAYER_1.maskBits;
//        PLAYER_HEAD_FIXTUREDEF.restitution = 0.6f; // Make it bounce a little bit
    }
    public static final JointDef PLAYER_NECK_JOINT;
    static {
        RevoluteJointDef neckJoint = new RevoluteJointDef();
        PLAYER_NECK_JOINT = neckJoint;
    }

    public static final BodyDef PLAYER_FOOT_BODYDEF;
    static {
        PLAYER_FOOT_BODYDEF = new BodyDef();
        // We set our rootBody to dynamic, for something like ground which doesn't move we would set it
        // to StaticBody
        PLAYER_FOOT_BODYDEF.type = BodyDef.BodyType.DynamicBody;
        PLAYER_FOOT_BODYDEF.fixedRotation = true;
    }
    public static final PolygonShape PLAYER_FOOT_SHAPE;
    static {
        // Create a circle torsoShape and set its radius to 6
        float PLAYER_FOOT_WIDTH = 0.4f;
        float PLAYER_FOOT_HEIGHT = 0.2f;
        float PLAYER_FOOT_TAPER = 0.05f;
        PLAYER_FOOT_SHAPE = new PolygonShape();
        PLAYER_FOOT_SHAPE.set(new float[] {
                -PLAYER_FOOT_WIDTH/2.0f, -PLAYER_FOOT_HEIGHT,
                PLAYER_FOOT_WIDTH/2.0f, -PLAYER_FOOT_HEIGHT,
                PLAYER_FOOT_WIDTH/2.0f - PLAYER_FOOT_TAPER, 0,
                -PLAYER_FOOT_WIDTH/2.0f + PLAYER_FOOT_TAPER, 0
        });
    }
    public static final FixtureDef PLAYER_FOOT_FIXTUREDEF;
    static {
        // Create a rootFixture definition to apply our torsoShape to
        PLAYER_FOOT_FIXTUREDEF = new FixtureDef();
        PLAYER_FOOT_FIXTUREDEF.shape = PLAYER_FOOT_SHAPE;
        PLAYER_FOOT_FIXTUREDEF.density = 0.2f;
        PLAYER_FOOT_FIXTUREDEF.friction = 1.0f;
        PLAYER_FOOT_FIXTUREDEF.filter.categoryBits = FilterCategory.PLAYER_1.categoryBits;
        PLAYER_FOOT_FIXTUREDEF.filter.maskBits = FilterCategory.PLAYER_1.maskBits;
//        PLAYER_FOOT_FIXTUREDEF.restitution = 0.6f; // Make it bounce a little bit
    }
    public static final JointDef PLAYER_ANKLE_JOINT;
    static {
        RevoluteJointDef neckJoint = new RevoluteJointDef();
        PLAYER_ANKLE_JOINT = neckJoint;
    }

    public static BodyComponent createPlayerBodyComponent(Entity worldEntity) {
        WorldComponent worldComp = worldEntity.getComponent(WorldComponent.class);
        BodyComponent newBodyComponent = new BodyComponent(worldEntity);

        // Create our rootBody in the world using our torsoBody definition
        newBodyComponent.rootBody = worldComp.world.createBody(PLAYER_TORSO_BODYDEF);
        // Create our rootFixture and attach it to the rootBody
        newBodyComponent.rootFixture =
                newBodyComponent.rootBody.createFixture(PLAYER_TORSO_FIXTUREDEF);

        // Create our headBody in the world using our headBody definition
        Body headBody = worldComp.world.createBody(PLAYER_HEAD_BODYDEF);
        // Create our headFixture and attach it to the headBody
        Fixture headFixture = headBody.createFixture(PLAYER_HEAD_FIXTUREDEF);
        ((RevoluteJointDef) PLAYER_NECK_JOINT).initialize(
                newBodyComponent.rootBody, headBody, DEFAULT_START_POS);
        // Returns subclass Joint.
        worldComp.world.createJoint(PLAYER_NECK_JOINT);

        float PLAYER_LEG_ANGLE = - (70) * MathUtils.PI / 180.0f;

        // Create our footBodies in the world using our footBody definition
        PLAYER_FOOT_BODYDEF.position.set(DEFAULT_START_POS.cpy().add(
                MathUtils.cos(PLAYER_LEG_ANGLE), MathUtils.sin(PLAYER_LEG_ANGLE)));
        Body leftFootBody = worldComp.world.createBody(PLAYER_FOOT_BODYDEF);
        // Create our headFixture and attach it to the headBody
        Fixture leftFootFixture = leftFootBody.createFixture(PLAYER_FOOT_FIXTUREDEF);
        ((RevoluteJointDef) PLAYER_ANKLE_JOINT).initialize(
                newBodyComponent.rootBody, leftFootBody, DEFAULT_START_POS);
        // Returns subclass Joint.
        worldComp.world.createJoint(PLAYER_ANKLE_JOINT);

        // Create our footBodies in the world using our footBody definition
        PLAYER_LEG_ANGLE = (float) MathUtils.PI - PLAYER_LEG_ANGLE;
        PLAYER_FOOT_BODYDEF.position.set(DEFAULT_START_POS.cpy().add(
                MathUtils.cos(PLAYER_LEG_ANGLE), MathUtils.sin(PLAYER_LEG_ANGLE)));
        Body rightFootBody = worldComp.world.createBody(PLAYER_FOOT_BODYDEF);
        // Create our headFixture and attach it to the headBody
        Fixture rightFootFixture = rightFootBody.createFixture(PLAYER_FOOT_FIXTUREDEF);
        ((RevoluteJointDef) PLAYER_ANKLE_JOINT).initialize(
                newBodyComponent.rootBody, rightFootBody, DEFAULT_START_POS);
        // Returns subclass Joint.
        worldComp.world.createJoint(PLAYER_ANKLE_JOINT);

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

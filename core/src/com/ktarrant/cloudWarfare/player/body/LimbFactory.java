package com.ktarrant.cloudWarfare.player.body;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.ktarrant.cloudWarfare.world.WorldComponent;

/**
 * Created by ktarrant1 on 3/21/16.
 */
public class LimbFactory {
    private ComponentMapper<PlayerComponent> playerMapper =
            ComponentMapper.getFor(com.ktarrant.cloudWarfare.player.body.PlayerComponent.class);

    // ---------------------------------------------------------------------------------------------
    // Constants for controlling the factory
    // ---------------------------------------------------------------------------------------------
    public static final float PLAYER_HEAD_RADIUS = PlayerFactory.PLAYER_TORSO_RADIUS / 2.0f;
    public static final float PLAYER_FOOT_WIDTH = PLAYER_HEAD_RADIUS;
    public static final float PLAYER_FOOT_HEIGHT = PLAYER_FOOT_WIDTH / 2.0f;
    public static final float PLAYER_FOOT_TAPER = PLAYER_FOOT_WIDTH / 5.0f;
    public static final float PLAYER_WING_SPAN = PlayerFactory.PLAYER_TORSO_RADIUS * 11.0f / 6.0f;
    public static final float PLAYER_SHOULDER_WIDTH = PlayerFactory.PLAYER_TORSO_RADIUS / 10.0f;

    // ---------------------------------------------------------------------------------------------
    // Shapes used as limbs of the character
    // ---------------------------------------------------------------------------------------------
    protected static final CircleShape PLAYER_HEAD_SHAPE;
    static {
        // Create a circle torsoShape and set its radius to 6
        PLAYER_HEAD_SHAPE = new CircleShape();
        PLAYER_HEAD_SHAPE.setRadius(PLAYER_HEAD_RADIUS);
    }
    protected static final PolygonShape PLAYER_FOOT_SHAPE;
    static {
        PLAYER_FOOT_SHAPE = new PolygonShape();
        PLAYER_FOOT_SHAPE.set(new float[] {
                -PLAYER_FOOT_WIDTH/2.0f, -PLAYER_FOOT_HEIGHT,
                PLAYER_FOOT_WIDTH/2.0f, -PLAYER_FOOT_HEIGHT,
                PLAYER_FOOT_WIDTH/2.0f - PLAYER_FOOT_TAPER, 0,
                -PLAYER_FOOT_WIDTH/2.0f + PLAYER_FOOT_TAPER, 0
        });
    }
    protected static final PolygonShape PLAYER_WING_SHAPE;
    static {
        PLAYER_WING_SHAPE = new PolygonShape();
        PLAYER_WING_SHAPE.set(new float[] {
                0, -PLAYER_SHOULDER_WIDTH / 2.0f,
                0, PLAYER_SHOULDER_WIDTH / 2.0f,
                PLAYER_WING_SPAN, 0
        });
    }

    // ---------------------------------------------------------------------------------------------
    // Common Definitions for a Limb
    // ---------------------------------------------------------------------------------------------
    protected static final RevoluteJointDef PLAYER_LIMB_JOINT;
    static {
        PLAYER_LIMB_JOINT = new RevoluteJointDef();
    }

    protected enum LimbType {
        HEAD        (1.3f,   90.0f, PLAYER_HEAD_SHAPE, 0),
        LEFT_TALON  (1.6f, -110.0f, PLAYER_FOOT_SHAPE, 0),
        RIGHT_TALON (1.6f,  -70.0f, PLAYER_FOOT_SHAPE, 0),
        LEFT_WING   (0.8f,  160.0f, PLAYER_WING_SHAPE, -110),
        RIGHT_WING  (0.8f,   20.0f, PLAYER_WING_SHAPE, -70);

        public final float DOCK_RADIUS_RATIO;
        public final float DOCK_ANGLE;
        public final Shape LIMB_SHAPE;
        public final float DEFAULT_ANGLE;

        private LimbType(float dockRadiusRatio, float dockAngle, Shape shape, float defaultAngle) {
            this.DOCK_RADIUS_RATIO = dockRadiusRatio;
            this.DOCK_ANGLE = dockAngle;
            this.LIMB_SHAPE = shape;
            this.DEFAULT_ANGLE = defaultAngle;
        }
    }

    private static void addLimbToTorso(LimbType type, WorldComponent worldComp,
                                       PlayerComponent playerComp) {
        Limb limb = new Limb();

        float bodyRadius = type.DOCK_RADIUS_RATIO * playerComp.rootFixture.getShape().getRadius();
        float defaultAngleRad = type.DEFAULT_ANGLE * MathUtils.degreesToRadians;
        float bodyAngleRad = type.DOCK_ANGLE * MathUtils.degreesToRadians;
        Vector2 limbPos = new Vector2(MathUtils.cos(bodyAngleRad), MathUtils.sin(bodyAngleRad));
        limbPos.scl(bodyRadius);
        limbPos.add(playerComp.rootBody.getPosition());
        // Create our headBody in the world using our headBody definition
        BodyDef limbBodyDef = PlayerFactory.createBaseBodyDef();
        limb.body = worldComp.world.createBody(limbBodyDef);
        limb.body.setTransform(limbPos, defaultAngleRad);

        // Create our headFixture and attach it to the headBody
        FixtureDef limbFixtureDef = PlayerFactory.createBaseFixtureDef();
        limbFixtureDef.shape = type.LIMB_SHAPE;
        limb.fixture = limb.body.createFixture(limbFixtureDef);
        limb.fixture.setUserData(playerComp.rootFixture.getUserData());

        PLAYER_LIMB_JOINT.initialize(playerComp.rootBody, limb.body, playerComp.rootBody.getPosition());
        // Returns subclass Joint.
        limb.joint = (RevoluteJoint) worldComp.world.createJoint(PLAYER_LIMB_JOINT);

        playerComp.limbs.add(limb);
    }

    public static void setupLimbs(WorldComponent worldComp, PlayerComponent playerComp) {
        for (LimbType type : LimbType.values()) {
            addLimbToTorso(type, worldComp, playerComp);
        }
    }
}

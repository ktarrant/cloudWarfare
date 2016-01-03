package com.ktarrant.cloudWarfare.player;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;
import java.util.EnumMap;

/**
 * Created by ktarrant1 on 12/20/15.
 */
public class Player {
    public static final float AIR_LINEAR_DAMPING = 0.5f;
    public static final float AIR_ANGULAR_DAMPING = 0.5f;

    public BodyDef bodyDef = null;
    public Body body = null;
    public CircleShape circleShape = null;
    public FixtureDef fixtureDef = null;
    public Fixture fixture = null;
    public PlayerState state;
    public ArrayList<Body> contactBodies;
    public EnumMap<PlayerState, PlayerStateAttributes> stateAttributes;

    public static class PlayerStateAttributes {
        public float jumpPower;
        public float runPower;
        public float horizDeadZoneCos;
        public float vertDeadZoneCos;
        public float horizDeadZoneAng;
        public float vertDeadZoneAng;
    }

    public enum PlayerState {
        AIR_ACTIVE('A'),
        AIR_PUFF('P'),
        FOOT_ACTIVE('F'),
        FOOT_WALK('W'),
        FOOT_RUN('R');

        public final char stateIcon;

        private PlayerState(char stateIcon) {
            this.stateIcon = stateIcon;
        }
    }

    public void setState(PlayerState newState) {
        if (newState == this.state) {
            return;
        }

        switch (newState) {
            case FOOT_ACTIVE:
                Body floorBody = getFloorBody();
                this.body.setFixedRotation(true);
                this.body.setAngularVelocity(0.0f);
                this.body.setLinearDamping(getFloorBody().getLinearDamping());
                break;
            case FOOT_WALK:
                // Don't need to do anything special - settings same as FOOT_ACTIVE.
                break;
            case FOOT_RUN:
                this.body.setLinearDamping(0.0f);
                break;
            case AIR_ACTIVE:
            case AIR_PUFF:
                this.body.setFixedRotation(false);
                this.body.setLinearDamping(AIR_LINEAR_DAMPING);
                this.body.setAngularDamping(AIR_ANGULAR_DAMPING);
                break;
        }

        System.out.print("New state: " + newState.toString() + "\n");

        state = newState;
    }

    public void update() {

    }

    public PlayerStateAttributes getStateAttributes() {
        return stateAttributes.get(state);
    }

    private Body getFloorBody() {
        if (contactBodies == null || contactBodies.isEmpty()) {
            return null;
        } else {
            return contactBodies.get(0);
        }
    }
}

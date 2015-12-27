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

/**
 * Created by ktarrant1 on 12/20/15.
 */
public class Player {
    public final static float defaultControlDeadZone = MathUtils.PI / 6.0f;
    public final static float playerBodyRadius = 0.6f; // TODO: Make player size configurable

    public BodyDef bodyDef = null;
    public Body body = null;
    public CircleShape circleShape = null;
    public FixtureDef fixtureDef = null;
    public Fixture fixture = null;
    public PlayerState state;
    public ArrayList<Body> contactBodies;

    public enum PlayerState {
        AIR_ACTIVE(0.5f, 0.5f, 0, defaultControlDeadZone, 'A'),
        AIR_PUFF(0.5f, 0.5f, 0, defaultControlDeadZone, 'P'),
        FOOT_ACTIVE(2.0f, 0.5f, defaultControlDeadZone, defaultControlDeadZone, 'F');

        public float jumpPower;
        public float runPower;
        public float horizDeadZoneCos;
        public float vertDeadZoneCos;
        public float horizDeadZoneAng;
        public float vertDeadZoneAng;
        public final char stateIcon;

        private PlayerState(float jumpPower, float runPower, float horizDeadZoneAng, float vertDeadZoneAng, char stateIcon) {
            this.jumpPower = jumpPower;
            this.runPower = runPower;
            this.stateIcon = stateIcon;
            this.horizDeadZoneAng = horizDeadZoneAng;
            this.vertDeadZoneAng = vertDeadZoneAng;
            this.horizDeadZoneCos = MathUtils.cos(horizDeadZoneAng / 2.0f);
            this.vertDeadZoneCos = MathUtils.cos(vertDeadZoneAng / 2.0f);
        }
    }

    public void setState(PlayerState newState) {
        if (newState == this.state) {
            return;
        }

        switch (newState) {
            case FOOT_ACTIVE:
                //TODO: What happens when we are on-foot?
                this.body.setFixedRotation(true);
                this.body.setAngularVelocity(0.0f);
                this.body.setLinearVelocity(0.0f, 0.0f);
                break;
            case AIR_ACTIVE:
            case AIR_PUFF:
                this.body.setFixedRotation(false);
                break;
        }

        System.out.print("New state: " + newState.toString() + "\n");

        state = newState;
    }

    public PlayerState getState() {
        return state;
    }
}

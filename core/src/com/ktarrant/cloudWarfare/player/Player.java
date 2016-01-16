package com.ktarrant.cloudWarfare.player;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import java.util.ArrayList;

/**
 * Created by ktarrant1 on 12/20/15.
 */
public class Player {
    public static final float DEFAULT_MAX_STAMINA = 1.0f;
    public float maxStamina;
    public float stamina;
    public BodyDef bodyDef = null;
    public Body body = null;
    public CircleShape circleShape = null;
    public FixtureDef fixtureDef = null;
    public Fixture fixture = null;
    public PlayerState state;
    public ArrayList<Body> contactBodies;

    private boolean needsStateUpdate = false;

    public Player(float maxStamina) {
        this.maxStamina = maxStamina;
        this.stamina = maxStamina;
    }

    public Player() {
        this(DEFAULT_MAX_STAMINA);
    }

    public void setState(PlayerState newState) {
        if (newState == this.state) {
            return;
        } else {
            needsStateUpdate = true;
            System.out.print("New state: " + newState.toString() + "\n");
            state = newState;
        }
    }

    private Body getFloorBody() {
        if (contactBodies == null || contactBodies.isEmpty()) {
            return null;
        } else {
            return contactBodies.get(0);
        }
    }

    public void update(float delta) {
        if (needsStateUpdate) {
            needsStateUpdate = false;

            // Update any properties we can regardless of state
            this.body.setFixedRotation(state.isFixedRotation);
            this.body.setAngularDamping(state.angularDamping);

            if (state.isOnFoot) {
                Body floorBody = getFloorBody();
                this.body.setLinearDamping(state.linearDamping * floorBody.getLinearDamping());
            } else {
                this.body.setLinearDamping(state.linearDamping);
            }
        }

        // If the player is on Foot and Active, update which way they are facing.
        if (state == PlayerState.FOOT_ACTIVE && this.body.getLinearVelocity().x != 0.0f) {
            this.body.setTransform(this.body.getPosition(),
                    this.body.getLinearVelocity().x > 0.0f ? 0.0f : MathUtils.PI);
        }

        // If the player is on Foot, replenish stamina faster
        this.stamina += state.staminaRegenRate * delta;
        this.stamina = (this.stamina > maxStamina) ? maxStamina : this.stamina;
    }

    public float getStamina() {
        return stamina;
    }
}

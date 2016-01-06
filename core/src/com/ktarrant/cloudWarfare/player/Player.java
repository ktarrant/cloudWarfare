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
    public static final float DEFAULT_STAMINA_GROUND_REGEN_RATE = 0.65f;
    public static final float DEFAULT_STAMINA_AIR_REGEN_RATE = 0.15f;
    public static final float AIR_LINEAR_DAMPING = 1.0f;
    public static final float AIR_ANGULAR_DAMPING = 1.0f;

    public final float maxStamina;
    public final float staminaRegenRate;
    public float stamina;
    public boolean isRunning;
    public BodyDef bodyDef = null;
    public Body body = null;
    public CircleShape circleShape = null;
    public FixtureDef fixtureDef = null;
    public Fixture fixture = null;
    public PlayerState state;
    public ArrayList<Body> contactBodies;

    private boolean needsStateUpdate = false;

    public Player(float maxStamina, float staminaRegenRate) {
        this.maxStamina = maxStamina;
        this.staminaRegenRate = staminaRegenRate;
        this.stamina = maxStamina;
    }

    public Player() {
        this(DEFAULT_MAX_STAMINA, DEFAULT_STAMINA_AIR_REGEN_RATE);
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
            switch (state) {
                case FOOT_ACTIVE: {
                    Body floorBody = getFloorBody();
                    this.body.setFixedRotation(true);
                    this.body.setAngularVelocity(0.0f);
                    this.body.setLinearDamping(getFloorBody().getLinearDamping());
                    break;
                }
                case FOOT_INACTIVE: {
                    Body floorBody = getFloorBody();
                    this.body.setFixedRotation(false);
                    this.body.setLinearDamping(getFloorBody().getLinearDamping());
                    break;
                }
                case AIR_ACTIVE: {
                    this.body.setFixedRotation(true);
                    this.body.setLinearDamping(AIR_LINEAR_DAMPING);
                    this.body.setAngularDamping(AIR_ANGULAR_DAMPING);
                    break;
                }
                case AIR_INACTIVE: {
                    this.body.setFixedRotation(false);
                    this.body.setLinearDamping(AIR_LINEAR_DAMPING);
                    this.body.setAngularDamping(AIR_ANGULAR_DAMPING);
                    break;
                }
            }
        }

        // If the player is on Foot and Active, update which way they are facing.
        if (state == PlayerState.FOOT_ACTIVE && this.body.getLinearVelocity().x != 0.0f) {
            this.body.setTransform(this.body.getPosition(),
                    this.body.getLinearVelocity().x > 0.0f ? 0.0f : MathUtils.PI);
        }

        // If the player is on Foot, replenish stamina faster
        float staminaRate = ((state == PlayerState.FOOT_INACTIVE) ||
                (state == PlayerState.FOOT_ACTIVE)) ?
                DEFAULT_STAMINA_GROUND_REGEN_RATE :
                DEFAULT_STAMINA_AIR_REGEN_RATE;
        this.stamina += staminaRate * delta;
        this.stamina = (this.stamina > maxStamina) ? maxStamina : this.stamina;
    }

    public float getStamina() {
        return stamina;
    }
}

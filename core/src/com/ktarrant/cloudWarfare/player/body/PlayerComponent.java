package com.ktarrant.cloudWarfare.player.body;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.utils.Array;
import com.ktarrant.cloudWarfare.player.PlayerState;

/**
 * Created by Kevin on 2/28/2016.
 */
public class PlayerComponent implements Component {
    // STATIC REFERENCES
    public final Entity worldEntity;

    // BOX2D INSTANCE VARIABLES
    public Body rootBody = null;
    public Fixture rootFixture = null;
    public Array<Limb> limbs = new Array<Limb>();

    // STATE VARIABLES
    public float maxStamina = 0.0f;
    public float stamina = 0.0f;
    public PlayerState state = PlayerState.AIR_ACTIVE;

    public PlayerComponent(Entity worldEntity) {
        this.worldEntity = worldEntity;
    }
}

package com.ktarrant.cloudWarfare.world;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Kevin on 2/28/2016.
 */
public class BodyComponent implements Component {
    // STATIC REFERENCES
    public final Entity worldEntity;

    // BOX2D INSTANCE VARIABLES
    public Body rootBody = null;
    public Fixture rootFixture = null;

    // STATE VARIABLES
    public Array<Entity> contactBodies = new Array<Entity>();

    public BodyComponent(Entity worldEntity) {
        this.worldEntity = worldEntity;
    }
}

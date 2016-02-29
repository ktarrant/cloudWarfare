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
    public BodyDef bodyDef = null;
    public Body body = null;
    public Shape shape = null;
    public FixtureDef fixtureDef = null;
    public Fixture fixture = null;
    public Array<Entity> contactBodies = new Array<Entity>();
}

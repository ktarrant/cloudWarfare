package com.ktarrant.cloudWarfare.world;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;

/**
 * Created by ktarrant1 on 3/21/16.
 */
public class ContactComponent implements Component {
    public Fixture rootFixture;
    public Array<Entity> contactBodies = new Array<Entity>();
}

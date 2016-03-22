package com.ktarrant.cloudWarfare.player.body;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

/**
 * Created by ktarrant1 on 3/21/16.
 */
public class Limb {
    public Body body;
    public Fixture fixture;
    public RevoluteJoint joint;

}

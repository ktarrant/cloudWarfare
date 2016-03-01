package com.ktarrant.cloudWarfare.action;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Kevin on 2/29/2016.
 */
public class ActionDef {
    public float actionStartAngle;
    public float actionLengthAngle;
    public final Vector2 linearImpulseMultiplier = new Vector2();
    public final Vector2 linearImpulseConstant = new Vector2();
    public float depleteConstant;
    public float depleteMultiplier;
    public float repeatInterval;
}

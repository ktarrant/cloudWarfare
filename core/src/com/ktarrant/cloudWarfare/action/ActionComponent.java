package com.ktarrant.cloudWarfare.action;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by ktarrant1 on 1/3/16.
 */
public class ActionComponent implements Component {
    public float actionStartAngle;
    public float actionLengthAngle;
    public final Vector2 linearImpulseMultiplier = new Vector2();
    public final Vector2 linearImpulseConstant = new Vector2();
    public float depleteConstant;
    public float depleteMultiplier;
    public float repeatInterval;
}

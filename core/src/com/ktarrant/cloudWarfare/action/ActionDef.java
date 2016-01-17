package com.ktarrant.cloudWarfare.action;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by ktarrant1 on 1/3/16.
 */
public class ActionDef {
    public final float actionStartAngle;
    public final float actionLengthAngle;
    public final Vector2 linearImpulseMultiplier;
    public final Vector2 linearImpulseConstant;
    public final float depleteConstant;
    public final float depleteMultiplier;
    public final float repeatInterval;

    public ActionDef(float actionStartAngle,
                     float actionLengthAngle,
                     Vector2 linearImpulseMultiplier,
                     Vector2 linearImpulseConstant,
                     float depleteConstant,
                     float depleteMultiplier,
                     float repeatInterval) {

        this.actionStartAngle = actionStartAngle;
        this.actionLengthAngle = actionLengthAngle;
        this.linearImpulseMultiplier = linearImpulseMultiplier;
        this.linearImpulseConstant = linearImpulseConstant;
        this.depleteConstant = depleteConstant;
        this.depleteMultiplier = depleteMultiplier;
        this.repeatInterval = repeatInterval;
    }


    /**
     * Check if the given direction vector lies inside this ActionDef 's capture angle zone.
     * TODO: Optimize this process. We are leaning on angle computations here and they are
     * probably very expensive.
     * @param direction Direction vector
     * @return True if the direction vector lies in our capture angle zone.
     */
    public boolean isInCaptureZone(Vector2 direction) {
        float angle = direction.angleRad();
        float diff = angle - actionStartAngle;
        if (diff < 0.0f && (diff + MathUtils.PI2 < actionLengthAngle)) {
            return true;
        } else if (diff > 0.0f && (diff < actionLengthAngle)) {
            return true;
        } else {
            return false;
        }
    }
}

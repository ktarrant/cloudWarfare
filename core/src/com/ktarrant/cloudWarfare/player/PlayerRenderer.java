package com.ktarrant.cloudWarfare.player;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by ktarrant1 on 12/23/15.
 */
public class PlayerRenderer extends ShapeRenderer {

    public PlayerRenderer() {
        super();
        this.setAutoShapeType(true);
        this.setColor(0.2f, 0.2f, 0.2f, 0.1f);
    }

    public void drawPlayerControlHelp(Player player) {
        this.set(ShapeType.Filled);
        Vector2 playerPos = player.getPlayerBody().getPosition();
        float arcRadius = player.playerBodyRadius * 40.0f;
        float arcAngle = player.state.horizDeadZoneAng * 180.0f / MathUtils.PI;
        float arcStart = -(arcAngle / 2.0f);
        if (arcAngle > 0) {
            arc(playerPos.x, playerPos.y, arcRadius, arcStart, arcAngle);
            arcStart += 180.0f;
            arc(playerPos.x, playerPos.y, arcRadius, arcStart, arcAngle);
        }
        arcAngle = player.state.vertDeadZoneAng * 180.0f / MathUtils.PI;
        arcStart = -(arcAngle / 2.0f) + 90.0f;
        if (arcAngle > 0) {
            arc(playerPos.x, playerPos.y, arcRadius, arcStart, arcAngle);
            arcStart += 180.0f;
            arc(playerPos.x, playerPos.y, arcRadius, arcStart, arcAngle);
        }
    }
}

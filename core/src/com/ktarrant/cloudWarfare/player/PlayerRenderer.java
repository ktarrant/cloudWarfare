package com.ktarrant.cloudWarfare.player;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.ktarrant.cloudWarfare.action.ActionDef;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by ktarrant1 on 12/23/15.
 */
public class PlayerRenderer extends ShapeRenderer {
    protected HashMap<String, PlayerDataLabel> envData;
    protected SpriteBatch textBatch;
    protected BitmapFont font;

    public PlayerRenderer(BitmapFont font) {
        super();
        this.setAutoShapeType(true);
        textBatch = new SpriteBatch();
        setFont(font);
        font.setScale(0.02f);
        font.setUseIntegerPositions(false);
        envData = new HashMap<String, PlayerDataLabel>();
    }

    @Override
    public void setProjectionMatrix(Matrix4 matrix) {
        super.setProjectionMatrix(matrix);

        textBatch.setProjectionMatrix(matrix);
    }

    public void drawPlayerControlHelp(Player player, ActionDef actionDef) {
        this.set(ShapeType.Filled);
        this.setColor(0.2f, 0.2f, 0.2f, 0.1f);
        Vector2 playerPos = player.body.getPosition();
        float playerRadius = player.circleShape.getRadius();
        float arcRadius = playerRadius * 4.0f;
        float arcAngle = MathUtils.radiansToDegrees * actionDef.actionLengthAngle;
        float arcStart = MathUtils.radiansToDegrees * actionDef.actionStartAngle;
        if (arcAngle > 0) {
            arc(playerPos.x, playerPos.y, arcRadius, arcStart, arcAngle, 32);
        }
    }

    public void updateEnvironmentData(String label, float newValue) {
        PlayerDataLabel dataLabel = this.envData.get(label);
        if (dataLabel == null) {
            dataLabel = new PlayerDataLabel(label, newValue);
            this.envData.put(label, dataLabel);
        } else {
            dataLabel.update(newValue);
        }
    }

    public void drawEnvironmentData(Player player) {
        float playerRadius = player.circleShape.getRadius();
        float textRad = playerRadius * 4.0f;
        Vector2 playerPos = player.body.getPosition();
        int i = 0;
        textBatch.begin();
        for (PlayerDataLabel label : this.envData.values()) {
            float angle = (MathUtils.PI / 6.0f) * ((i == 0) ? 0 : (i % 2 == 0 ? (-i / 2) : (i / 2)));
            float x = textRad * MathUtils.cos(angle);
            float y = textRad * MathUtils.sin(angle);
            label.draw(font, textBatch, x + playerPos.x, y + playerPos.y);
            i++;
        }
        textBatch.end();
    }

    public void drawPlayerState(Player player) {
        textBatch.begin();
        Vector2 playerPos = player.body.getPosition();
        font.draw(textBatch, String.valueOf(player.state.stateIcon), playerPos.x, playerPos.y);
        textBatch.end();
    }

    public void setFont(BitmapFont font) {
        this.font = font;
    }

    protected static class PlayerDataLabel {
        public static final int UPDATE_COUNT = 60;
        public static final Color COLOR_UPDATED = Color.GREEN;
        public static final Color COLOR_NORMAL = Color.WHITE;
        private String label;
        private String text;
        private float value;
        private int updateCounter;

        public PlayerDataLabel(String label, float startValue) {
            this.label = label;
            this.update(startValue);
        }

        public void update(float value) {
            this.value = value;
            this.text = String.format("%s: %f", this.label, this.value);
            this.updateCounter = UPDATE_COUNT;
        }

        public void draw(BitmapFont font, SpriteBatch batch, float x, float y) {
            if (updateCounter > 0) {
                updateCounter--;
                font.setColor(COLOR_UPDATED);
            } else {
                font.setColor(COLOR_NORMAL);
            }
            font.draw(batch, text, x, y);
        }
    }
}

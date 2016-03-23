package com.ktarrant.cloudWarfare.player;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.ktarrant.cloudWarfare.SystemPriority;
import com.ktarrant.cloudWarfare.action.ActionComponent;
import com.ktarrant.cloudWarfare.action.ActionModifierComponent;
import com.ktarrant.cloudWarfare.player.body.PlayerComponent;
import com.ktarrant.cloudWarfare.world.CameraComponent;
import com.ktarrant.cloudWarfare.world.ContactComponent;

import java.util.HashMap;

/**
 * Created by ktarrant1 on 12/23/15.
 */
public class PlayerRendererSystem extends IteratingSystem {
    public static final float CONTROL_COLOR_ALPHA = 0.8f;
    public static final float CONTROL_COLOR_SAT = 0.4f;
    public static final Color[] CONTROL_COLOR_LIST;
    static {
        CONTROL_COLOR_LIST = new Color[8];
        int i = 0;
        for (float r = 0.0f; r <= CONTROL_COLOR_SAT; r += CONTROL_COLOR_SAT) {
            for (float g = 0.0f; g <= CONTROL_COLOR_SAT; g += CONTROL_COLOR_SAT) {
                for (float b = 0.0f; b <= CONTROL_COLOR_SAT; b += CONTROL_COLOR_SAT) {
                    if (r == 0.0f && g == 0.0f && b == 0.0f) {
                        // skip black
                        continue;
                    }
                    CONTROL_COLOR_LIST[i++] = new Color(r, g, b, CONTROL_COLOR_ALPHA);
                }
            }
        }
    }
    private ComponentMapper<ActionComponent> actionMapper =
            ComponentMapper.getFor(ActionComponent.class);
    private ComponentMapper<ActionModifierComponent> actionModMapper =
            ComponentMapper.getFor(ActionModifierComponent.class);
    private ComponentMapper<PlayerComponent> playerMapper =
            ComponentMapper.getFor(PlayerComponent.class);
    private ComponentMapper<ContactComponent> contactMapper =
            ComponentMapper.getFor(ContactComponent.class);
    private ComponentMapper<CameraComponent> cameraMapper =
            ComponentMapper.getFor(CameraComponent.class);

    protected HashMap<String, PlayerDataLabel> envData;
    protected ShapeRenderer shapeRenderer;
    protected SpriteBatch textBatch;
    protected BitmapFont font;

    public PlayerRendererSystem() {
        super(Family.all(
                PlayerComponent.class).get(),
                SystemPriority.PLAYER_RENDER.getPriorityValue());

        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        textBatch = new SpriteBatch();
        font = new BitmapFont();
        // TODO: Set Bitmap Font
//        setFont(font);
        font.setUseIntegerPositions(false);
        envData = new HashMap<String, PlayerDataLabel>();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        // Set up the camera
        PlayerComponent playerComp = playerMapper.get(entity);
        ContactComponent contactComp = contactMapper.get(entity);
        Entity worldEntity = playerComp.worldEntity;
        Camera camera = cameraMapper.get(worldEntity).camera;
        shapeRenderer.setProjectionMatrix(camera.combined);
        textBatch.setProjectionMatrix(camera.combined);

        // Draw the player state
        PlayerState stateComp = playerMapper.get(entity).state;
        drawPlayerState(playerComp, stateComp);
//
//        // Update and draw the environmental data
//        updateEnvironmentData("staminaRegenRate", stateComp.staminaRegenRate);
//        updateEnvironmentData("contactCount", contactComp.contactBodies.size);
//        drawEnvironmentData(playerComp);
    }

//    public void drawPlayerControlHelp(PlayerComponent playerComponent, Array<ActionComponent> actionDefList) {
//        this.set(ShapeType.Filled);
//        Vector2 playerPos = playerComponent.rootBody.getPosition();
//        float playerRadius = playerComponent.PLAYER_TORSO_SHAPE.getRadius();
//        float arcRadius = playerRadius * 4.0f;
//        int colorIndex = 0;
//        for (ActionComponent actionComponent : actionDefList) {
//            this.setColor(CONTROL_COLOR_LIST[colorIndex]);
//            float arcAngle = MathUtils.radiansToDegrees * actionComponent.actionLengthAngle;
//            float arcStart = MathUtils.radiansToDegrees * actionComponent.actionStartAngle;
//            if (arcAngle > 0) {
//                arc(playerPos.x, playerPos.y, arcRadius, arcStart, arcAngle, 32);
//            }
//            colorIndex = (colorIndex + 1) % actionDefList.size;
//        }
//    }

    private void updateEnvironmentData(String label, float newValue) {
        PlayerDataLabel dataLabel = this.envData.get(label);
        if (dataLabel == null) {
            dataLabel = new PlayerDataLabel(label, newValue);
            this.envData.put(label, dataLabel);
        } else {
            dataLabel.update(newValue);
        }
    }

    private void drawEnvironmentData(PlayerComponent playerComp) {
        float playerRadius = playerComp.rootFixture.getShape().getRadius();
        float textRad = playerRadius * 4.0f;
        Vector2 playerPos = playerComp.rootBody.getPosition();
        int i = 0;
        textBatch.begin();
        for (PlayerDataLabel label : this.envData.values()) {
            float angle = (MathUtils.PI / 6.0f) * ((i == 0) ? 0 :
                    (i % 2 == 0 ? ((i / 2) + 1) : -(i / 2) - 1));
            float x = textRad * MathUtils.cos(angle);
            float y = textRad * MathUtils.sin(angle);
            label.draw(font, textBatch, x + playerPos.x, y + playerPos.y);
            i++;
        }
        textBatch.end();
    }

    public void drawPlayerState(PlayerComponent playerComp, PlayerState stateComp) {
        float fontScale = playerComp.rootFixture.getShape().getRadius()/12.0f;
        font.getData().setScale(fontScale);
        textBatch.begin();
        Vector2 playerPos = playerComp.rootBody.getPosition();
        font.draw(textBatch, String.valueOf(stateComp.stateIcon), playerPos.x, playerPos.y);
        textBatch.end();
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

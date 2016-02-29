package com.ktarrant.cloudWarfare;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.ktarrant.cloudWarfare.action.ActionModifierComponent;
import com.ktarrant.cloudWarfare.player.PlayerSystem;

import java.util.HashMap;

/**
 * Created by Kevin on 1/17/2016.
 */
public class TestInputProcessor { //} implements InputProcessor {
//    private PlayerSystem manager;
//    private OrthographicCamera camera;
//    private HashMap<Integer, Boolean> keyMap;
//
//    public TestInputProcessor(OrthographicCamera camera, PlayerSystem manager) {
//        this.camera = camera;
//        this.manager = manager;
//
//        keyMap = new HashMap<Integer, Boolean>();
//        updateKeys();
//    }
//
//    private void updateKeys() {
////        ActionModifierComponent rv = ActionModifierComponent.NORMAL;
////
////        if (keyMap.containsKey(Input.Keys.A)) {
////            if (rv == ActionModifierComponent.NORMAL && keyMap.get(Input.Keys.A)) {
////                rv = ActionModifierComponent.ATTACK;
////            }
////        }
//
////        manager.updateModifier(rv);
//    }
//
//    @Override
//    public boolean keyDown(int keycode) {
//        keyMap.put(keycode, true);
//        updateKeys();
//        return true;
//    }
//
//    @Override
//    public boolean keyUp(int keycode) {
//        keyMap.put(keycode, false);
//        updateKeys();
//        return true;
//    }
//
//    @Override
//    public boolean keyTyped(char character) {
//        return false;
//    }
//
//    @Override
//    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
//        // Compute the angles and vectors of the tap relative to the character
//        Vector3 worldCoor = camera.unproject(new Vector3(screenX, screenY, 0.0f));
//        Vector2 playerPos = manager.getActivePosition();
//        manager.updateCursorChange(
//                new Vector2(worldCoor.x - playerPos.x, worldCoor.y - playerPos.y),
//                true);
//        return true;
//    }
//
//    @Override
//    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
//        manager.updateCursorChange(null, false);
//        return true;
//    }
//
//    @Override
//    public boolean touchDragged(int screenX, int screenY, int pointer) {
//        // Compute the angles and vectors of the tap relative to the character
//        Vector3 worldCoor = camera.unproject(new Vector3(screenX, screenY, 0.0f));
//        Vector2 playerPos = manager.getActivePosition();
//        manager.updateCursorChange(
//                new Vector2(worldCoor.x - playerPos.x, worldCoor.y - playerPos.y),
//                false);
//
//        return true;
//    }
//
//    @Override
//    public boolean mouseMoved(int screenX, int screenY) {
//        return false;
//    }
//
//    @Override
//    public boolean scrolled(int amount) {
//        return false;
//    }
}

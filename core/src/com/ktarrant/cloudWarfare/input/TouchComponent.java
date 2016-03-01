package com.ktarrant.cloudWarfare.input;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Kevin on 2/29/2016.
 */
public class TouchComponent implements Component {
    public Vector2 playerTouchVector = new Vector2();
    boolean touchedDown = false;
}

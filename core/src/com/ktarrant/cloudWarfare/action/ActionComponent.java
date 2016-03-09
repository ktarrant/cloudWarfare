package com.ktarrant.cloudWarfare.action;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Created by ktarrant1 on 1/3/16.
 */
public class ActionComponent implements Component {
    public Array<ActionDef> actionDefList = new Array<ActionDef>();
}

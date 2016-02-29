package com.ktarrant.cloudWarfare.world;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by Kevin on 2/28/2016.
 */
public class BoundsComponent implements Component {
    /** Bounds of the Map to confine players in. */
    public final Rectangle bounds = new Rectangle();
}

package com.ktarrant.cloudWarfare.world;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Kevin on 2/28/2016.
 */
public class WorldComponent implements Component {
    /** The World containing all the PlayerComponent s */
    public World world;
}

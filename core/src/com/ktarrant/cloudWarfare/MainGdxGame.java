package com.ktarrant.cloudWarfare;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.physics.box2d.*;
import com.ktarrant.cloudWarfare.action.ActionSystem;
import com.ktarrant.cloudWarfare.input.TouchSystem;
import com.ktarrant.cloudWarfare.player.PlayerFactory;
import com.ktarrant.cloudWarfare.player.PlayerRendererSystem;
import com.ktarrant.cloudWarfare.player.PlayerStateSystem;
import com.ktarrant.cloudWarfare.world.BodyComponent;
import com.ktarrant.cloudWarfare.world.BodySystem;
import com.ktarrant.cloudWarfare.world.DebugRendererSystem;
import com.ktarrant.cloudWarfare.world.WorldFactory;
import com.ktarrant.cloudWarfare.world.WorldSystem;

public class MainGdxGame extends ApplicationAdapter {
    WorldFactory worldFactory;
    PlayerFactory playerFactory;
//    PlayerSystem playerSystem;
//    TestInputProcessor inputProc;
    InputMultiplexer multiplexer;
    Engine engine;

    @Override
    public void create() {
        // Set up Box2D
        Box2D.init();

        // Set up Ashley's Engine and Systems
        this.engine = new Engine();
        this.engine.addSystem(new WorldSystem());           // PR. 0
        this.engine.addSystem(new BodySystem());            // PR. 1
        this.engine.addSystem(new PlayerStateSystem());     // PR. 2
        TouchSystem touchSystem = new TouchSystem();
        this.engine.addSystem(touchSystem);                 // PR. 3
        this.engine.addSystem(new ActionSystem());          // PR. 4
        this.engine.addSystem(new DebugRendererSystem());   // PR. 5
        this.engine.addSystem(new PlayerRendererSystem());  // PR. 6

        // Create some demo objects to play with
        worldFactory = new WorldFactory();
        playerFactory = new PlayerFactory();
        Entity demoWorld = worldFactory.createDemoWorld();
        Entity player = playerFactory.createPlayerEntity(demoWorld);
        Entity platform = new Entity();
        // HACK: To help me out in the BodySystem, give Fixture reference to Entity
        BodyComponent bodyComponent = worldFactory.createPlatformComponent(demoWorld);
        platform.add(bodyComponent);

        // Add the objects to the engine
        engine.addEntity(demoWorld);
        engine.addEntity(player);
        engine.addEntity(platform);

        Gdx.input.setInputProcessor(touchSystem);

//        // Add a player
//        playerSystem = new PlayerSystem(testWorld.world, camera);
//        // Check if the current player fell off the map
//        playerSystem.setBounds(
//
//                playerSystem.addNewPlayer();

//        // Create a font generator and generate a font
//        SmartFontGenerator fontGen = new SmartFontGenerator();
//        FileHandle exoFile = Gdx.files.local("open-sans/OpenSans-Regular.ttf");
//        BitmapFont fontSmall = fontGen.createFont(exoFile, "exo-small", 6);
//
//        // Create a renderer that draws the current player
//        playerRenderer = new PlayerRendererSystem(fontSmall);

//        // Set up the control processing
//        inputProc = new TestInputProcessor(camera, playerSystem);
//        multiplexer = new InputMultiplexer();
//        multiplexer.addProcessor(inputProc);
//        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update the Ashley engine. This will automatically update the Box2D world.
        this.engine.update(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void dispose() {
        worldFactory.dispose();
    }
}

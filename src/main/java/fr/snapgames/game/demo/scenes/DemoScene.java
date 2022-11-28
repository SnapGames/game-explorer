package fr.snapgames.game.demo.scenes;

import fr.snapgames.game.Game;
import fr.snapgames.game.core.behavior.Behavior;
import fr.snapgames.game.core.config.Configuration;
import fr.snapgames.game.core.entity.CameraEntity;
import fr.snapgames.game.core.entity.EntityType;
import fr.snapgames.game.core.entity.GameEntity;
import fr.snapgames.game.core.entity.TextEntity;
import fr.snapgames.game.core.entity.behaviors.*;
import fr.snapgames.game.core.math.Vector2D;
import fr.snapgames.game.core.math.physic.Influencer;
import fr.snapgames.game.core.math.physic.Material;
import fr.snapgames.game.core.math.physic.World;
import fr.snapgames.game.core.scene.AbstractScene;
import fr.snapgames.game.core.scene.Scene;

import java.awt.*;
import java.util.Collection;

public class DemoScene extends AbstractScene implements Scene {


    public DemoScene(Game g) {
        super(g);
    }

    @Override
    public String getName() {
        return "demo";
    }


    @Override
    public void initialize(Game g) {

    }

    @Override
    public void create(Game g) {
        Configuration config = g.getConfiguration();

        int worldWidth = config.getInteger("game.world.width", 1000);
        int worldHeight = config.getInteger("game.world.height", 1000);
        int screenWidth = config.getInteger("game.screen.width", 320);

        GameEntity player = (GameEntity) new GameEntity("player")
                .setPosition(new Vector2D(worldWidth / 2.0, worldHeight / 2.0))
                .setSize(new Vector2D(24, 32))
                .setColor(Color.BLUE)
                .setMass(80.0)
                .setMaterial(new Material("playerMat", 1.0, 0.21, 1.0))
                .setAttribute("maxSpeed", 800.0)
                .setAttribute("maxAcceleration", 800.0)
                .setAttribute("speedStep", 300.0)
                .setAttribute("score", 0)
                .addBehavior(new PlayerInputBehavior());
        add(player);

        TextEntity score = (TextEntity) new TextEntity("score")
                .setText("%05d")
                .setValue(player, "score", 0)
                .setFont(g.getFont().deriveFont(20.0f))
                .setPosition(new Vector2D(screenWidth * 0.8, 10))
                .setSize(new Vector2D(16, 16))
                .setColor(Color.WHITE)
                .stickToCamera(true)
                .addBehavior(new ScoreUpdateBehavior());
        add(score);

        add(new Influencer("magnet")
                .setPosition(new Vector2D(0, worldHeight * 0.5))
                .setSize(new Vector2D(100, worldHeight * 0.5))
                .addForce(new Vector2D(10.0, 0.0))
                .setColor(new Color(0.6f, 0.5f, 0.0f, 0.5f)));

        add(new Influencer("water")
                .setWorld(
                        new World(
                                null,
                                new Vector2D(0.0, 0.90 * -0.981))
                                .setMaterial(new Material("water", 1.0, 1.2, 0.75)))
                .setPosition(new Vector2D(0, worldHeight * 0.80))
                .setSize(new Vector2D(worldWidth, worldHeight * 0.20))
                .addForce(new Vector2D(0.0, 10.0 * -0.981))
                .setColor(new Color(0.0f, 0.5f, 0.8f, 0.5f)));

        for (int i = 0; i < 10; i++) {
            double size = Math.random() * 24.0;
            GameEntity e = (GameEntity) new GameEntity("en_" + i)
                    .setPosition(new Vector2D(Math.random() * worldWidth, Math.random() * worldHeight))
                    .setSize(new Vector2D(size, size))
                    .setColor(Color.RED)
                    .setType(EntityType.CIRCLE)
                    .setMass(30.0 * Math.random() + 20.0)
                    .setMaterial(new Material("enemyMat", 1.1, 0.70, 1.0))
                    .setAttribute("maxSpeed", 800.0)
                    .setAttribute("maxAcceleration", 800.0)
                    .setAttribute("attraction.distance", 80.0 * Math.random() + 30.0)
                    .setAttribute("attraction.force", 20.0 * Math.random() + 5.0)
                    .addBehavior(new EnemyFollowerBehavior());
            add(e);
        }

        int vpWidth = config.getInteger("game.screen.width", 320);
        int vpHeight = config.getInteger("game.screen.height", 200);

        CameraEntity cam = (CameraEntity) new CameraEntity("camera")
                .setTarget(player.name)
                .setTween(0.02)
                .setViewport(new Dimension(vpWidth, vpHeight))
                .setRotation(0.0)
                .addBehavior(new CameraInputBehavior())
                .addBehavior(new CameraUpdateBehavior());

        addCamera(cam);
    }

    @Override
    public void dispose(Game g) {

    }
}

package features;

import fr.snapgames.game.Game;
import fr.snapgames.game.core.entity.CameraEntity;
import fr.snapgames.game.core.entity.GameEntity;
import fr.snapgames.game.core.math.Vector2D;
import io.cucumber.java8.En;


import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import static org.junit.Assert.*;

public class CameraStepdefs implements En {
    private Game game;

    public CameraStepdefs() {
        And("no Camera is set", () -> {
            game = (Game) TestContext.get("game", new Game());
            game.setCurrentCamera(null);
        });
        And("I add a Camera named {string}", (String camName) -> {
            game = (Game) TestContext.get("game");
            CameraEntity cam = new CameraEntity(camName);
            game.setCurrentCamera(cam);
        });
        And("I add a Camera named {string} with tween at {double}", (String camName, Double tweenFactor) -> {
            game = (Game) TestContext.get("game");
            CameraEntity cam = new CameraEntity(camName).setTween(tweenFactor);
            game.setCurrentCamera(cam);
        });
        Then("the current Camera is not null", () -> {
            game = (Game) TestContext.get("game");
            assertNotNull(game.getCurrentCamera());
        });
        And("the current Camera name is {string}", (String camName) -> {
            game = (Game) TestContext.get("game");
            assertEquals(camName, game.getCurrentCamera().name);
        });
        And("set Camera {string} target as GameEntity {string}", (String camName, String targetName) -> {
            game = (Game) TestContext.get("game");
            GameEntity player = (GameEntity) game.getEntities().get(targetName);
            game.getCurrentCamera().setTarget(player.name);
        });
        And("the current Camera {string} name is centered on {string}", (String camName, String targetName) -> {
            game = (Game) TestContext.get("game");
            CameraEntity cam = game.getCurrentCamera();
            GameEntity target = (GameEntity) game.getEntities().get(targetName);
            Vector2D targetPos = target.position;
            Vector2D camPos = cam.position;
            // define the area covered by the target
            Rectangle2D.Double r = new Rectangle2D.Double(targetPos.x, targetPos.y, target.size.x, target.size.y);
            // check is center of viewport area is in the target covered area.
            assertTrue(r.contains(
                    new Point2D.Double(
                            camPos.x + (cam.viewport.width * 0.5),
                            camPos.y + (cam.viewport.height * 0.5))));
        });
        And("set Camera {string} viewport as {int},{int}", (String camName, Integer vpWidth, Integer vpHeight) -> {
            game = (Game) TestContext.get("game");
            CameraEntity cam = game.getCurrentCamera();
            cam.viewport = new Dimension(vpWidth, vpHeight);
        });


    }
}

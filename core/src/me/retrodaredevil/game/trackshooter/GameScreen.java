package me.retrodaredevil.game.trackshooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import me.retrodaredevil.controller.ControllerManager;
import me.retrodaredevil.controller.SimpleControllerManager;
import me.retrodaredevil.game.input.DefaultGameInput;
import me.retrodaredevil.game.input.GameInput;
import me.retrodaredevil.game.input.StandardUSBControllerInput;
import me.retrodaredevil.game.trackshooter.entity.player.Player;
import me.retrodaredevil.game.trackshooter.entity.player.PlayerController;
import me.retrodaredevil.game.trackshooter.level.Level;
import me.retrodaredevil.game.trackshooter.level.LevelMode;
import me.retrodaredevil.game.trackshooter.overlay.Overlay;
import me.retrodaredevil.game.trackshooter.render.RenderComponent;
import me.retrodaredevil.game.trackshooter.render.WorldViewport;
import me.retrodaredevil.game.trackshooter.util.Constants;
import me.retrodaredevil.game.trackshooter.world.World;

public class GameScreen extends ScreenAdapter {

	private final Player player;
	private final Stage stage;
	private final World world;

	private final Overlay overlay;
	private final Stage textStage;

	private boolean shouldExit = false;

	public GameScreen(GameInput gameInput){
		this.player = new Player();
		this.world = new World(new GameLevelGetter(player), 18, 18);
		this.stage = new Stage(new WorldViewport(world));
		this.overlay = new Overlay(world, player);
		this.textStage = new Stage(new ScreenViewport());
//		textStage = null;


		player.setEntityController(new PlayerController(player, gameInput));
		world.addEntity(player);

//		System.out.println("initial entities: " + world.getEntities());

//		Resources.INTRO.play();
	}

	@Override
	public void render(float delta) {
		if(delta > 1){
			System.out.println("Delta is high: " + delta);
			delta = Math.min(delta, .1f); //
			float virtualDelta = 1f / 30f; // seems like it's 30 fps now
			for(int i = 0; i < delta / virtualDelta; i++){
				doUpdate(virtualDelta);
			}
		} else {
			doUpdate(delta);
		}
		doRender(delta);

	}
	private void doUpdate(float delta){
		world.update(delta, world);
		stage.getViewport().apply(true);


		Level level = world.getLevel();
		LevelMode mode = level.getMode();

		if(player.getScoreObject().getLives() <= 0){
			if(mode == LevelMode.NORMAL) {
				level.setMode(LevelMode.RESET);
			}
			if(mode == LevelMode.STANDBY){ // all enemies have returned to start
				if(level.getModeTime() > 4000) {
					shouldExit = true;
				}
			}
			return;
		}
		if(player.isRemoved() && mode == LevelMode.NORMAL){
			level.setMode(LevelMode.RESET);
		}
		if(mode == LevelMode.STANDBY){
			long time = level.getModeTime();
			if(player.isRemoved()){
				if(time > 750){
					world.addEntity(player);
				}
			} else if(time > 1500){
				level.setMode(LevelMode.NORMAL);
			}
		}

	}
	private void doRender(float delta){
		Color backgroundColor = Constants.BACKGROUND_COLOR;
		Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		RenderComponent worldRender = world.getRenderComponent();
		if(worldRender != null){
			worldRender.render(delta, stage);
		}

		stage.act();
		stage.draw();


		RenderComponent overlayRender = overlay.getRenderComponent();
		if(overlayRender != null){
			overlayRender.render(delta, textStage);
		}
		textStage.act();
		textStage.draw();
	}

	@Override
	public void resize(int width, int height) {
		Gdx.gl.glViewport(0, 0, width, height);
		stage    .getViewport().update(width, height,true);
		textStage.getViewport().update(width, height,true);

//		OrthographicCamera camera = (OrthographicCamera) stage.getCamera();
//		camera.setToOrtho(false, 20, 20);
//		camera.position.set(0, 0, 0);
	}

	@Override
	public void dispose() {
		world.disposeRenderComponent();
		stage.dispose();
		textStage.dispose();

	}
	public boolean isGameCompletelyOver(){
		return shouldExit;
	}
}

package me.retrodaredevil.game.trackshooter;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.FitViewport;

import me.retrodaredevil.game.input.GameInput;
import me.retrodaredevil.game.trackshooter.render.Renderer;

public class StartScreen extends ScreenAdapter {
	private static final int BUTTON_WIDTH = 220;
	private static final int BUTTON_HEIGHT = 60;
	private final GameInput gameInput;
	private final RenderParts renderParts;
	private final RenderObject renderObject;
	private boolean start;

	private final Stage uiStage;

	private final Button startButton;
	private final Button optionsButton;
	private boolean optionsDown = false;

	public StartScreen(GameInput gameInput, RenderObject renderObject, RenderParts renderParts){
		this.gameInput = gameInput;
		this.renderParts = renderParts;
		this.renderObject = renderObject;
		this.uiStage = new Stage(new FitViewport(640, 640), renderObject.getBatch());

		Table table = new Table();
		table.setFillParent(true);
		table.center();
		TextButton.TextButtonStyle style = renderObject.getUISkin().get(TextButton.TextButtonStyle.class);
		startButton = new TextButton("start", style); // do stuff with getStartButton.getStyle()
		table.add(startButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT);
		table.row();
		optionsButton = new TextButton("options", style);
		table.add(optionsButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT);


		uiStage.addActor(table);
	}
	@Override
	public void render(float delta) {
		if(optionsDown && !optionsButton.isPressed()){ // just released options button
			renderParts.getOptionsMenu().setToController(gameInput, gameInput);
		}
		optionsDown = optionsButton.isPressed();
		if(gameInput.getStartButton().isPressed() || startButton.isPressed()){
			start = true;
		}
		InputFocuser focuser = new InputFocuser(renderParts.getInputMultiplexer());
		focuser.addInputFocus(renderParts.getOptionsMenu());
//		focuser.addParallelInputProcessor(inputCancellerTester);
		focuser.giveFocus(uiStage);

		Renderer renderer = new Renderer(renderObject.getBatch(), uiStage);
		renderer.addRenderable(renderParts.getBackground());
		renderer.addMainStage();
		renderer.addRenderable(renderParts.getOptionsMenu());
		renderer.addRenderable(renderParts.getOverlay());

		renderer.render(delta);

//		System.out.println(gameInput.getActivatePowerup().isDown());
	}

	@Override
	public void resize(int width, int height) {
		uiStage.getViewport().update(width, height, true);
		renderParts.resize(width, height);
	}

	@Override
	public void dispose() {
		super.dispose();
		uiStage.dispose(); // we created uiStage so dispose it
	}

	public boolean isReadyToStart(){
		return start && !startButton.isPressed();
	}
}

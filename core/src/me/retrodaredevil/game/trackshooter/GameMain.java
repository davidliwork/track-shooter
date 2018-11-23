package me.retrodaredevil.game.trackshooter;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import me.retrodaredevil.controller.ControllerManager;
import me.retrodaredevil.controller.DefaultControllerManager;
import me.retrodaredevil.controller.SimpleControllerPart;
import me.retrodaredevil.controller.implementations.BaseExtremeFlightJoystickControllerInput;
import me.retrodaredevil.controller.implementations.BaseLogitechAttack3JoystickControllerInput;
import me.retrodaredevil.controller.implementations.BaseStandardControllerInput;
import me.retrodaredevil.controller.implementations.ControllerPartCreator;
import me.retrodaredevil.controller.implementations.DefaultExtremeFlightJoystickInputCreator;
import me.retrodaredevil.controller.implementations.DefaultLogitechAttack3JoystickInputCreator;
import me.retrodaredevil.controller.implementations.DefaultStandardControllerInputCreator;
import me.retrodaredevil.controller.options.OptionValues;
import me.retrodaredevil.game.trackshooter.input.ChangeableGameInput;
import me.retrodaredevil.game.trackshooter.input.ControllerGameInput;
import me.retrodaredevil.game.trackshooter.input.GameInput;
import me.retrodaredevil.game.trackshooter.input.GameInputs;
import me.retrodaredevil.game.trackshooter.input.UsableGameInput;
import me.retrodaredevil.game.trackshooter.input.implementations.GdxControllerPartCreator;
import me.retrodaredevil.game.trackshooter.render.RenderObject;
import me.retrodaredevil.game.trackshooter.render.RenderParts;
import me.retrodaredevil.game.trackshooter.render.parts.Background;
import me.retrodaredevil.game.trackshooter.render.parts.OptionMenu;
import me.retrodaredevil.game.trackshooter.render.parts.Overlay;
import me.retrodaredevil.game.trackshooter.render.parts.TouchpadRenderer;
import me.retrodaredevil.game.trackshooter.save.SaveObject;
import me.retrodaredevil.game.trackshooter.util.Resources;

public class GameMain extends Game {

	private RenderObject renderObject;
	private SaveObject saveObject;
	private RenderParts renderParts;

	private ControllerManager controllerManager;
	private List<GameInput> inputs = new ArrayList<>();


	@Override
	public void create () {
		SimpleControllerPart.setDebugChangeInParent(true);

		Batch batch = new SpriteBatch();
		Skin skin = new Skin(Gdx.files.internal("skins/main/skin.json"));
		Resources.loadToSkin(skin);
//		Skin uiSkin = new Skin(Gdx.files.internal("skins/ui/uiskin.json"));
		Skin uiSkin = new Skin(Gdx.files.internal("skins/sgx/sgx-ui.json"));
		Skin arcadeSkin = new Skin(Gdx.files.internal("skins/arcade/arcade-ui.json"));
		renderObject = new RenderObject(batch, skin, uiSkin, arcadeSkin);
		saveObject = new SaveObject();
		renderParts = new RenderParts(new Background(renderObject), new OptionMenu(renderObject, saveObject),
				new Overlay(renderObject), new TouchpadRenderer(renderObject), new InputMultiplexer());
		controllerManager = new DefaultControllerManager();
		{
			boolean firstRun = true;
			for (Iterator<Controller> it = new Array.ArrayIterator<>(Controllers.getControllers()); it.hasNext(); ) {
				Controller controller = it.next();
				String controllerName = controller.getName().toLowerCase();

				// ====== Controller =====
				final ControllerPartCreator controllerPartCreator = new GdxControllerPartCreator(controller);
				final UsableGameInput controllerInput;
				if(controllerName.contains("extreme") && controllerName.contains("logitech")){
					controllerInput = new ControllerGameInput(new BaseExtremeFlightJoystickControllerInput(
							new DefaultExtremeFlightJoystickInputCreator(),
							controllerPartCreator
					));
				} else if(controllerName.contains("attack") && controllerName.contains("logitech")){
//					controllerInput = new ControllerGameInput(new StandardAttackJoystickControllerInput(controller));
					controllerInput = new ControllerGameInput(new BaseLogitechAttack3JoystickControllerInput(
							new DefaultLogitechAttack3JoystickInputCreator(),
                            controllerPartCreator
					));
				} else {
//					controllerInput = new ControllerGameInput(new StandardUSBControllerInput(controller));
					controllerInput = new ControllerGameInput(new BaseStandardControllerInput(
							new DefaultStandardControllerInputCreator(),
							controllerPartCreator,
							OptionValues.createImmutableBooleanOptionValue(false), // TODO make actual option
							OptionValues.createImmutableBooleanOptionValue(false)
					));
				}
				controllerManager.addController(controllerInput);

				// ====== Physical Inputs (Keyboards, on screen) (Only add if we haven't already)
				final Collection<? extends UsableGameInput> addBefore = firstRun ? getPhysicalInputs() : Collections.emptySet();
				for(GameInput input : addBefore){
					controllerManager.addController(input);
				}

				// ==== Inputs to go into our ChangeableGameInput
				final List<UsableGameInput> usableInputs = new ArrayList<>(addBefore);
				usableInputs.add(controllerInput);

				// ==== Create our ChangeableGameInput and add it to our offical inputs
				GameInput realGameInput = new ChangeableGameInput(usableInputs);
				controllerManager.addController(realGameInput);
				inputs.add(realGameInput);

				firstRun = false;
			}
		}
		if(inputs.isEmpty()) {
			List<UsableGameInput> gameInputs = getPhysicalInputs();
			for(UsableGameInput input : gameInputs){
				controllerManager.addController(input);
			}
			GameInput realGameInput = new ChangeableGameInput(gameInputs);
			controllerManager.addController(realGameInput);
			inputs.add(realGameInput);
		}
		{
			int i = 0;
			for (GameInput input : inputs) {
				saveObject.getOptionSaver().loadControllerConfiguration(i, input);
				i++;
			}
		}

//		Gdx.app.setLogLevel(Application.LOG_ERROR);
		Gdx.graphics.setTitle("Track Shooter");
		startScreen();
	}
	private List<UsableGameInput> getPhysicalInputs(){

		List<UsableGameInput> gameInputs = new ArrayList<>();
		if(Gdx.app.getType() == Application.ApplicationType.Android){
			gameInputs.add(GameInputs.createVirtualJoystickInput(renderParts));
			if(Gdx.input.isPeripheralAvailable(Input.Peripheral.Gyroscope)) {
				gameInputs.add(GameInputs.createTouchGyroInput());
			}
		}
		gameInputs.add(GameInputs.createMouseAndKeyboardInput());
		return gameInputs;
	}

	@Override
	public UsableScreen getScreen() {
		return (UsableScreen) super.getScreen();
	}

	@Override
	public void setScreen(Screen screen) {
		if (!(screen instanceof UsableScreen)) {
			throw new IllegalArgumentException("The screen must be a UsableScreen! got: " + screen);
		}
		UsableScreen old = getScreen();
		if(old != null) old.dispose();
		super.setScreen(screen);
	}

	@Override
	public void resize(int width, int height) {
		Gdx.gl.glViewport(0, 0, width, height);
		super.resize(width, height);
	}

	@Override
	public void render() {
		controllerManager.update();
		super.render(); // renders current screen
//		for(GameInput input : inputs){
//			if(!input.isConnected()){
//				System.out.println("input: " + input + " is disconnected!");
//			}
//		}

		UsableScreen screen = getScreen();
		if(screen.isScreenDone()){
			setScreen(screen.createNextScreen());
		}
	}
	private void startScreen(){
		setScreen(new StartScreen(inputs, renderObject, renderParts));
	}

	@Override
	public void dispose() {
		super.dispose();
		renderObject.dispose();
		renderParts.dispose();
		System.out.println("dispose() called on GameMain!");
	}
}

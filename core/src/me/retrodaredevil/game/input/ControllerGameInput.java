package me.retrodaredevil.game.input;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import me.retrodaredevil.controller.ControllerPart;
import me.retrodaredevil.controller.SimpleControllerPart;
import me.retrodaredevil.controller.input.HighestPositionInputPart;
import me.retrodaredevil.controller.input.InputPart;
import me.retrodaredevil.controller.input.JoystickPart;
import me.retrodaredevil.controller.input.SensitiveInputPart;
import me.retrodaredevil.controller.options.ControlOption;
import me.retrodaredevil.controller.options.OptionValues;
import me.retrodaredevil.controller.output.ControllerRumble;
import me.retrodaredevil.controller.types.RumbleCapableController;
import me.retrodaredevil.controller.types.StandardControllerInput;

public class ControllerGameInput extends SimpleControllerPart implements UsableGameInput {
	private final ControllerPart reliesOn;

	private final JoystickPart mainJoystick;
//	private final JoystickPart rotateJoystick;
	private final InputPart rotateAxis;
	private final InputPart fireButton;
	private final InputPart slow;
	private final InputPart activatePowerup;
	private final InputPart startButton;
	private final InputPart pauseButton;
	private final InputPart backButton;

	private final ControllerRumble rumble;

	private final Collection<? extends ControlOption> controlOptions;

	/**
	 *
	 * @param controller The controller to use. This will also be added as a child to this object. When passed, it CANNOT have a parent
	 */
	public ControllerGameInput(StandardControllerInput controller){
		addChildren(false, false, controller);
		reliesOn = controller;

		mainJoystick = controller.getLeftJoy();
//		getMainJoystick = controller.getDPad();
		ControlOption rotateAxisSensitivity = new ControlOption("Rotation Sensitivity", "Adjust the sensitivity when rotating",
				"controls.all", OptionValues.createAnalogRangedOptionValue(.4, 2.5, 1));
		rotateAxis = new SensitiveInputPart(controller.getRightJoy().getXAxis(), rotateAxisSensitivity.getOptionValue(),null);
		fireButton = new HighestPositionInputPart(controller.getRightBumper(), controller.getLeftBumper(), controller.getRightTrigger(), controller.getLeftTrigger());
//		getFireButton = controller.getLeftBumper();
		slow = controller.getLeftStick();
		activatePowerup = controller.getFaceLeft();
		startButton = controller.getStart();
		pauseButton = controller.getStart();
		backButton = controller.getBButton();
		if(controller instanceof RumbleCapableController) {
			rumble = ((RumbleCapableController) controller).getRumble();
		} else {
			rumble = null;
		}

		addChildren(Arrays.asList(rotateAxis, fireButton), false, false);

		controlOptions = Arrays.asList(rotateAxisSensitivity);
	}

	@Override
	public String getRadioOptionName() {
		return "Controller Control Option";
	}

	@Override
	public Collection<? extends ControlOption> getControlOptions() {
		return controlOptions;
	}

	@Override
	public JoystickPart getMainJoystick(){
		return mainJoystick;
	}

	@Override
	public InputPart getRotateAxis() {
		return rotateAxis;
	}

	@Override
	public InputPart getFireButton() {
		return fireButton;
	}

	@Override
	public InputPart getSlowButton() {
		return slow;
	}

	@Override
	public InputPart getActivatePowerup() {
		return activatePowerup;
	}

	@Override
	public InputPart getStartButton() {
		return startButton;
	}

	@Override
	public InputPart getPauseButton() {
		return pauseButton;
	}

	@Override
	public InputPart getBackButton() {
		return backButton;
	}

	@Override
	public boolean isConnected() {
		return areAnyChildrenConnected() && (reliesOn == null || reliesOn.isConnected());
	}

	@Override
	public ControllerRumble getRumble() {
		return rumble;
	}
}

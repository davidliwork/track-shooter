package me.retrodaredevil.controller.input;

import me.retrodaredevil.controller.ControllerPart;

/**
 * Represents a ControllerPart where its input is an angle in degrees.
 * <br/>
 * Can have numerous uses such as a joystick, steering wheel, gyro, etc.
 */
public interface AnglePart extends ControllerPart {

	/** @return angle of the joystick in degrees. If joystick completely centered, usually returns 0 but not guaranteed. */
	double getAngle();

	/** @return angle of the joystick in radians. If joystick completely centered, usually returns 0 but not guaranteed.*/
	double getAngleRadians();

	/**
	 * Depending on the implementation this may or may not return true. If it is a joystick, this
	 * will return true when centered.
	 * @return true if the magnitude or another factor determine that the returned values from getAngle() or other
	 * 			methods are not accurate, false otherwise.
	 */
	boolean isDeadzone();
}

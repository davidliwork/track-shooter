package me.retrodaredevil.game.trackshooter.util;

public final class Constants {
	/** In world units */
	public static final float BULLET_SPEED = 22;
	/** In world units */
	public static final float SHOT_GUN_BULLET_SPEED = 15;

	/** In degrees */
	public static final float ROTATIONAL_VELOCITY_SET_GOTO_DEADBAND = 10;
	/** In world units*/
	public static final float TRAVEL_VELOCITY_SET_GOTO_DEADBAND = .5f;

	/** The normal size for buttons on menus with buttons */
	public static final Size BUTTON_SIZE = Size.createSize(220, 60);
	public static final Size OPTIONS_MENU_TOP_BUTTONS_SIZE = Size.createSize(70, 30);
	public static final Size OPTIONS_MENU_BOTTOM_BUTTONS_SIZE = Size.createSize(100, 40);

	/** The maximum velocity the player can travel at on the track*/
	public static final float PLAYER_VELOCITY = 5f;

}

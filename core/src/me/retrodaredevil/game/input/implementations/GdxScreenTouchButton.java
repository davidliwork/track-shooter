package me.retrodaredevil.game.input.implementations;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;

import me.retrodaredevil.controller.input.AutoCachingInputPart;
import me.retrodaredevil.controller.input.AxisType;
import me.retrodaredevil.game.trackshooter.util.Util;

public class GdxScreenTouchButton extends AutoCachingInputPart {
	private static final int NUM_TOUCHES = 20;
	private final boolean needsTouchScreenForConnection;
	private final Rectangle proportionalScreenArea = new Rectangle();
	public GdxScreenTouchButton(boolean needsTouchScreenForConnection, Rectangle proportionalScreenArea) {
		super(new AxisType(false, false, false, true), false);
		this.needsTouchScreenForConnection = needsTouchScreenForConnection;
		this.proportionalScreenArea.set(proportionalScreenArea);
	}
	public GdxScreenTouchButton(Rectangle proportionalScreenArea){
		this(true, proportionalScreenArea);
	}

	@Override
	protected double calculatePosition() {
		Rectangle area = Util.proportionalRectangleToScreenArea(proportionalScreenArea);
		if(Gdx.input.isTouched()){
			for(int i = 0; i < NUM_TOUCHES; i++){
				try {
					if (Gdx.input.isTouched(i)) {
						if (area.contains(Gdx.input.getX(i), Gdx.input.getY(i))) {
							return 1;
						}
					}
				} catch(IndexOutOfBoundsException ex){
					ex.printStackTrace();
					System.err.println("index: " + i + " is too big to be a pointer.");
				}
			}
		}
		return 0;
	}

	@Override
	public boolean isConnected() {
		return !needsTouchScreenForConnection || Gdx.input.isPeripheralAvailable(Input.Peripheral.MultitouchScreen);
	}
}
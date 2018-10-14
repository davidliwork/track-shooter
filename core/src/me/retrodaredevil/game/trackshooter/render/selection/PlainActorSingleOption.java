package me.retrodaredevil.game.trackshooter.render.selection;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.util.Collection;

import me.retrodaredevil.controller.input.InputPart;
import me.retrodaredevil.controller.input.JoystickPart;
import me.retrodaredevil.game.trackshooter.util.ActorUtil;

public class PlainActorSingleOption extends ContainerSingleOption{
	private final Actor actor;
	private final Float width, height;

	public PlainActorSingleOption(Actor actor, Float width, Float height){
		super();
		this.actor = actor;
		this.width = width;
		this.height = height;
	}

	@Override
	protected void onInit(Table container) {
		super.onInit(container);
		Cell<Actor> cell = container.add(actor);
		if(width != null){
			cell.width(width);
		}
		if(height != null){
			cell.height(height);
		}
	}

	@Override
	public void selectUpdate(float delta, JoystickPart selector, InputPart select, InputPart back, Collection<SelectAction> requestedActions) {
		ActorUtil.fireInputEvents(actor, InputEvent.Type.enter);
		if(select.isPressed()){
			ActorUtil.fireInputEvents(actor, InputEvent.Type.touchDown, InputEvent.Type.touchUp);
		}
//		if(select.isReleased()){
//		}
	}

	@Override
	public void deselect() {
		ActorUtil.fireInputEvents(actor, InputEvent.Type.exit);

	}
}

package me.retrodaredevil.game.trackshooter.render.selection;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Pools;

/**
 *
 */
public abstract class ContainerSingleOption implements SingleOption {
	private final Table container = new Table();
	private Cell containerCell = null;

	private boolean initialized = false;

	protected void onInit(Table container){}
	protected void onUpdate(Table container){}

	@Override
	public void renderUpdate(Table table) {

		if(!initialized){
			onInit(container);
		}
		initialized = true;
		if(container.getParent() != table){
			containerCell = table.add(container);
			table.row();
		}

		onUpdate(container);
	}

	@Override
	public void reset() {

	}

	@Override
	public void remove() {
//		System.out.println("cell's actor before: " + containerCell.getActor());
		container.remove();
//		System.out.println("cell's actor: " + containerCell.getActor());
		initialized = false;
	}

	public static boolean fireInputEvents(Actor actor, InputEvent.Type... types){
		boolean eitherHandled = false;
		for(InputEvent.Type type : types){
			InputEvent event = Pools.obtain(InputEvent.class);
			event.setPointer(-1); // fix for hard coded ClickListener mouse pointer requirement
			event.setType(type);
			event.setButton(Input.Buttons.LEFT);

			actor.fire(event);
			if(!eitherHandled){
				eitherHandled = event.isHandled();
			}
			Pools.free(event);
		}
		return eitherHandled;
	}
}

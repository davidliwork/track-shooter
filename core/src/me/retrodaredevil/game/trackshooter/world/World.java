package me.retrodaredevil.game.trackshooter.world;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import me.retrodaredevil.game.trackshooter.CollisionHandler;
import me.retrodaredevil.game.trackshooter.Renderable;
import me.retrodaredevil.game.trackshooter.Updateable;
import me.retrodaredevil.game.trackshooter.entity.Entity;
import me.retrodaredevil.game.trackshooter.level.Level;
import me.retrodaredevil.game.trackshooter.level.LevelGetter;
import me.retrodaredevil.game.trackshooter.level.LevelMode;
import me.retrodaredevil.game.trackshooter.render.RenderComponent;
import me.retrodaredevil.game.trackshooter.render.WorldRenderComponent;

import java.util.*;

public class World implements Updateable, Renderable {
	private static final Vector2 temp = new Vector2();


	private final LevelGetter levelGetter;
	private Level level;

	private final List<Entity> entities = new ArrayList<>();
	private ListIterator<Entity> currentIterator = null;

	private CollisionHandler collisionHandler;
	private final Rectangle bounds;
//	private final Rectangle largeBounds = new Rectangle(); // shouldn't be referenced without getLargeBounds()

	protected RenderComponent renderComponent;

	public World(LevelGetter levelGetter, float width, float height){
		this.levelGetter = levelGetter;
		this.bounds = new Rectangle(width / -2f, height / -2f, width, height);
		this.renderComponent = new WorldRenderComponent(this);
		this.collisionHandler = new CollisionHandler();

		this.level = levelGetter.nextLevel();

	}


	@Override
	public void update(float delta, World theWorld) {
		assert theWorld == this || theWorld == null;
		if(level == null || level.isDone()){
			level = levelGetter.nextLevel();
		}

		for(currentIterator = entities.listIterator(); currentIterator.hasNext(); ){
			Entity entity = currentIterator.next();
			assert !entity.isRemoved();
			entity.update(delta, this);
			if(entity.shouldRemove(this)){
				try {
					currentIterator.remove();
				} catch(IllegalStateException ex){
					while(currentIterator.previous() != entity); // if the call to update called currentIterator.add(), this gets it back to entity

					currentIterator.remove();
					System.out.println("This code fixed adding in update and removing afterwards. Yay!");
				}
				entity.afterRemove(this);
			}
		}
		currentIterator = null;
		this.collisionHandler.update(delta, this);
		this.level.update(delta, this);
		if(level.getMode() == LevelMode.STANDBY){
			level.setMode(LevelMode.NORMAL);
		}
	}

	public Track getTrack(){
		return level.getTrack();
	}
	public Level getLevel(){
		return level;
	}

	/**
	 * Normally, it is not recommended to call this because you shouldn't need it that much.
	 * Use #addEntity() to add entities instead of this.
	 * @return A Collection of Entities
	 */
	public Collection<Entity> getEntities(){
		return entities;
	}
	public void addEntity(Entity entity){
		if(currentIterator != null){
			currentIterator.add(entity);
		} else {
			entities.add(entity);
		}
	}
	public Rectangle getBounds(){
		return bounds;
	}

//	public Rectangle getLargeBounds(){ maybe use in future?
//		largeBounds.setSize(getBounds().getWidth() * 1.5f, getBounds().getHeight() * 1.5f);
//		largeBounds.setCenter(getBounds().getCenter(temp));
//		return largeBounds;
//	}

	@Override
	public RenderComponent getRenderComponent() {
		return renderComponent;
	}

	/**
	 * A simple util method that takes a list and removes elements from the passed instance if they are removed
	 *
	 * @param entities The list of entities to remove removed entities from
	 */
	public static void updateEntityList(List<? extends Entity> entities){
		if(!entities.isEmpty()){
			for(Iterator<? extends Entity> it = entities.iterator(); it.hasNext();){
				Entity entity = it.next();
				if(entity.isRemoved()){
					it.remove();
				}
			}
		}
	}
}

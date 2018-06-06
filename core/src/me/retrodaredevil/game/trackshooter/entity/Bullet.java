package me.retrodaredevil.game.trackshooter.entity;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import me.retrodaredevil.game.trackshooter.CollisionIdentity;
import me.retrodaredevil.game.trackshooter.entity.movement.FixedVelocityMoveComponent;
import me.retrodaredevil.game.trackshooter.entity.powerup.PowerupEntity;
import me.retrodaredevil.game.trackshooter.render.ImageRenderComponent;
import me.retrodaredevil.game.trackshooter.util.CannotHitException;
import me.retrodaredevil.game.trackshooter.util.Resources;
import me.retrodaredevil.game.trackshooter.world.World;

public class Bullet extends SimpleEntity implements Entity {

	private Entity shooter;

	private Entity hitEntity = null; // right now this is just used to check if it's null. Maybe use in future

	private final float shotDistance2; // the shot distance squared
	private Vector2 startingLocation = null;

	/**
	 *
	 * @param shooter The entity that shot this bullet
	 * @param start The starting location of this bullet. It can be mutated after constructor is called
	 * @param velocity The velocity of the bullet. It cannot be mutated after the constructor is called
	 */
	public Bullet(Entity shooter, Vector2 start, Vector2 velocity, float rotation, float shotDistance, CollisionIdentity collisionIdentity){
		this.shooter = shooter;
		this.shotDistance2 = shotDistance * shotDistance;
		setHitboxSize(.25f, .25f);
		setMoveComponent(new FixedVelocityMoveComponent(this, velocity));
		setRenderComponent(new ImageRenderComponent(new Image(Resources.BULLET_TEXTURE), this, .5f, .5f));
		setLocation(start, rotation);
		canRespawn = false;
		this.collisionIdentity = collisionIdentity; // super.collisionIdentity same thing
	}

	@Override
	public void update(float delta, World world) {
		super.update(delta, world);
		if(startingLocation == null){
			startingLocation = this.getLocation();
		}
	}

	public static Bullet createFromEntity(Entity entity, float speed, float directionOffsetDegrees, float shotDistance, CollisionIdentity collisionIdentity){
		float rotation = entity.getRotation() + directionOffsetDegrees;
		Vector2 velocity = new Vector2(MathUtils.cosDeg(rotation), MathUtils.sinDeg(rotation));
		velocity.scl(speed);
		return new Bullet(entity, entity.getLocation(), velocity, rotation, shotDistance, collisionIdentity);
	}

	@Override
	public Entity getShooter(){
		return shooter;
	}

	@Override
	public void onHit(World world, Entity other) throws CannotHitException{
		if(hitEntity != null){
			throw new IllegalStateException("I hit something twice!!");
		}
		CollisionIdentity identity = other.getCollisionIdentity();
		if(other == shooter || identity == CollisionIdentity.ENEMY_PROJECTILE
				|| identity == CollisionIdentity.FRIENDLY_PROJECTILE || identity == CollisionIdentity.POWERUP){
			throw new CannotHitException(other, this);
		}
		this.hitEntity = other;
	}

	@Override
	public boolean shouldRemove(World world) {
		return super.shouldRemove(world) || hitEntity != null
				|| (this.startingLocation != null && startingLocation.dst2(getX(), getY()) > shotDistance2);
	}

	public enum ShotType{
		/** A single bullet is fired straight*/
		STRAIGHT,
		/** 3 bullets are fired, one goes straight the other two go off to the left and right respectively at the same angle*/
		TRIPLE,
		/** Multiple bullets fire in a general direction randomly */
		SHOT_GUN,
		/** Multiple shots fire in all directions evenly spaced*/
		FULL
	}
}

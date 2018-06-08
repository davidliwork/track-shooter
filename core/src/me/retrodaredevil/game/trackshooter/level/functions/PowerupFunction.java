package me.retrodaredevil.game.trackshooter.level.functions;

import me.retrodaredevil.game.trackshooter.entity.powerup.PowerupEntity;
import me.retrodaredevil.game.trackshooter.level.Level;
import me.retrodaredevil.game.trackshooter.level.LevelMode;
import me.retrodaredevil.game.trackshooter.world.World;

import java.util.Queue;

public abstract class PowerupFunction implements LevelFunction {
	private final long addAt; // 15 seconds
	private final long removeAt; // stay for 10 seconds (remove at 25 seconds)
	private PowerupEntity powerup = null;

	protected PowerupFunction(long addAt, long stayTime){
		this.addAt = addAt;
		this.removeAt = addAt + stayTime;
	}

	@Override
	public boolean update(float delta, World world, Queue<LevelFunction> functionsToAdd) {
		Level level = world.getLevel();
		long modeTime = level.getModeTime();
		if(powerup != null){
			if(powerup.isRemoved()){
				return true; // the powerup must have been eaten so we are done here
			}
			if(modeTime >= removeAt){ // remove powerup if time is up
				powerup.setToRemove();
				return true;
			}
			return false;
		}

		// powerup == null
		if(level.getMode() == LevelMode.NORMAL && modeTime > addAt){
			this.powerup = createPowerup(world);
			level.addEntity(world, powerup);
		}
		return false;
	}

	@Override
	public void levelEnd(World world) {
		// do nothing because the level will automatically remove the powerup
	}

	/**
	 * Should create the powerup object but SHOULD NOT add it to world's entities
	 * @param world The world the Fruit will be added too
	 * @return The Powerup to be added to world
	 */
	protected abstract PowerupEntity createPowerup(World world);


	@Override
	public void onModeChange(Level level, LevelMode mode, LevelMode previous) {
		if(powerup != null){
			powerup.setToRemove();
		}
	}
}

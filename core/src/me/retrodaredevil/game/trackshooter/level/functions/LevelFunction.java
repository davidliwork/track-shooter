package me.retrodaredevil.game.trackshooter.level.functions;

import me.retrodaredevil.game.trackshooter.level.Level;
import me.retrodaredevil.game.trackshooter.level.LevelMode;
import me.retrodaredevil.game.trackshooter.world.World;

import java.util.Queue;

/**
 * A useful interface utilized by SimpleLevel and that can be utilized elsewhere if needed
 * <p>
 * <p>
 * A LevelFunction can be temporary, or it can be something that goes on for the entire level. It is meant to be used
 * as something to easily change how a level works and allow for code reusability in different levels and level types
 */
public interface LevelFunction {

	/**
	 *
	 * @param functionsToAdd A Collection that if appended to will add all elements to the level's LevelFunction list
	 *                       and each of those elements will probably be called the same frame as this function.
	 * @return true if this LevelFunction done and should not be called again, false for this to keep getting called
	 */
	boolean update(float delta, World world, Queue<LevelFunction> functionsToAdd);

	/**
	 * Called when the level ends while this function is still active
	 *
	 * @param world The world where getLevel() is still the level you expect
	 */
	void levelEnd(World world);

	void onModeChange(Level level, LevelMode mode, LevelMode previous);
}

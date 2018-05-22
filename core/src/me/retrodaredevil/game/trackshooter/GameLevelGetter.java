package me.retrodaredevil.game.trackshooter;

import com.badlogic.gdx.math.Vector2;
import me.retrodaredevil.game.trackshooter.entity.enemies.Shark;
import me.retrodaredevil.game.trackshooter.entity.enemies.SharkAIController;
import me.retrodaredevil.game.trackshooter.entity.player.Player;
import me.retrodaredevil.game.trackshooter.level.EnemyLevel;
import me.retrodaredevil.game.trackshooter.level.Level;
import me.retrodaredevil.game.trackshooter.level.LevelGetter;
import me.retrodaredevil.game.trackshooter.world.Track;
import me.retrodaredevil.game.trackshooter.world.Tracks;
import me.retrodaredevil.game.trackshooter.world.World;

/**
 * This is the default level getter used all of the time
 */
public class GameLevelGetter implements LevelGetter {
	private static final Vector2 temp = new Vector2();

	private int levelNumber = 1;
	private final Track[] tracks;
	private final Player player;

	public GameLevelGetter(Player player){
		this.player = player;
		this.tracks = new Track[] {Tracks.newKingdomTrack(), Tracks.newWeirdTrack(), Tracks.newCircleTrack() };
	}

	@Override
	public Level nextLevel() {
		Level level = new EnemyLevel(levelNumber, tracks[(levelNumber - 1) % tracks.length]) {
			@Override
			protected void onStart(World world) {
				super.onStart(world);

				final Vector2[] positions = new Vector2[] { new Vector2(1, 1), new Vector2(-1, 1), new Vector2(-1, -1), new Vector2(1, -1)};
				final int amount = 4;
				final float spacing = world.getTrack().getTotalDistance() / amount;
				for(int i = 0; i < amount; i++){
					int sign = ((i % 2) * 2) - 1;
					float trackDistanceAway = sign * ((i / 2f) * spacing);
					Vector2 location = positions[i];
					float angle = 45 + 90 * i;
					Shark shark = new Shark(location, angle);
					shark.setEntityController(new SharkAIController(shark, player, trackDistanceAway, sign));
					// start in the start position
					shark.setLocation(location);
					shark.setRotation(angle);

					addEnemy(world, shark);
				}
			}
		};
		levelNumber++; // future programmers you're welcome that I put this on a separate line.
		return level;
	}
}

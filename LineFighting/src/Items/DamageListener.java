package Items;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.state.StateBasedGame;

import Fight.FightState;

public interface DamageListener {
	public abstract boolean onDamage(GameContainer container, StateBasedGame game, FightState state);
}

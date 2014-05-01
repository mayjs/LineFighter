package Items;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.state.StateBasedGame;

import Fight.FightState;
import Fight.Player;

public abstract class Item {
	protected String category;
	protected Color ringColor;

	
	
	public abstract void render(GameContainer container, StateBasedGame game, Graphics g, Player myPlayer);
	public abstract boolean update(GameContainer container, StateBasedGame game, int delta, FightState state, Player myPlayer);
	public abstract void onApply(GameContainer container, StateBasedGame game, FightState state, Player myPlayer);
	public abstract Image getImage();
	
	public String getCategory() {
		return category;
	}
	public Color getRingColor() {
		return ringColor;
	}
}
package MainGame;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import Fight.FightState;

public class Game extends StateBasedGame{

	public Game(String name) {
		super(name);
	}

	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		addState(new FightState());
	}

	public static void main(String[] args){
		AppGameContainer agc;
		try {
			agc = new AppGameContainer(new Game("Line Fighter"), 800, 600, false);
			agc.setTargetFrameRate(60);
			agc.setShowFPS(false);
			agc.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}

package Items;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.state.StateBasedGame;

import Fight.FightState;
import Fight.Player;

public class ShieldItem extends Item implements DamageListener{
	private Color shieldColor = Color.cyan;
	private boolean used = false;
	private int immunityTime = 750;
	private Shape shieldShape;
	private float width;
	
	public ShieldItem(){
		this.category = "shield";
	}
	
	@Override
	public boolean onDamage(GameContainer container, StateBasedGame game,
			FightState state) {
		if(immunityTime > 0){
			used = true;
			return false;
		}
		return true;
	}

	@Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g, Player myPlayer) {
		g.setLineWidth(width);
		g.setColor(used?shieldColor.brighter(0.1f):shieldColor);
		g.draw(shieldShape);
	}

	@Override
	public boolean update(GameContainer container, StateBasedGame game, int delta,
			FightState state, Player myPlayer) {
		shieldShape.setCenterX(myPlayer.getX());
		shieldShape.setCenterY(myPlayer.getY());
		
		if(used){
			immunityTime -= delta;
			if(delta <= 0) return true;
		}
		
		return false;
	}

	@Override
	public void onApply(GameContainer container, StateBasedGame game,
			FightState state, Player myPlayer) {
		Circle c = myPlayer.getBoundingCircle();
		shieldShape = new Circle(c.getCenterX(),c.getCenterY(),c.getRadius()+5);
	}

	@Override
	public Image getImage() {
		try{
			Image img = new Image(50,50);
			Graphics g = img.getGraphics();
			Graphics.setCurrent(g);
			g.setColor(Color.white);
			g.fill(new Circle(25, 25, 25));
			g.setColor(Color.cyan);
			g.setLineWidth(3);
			g.draw(new Circle(25,25,22));
			g.flush();
			return img;
		} catch (Exception ex){}
		return null;
	}
}

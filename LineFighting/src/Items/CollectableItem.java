package Items;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.StateBasedGame;

import Fight.FightState;
import Fight.Player;

public class CollectableItem {
	private Image image;
	private Color filter = Color.white;
	private float x, y;
	private Item item;
	private int collectTiming;
	private boolean timerEnabled=false;
	private Rectangle bounding;
		
	public CollectableItem(float x, float y, Item item) {
		super();
		this.image = item.getImage();
		this.x = x;
		this.y = y;
		this.item = item;
		createBounding();
	}

	private void createBounding(){
		bounding = new Rectangle((int)x, (int)y, image.getWidth(), image.getHeight());
	}
	
	public void render(GameContainer container, StateBasedGame game, Graphics g){
		image.draw(x, y, filter);
	}
	
	public boolean update(GameContainer container, StateBasedGame game, int delta, FightState state){
		if(timerEnabled){
			collectTiming -=delta;
			if(collectTiming <= 0){
				return true;
			}
		}
		
		for(Player p : state.getPlayers()){
			if(p.getBoundingCircle().intersects(bounding)){
				//If there might be collision start pixel perfect checking
				float[] points = p.getShape().getPoints();
				for(int i = 0; i < points.length; i+=2){
					try{
						if(image.getColor((int)(points[i]-x),(int)(points[i+1] - y)).a != 0){
							item.onApply(container, game, state, p);
							p.applyItem(item);
							return true;
						}
					} catch(Exception e){}
				}
			}	
		}
		return false;
	}
}

package Fight;

import java.util.LinkedList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.FontUtils;

import Fight.Player.InputDef;

public class FightState extends BasicGameState{
	public static final int ID = 0;
	
	Image lineLayer, fightBgLayer, topBg;
	Graphics topBgGraphics;
	Rectangle topBar,mainField;
	List<Player> players;
	List<Player> deadPlayers;
	long gameTime;
	Font timeFont,fpsFont;
	
	float markerPos, markerSpeed=0.009f, oldMarkerPos;
	
	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		for(Player p : players){
			p.update(container, game, delta);
			if(p.doCollisionCheck(lineLayer)){
				deadPlayers.add(p);
			}
		}
		for(Player d : deadPlayers){
			if(players.contains(d)){
				players.remove(d);
			}
		}
		if(players.size() > 0)
			gameTime += delta;
		oldMarkerPos = markerPos;
		markerPos += markerSpeed * delta;
	}

	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		 topBar = new Rectangle(0, 0, container.getWidth(), (float)container.getHeight()/10f);
		 mainField = new Rectangle(0,(float)container.getHeight()/10f,container.getWidth(),(float)container.getHeight()/10f*9f);
		 lineLayer = new Image((int)mainField.getWidth(), (int)mainField.getHeight());
		 lineLayer.getGraphics().setBackground(Color.transparent);
		 lineLayer.getGraphics().clear();
		 fightBgLayer = new Image((int)mainField.getWidth(), (int)mainField.getHeight());
		 topBg = new Image((int)topBar.getWidth(), (int)topBar.getHeight());
		 topBgGraphics = topBg.getGraphics();
		 
		 players = new LinkedList<Player>();
		 deadPlayers = new LinkedList<Player>();
		 
		 Polygon playerShape = new Polygon(new float[]{0,-5,0,5,10,0});
		 LinkedList<Player> initplayers = new LinkedList<Player>();
		 initplayers.add(new Player(playerShape.copy(),Color.red,InputDef.ARROWS));
		 initplayers.add(new Player(playerShape.copy(),Color.blue,InputDef.AD));
		 
		 timeFont = new TrueTypeFont(new java.awt.Font("verdana", java.awt.Font.PLAIN, 20), false);
		 fpsFont = new TrueTypeFont(new java.awt.Font("verdana", java.awt.Font.PLAIN, 12), false);
		 
		 initNewGame(initplayers);
		 
		 
//		 System.out.println("W: " + mainField.getWidth() + " H: " + mainField.getHeight());
	}
	
	public void initNewGame(List<Player> players){
		gameTime = 0;
		
		float r = Math.min(mainField.getWidth(),mainField.getHeight())/2 * 0.8f;
		float angleDist = (float) (2*Math.PI/players.size());
		float currentAngle=0;
		
		for(Player p : players){
			float angleToUse=0,x,y;
			
			if(currentAngle <= Math.PI/2){ //I. Quadrant
				x = (float) (r * Math.cos(currentAngle));
				y = -(float) (r * Math.sin(currentAngle));
			} else if(currentAngle <= Math.PI){ //II. Quadrant
				angleToUse = normAngle((float) (Math.PI - currentAngle));
				x = -(float) (r * Math.cos(angleToUse));
				y = -(float) (r * Math.sin(angleToUse));
			} else if(currentAngle <= 3*Math.PI/2){ //III. Quadrant
				angleToUse = normAngle((float) (3*Math.PI/2 - currentAngle));
				x = -(float) (r * Math.sin(angleToUse));
				y = (float) (r * Math.cos(angleToUse));
			} else { // IV. Quadrant
				angleToUse = normAngle((float) (Math.PI*2 - currentAngle));
				x = (float) (r * Math.cos(angleToUse));
				y = (float) (r * Math.sin(angleToUse));
			}
			 
			p.setPosition(x+mainField.getWidth()/2, y+mainField.getHeight()/2);
			double angle = Math.acos(x / (Math.sqrt(x*x + y*y)));
			angle += Math.PI;
			p.rotate((float)angle);
			
			currentAngle += angleDist;
		}
		
		this.players.clear();
		this.players.addAll(players);
		deadPlayers.clear();
	}

	
	private float normAngle(float angle){
		float corr = angle;
		while(corr > 2*Math.PI){
			corr -= 2*Math.PI;
		}
		while(corr < 0){
			corr += 2*Math.PI;
		}
		return corr;
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
//		Graphics.setCurrent(g);
//		g.setBackground(Color.black);
//		g.clear();
//		g.setColor(Color.green);
//		g.fillRect(mainField.getX(), mainField.getY(), mainField.getWidth(), mainField.getHeight());

		//Main Part
		g.clearWorldClip();
//		g.setWorldClip(mainField);
		g.translate(0, topBar.getHeight());
		fightBgLayer.draw();
		
		Graphics lineGraphics = lineLayer.getGraphics();
		Graphics.setCurrent(lineGraphics);
		for(Player p : players){
			p.renderLinePart(container, game, lineGraphics,(int)mainField.getWidth(),(int)mainField.getHeight());
		}
		for(Player d : deadPlayers){
			d.renderBuffers();
		}
		lineGraphics.flush();
		Graphics.setCurrent(g);
		lineLayer.draw();
		for(Player p : players){
			p.renderPlayer(container, game, g,(int)mainField.getWidth(),(int)mainField.getHeight());

		}
		g.translate(0, -topBar.getHeight());
		
		//Insert top part here
		Graphics.setCurrent(topBgGraphics);
		for(Player p : players){
			float oldY = p.getOldY()/mainField.getHeight() * topBg.getHeight();
			float y = p.getY()/mainField.getHeight() * topBg.getHeight();
			topBgGraphics.setColor(p.getColor());
			topBgGraphics.drawLine(oldMarkerPos, oldY, markerPos, y);
		}
		topBgGraphics.flush();
		
		Graphics.setCurrent(g);
		g.setWorldClip(topBar);
		g.setColor(Color.black);
		g.fill(topBar);
		topBg.draw();
		g.setColor(Color.white);
		g.setLineWidth(2);
		g.drawRect(1,0, topBar.getWidth()-2, topBar.getHeight()-2);
		fpsFont.drawString(3, 3,container.getFPS()+"");
		
		long seconds = gameTime/1000;
		long ms = gameTime - seconds*1000;
		String time = seconds + "." + ms;
		float w = g.getFont().getLineHeight();
		FontUtils.drawCenter(timeFont, time,(int) topBar.getCenterX(), (int)topBar.getCenterY()-timeFont.getLineHeight()/2, (int)w);
//		g.drawString(time, (topBar.getWidth()-w)/2, (topBar.getHeight()-h)/2);
	}

	@Override
	public void keyPressed(int key, char c) {
		if(c=='y'){
			System.out.println(players.get(0).getY());
		}
	}
	
	@Override
	public int getID() {
		return ID;
	}
}

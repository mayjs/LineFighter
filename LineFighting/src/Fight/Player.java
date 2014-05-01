package Fight;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.state.StateBasedGame;

import Items.Item;

public class Player {
	float rotationSpeed = (float)Math.PI/3000f,runningSpeed=0.09f; 
	int key_right, key_left;
	Shape shape;
	Color c;
	Image playerBufferImg, trailBuffer1,trailBuffer2;
	public static int hitDetectionAccuracy=3;
	float oldX, oldY, rotation, deltaMovement, maxDeltaMovement, lineThickness=3f;
	boolean drawPlayerMeshed;
	LinkedList<float[]> lineBuffer = new LinkedList<float[]>();
	int minBufferLength = 5;
	int cbuffer;
	Circle boundingCircle;
	HashMap<String, Item> items=new HashMap<String, Item>();
	
	public Player(Shape shape, Color c, int key_right, int key_left){
		init(shape, c, key_right, key_left);
	}
	
	private void init(Shape shape, Color c, int key_right, int key_left){
		this.key_right = key_right;
		this.key_left = key_left;
		this.shape = shape;
		this.c = c;
		oldX = shape.getCenterX();
		oldY = shape.getCenterY();
		maxDeltaMovement = (float) Math.sqrt(Math.pow(shape.getMaxX() - shape.getMinX(), 2) + Math.pow(shape.getMaxY() - shape.getMinY(), 2));
		boundingCircle = new Circle(shape.getCenterX(), shape.getCenterY(), shape.getBoundingCircleRadius());
	}
	
	public Player(Shape shape, Color c, InputDef input){
		switch (input){
		case ARROWS: init(shape,c,Input.KEY_RIGHT,Input.KEY_LEFT); break;
		case AD: init(shape, c, Input.KEY_D, Input.KEY_A); break;
		}
	}
	
	
	public enum InputDef{
		ARROWS,
		AD
	}
	
	public void update(GameContainer container, StateBasedGame game, int delta, FightState state)
			throws SlickException {
		if(container.getInput().isKeyDown(key_right)){
			rotation -= rotationSpeed*delta;
			shape = shape.transform(Transform.createRotateTransform(rotationSpeed*delta,shape.getCenterX(),shape.getCenterY()));
		} else if (container.getInput().isKeyDown(key_left)){
			rotation += rotationSpeed*delta;
			shape = shape.transform(Transform.createRotateTransform(-rotationSpeed*delta,shape.getCenterX(),shape.getCenterY()));
		}
		
		rotation = normAngle(rotation);
		float angleToUse=0,vx,vy;
		
		if(rotation <= Math.PI/2){ //I. Quadrant
			vx = (float) (runningSpeed * Math.cos(rotation));
			vy = -(float) (runningSpeed * Math.sin(rotation));
		} else if(rotation <= Math.PI){ //II. Quadrant
			angleToUse = normAngle((float) (Math.PI - rotation));
			vx = -(float) (runningSpeed * Math.cos(angleToUse));
			vy = -(float) (runningSpeed * Math.sin(angleToUse));
		} else if(rotation <= 3*Math.PI/2){ //III. Quadrant
			angleToUse = normAngle((float) (3*Math.PI/2 - rotation));
			vx = -(float) (runningSpeed * Math.sin(angleToUse));
			vy = (float) (runningSpeed * Math.cos(angleToUse));
		} else { // IV. Quadrant
			angleToUse = normAngle((float) (Math.PI*2 - rotation));
			vx = (float) (runningSpeed * Math.cos(angleToUse));
			vy = (float) (runningSpeed * Math.sin(angleToUse));
		}
		
		oldX = shape.getCenterX();
		oldY = shape.getCenterY();
		
		shape.setCenterX(oldX+vx*delta);
		shape.setCenterY(oldY+vy*delta);
	
		deltaMovement += runningSpeed*delta;
		
		boundingCircle.setCenterX(shape.getCenterX());
		boundingCircle.setCenterY(shape.getCenterY());
		
		for(String s : items.keySet()){
			Item i = items.get(s);
			if(i != null){
				if(i.update(container, game, delta, state, this)){
					items.put(s,null);
				}
			}
		}
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
	
	boolean out=true;
	
	public void renderLinePart(GameContainer container, StateBasedGame game, Graphics g,int width,int height)
			throws SlickException {
		if(trailBuffer1==null) trailBuffer1 = new Image(width,height);
		if(trailBuffer2==null) trailBuffer2 = new Image(width,height);
		if(out){
			System.out.println("IW: " + trailBuffer1.getWidth() + " IH " + trailBuffer1.getHeight());
			out = false;
		}
		
		
		Graphics.setCurrent(g);
		if(deltaMovement > maxDeltaMovement){
			deltaMovement = 0;
			Image releaseBuffer = cbuffer==1?trailBuffer1:trailBuffer2;
			releaseBuffer.draw();
			Graphics trailGraphics = releaseBuffer.getGraphics();
			trailGraphics.setBackground(Color.transparent);
			trailGraphics.clear();
			trailGraphics.flush();
			cbuffer = cbuffer==1?0:1;
		}
		
		Graphics trailGraphics = (cbuffer==0?trailBuffer1:trailBuffer2).getGraphics();
		
		Graphics.setCurrent(trailGraphics);
		trailGraphics.setLineWidth(lineThickness);
		trailGraphics.setColor(c);
		trailGraphics.drawLine(oldX, oldY, shape.getCenterX(), shape.getCenterY());
		trailGraphics.flush();
		
//		lineBuffer.addLast(new float[]{oldX,oldY,shape.getCenterX(),shape.getCenterY()});
//		Graphics.setCurrent(g);
//		while(lineBuffer.size() > minBufferLength){
//			drawLine(lineBuffer.removeFirst(), g, c);
//		}
	}
	
//	private void drawLine(float[] points, Graphics g, Color color){
//		g.setLineWidth(lineThickness);
//		g.setColor(color);
//		g.drawLine(points[0], points[1], points[2], points[3]);
//	}
	
	public void renderPlayer(GameContainer container, StateBasedGame game, Graphics g,int width,int height)
			throws SlickException {
		
		trailBuffer1.draw();
		trailBuffer2.draw();
		
		g.setColor(c);
		if(drawPlayerMeshed){
			g.draw(shape);
		} else {
			g.fill(shape);
		}
//		g.draw(boundingCircle);
	}
	
	public void renderItems(GameContainer container, StateBasedGame game, Graphics g,int width,int height){
		for(Item i : items.values()){
			if(i!=null){
				i.render(container, game, g, this);
			}
		}
	}
	
	public boolean doCollisionCheck(Image lines){	
		float[] points = shape.getPoints();
		for(int i = 0; i< points.length; i+=2){
			try{
			if(lines.getColor((int) points[i], (int)points[i+1]).a != 0){
				return true;
			}}catch(Exception e){}
		}
		return false;
	}
	
	public void renderBuffers(){
		trailBuffer1.draw();
		trailBuffer2.draw();
	}
	
	public float getRotation() {
		return rotation;
	}

	public void dispose(){
		try {
			trailBuffer1.destroy();
			trailBuffer2.destroy();
			playerBufferImg.destroy();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	
	public void setPosition(float x, float y){
		shape.setCenterX(x);
		shape.setCenterY(y);
		oldX = x; oldY = y;
	}
	
	public void rotate(float angle){
		rotation -= angle;
		shape = shape.transform(Transform.createRotateTransform(angle,shape.getCenterX(),shape.getCenterY()));
		
		rotation = normAngle(rotation);
	}
	
	public float getX(){
		return shape.getCenterX();
	}
	
	public float getY(){
		return shape.getCenterY();
	}
	
	public float getOldY(){
		return oldY;
	}
	
	public Color getColor() {
		return c;
	}
	
	public Circle getBoundingCircle(){
		return boundingCircle;
	}

	public Shape getShape() {
		return shape;
	}
	
	public void applyItem(Item i){
		items.put(i.getCategory(), i);
	}
	
	public Collection<Item> getItems(){
		return items.values();
	}
	
	public Collection<Item> getFilteredItems(Class<?>... filters){
		LinkedList<Item> result = new LinkedList<>();
		
		for(Class<?> filter : filters){
			for(Item item : items.values()){
				if(filter.isInstance(item)){
					result.add(item);
				}
			}
		}
		
		return result;
	}
}

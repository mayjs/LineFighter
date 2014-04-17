package Fight;

import java.util.LinkedList;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.state.StateBasedGame;

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
//		maxDeltaMovement *= 5;
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
	
	public void update(GameContainer container, StateBasedGame game, int delta)
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
//		for(float[] line : lineBuffer){
//			drawLine(line, g, c);
//		}
		
		trailBuffer1.draw();
		trailBuffer2.draw();
		
		g.setColor(c);
		if(drawPlayerMeshed){
			g.draw(shape);
		} else {
			g.fill(shape);
		}
	}
	
	public boolean doCollisionCheck(Image lines){
//		if(playerBufferImg == null)
//			try {
//				playerBufferImg = new Image(lines.getWidth(),lines.getHeight());
//			} catch (SlickException e1) {
//				e1.printStackTrace();
//			}
//		Graphics g = null;
//		try {
//			g = playerBufferImg.getGraphics();
//		} catch (SlickException e) {
//			e.printStackTrace();
//		}
//		Graphics.setCurrent(g);
//		g.setDrawMode(Graphics.MODE_NORMAL);
//		g.setBackground(Color.transparent);
//		g.clear();
//		g.fill(shape);
//		
//		g.flush();
//		
		float[] points = shape.getPoints();
		for(int i = 0; i< points.length; i+=2){
			try{
			if(lines.getColor((int) points[i], (int)points[i+1]).a != 0){
				return true;
			}}catch(Exception e){}
		}
		
//		for(int x = (int)shape.getMinX(); x <= shape.getMaxX(); x += hitDetectionAccuracy){
//			for(int y = (int)shape.getMinY(); y <= shape.getMaxY(); y += hitDetectionAccuracy){
//				try{
//				if(playerBufferImg.getColor(x, y).a != 0 && lines.getColor(x, y).a != 0){
//					try {
//						Graphics lg = lines.getGraphics();
//						Graphics.setCurrent(lg);
//						lg.setColor(Color.green);
//						lg.fill(shape);
//						lg.setColor(Color.cyan);
//						lg.fillRect(x-1, y-1, 3, 3);
//						lg.flush();
//					} catch (SlickException e) {
//						e.printStackTrace();
//					}
//					return true;					
//				}}catch(Exception ex){}
//			}
//		}
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
}

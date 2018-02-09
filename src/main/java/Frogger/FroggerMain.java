package Frogger;

import java.util.Map;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.EntityView;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.RenderLayer;
import com.almasb.fxgl.entity.animation.AnimationBuilder;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxgl.entity.component.ViewComponent;
import com.almasb.fxgl.entity.control.ProjectileControl;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsWorld;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.texture.Texture;
import javafx.scene.effect.Effect;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class FroggerMain extends GameApplication{
	GameEntity frog;
	GameEntity leftLog, rightLog, leftHalfLog, rightHalfLog;
	GameEntity leftLog1, rightLog1, leftHalfLog1, rightHalfLog1; //log ones keep track of the logs just about to exit the screen for collision purposes
	int x = 0;
	Texture a;
	String[] pics = {"frogIdle.png", "frogJump.png"};
	Text text;
	@Override
	protected void initSettings(GameSettings settings) {
		// TODO Auto-generated method stub
		settings.setIntroEnabled(true);
		settings.setMenuEnabled(false);
		settings.setProfilingEnabled(false);
		settings.setCloseConfirmation(false);
		settings.setTitle("Frogger");
		settings.setVersion("");
		settings.setHeight((int)941/2);
		settings.setWidth((int)(1722/2)-1);
	}
	
	
	protected void splat(){
		Texture splat = getAssetLoader().loadTexture("splatSpriteSheet.png").toAnimatedTexture(3, Duration.millis(450));
		EntityView splatting = new EntityView(splat);
		splatting.setScaleX(1.5);
		splatting.setScaleY(1.5);
		splatting.setTranslateX(frog.getX()+frog.getWidth()*3.2);
		splatting.setTranslateY(frog.getY()+frog.getHeight()*3.2);
		getGameScene().addGameView(splatting);
		getMasterTimer().runOnceAfter(() -> {
			getGameScene().removeGameView(splatting);
			deadFrog();
			initPlayer();
		}, Duration.millis(450));
	}
	
	protected void sink(){
		Texture animatedSinking = getAssetLoader().loadTexture("sinkingSpriteSheet.png").toAnimatedTexture(5, Duration.millis(450));
		EntityView sink = new EntityView(animatedSinking);
		sink.setScaleX(1.5);
		sink.setScaleY(1.5);
		sink.setTranslateX(frog.getX()+frog.getWidth()*3.2);
		sink.setTranslateY(frog.getY());
		getGameScene().addGameView(sink);
		
		getMasterTimer().runOnceAfter(() -> {
			getGameScene().removeGameView(sink);
			deadFrog();
			initPlayer();
		}, Duration.millis(450));
		
			
	}
	
	protected void initWater(){
		Entities.builder()
		.at(0,26)
		//.viewFromNode(new Rectangle(getWidth(), 140, Color.BLACK))
		.bbox(new HitBox("WATERBODY", BoundingShape.box(getWidth(), 140)))
		.with(new CollidableComponent(true))
		.type(FroggerTypes.WATER)
		.renderLayer(RenderLayer.TOP)
		.buildAndAttach(getGameWorld());
	}
	@Override
	protected void initGame(){
		leftLog1 = new GameEntity();
		leftHalfLog1 = new GameEntity();
		rightLog1 = new GameEntity();
		rightHalfLog1 = new GameEntity();
		getGameWorld().setEntityFactory(new FroggerFactory());
		initPlayer();
		initPointHitBox();
		initWater();
		getGameWorld().spawn("RightCar");
		getGameWorld().spawn("LeftCar");
		leftLog = (GameEntity)getGameWorld().spawn("LeftLog", getWidth() + 100,115);
		rightLog = (GameEntity)getGameWorld().spawn("RightLog",-100,35);
		rightHalfLog = (GameEntity)getGameWorld().spawn("RightHalfLog", -100, 75);
		leftHalfLog = (GameEntity)getGameWorld().spawn("LeftHalfLog", getWidth()+100, -5);
		
		
		if(frog.getX() < 0 || frog.getRightX() > getWidth()){
			deadFrog();
		}
		
		getMasterTimer().runAtInterval(() -> {
			if(leftHalfLog.getX() < getWidth()/2){
				leftHalfLog1 = leftHalfLog;
				leftHalfLog = (GameEntity)getGameWorld().spawn("LeftHalfLog",getWidth() + 100,-5);
				}
		}, Duration.millis(FXGLMath.random(250, 1250)));
		getMasterTimer().runAtInterval(() -> {
			if(rightHalfLog.getX() > getWidth()/2){
				rightHalfLog1 = rightHalfLog;
				rightHalfLog = (GameEntity)getGameWorld().spawn("RightHalfLog",-100,75);
				}
		}, Duration.millis(FXGLMath.random(250, 1250)));
		getMasterTimer().runAtInterval(() -> {
			if(leftLog.getX() < getWidth()/2){
				leftLog1 = leftLog;
				leftLog = (GameEntity)getGameWorld().spawn("LeftLog",getWidth() + 100,115);
				}
		}, Duration.millis(FXGLMath.random(250, 1250)));
	
		getMasterTimer().runAtInterval(() -> {
			if(rightLog.getX() > getWidth()/2){
				rightLog1 = rightLog;
				rightLog = (GameEntity)getGameWorld().spawn("RightLog",-100,35);
				}
		}, Duration.millis(FXGLMath.random(250, 1250)));
		getMasterTimer().runAtInterval(() -> {
			getGameWorld().spawn("RightCar");
			getGameWorld().spawn("LeftCar");
		}, Duration.millis(750));
	}
	
	@Override
	protected void initGameVars(Map<String, Object> vars){
		vars.put("Score", 0);
		vars.put("Deaths", 0);
	}
	
	@Override
	protected void initPhysics(){
		PhysicsWorld physicsWorld = getPhysicsWorld();
		physicsWorld.addCollisionHandler(new CollisionHandler(FroggerTypes.FROG, FroggerTypes.POINTBOUND){
			@Override
			protected void onCollisionEnd(Entity Frog, Entity PointBound){
				a = getAssetLoader().loadTexture("frogIdle.png").toGrayscale();
				a.setScaleX(.5);
				a.setScaleY(.5);
			getGameState().increment("Score", +1);
			EntityView winFrog = new EntityView(a);
			winFrog.setTranslateX(frog.getX());
			winFrog.setTranslateY(frog.getY()-7);
			getGameScene().addGameView(winFrog);
			Frog.removeFromWorld();
			initPlayer();
			}
		});	
		physicsWorld.addCollisionHandler(new CollisionHandler(FroggerTypes.FROG, FroggerTypes.WATER){
			@Override
			protected void onCollision(Entity Frog, Entity Water){
				if(!(frog.isColliding(leftLog) || frog.isColliding(leftLog1) || (frog.isColliding(leftLog) && frog.isColliding(leftLog1)) || frog.isColliding(rightLog) || frog.isColliding(rightLog1) || (frog.isColliding(rightLog) && frog.isColliding(rightLog1)) || frog.isColliding(rightHalfLog) || frog.isColliding(rightHalfLog1) || (frog.isColliding(rightHalfLog) && frog.isColliding(rightHalfLog1)) || frog.isColliding(leftHalfLog) || frog.isColliding(leftHalfLog1) || (frog.isColliding(leftHalfLog) && frog.isColliding(leftHalfLog1)))){
				sink();
				Frog.removeFromWorld();
				}
			}
		});
		physicsWorld.addCollisionHandler(new CollisionHandler(FroggerTypes.FROG, FroggerTypes.CAR){
			@Override
			protected void onCollision(Entity Frog, Entity Car){
				splat();
				Frog.removeFromWorld();
				
			}
		});
		physicsWorld.addCollisionHandler(new CollisionHandler(FroggerTypes.FROG, FroggerTypes.LOG){
			@Override
			protected void onCollisionBegin(Entity Frog, Entity Log){
				getMasterTimer().runOnceAfter(() ->{
					((GameEntity) Log).setRenderLayer(RenderLayer.BACKGROUND);
					((GameEntity) Frog).setY(Log.getComponent(PositionComponent.class).getY());

				}, Duration.millis(1));
				getMasterTimer().runAtInterval(() ->{
					((GameEntity) Frog).setX(Log.getComponent(PositionComponent.class).getX());
				}, Duration.millis(1));
				
			}
		});
	}
	protected void deadFrog(){
		getGameState().increment("Deaths", +1);
		a = getAssetLoader().loadTexture("frogIdle.png").multiplyColor(Color.RED);
		a.setScaleX(.5);
		a.setScaleY(.5);
		EntityView deadFrog = new EntityView(a);
		deadFrog.setTranslateX(frog.getX());
		deadFrog.setTranslateY(frog.getY());
		getGameScene().addGameView(deadFrog);
	}
	@Override
	protected void initInput(){
		Input input = getInput();
		input.addAction(new UserAction("Move Up") {
	        @Override
	        protected void onAction() {
	        	x++;
	        	input.mockKeyRelease(KeyCode.UP);
	        	if(frog.getRotation() == 180){
	        		frog.rotateBy(180);
	        		frog.setRotation(0); //0 is the top (0,1) of the unit circle
	        	}
	        	if(frog.getRotation() == 90){
	        		frog.rotateBy(-90);
	        		frog.setRotation(0);
	        	}
	        	if(frog.getRotation() == -90){
	        		frog.rotateBy(90);
	        		frog.setRotation(0);
	        	}
	        	frog.setViewFromTexture(pics[x%2]);
	            frog.setY(frog.getY()-30);
	            getMasterTimer().runOnceAfter(() -> { 
	            	x++;
	            	frog.setViewFromTexture(pics[x%2]);
	            }, Duration.millis(200));
	        	
	        }
	    }, KeyCode.UP);
		input.addAction(new UserAction("Move Down") {
	        @Override
	        protected void onAction() {
	        	x++;
	        	input.mockKeyRelease(KeyCode.DOWN);
	        if(frog.getRotation() != 180){
	        	frog.rotateBy(180);
	        	frog.setRotation(180);
	        }
	            frog.setY(frog.getY()+30);
	            frog.setViewFromTexture(pics[x%2]);
	            getMasterTimer().runOnceAfter(() -> { 
	            	x++;
	            	frog.setViewFromTexture(pics[x%2]);
	            }, Duration.millis(200));
	        }
	    }, KeyCode.DOWN);
		input.addAction(new UserAction("Move Right") {
	        @Override
	        protected void onAction() {
	        	x++;
	        	input.mockKeyRelease(KeyCode.RIGHT);
	        	if(frog.getRotation() != 90){
	        		frog.rotateBy(90);
	        		frog.setRotation(90);
	        	}
	            frog.setX(frog.getX()+30);
	            frog.setViewFromTexture(pics[x%2]);
	            getMasterTimer().runOnceAfter(() -> { 
	            	x++;
	            	frog.setViewFromTexture(pics[x%2]);
	            }, Duration.millis(200));
	        }
	    }, KeyCode.RIGHT);
		input.addAction(new UserAction("Move Left") {
	        @Override
	        protected void onAction() {
	        	x++;
	        	input.mockKeyRelease(KeyCode.LEFT);
	        	if(frog.getRotation() != -90){
	        		frog.rotateBy(-90);
	        		frog.setRotation(-90);
	        	}
	            frog.setX(frog.getX()-30);
	            frog.setViewFromTexture(pics[x%2]);
	            getMasterTimer().runOnceAfter(() -> { 
	            	x++;
	            	frog.setViewFromTexture(pics[x%2]);
	            }, Duration.millis(200));
	        }
	    }, KeyCode.LEFT);
	}
	protected void initPlayer(){
		a = getAssetLoader().loadTexture("frogIdle.png");
		EntityView view = new EntityView(a);
		frog = Entities.builder()
				.viewFromNode(view)
				.bbox(new HitBox("playerBody", BoundingShape.circle(5)))
				.at(getWidth()/8, getHeight()-60)
				.renderLayer(RenderLayer.TOP)
				.type(FroggerTypes.FROG)
				.with(new CollidableComponent(true))
				.buildAndAttach(getGameWorld());
		frog.getView().setScaleX(.5);
		frog.getView().setScaleY(.5);
	}
	
	protected void initPointHitBox(){
		Entities.builder()
		.bbox(new HitBox("POINT", BoundingShape.box(getWidth(), 1)))
		.at(0,0)
		.with(new CollidableComponent(true))
		.renderLayer(RenderLayer.TOP)
		.type(FroggerTypes.POINTBOUND)
		.buildAndAttach(getGameWorld());
	}
	
	@Override
	protected void initUI(){
		Texture view = getAssetLoader().loadTexture("FroggerBackground.jpg");
		view.setScaleX(.5);
		view.setScaleY(.5);
		view.setTranslateX(-431);
		view.setTranslateY(-235);
		EntityView background = new EntityView(view);
		background.setRenderLayer(RenderLayer.BACKGROUND);
		getGameScene().addGameView(background);
		EntityView DeathsPic = new EntityView(getAssetLoader().loadTexture("frogIdle.png").multiplyColor(Color.RED));
		DeathsPic.setScaleX(.5);
		DeathsPic.setScaleY(.5);
		DeathsPic.setTranslateX(getWidth()-200);
		DeathsPic.setTranslateY(getHeight()-60);
		DeathsPic.setRenderLayer(RenderLayer.TOP);
		getGameScene().addGameView(DeathsPic);
		text = new Text();
		text.setFont(Font.font ("Arial Bold", 20));
		text.setFill(Color.BLACK);
		text.setTranslateX(getWidth()-105);
		text.setTranslateY(getHeight()-5);
		text.textProperty().bind(getGameState().intProperty("Deaths").asString());
		Text text1 = new Text(" = ");
		text1.setFont(Font.font ("Arial Bold", 20));
		text1.setFill(Color.BLACK);
		text1.setTranslateX(getWidth()-135);
		text1.setTranslateY(getHeight()-5);
		getGameScene().addUINode(text);
		getGameScene().addUINode(text1);
		Text text2 = new Text();
		text2.setFont(Font.font ("Arial Bold", 40));
		text2.textProperty().bind(getGameState().intProperty("Score").asString());
		text2.setFill(Color.WHITE);
		text2.setTranslateX(getWidth()/40);
		text2.setTranslateY(getHeight()-5);
		getGameScene().addUINode(text2);
		
		
	}
	
	public static void main(String[] args){
		launch(args);
	}

}

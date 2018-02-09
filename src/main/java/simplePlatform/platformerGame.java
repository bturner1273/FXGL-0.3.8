package simplePlatform;

import com.almasb.fxgl.app.ApplicationMode;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.EntityView;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.RenderLayer;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;

import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;

public class platformerGame extends GameApplication {
GameEntity player;
PhysicsComponent physics;
PhysicsComponent groundPhysics;
	@Override
	protected void initSettings(GameSettings settings) {
		// TODO Auto-generated method stub
		settings.setWidth(1000);
		settings.setHeight(500);
		settings.setIntroEnabled(false);
		settings.setMenuEnabled(false);
		settings.setProfilingEnabled(false);
		settings.setApplicationMode(ApplicationMode.DEBUG);
		settings.setCloseConfirmation(false);	
		settings.setTitle("BasicPlatformer");
		settings.setVersion("");
	}
	
	@Override 
	protected void initGame(){
		getPhysicsWorld().setGravity(0, 7);
		initPlayer();
		initGroundHitBox();
		makePlatformTile(200,350,.1f, Color.CHOCOLATE);
		makePlatformTile(300,250,.1f, Color.CHOCOLATE);
		makePlatformTile(400,250,.1f, Color.CHOCOLATE);
		makePlatformTile(600,400,1f, Color.AQUA);
	}
	
	protected void makePlatformTile(int x, int y, float restitution, Color color){
		PhysicsComponent a = new PhysicsComponent();
		FixtureDef fd = new FixtureDef();
		fd.setDensity(100f);
		fd.setRestitution(restitution);
		fd.setFriction(.3f);
		a.setFixtureDef(fd);
		a.setBodyType(BodyType.STATIC);
		GameEntity tile = Entities.builder()
				.at(x, y)
				.with(a)
				.viewFromNodeWithBBox(new Rectangle(100,10,color))
				.buildAndAttach(getGameWorld());
	}
	
	@Override
	protected void initInput(){
		final int jump = -250;
	    final Input input = getInput(); // get input service
	    input.addAction(new UserAction("Move Up") {
	        @Override
	        protected void onAction() {
	        	if(player.getBottomY() >= getHeight()-70 || physics.getVelocityY() < 10){
	        	input.mockKeyRelease(KeyCode.UP);
	            physics.setVelocityY(jump);
	        	}
	        }
	    }, KeyCode.UP);
	    input.addAction(new UserAction("Move Right") {
	        @Override
	        protected void onAction() {
	        	player.setViewFromTexture("static.png");
	            physics.setVelocityX(100);
	        }
	    }, KeyCode.RIGHT);
	    input.addAction(new UserAction("Move Left") {
	        @Override
	        protected void onAction() {
	        	player.setViewFromTexture("staticLeft.png");
	        	physics.setVelocityX(-100);
	        }
	    }, KeyCode.LEFT);
	}
	protected void initGroundHitBox(){
		groundPhysics = new PhysicsComponent();
		groundPhysics.setBodyType(BodyType.STATIC);
		FixtureDef fd = new FixtureDef();
		fd.setDensity(1f);
		fd.setRestitution(.0001f);
		fd.setFriction(.4f);
		groundPhysics.setFixtureDef(fd);
		GameEntity Ground = Entities.builder()
				//.viewFromNode(new Rectangle(100,100, Color.BLACK))
				.bbox(new HitBox("BODY", BoundingShape.box(1000, 100)))
				.at(0,getHeight()-60)
				.renderLayer(RenderLayer.TOP)
				.with(groundPhysics)
				.buildAndAttach(getGameWorld());
	}
	protected void initPlayer(){
		physics = new PhysicsComponent();
		physics.setBodyType(BodyType.DYNAMIC);

		// these are direct jbox2d objects, so we don't actually introduce new API
		FixtureDef fd = new FixtureDef();
		fd.setDensity(1f);
		fd.setRestitution(.001f);
		fd.setFriction(1.25f);
		physics.setFixtureDef(fd);

	        Texture view = getAssetLoader().loadTexture("static.png");
	                //.toAnimatedTexture(2, Duration.seconds(0.5));

	        	player = Entities.builder()
	                .at(50, 100)
	                .bbox(new HitBox("BODY", BoundingShape.box(41, 59)))
	                .viewFromNode(view)
	                .with(new CollidableComponent(true))
	                .with(physics)
	                .renderLayer(RenderLayer.TOP)
	                .buildAndAttach(getGameWorld());
	}
		
	
	
	@Override
	protected void initUI(){
		//getGameScene().setCursor("cursor.png", new Point2D(-1,1));
		Texture background = getAssetLoader().loadTexture("full-background.png");
		EntityView view = new EntityView(background);
		view.setRenderLayer(RenderLayer.BACKGROUND);
		getGameScene().addGameView(view);
		
		
	}
	
	public static void main(String[] args){
		launch(args);
	}

}

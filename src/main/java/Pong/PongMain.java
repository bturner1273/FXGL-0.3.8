package Pong;

import java.util.Map;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.EntityView;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.RenderLayer;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.PhysicsWorld;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.texture.Texture;
import BounceAround.BounceAroundTypes;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.util.Duration;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class PongMain extends GameApplication{
	GameEntity paddle1, paddle2;
	GameEntity ball;
	PhysicsComponent physics;

	@Override
	protected void initSettings(GameSettings settings) {
		// TODO Auto-generated method stub
		settings.setIntroEnabled(true);
		settings.setMenuEnabled(false);
		settings.setProfilingEnabled(false);
		settings.setCloseConfirmation(false);
		settings.setTitle("AtariStylePong");
		settings.setVersion("");
	}
	
	
	@Override
	protected void initPhysics(){
		PhysicsWorld physicsWorld = getPhysicsWorld();
		physicsWorld.addCollisionHandler(new CollisionHandler(PongTypes.BALL, PongTypes.PADDLE){
			@Override
			protected void onCollisionBegin(Entity ball, Entity paddle){
			ball.getComponent(PhysicsComponent.class).setVelocityY(ball.getComponent(PhysicsComponent.class).getVelocityY()*-1);
			ball.getComponent(PhysicsComponent.class).setVelocityX(ball.getComponent(PhysicsComponent.class).getVelocityX()*1.1);
			}
		});
		physicsWorld.addCollisionHandler(new CollisionHandler(PongTypes.BALL, PongTypes.SIDEWALL){
			@Override
			protected void onCollisionBegin(Entity ball, Entity bound){
			ball.getComponent(PhysicsComponent.class).setVelocityX(ball.getComponent(PhysicsComponent.class).getVelocityX()*-1);
			}
		});
		physicsWorld.addCollisionHandler(new CollisionHandler(PongTypes.BALL, PongTypes.BOTTOMWALL){
			@Override
			protected void onCollisionBegin(Entity ball, Entity bound){
				getGameState().increment("Score2", +1);
				ball.removeFromWorld();
				initBall();
			}
		});
		physicsWorld.addCollisionHandler(new CollisionHandler(PongTypes.BALL, PongTypes.TOPWALL){
			@Override
			protected void onCollisionBegin(Entity ball, Entity bound){
				getGameState().increment("Score1", +1);
				ball.removeFromWorld();
				initBall();			
				}
		});
	}
	
	protected void initBall(){
		int radius = 20;
		physics = new PhysicsComponent();
		physics.setBodyType(BodyType.KINEMATIC);
		FixtureDef fd = new FixtureDef();
		fd.setRestitution(1f);
		physics.setFixtureDef(fd);
		
		ball = Entities.builder()
				.viewFromNodeWithBBox(new Circle(radius, Color.RED))
				.with(new CollidableComponent(true))
				.type(PongTypes.BALL)
				.at(getWidth()/2-radius, getHeight()/2-radius)
				.with(physics)
				.renderLayer(RenderLayer.TOP)
				.buildAndAttach(getGameWorld());
		
		physics.setVelocityX(FXGLMath.random(-45*4, 45*4));
		physics.setVelocityY(FXGLMath.random(-500, 500));
	}
	
	@Override
	protected void initGameVars(Map<String, Object> vars){
		vars.put("Score1", 0);
		vars.put("Score2", 0);
	}
	
	protected void initScreenBounds(){
		GameEntity left = Entities.builder()
				.at(0,0)
				.type(PongTypes.SIDEWALL)
				.with(new CollidableComponent(true))
				.viewFromNode(new Rectangle(5, getHeight(), Color.RED))
				.bbox(new HitBox("BODY", BoundingShape.box(1, getHeight())))
				.buildAndAttach(getGameWorld());
		
		GameEntity right = Entities.builder()
				.at(getWidth()-5, 0)
				.type(PongTypes.SIDEWALL)
				.with(new CollidableComponent(true))
				.viewFromNode(new Rectangle(5, getHeight(), Color.RED))
				.bbox(new HitBox("BODY", BoundingShape.box(1, getHeight())))
				.buildAndAttach(getGameWorld());
		GameEntity top = Entities.builder()
				.at(0, 0)
				.type(PongTypes.TOPWALL)
				.with(new CollidableComponent(true))
				.viewFromNode(new Rectangle(getWidth(), 5, Color.RED))
				.bbox(new HitBox("BODY", BoundingShape.box(getWidth(), 1)))
				.buildAndAttach(getGameWorld());
		
		GameEntity bottom = Entities.builder()
				.at(0, getHeight()-5)
				.type(PongTypes.BOTTOMWALL)
				.with(new CollidableComponent(true))
				.viewFromNode(new Rectangle(getWidth(), 5, Color.RED))
				.bbox(new HitBox("BODY", BoundingShape.box(getWidth(), getHeight())))
				.buildAndAttach(getGameWorld());
	}
	
	@Override
	protected void initInput(){
		 final Input input = getInput(); // get input service
		    input.addAction(new UserAction("MOVE P1 RIGHT") {
		        @Override
		        protected void onAction() {
		        	if(paddle1.getRightX() < getWidth()) paddle1.translateX(10);
		        }
		    }, KeyCode.RIGHT);
		    input.addAction(new UserAction("MOVE P1 LEFT") {
		        @Override
		        protected void onAction() {
		        	if(paddle1.getX() > 0) paddle1.translateX(-10);
		        }
		    }, KeyCode.LEFT);
		    input.addAction(new UserAction("MOVE P2 RIGHT") {
		        @Override
		        protected void onAction() {
		           if(paddle2.getRightX() < getWidth()) paddle2.translateX(10);
		        }
		    }, KeyCode.D);
		    input.addAction(new UserAction("MOVE P2 LEFT") {
		        @Override
		        protected void onAction() {
		           if(paddle2.getX() > 0) paddle2.translateX(-10);
		        }
		    }, KeyCode.A);
	}
	
	@Override
	protected void initGame(){
		int widthFromEdge = 40;
		paddle1 = initPaddle(getWidth()/2, getHeight()-20 - widthFromEdge);
		getGameWorld().addEntities(paddle1);
		paddle2 = initPaddle(getWidth()/2, widthFromEdge);
		getGameWorld().addEntities(paddle2);
		initBall();
		initScreenBounds();
		
		getMasterTimer().runAtInterval(() -> {
			if(physics.getVelocityY() < 0){
				physics.setVelocityY(physics.getVelocityY() - 5);
			}else{
				physics.setVelocityY(physics.getVelocityY() + 5);
			}
		}, Duration.millis(250));
	}
	
	protected GameEntity initPaddle(int x, int y){
		int width = 120;
		return Entities.builder()
		.type(PongTypes.PADDLE)
		.viewFromNodeWithBBox(new Rectangle(width,20,Color.WHITE))
		.at(x-width/2,y)
		.renderLayer(RenderLayer.TOP)
		.with(new CollidableComponent(true))
		.build();
	}
	
	
	@Override
	protected void initUI(){
		Texture black = getAssetLoader().loadTexture("black.jpg");
		EntityView background = new EntityView(black);
		background.setRenderLayer(RenderLayer.BACKGROUND);
		getGameScene().addGameView(background);
		Text text = new Text();
		Text text1 = new Text();
		text.setFont(Font.font ("Arial Bold", 40));
		text.textProperty().bind(getGameState().intProperty("Score1").asString());
		text.setFill(Color.RED);
		text.setTranslateX(50);
		text.setTranslateY(getHeight()-50);
		getGameScene().addUINode(text);
		text1.setFont(Font.font ("Arial Bold", 40));
		text1.textProperty().bind(getGameState().intProperty("Score2").asString());
		text1.setFill(Color.RED);
		text1.setTranslateX(getWidth() - 50);
		text1.setTranslateY(50);
		getGameScene().addUINode(text1);
	}
	
	
	public static void main(String[] args){
		launch(args);
	}

}

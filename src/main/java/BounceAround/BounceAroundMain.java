package BounceAround;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.entity.control.ProjectileControl;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.PhysicsWorld;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.texture.AnimationChannel;

import javafx.scene.shape.*;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

public class BounceAroundMain extends GameApplication{
	GameEntity player;
	PhysicsComponent physics;
	@Override
	protected void initSettings(GameSettings settings) {
		// TODO Auto-generated method stub
		settings.setIntroEnabled(true);
		settings.setMenuEnabled(false);
		settings.setCloseConfirmation(false);
		settings.setProfilingEnabled(false);
		settings.setTitle("RandomBounce");
		settings.setVersion("");
	}
	@Override
	protected void initGame(){
		//initPlayer();  this makes two balls appear
		initPlayer();
		initPlayer();
		initScreenBounds();
	}
	@Override
	protected void initPhysics(){
		PhysicsWorld physicsWorld = getPhysicsWorld();
		physicsWorld.addCollisionHandler(new CollisionHandler(BounceAroundTypes.BALL, BounceAroundTypes.TOP){
			@Override
			protected void onCollisionBegin(Entity ball, Entity bound){
			ball.getComponent(PhysicsComponent.class).setVelocityY(ball.getComponent(PhysicsComponent.class).getVelocityY()*-1);
			}
			
			});	
		physicsWorld.addCollisionHandler(new CollisionHandler(BounceAroundTypes.BALL, BounceAroundTypes.BOTTOM){
			@Override
			protected void onCollisionBegin(Entity ball, Entity bound){
			ball.getComponent(PhysicsComponent.class).setVelocityY(ball.getComponent(PhysicsComponent.class).getVelocityY()*-1);
			}
			
			});	
		physicsWorld.addCollisionHandler(new CollisionHandler(BounceAroundTypes.BALL, BounceAroundTypes.LEFT){
			@Override
			protected void onCollisionBegin(Entity ball, Entity bound){
				ball.getComponent(PhysicsComponent.class).setVelocityX(ball.getComponent(PhysicsComponent.class).getVelocityX()*-1);	
			}
			
			});	
		physicsWorld.addCollisionHandler(new CollisionHandler(BounceAroundTypes.BALL, BounceAroundTypes.RIGHT){
			@Override
			protected void onCollisionBegin(Entity ball, Entity bound){
				ball.getComponent(PhysicsComponent.class).setVelocityX(ball.getComponent(PhysicsComponent.class).getVelocityX()*-1);
			}
			
			});	
	}
	protected void initScreenBounds(){
		GameEntity left = Entities.builder()
				.at(0,0)
				.type(BounceAroundTypes.LEFT)
				.with(new CollidableComponent(true))
				.viewFromNode(new Rectangle(5, getHeight(), Color.RED))
				.bbox(new HitBox("BODY", BoundingShape.box(1, getHeight())))
				.buildAndAttach(getGameWorld());
		
		GameEntity right = Entities.builder()
				.at(getWidth()-5, 0)
				.type(BounceAroundTypes.RIGHT)
				.with(new CollidableComponent(true))
				.viewFromNode(new Rectangle(5, getHeight(), Color.RED))
				.bbox(new HitBox("BODY", BoundingShape.box(1, getHeight())))
				.buildAndAttach(getGameWorld());
		
		GameEntity top = Entities.builder()
				.at(0, 0)
				.type(BounceAroundTypes.TOP)
				.with(new CollidableComponent(true))
				.viewFromNode(new Rectangle(getWidth(), 5, Color.RED))
				.bbox(new HitBox("BODY", BoundingShape.box(getWidth(), 1)))
				.buildAndAttach(getGameWorld());
		
		GameEntity bottom = Entities.builder()
				.at(0, getHeight()-5)
				.type(BounceAroundTypes.BOTTOM)
				.with(new CollidableComponent(true))
				.viewFromNode(new Rectangle(getWidth(), 5, Color.RED))
				.bbox(new HitBox("BODY", BoundingShape.box(getWidth(), getHeight())))
				.buildAndAttach(getGameWorld());			
	}
	protected void initPlayer(){
		physics = new PhysicsComponent();
		physics.setBodyType(BodyType.KINEMATIC);
		FixtureDef fd = new FixtureDef();
		fd.setRestitution(1f);
		physics.setFixtureDef(fd);
		
		player = Entities.builder()
				.at(getWidth()/2,getHeight()/2)
				.type(BounceAroundTypes.BALL)
				.with(physics)
				.with(new CollidableComponent(true))
				.viewFromNodeWithBBox(new Circle(30, Color.RED))
				.buildAndAttach(getGameWorld());
		physics.setVelocityX(FXGLMath.random(-500, 500));
		physics.setVelocityY(FXGLMath.random(-500, 500));
	}
	public static void main(String[] args){
		launch(args);
	}

}

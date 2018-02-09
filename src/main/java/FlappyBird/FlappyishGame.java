package FlappyBird;

import javafx.geometry.Orientation;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;


import javafx.scene.paint.Color;
import java.util.Map;
import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.texture.Texture;
//import com.almasb.fxgl.ecs.component.Required;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.EntityView;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.RenderLayer;
import com.almasb.fxgl.entity.ScrollingBackgroundView;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.entity.component.RotationComponent;
//import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.PhysicsWorld;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;

import javafx.scene.text.Font;
import javafx.scene.text.Text;


public class FlappyishGame extends GameApplication {
	GameEntity player;
    PhysicsComponent physics;
    Text text;
    boolean done = false;
	
	@Override
	protected void initUI(){
		Texture background = getAssetLoader().loadTexture("background.png");
		ScrollingBackgroundView background1 = new ScrollingBackgroundView(background, Orientation.HORIZONTAL, 5, RenderLayer.BACKGROUND);
		background1.setRenderLayer(RenderLayer.BACKGROUND);
		getGameScene().addGameView(background1);
		text = new Text();
		text.setFont(Font.font ("Arial Bold", 40));
		text.textProperty().bind(getGameState().intProperty("Score").divide(15).asString());
		text.setFill(Color.WHITE);
		text.setTranslateX(getWidth()/10);
		text.setTranslateY(getHeight()-5);
		getGameScene().addUINode(text);
		
	}
	@Override
	protected void initPhysics(){
		PhysicsWorld physicsWorld = getPhysicsWorld();
		physicsWorld.setGravity(0, 20);
		physicsWorld.addCollisionHandler(new CollisionHandler(FlappyTypes.FLAPPY,FlappyTypes.BOTTOMPIPE){
			@Override
			protected void onCollision(Entity Flappy, Entity Bottom){
				initGameOver();
			}
		});
		physicsWorld.addCollisionHandler(new CollisionHandler(FlappyTypes.FLAPPY,FlappyTypes.TOPPIPE){
			@Override
			protected void onCollision(Entity Flappy, Entity Top){
				initGameOver();
			}
		});
		physicsWorld.addCollisionHandler(new CollisionHandler(FlappyTypes.FLAPPY,FlappyTypes.GROUND){
			@Override
			protected void onCollision(Entity Flappy, Entity Ground){
				initGameOver();
			}
		});
		physicsWorld.addCollisionHandler(new CollisionHandler(FlappyTypes.FLAPPY,FlappyTypes.HITBOX){
			@Override
			protected void onCollision(Entity Flappy, Entity HitBox){
				if(!done){
				getGameState().increment("Score", +1);
				}
			}
		});
	}
	
	protected void initGameOver(){
		done = true;
		Texture gO = getAssetLoader().loadTexture("gameOver.png");
		EntityView gameOver = new EntityView(gO);
		gameOver.setLayoutX(getWidth()/6);
		gameOver.setLayoutY(getHeight()/2-50);
		gameOver.setRenderLayer(RenderLayer.TOP);
		getGameScene().addGameView(gameOver);
		for(int i = 0; i < getGameWorld().getEntities().size(); i++){
			getGameWorld().getEntities().get(i).removeAllControls();
		}
		getMasterTimer().clear();
	}
	
	
	@Override
	protected void initGameVars(Map<String, Object> vars){
		vars.put("Score", 0);
	}
	@Override
	protected void initInput() {
		final int flapForce = -250;
	    final Input input = getInput(); // get input service
	    input.addAction(new UserAction("Move Up") {
	        @Override
	        protected void onAction() {
	        	input.mockKeyRelease(KeyCode.UP);
	            physics.setVelocityY(flapForce);
	            //physics.applyAngularImpulse(5f);
	        }
	    }, KeyCode.UP);

	}
	
	@Override
	protected void initGame(){
		double bottomPipeUpperLimit = getHeight()/3;
		double bottomPipeLowerLimit = 9*getHeight()/12;
		double pipeSpacing = 350;
		
		
		//double bottomPipeLowerLimit = 
		initPlayer();
		getGameWorld().setEntityFactory(new PipeFactory());
		getGameWorld().spawn("Ground", 0 , getHeight()-50);
		getGameWorld().spawn("Ground", getWidth(), getHeight()-50);
		getMasterTimer().runAtInterval(() -> {
			double x = FXGLMath.random(bottomPipeLowerLimit,  bottomPipeUpperLimit);
				getGameWorld().spawn("TopPipe", getWidth(), x - pipeSpacing);
				getGameWorld().spawn("HitBox", getWidth(), x - pipeSpacing);
				getGameWorld().spawn("BottomPipe",getWidth(),x);
				getGameWorld().spawn("Ground", getWidth(), getHeight()-50);
			}, Duration.seconds(1.1));
		getMasterTimer().runAtInterval(() -> {
			rotate();
		}, Duration.millis(50));
	}
	protected void rotate(){
		double x = physics.getVelocityY();
		if(player.getRotation()/2 > 30){
			physics.setAngularVelocity(x/70);
		}else if(player.getRotation()/2 <= 30){
			if(x < 0){
				physics.setAngularVelocity(x/100);	
			}else physics.setAngularVelocity(x/120);
		}
	}
	protected void initPlayer() {
	       // playerControl = new PlayerControl()
		physics = new PhysicsComponent();
		physics.setBodyType(BodyType.DYNAMIC);

		// these are direct jbox2d objects, so we don't actually introduce new API
		FixtureDef fd = new FixtureDef();
		fd.setDensity(1f);
		fd.setRestitution(0.4f);
		fd.setFriction(199f);
		physics.setFixtureDef(fd);

	        Texture view = getAssetLoader().loadTexture("flappySpriteSheet.png").toAnimatedTexture(3, Duration.millis(300));
	                //.toAnimatedTexture(2, Duration.seconds(0.5));

	        	player = Entities.builder()
	                .at(50, 100)
	                .bbox(new HitBox("BODY", BoundingShape.circle(10)))
	                .type(FlappyTypes.FLAPPY)
	                .viewFromNode(view)
	                .with(new CollidableComponent(true))
	                .with(physics)
	                .renderLayer(RenderLayer.TOP)
	                .buildAndAttach(getGameWorld());

	        getGameScene().getViewport().setBounds(0, 0, Integer.MAX_VALUE, getHeight());
	        getGameScene().getViewport().bindToEntity(player, getWidth(), getHeight());
	    }

	@Override
	protected void initSettings(GameSettings settings) {
		   settings.setWidth(275);
	       settings.setHeight(400);
	       settings.setVersion("0.1");
	       settings.setTitle("FlappyBird");
	       settings.setMenuEnabled(false);
	       settings.setProfilingEnabled(false);
	       settings.setCloseConfirmation(false);
	       settings.setApplicationMode(ApplicationMode.RELEASE);
		
	}
	public static void main( String[] args )
    {
       launch(args);
    }

}


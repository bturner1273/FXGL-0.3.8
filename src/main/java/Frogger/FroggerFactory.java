package Frogger;

import com.almasb.fxgl.annotation.SetEntityFactory;
import com.almasb.fxgl.annotation.Spawns;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.RenderLayer;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.entity.control.ProjectileControl;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.texture.Texture;

import javafx.geometry.Point2D;

@SetEntityFactory
public class FroggerFactory implements EntityFactory {
	public static String[] leftCars = {"blueCarLeft.png", "greenCarLeft.png", "orangeCarLeft.png", "purpleCarLeft.png", "redCarLeft.png"};
	String[] rightCars = {"blueCarRight.png", "greenCarRight.png", "orangeCarRight.png", "purpleCarRight.png", "redCarRight.png"};
	@Spawns("RightCar")
	public Entity newRightCar(SpawnData data){
		return Entities.builder()
				.viewFromTexture(rightCars[FXGLMath.random(0, rightCars.length-1)])
				.type(FroggerTypes.CAR)
				.bbox(new HitBox("car", BoundingShape.circle(15)))
				.renderLayer(RenderLayer.TOP)
				.at(-100, FXGLMath.random(300, 350))
				.with(new CollidableComponent(true))
				.with(new ProjectileControl(new Point2D(1,0), 300))
				.build();
	}
	@Spawns("LeftCar")
	public Entity newLeftCar(SpawnData data){
		return Entities.builder()
				.viewFromTexture(leftCars[FXGLMath.random(0, leftCars.length-1)])
				.bbox(new HitBox("car", BoundingShape.circle(15)))
				.type(FroggerTypes.CAR)
				.renderLayer(RenderLayer.TOP)
				.at((int)(1722/2)-1 + 100, FXGLMath.random(205, 255))
				.with(new CollidableComponent(true))
				.with(new ProjectileControl(new Point2D(-1,0), 300))
				.build();
	}
	@Spawns("LeftLog")
	public Entity newLeftLog(SpawnData data){
		return Entities.builder()
				.from(data)
				.viewFromTexture("rightLog.png")
				.bbox(new HitBox("log", BoundingShape.box(-36, 30)))
				.bbox(new HitBox("log", BoundingShape.box(96, 30)))
				.type(FroggerTypes.LOG)
				.with(new CollidableComponent(true))
				.with(new ProjectileControl(new Point2D(-1,0), FXGLMath.random(50, 175)))
				.build();
	}
	@Spawns("RightLog")
	public Entity newRightLog(SpawnData data){
		return Entities.builder()
				.from(data)
				.viewFromTexture("rightLog.png")
				.bbox(new HitBox("log", BoundingShape.box(-36, 30)))
				.bbox(new HitBox("log", BoundingShape.box(96, 30)))
				.type(FroggerTypes.LOG)
				.with(new CollidableComponent(true))
				.with(new ProjectileControl(new Point2D(1,0), FXGLMath.random(50, 175)))
				.build();
	}
	@Spawns("LeftHalfLog")
	public Entity newLeftHalfLog(SpawnData data){
		return Entities.builder()
				.from(data)
				.viewFromTexture("halfRightLog.png")
				.bbox(new HitBox("log", BoundingShape.circle(15)))
				.type(FroggerTypes.LOG)
				.with(new CollidableComponent(true))
				.with(new ProjectileControl(new Point2D(-1,0), FXGLMath.random(50, 175)))
				.build();
	}
	@Spawns("RightHalfLog")
	public Entity newRightHalfLog(SpawnData data){
		return Entities.builder()
				.from(data)
				.viewFromTexture("halfRightLog.png")
				.bbox(new HitBox("log", BoundingShape.circle(15)))
				.type(FroggerTypes.LOG)
				.with(new CollidableComponent(true))
				.with(new ProjectileControl(new Point2D(1,0), FXGLMath.random(50, 175)))
				.build();
	}

}

package FlappyBird;


import javafx.geometry.Point2D;
import com.almasb.fxgl.annotation.SetEntityFactory;
import com.almasb.fxgl.annotation.Spawns;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.RenderLayer;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.entity.control.ProjectileControl;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;

@SetEntityFactory
public class PipeFactory implements EntityFactory{

	@Spawns("BottomPipe")
	public Entity newBottomPipe(SpawnData data){
		return Entities.builder()
				.from(data)
				.type(FlappyTypes.BOTTOMPIPE)
				.viewFromTexture("bottom.png")
				.bbox(new HitBox("BODY",BoundingShape.box(50, 265)))
				.renderLayer(RenderLayer.TOP)
				.with(new CollidableComponent(true))
				.with(new ProjectileControl(new Point2D(-1,0), 300))
				.build();	
	}
	
	@Spawns("HitBox")
	public Entity newHitBox(SpawnData data){
		return Entities.builder()
				.from(data)
				.type(FlappyTypes.HITBOX)
				.with(new CollidableComponent(true))
				.with(new ProjectileControl(new Point2D(-1,0), 300))
				.bbox(new HitBox("BODY", BoundingShape.box(50, 350)))
				.build();
	}
	
	@Spawns("TopPipe")
	public Entity newTopPipe(SpawnData data){
		return Entities.builder()
				.from(data)
				.type(FlappyTypes.TOPPIPE)
				.viewFromTexture("top.png")
				.bbox(new HitBox("BODY",BoundingShape.box(50, 265)))
				.renderLayer(RenderLayer.TOP)
				.with(new CollidableComponent(true))
				.with(new ProjectileControl(new Point2D(-1,0), 300))
				.build();
	}
	
	@Spawns("Ground")
	public Entity newGround(SpawnData data){
		return Entities.builder()
				.from(data)
				.type(FlappyTypes.GROUND)
				.viewFromTextureWithBBox("ground.png")
				.renderLayer(RenderLayer.TOP)
				.with(new CollidableComponent(true))
				.with(new ProjectileControl(new Point2D(-1,0), 300))
				.build();
	}
	
}

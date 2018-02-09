package idleAnimation;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.EntityView;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.settings.GameSettings;
import javafx.util.Duration;

public class idleMain extends GameApplication {
	GameEntity player;
	EntityView[] idles = new EntityView[4];
	int scale = 2;
	int x = 0;
	@Override
	protected void initSettings(GameSettings settings) {
		// TODO Auto-generated method stub
		settings.setIntroEnabled(false);
		settings.setMenuEnabled(false);
		settings.setProfilingEnabled(false);
		settings.setCloseConfirmation(false);
		settings.setTitle("IdleAnimation");
		settings.setVersion("");
	}
	
	@Override
	protected void initGame(){
		initPlayer();
		getMasterTimer().runAtInterval(() -> {
		idleAnimate();	
		}, Duration.millis(75));
	}
	protected void idleAnimate(){
		x++;
		idles[x%4] = new EntityView(getAssetLoader().loadTexture("dummyIdle" + x%4 + ".png"));
		player.setView(idles[x%4]);
		player.getView().setScaleX(scale);
		player.getView().setScaleY(scale);
	}
	protected void initPlayer(){
		EntityView view = new EntityView(getAssetLoader().loadTexture("dummyIdle0.png"));
		player = Entities.builder()
		.viewFromNode(view)
		.at(325,200)
		.buildAndAttach(getGameWorld());
		player.getView().setScaleX(scale);
		player.getView().setScaleY(scale);
	}
	
	public static void main(String[] args){
		launch(args);
	}
	

}

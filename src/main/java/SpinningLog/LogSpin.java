package SpinningLog;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.EntityView;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.texture.Texture;

import javafx.util.Duration;

public class LogSpin extends GameApplication{

	@Override
	protected void initSettings(GameSettings settings) {
		// TODO Auto-generated method stub
		settings.setIntroEnabled(false);
		settings.setMenuEnabled(false);
		settings.setProfilingEnabled(false);
		settings.setCloseConfirmation(false);
		settings.setVersion("");
		settings.setTitle("LogSpin");
		settings.setHeight(400);
		settings.setWidth(400);
	}
	@Override
	protected void initUI(){
		Texture spinningLog = getAssetLoader().loadTexture("RotatingLogAnimation.png").toAnimatedTexture(4, Duration.millis(450));
		EntityView Log = new EntityView(spinningLog);
		Log.setScaleX(3);
		Log.setScaleY(3);
		Log.setTranslateX(getWidth()/2-15);
		Log.setTranslateY(getHeight()/2-20);
		getGameScene().addGameView(Log);
	}
	public static void main(String[] args){
		launch(args);
	}

}

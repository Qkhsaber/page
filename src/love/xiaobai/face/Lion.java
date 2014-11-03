package love.xiaobai.face;

import love.xiaobai.f.ever.R;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

public class Lion extends Activity {
	ImageView start = null;
	TextView start2 = null;
	private static final long SPLASH_DELAY_MILLIS = 1000;
	Handler handler1 = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.lion);

		Typeface fontFace = Typeface.createFromAsset(getAssets(),
				"fonts/BUXTONSKETCH.TTF");
		start = (ImageView) findViewById(R.id.text);

		start.setBackgroundResource(R.anim.sett);
		final AnimationDrawable animationDrawable = (AnimationDrawable) start
				.getBackground();
		start.post(new Runnable() {
			@Override
			public void run() {
				animationDrawable.start();
			}
		});
		
		start2 = (TextView) findViewById(R.id.we);
		start2.setTypeface(fontFace);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				loginbutton();
			}
		}, 200);
		handler1.postDelayed(new Runnable() {
			@Override
			public void run() {
				goHome();
			}
		}, 3700);

	}

	void loginbutton() {
		// 文字动画
		// start.setVisibility(1);
		start2.setVisibility(1);
		final Animation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
		alphaAnimation.setDuration(2000);
		// start.setAnimation(alphaAnimation);
		start2.setAnimation(alphaAnimation);
		alphaAnimation.startNow();

	}

	private void goHome() {
		Intent intent = new Intent(Lion.this, MainActivity.class);
		Lion.this.startActivity(intent);
		Lion.this.finish();
	}

}

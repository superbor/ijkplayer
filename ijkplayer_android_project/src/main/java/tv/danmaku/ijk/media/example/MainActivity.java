package tv.danmaku.ijk.media.example;

import tv.danmaku.ijk.media.example.content.RecentMediaStorage;
import tv.danmaku.ijk.media.example.fragments.TracksFragment;
import tv.danmaku.ijk.media.example.widget.media.AndroidMediaController;
import tv.danmaku.ijk.media.example.widget.media.AndroidMediaController.MediaOrientation;
import tv.danmaku.ijk.media.example.widget.media.IjkVideoView;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.misc.ITrackInfo;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends Activity implements
		TracksFragment.ITrackHolder ,MediaOrientation{
	private static final String TAG = "VideoActivity";

	private String mVideoPath;
	private Uri mVideoUri;

	private AndroidMediaController mMediaController;
	private IjkVideoView mVideoView;
	private boolean mBackPressed;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

//		 mVideoPath
//		 ="http://v.test.jisujianbao.cn/3694/7011/T1DyYTBmVT1RCvBVdK.m3u8";
		mVideoPath = "/mnt/sdcard/sintel.mp4";

		if (!TextUtils.isEmpty(mVideoPath)) {
			new RecentMediaStorage(this).saveUrlAsync(mVideoPath);
		}

		mMediaController = new AndroidMediaController(this, false);
		IjkMediaPlayer.loadLibrariesOnce(null);
		IjkMediaPlayer.native_profileBegin("libijkplayer.so");

		mVideoView = (IjkVideoView) findViewById(R.id.video_view);
		mVideoView.setMediaController(mMediaController);
		if (mVideoPath != null)
			mVideoView.setVideoPath(mVideoPath);
		else if (mVideoUri != null)
			mVideoView.setVideoURI(mVideoUri);
		else {
			Log.e(TAG, "Null Data Source\n");
			finish();
			return;
		}
		mVideoView.start();
	}
	
	
	private boolean boolean_orientation = false;
	@Override
	public void changeOrientation(){
		if(boolean_orientation == false){
			
			boolean_orientation = true;
			//横屏
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			mVideoView.toggleAspectRatio(1);
		}else{
			boolean_orientation = false;
			//横屏
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			mVideoView.toggleAspectRatio(0);
		}
	}

	@Override
	public void onBackPressed() {
		mBackPressed = true;

		super.onBackPressed();
	}


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		if (mBackPressed || !mVideoView.isBackgroundPlayEnabled()) {
			mVideoView.stopPlayback();
			mVideoView.release(true);
			mVideoView.stopBackgroundPlay();
		} else {
			mVideoView.enterBackground();
		}
		IjkMediaPlayer.native_profileEnd();
	}


	@Override
	public ITrackInfo[] getTrackInfo() {
		if (mVideoView == null)
			return null;

		return mVideoView.getTrackInfo();
	}

	@Override
	public void selectTrack(int stream) {
		mVideoView.selectTrack(stream);
	}

	@Override
	public void deselectTrack(int stream) {
		mVideoView.deselectTrack(stream);
	}

	@Override
	public int getSelectedTrack(int trackType) {
		if (mVideoView == null)
			return -1;

		return mVideoView.getSelectedTrack(trackType);
	}

	@Override
	public void onTextClick(TextView textView) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getTvDefinition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void hidePop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showPop() {
		// TODO Auto-generated method stub
		
	}
}

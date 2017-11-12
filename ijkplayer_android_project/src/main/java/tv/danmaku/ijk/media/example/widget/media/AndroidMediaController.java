/*
 * Copyright (C) 2015 Zhang Rui <bbcallen@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tv.danmaku.ijk.media.example.widget.media;

import java.lang.reflect.Field;
import java.util.ArrayList;

import tv.danmaku.ijk.media.example.MainActivity;
import tv.danmaku.ijk.media.example.R;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class AndroidMediaController extends MediaController implements IMediaController {
    
    private MediaOrientation mediaOrientation;

    public AndroidMediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        mActivity = (Activity) context;
        mediaOrientation = (MediaOrientation) context;
    }

    public AndroidMediaController(Context context, boolean useFastForward) {
        super(context, useFastForward);
        initView(context);
        mActivity = (Activity) context;
        mediaOrientation = (MediaOrientation) context;
    }

    public AndroidMediaController(Context context) {
        super(context);
        initView(context);
        mActivity = (Activity) context;
        mediaOrientation = (MediaOrientation) context;
    }

    private void initView(Context context) {
    }


    //----------
    // Extends
    //----------
    private ArrayList<View> mShowOnceArray = new ArrayList<View>();

    public void showOnce(@NonNull View view) {
        mShowOnceArray.add(view);
        view.setVisibility(View.VISIBLE);
        show();
    }
    
	private Activity mActivity;
	private View mView;
	private TextView textView1;
	@Override
	public void setAnchorView(View view) {
		super.setAnchorView(view);
		mView = LayoutInflater.from(getContext()).inflate(
				R.layout.video_button, null);
		textView1 = (TextView) mView.findViewById(R.id.textView1);
		textView1.setText(mediaOrientation.getTvDefinition());
		textView1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mediaOrientation.onTextClick(textView1);
			}
		});
		
		try {
			SeekBar sb = (SeekBar) LayoutInflater.from(getContext()).inflate(
					R.layout.video_seekbar, null);
			ImageView image_view = (ImageView) LayoutInflater.from(getContext()).inflate(
					R.layout.video_imagebutton, null);
			Field mRoot = android.widget.MediaController.class
					.getDeclaredField("mRoot");
			mRoot.setAccessible(true);
			ViewGroup mRootVg = (ViewGroup) mRoot.get(this);
			
			//获取上面的线性布局 移除里面所有控件
			ViewGroup vg1 = findImageButtonParent(mRootVg);
			ArrayList<Integer> list = new ArrayList<Integer>();
			for (int i = 0; i < vg1.getChildCount(); i++) {
				if (vg1.getChildAt(i) instanceof ImageButton) {
					list.add(i);
				}
			}
			for(int i=0;i<list.size();i++){
				vg1.removeViewAt(list.get(0));
			}
			
			//获取下面线性布局中的SeekBar 并将其替换为自己的SeekBar
			ViewGroup vg = findSeekBarParent(mRootVg);
			int index = 1;
			for (int i = 0; i < vg.getChildCount(); i++) {
				if (vg.getChildAt(i) instanceof SeekBar) {
					index = i;
					break;
				}
			}
			vg.removeViewAt(index);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.weight = 10;
			params.gravity = Gravity.CENTER_VERTICAL;
			vg.addView(sb, index, params);
			Field mProgress = android.widget.MediaController.class
					.getDeclaredField("mProgress");
			mProgress.setAccessible(true);
			mProgress.set(this, sb);
			Field mSeekListener = android.widget.MediaController.class
					.getDeclaredField("mSeekListener");
			mSeekListener.setAccessible(true);
			sb.setOnSeekBarChangeListener((OnSeekBarChangeListener) mSeekListener
					.get(this));
			LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(60, 60);
			params1.gravity = Gravity.CENTER_VERTICAL;
			params1.rightMargin = 20;
			params1.leftMargin = 20;
			image_view.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					mediaOrientation.changeOrientation();
				}
			});
			vg.addView(image_view, 3, params1);
			
			//用反射回去暂停按钮并添加到下面的线性布局中
			LinearLayout.LayoutParams params11 = new LinearLayout.LayoutParams(60, 60);
			params11.gravity = Gravity.CENTER_VERTICAL;
			params11.rightMargin = 20;
			params11.leftMargin = 20;
			params11.topMargin = 20;
			params11.bottomMargin = 20;
			final ImageButton ImageButton;
			Field mPauseButton = android.widget.MediaController.class
					.getDeclaredField("mPauseButton");
			mPauseButton.setAccessible(true);
			ImageButton = (android.widget.ImageButton) mPauseButton.get(this);
			
			final View.OnClickListener thisPauseListener;//声明点击监听
			Field mPauseListener = android.widget.MediaController.class
					.getDeclaredField("mPauseListener");
			mPauseListener.setAccessible(true);
			thisPauseListener = (OnClickListener) mPauseListener.get(this);//反射回去点击监听
			ImageButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					thisPauseListener.onClick(v);
					new Handler().postDelayed(new Runnable() {
						
						@Override
						public void run() {
							show(3000);
						}
					}, 100);
				}
			});//添加点击
			
			vg.addView(ImageButton, 0, params11);
			
			LinearLayout.LayoutParams paramsRootView = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			paramsRootView.gravity = Gravity.CENTER_VERTICAL;
//			paramsRootView.bottomMargin = 40;
//			paramsRootView.topMargin = 30;
			vg.setLayoutParams(paramsRootView);

			//显示总时长的 和 当前播放时间 TextView 的 LayoutParams
			LinearLayout.LayoutParams paramsTextView = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			paramsTextView.gravity = Gravity.CENTER_VERTICAL;
			paramsTextView.bottomMargin=5;
			//显示总时长的 TextView
			Field mEndTime = android.widget.MediaController.class
					.getDeclaredField("mEndTime");
			mEndTime.setAccessible(true);
			TextView mymEndTime = (TextView) mEndTime.get(this);
			mymEndTime.setLayoutParams(paramsTextView);
			mEndTime.set(this, mymEndTime);
			//显示当前播放时间 TextView
			Field mCurrentTime = android.widget.MediaController.class
					.getDeclaredField("mCurrentTime");
			mCurrentTime.setAccessible(true);
			TextView mymCurrentTime = (TextView) mCurrentTime.get(this);
			mymCurrentTime.setLayoutParams(paramsTextView);
			mCurrentTime.set(this, mymCurrentTime);
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private ViewGroup findSeekBarParent(ViewGroup vg) {
		ViewGroup viewGroup = null;
		for (int i = 0; i < vg.getChildCount(); i++) {
			if(viewGroup!=null){
				return viewGroup;
			}
			View view = vg.getChildAt(i);
			if (view instanceof SeekBar) {
				viewGroup = (ViewGroup) view.getParent();
				break;
			} else if (view instanceof ViewGroup) {
				viewGroup = findSeekBarParent((ViewGroup) view);
			} else {
				continue;
			}
		}
		return viewGroup;
	}
	
	
	private ViewGroup findImageButtonParent(ViewGroup vg) {
		ViewGroup viewGroup = null;
		
		for (int i = 0; i < vg.getChildCount(); i++) {
			if(viewGroup!=null){
				return viewGroup;
			}
			View view = vg.getChildAt(i);
			if (view instanceof ViewGroup) {
				viewGroup = findImageButtonParent((ViewGroup) view);
			} else if (view instanceof ImageButton) {
				viewGroup = (ViewGroup) view.getParent();
				break;
			} else {
				continue;
			}
		}
		return viewGroup;
	}

	@Override
	public void show(int timeout) {
		super.show(timeout);
		((ViewGroup) mActivity.findViewById(android.R.id.content))
				.removeView(mView);
		((ViewGroup) mActivity.findViewById(android.R.id.content))
				.addView(mView);
		mediaOrientation.showPop();
	}

	@Override
	public void hide() {
		super.hide();
		((ViewGroup) mActivity.findViewById(android.R.id.content))
				.removeView(mView);
		mediaOrientation.hidePop();
	}
	
	public void changeText(String str){
		if(textView1!=null){
			textView1.setText(str);
		}
	}
	
	public void setTextViewVisibility(int vis){
		if(textView1!=null){
			textView1.setVisibility(vis);
		}
	}

	
	public interface MediaOrientation{
		void changeOrientation();
		void onTextClick(TextView textView);
		String getTvDefinition();
		void hidePop();
		void showPop();
		
	}
	


}

package com.flysand.mylibrary.implement;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 自定义Toast,调用setText(...)方法显示或更换显示的文字
 * 
 * @author SunFangTao
 * @Date 2014-6-10
 */
@SuppressLint("NewApi")
@SuppressWarnings("deprecation")
public class MyToastImplement {

	/**
	 * toast显示的默认时间
	 */
	public static final int LENGTH_LONG = 3500;
	public static final int LENGTH_SHORT = 2000;

	/**
	 * toast图片和文字的布局方式
	 */
	public static final int Image_LEFT = 0;
	public static final int Image_TOP = 1;
	public static final int Image_RIGTH = 2;
	public static final int Image_BOTTOM = 3;

	private WindowManager mWindowManager;
	private WindowManager.LayoutParams mWindowParams;
	/**
	 * 充当toast的控件
	 */
	private LinearLayout toastView;
	private TextView toastText;
	private ImageView toastImage;
	private Context context;
	/**
	 * 定时取消toast的handler
	 */
	private Handler mHandler;
	/**
	 * toast显示的内容
	 */
	private String mToastContent = "";
	/**
	 * toast显示的时间
	 */
	private int duration;
	/**
	 * 系统默认的toast动画
	 */
	private int animStyleId;
	/**
	 * 取消toast显示的线程
	 */
	private Runnable timerRunnable;
	/**
	 * 判断toast是否正在显示
	 */
	private boolean isShowing = false;
	/**
	 * 默认的圆角率
	 */
	private float defaultRadius = 25.0f;
	/**
	 * 四个角的圆角率，默认值为默认的圆角率
	 */
	private float mTopLeftRadius;
	private float mTopRightRadius;
	private float mBottomLeftRadius;
	private float mBottomRightRadius;
	/**
	 * toast背景的drawable对象
	 */
	private ShapeDrawable backgroundDrawable;

	public MyToastImplement(Context context) {

		this.context = context.getApplicationContext() != null ? context
				.getApplicationContext() : context;
		this.mWindowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		this.duration = MyToastImplement.LENGTH_SHORT;
		this.mHandler = new Handler();
		this.timerRunnable = new Runnable() {
			@Override
			public void run() {
				// 取消toast显示
				cancel();
			}
		};
		init();
	}

	private void init() {
		getDefaultToastView();
		mWindowParams = new WindowManager.LayoutParams();
		mWindowParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		mWindowParams.alpha = 1.0f;
		mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		mWindowParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
		mWindowParams.format = PixelFormat.TRANSLUCENT;
		mWindowParams.type = WindowManager.LayoutParams.TYPE_TOAST;
		mWindowParams.packageName = context.getPackageName();
		mWindowParams.windowAnimations = android.R.style.Animation_Toast;
		mWindowParams.y = context.getResources().getDisplayMetrics().widthPixels / 5;
		// mWindowParams.gravity = android.support.v4.view.GravityCompat
		// .getAbsoluteGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM,
		// android.support.v4.view.ViewCompat
		// .getLayoutDirection(toastView));
		mWindowParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;

	}

	private void getDefaultToastView() {
		toastText = new TextView(context);
		toastText.setSingleLine(false);
		toastText.setText(mToastContent);
		toastText.setTextSize(20);
		toastText.setFocusable(false);
		toastText.setClickable(false);
		toastText.setFocusableInTouchMode(false);
		toastText.setTextColor(Color.BLACK);
		toastText.setGravity(Gravity.CENTER);

		toastImage = new ImageView(context);
		// 默认没有图片
		toastImage.setVisibility(View.GONE);
		toastImage.setLayoutParams(new LinearLayout.LayoutParams(50, 50));

		toastView = new LinearLayout(context);
		toastView.setPadding(30, 8, 30, 8);
		toastView.addView(toastText);
		// toast默认背景
		// Drawable drawable = context.getResources().getDrawable(
		// android.R.drawable.toast_frame);

		// 圆角矩形背景
		mTopLeftRadius = defaultRadius;
		mTopRightRadius = defaultRadius;
		mBottomLeftRadius = defaultRadius;
		mBottomRightRadius = defaultRadius;

		float[] mOuter = new float[] { mTopLeftRadius, mTopLeftRadius,
				mTopRightRadius, mTopRightRadius, mBottomLeftRadius,
				mBottomLeftRadius, mBottomRightRadius, mBottomLeftRadius };

		backgroundDrawable = new ShapeDrawable(new RoundRectShape(mOuter, null,
				null));

		// 指定填充颜色
		backgroundDrawable.getPaint().setColor(Color.GRAY);
		// 指定填充模式
		backgroundDrawable.getPaint().setStyle(Paint.Style.FILL);

		if (Build.VERSION.SDK_INT < 16) {
			toastView.setBackgroundDrawable(backgroundDrawable);
		} else {
			toastView.setBackground(backgroundDrawable);
		}

	}

	/**
	 * 设置toast显示的图片及其位置
	 * 
	 * @param resouceId
	 * @param imagePosition
	 */
	public void setImage(int resouceId, int imagePosition) {
		setImage(resouceId, null, imagePosition);
	}

	/**
	 * 设置toast显示的图片及其位置
	 * 
	 * @param drawable
	 * @param imagePosition
	 */
	public void setImage(Drawable drawable, int imagePosition) {
		setImage(-1, drawable, imagePosition);
	}

	/**
	 * 设置toast不显示图片
	 */
	public void removeImage() {
		toastImage.setVisibility(View.GONE);
	}

	private void setImage(int resouceId, Drawable drawable, int imagePosition) {
		if (imagePosition < 0 || imagePosition > 4) {
			try {
				throw new Exception("imagePosition value is illegal");
			} catch (Exception e) {

			}
		} else {
			toastView.removeAllViews();
			toastImage.setVisibility(View.VISIBLE);
			if (resouceId < 0) {
				if (Build.VERSION.SDK_INT < 16) {
					toastImage.setBackgroundDrawable(drawable);
				} else {
					toastImage.setBackground(drawable);
				}
			} else {
				toastImage.setBackgroundResource(resouceId);
			}
			switch (imagePosition) {
			case 0:// 图片在左
				toastView.setOrientation(LinearLayout.HORIZONTAL);
				toastView.setGravity(Gravity.CENTER_VERTICAL);
				toastView.addView(toastImage);
				toastView.addView(toastText);
				break;
			case 1:// 图片在上
				toastView.setOrientation(LinearLayout.VERTICAL);
				toastView.setGravity(Gravity.CENTER_HORIZONTAL);
				toastView.addView(toastImage);
				toastView.addView(toastText);
				break;
			case 2:// 图片在右
				toastView.setOrientation(LinearLayout.HORIZONTAL);
				toastView.setGravity(Gravity.CENTER_VERTICAL);
				toastView.addView(toastText);
				toastView.addView(toastImage);
				break;
			case 3:// 图片在下
				toastView.setOrientation(LinearLayout.VERTICAL);
				toastView.setGravity(Gravity.CENTER_HORIZONTAL);
				toastView.addView(toastText);
				toastView.addView(toastImage);
				break;
			}
		}
	}

	/**
	 * 设置toast中图片的大小
	 * 
	 * @param width
	 * @param height
	 */
	public void setImageSize(int width, int height) {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width,
				height);
		toastImage.setLayoutParams(params);
	}

	/**
	 * 显示Toast或更换显示的文字
	 * 
	 * @param str
	 *            显示或更换的文字
	 * @param duration
	 *            toast显示的时间
	 * @return ToastHelper的实例
	 */
	public MyToastImplement setText(String str, int duration) {
		return setText(str).setDuration(duration);
	}

	/**
	 * 显示Toast或更换显示的文字
	 * 
	 * @param str
	 *            显示或更换的文字
	 * @return ToastHelper的实例
	 */
	public MyToastImplement setText(String str) {
		if (isShowing) {
			// 重新计时取消toast
			mHandler.removeCallbacks(timerRunnable);
		} else {
			// 设置为正在显示toast
			isShowing = true;
			mWindowManager.addView(toastView, mWindowParams);
		}
		toastText.setText(str);
		mHandler.postDelayed(timerRunnable, duration);
		return this;
	}

	/**
	 * 取消toast的显示
	 */
	public void cancel() {
		if (isShowing) {
			try {
				mWindowManager.removeView(toastView);
				mHandler.removeCallbacks(timerRunnable);
				// 设置为toast没有显示
			} catch (Exception e) {

			}
			isShowing = false;
		}
	}

	/**
	 * 设置toast的持续时间
	 * 
	 * @param duration
	 *            时间
	 * @return ToastHelper的实例
	 */
	public MyToastImplement setDuration(int duration) {
		this.duration = duration;
		return this;
	}

	/**
	 * 设置toast出现或消失的动画，style中定义；如:
	 * 
	 * <style name="name"> <item
	 * name="@android:windowEnterAnimation">@anim/enter</item> <item
	 * name="@android:windowExitAnimation">@anim/exit</item> </style>
	 * 
	 * @use R.style.name
	 * @param animStyleId
	 *            动画的资源ID
	 * @return ToastHelper的实例
	 */
	public MyToastImplement setAnimation(int animStyleId) {
		this.animStyleId = animStyleId;
		mWindowParams.windowAnimations = this.animStyleId;
		return this;
	}

	/**
	 * 取消设置的toast的动画，即使用默认的动画
	 */
	public MyToastImplement removeAnimation() {
		return setAnimation(android.R.style.Animation_Toast);
	}

	/**
	 * 设置toast显示的字体的颜色
	 * 
	 * @param color
	 * @return
	 */
	public MyToastImplement setTextColor(int color) {
		toastText.setTextColor(color);
		return this;
	}

	/**
	 * 设置toast显示的字体的颜色
	 * 
	 * @param colors
	 * @return
	 */
	public MyToastImplement setTextColor(ColorStateList colors) {
		toastText.setTextColor(colors);
		return this;
	}

	/**
	 * 设置toast显示的字体的大小
	 * 
	 * @param size
	 * @return
	 */
	public MyToastImplement setTextSize(int size) {
		toastText.setTextSize(size);
		return this;
	}

	/**
	 * 设置toast显示的字体的大小
	 * 
	 * @param unit
	 * @param size
	 * @return
	 */
	public MyToastImplement setTextSize(int unit, int size) {
		toastText.setTextSize(unit, size);
		return this;
	}

	/**
	 * 获取后可以设置toast背景的相关属性；
	 * 
	 * @return Paint
	 */
	public Paint getBackgroundDrawable() {
		return backgroundDrawable.getPaint();
	}

	/**
	 * 设置toast的背景drawable
	 * 
	 * @param drawable
	 */
	public void setBackgroudDrawable(Drawable drawable) {
		if (Build.VERSION.SDK_INT < 16) {
			toastView.setBackgroundDrawable(drawable);
		} else {
			toastView.setBackground(drawable);
		}
	}

}
package com.flysand.mylibrary.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;

import com.flysand.mylibrary.implement.MyToastImplement;


/**
 * 自定义Toast,调用setText(...)方法显示或更换显示的文字
 * 
 * @author SunFangTao
 * @Date 2014-6-10
 */
public class MyToast {

	private MyToastImplement toastImplement;

	public MyToast(Context context) {
		toastImplement = new MyToastImplement(context);
	}

	/**
	 * 设置toast显示的图片及其位置
	 * 
	 * @param resouceId
	 * @param imagePosition
	 */
	public void setImage(int resouceId, int imagePosition) {
		toastImplement.setImage(resouceId, imagePosition);
	}

	/**
	 * 设置toast显示的图片及其位置
	 * 
	 * @param drawable
	 * @param imagePosition
	 */
	public void setImage(Drawable drawable, int imagePosition) {
		toastImplement.setImage(drawable, imagePosition);
	}

	/**
	 * 设置toast不显示图片
	 */
	public void removeImage() {
		toastImplement.removeImage();
	}

	/**
	 * 设置toast中图片的大小
	 * 
	 * @param width
	 * @param height
	 */
	public void setImageSize(int width, int height) {
		toastImplement.setImageSize(width, height);
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
	public MyToast setText(String str, int duration) {
		toastImplement.setText(str, duration);
		return this;
	}

	/**
	 * 显示Toast或更换显示的文字
	 * 
	 * @param str
	 *            显示或更换的文字
	 * @return ToastHelper的实例
	 */
	public MyToast setText(String str) {
		toastImplement.setText(str);
		return this;
	}

	/**
	 * 取消toast的显示
	 */
	public void cancel() {
		toastImplement.cancel();
	}

	/**
	 * 设置toast的持续时间
	 * 
	 * @param duration
	 *            时间
	 * @return ToastHelper的实例
	 */
	public MyToast setDuration(int duration) {
		toastImplement.setDuration(duration);
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
	public MyToast setAnimation(int animStyleId) {
		toastImplement.setAnimation(animStyleId);
		return this;
	}

	/**
	 * 取消设置的toast的动画，即使用默认的动画
	 */
	public MyToast removeAnimation() {
		toastImplement.removeAnimation();
		return this;
	}

	/**
	 * 设置toast显示的字体的颜色
	 * 
	 * @param color
	 * @return
	 */
	public MyToast setTextColor(int color) {
		toastImplement.setTextColor(color);
		return this;
	}

	/**
	 * 设置toast显示的字体的颜色
	 * 
	 * @param colors
	 * @return
	 */
	public MyToast setTextColor(ColorStateList colors) {
		toastImplement.setTextColor(colors);
		return this;
	}

	/**
	 * 设置toast显示的字体的大小
	 * 
	 * @param size
	 * @return
	 */
	public MyToast setTextSize(int size) {
		toastImplement.setTextSize(size);
		return this;
	}

	/**
	 * 设置toast显示的字体的大小
	 * 
	 * @param unit
	 * @param size
	 * @return
	 */
	public MyToast setTextSize(int unit, int size) {
		toastImplement.setTextSize(unit, size);
		return this;
	}

	/**
	 * 设置toast的背景drawable
	 * 
	 * @param drawable
	 */
	public void setBackgroudDrawable(Drawable drawable) {
		toastImplement.setBackgroudDrawable(drawable);
	}

}
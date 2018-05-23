package cn.lt.game.lib.util;

import android.graphics.Color;
import android.graphics.ColorMatrix;

public class ColorUtil {
	/**
	 * 变成灰度图
	 * @return
	 */
	public static ColorMatrix changleGray(){
		ColorMatrix matrix= new ColorMatrix();
		matrix.setSaturation(0);
		return matrix;
	}
	
	
	/**
	 * 变成点击效果(把图片变暗)
	 * @return
	 */
	public static ColorMatrix changlePressed(){
		return new ColorMatrix(getPressedArray());
	}
	
	/**
	 * 点击效果矩阵(把图片变暗)
	 * @return
	 */
	public static float[] getPressedArray(){
		return getContrastArray(0.7f);
	}
	
	/**
	 * 改变对比度
	 * @param contrast
	 * @return
	 */
	public static float[] getContrastArray(float contrast){
		return new float[]{
				contrast,0,       0,       0,128*(1-contrast),
				0,       contrast,0,       0,128*(1-contrast),
				0,       0,       contrast,0,128*(1-contrast),
				0,       0,       0,       1,0
			};
	}
	
	/**
	 * 把图片除透明部分外变成纯色
	 * @param newColor
	 * @return
	 */
	public static ColorMatrix changlePure(int newColor){
		return changleColor(0x000000, newColor);
	}
	
	/**
	 * 把一种颜色变成另一种颜色，保留渐变效果
	 * @param oldColor
	 * @param newColor
	 * @return
	 */
	public static ColorMatrix changleColor(int oldColor,int newColor){
		ColorMatrix matrix=new ColorMatrix();
		
		if(oldColor==newColor){
			return matrix;
		}
		
		float oldRed=Color.red(oldColor);
		float oldGreen=Color.green(oldColor);
		float oldBlud=Color.blue(oldColor);
		float newRed=Color.red(newColor);
		float newGreen=Color.green(newColor);
		float newBlud=Color.blue(newColor);
		
		float offsetReg=(oldRed==0?newRed:0);
		float offsetGreen=(oldGreen==0?newGreen:0);
		float offsetBlud=(oldBlud==0?newBlud:0);
		float scaleReg=(oldRed==0?0:newRed/oldRed);
		float scaleGreen=(oldGreen==0?0:newGreen/oldGreen);
		float scaleBlud=(oldBlud==0?0:newBlud/oldBlud);
		
		float[] array=new float[]{
				scaleReg,0,0,0,offsetReg,
				0,scaleGreen,0,0,offsetGreen,
				0,0,scaleBlud,0,offsetBlud,
				0,0,0,1,0
		};
		
		matrix.set(array);
		return matrix;
	}
}

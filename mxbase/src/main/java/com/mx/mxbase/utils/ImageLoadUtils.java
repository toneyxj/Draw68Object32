package com.mx.mxbase.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.media.ExifInterface;

import com.mx.mxbase.base.BaseApplication;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 使用imageloader的方式处理图片静态类
 * 
 * @author Administrator
 *   show_image.buildDrawingCache();
Bitmap bitmap=show_image.getDrawingCache();
imageView.setImageBitmap(bitmap);
 * 
 */
public class ImageLoadUtils {
	/**
	 * 加载sdCard图片处理类
	 *
	 * @param pathName
	 *            sdcard图片文件路径
	 * @param reqWidth
	 *            处理后图片宽度
	 * @param reqHeight
	 *            处理后图片高度
	 * @return
	 */
	public static Bitmap decodeSampledBitmapFromSdCard(String pathName,
													   int reqWidth, int reqHeight) {
		// 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(pathName, options);
		// 调用上面定义的方法计算inSampleSize值
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		// 使用获取到的inSampleSize值再次解析图片
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(pathName, options);
	}

	/**
	 * 加载本地资源文件图片处理内
	 *
	 * @param res
	 *            资源获取对象
	 * @param resId
	 *            资源id
	 * @param reqWidth
	 *            处理后图片宽度
	 * @param reqHeight
	 *            处理后图片高度
	 * @return
	 */
	public static Bitmap decodeSampledBitmapFromResource(Resources res,
														 int resId, int reqWidth, int reqHeight) {
		// 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);
		// 调用上面定义的方法计算inSampleSize值
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		// 使用获取到的inSampleSize值再次解析图片
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}

	/**
	 * 图片压缩计算方法
	 *
	 * @param options
	 *            压缩原文件
	 * @param reqWidth
	 *            压缩目标宽度
	 * @param reqHeight
	 *            压缩目标高度
	 * @return 返回压缩倍数
	 */
	private static int calculateInSampleSize(BitmapFactory.Options options,
											 int reqWidth, int reqHeight) {
		// 源图片的高度和宽度
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			// 计算出实际宽高和目标宽高的比率
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			// 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
			// 一定都会大于等于目标的宽和高。
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}
	/**
	 * 质量压缩
	 * @param file 压缩源文件
	 * @param hundredKb 压缩质量以百kb为单位
	 * @return 图片bitmap
	 */
	public static  Bitmap compressImage(String file,int hundredKb) {
		Bitmap image=BitmapFactory.decodeFile(file);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		double size=(baos.toByteArray().length)/(102400.0*hundredKb);
		if (size<=1){
			return image;
		}
		if (size>1) {
			options = (int) (100 / size);
			baos.reset();//重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
		}
		return BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.toByteArray().length);
	}
	/**
	 * 将图片持久化到本地文件夹下面
	 *
	 * @param bitmap
	 *            需要持久化的图片
	 */
	public static String saveImageFile(Bitmap bitmap) throws IOException {
		String path = null;
		String sd = StringUtils.getSDPath()+"image/";
		if (sd == null) {
			path = "";
			BaseApplication.Toast("请插入，SD卡");
			return path;
		}
		String my =  System.currentTimeMillis() + ".png";
		File file = new File(sd);
		if (!file.exists())
			file.mkdirs();
		FileOutputStream fileOutputStream = new FileOutputStream(
				sd + my);
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
		fileOutputStream.close();
		path = sd + my;
		return path;
	}
	/**
	 * 将图片持久化到本地文件夹下面
	 *
	 * @param bitmap
	 *            需要持久化的图片
	 */
	public static String saveImageFile(Bitmap bitmap,String path) throws IOException {
		File file = new File(path);
		if (file.exists()){
			StringUtils.deleteFile(file);
			file.createNewFile();
		}
		FileOutputStream fileOutputStream = new FileOutputStream(
				path);
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
		fileOutputStream.close();
		return path;
	}
	/**
	 * 旋转图片
	 * @param bm
	 * @param orientationDegree
	 * @return
	 */
	public static Bitmap adjustPhotoRotation(Bitmap bm, final int orientationDegree) {

		Matrix m = new Matrix();
		m.setRotate(orientationDegree, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
		float targetX, targetY;
		if (orientationDegree == 90) {
			targetX = bm.getHeight();
			targetY = 0;
		} else {
			targetX = bm.getHeight();
			targetY = bm.getWidth();
		}

		final float[] values = new float[9];
		m.getValues(values);

		float x1 = values[Matrix.MTRANS_X];
		float y1 = values[Matrix.MTRANS_Y];

		m.postTranslate(targetX - x1, targetY - y1);

		Bitmap bm1 = Bitmap.createBitmap(bm.getHeight(), bm.getWidth(), Bitmap.Config.ARGB_8888);

		Paint paint = new Paint();
		Canvas canvas = new Canvas(bm1);
		canvas.drawBitmap(bm, m, paint);


		return bm1;
	}

	/**
	 * 旋转图片
	 * @param angle
	 * @param bitmap
	 * @return Bitmap
	 */
	public static   Bitmap rotaingImageView(int angle , Bitmap bitmap) {
		//旋转图片 动作
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}
	/**
	 * 读取图片属性：旋转的角度
	 * @param path 图片绝对路径
	 * @return degree旋转的角度
	 */
	public static int readPictureDegree(String path) {
		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.inSampleSize = 8;
		File file = new File(path);
		path=file.getAbsolutePath();
		int degree  = 0;

		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					degree = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					degree = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					degree = 270;
					break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}
	/**
	 * 根据原图和变长绘制圆形图片
	 *
	 * @param source 裁剪图片源
	 * @return 返回裁剪后的图片
	 */
	public static Bitmap createCircleImage(Bitmap source) {
		int min = source.getWidth();
		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		Bitmap target = Bitmap.createBitmap(min, min, Bitmap.Config.ARGB_8888);
		/**
		 * 产生一个同样大小的画布
		 */
		Canvas canvas = new Canvas(target);
		/**
		 * 首先绘制圆形
		 */
		canvas.drawCircle(min / 2, min / 2, min / 2, paint);
		/**
		 * 使用SRC_IN
		 */
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		/**
		 * 绘制图片
		 */
		canvas.drawBitmap(source, 0, 0, paint);
		return target;
	}

	/**
	 * 回收bitmap
	 * @param bitmap
     */
	public static void recycleBitmap(Bitmap bitmap){
		if (bitmap!=null&&!bitmap.isRecycled()){
			bitmap.recycle();
			bitmap=null;
		}
		System.gc();
	}

}

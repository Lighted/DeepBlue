package com.amazing.utils;

import com.amazing.deepblue.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;

//�����ֻ��Ƶ� ͼƬ��
public class DrawText 
{
	Context context = null;
	
	public DrawText(Context context)
	{
		this.context = context;
	}
	
	/*
	 * bitmap ͼƬ
	 * text ����
	 * textSize ���ִ�С
	 * rect�ü�����
	 * dx ����ƫ����
	 * dy
	 */
	public Bitmap drawTextToBitmap(Bitmap bitmap, String text, int color, float textSize, int width, float dx, float dy)
	{
		//����һ���µ�ͼƬ
		Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		
		//�½�����
		Canvas canvas = new Canvas(outBitmap);
		
		//ͼƬ����
		Paint bitmapPaint = new Paint();
		
		//���Ȼ���ͼƬ
		canvas.drawBitmap(bitmap, 0, 0, bitmapPaint);
		
		//ƫ����
		canvas.translate(dx, dy);
		
		//��������
		TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);
		textPaint.setColor(color);
		//textPaint.setStyle(Style.FILL);
		textPaint.setTextSize(textSize);
		
		//�����Զ�������
		Typeface typeface = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.font_path));
		textPaint.setTypeface(typeface);
		
		//�������ֲ���
		StaticLayout staticLayout = new StaticLayout(text, textPaint, width, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
		
		//��������
		staticLayout.draw(canvas);
		
		//--����
		//File file = new File("/sdcard/namecard/yyc", "xx.png"); 
		 
		//if(file.exists())
			//file.delete();
		
		/*String fileName = "/mnt/sdcard/xx.png";
		
		 File file = new File(fileName);

         FileOutputStream out = null;
         try{
                 out = new FileOutputStream(file);
                 
                 //100��ʾ��ѹ��
                 if(outBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) 
                 {
                         out.flush();
                         out.close();
                 }
         } 
         catch (FileNotFoundException e) 
         {
                 e.printStackTrace();
         } 
         catch (IOException e) 
         {
                 e.printStackTrace(); 
         }*/

		return outBitmap;
	} 
}

























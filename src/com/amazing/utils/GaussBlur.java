package com.amazing.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Matrix;

/*
 * �����ͼƬ���и�˹ģ��
 */
public class GaussBlur 
{
	//���Ų���
	//private final static float SCALE = 4.0f;
	private final static int RENDER_BITMAP_MAX_RADIUS = 200;
	private final static int MAX_RADIUS = 100;	//��ɫ��˹ģ�������뾶 �� ��С�뾶
	private final static int MIN_RADIUS = 1;
	
	private final static int RENDER_MAX_RADIUS = 25;
	private final static int RENDER_MIN_RADIUS = 1;
	
	Context context = null;
	
	//��Ļ�Ŀ����
	int screenWidth;
	int screenHeight;
	
	//�� �� ��
	float scale;
	
	public GaussBlur(Context context, int screenWidth, int screenHeight)
	{
		this.context = context;
		
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		
		this.scale = 1.0f * screenHeight / screenWidth;
	}
	
	//����һ����ɫֵ �������ɫͼƬ���и�˹ģ�� ��Ե�����ð�ɫ��ȫ
	public Bitmap blurColor(int color, int radius)
	{
		//ģ���뾶Ϊ0 ֱ�ӷ���һ��ͼƬ
		if(0 == radius)
		{
			//ֱ�ӷ���һ����ɫͼƬ
			int[] pix = new int[screenWidth * screenHeight];
			int n = screenWidth * screenHeight;
			for(int i = 0; i < n; ++i)
				pix[i] = color;
			
			Bitmap bitmap = Bitmap.createBitmap(pix, screenWidth, screenHeight, Config.ARGB_8888);
			
			return bitmap;
		}
		
		//�жϱ߽�����
		if(radius > MAX_RADIUS)	radius = MAX_RADIUS;
		if(radius < MIN_RADIUS)	radius = MIN_RADIUS;
		
		int w;
		int h;
		int n;
		
		if(screenHeight > screenWidth)
		{
			//�ߴ��ڿ�
			w = 2 * (MAX_RADIUS + radius);
			h = (int)(scale * w);
			n = w * h;
		}
		else
		{
			h = 2 * (MAX_RADIUS + radius);
			w = (int)(h / scale);
			n = w * h;
		}

		int[] pix = new int[n];
		
		//������Ϊ��ɫ
		for(int i = 0; i < n; ++i)
			pix[i] = 0xFFFFFFFF;
		
		//�м�ľ�������Ϊ �ȶ���ɫ
		for(int i = radius; i < w - radius; ++i)
		for(int j = radius; j < h - radius; ++j)
			pix[j * w + i] = color;
		
		//���и�˹ģ��
		initCBlur(pix, w ,h, radius);
		
		//����һ��ͼƬ
		Bitmap intBitmap = Bitmap.createBitmap(pix, w, h, Config.ARGB_8888);
		
		//��ͼƬ���вü�
		w -= 2 * radius;
		h -= 2 * radius;
		Matrix matrix = new Matrix();
		matrix.postScale(1.0f * screenWidth / w, 1.0f * screenHeight / h);
		Bitmap blurBitmap = Bitmap.createBitmap(intBitmap, radius, radius, w, h, matrix, true);
		
		//�����ڴ�
		intBitmap.recycle();
		
		return blurBitmap;
	}
	
	public Bitmap blurBitmap2(Bitmap bitmap, int radius)
	{
		if(0 == radius)
		{
			//����һ����ͬ��ͼƬ
			//Bitmap outBitmap = Bitmap.createBitmap(bitmap);
			int w = bitmap.getWidth();
			int h = bitmap.getHeight();
			int[] pix = new int[w * h];
			
			//д����������
			bitmap.getPixels(pix, 0, w, 0, 0, w, h);
			
			Bitmap outBitmap = Bitmap.createBitmap(pix, w, h, Bitmap.Config.ARGB_8888);
			
			return outBitmap;
		}
		
		//��ֹԽ��
		if(radius > MAX_RADIUS)	radius = MAX_RADIUS;
		if(radius < MIN_RADIUS) radius = MIN_RADIUS;
		
		//��ͼƬ��������
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		
		float ss;	//���ű���
		
		if(h > w)	ss = 1.0f * 2 * RENDER_BITMAP_MAX_RADIUS / w;
		else 		ss = 1.0f * 2 * RENDER_BITMAP_MAX_RADIUS / h;
		
		Matrix smallMatrix = new Matrix();
		smallMatrix.postScale(ss, ss);
		Bitmap inBitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, smallMatrix, true);
		
		int inW = inBitmap.getWidth();
		int inH = inBitmap.getHeight();
		int[] pix = new int[inW * inH];
		
		//д������
		inBitmap.getPixels(pix, 0, inW, 0, 0, inW, inH);
		
		//����ģ��
		initCBlur(pix, inW, inH, radius);
		
		//д������
		inBitmap.setPixels(pix, 0, inW, 0, 0, inW, inH);
		
		//�Ŵ�
		//�Ŵ�
        ss = 1.0f / ss;
        Matrix bigMatrix = new Matrix();
        bigMatrix.postScale(ss, ss);
        Bitmap outBitmap = Bitmap.createBitmap(inBitmap, 0, 0, inW, inH, bigMatrix, true);
        
        inBitmap.recycle();
        
        return outBitmap;
	}
	
	//����android�Դ����㷨���� ģ������
	/*public Bitmap blurBitmap(Bitmap bitmap, int radius)
	{  
		if(0 == radius)
		{
			//����һ����ͬ��ͼƬ
			//Bitmap outBitmap = Bitmap.createBitmap(bitmap);
			int w = bitmap.getWidth();
			int h = bitmap.getHeight();
			int[] pix = new int[w * h];
			
			//д����������
			bitmap.getPixels(pix, 0, w, 0, 0, w, h);
			
			Bitmap outBitmap = Bitmap.createBitmap(pix, w, h, Bitmap.Config.ARGB_8888);
			
			return outBitmap;
		}
		
		//��ֹԽ��
		if(radius > RENDER_MAX_RADIUS)	radius = RENDER_MAX_RADIUS;
		if(radius < RENDER_MIN_RADIUS)  radius = RENDER_MIN_RADIUS;
		
		//��ͼƬ��������
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		
		float ss;	//���ű���
		
		if(h > w)	ss = 1.0f * 2 * RENDER_BITMAP_MAX_RADIUS / w;
		else 		ss = 1.0f * 2 * RENDER_BITMAP_MAX_RADIUS / h;
		
		Matrix smallMatrix = new Matrix();
		smallMatrix.postScale(ss, ss);
		Bitmap inBitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, smallMatrix, true);
		
        //Let's create an empty bitmap with the same size of the bitmap we want to blur  
        Bitmap midBitmap = Bitmap.createBitmap(inBitmap.getWidth(), inBitmap.getHeight(), Config.ARGB_8888);  
          
        //Instantiate a new Renderscript  
        RenderScript rs = RenderScript.create(context.getApplicationContext());  
          
        //Create an Intrinsic Blur Script using the Renderscript  
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));  
          
        //Create the Allocations (in/out) with the Renderscript and the in/out bitmaps  
        Allocation allIn = Allocation.createFromBitmap(rs, inBitmap);  
        Allocation allOut = Allocation.createFromBitmap(rs, midBitmap);  
          
        //Set the radius of the blur  
        blurScript.setRadius(radius);
          
        //Perform the Renderscript  
        blurScript.setInput(allIn);  
        blurScript.forEach(allOut);  
          
        //Copy the final bitmap created by the out Allocation to the outBitmap  
        allOut.copyTo(midBitmap);  
         
        //�Ŵ�
        ss = 1.0f / ss;
        Matrix bigMatrix = new Matrix();
        bigMatrix.postScale(ss, ss);
        Bitmap outBitmap = Bitmap.createBitmap(midBitmap, 0, 0, midBitmap.getWidth(), midBitmap.getHeight(), bigMatrix, true);
        
        //After finishing everything, we destroy the Renderscript.  
        inBitmap.recycle();
        midBitmap.recycle();
        rs.destroy();
        
          
        return outBitmap;  
    }*/
	
	//����һ����ɫֵ �������ɫͼƬ���и�˹ģ�� ��Ե�����ð�ɫ��ȫ
	/*public Bitmap blurColor(int color, int radius)
	{
		//ģ���뾶Ϊ0 ֱ�ӷ���һ��ͼƬ
		if(0 == radius)
		{
			return null;
		}
		
		//�жϱ߽�����
		if(radius > MAX_RADIUS)	radius = MAX_RADIUS;
		else if(radius < MIN_RADIUS)	radius = MIN_RADIUS;
		
		int w;
		int h;
		int n;
		
		if(screenHeight > screenWidth)
		{
			//�ߴ��ڿ�
			w = 2 * (MAX_RADIUS + radius);
			h = (int)(scale * w);
			n = w * h;
		}
		else
		{
			h = 2 * (MAX_RADIUS + radius);
			w = (int)(h / scale);
			n = w * h;
		}

		int[] pix = new int[n];
		
		//������Ϊ��ɫ
		for(int i = 0; i < n; ++i)
			pix[i] = 0xFFFFFFFF;
		
		//�м�ľ�������Ϊ �ȶ���ɫ
		for(int i = radius; i < w - radius; ++i)
		for(int j = radius; j < h - radius; ++j)
			pix[j * w + i] = color;
		
		//���и�˹ģ��
		initCBlur2(pix, w ,h, radius);
		
		//����һ��ͼƬ
		Bitmap intBitmap = Bitmap.createBitmap(pix, w, h, Config.ARGB_8888);
		
		//��ͼƬ���вü�
		w -= 2 * radius;
		h -= 2 * radius;
		Matrix matrix = new Matrix();
		matrix.postScale(1.0f * screenWidth / w, 1.0f * screenHeight / h);
		Bitmap blurBitmap = Bitmap.createBitmap(intBitmap, radius, radius, w, h, matrix, false);
		
		//�����ڴ�
		intBitmap.recycle();
		
		return blurBitmap;
	}*/
	
	//ʹ��c++�����˹ģ��
	/*
	 * pix ��������
	 * w �����ظ���
	 * h ����
	 * radius ģ���뾶
	 */
	private native void initCBlur(int[] pix, int w ,int h, int r);
	//private native void initCBlur2(int[] pix, int w ,int h, int r);
	
	//����nativeģ��
	static
	{
		System.loadLibrary("blur"); 
	};
}



















package com.amazing.utils;

import java.util.Random;

import android.content.Context;
import android.content.SharedPreferences;

//���ݶ�ȡ��д�� ��
//���� ��ȡ ���ֲ��� �� �洢���ֲ���
public class ContentPreference 
{
	//
	private final static String DATA_FILE_NAME = "lff"; //�洢���ļ���
	private final static String BG_COLOR_KEY = "bgcolorindex";
	private final static String FONT_COLOR_KEY = "fontcolorindex";
	private final static String SIZE_PROGRESS_KEY = "sizeprogress";
	
	private int bgColorIndexMax;	//������ɫ����
	private int fontColorIndexMax;//����������ɫ ����
	private int sizeProgressMax;	//���������С
	
	private int bgColorIndex;	//������ɫ����
	private int fontColorIndex;	//������ɫ����
	private int blobProgress;	//ģ��
	private int sizeProgress;	//����Ĵ�С
	
	//
	private SharedPreferences sharePreferences = null;
	
	//�����
	Random random = null;
	
	public ContentPreference(Context context, int bgColorIndexMax, int fontColorIndexMax, int sizeProgressMax)
	{
		this.bgColorIndexMax = bgColorIndexMax;
		this.fontColorIndexMax = fontColorIndexMax;
		this.sizeProgressMax = sizeProgressMax;
		
		//��ȡ����
		sharePreferences = context.getSharedPreferences(DATA_FILE_NAME, 0);
		
		//�����
		random = new Random();
		
		readParams();
	}
	


	public int getBgColorIndex() {
		return bgColorIndex;
	}

	public int getFontColorIndex() {
		return fontColorIndex;
	}
	
	public int getBlobProgress() {
		return blobProgress;
	}

	public int getSizeProgress() {
		return sizeProgress;
	}



	public void setSizeProgress(int sizeProgress) {
		this.sizeProgress = sizeProgress;
	}

	//�洢����
	public void commitParams(int bgColorIndex, int fontColorIndex, int blobProgress, int sizeProgress)
	{
		SharedPreferences.Editor editor = sharePreferences.edit();
		editor.putInt(BG_COLOR_KEY, bgColorIndex);  
		editor.putInt(FONT_COLOR_KEY, fontColorIndex);  
		editor.putInt(SIZE_PROGRESS_KEY, sizeProgress);  
		editor.commit();  
	}
	
	//��ȡ���ֲ���
	private void readParams()
	{
		bgColorIndex = sharePreferences.getInt(BG_COLOR_KEY, -1);
		if(0 > bgColorIndex || bgColorIndex >= bgColorIndexMax)
			bgColorIndex = Math.abs( random.nextInt() ) % bgColorIndexMax;
		
		//---------------------------------
		//System.out.println("bgColorIndex��" + bgColorIndex);
		
		fontColorIndex = sharePreferences.getInt(FONT_COLOR_KEY, -1);
		if(0 > fontColorIndex || fontColorIndex >= fontColorIndexMax)
			fontColorIndex = Math.abs( random.nextInt() ) % fontColorIndexMax;
		
		sizeProgress = sharePreferences.getInt(SIZE_PROGRESS_KEY, 25);
		if(0 > sizeProgress || sizeProgress > sizeProgressMax)
			sizeProgress = 25;
		
		//ģ���̶�Ĭ����0
		blobProgress = 0;
	}
}
























package com.amazing.utils;

import android.graphics.Bitmap;

//�洢��Ҫ�ľ�̬���� 
//��Ҫ��intent��ת��Ҫ�ĳ���
public class Constant 
{
	public final static int REQUEST_GET_IMAGE_CODE = 1;	//����õ�һ��ͼƬ��uri
	public final static int REQUEST_EDIT_IMAGE_CODE = 2;	//����༭һ��ͼƬ
	
	//��editactivit �༭�õ�ͼƬ���� ����������activity֮�䴫��
	private static Bitmap editBitmap = null;
	
	public static void setEditBitmap(Bitmap bitmap)
	{
		editBitmap = bitmap;
	}
	
	public static Bitmap getEditBitmap()
	{
		return editBitmap;
	}
}

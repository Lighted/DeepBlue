package com.amazing.utils;

import android.graphics.Bitmap;

//mainActivity״̬ ������ǰ�� ��ɫ���� ���� ͼƬ����
public class BackgroundStatus 
{
	static public enum STATUS
	{
		COLOR_BG,	//����ɫΪ����
		BITMAP_BG,	//��ͼƬ Ϊ����
	}
	
	public STATUS status;	//��Ӧ��״̬
	
	public int color;	//�������ɫ״̬ �洢��ǰ��ɫ
	
	public Bitmap originBitmap = null;	//�����ͼƬ״̬ �洢ԭʼͼƬ
	public Bitmap blurBitmap = null;	//�洢�Ѿ���Ϊ������ͼƬ 
	
	//��Ҫ�����ͼƬ
	public Bitmap recycleBitmap = null;
}

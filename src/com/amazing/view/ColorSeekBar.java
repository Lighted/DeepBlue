package com.amazing.view;

import com.amazing.utils.Point;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

public class ColorSeekBar extends SeekBar
{
	/////////////////////////////////////////
	//������Ϣ ���ڶ�������
	protected Point startPoint = null;	//��������������� ����ڷ������ƫ�� ��(toLeft, toBottom)(x, y)
	protected Point endPoint   = null;
	protected Point farPoint   = null;
	
	//����
	Animation startAnim = null;
	Animation endAnim = null;
	Animation farAnim = null;
	
	//������Ϣ һ��start�Ĳ�����Ϣ һ�� end�Ĳ�����Ϣ
	RelativeLayout.LayoutParams startParams = null;
	RelativeLayout.LayoutParams endParams = null;
	RelativeLayout.LayoutParams farParams = null;

	public ColorSeekBar(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
	}
}





















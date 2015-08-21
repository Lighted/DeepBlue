package com.amazing.view;

import com.amazing.utils.Point;

import com.amazing.deepblue.R;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

//�Զ����ColorButton ģ�ⵥѡ��ť
public class ColorButton extends ImageButton
{
	//����
	//private final static String STROKE_COLOR = "#FFAAAAAA";	//�����ɫ
	private final static int STROKE_WIDTH = 5;	//��߿��
	public final static int CONTINUE_TIME = 150;// ����������ʱ��
	
	///////////////////////////////////////////////////////////
	private int fillColor;	//�����ɫ
	private int width;	//ͳһΪԲ�ΰ�ť ����Բ�ΰ뾶
	
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

	Context context = null;
	AttributeSet attrs = null;
	
	public ColorButton(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		
		this.context = context;
		this.attrs = attrs;
	}
	
	public ColorButton(int color, int w, Context context, AttributeSet attrs) 
	{
		this(context, attrs);
		
		//������ɫ
	    fillColor = color;			//�ڲ������ɫ
	    width = w;	//��ť�İ뾶

	    //���ñ���
	    this.setBackground(getBg());
	}
	
	//ִ����չ����
	public void expandAnim()
	{
		this.startAnimation(farAnim);
	}
	
	//��������
	public void shrinkAnim()
	{
		this.startAnimation(startAnim);
	}
	
	//��ʼ��far ��end����
	private void startFarToEndAnim()
	{
		this.startAnimation(endAnim);
	}
	
	//�������
	private void clearAnim()
	{
		this.clearAnimation();
	}
	
	//���ò�����Ϣ
	private void setStartLayoutParams()
	{
		this.setLayoutParams(startParams);
	}
	
	//���ò�����Ϣ
	private void setEndLayoutParams()
	{
		this.setLayoutParams(endParams);
	}
	
	//���ò�����Ϣ
	private void setFarLayoutParams()
	{
		this.setLayoutParams(farParams);
	}
	
	//�����Ƿ�ɼ�
	private void setVisibleOn()
	{
		this.setAlpha(1.0f);
	}
	//�����Ƿ�ɼ�
	private void setVisibleOff()
	{
		this.setAlpha(0.0f);
	}

	//����������Ϣ
	public void setPoints(Point startPoint, Point endPoint, Point farPoint)
	{
		this.startPoint = startPoint;
		this.endPoint   = endPoint;
		this.farPoint   = farPoint;

		//��ʼ������
		initLayoutParams();
		initAnimation();
	}
	
	//��ʼ����������
	private void initAnimation()
	{
		//�������� start-far far-end end-start
		startAnim = translateAnim(startPoint, endPoint, CONTINUE_TIME);	//end - start
		endAnim   = translateAnim(endPoint, farPoint, CONTINUE_TIME);	//far - end
		farAnim   = translateAnim(farPoint, startPoint, CONTINUE_TIME);	//start - far
		
		//��end - ��start
		startAnim.setAnimationListener(new AnimationListener() 
		{
			@Override
			public void onAnimationStart(Animation arg0) 
			{
			}
			
			@Override
			public void onAnimationRepeat(Animation arg0) {
			}
			
			@Override
			public void onAnimationEnd(Animation arg0) 
			{
				//����������ִ�лص�
				clearAnim();
				setStartLayoutParams();
				
				//���ɼ��Ӳ��ɵ��
				setVisibleOff();
			}
		});
		
		endAnim.setAnimationListener(new AnimationListener() 
		{
			@Override
			public void onAnimationStart(Animation arg0) {
			}
			
			@Override
			public void onAnimationRepeat(Animation arg0) {
			}
			
			@Override
			public void onAnimationEnd(Animation arg0) 
			{
				//����������ִ�лص�
				clearAnim();
				setEndLayoutParams();
			}
		});
		
		farAnim.setAnimationListener(new AnimationListener() 
		{
			@Override
			public void onAnimationStart(Animation arg0) 
			{
				setVisibleOn();
			}
			
			@Override
			public void onAnimationRepeat(Animation arg0) {
			}
			
			@Override
			public void onAnimationEnd(Animation arg0) 
			{
				//����������ִ�лص�
				clearAnim();
				setFarLayoutParams();
				
				startFarToEndAnim();
			}
		});
	}
	
	//��ʼ��������Ϣ
	private void initLayoutParams()
	{
		//��ʼ�Ĳ�����Ϣ
		startParams = new RelativeLayout.LayoutParams(width, width);
		startParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		startParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		startParams.leftMargin = startPoint.x;
		startParams.bottomMargin = startPoint.y;
		
		//������Ĳ�����Ϣ
		endParams = new RelativeLayout.LayoutParams(width, width);
		endParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		endParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		endParams.leftMargin   = endPoint.x;
		endParams.bottomMargin = endPoint.y;
		
		farParams = new RelativeLayout.LayoutParams(width, width);
		farParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		farParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		farParams.leftMargin   = farPoint.x;
		farParams.bottomMargin = farPoint.y;
		
		//����Ϊ��ʼ�Ĳ�����Ϣ
		setLayoutParams(startParams);
		
		//���ɼ�
		setVisibleOff();
	}
	
	//�õ�һ���ƶ�����
	private Animation translateAnim(Point fromPoint, Point toPoint, long durationMillis) 
	{
		TranslateAnimation anTransformation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.ABSOLUTE, toPoint.x - fromPoint.x, 
																	 Animation.RELATIVE_TO_SELF, 0, Animation.ABSOLUTE, toPoint.y - fromPoint.y);
		
		//����ʱ��
		anTransformation.setDuration(durationMillis);
		
		/*
		 * ���ǣ�animationֻ�ǲ���View ��λͼ��ʾ��bitmap representation���������������ĸı�View��λ��
			����������View�ص���ԭ����λ�ã�setFillAfter �� setFillBefore �����ܽ��������⣬
			ҪʹView���ֶ�������ʱ��״̬����������ı�View�����ԣ����������������ı�View�����ԣ���
			setFillAfter �� setFillBefore ֻ�ܸı䶯��������
			Ϊʲô����setFillAfter �� setFillBefore������������
			����Ϊ�ж�������ԭ�򣬼ٶ�����һ���ƶ��Ķ�������һ�������Ķ�����
			����㲻���ƶ��Ķ�����setFillAfter��Ϊtrue����ô�ƶ�����������
			View��ص�ԭ����λ�õ��������setFillAfter��Ϊtrue�� �ͻ����ƶ�����������λ�õ���
		 */
		anTransformation.setFillAfter(true);
		anTransformation.setFillBefore(false);
		/*
		 * ������˵�����÷�����������һ������Ч��ִ����Ϻ�View����������ֹ��λ�á��÷�����ִ�У�
		 * ��Ҫ����ͨ��setFillEnabled����ʹ�����Ч��������������Ч��
		 */
		anTransformation.setFillEnabled(true);
		
		return anTransformation;
	}

	//�õ�����GradientDrawable 
	private GradientDrawable getBg()
	{
		GradientDrawable gd = new GradientDrawable();//����drawable
	    gd.setColor(fillColor);
	    gd.setCornerRadius(width / 2);
	    gd.setStroke(STROKE_WIDTH, context.getResources().getColor(R.color.stroke_color));
	    
	    return gd;
	}
	
	/////////////////////////////////////////
	//�õ������ɫ
	public int getFillColor()
	{
		return this.fillColor;
	}
}



















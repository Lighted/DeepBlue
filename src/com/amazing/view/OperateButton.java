package com.amazing.view;

import com.amazing.utils.Point;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

//������ť ��������һ��imagebutton һ��textview
public class OperateButton extends RelativeLayout
{
	public final static int CONTINUE_TIME = 150;// ����������ʱ��

	private final static int VIEW_INTER = 20;	//�����ؼ�֮������Ҿ���
	private final static int TEXT_VIEW_WIDTH = 100;	//textview�Ĵ�С
	private final static int TEXT_VIEW_HEIGHT = 50;
	private final static int TEXT_VIEW_SIZE = 25;
	
	////////////////////////////////////////////////
	int textId;	//˵������
	int textViewBgResId = -1;	//���ֱ���
	int textColor = -1;	//������ɫ
	
	int operateBgResId = -1;	//operatebutton��ť����
	int operateImageResId= -1;	//��ť��ͼƬ����
	
	public TextView    textView      = null;	//˵������
	public ImageButton operateButton = null;	//������ť
	
	//��ť�Ŀ��
	int width;
	
	//RelativeLayout˵���õ��ı���
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
	
	Context context    = null;
	AttributeSet attrs = null;

	public OperateButton(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		
		this.context = context;
		this.attrs   = attrs;
	}
	
	public void initParams(int width, int textId, int textViewBgResId, int textColor, int operateBgResId, int operateImageResId)
	{
		this.width             = width;
		this.textId              = textId;
		this.textViewBgResId   = textViewBgResId;
		this.textColor         = textColor;
		this.operateBgResId    = operateBgResId;
		this.operateImageResId = operateImageResId;
		
		initViews();
	}

	//��ʼ�����ְ�ť
	private void initViews()
	{
		//���ְ�ť
		textView = new TextView(context, attrs);
		textView.setText(textId);	//������������
		textView.setTextColor(context.getResources().getColor(textColor));
		textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, TEXT_VIEW_SIZE);	//�����ص�λ���������С
		textView.setGravity(Gravity.CENTER);	//���־���
		textView.setBackgroundResource(textViewBgResId);	//����
		
		RelativeLayout.LayoutParams textLayout = new RelativeLayout.LayoutParams(TEXT_VIEW_WIDTH, TEXT_VIEW_HEIGHT);
		textLayout.leftMargin = -(VIEW_INTER + TEXT_VIEW_WIDTH);
		textLayout.bottomMargin = width / 2 - TEXT_VIEW_HEIGHT / 2;
		//textLayout.leftMargin = 0;
		//textLayout.bottomMargin = 300;
		textLayout.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		textLayout.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		
		textView.setLayoutParams(textLayout);
		
		addView(textView);
		
		//������ť
		operateButton = new ImageButton(context, attrs);
		operateButton.setImageResource(operateImageResId);
		operateButton.setBackgroundResource(operateBgResId);
		
		RelativeLayout.LayoutParams operateLayout = new RelativeLayout.LayoutParams(width, width);
		operateLayout.leftMargin = 0;
		operateLayout.bottomMargin = 0;
		operateLayout.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		operateLayout.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		
		operateButton.setLayoutParams(operateLayout);
		
		addView(operateButton);
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
		textView.setAlpha(1.0f);
		operateButton.setAlpha(1.0f);
	}
	//�����Ƿ�ɼ�
	private void setVisibleOff()
	{
		this.setAlpha(0.0f);
		textView.setAlpha(0.0f);
		operateButton.setAlpha(0.0f);
	}
	
	//��ʼ������
	private void initLayoutParams()
	{
		//��ʼ�Ĳ�����Ϣ
		startParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		startParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		startParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		startParams.leftMargin = startPoint.x;
		startParams.bottomMargin = startPoint.y;
		
		//������Ĳ�����Ϣ
		endParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		endParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		endParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		endParams.leftMargin   = endPoint.x;
		endParams.bottomMargin = endPoint.y;
		
		farParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		farParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		farParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		farParams.leftMargin   = farPoint.x;
		farParams.bottomMargin = farPoint.y;
		
		//����Ϊ��ʼ�Ĳ�����Ϣ
		setStartLayoutParams();
		
		//���ɼ�
		setVisibleOff();
	}
	
	//��ʼ������
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
}
















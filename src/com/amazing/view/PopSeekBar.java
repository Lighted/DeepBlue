package com.amazing.view;

import com.amazing.utils.Point;
import com.amazing.utils.PopView;
import com.amazing.utils.PopViewStatus;
import com.amazing.utils.PopViewStatus.POP_STATUS;

import android.content.Context;

import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

//�������϶���
public class PopSeekBar extends RelativeLayout  implements PopView
{
	//�϶������������Ļ��Ե�ľ���
	private final static int VIEW_INTER = 15;	//view֮��ļ��
	private final static int SEEKBAR_HEIGHT = 500;	//bar�ĸ߶�
	private final static int FAR_END_INTER = 110;	//ÿ��������ť����Զ���� �� ���վ���Ĳ�ֵ ������ʾ�ص�Ч��
	public final static int CONTINUE_TIME = 150;// ����������ʱ��

	//��Դid
	private int controlResId = -1;	//���ư�ť����Դid
	private int closeResId = -1;	//����ť����ʽ ��ʾ�Ĺر���Դid
	
	//��Ӧ�İ�ť
	private ImageButton controlButton = null;	//���ư�ť
	private VerticalSeekBarWrapper     seekBarWrapper  = null;	//�϶���
	private VerticalSeekBar seekBar = null;

	//���ư�ť�Ŀ��
	int width;
	
	//controlButton����ڷ�layout��ƫ����ֵ
	private int left = 0;
	private int bottom = 0;
	
	//���ڶ���������
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
	
	/*
	//״̬��Ϣ
	//boolean isExpand = false;
	private enum POP_STATUS
	{
		SHRINKED,	//�Ѿ�����״̬
		SHRINKING,	//��������
		EXPANDED,	//�Ѿ���չ״̬
		EXPANDING, 	//������չ״̬
	};
	
	private POP_STATUS popStatus = POP_STATUS.SHRINKED;*/
	
	private PopViewStatus popViewStatus = null;
	
	//���ڶ����Ļص�
	private Handler mHandler = new Handler();
	
	private Context context = null;
	private AttributeSet attrs = null;

	public PopSeekBar(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		
		this.context = context;
		this.attrs   = attrs;
	}
	
	public PopView getSef()
	{
		return this;
	}
	
	
	////////////////////////////////////////////////////////////////////
	//������RelativeLayout �Ĵ����¼�
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if(POP_STATUS.EXPANDING == popViewStatus.popStatus || POP_STATUS.SHRINKING == popViewStatus.popStatus)
			return true;
		
		if(POP_STATUS.EXPANDED == popViewStatus.popStatus && this != popViewStatus.popView)
			return false;
		
		//����Ѿ��пؼ����ڵ���״̬ ��
		if(POP_STATUS.EXPANDED == popViewStatus.popStatus && this == popViewStatus.popView)
		{
			//���õ�ǰ���ڵ���״̬�Ŀؼ����� ����
			popViewStatus.popView.executeShrink();
			
			return true;
		}

		return super.onTouchEvent(event);
	}
	
	//����true��ʾ ���� ��view���� �����¼� ���� ��onTouchEvent����
	@Override
	public boolean onInterceptTouchEvent(MotionEvent event)
	{
		if(POP_STATUS.EXPANDING == popViewStatus.popStatus || POP_STATUS.SHRINKING == popViewStatus.popStatus)
			return true;
		
		if(POP_STATUS.EXPANDED == popViewStatus.popStatus && this != popViewStatus.popView)
			return true;
		
		if(POP_STATUS.EXPANDED == popViewStatus.popStatus && this == popViewStatus.popView)
			return false;

		return super.onInterceptTouchEvent(event);
	}
	
	//��ʼ����Ҫ�Ĳ��� 
	public void initParams(int width, int left, int bottom, int controlResId, int closeResId, ImageButton controlButton, VerticalSeekBarWrapper seekBarWrapper, VerticalSeekBar seekBar, PopViewStatus popViewStatus)
	{
		this.width = width;
		this.left = left;
		this.bottom = bottom; 
		
		this.controlResId = controlResId;
		this.closeResId   = closeResId;
		
		this.controlButton = controlButton;
		this.seekBarWrapper = seekBarWrapper;
		this.seekBar = seekBar;
		
		this.popViewStatus = popViewStatus;
		
		initPoints();
		initLayoutParams();
		initAnimation();
		initViews();
	}
	
	private void initPoints()
	{
		startPoint = new Point(left, -(SEEKBAR_HEIGHT - VIEW_INTER));
		endPoint   = new Point(left, bottom + width + VIEW_INTER);
		farPoint   = new Point(left, endPoint.y + FAR_END_INTER);
	}
	
	//��ʼ��������Ϣ
	private void initLayoutParams()
	{
		//��RelativeLayout�Ĳ�����Ϣ
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		lp.leftMargin = 0;
		lp.bottomMargin = 0;
		lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		
		this.setLayoutParams(lp);
		
		//��ʼ�Ĳ�����Ϣ
		startParams = new RelativeLayout.LayoutParams(width, SEEKBAR_HEIGHT);
		startParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		startParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		startParams.leftMargin = startPoint.x;
		startParams.bottomMargin = startPoint.y;
		
		//������Ĳ�����Ϣ
		endParams = new RelativeLayout.LayoutParams(width, SEEKBAR_HEIGHT);
		endParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		endParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		endParams.leftMargin   = endPoint.x;
		endParams.bottomMargin = endPoint.y;
		
		farParams = new RelativeLayout.LayoutParams(width, SEEKBAR_HEIGHT);
		farParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		farParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		farParams.leftMargin   = farPoint.x;
		farParams.bottomMargin = farPoint.y;
	}
	
	//ִ����չ����
	public void expandAnim()
	{
		seekBarWrapper.startAnimation(farAnim);
	}
	
	//��������
	public void shrinkAnim()
	{
		seekBarWrapper.startAnimation(startAnim);
	}
	
	//��ʼ��far ��end����
	private void startFarToEndAnim()
	{
		seekBarWrapper.startAnimation(endAnim);
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
				seekBar.setEnabled(false);
			}
			
			@Override
			public void onAnimationRepeat(Animation arg0) {
			}
			
			@Override
			public void onAnimationEnd(Animation arg0) 
			{
				//����������ִ�лص�
				seekBarWrapper.clearAnimation();
				seekBarWrapper.setLayoutParams(startParams);
				seekBarWrapper.setAlpha(0.0f);
				
				//״̬
				popViewStatus.popStatus = POP_STATUS.SHRINKED;
				popViewStatus.popView = null;
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
				seekBarWrapper.clearAnimation();
				seekBarWrapper.setLayoutParams(endParams);
				seekBar.setEnabled(true);
				
				popViewStatus.popStatus = POP_STATUS.EXPANDED;
				popViewStatus.popView = getSef();
			}
		});
		
		farAnim.setAnimationListener(new AnimationListener() 
		{
			@Override
			public void onAnimationStart(Animation arg0) 
			{
				seekBarWrapper.setAlpha(1.0f);
			}
			
			@Override
			public void onAnimationRepeat(Animation arg0) {
			}
			
			@Override
			public void onAnimationEnd(Animation arg0) 
			{
				//����������ִ�лص�
				seekBarWrapper.clearAnimation();
				seekBarWrapper.setLayoutParams(farParams);
				
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
	
	//��������
	private void initViews()
	{
		//control�Ĳ����ļ�
		RelativeLayout.LayoutParams controlLayout = new RelativeLayout.LayoutParams(width, width);
		controlLayout.leftMargin = left;
		controlLayout.bottomMargin = bottom;
		controlLayout.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		controlLayout.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		
		controlButton.setLayoutParams(controlLayout);
		controlButton.setOnClickListener(controlClickListener);
		
		//seekBarWrapper�����ļ�
		seekBarWrapper.setLayoutParams(startParams);
		seekBarWrapper.setAlpha(0.0f);
		seekBar.setEnabled(false);
	}
	
	//���ֻص�����
	private View.OnClickListener controlClickListener = new View.OnClickListener() 
	{
		@Override
		public void onClick(View v)
		{
			if(POP_STATUS.EXPANDING == popViewStatus.popStatus || POP_STATUS.SHRINKING == popViewStatus.popStatus)
				return;
			
			//����״̬�趨ִ�еĶ���Ч��
			if(POP_STATUS.EXPANDED == popViewStatus.popStatus)
			{
				//���ùر�ͼ��
				controlButton.setImageResource(controlResId);
				
				popViewStatus.popStatus = POP_STATUS.SHRINKING;
				mHandler.post(new PopRunnable());
			}
			else if(POP_STATUS.SHRINKED == popViewStatus.popStatus)
			{
				//��������ͼ��
				controlButton.setImageResource(closeResId);
				
				popViewStatus.popStatus = POP_STATUS.EXPANDING;
				mHandler.post(new PopRunnable());
			}
		}
	};
	
	//����һ���ڲ��� ��ִ�ж����Ĳ���
	private class PopRunnable implements Runnable
	{
		@Override
		public void run() 
		{
			//ִ�ж���
			if(POP_STATUS.EXPANDING == popViewStatus.popStatus)
				expandAnim();
			else if(POP_STATUS.SHRINKING == popViewStatus.popStatus)
				shrinkAnim();
		}
	}
	
	///////////////////////////////////////////////////////////////////
	///ִ�� ��չ�������� ������纯�� ����
	@Override
	public void executeExpand()
	{
	
	}
	
	@Override
	public void executeShrink()
	{
		//���ùر�ͼ��
		controlButton.setImageResource(controlResId);
		
		popViewStatus.popStatus = POP_STATUS.SHRINKING;
		mHandler.post(new PopRunnable());
	}

}






















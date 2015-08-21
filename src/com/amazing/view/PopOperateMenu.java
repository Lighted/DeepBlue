package com.amazing.view;

import java.util.ArrayList;

import com.amazing.utils.Point;
import com.amazing.utils.PopOperateMenuListener;
import com.amazing.utils.PopView;
import com.amazing.utils.PopViewStatus;
import com.amazing.utils.PopViewStatus.POP_STATUS;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

//�Զ���ĵ���������ť
public class PopOperateMenu extends RelativeLayout  implements PopView
{
	private final static int BUTTON_INTER = 15;	//��ť֮��ļ����λΪ����
	private final static int FAR_END_INTER = 110;	//ÿ��������ť����Զ���� �� ���վ���Ĳ�ֵ ������ʾ�ص�Ч��
	private static final int DELAYED_TIME = 30;// �ӳٶ�����ʱ��

	int[] textIds;	//����id����
	int[] textColors;	//������ɫ
	int textViewBgId = -1;	//���ֱ��� ֻ��һ��
	
	int[] oeprateBgResIds;	//��ť�ı�������
	int[] operateImageResIds;	//��ť��ǰ��ͼƬ
	
	int controlBgResId = -1;
	int controlImageResId = -1;
	
	ArrayList<OperateButton> popButtons;	//���еİ�ť����
	Button controlButton = null;	//���ư�ť
	
	//������Ϣ
	int width;
	int left;
	int bottom;

	private PopViewStatus popViewStatus = null;
	
	//�ص�����
	PopOperateMenuListener popOperateMenuListener = null;
	
	//���ڶ����Ļص�
	private Handler mHandler = new Handler();
	
	Context context = null;
	AttributeSet attrs = null;
	
	public PopOperateMenu(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		
		this.context = context;
		this.attrs   = attrs;
	}
	
	public PopView getSef()
	{
		return this;
	}

	public void initParams(int width, int left, int bottom, int textViewBgId,
			int[] textIds, int[] textColors, int[] oeprateBgResIds, int[] operateImageResIds,
			int controlBgResId, int controlImageResId,
			PopViewStatus popViewStatus)
	{
		this.width  = width;
		this.left   = left;
		this.bottom = bottom;
		
		this.textViewBgId = textViewBgId;
		this.textIds = textIds;
		this.textColors = textColors;
		this.oeprateBgResIds = oeprateBgResIds;
		this.operateImageResIds = operateImageResIds;
		this.controlBgResId = controlBgResId;
		this.controlImageResId = controlImageResId;
		
		this.popViewStatus = popViewStatus;
		
		initViews();
	}

	//��ʼ������views
	private void initViews()
	{
		//��ʼ����view
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		lp.leftMargin = 0;
		lp.bottomMargin = 0;
		lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		
		this.setLayoutParams(lp);
		this.setClipChildren(false);
		
		int startX = left;
		int startY = bottom;
		int x = startX;
		int y = startY;
		int delta = width + BUTTON_INTER;
		
		popButtons = new ArrayList<OperateButton>();
		
		//��Ӹ���items�İ�ť
		int size = textIds.length;
		for(int i = 0; i < size; ++i)
		{
			y += delta;
			
			OperateButton operateButton = new OperateButton(context, attrs);
			operateButton.setClipChildren(false);
			operateButton.initParams(width, textIds[i], textViewBgId, textColors[i], oeprateBgResIds[i], operateImageResIds[i]);
			operateButton.setPoints(new Point(startX, startY), new Point(x, y), new Point(x, y + FAR_END_INTER));
			
			//���ñ��
			operateButton.operateButton.setTag(i);
			
			//��ť�ص�
			operateButton.operateButton.setOnClickListener(itemClickListener);
			
			addView(operateButton);
			popButtons.add(operateButton);
		}
		
		//controlbutton�ĳ�ʼ��
		//��ʼ����view
		RelativeLayout.LayoutParams controlLayout = new RelativeLayout.LayoutParams(width, width);
		controlLayout.leftMargin = startX;
		controlLayout.bottomMargin = startY;
		controlLayout.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		controlLayout.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		
		controlButton = new Button(context, attrs);
		controlButton.setBackgroundResource(controlBgResId);
		
		controlButton.setLayoutParams(controlLayout);
		
		controlButton.setOnClickListener(controlClickListener);
		
		addView(controlButton);		//�ؼ�����Ӱ�ť
	}
	
	//--------------------------------------------------------------
	public void setPopOperateMenuListener(PopOperateMenuListener popOperateMenuListener)
	{
		this.popOperateMenuListener = popOperateMenuListener;
	}
	
	//���ְ�ť�Ļص�
	private View.OnClickListener itemClickListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View view) 
		{
			if(POP_STATUS.EXPANDED != popViewStatus.popStatus)
				return;
			
			//�õ�����
			int index = Integer.parseInt(String.valueOf(view.getTag()));
			
			if(0 <= index && index < popButtons.size())
			{
				//�õ���ť
				OperateButton button = popButtons.get(index);
				
				//ִ�лص�
				if(null != popOperateMenuListener)
				{
					popOperateMenuListener.onClickItem(button, index);
				}
			}
		}
	};
	
	
	
	
	
	///////////////////////////////////////////////////////////////////
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
	
	
	
	//���ֻص�����
	private View.OnClickListener controlClickListener = new View.OnClickListener() 
	{
		@Override
		public void onClick(View v)
		{
			//���ڶ���ֱ������
			if(POP_STATUS.SHRINKING == popViewStatus.popStatus || POP_STATUS.EXPANDING == popViewStatus.popStatus)	
				return;
			
			//����״̬�趨ִ�еĶ���Ч��
			if(POP_STATUS.SHRINKED == popViewStatus.popStatus)
				expand();
			else if(POP_STATUS.EXPANDED == popViewStatus.popStatus)
				shrink();
		}
	};
	
	//ִ����չ����
	private void expand()
	{
		//�޸�״̬
		popViewStatus.popStatus = POP_STATUS.EXPANDING;
		
		int size = popButtons.size();
		for(int i = size - 1; i >= 0; --i)
		{
			//�ӳ�ʱ�䷢��һ����������
			mHandler.postDelayed(new PopRunnable(i), DELAYED_TIME * (size - 1 - i));
		}

		//�޸�״̬
		mHandler.postDelayed(new PopRunnable(size), DELAYED_TIME * (size - 1) + 2 * OperateButton.CONTINUE_TIME);
	}
	
	//��������
	private void shrink()
	{
		//�޸�״̬
		popViewStatus.popStatus = POP_STATUS.SHRINKING;

		int size = popButtons.size();
		for(int i = 0; i < size; ++i)
		{
			mHandler.postDelayed(new PopRunnable(i), DELAYED_TIME * i);
		}
		
		//�ı�״̬��Ϣ
		mHandler.postDelayed(new PopRunnable(size), DELAYED_TIME * (size - 1) + OperateButton.CONTINUE_TIME);
	}
	
	//����һ���ڲ��� ��ִ�ж����Ĳ���
	private class PopRunnable implements Runnable
	{
		int index;
		
		public PopRunnable(int index) 
		{
			this.index = index;
		}
		
		@Override
		public void run() 
		{
			int size = popButtons.size();
			if(index == size)
			{
				//����״̬
				if(POP_STATUS.EXPANDING == popViewStatus.popStatus)
				{
					popViewStatus.popStatus = POP_STATUS.EXPANDED;
					popViewStatus.popView = getSef();
				}
					
				else if(POP_STATUS.SHRINKING == popViewStatus.popStatus)
				{
					popViewStatus.popStatus = POP_STATUS.SHRINKED;
					popViewStatus.popView = null;
				}
					
			}
			else if(0 <= index && index < size)
			{
				OperateButton button = null;

				button = popButtons.get(index);
				
				//����״̬���ò�ͬ�Ķ���
				if(POP_STATUS.EXPANDING == popViewStatus.popStatus)
					button.expandAnim();
				else if(POP_STATUS.SHRINKING == popViewStatus.popStatus)
					button.shrinkAnim();
			}
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
		shrink();
	}
}




















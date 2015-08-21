package com.amazing.view;

import java.util.ArrayList;

import com.amazing.deepblue.R;
import com.amazing.utils.Point;
import com.amazing.utils.PopMenuListener;
import com.amazing.utils.PopView;
import com.amazing.utils.PopViewStatus;
import com.amazing.utils.PopViewStatus.POP_STATUS;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

//�Զ���˵���
public class PopMenu extends RelativeLayout implements PopView
{
	private final static int BUTTON_INTER = 6;	//��ť֮��ļ����λΪ����
	private final static int FAR_END_INTER = 110;	//ÿ��������ť����Զ���� �� ���վ���Ĳ�ֵ ������ʾ�ص�Ч��
	private static final int DELAYED_TIME = 20;// �ӳٶ�����ʱ��
	
	//�Զ���ؼ�����ǰ�ؼ��������һ�����ư�ť ����������ť�Ƿ����� ���� ����һ��add��ť �˰�ť���������������
	//��ѡ��ϣ�����ѡ����� ����ͬʱѡ�����ֻ��ѡ��һ��
	
	private int selectResId = -1;	//ѡ��ť ��ĳһ����ť��ѡ���� ��������ʾ��ԴselectResId
	private int controlResId = -1;	//���ư�ť����Դid
	private int closeResId = -1;	//����ť����ʽ ��ʾ�Ĺر���Դid
	private int addResId = -1;	//��Ӱ�ť����Դid
	
	private ArrayList<Integer> colors = null;
	private Context context = null;
	private AttributeSet attrs = null;
	
	//���ְ�ť
	private ImageButton controlButton = null;	
	private ColorButton addButton = null;	//������������
	private ArrayList<ColorButton> popButtons = null;	//��ѡ��ť Ҫôѡ��һ�� Ҫ�𶼲�ѡ
	
	private int width;	//���еİ�ť��ΪԲ�� width��ʾΪ��� �� �߶�
	
	//RelativeLayout����ڸ��ؼ��Ĳ���
	private int left;
	private int bottom;
	
	//ִ�и��ֻص� �� ѡ��״̬
	private int selectIndex = -1;	//0,size-1��ʾ ѡ���Ӧ�İ�ť -1��ʾû���κ�ѡ��
	private PopMenuListener popMenuListener = null;

	private PopViewStatus popViewStatus = null;
	
	//���ڶ����Ļص�
	private Handler mHandler = new Handler();

	public PopMenu(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		
		this.context = context;
		this.attrs   = attrs;
	}
	
	public PopView getSef()
	{
		return this;
	}
	
	//��� ���ư�ť���� ��Ӱ�ť���� ��ɫ
	public void initParams(int width, int left, int bottom, int selectResId, int controlResId, int closeResId, int addResId, ArrayList<Integer> colors, PopViewStatus popViewStatus)
	{
		this.width        = width;
		this.left         = left;
		this.bottom       = bottom;
		
		this.selectResId  = selectResId;
		this.controlResId = controlResId;
		this.closeResId   = closeResId;
		this.addResId     = addResId;
		this.colors       = colors;
		
		this.popViewStatus = popViewStatus;
		
		initLayoutParams();
		addButtons();
	}
	
	//���ݴ����left bottom���ñ��ռ�
	private void initLayoutParams()
	{
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		lp.leftMargin = 0;
		lp.bottomMargin = 0;
		lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		
		this.setLayoutParams(lp);
	}

	//��ʼ�����ֲ��� ����Ӱ�ť
	private void addButtons()
	{
		//������Ϣ
		int startX = left;
		int startY = bottom;
		int x = startX;
		int y = startY;
		int delta = width + BUTTON_INTER;
		
		//���ȳ�ʼ��addButton
		//���ֲ���
		if(-1 != addResId)
		{
			addButton = new ColorButton(Color.WHITE, width, context, attrs);
			addButton.setBackgroundResource(R.drawable.add_button_bg_selector);
			
			y += delta;
			addButton.setPoints(new Point(startX, startY), new Point(x, y), new Point(x, y + FAR_END_INTER));
			addButton.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);
			addButton.setImageResource(addResId);
			
			//���ûص�
			addButton.setOnClickListener( addClickListener );
			
			//���ñ��
			addButton.setTag(-1);
			
			//��Ӱ�ť
			addView(addButton);
		}
		
		//������ť
		popButtons = new ArrayList<ColorButton>();
		
		int size = colors.size();
		for(int i = 0; i < size; ++i)
		{
			//��ɫ��ť
			ColorButton popButton = new ColorButton(colors.get(i).intValue(), width, context, attrs);
			
			y += delta;
			popButton.setPoints(new Point(startX, startY), new Point(x, y), new Point(x, y + FAR_END_INTER));
			popButton.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);
			
			//���ñ��
			popButton.setTag(i);
			addView(popButton);
			
			//���ûص�����
			popButton.setOnTouchListener(itemTouchListener);
			
			//��ӵ�����
			popButtons.add(popButton);
		}
		
		//���ư�ť�ĳ�ʼ��
		RelativeLayout.LayoutParams controlParams = new RelativeLayout.LayoutParams(width, width);
		controlParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		controlParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		controlParams.leftMargin   = startX;
		controlParams.bottomMargin = startY;

		controlButton = new ImageButton(context, attrs);
		controlButton.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);
		controlButton.setImageResource(controlResId);
		controlButton.setLayoutParams(controlParams);
		controlButton.setBackgroundResource(R.drawable.button_bg_selector);
		
		//�ص�����
		controlButton.setOnClickListener(controlClickListener);	
		
		addView(controlButton);		//�ؼ�����Ӱ�ť
	}
	
	//--------------------------------------------------------
	//��������������ɫֵ
	public int getColorByIndex(int index)
	{
		if(index < 0 || index >= popButtons.size())
			index = 0;
		
		return popButtons.get(index).getFillColor();
	}
	
	//-------------------------------------
	//���õ�ǰ������
	public void setIndexSelected(int index)
	{
		//����뵱ǰ��ѡ��������ֱͬ�ӷ���
		if(index == selectIndex) return;
		
		if(0 <= index && index <= popButtons.size())
		{
			//�õ�����
			ColorButton button = popButtons.get(index);
			
			//�Ѿ�ѡ��һ����ť
			if(0 <= selectIndex && selectIndex < popButtons.size())
			{
				//����ǰ��������ʾ
				popButtons.get(selectIndex).setImageAlpha(0);
			}
			
			selectIndex = index;
			button.setImageAlpha(255);
			
			if(Color.WHITE == button.getFillColor())
				button.setImageResource(R.drawable.ic_action_tick_black);
			else
				button.setImageResource(selectResId);
		}
	}
	
	//ȡ��ѡ�� �� ʲô����ѡ
	public void cancelSelected()
	{
		//�Ѿ�ѡ��һ����ť
		if(0 <= selectIndex && selectIndex < popButtons.size())
		{
			//����ǰ��������ʾ
			popButtons.get(selectIndex).setImageAlpha(0);
			
			selectIndex = -1;
		}
	}
	
	//�õ���ǰѡ��
	public int getSelectedIndex()
	{
		return selectIndex;
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
			{
				//���ùر�ͼ��
				controlButton.setImageResource(closeResId);
				
				expand();
			}
				
			else if(POP_STATUS.EXPANDED == popViewStatus.popStatus)
			{
				//��������ͼ��
				controlButton.setImageResource(controlResId);
				
				shrink();
			}
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

		//����һ��-1��Ϣ addButton�Ķ���Ч��
		if(null != addButton)
		{
			mHandler.postDelayed(new PopRunnable(-1), DELAYED_TIME * size);
			
			//����һ��size��Ϣ��ʾ �ı�״̬
			mHandler.postDelayed(new PopRunnable(size), DELAYED_TIME * size + 2 * ColorButton.CONTINUE_TIME);
		}
		else
		{
			//����һ��size��Ϣ��ʾ �ı�״̬
			mHandler.postDelayed(new PopRunnable(size), DELAYED_TIME * (size - 1) + 2 * ColorButton.CONTINUE_TIME);
		}
		
		
	}
	
	//��������
	private void shrink()
	{
		//�޸�״̬
		popViewStatus.popStatus = POP_STATUS.SHRINKING;
		
		long delayed_time = 0;
		if(null != addButton)
		{
			mHandler.postDelayed(new PopRunnable(-1), 0);
			delayed_time += DELAYED_TIME;
		}
		
		int size = popButtons.size();
		for(int i = 0; i < size; ++i)
		{
			mHandler.postDelayed(new PopRunnable(i), DELAYED_TIME * i + delayed_time);
		}
		
		//�ı�״̬��Ϣ
		mHandler.postDelayed(new PopRunnable(size), DELAYED_TIME * (size - 1) + delayed_time + ColorButton.CONTINUE_TIME);
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
			else if(-1 <= index && index < size)
			{
				ColorButton button = null;
				
				if(-1 == index)
					button = addButton;
				else
					button = popButtons.get(index);
				
				//����״̬���ò�ͬ�Ķ���
				if(POP_STATUS.EXPANDING == popViewStatus.popStatus)
					button.expandAnim();
				else if(POP_STATUS.SHRINKING == popViewStatus.popStatus)
					button.shrinkAnim();
			}
		}
	}
	
	///////////////////////////////////////////////////
	//���ü����ӿ�
	public void setPopMenuListener(PopMenuListener popMenuListener)
	{
		this.popMenuListener = popMenuListener;
	}
	
	//��Ӱ�ť�Ļػص�
	private View.OnClickListener addClickListener = new View.OnClickListener() 
	{
		@Override
		public void onClick(View arg0)
		{
			// TODO Auto-generated method stub
			//ִ�лص�
			if(null != popMenuListener)
				popMenuListener.onClickAddButton();
		}
	};
	
	//ѡ��ť�Ļص�
	private View.OnTouchListener itemTouchListener = new View.OnTouchListener()
	{
		@Override
		public boolean onTouch(View view, MotionEvent event) 
		{
			if(POP_STATUS.EXPANDED != popViewStatus.popStatus)
				return false;
			
			if(event.getAction() != MotionEvent.ACTION_DOWN)
				return false;
			
			//�õ�����
			int index = Integer.parseInt(String.valueOf(view.getTag()));
			
			//����뵱ǰ��ѡ��������ֱͬ�ӷ���
			if(index == selectIndex)	return false;
			
			if(0 <= index && index <= popButtons.size())
			{
				//�õ�����
				ColorButton button = popButtons.get(index);
				
				//�Ѿ�ѡ��һ����ť
				if(0 <= selectIndex && selectIndex < popButtons.size())
				{
					//����ǰ��������ʾ
					popButtons.get(selectIndex).setImageAlpha(0);
				}
				
				selectIndex = index;
				button.setImageAlpha(255);
				
				if(Color.WHITE == button.getFillColor())
					button.setImageResource(R.drawable.ic_action_tick_black);
				else
					button.setImageResource(selectResId);

				//ִ�лص�
				if(null != popMenuListener)
					popMenuListener.onSelectItem(button, index, button.getFillColor());
			}
			
			return false;
		}
	};
		
	
	///////////////////////////////////////////////////////////////////
	///ִ�� ��չ�������� ������纯�� ����
	@Override
	public void executeExpand()
	{
		
	}
	
	@Override
	public void executeShrink()
	{
		//��������ͼ��
		controlButton.setImageResource(controlResId);
		
		shrink();
	}

}

















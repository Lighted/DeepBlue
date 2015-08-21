package com.amazing.view;

import com.amazing.deepblue.R;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.EditText;

/*
 * ����Ӧ ��EditText
 */
public class AdaptEditText extends EditText
{	
	//��ؼ��Ŀ�͸�
	int parentWidth;
	int parentHeight;

	//���ڼ����ַ����ĳ���
	TextPaint fontPaint = null;
	
	//���ڲü� ���ֵľ���
	float clipLeft;
	float clipTop;
	float clipRight;
	float clipBottom;
	float dx;	//���л��Ƶ�ʱ���ƫ����
	float dy;
	
	Context context = null;
	AttributeSet attrs = null;
	
	public AdaptEditText(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		
		this.context = context;
		this.attrs = attrs;
	}

	public void initParams(int parentWidth, int parentHeight)
	{
		this.parentWidth = parentWidth;
		this.parentHeight = parentHeight;
		
		fontPaint = new TextPaint();
				
		//��������
		setFont();

		//���ò���
		setPadding();
		
		//���ü���
		this.addTextChangedListener(textWatcher);
	}
	
	//�ü�����
	public RectF getClipRectF()
	{
		//-------------------------
				//ͨ����Щ���� ������õ� ���ֵĿ��ӷ�Χ
				//ֱ�� ����textview ��ondraw����
				/*int compoundPaddingLeft = getCompoundPaddingLeft();
		        int compoundPaddingTop = getCompoundPaddingTop();
		        int compoundPaddingRight = getCompoundPaddingRight();
		        int compoundPaddingBottom = getCompoundPaddingBottom();
		        int scrollX = getScrollX();
		        int scrollY = getScrollY();
		        int right = getRight();
		        int left = getLeft();
		        int bottom = getBottom();
		        int top = getTop();
				
		        int extendedPaddingTop = getExtendedPaddingTop();
		        int extendedPaddingBottom = getExtendedPaddingBottom();

		        Layout layout = getLayout();
		        
		        int vspace = bottom - top - compoundPaddingBottom - compoundPaddingTop;
		        int maxScrollY = layout.getHeight() - vspace;

		        clipLeft = compoundPaddingLeft + scrollX;
		        clipTop = (scrollY == 0) ? 0 : extendedPaddingTop + scrollY;
		        clipRight = right - left - compoundPaddingRight + scrollX;
		        clipBottom = bottom - top + scrollY - ((scrollY == maxScrollY) ? 0 : extendedPaddingBottom);
		        
		        dx = getTotalPaddingLeft();
		        dy = getTotalPaddingTop();
		        
		        Rect r = new Rect();
		        getLocalVisibleRect(r);
		        
		        System.out.println("clipLeft r.left��" + clipLeft + ", " + r.left);
		        System.out.println("clipTop r.top��" + clipTop + ", " + r.top);
		        System.out.println("clipRight r.right��" + clipRight + ", " + r.right);
		        System.out.println("clipBottom r.bottom��" + clipBottom + ", " + r.bottom);
		        
		        System.out.println("dx��" + dx);
		        System.out.println("dy��" + dy);
		        System.out.println("------------------------------------------");
		        
		        /*System.out.println("clipLeft��" + clipLeft);
		        System.out.println("clipTop��" + clipTop);
		        System.out.println("clipRight��" + clipRight);
		        System.out.println("clipBottom��" + clipBottom);
		        
		        System.out.println("dx��" + dx);
		        System.out.println("dy��" + dy);
		        System.out.println("------------------------------------------");*/
		        
		        
		        /*if(layout.getHeight() < parentHeight)
		        	dy = (parentHeight - layout.getHeight()) >> 1;
		        else
		        	dy = 0;*/
		        
		        //clipRect.left = clipLeft;
		        
		        //���� �� ����
		        /*System.out.println("clipLeft��" + clipLeft);
		        System.out.println("clipTop��" + clipTop);
		        System.out.println("clipRight��" + clipRight);
		        System.out.println("clipBottom��" + clipBottom);*/
		        
		        //���� ���� 
		        /*Rect r = new Rect();
				getLocalVisibleRect(r);

				System.out.println("r.top��" + r.top);
				System.out.println("r.bottom��" + r.bottom);
				System.out.println("r.left��" + r.left);
				System.out.println("r.right��" + r.right);*/
		
		int compoundPaddingLeft = getCompoundPaddingLeft();
        int compoundPaddingRight = getCompoundPaddingRight();
        int scrollX = getScrollX();
        int right = getRight();
        int left = getLeft();

        //���ӷ�Χ
        Rect visibleRect = new Rect();
        getLocalVisibleRect(visibleRect);
		
		RectF rect = new RectF();
		
		rect.left   = compoundPaddingLeft + scrollX;
		rect.top    = visibleRect.top;
		rect.right  = right - left - compoundPaddingRight + scrollX;
		rect.bottom = visibleRect.bottom;
		
		return rect;
	}
	
	public int getTextVisibleWidth()
	{
		return getRight() - getLeft() - getCompoundPaddingRight() - getCompoundPaddingLeft();
	}
	
	//ƫ����
	public float getDx()
	{
		//x��ƫ���� 
		return getTotalPaddingLeft();
	}
	
	public float getDy()
	{
		//y���ƫ����
		float dy = getTotalPaddingTop();
		
		//���dy����0 ��ʾ �����Ѿ�������Ļ�ϲ�
		if(0 == dy)
		{
			Rect vRect = new Rect();
			getLocalVisibleRect(vRect);
			
			dy = -vRect.top;
		}
		
		return dy;
	}

	public void setAdaptTextSize(int textSize)
	{
		setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		setPadding();
	}
	
	private void setFont()
	{
		Typeface typeface = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.font_path));
		this.setTypeface(typeface);
	}

	//��ʼ�������ļ� 
	private void setPadding()
	{
		int length = (int)getMaxTextWidth(getText().toString(), getTextSize());

		int padding = (parentWidth - length) / 2;
		this.setPadding(padding, 0, 0, 0);
	}
	
	//�õ�text��û�л��е���󳤶�
	private float getMaxTextWidth(String text, float size)
	{
		fontPaint.setTextSize(size);
		
		float maxWidth =  0;
		
		int start = 0;
		int pos = -1;
		while(-1 != (pos = text.indexOf("\n", start)))
		{
			maxWidth = Math.max(maxWidth, fontPaint.measureText(text.substring(start, pos)));
			
			start = pos + 2;
		}
		
		if(start < text.length())
		{
			maxWidth = Math.max(maxWidth, fontPaint.measureText(text.substring(start, text.length())));
		}
		
		//------------------------------------
		if(maxWidth > parentWidth)
			maxWidth = (int)(parentWidth / size) * size;
		
		return maxWidth;
		
		
	}
	
	TextWatcher textWatcher = new TextWatcher()
	{
		@Override
		public void afterTextChanged(Editable arg0) 
		{
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) 
		{
		}

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) 
		{
			setPadding();
		}
	};
}















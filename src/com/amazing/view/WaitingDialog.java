package com.amazing.view;

import com.amazing.deepblue.R;

import android.app.Dialog;
import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

/*
 * �ȴ����� ��ʾһ����ת�Ķ��� ��ʾ�ȴ�
 */
public class WaitingDialog extends Dialog 
{
	//������ת��ͼƬ
	ImageView imageView = null;
	
	//��ת����
	RotateAnimation animation = null;

	public WaitingDialog(Context context, int theme) 
	{
		super(context, theme);
		
		//���ò����ļ�
		setContentView(R.layout.waiting_dialog);
		
		//���Ƶ���հ�����Ի����Ƿ���ʧ
		setCanceledOnTouchOutside(false);
				
		//�����ǿ��Ƶ�����ؼ��Ի����Ƿ���ʧ
		setCancelable(false);
		
		//�õ��ؼ�
		imageView = (ImageView)findViewById(R.id.waiting_image_view);
		
		//��ʼ������
		animation =new RotateAnimation(0f,360f,Animation.RELATIVE_TO_SELF, 
                0.5f,Animation.RELATIVE_TO_SELF,0.5f); 
		animation.setDuration(800);//���ö�������ʱ�� 
		animation.setRepeatCount(-1);//�����ظ����� 
		animation.setFillAfter(true);//����ִ������Ƿ�ͣ����ִ�����״̬ 
	}

	@Override
	public void show()
	{
		super.show();
		
		imageView.startAnimation(animation);
	}
	
	@Override
	public void dismiss()
	{
		super.dismiss();
		
		imageView.clearAnimation();
	}
}






















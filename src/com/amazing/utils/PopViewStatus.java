package com.amazing.utils;

//״̬  5����ť ֻ��һ��״̬ 
public class PopViewStatus 
{
	static public enum POP_STATUS
	{
		SHRINKED,	//�Ѿ�����״̬
		SHRINKING,	//��������
		EXPANDED,	//�Ѿ���չ״̬
		EXPANDING, 	//������չ״̬
	};
	
	public POP_STATUS popStatus = POP_STATUS.SHRINKED;
	public PopView popView = null;
}

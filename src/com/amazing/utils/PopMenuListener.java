package com.amazing.utils;

import com.amazing.view.ColorButton;

//�û�PopMenu�Ļص�
public interface PopMenuListener 
{
	//��ĳ��ѡ��ť��ѡ���� ִ�� view ���� �� ��ɫ
	public void onSelectItem(ColorButton v, int index, int color);
	
	//����Ӱ�ť�����ʱִ��
	public void onClickAddButton();
}

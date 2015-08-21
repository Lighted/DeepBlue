package com.amazing.deepblue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParserException;

import com.amazing.utils.BackgroundStatus;
import com.amazing.utils.Constant;
import com.amazing.utils.ContentPreference;
import com.amazing.utils.DrawText;
import com.amazing.utils.GaussBlur;
import com.amazing.utils.PopMenuListener;
import com.amazing.utils.PopOperateMenuListener;
import com.amazing.utils.PopViewStatus;
import com.amazing.utils.ViewCoord;
import com.amazing.view.AdaptEditText;
import com.amazing.view.ColorButton;
import com.amazing.view.OperateButton;
import com.amazing.view.PopMenu;
import com.amazing.view.PopOperateMenu;
import com.amazing.view.PopSeekBar;
import com.amazing.view.VerticalSeekBar;
import com.amazing.view.VerticalSeekBarWrapper;
import com.amazing.view.WaitingDialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Images.Media;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

@SuppressLint("InlinedApi")
public class MainActivity extends Activity 
{
	//-----------------------------
	//��handle���Ϳ�ʼ���߽���������Ϣ
	private static final int START_WAITING_DIALOG = 1;	//��ʾ�ȴ� ����
	private static final int STOP_WAITING_DIALOG  = 2;	//�����ȴ�����
	private static final int CHANGE_IMAGE_VIEW = 3;		//����iamgeview�µ�ͼƬ
	
	private static final int START_SET_WALLPAPER = 4;	//��ʼ���ñ�ֽ
	private static final int SUCCESS_SET_WALLPAPER = 5;	//�ɹ����������ֽ
	private static final int FAIL_SET_WALLPAPER = 6;	//���������ֽʧ��
	
	private static final int START_SET_LOCKPAPER = 7;	//��ʼ����������ֽ
	private static final int SUCCESS_SET_LOCKPAPER = 8;	//�ɹ�����������ֽ
	private static final int FAIL_SET_LOCKPAPER = 9;	//����������ֽʧ��
	private static final int NOT_SUPPORT_LOCKPAPER = 10;
	
	private static final int START_SAVE_BITMAP = 11;	//��ʼ����ͼƬ
	private static final int SUCCESS_SAVE_BITMAP = 12;	//�ɹ�����ͼƬ
	private static final int FAIL_SAVE_BITMAP = 13;	//����ʧ��
	private static final int NO_SDCARD        = 14;	//û��sdcard
	
	private static final int START_SHARE_BITMAP = 15;	//��ʼ����ͼƬ
	private static final int SUCCESS_SHARE_BITMAP = 16;	//����ɹ�
	private static final int FAIL_SHARE_BITMAP = 17;	//����ʧ��
	private static final int NO_SHARE_SDCARD = 18;
	
	//-----------------------------------------------------
	PopMenu backgroundColorMenu = null;	//�޸ı�����ɫ�İ�ť
	PopMenu fontColorMenu       = null;	//�޸�������ɫ�İ�ť
	
	//��˹ģ����Ҫ�Ŀؼ�����Ӧ�Ŀ��ƺ���
	PopSeekBar             blobPopSeekBar     = null;
	VerticalSeekBarWrapper blobSeekBarWrapper = null;
	VerticalSeekBar        blobSeekBar        = null;
	ImageButton            blobControlButton  = null;
	
	//�ı������С�ؼ�����Ӧ�Ŀ��ƺ���
	PopSeekBar             sizePopSeekBar     = null;
	VerticalSeekBarWrapper sizeSeekBarWrapper = null;
	VerticalSeekBar        sizeSeekBar        = null;
	ImageButton            sizeControlButton  = null;

	//������ť �������� �������� ��������
	PopOperateMenu operate      = null;	
	
	ViewCoord      viewCoord    = null;	//����
	PopViewStatus popViewStatus = null;	//״̬
	
	//�ı�������
	AdaptEditText editText = null;
	
	//��˹ģ������
	GaussBlur gaussBlur = null;
	
	//�����ֻ��Ƶ�ͼƬ
	DrawText drawText = null;
	
	//��ʾ����ͼƬ�Ŀؼ�
	ImageView imageView = null;
	
	//���� ��ȡ�ʹ洢 ��
	ContentPreference contentPreference = null;
	
	//״̬��
	BackgroundStatus backgroundStatus = null;
	
	//�ȴ����� ������ʾ �ȴ�����
	WaitingDialog waitingDialog = null;
	
	//��ת��ť ��ת�� infoҳ��
	ImageButton infoButton = null;
	
	//----------------------------------------------
	//������
	BlobRunnable blobRunnable = null;	//ģ��ͼƬ
	SetWallPaperRunnable setWallPaperRunnable = null;	//���������ֽ
	SaveBitmapRunnable saveBitmapRunnable = null;
	SetLockWallpaperRunnable setLockWallpaperRunnable = null;
	ShareBitmapRunnable shareBitmapRunnable = null;
	
	//����ͼƬ��uri
	Uri shareUri = null;
	
	//���ؼ�������
	RelativeLayout mainLayout = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initButtons();	//��ʼ��info��ť
		initViewsParams();	//��ʼ����Ҫ�Ĳ��� ������Ļ�ֱ���
		initViews();	//��ʼ����Ҫ�İ�ť
		initViewsStatus();	//���ݶ�ȡ��״̬���� �����ؼ���״̬
		initRunnables();
		initViewsListener();	//��Ӱ�ť����
	}
	
	@Override
	protected void onStop()
	{
		super.onStop();
		
		//�洢���ֲ���
		contentPreference.commitParams(backgroundColorMenu.getSelectedIndex(), fontColorMenu.getSelectedIndex(), 
						blobSeekBar.getProgress(), sizeSeekBar.getProgress());
	}
	
	//�ȴ� ���ڵ���ʾ����ʧ
	Handler handler = new Handler()
	{
		public void handleMessage(Message msg) 
		{   
            switch (msg.what) 
            {
            case START_WAITING_DIALOG:
            	waitingDialog.show();
            	break;
            case STOP_WAITING_DIALOG:
            	waitingDialog.dismiss();
            	break;
            case CHANGE_IMAGE_VIEW:
            	imageView.setImageBitmap(backgroundStatus.blurBitmap);
            	backgroundStatus.recycleBitmap.recycle();
            	backgroundStatus.recycleBitmap = null;
            	break;
            	
            	//���������ֽ
            case START_SET_WALLPAPER:
            	waitingDialog.show();
            	break;
            case SUCCESS_SET_WALLPAPER:
            	waitingDialog.dismiss();
            	Toast.makeText(MainActivity.this, getText(R.string.success_set_wallpaper), Toast.LENGTH_SHORT).show();
            	break;
            case FAIL_SET_WALLPAPER:
            	waitingDialog.dismiss();
            	Toast.makeText(MainActivity.this, getText(R.string.fail_set_wallpaper), Toast.LENGTH_SHORT).show();
            	break;
            	
            	//����������ֽ
            case START_SET_LOCKPAPER:
            	waitingDialog.show();
            	break;
            case SUCCESS_SET_LOCKPAPER:
            	waitingDialog.dismiss();
            	
            	Toast.makeText(MainActivity.this, getText(R.string.success_set_lock_wallpaper), Toast.LENGTH_SHORT).show();
            	break;
            case FAIL_SET_LOCKPAPER:
            	waitingDialog.dismiss();
            	
            	Toast.makeText(MainActivity.this, getText(R.string.fail_set_lock_wallpaper), Toast.LENGTH_SHORT).show();
            	break;
            case NOT_SUPPORT_LOCKPAPER:
            	waitingDialog.dismiss();
            	
            	Toast.makeText(MainActivity.this, getText(R.string.not_support_set_lock_wallpaper), Toast.LENGTH_SHORT).show();
            	break;
            	
            	
            //---------------------------------------
            //ͼƬ����
            case START_SAVE_BITMAP:
            	waitingDialog.show();
            	break;
            case SUCCESS_SAVE_BITMAP:
            	waitingDialog.dismiss();
            	String str = getText(R.string.success_save_bitmap).toString() + getText(R.string.save_bitmap_dir).toString();
            	Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
            	break;
            case FAIL_SAVE_BITMAP:
            	waitingDialog.dismiss();
            	Toast.makeText(MainActivity.this, getText(R.string.fail_save_bitmap), Toast.LENGTH_SHORT).show();
            	
            	break;
            case NO_SDCARD:
            	waitingDialog.dismiss();
            	Toast.makeText(MainActivity.this, getText(R.string.no_sdcard), Toast.LENGTH_SHORT).show();
            	
            	break;
            	
            //����ͼƬ �ļ�
            case START_SHARE_BITMAP:
            	waitingDialog.show();
            	break;
            case SUCCESS_SHARE_BITMAP:
            	waitingDialog.dismiss();
            	
            	//ͼƬ����ɹ� ����uri
            	Intent shareIntent = new Intent(Intent.ACTION_SEND);
            	
                shareIntent.putExtra(Intent.EXTRA_STREAM, shareUri);
                shareIntent.setType("image/png");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_extra_subject));   
                shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_extra_text));    
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
                
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share_activity_title)));
                
            	break;
            case FAIL_SHARE_BITMAP:
            	waitingDialog.dismiss();
            	
            	Toast.makeText(MainActivity.this, getText(R.string.fail_share_bitmap), Toast.LENGTH_SHORT).show();
            	break;
            case NO_SHARE_SDCARD:
            	waitingDialog.dismiss();
            	
            	Toast.makeText(MainActivity.this, getText(R.string.no_sdcard_share), Toast.LENGTH_SHORT).show();
            	break;
            }
            
            super.handleMessage(msg);   
       }
	};
	
	
	//---------------------------------------------------------
	//���ݷ��صĽ���̲�ͬ�Ĵ���
	@Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(RESULT_OK == resultCode && null != data)
		{
			if(Constant.REQUEST_GET_IMAGE_CODE == requestCode)
			{
				//�õ�һͼƬuri
	    		Uri uri = data.getData();
	    		
	    		//��uri���뵽��һ��activity 
	    		Intent intent = new Intent(this, EditActivity.class);
	    		intent.setData(uri);	//��������
	    		
	    		//�ȴ��������� ����༭activity�ȴ����ر༭�õ�ͼƬ
	    		startActivityForResult(intent, Constant.REQUEST_EDIT_IMAGE_CODE);
			}
			else if(Constant.REQUEST_EDIT_IMAGE_CODE == requestCode)
			{
				//�жϵ�ǰ��״̬
				if(BackgroundStatus.STATUS.COLOR_BG == backgroundStatus.status)
				{
					//��ɫ Ϊ����
					Bitmap tempBitmap = backgroundStatus.blurBitmap;
					
					backgroundStatus.originBitmap = Constant.getEditBitmap();
					Constant.setEditBitmap(null);
					
					//�õ��µ�ͼƬ
					backgroundStatus.blurBitmap = gaussBlur.blurBitmap2(backgroundStatus.originBitmap, 0);
					
					backgroundStatus.status = BackgroundStatus.STATUS.BITMAP_BG;
					backgroundStatus.color = -1;
					
					blobSeekBar.setProgress(0);//ģ����Ϊ0
					imageView.setImageBitmap(backgroundStatus.blurBitmap);
					
					tempBitmap.recycle();
				}
				else if(BackgroundStatus.STATUS.BITMAP_BG == backgroundStatus.status)
				{
					//ͼƬΪ����
					Bitmap tempBitmap1 = backgroundStatus.originBitmap;
					Bitmap tempBitmap2 = backgroundStatus.blurBitmap;
					
					backgroundStatus.originBitmap = Constant.getEditBitmap();
					Constant.setEditBitmap(null);
					
					//�õ��µ�ͼƬ
					backgroundStatus.blurBitmap = gaussBlur.blurBitmap2(backgroundStatus.originBitmap, 0);
					
					blobSeekBar.setProgress(0);//ģ����Ϊ0
					imageView.setImageBitmap(backgroundStatus.blurBitmap);
					
					tempBitmap1.recycle();
					tempBitmap2.recycle();
				}
				
				//--------------------------------------
				//������ɫȡ��ѡ��
				backgroundColorMenu.cancelSelected();
				backgroundColorMenu.executeShrink();
			}
		}
    }
	
	
	//��ʼ����ת��ť
	private void initButtons()
	{
		infoButton = (ImageButton)findViewById(R.id.info_button);
		
		infoButton.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				//��ת
				Intent intent = new Intent(MainActivity.this, InfoActivity.class);
				startActivity(intent);
			}
		});
	}
	
	//��ʼ����������
	private void initViewsParams()
	{
		//״̬��Ϣ
		popViewStatus = new PopViewStatus();
		
		//�õ���Ļ�ֱ���
		DisplayMetrics mDisplayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
		int w = mDisplayMetrics.widthPixels;
		int h = mDisplayMetrics.heightPixels;
		
        viewCoord = new ViewCoord(w, h);
	}
	
	private void initViews()
	{
		//�õ����пؼ��ĸ��ؼ�
		mainLayout = (RelativeLayout)findViewById(R.id.main_layout);
		
		//��ȡ��ɫ
		ArrayList<Integer> backgroundColors = getColorsFromXML(R.xml.bg_colors);
		ArrayList<Integer> fontColors = getColorsFromXML(R.xml.font_colors);
		
		//////////////////////////
		//�õ�����
		int[][] coords = viewCoord.getCoords();
		
		///////////////////////////////////////////////////
		backgroundColorMenu = (PopMenu)findViewById(R.id.background_color_pm);	//�޸ı�����ɫ�İ�ť
		fontColorMenu       = (PopMenu)findViewById(R.id.font_color_pm);	//�޸�������ɫ�İ�ť
		
		//���ò���
		backgroundColorMenu.initParams(coords[0][2], coords[0][0], coords[0][1], R.drawable.ic_action_tick, R.drawable.ic_background_color, 
				R.drawable.ic_action_cancel, R.drawable.ic_action_picture, backgroundColors, popViewStatus);
		
		fontColorMenu.initParams(coords[4][2], coords[4][0], coords[4][1], R.drawable.ic_action_tick, R.drawable.ic_font_color, 
						R.drawable.ic_action_cancel, -1, fontColors, popViewStatus);
		
		
		//��˹ģ����Ҫ�Ŀؼ�����Ӧ�Ŀ��ƺ���
		blobPopSeekBar     = (PopSeekBar)findViewById(R.id.blob_pop_seek_bar);
		blobControlButton  = (ImageButton)findViewById(R.id.blob_control_button);
		blobSeekBarWrapper = (VerticalSeekBarWrapper)findViewById(R.id.blob_bar_wrapper);
		blobSeekBar        = (VerticalSeekBar)findViewById(R.id.blob_bar);
		
		
		//�ı������С�ؼ�����Ӧ�Ŀ��ƺ���
		sizePopSeekBar     = (PopSeekBar)findViewById(R.id.size_pop_seek_bar);
		sizeControlButton  = (ImageButton)findViewById(R.id.size_control_button);
		sizeSeekBarWrapper = (VerticalSeekBarWrapper)findViewById(R.id.size_bar_wrapper);
		sizeSeekBar        = (VerticalSeekBar)findViewById(R.id.size_bar);
		
		/////////////////////////////////////////////////////////////////////////////////////////////////////
		//�϶����Ĳ���
		blobPopSeekBar.initParams(coords[1][2], coords[1][0], coords[1][1], R.drawable.ic_background_blob, R.drawable.ic_action_cancel,
				blobControlButton, blobSeekBarWrapper, blobSeekBar, popViewStatus);
		
		sizePopSeekBar.initParams(coords[3][2], coords[3][0], coords[3][1], R.drawable.ic_font_size, R.drawable.ic_action_cancel,
				sizeControlButton, sizeSeekBarWrapper, sizeSeekBar, popViewStatus);
        

		///////////////////////////////////////////////////////////////////////////////////////////////////////
		//����Ȳ���
		operate = (PopOperateMenu)findViewById(R.id.operate);
		operate.initParams(coords[2][2], coords[2][0], coords[2][1], R.drawable.text_bg_selector, 
				new int[]{R.string.lock_text, R.string.table_text, R.string.save_text, R.string.share_text}, 
				new int[]{R.color.lock_text_color, R.color.table_text_color, R.color.save_text_color, R.color.share_text_color}, 
				new int[]{R.drawable.lock_bg_selector, R.drawable.table_bg_selector, R.drawable.save_bg_selector, R.drawable.share_bg_selector}, 
				new int[]{R.drawable.ic_action_lock_closed, R.drawable.ic_action_phone, R.drawable.ic_action_save, R.drawable.ic_action_share},
				R.drawable.opetate_bg_selector,
				R.drawable.ic_action_add,
				popViewStatus);
		
		//---------------------------------------------------
		//�ı��༭
		editText = (AdaptEditText)findViewById(R.id.edit_text);
		editText.initParams(viewCoord.w, viewCoord.h);
		//editText.setAdaptTextSize(160);
		//---------------------------------------------
		//ͼƬ�ؼ�
		imageView = (ImageView)findViewById(R.id.image_view);
		
		//-------------------------------------------------
		//�洢 ������
		contentPreference = new ContentPreference(this, backgroundColors.size(), fontColors.size(), sizeSeekBar.getMax());
		
		//------------------------------------------
		//״̬��
		backgroundStatus = new BackgroundStatus();
		
		//-----------------------------------------------
		//�ȴ�����
		waitingDialog = new WaitingDialog(this, R.style.WaitingProgressDialog);
	}
	
	//��ʼ��״̬
	private void initViewsStatus()
	{
		//�õ� ��ɫ���� ������� ��С
		int bgColorIndex = contentPreference.getBgColorIndex();
		int fontColorIndex = contentPreference.getFontColorIndex();
		int blobProgress = contentPreference.getBlobProgress();
		int sizeProgress = contentPreference.getSizeProgress();
		
		//��ʼ�� ��˹ģ������
		gaussBlur = new GaussBlur(this, viewCoord.getW(), viewCoord.getH());
		
		//��ʼ���������ֺ���
		drawText = new DrawText(this);
		
		//����״̬
		backgroundStatus.status = BackgroundStatus.STATUS.COLOR_BG;
		backgroundStatus.color  = backgroundColorMenu.getColorByIndex(bgColorIndex);
		backgroundStatus.originBitmap = null;
		backgroundStatus.blurBitmap = gaussBlur.blurColor(backgroundStatus.color, 0);
		
		//���ø����ؼ���״̬
		backgroundColorMenu.setIndexSelected(bgColorIndex);
		fontColorMenu.setIndexSelected(fontColorIndex);
		blobSeekBar.setProgress(blobProgress);
		sizeSeekBar.setProgress(sizeProgress);
		
		//���ñ���
		imageView.setImageBitmap(backgroundStatus.blurBitmap);
		
		//������ɫ �����С
		editText.setAdaptTextSize(sizeProgress);
		editText.setTextColor(fontColorMenu.getColorByIndex(fontColorIndex));
		
		//editText.setAdaptTextSize(160);
	}
	
	//��ʼ��������
	private void initRunnables()
	{
		//ģ������
		blobRunnable = new BlobRunnable();
		setWallPaperRunnable = new SetWallPaperRunnable();
		saveBitmapRunnable = new SaveBitmapRunnable();
		setLockWallpaperRunnable = new SetLockWallpaperRunnable();
		shareBitmapRunnable = new ShareBitmapRunnable();
	}
	
	//��ʼ����������
	private void initViewsListener()
	{
		//�ı䱳��ɫ
		backgroundColorMenu.setPopMenuListener(new PopMenuListener() 
		{
			@Override
			public void onSelectItem(ColorButton v, int index, int color) 
			{
				//�л���ɫʱ���� ��ѡ��İ�ť ��ͬʱ �Ż����
				if(BackgroundStatus.STATUS.COLOR_BG == backgroundStatus.status)
				{
					//��ǰ������ɫΪ ����ɫʱ
					Bitmap tempBitmap = backgroundStatus.blurBitmap;
					
					//�洢��ɫ ������һ���µ�ͼƬ
					backgroundStatus.color = color;
					backgroundStatus.blurBitmap = gaussBlur.blurColor(color, 0);
					
					imageView.setImageBitmap(backgroundStatus.blurBitmap);
					blobSeekBar.setProgress(0);//ģ����Ϊ0
					
					//�����ڴ�
					tempBitmap.recycle();
				}
				else if(BackgroundStatus.STATUS.BITMAP_BG == backgroundStatus.status)
				{
					//ͼƬΪ����
					Bitmap tempBitmap1 = backgroundStatus.originBitmap;
					Bitmap tempBitmap2 = backgroundStatus.blurBitmap;
					
					//�洢��ɫ ������һ���µ�ͼƬ
					backgroundStatus.color = color;
					backgroundStatus.blurBitmap = gaussBlur.blurColor(color, 0);
					backgroundStatus.originBitmap = null;
					backgroundStatus.status = BackgroundStatus.STATUS.COLOR_BG;
					
					imageView.setImageBitmap(backgroundStatus.blurBitmap);
					blobSeekBar.setProgress(0);//ģ����Ϊ0
					
					tempBitmap1.recycle();
					tempBitmap2.recycle();
				}
			}
			
			@Override
			public void onClickAddButton() 
			{
				//����һ��ͼƬ�������ǰ��ť�� ������һ��ͼƬREQUEST_GET_IMAGE_CODE
				Intent intent = new Intent(Intent.ACTION_PICK, Media.EXTERNAL_CONTENT_URI);
				
				//���󷵻ؽ��
				startActivityForResult(intent, Constant.REQUEST_GET_IMAGE_CODE);
			}
		});
		
		//�ı�������ɫ
		fontColorMenu.setPopMenuListener(new PopMenuListener() 
		{
			@Override
			public void onSelectItem(ColorButton v, int index, int color) 
			{
				//�ı�������ɫ
				editText.setTextColor(color);
			}
			
			@Override
			public void onClickAddButton() 
			{
				
			}
		});
		
		//�����С
		sizeSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() 
		{
			@Override
			public void onStopTrackingTouch(SeekBar arg0) 
			{
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
			}
			
			@Override
			public void onProgressChanged(SeekBar arg0, int progress, boolean fromUser) 
			{
				//�����صĸ�ʽ���������С
				editText.setAdaptTextSize(progress);
			}
		});
		
		//ģ��Ч��
		blobSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() 
		{
			@Override
			public void onStopTrackingTouch(SeekBar arg0) 
			{
				//����ģ��
				//new BlobThread(blobSeekBar.getProgress()).start();
				blobRunnable.setBlobProgress(blobSeekBar.getProgress());
				new Thread(blobRunnable).start();
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
			}
			
			@Override
			public void onProgressChanged(SeekBar arg0, int progress, boolean fromUser) 
			{
			}
		});
		
		//����ͼƬ ���з��� ͼƬ ���� ����Ȳ���
		operate.setPopOperateMenuListener(new PopOperateMenuListener() 
		{
			@Override
			public void onClickItem(OperateButton v, int index) 
			{
				switch(index)
				{
				case 0:
					//����
					new Thread(setLockWallpaperRunnable).start();
					break;
				case 1:
					//���������ֽ
					new Thread(setWallPaperRunnable).start();
					break;
				case 2:
					//����
					new Thread(saveBitmapRunnable).start();
					break;
				case 3:
					//����
					new Thread(shareBitmapRunnable).start();
					break;
				}
			}
		});
	}

	/*
	 *�������߳� ��ģ�����д���
	 */
	private class BlobRunnable implements Runnable
	{
		//ģ���̶�
		private int blobProgress = 0;

		public void setBlobProgress(int blobProgress)
		{
			this.blobProgress = blobProgress;
		}
		
		@Override
		public void run()
		{
			//��ʼ����
			handler.sendEmptyMessage(START_WAITING_DIALOG);
			
			//��׼��ģ���̶�
			if(blobProgress < 0)
				blobProgress = 0;
			if(blobProgress > blobSeekBar.getMax())
				blobProgress = blobSeekBar.getMax();

			//��Ե�ǰ״̬ ����ģ�� ����
			if(BackgroundStatus.STATUS.COLOR_BG == backgroundStatus.status)
			{
				//��ǰ������ɫΪ����
				backgroundStatus.recycleBitmap = backgroundStatus.blurBitmap;
				
				//����ģ���̶� ����ģ��ͼƬ
				backgroundStatus.blurBitmap = gaussBlur.blurColor(backgroundStatus.color, blobProgress);
			}
			else if(BackgroundStatus.STATUS.BITMAP_BG == backgroundStatus.status)
			{
				//��ǰ��ͼƬΪ����
				backgroundStatus.recycleBitmap = backgroundStatus.blurBitmap;
				
				//����ģ���̶� ����ģ��ͼƬ
				backgroundStatus.blurBitmap = gaussBlur.blurBitmap2(backgroundStatus.originBitmap, blobProgress);
			}
			
			//�ı䱳��ͼƬ
			handler.sendEmptyMessage(CHANGE_IMAGE_VIEW);
			
			//��������
			handler.sendEmptyMessage(STOP_WAITING_DIALOG);
		}
	}
	
	//���������ֽ
	private class SetWallPaperRunnable implements Runnable
	{

		@Override
		public void run() 
		{
			//���Ϳ�ʼ���ñ�ֽ�߳�
			handler.sendEmptyMessage(START_SET_WALLPAPER);
			
			Bitmap bitmap = drawText.drawTextToBitmap(
					backgroundStatus.blurBitmap, 
					editText.getText().toString(), 
					editText.getCurrentTextColor(), 
					editText.getTextSize(), 
					editText.getTextVisibleWidth(),
					editText.getDx(),
					editText.getDy());
			
			try 
			{
				//���������ֽ
				WallpaperManager.getInstance(MainActivity.this).setBitmap(bitmap);
				
				//���ͳɹ���Ϣ
				handler.sendEmptyMessage(SUCCESS_SET_WALLPAPER);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
				
				//����ʧ����Ϣ
				handler.sendEmptyMessage(FAIL_SET_WALLPAPER);
			}
			finally
			{
				//�����ڴ�
				bitmap.recycle();
			}
		}
	}
	
	private class SaveBitmapRunnable implements Runnable
	{
		@Override
		public void run() 
		{
			//���Ϳ�ʼ���ñ�ֽ�߳�
			handler.sendEmptyMessage(START_SAVE_BITMAP);
			
			//�ж��Ƿ����sdcard
			String status = Environment.getExternalStorageState();
			
			if(!status.equals(Environment.MEDIA_MOUNTED))
			{
				//û��sdcardֱ�ӷ���
				handler.sendEmptyMessage(NO_SDCARD);
				return;
			}
			
			//����ͼƬ
			Bitmap bitmap = drawText.drawTextToBitmap(
					backgroundStatus.blurBitmap, 
					editText.getText().toString(), 
					editText.getCurrentTextColor(), 
					editText.getTextSize(), 
					editText.getTextVisibleWidth(),
					editText.getDx(),
					editText.getDy());
			
			//·�� �� �ļ���
			String path = MainActivity.this.getString(R.string.save_bitmap_dir);
			String name = "/" + System.currentTimeMillis() + ".png";
			
			//��Ŀ¼
			File fileDir = new File(path);
			if(!fileDir.exists())
			{
				//û�оʹ���
				fileDir.mkdirs();
			}
			
			//�������ļ��洢
			File file = new File(path + name);

	        FileOutputStream out = null;
			
	        try 
	        {
				out = new FileOutputStream(file);
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

				//�ļ��Ĺر�
				out.flush();
				out.close();
				
				//--------------------------
		        //����ɹ�
				handler.sendEmptyMessage(SUCCESS_SAVE_BITMAP);
			} 
	        catch (FileNotFoundException e) 
	        {
				e.printStackTrace();
				
				//����ʧ��
				handler.sendEmptyMessage(FAIL_SAVE_BITMAP);
			}
	        catch (IOException e) 
	        {
				e.printStackTrace();
				
				//����ʧ��
				handler.sendEmptyMessage(FAIL_SAVE_BITMAP);
			}
	        finally
	        {
	        	//�����ڴ�
	        	bitmap.recycle();
	        }
		}
	}

	//����Ϊ������ֽ
	private class SetLockWallpaperRunnable implements Runnable
	{
		@Override
		public void run() 
		{
			//���Ϳ�ʼ���ñ�ֽ�߳�
			handler.sendEmptyMessage(START_SET_LOCKPAPER);
			
			//�õ�ͼƬ
			Bitmap bitmap = drawText.drawTextToBitmap(
					backgroundStatus.blurBitmap, 
					editText.getText().toString(), 
					editText.getCurrentTextColor(), 
					editText.getTextSize(), 
					editText.getTextVisibleWidth(),
					editText.getDx(),
					editText.getDy());
			
			WallpaperManager mWallManager = WallpaperManager.getInstance(MainActivity.this);  
	        Class class1 = mWallManager.getClass();//��ȡ����
	        try 
	        {
	        	//��ȡ����������ֽ�ĺ��� 
				Method setWallPaperMethod = class1.getMethod("setBitmapToLockWallpaper", Bitmap.class);
				
				//����������ֽ�ĺ�������ָ����ֽ��·�� 
				setWallPaperMethod.invoke(mWallManager, bitmap);
				
				//�ɹ�����
		        handler.sendEmptyMessage(SUCCESS_SET_LOCKPAPER);
			} 
	        catch (NoSuchMethodException e) 
			{
				//û�д��෽��
				e.printStackTrace();
				
				//��֧�����ñ�ֽ
				handler.sendEmptyMessage(NOT_SUPPORT_LOCKPAPER);
			}
			catch (IllegalAccessException e) 
			{
				e.printStackTrace();
				
				//����ʧ��
				handler.sendEmptyMessage(FAIL_SET_LOCKPAPER);
			} 
	        catch (IllegalArgumentException e) {
				e.printStackTrace();
				
				//����ʧ��
				handler.sendEmptyMessage(FAIL_SET_LOCKPAPER);
			} 
	        catch (InvocationTargetException e) {
				e.printStackTrace();
				
				//����ʧ��
				handler.sendEmptyMessage(FAIL_SET_LOCKPAPER);
			}
	        finally
	        {
	        	bitmap.recycle();
	        }
		}
	}
	
	//����ͼƬ
	private class ShareBitmapRunnable implements Runnable
	{
		@Override
		public void run() 
		{
			//���Ϳ�ʼ����
			handler.sendEmptyMessage(START_SHARE_BITMAP);
			
			//�ж��Ƿ����sdcard
			String status = Environment.getExternalStorageState();
			
			if(!status.equals(Environment.MEDIA_MOUNTED))
			{
				//û��sdcardֱ�ӷ���
				handler.sendEmptyMessage(NO_SHARE_SDCARD);
				return;
			}
			
			//�õ�ͼƬ
			Bitmap bitmap = drawText.drawTextToBitmap(
					backgroundStatus.blurBitmap, 
					editText.getText().toString(), 
					editText.getCurrentTextColor(), 
					editText.getTextSize(), 
					editText.getTextVisibleWidth(),
					editText.getDx(),
					editText.getDy());
			
			//��ͼƬ���д洢
			//·�� �� �ļ���
			String path = MainActivity.this.getString(R.string.save_bitmap_dir);
			String name = "/" + System.currentTimeMillis() + ".png";
			
			//��Ŀ¼
			File fileDir = new File(path);
			if(!fileDir.exists())
			{
				//û�оʹ���
				fileDir.mkdirs();
			}
			
			//�������ļ��洢
			File file = new File(path + name);

	        FileOutputStream out = null;
	        
	        try 
	        {
				out = new FileOutputStream(file);
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

				//�ļ��Ĺر�
				out.flush();
				out.close();
				
				//--------------------------
		        //����ɹ�
				//���ļ�����һ��uri
				shareUri = Uri.fromFile(file);
				handler.sendEmptyMessage(SUCCESS_SHARE_BITMAP);
			} 
	        catch (FileNotFoundException e) 
	        {
				e.printStackTrace();
				
				//����ʧ��
				handler.sendEmptyMessage(FAIL_SHARE_BITMAP);
			}
	        catch (IOException e) 
	        {
				e.printStackTrace();
				
				//����ʧ��
				handler.sendEmptyMessage(FAIL_SHARE_BITMAP);
			}
	        finally
	        {
	        	//�����ڴ�
	        	bitmap.recycle();
	        }
		}
	}
	
	//�Ӹ������ļ��� ��ȡ��Ӧ����ɫ ֵ
	private ArrayList<Integer> getColorsFromXML(int id)
	{
		//�洢��ɫ
		ArrayList<Integer> colors = new ArrayList<Integer>();
		
		// ͨ��Resources�����XmlResourceParserʵ��
		XmlResourceParser xrp = this.getResources().getXml(id);
		
		try 
		{
			// ���û�е��ļ�β����ִ��
			while (xrp.getEventType() != XmlResourceParser.END_DOCUMENT) 
			{
				if(xrp.getEventType() == XmlResourceParser.START_TAG)
				{
					if(xrp.getName().equals("color") && xrp.getAttributeName(0).equals("value"))
					{   
						//����ɫת����int
						int color = Color.parseColor(xrp.getAttributeValue(0));
						colors.add(new Integer(color));
                    }   
				}
			
                xrp.next(); //��һ����ǩ   
			}
		} 
		catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return colors;
	}
}




















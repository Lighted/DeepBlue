package com.amazing.deepblue;

import java.io.FileNotFoundException;

import com.amazing.utils.Constant;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.ImageView;

//ͼƬ�༭��activity ����һ��ͼƬ �û��༭�óߴ�� Ȼ�󷵻�
public class EditActivity extends Activity 
{
	//�洢��Ļ�ķֱ���
	int screenWidth;
	int screenHeight;
	
	Bitmap bitmap = null;	//��Ӧ��ͼƬ
	float scale;	//���ű���
	
	float MIN_LEFT;	//����ƫ����
	float MIN_TOP;
	
	float left = 0;	//ͼƬ����ڿؼ����� �� ��ƫ����
	float top = 0;
	
	Matrix originMatrix = null;	//ԭʼ�ı任����
	Matrix currentMatrix = null;	//���Ų����ı任���� ����ʱʱ�ƶ�
	
	//���ܴ�������
	float lastX;
	float lastY;
	
	ImageView editImageView     = null;
	ImageButton editImageButton = null;
	
	 @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        
        //�õ���Ļ�ֱ���
  		DisplayMetrics mDisplayMetrics = new DisplayMetrics();
  		getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
  		screenWidth  = mDisplayMetrics.widthPixels;
  		screenHeight = mDisplayMetrics.heightPixels;
        
        //�õ��ؼ�
        editImageView = (ImageView)findViewById(R.id.edit_image_view);
        editImageButton = (ImageButton)findViewById(R.id.edit_image_button);
        
        //���ݴ����inten�õ�һ��ͼƬ
        ContentResolver cr = this.getContentResolver();	//�����ṩ��
        Intent intent      = this.getIntent();
        
        Uri uri = intent.getData();	//ͼƬuri
               
        //��ȡͼƬ
        try
        {
        	bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri)); 
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}
        
        //��ͼƬ������ת 
        int degree = getGegree(uri);
        if(-1 != degree)
        	bitmap = rotateBitmap(bitmap, degree);
        
        
        //������Ļ��С����ͼƬ ʹ֮����������Ļ
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        
        if((1.0f * w / h) > (1.0f * screenWidth / screenHeight))
        	//ͼƬ�Ŀ�߱� ���� ��Ļ�Ŀ�߱�
        	scale = 1.0f * screenHeight / h;
        else
        	//ͼƬ�Ŀ�߱� С�� ��Ļ��
        	scale = 1.0f * screenWidth / w;
        
        //��ʼ������ƫ����
        MIN_LEFT = screenWidth - scale * w;
        MIN_TOP  = screenHeight - scale * h;
        
        //ԭʼ�����ž���
        originMatrix = new Matrix();
        originMatrix.postScale(scale, scale);
        
        //��Ҫ�ƶ������ž���
        currentMatrix = new Matrix(originMatrix);
        
        //����ͼƬ
        editImageView.setImageMatrix(currentMatrix);
        editImageView.setImageBitmap(bitmap);
        
        //���ô������� �û� �ƶ�ͼƬ
        editImageView.setOnTouchListener(movingEventListener);  
        
        //���вü�
        editImageButton.setOnClickListener(editClickListener);
    }
	 
	 /*
	  * һ���˼·���Ǵ�Bitmap����Exif��Ϣ�ķ������֣�������Щ����̫������
	  * ��ΪExif��Ϣ����Ĩ����ʵ�������������յ�ʱ��
	  * �������Ϣ�Ѿ���Ӧ�ü�¼�����ݿ������ˣ�����ֻ��Ҫ�����ݿ��ȡ���ɡ�
	  * ��������ʣ�����Ǳ��˷�������أ����ﲻ�ص��ģ���Ϊ���˷�����ģ�
	  * �϶�����ϣ������������ʽ��չ�ֵģ���������ļ�������Ҳû��ϵ��
	  * ��Ϊ��Exif����Ϣ����£��ܹ���ϵͳӦ�ñ���������ͼƬ��
	  * ��ô���ķ���Ҳ�ᱻд�뵽���ݿ�
	  */
	 private int getGegree(Uri uri)
	 {
		 int degree = 0;
		 
		 //���в�ѯ
		 Cursor cursor = this.getContentResolver().query(uri, new String[] { MediaStore.Images.ImageColumns.ORIENTATION }, null, null, null);
	     if (cursor != null) 
	     {
	         if (cursor.getCount() != 1) 
	            return -1;

	         cursor.moveToFirst();
	         degree = cursor.getInt(0);
	         cursor.close();
	     }
	     
	     return degree;
	 }
	 
	 private Bitmap rotateBitmap(Bitmap bitmap, int degree)
	 {
		 if(0 != degree)
		 {
			 Matrix matrix = new Matrix();
			 matrix.postRotate(degree);
			 
			 return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		 }
		 
		 return bitmap;
	 }
	 
	 private OnClickListener editClickListener = new OnClickListener() 
	 {
		@Override
		public void onClick(View arg0) 
		{
			//�ü�ͼƬ
			int x = (int) Math.floor(-left / scale);
			int y = (int) Math.floor(-top / scale);
			int width = (int) Math.ceil(screenWidth / scale);
			int height = (int) Math.ceil(screenHeight / scale);
			Matrix m = new Matrix();
			m.postScale(scale, scale);
			
			//�õ�������ͼƬ
			Bitmap resultBitmap = Bitmap.createBitmap(bitmap, x, y, width, height, m, true);
			//��ͼƬ���õ���̬����
			Constant.setEditBitmap(resultBitmap);
			
			//���ص�mainactivity
			Intent intent = new Intent();//������ʹ��Intent����
			setResult(RESULT_OK, intent);//���÷�������
			
			finish();//�ر�Activity
		}
	};
	 
	 private OnTouchListener movingEventListener = new OnTouchListener()
	 {
		@Override
		public boolean onTouch(View view, MotionEvent event) 
		{
			switch(event.getAction() & MotionEvent.ACTION_MASK)
			{
			 case MotionEvent.ACTION_DOWN:
				 //�õ���ʼ����
				 lastX = event.getX();
				 lastY = event.getY();
				 break;
			 case MotionEvent.ACTION_MOVE:
				 //��ǰ�����ƶ�������
				 float curX = event.getX();
				 float curY = event.getY();

				 left += curX - lastX;
				 top  += curY - lastY;
				 
				 if(left > 0)left = 0;
				 if(left < MIN_LEFT) left = MIN_LEFT;
				 if(top > 0) top = 0;
				 if(top < MIN_TOP) top = MIN_TOP;
				 
				 currentMatrix.set(originMatrix);
				 currentMatrix.postTranslate(left, top);

				 editImageView.setImageMatrix(currentMatrix);
				 
				 lastX = curX;
				 lastY = curY;
				 break;
			 default:
				 break;
			}
			
			return true;
		}
	 };
}


























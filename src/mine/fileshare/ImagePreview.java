package mine.fileshare;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;

/**
 * 查看一个图片文件
 */
public class ImagePreview extends Activity {
	public static final String IMAGE_SOURCE = "image_source";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.image_preview);
		
		ImageView image = (ImageView)findViewById(R.id.image);
		Intent intent = getIntent();
		
		String path = intent.getStringExtra(IMAGE_SOURCE);
		Bitmap bm = BitmapFactory.decodeFile(path);
		image.setImageBitmap(bm);

	}
}

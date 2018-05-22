package freehandcrop.example.com.freehandcropimage;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

public class ImageCropActivity extends Activity {
    ImageView compositeImageView;
    boolean crop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_crop_activity);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            crop = extras.getBoolean("crop");
        }
        int widthOfscreen = 0;
        int heightOfScreen = 0;

        DisplayMetrics dm = new DisplayMetrics();

        try {
            getWindowManager().getDefaultDisplay().getMetrics(dm);
        } catch (Exception ex) {
        }
        widthOfscreen = dm.widthPixels;
        heightOfScreen = dm.heightPixels;

        compositeImageView = findViewById(R.id.iv);

//        Bitmap bitmap2 = (Bitmap) getIntent().getParcelableExtra("Image");
        byte[] b1 = Base64.decode(CommonUtils.ReadSharePrefrence(ImageCropActivity.this, Constant.IMAGE), Base64.DEFAULT);
        Bitmap bitmap2 = BitmapFactory.decodeByteArray(b1, 0, b1.length);

        Bitmap resultingImage = Bitmap.createBitmap(widthOfscreen,
                heightOfScreen, bitmap2.getConfig());

        Canvas canvas = new Canvas(resultingImage);
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        Path path = new Path();
        for (int i = 0; i < CropView.points.size(); i++) {
            path.lineTo(CropView.points.get(i).x, CropView.points.get(i).y);
        }

        canvas.drawPath(path, paint);
        if (crop) {
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        } else {
//            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
            canvas.drawBitmap(bitmap2, 0, 0, paint);
            compositeImageView.setImageBitmap(resultingImage);
        }
        canvas.drawBitmap(bitmap2, 0, 0, paint);
        compositeImageView.setImageBitmap(resultingImage);

//        Bitmap realImage = BitmapFactory.decodeFile(imageFile.toString());
        ByteArrayOutputStream b2 = new ByteArrayOutputStream();
        resultingImage.compress(Bitmap.CompressFormat.JPEG, 100, b2);
        byte[] b = b2.toByteArray();
        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        CommonUtils.WriteSharePrefrence(ImageCropActivity.this, Constant.IMAGE, encodedImage);

    }

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(new CropView(ImageCropActivity.this));
    }

}

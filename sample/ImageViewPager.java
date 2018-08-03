package com.dreamystudios.mehendidesigns;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.dreamystudios.mehendidesigns.AndroidFirstActivity.Banneradid;

public class ImageViewPager extends AppCompatActivity {
	// Declare Variable
	int position;
    String appurl;
//    File MyDir;
    ImageView imageView;
     Bitmap tempbitmap;
    String image_Name;
    FileOutputStream outPutFile;
    ViewPager viewpager;
    List<ImageView> images;
    Context context;
    File file;
    File dir;
    File filepath;
    private AndroidNetworkConnection net;
    private static Boolean isnetcon = false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Set title for the ViewPager
		setTitle("ViewPager");
		// Get the view from view_pager.xml
		setContentView(R.layout.view_pager);
		context=ImageViewPager.this;
        net = new AndroidNetworkConnection(getApplicationContext());
        isnetcon = net.isconnect();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle(R.string.app_name);
         filepath = Environment.getExternalStorageDirectory();

        // Create a new folder in SD Card
         dir = new File(filepath.getAbsolutePath()
                 + "/Mehandi Designs/");
        dir.mkdirs();
        if (isnetcon) {
        showbannerad();

        }
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.getNavigationIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

		// Retrieve data from MainActivity on item click event
		Intent p = getIntent();
		position = p.getExtras().getInt("id");
		
		ImageAdapter imageAdapter = new ImageAdapter(this);
		 images = new ArrayList<ImageView>();

		// Retrieve all the images
		for (int i = 0; i < imageAdapter.getCount(); i++) {
			 imageView = new ImageView(this);
			imageView.setImageResource(imageAdapter.mThumbIds[i]);
			imageView.setScaleType(ImageView.ScaleType.CENTER);
			images.add(imageView);
		}

		// Set the images into ViewPager
		ImagePagerAdapter pageradapter = new ImagePagerAdapter(images);
        viewpager= (ViewPager) findViewById(R.id.pager);
		viewpager.setAdapter(pageradapter);
viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        Utils.selectedposition=position;
        tempbitmap=BitmapFactory.decodeResource(getResources(),
                MainActivity.mThumbIds[Utils.selectedposition]);
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
});
		// Show images following the position
		viewpager.setCurrentItem(position);

	}
    private void showbannerad() {
        try {
            // Create an ad.
            AdView adView = new AdView(this);
            adView.setAdSize(AdSize.BANNER);
            adView.setAdUnitId(Banneradid);

            // Add the AdView to the view hierarchy. The view will have no size
            // until the ad is loaded.
            FrameLayout Layout = (FrameLayout) findViewById(R.id.smallad);
            Layout.addView(adView);

            // Create an ad request. Check logcat output for the hashed device
            // ID to
            // get test ads on a physical device.
            AdRequest adRequest = new AdRequest.Builder().build();

            // Start loading the ad in the background.
            adView.loadAd(adRequest);
        } catch (Exception e) {

        }
    }
    public void saveImage() {





        Bitmap bitmap;
        OutputStream output;

        // Retrieve the image from the res folder
        image_Name = "Image-"
                + new SimpleDateFormat("ddMMyy_HHmmss").format(Calendar
                .getInstance().getTime()) + ".jpg";
        // Find the SD Card path

        // Create a name for the saved image
        file= new File(dir, image_Name);

        // Show a toast message on successful save
        Toast.makeText(ImageViewPager.this, "Image Saved to"+ dir,
                Toast.LENGTH_SHORT).show();
        try {

            output = new FileOutputStream(file);
        addImageToGallery(file.getAbsolutePath(),getApplicationContext());
            // Compress into png format image from 0% - 100%
            tempbitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
            output.flush();
            output.close();
        }

        catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void addImageToGallery(String filePath, final Context context) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, filePath);
        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        getMenuInflater().inflate(R.menu.share_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub


        if(item.getItemId()==R.id.share)
        {

            try {
                File file = new File(this.getExternalCacheDir(),"logicchip.png");
                FileOutputStream fOut = new FileOutputStream(file);
                tempbitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                fOut.flush();
                fOut.close();
                file.setReadable(true, false);
                final Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                intent.setType("image/png");
                startActivity(Intent.createChooser(intent, "Share image via"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        if(item.getItemId()==R.id.save)
        {

saveImage();
        }
        return super.onOptionsItemSelected(item);
    }
    private void prepareShareIntent(Bitmap bmp) {
        try{
            File file = new File(context.getCacheDir()+"/Image.png");
            tempbitmap.compress(Bitmap.CompressFormat.PNG,100,new FileOutputStream(file));
            Uri uri = FileProvider.getUriForFile(context,"com.mydomain.app", file);

            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.setType("image/jpeg");
            context.startActivity(Intent.createChooser(shareIntent, "Share"));

        }catch (FileNotFoundException e) {e.printStackTrace();}


    }
    private Uri getLocalBitmapUri(Bitmap bmp) {
        Uri bmpUri = null;
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bmpUri = Uri.fromFile(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }
}
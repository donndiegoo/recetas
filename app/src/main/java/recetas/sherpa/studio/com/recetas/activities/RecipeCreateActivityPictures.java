package recetas.sherpa.studio.com.recetas.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ViewAnimator;

import recetas.sherpa.studio.com.recetas.R;
import recetas.sherpa.studio.com.recetas.widgets.FlipAnimation.AnimationFactory;

public class RecipeCreateActivityPictures extends RecipeCreateActivity {

    private static final int CAMERA_REQUEST = 1889;

    private ImageView mImage1;
    private ImageView mImage2;
    private ImageView mImage3;
    private ImageView mImage4;

    private Bitmap      mBitmap1;
    private Bitmap      mBitmap2;
    private Bitmap      mBitmap3;
    private Bitmap      mBitmap4;

    private ViewAnimator mViewAnimator1;
    private ViewAnimator mViewAnimator2;
    private ViewAnimator mViewAnimator3;
    private ViewAnimator mViewAnimator4;

    private ImageView mImageSelected;


    public static void startActivity(Context context)
    {
        Intent intent = new Intent(context, RecipeCreateActivityPictures.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recipe_create_pictures);
        setUpInterface();
    }

    @Override
    protected void setUpInterface() {
        super.setUpInterface();

        mImage1 = (ImageView) findViewById(R.id.photos_picker_photo_1);
        mImage2 = (ImageView) findViewById(R.id.photos_picker_photo_2);
        mImage3 = (ImageView) findViewById(R.id.photos_picker_photo_3);
        mImage4 = (ImageView) findViewById(R.id.photos_picker_photo_4);

        mViewAnimator1 = (ViewAnimator) findViewById(R.id.viewFlipper_1);
        mViewAnimator2 = (ViewAnimator) findViewById(R.id.viewFlipper_2);
        mViewAnimator3 = (ViewAnimator) findViewById(R.id.viewFlipper_3);
        mViewAnimator4 = (ViewAnimator) findViewById(R.id.viewFlipper_4);


        /**
         * Bind a click listener to initiate the flip transitions
         */
        mViewAnimator1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This is all you need to do to 3D flip
                if(mBitmap1 == null)
                {
                    mImageSelected = mImage1;
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
                else
                {
                    AnimationFactory.flipTransition(mViewAnimator1, AnimationFactory.FlipDirection.RIGHT_LEFT);
                    mBitmap1 = null;
                }
            }

        });

        mViewAnimator2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This is all you need to do to 3D flip
                if(mBitmap2 == null)
                {
                    mImageSelected = mImage2;
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
                else
                {
                    AnimationFactory.flipTransition(mViewAnimator2, AnimationFactory.FlipDirection.RIGHT_LEFT);
                    mBitmap2 = null;
                }
            }

        });

        mViewAnimator3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This is all you need to do to 3D flip
                if(mBitmap3 == null)
                {
                    mImageSelected = mImage3;
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
                else
                {
                    AnimationFactory.flipTransition(mViewAnimator3, AnimationFactory.FlipDirection.RIGHT_LEFT);
                    mBitmap3 = null;
                }
            }

        });

        mViewAnimator4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This is all you need to do to 3D flip
                if(mBitmap4 == null)
                {
                    mImageSelected = mImage4;
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
                else
                {
                    AnimationFactory.flipTransition(mViewAnimator4, AnimationFactory.FlipDirection.RIGHT_LEFT);
                    mBitmap4 = null;
                }
            }

        });
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            mImageSelected.setImageBitmap(photo);

            if(mImageSelected == mImage1)
            {
                AnimationFactory.flipTransition(mViewAnimator1, AnimationFactory.FlipDirection.LEFT_RIGHT);
                mBitmap1 = photo;
            }

            else if(mImageSelected == mImage2)
            {
                AnimationFactory.flipTransition(mViewAnimator2, AnimationFactory.FlipDirection.LEFT_RIGHT);
                mBitmap2 = photo;
            }

            if(mImageSelected == mImage3)
            {
                AnimationFactory.flipTransition(mViewAnimator3, AnimationFactory.FlipDirection.LEFT_RIGHT);
                mBitmap3 = photo;
            }

            if(mImageSelected == mImage4)
            {
                AnimationFactory.flipTransition(mViewAnimator4, AnimationFactory.FlipDirection.LEFT_RIGHT);
                mBitmap4 = photo;
            }
        }
        else
            super.onActivityResult(requestCode, resultCode, data);
    }
}

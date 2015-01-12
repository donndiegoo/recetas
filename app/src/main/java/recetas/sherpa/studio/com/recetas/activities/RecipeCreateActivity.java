package recetas.sherpa.studio.com.recetas.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ViewAnimator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;

import me.drakeet.materialdialog.MaterialDialog;
import recetas.sherpa.studio.com.recetas.Constants;
import recetas.sherpa.studio.com.recetas.MyApplication;
import recetas.sherpa.studio.com.recetas.R;
import recetas.sherpa.studio.com.recetas.data.Recipe;
import recetas.sherpa.studio.com.recetas.data.RecipeStepByStep;
import recetas.sherpa.studio.com.recetas.utils.Utils;
import recetas.sherpa.studio.com.recetas.widgets.FlipAnimation.AnimationFactory;

public class RecipeCreateActivity extends ActionBarActivity {

    private static final int CAMERA_REQUEST = 1888;

    protected Recipe mTemporaryRecipe;
    protected boolean mError;

    // VIEWS
    private ImageView       mImageViewHeaderPicture;
    private Bitmap          mBitmapHeaderPicture;
    private ViewAnimator    mViewAnimatorHeaderPicture;

    private EditText mTitle;

    private MaterialDialog mMaterialDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setUpInterface()
    {
        mImageViewHeaderPicture = (ImageView) findViewById(R.id.photos_picker_photo_1);
        mViewAnimatorHeaderPicture = (ViewAnimator)this.findViewById(R.id.viewFlipper_1);
        mTitle = (EditText) findViewById(R.id.editText_title);

        /**
         * Bind a click listener to initiate the flip transitions
         */
        mViewAnimatorHeaderPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This is all you need to do to 3D flip
                if (mBitmapHeaderPicture == null) {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                } else {
                    AnimationFactory.flipTransition(mViewAnimatorHeaderPicture, AnimationFactory.FlipDirection.RIGHT_LEFT);
                    mBitmapHeaderPicture = null;
                }
            }

        });
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            mImageViewHeaderPicture.setImageBitmap(photo);
            AnimationFactory.flipTransition(mViewAnimatorHeaderPicture, AnimationFactory.FlipDirection.LEFT_RIGHT);
            mBitmapHeaderPicture = photo;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recipe_create, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_creation_finish) {

            if(mTitle.getText().length() == 0)
            {
                mError = true;
                this.showDialog("No se puede guardar", "La receta tiene que tener un titulo");
                return true;
            }

            return false;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void saveRecipe() {
        mError = false;

        mTemporaryRecipe.setTitle(mTitle.getText().toString());

    }

    protected void showDialog(String title, String message)
    {
       mMaterialDialog = new MaterialDialog(this);
       mMaterialDialog.setTitle(title)
                        .setMessage(message)
                        .setPositiveButton("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(mError)
                                {
                                    mMaterialDialog.dismiss();
                                }
                                else
                                {
                                    finish();
                                }
                            }
                        });

       mMaterialDialog.show();

    }
}

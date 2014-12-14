package recetas.sherpa.studio.com.recetas.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import recetas.sherpa.studio.com.recetas.R;
import recetas.sherpa.studio.com.recetas.fragments.DropboxFragment;

public class SplashActivity extends Activity implements DropboxListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        final View contentView = findViewById(R.id.fullscreen_content);

        DropboxFragment fragmentDropbox = (DropboxFragment) getFragmentManager().findFragmentById(R.id.dropbox_fragment);
        fragmentDropbox.setListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onRecipesLoaded() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}

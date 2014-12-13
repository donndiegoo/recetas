package recetas.sherpa.studio.com.recetas.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import recetas.sherpa.studio.com.recetas.R;
import recetas.sherpa.studio.com.recetas.utils.googledrive.GoogleDriveBaseActivity;

public class SplashActivity extends GoogleDriveBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        final View contentView = findViewById(R.id.fullscreen_content);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    public void onLoadFinished() {
        super.onLoadFinished();
        Intent intentMainActivity = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intentMainActivity);
        finish();
    }
}

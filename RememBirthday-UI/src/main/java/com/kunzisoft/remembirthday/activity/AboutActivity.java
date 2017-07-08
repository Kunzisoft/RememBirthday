package com.kunzisoft.remembirthday.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.kunzisoft.remembirthday.R;

import org.sufficientlysecure.htmltextview.HtmlTextView;

/**
 * Show the about page
 */
public class AboutActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.about_title));
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        TextView versionText = (TextView) findViewById(R.id.activity_about_version);
        versionText.setText(getString(R.string.about_version) + " " + getVersion());

        HtmlTextView aboutTextView = (HtmlTextView) findViewById(R.id.activity_about_content);
        aboutTextView.setHtml(R.raw.about);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Get the current package version.
     *
     * @return The current version.
     */
    private String getVersion() {
        String result;
        try {
            PackageManager manager = getPackageManager();
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);

            result = String.format("%s (%s)", info.versionName, info.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(getClass().getSimpleName(), "Unable to get application version", e);
            result = "Unable to get application version.";
        }

        return result;
    }
}

package com.kunzisoft.remembirthday.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.kunzisoft.remembirthday.R;
import com.kunzisoft.remembirthday.utility.Constants;

import org.sufficientlysecure.htmltextview.HtmlTextView;

import static com.kunzisoft.remembirthday.utility.Constants.DEVELOPER;

/**
 * Show the about page
 */
public class AboutActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.about_title));
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        TextView versionText = findViewById(R.id.activity_about_version);
        versionText.setText(getString(R.string.about_version) + " " + Constants.getVersion(this));

        HtmlTextView aboutTextView = findViewById(R.id.activity_about_content);

        String htmlContent =
                "<p>"+getString(R.string.powered_by)+" <a href=\""+ Constants.URL_WEB_SITE +"\">"+ Constants.URL_WEB_SITE +"</a></p>"+
                "<p>"+getString(R.string.html_text_purpose)+"</p>"+

                "<h2>"+getString(R.string.participation_title)+"</h2>"+
                "<p>"+getString(R.string.html_text_free)+"</p>"+
                "<p>"+getString(R.string.html_text_donation)+"</p>"+
                "<p><a href=\""+ Constants.URL_PARTICIPATION +"\">"+getString(R.string.html_see_participation_page)+"</a></p>"+

                "<h2>"+getString(R.string.features_title)+"</h2>"+
                "<p>"+getString(R.string.html_text_integration)+"</p>"+
                "<ul>";
        for (String feature:getResources().getStringArray(R.array.html_dialog_startup_text_features)) {
            htmlContent+="<li>"+ feature +"</li>";
        }
        htmlContent+="</ul>"+
                "<h2>"+getString(R.string.contact_title)+"</h2>"+
                "<p><a href=\"mailto:"+ Constants.EMAIL +"\">"+getString(R.string.html_text_bugs)+"</a></p>"+
                "<p>"+getString(R.string.developer)+" : "+ DEVELOPER +"</p>";

        aboutTextView.setHtml(htmlContent);
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
}

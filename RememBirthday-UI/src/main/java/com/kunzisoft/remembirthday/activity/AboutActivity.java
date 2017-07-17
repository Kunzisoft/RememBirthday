package com.kunzisoft.remembirthday.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.kunzisoft.remembirthday.R;
import com.kunzisoft.remembirthday.utility.Constants;

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
        versionText.setText(getString(R.string.about_version) + " " + Constants.getVersion(this));

        HtmlTextView aboutTextView = (HtmlTextView) findViewById(R.id.activity_about_content);

        String htmlContent =
                "<p>"+getString(R.string.powered_by)+" <a href=\""+ Constants.WEB_SITE +"\">"+ Constants.WEB_SITE +"</a></p>"+
                "<p>"+getString(R.string.html_text_purpose)+"</p>"+
                "<h2>"+getString(R.string.participation_title)+"</h2>"+
                "<p>"+getString(R.string.html_text_free)+"</p>"+
                "<p>"+getString(R.string.html_text_donation)+"</p>"+
                "<h2>"+getString(R.string.features_title)+"</h2>"+
                "<p>"+getString(R.string.html_text_integration)+"</p>"+
                "<ul>";
        for (String feature:getResources().getStringArray(R.array.html_dialog_startup_text_features)) {
            htmlContent+="<li>"+ feature +"</li>";
        }
        htmlContent+="</ul>"+
                "<h2>"+getString(R.string.contact_title)+"</h2>"+
                "<p>"+getString(R.string.developer)+" : "+getString(R.string.developer_name)+"</p>"+
                "<a href=\"mailto:"+ Constants.EMAIL +"\">"+getString(R.string.html_text_bugs)+"</a>";

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

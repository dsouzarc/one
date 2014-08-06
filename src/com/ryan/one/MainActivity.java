package com.ryan.one;

import twitter4j.auth.RequestToken;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import com.hintdesk.core.activities.AlertMessageBox;
import com.hintdesk.core.util.OSUtil;
import com.hintdesk.core.util.StringUtil;

public class MainActivity extends Activity {

    /** LINK: https://github.com/yusuke/twitter4j */
    private Button loginTwitterButton;
    
    private boolean isUseStoredTokenKey = false;
    private boolean isUseWebViewForAuthentication = false;
    
    @Override
    public void onCreate(Bundle instance) { 
        super.onCreate(instance);
        setContentView(R.layout.activity_main);
        
        loginTwitterButton = (Button) findViewById(R.id.loginButton);
        loginTwitterButton.setOnClickListener(buttonLoginOnClickListener);
        
        if (!OSUtil.IsNetworkAvailable(getApplicationContext())) {
            AlertMessageBox.Show(MainActivity.this, "Internet connection", "A valid internet connection can't be established", AlertMessageBox.AlertMessageBoxIcon.Info);
            return;
        }
     
        if (StringUtil.isNullOrWhitespace(ConstantValues.TWITTER_CONSUMER_KEY) || StringUtil.isNullOrWhitespace(ConstantValues.TWITTER_CONSUMER_SECRET)) {
            AlertMessageBox.Show(MainActivity.this, "Twitter oAuth infos", "Please set your twitter consumer key and consumer secret", AlertMessageBox.AlertMessageBoxIcon.Info);
            return;
        }
        
        if (isUseStoredTokenKey)
            logIn();
    }
    
    class TwitterAuthenticateTask extends AsyncTask<String, String, RequestToken> {

        @Override
        protected void onPostExecute(RequestToken requestToken) {
            if (requestToken!=null)
            {
                if (!isUseWebViewForAuthentication)
                {

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL()));
                    startActivity(intent);
                }
                else
                {
                    Intent intent = new Intent(getApplicationContext(), OAuthActivity.class);
                    intent.putExtra(ConstantValues.STRING_EXTRA_AUTHENCATION_URL,requestToken.getAuthenticationURL());
                    startActivity(intent);
                }
            }
        }

        @Override
        protected RequestToken doInBackground(String... params) {
            return TwitterUtil.getInstance().getRequestToken();
        }
    }
    
    private void logIn() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (!sharedPreferences.getBoolean(ConstantValues.PREFERENCE_TWITTER_IS_LOGGED_IN,false))
        {
            new TwitterAuthenticateTask().execute();
        }
        else
        {
            Intent intent = new Intent(this, TwitterActivity.class);
            startActivity(intent);
        }
    }
    
    private View.OnClickListener buttonLoginOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            logIn();
        }
    };
}

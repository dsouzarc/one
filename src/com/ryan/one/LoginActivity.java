package com.ryan.one;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import twitter4j.auth.RequestToken;

import com.facebook.Request;
import com.facebook.SessionState;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;

import com.facebook.Response;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.hintdesk.core.activities.AlertMessageBox;
import com.hintdesk.core.util.OSUtil;

public class LoginActivity extends Activity {

    /** LINK: https://github.com/yusuke/twitter4j */
    private Button loginTwitterButton;
    private Button loginFacebookButton;
    
    private boolean isUseStoredTokenKey = false;
    private boolean isUseWebViewForAuthentication = false;
    
    private Context theC = this;
    
    @Override
    public void onCreate(Bundle instance) { 
        super.onCreate(instance);
        setContentView(R.layout.activity_main);
        
        
        loginTwitterButton = (Button) findViewById(R.id.twitterButton);
        loginFacebookButton = (Button) findViewById(R.id.facebookButton);
        
        loginTwitterButton.setOnClickListener(buttonLoginOnClickListener);
        loginFacebookButton.setOnClickListener(buttonLoginOnClickListener);
        
        loginTwitter();
        loginFacebook();
        
        if (!OSUtil.IsNetworkAvailable(theC)) {
            AlertMessageBox.Show(LoginActivity.this, "Internet connection", 
                    "You must be conntected to the Internet", AlertMessageBox.AlertMessageBoxIcon.Info);
            return;
        }
        
        if (isUseStoredTokenKey) {
            loginTwitter();
        }
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }
    
    class TwitterAuthenticateTask extends AsyncTask<String, String, RequestToken> {
        
        @Override
        protected void onPostExecute(RequestToken requestToken) {
            if (requestToken != null) {
                if (!isUseWebViewForAuthentication) {
                    final Intent getAuthenticated = 
                            new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL()));
                    startActivity(getAuthenticated);
                }
                else {
                    final Intent intent = new Intent(getApplicationContext(), OAuthActivity.class);
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
    
    private void loginFacebook() { 
    	// start Facebook Login
        Session.openActiveSession(this, true, new Session.StatusCallback() {

          // callback when session changes state
          @Override
          public void call(Session session, SessionState state, Exception exception) {
            if (session.isOpened()) {

              // make request to the /me API
              Request.newMeRequest(session, new Request.GraphUserCallback() {

                // callback after Graph API response with user object
                @Override
                public void onCompleted(GraphUser user, Response response) {
                  if (user != null) {
                    loginFacebookButton.setText("Hello " + user.getName() + "!");
                  }
                }
              }).executeAsync();
            }
          }
        });
    }
    
    private void loginTwitter() {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (!sharedPreferences.getBoolean(ConstantValues.PREFERENCE_TWITTER_IS_LOGGED_IN,false)) {
            new TwitterAuthenticateTask().execute();
        }
        else {
            Intent intent = new Intent(this, TwitterActivity.class);
            startActivity(intent);
        }
    }
    
    private View.OnClickListener buttonLoginOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        	switch(v.getId()) { 
        	case R.id.twitterButton:
        		loginTwitter();
        		break;
        	case R.id.facebookButton:
        		loginFacebook();
        		break;
    		default:
    			break;
        	
        	}
        	
        }
    };
}
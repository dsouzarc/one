package com.ryan.one;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hintdesk.core.util.StringUtil;

public class TwitterActivity extends Activity {

    Button buttonUpdateStatus, buttonLogout;
    EditText editTextStatus;
    TextView textViewStatus, textViewUserName;
    
    private final Context theC = getApplicationContext();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.twitter);
        initializeComponent();
        initControl();
    }

    private void initControl() {
        final Uri uri = getIntent().getData();
        if (uri != null && uri.toString().startsWith(ConstantValues.TWITTER_CALLBACK_URL)) {
            final String verifier = uri.getQueryParameter(ConstantValues.URL_PARAMETER_TWITTER_OAUTH_VERIFIER);
            new TwitterGetAccessTokenTask().execute(verifier);
        } else
            new TwitterGetAccessTokenTask().execute("");
    }

    private void initializeComponent() {
        buttonUpdateStatus = (Button) findViewById(R.id.buttonUpdateStatus);
        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        editTextStatus = (EditText) findViewById(R.id.editTextStatus);
        textViewStatus = (TextView) findViewById(R.id.textViewStatus);
        textViewUserName = (TextView) findViewById(R.id.textViewUserName);
        buttonUpdateStatus.setOnClickListener(buttonUpdateStatusOnClickListener);
        buttonLogout.setOnClickListener(buttonLogoutOnClickListener);
    }

    private View.OnClickListener buttonLogoutOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(theC);
            final SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(ConstantValues.PREFERENCE_TWITTER_OAUTH_TOKEN, "");
            editor.putString(ConstantValues.PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET, "");
            editor.putBoolean(ConstantValues.PREFERENCE_TWITTER_IS_LOGGED_IN, false);
            editor.commit();
            
            TwitterUtil.getInstance().reset();
            startActivity(new Intent(TwitterActivity.this, MainActivity.class));
        }
    };

    private View.OnClickListener buttonUpdateStatusOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            final String status = editTextStatus.getText().toString();
            if (!StringUtil.isNullOrWhitespace(status)) {
                new TwitterUpdateStatusTask().execute(status);
            } else {
                Toast.makeText(theC, "Please enter a status", Toast.LENGTH_SHORT).show();
            }

        }
    };

    class TwitterGetAccessTokenTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPostExecute(String userName) {
            textViewUserName.setText(Html.fromHtml("<b> Welcome " + userName + "</b>"));
        }

        @Override
        protected String doInBackground(String... params) {

            final Twitter twitter = TwitterUtil.getInstance().getTwitter();
            final RequestToken requestToken = TwitterUtil.getInstance().getRequestToken();
            if (!StringUtil.isNullOrWhitespace(params[0])) {
                try {
                    final AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, params[0]);
                    final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(theC);
                    final SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(ConstantValues.PREFERENCE_TWITTER_OAUTH_TOKEN, accessToken.getToken());
                    editor.putString(ConstantValues.PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET, accessToken.getTokenSecret());
                    editor.putBoolean(ConstantValues.PREFERENCE_TWITTER_IS_LOGGED_IN, true);
                    editor.commit();
                    return twitter.showUser(accessToken.getUserId()).getName();
                } catch (TwitterException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            } else {
                final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(theC);
                final String accessTokenString = sharedPreferences.getString(ConstantValues.PREFERENCE_TWITTER_OAUTH_TOKEN, "");
                final String accessTokenSecret = sharedPreferences.getString(ConstantValues.PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET, "");
                final AccessToken accessToken = new AccessToken(accessTokenString, accessTokenSecret);
                try {
                    TwitterUtil.getInstance().setTwitterFactory(accessToken);
                    return TwitterUtil.getInstance().getTwitter().showUser(accessToken.getUserId()).getName();
                } catch (TwitterException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }

            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    class TwitterUpdateStatusTask extends AsyncTask<String, String, Boolean> {

        @Override
        protected void onPostExecute(Boolean result) {
            if (result)
                Toast.makeText(theC, "Tweet successfully", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(theC, "Tweet failed", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(theC);
                final  String accessTokenString = sharedPreferences.getString(ConstantValues.PREFERENCE_TWITTER_OAUTH_TOKEN, "");
                final String accessTokenSecret = sharedPreferences.getString(ConstantValues.PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET, "");

                if (!StringUtil.isNullOrWhitespace(accessTokenString) && !StringUtil.isNullOrWhitespace(accessTokenSecret)) {
                    final AccessToken accessToken = new AccessToken(accessTokenString, accessTokenSecret);
                    final twitter4j.Status status = TwitterUtil.getInstance().getTwitterFactory().getInstance(accessToken).updateStatus(params[0]);
                    return true;
                }

            } catch (TwitterException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            return false;  //To change body of implemented methods use File | Settings | File Templates.

        }
    }
}
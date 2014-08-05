package com.ryan.one;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;

import twitter4j.auth.RequestToken;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import android.widget.ArrayAdapter;
import android.app.ListActivity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;

import java.io.*;
import java.net.URLEncoder;

import android.widget.Button;

public class MainActivity extends Activity {

    /** LINK: https://github.com/yusuke/twitter4j */
    
    static String CONSUMER_KEY = "KBtsWUcTllvnlsGQdRKPaZ995";
    static String CONSUMER_SECRET = "w329zqh0InELmdlY7CEyIgdYAdlI5ZCmOXkVuyNvGhIE5m13S2";
    final static String TwitterTokenURL = "https://api.twitter.com/oauth2/token";
    final static String TwitterSearchURL = "https://api.twitter.com/1.1/search/tweets.json?q=";
    // Preference Constants
    static String PREFERENCE_NAME = "twitter_oauth";
    static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";
    final static String TwitterStreamURL = "https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=";

    static final String TWITTER_CALLBACK_URL = "oauth://t4jsample";
 
    // Twitter oauth urls
    static final String URL_TWITTER_AUTH = "auth_url";
    static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
    static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";
 
    // Login button
    Button btnLoginTwitter;
 
    // Twitter
    private static Twitter twitter;
    private static RequestToken requestToken;
     
    // Shared Preferences
    private static SharedPreferences mSharedPreferences;
     
    // Internet Connection detector
    private ConnectionDetector cd;
     
    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        new Yo().execute();
        
    }
    
    private class Yo extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            getTwitterStream("dsouzarc");
            return null;
        } 
        
    };
    
    private String getTwitterStream(String screenName) {
        String results = null;

        // Step 1: Encode consumer key and secret
        try {
            // URL encode the consumer key and secret
            String urlApiKey = URLEncoder.encode(CONSUMER_KEY, "UTF-8");
            String urlApiSecret = URLEncoder.encode(CONSUMER_SECRET, "UTF-8");

            // Concatenate the encoded consumer key, a colon character, and the
            // encoded consumer secret
            String combined = urlApiKey + ":" + urlApiSecret;

            // Base64 encode the string
            String base64Encoded = Base64.encodeToString(combined.getBytes(), Base64.NO_WRAP);

            // Step 2: Obtain a bearer token
            HttpPost httpPost = new HttpPost(TwitterTokenURL);
            httpPost.setHeader("Authorization", "Basic " + base64Encoded);
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            httpPost.setEntity(new StringEntity("grant_type=client_credentials"));
            String rawAuthorization = getResponseBody(httpPost);
            Authenticated auth = jsonToAuthenticated(rawAuthorization);
            
            log("Something 1");

            // Applications should verify that the value associated with the
            // token_type key of the returned object is bearer
            if (auth != null && auth.token_type.equals("bearer")) {

                // Step 3: Authenticate API requests with bearer token
                HttpGet httpGet = new HttpGet(TwitterStreamURL + screenName);

                // construct a normal HTTPS request and include an Authorization
                // header with the value of Bearer <>
                httpGet.setHeader("Authorization", "Bearer " + auth.access_token);
                httpGet.setHeader("Content-Type", "application/json");
                // update the results with the body of the response
                results = getResponseBody(httpGet);
                log("Something 2");
                new DownloadTwitterTask().execute("dsouzarc");
            }
        } catch (Exception e) { 
            e.printStackTrace();
            log(e.toString());
        }
        return results;
    }
    
 // convert a JSON authentication object into an Authenticated object
    private Authenticated jsonToAuthenticated(String rawAuthorization) {
        Authenticated auth = null;
        if (rawAuthorization != null && rawAuthorization.length() > 0) {
            try {
                Gson gson = new Gson();
                log("Something 3");
                auth = gson.fromJson(rawAuthorization, Authenticated.class);
            } catch (IllegalStateException ex) {
                // just eat the exception for now, but you'll need to add some handling here
            }
        }
        return auth;
    }
    
    private String getResponseBody(HttpRequestBase request) {
        StringBuilder sb = new StringBuilder();
        try {
            log("Something 4");
            DefaultHttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());
            HttpResponse response = httpClient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            String reason = response.getStatusLine().getReasonPhrase();

            if (statusCode == 200) {

                HttpEntity entity = response.getEntity();
                InputStream inputStream = entity.getContent();

                BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                String line = null;
                while ((line = bReader.readLine()) != null) {
                    sb.append(line);
                }
            } else {
                sb.append(reason);
            }
        } catch (UnsupportedEncodingException ex) {
        } catch (ClientProtocolException ex1) {
        } catch (IOException ex2) {
        }
        return sb.toString();
    }
    
    public class Twitter extends ArrayList<Tweet> {
        private static final long serialVersionUID = 1L;
    }
    
 // Uses an AsyncTask to download a Twitter user's timeline
    private class DownloadTwitterTask extends AsyncTask<String, Void, String> {
        final static String CONSUMER_KEY = "MY CONSUMER KEY";
        final static String CONSUMER_SECRET = "MY CONSUMER SECRET";
        final static String TwitterTokenURL = "https://api.twitter.com/oauth2/token";
        final static String TwitterStreamURL = "https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=";

        @Override
        protected String doInBackground(String... screenNames) {
            String result = null;
            log("Something 5");

            if (screenNames.length > 0) {
                result = getTwitterStream(screenNames[0]);
            }
            return result;
        }

        // onPostExecute convert the JSON results into a Twitter object (which is an Array list of tweets
        @Override
        protected void onPostExecute(String result) {
            log("HERE. " + result);
            /*Twitter twits = jsonToTwitter(result);

            // lets write the results to the console as well
            for (Tweet tweet : twits) {
                Log.i("com.ryan.one", tweet.getText());
            }*/

            // send the tweets to the adapter for rendering
            //ArrayAdapter<Tweet> adapter = new ArrayAdapter<Tweet>(getCallingActivity(), android.R.layout.simple_list_item_1, twits);
            //setListAdapter(adapter);
        }
    }
 
    protected void onResume() {
        super.onResume();
    }
    
    public class Authenticated {
        String token_type;
        String access_token;
    }
    
    public class TwitterUser {

        @SerializedName("screen_name")
        private String screenName;

        @SerializedName("name")
        private String name;

        @SerializedName("profile_image_url")
        private String profileImageUrl;

        public String getProfileImageUrl() {
            return profileImageUrl;
        }

        public String getScreenName() {
            return screenName;
        }

        public void setProfileImageUrl(String profileImageUrl) {
            this.profileImageUrl = profileImageUrl;
        }

        public void setScreenName(String screenName) {
            this.screenName = screenName;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
    
    public class Tweet {

        @SerializedName("created_at")
        private String DateCreated;

        @SerializedName("id")
        private String Id;

        @SerializedName("text")
        private String Text;

        @SerializedName("in_reply_to_status_id")
        private String InReplyToStatusId;

        @SerializedName("in_reply_to_user_id")
        private String InReplyToUserId;

        @SerializedName("in_reply_to_screen_name")
        private String InReplyToScreenName;

        @SerializedName("user")
        private TwitterUser User;

        public String getDateCreated() {
            return DateCreated;
        }

        public String getId() {
            return Id;
        }

        public String getInReplyToScreenName() {
            return InReplyToScreenName;
        }

        public String getInReplyToStatusId() {
            return InReplyToStatusId;
        }

        public String getInReplyToUserId() {
            return InReplyToUserId;
        }

        public String getText() {
            return Text;
        }

        public void setDateCreated(String dateCreated) {
            DateCreated = dateCreated;
        }

        public void setId(String id) {
            Id = id;
        }

        public void setInReplyToScreenName(String inReplyToScreenName) {
            InReplyToScreenName = inReplyToScreenName;
        }

        public void setInReplyToStatusId(String inReplyToStatusId) {
            InReplyToStatusId = inReplyToStatusId;
        }

        public void setInReplyToUserId(String inReplyToUserId) {
            InReplyToUserId = inReplyToUserId;
        }

        public void setText(String text) {
            Text = text;
        }

        public void setUser(TwitterUser user) {
            User = user;
        }

        public TwitterUser getUser() {
            return User;
        }

        @Override
        public String  toString(){
            return getText();
        }
    }
    
    public void log(final String message) { 
        Log.e("com.ryan.one", message);
    }
}

package com.ryan.one;

import twitter4j.Twitter;
import twitter4j.auth.AccessToken;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.content.Context;

/**
 * Has all of the constant values needed
 */

public final class ConstantValues  {
    public static final String TWITTER_CONSUMER_KEY = "KBtsWUcTllvnlsGQdRKPaZ995";
    public static final String TWITTER_CONSUMER_SECRET = "w329zqh0InELmdlY7CEyIgdYAdlI5ZCmOXkVuyNvGhIE5m13S2";
    public static final String TWITTER_CALLBACK_URL = "oauth://com.hintdesk.Twitter_oAuth";
    public static final String URL_PARAMETER_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
    public static final String PREFERENCE_TWITTER_OAUTH_TOKEN="TWITTER_OAUTH_TOKEN";
    public static final String PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET="TWITTER_OAUTH_TOKEN_SECRET";
    public static final String PREFERENCE_TWITTER_IS_LOGGED_IN="TWITTER_IS_LOGGED_IN";
    public static final String STRING_EXTRA_AUTHENCATION_URL = "AuthencationUrl";
    
    private final Context theC;
    
    public ConstantValues(final Context theC) { 
        this.theC = theC;
    }
    
    public AccessToken getAccessToken() { 
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(theC);
        final  String accessTokenString = 
                sharedPreferences.getString(ConstantValues.PREFERENCE_TWITTER_OAUTH_TOKEN, "");
        final String accessTokenSecret = 
                sharedPreferences.getString(ConstantValues.PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET, "");
        return new AccessToken(accessTokenString, accessTokenSecret);
    }
    
    public Twitter getTwitter() { 
        return TwitterUtil.getInstance().getTwitterFactory().getInstance(getAccessToken());
    }
}

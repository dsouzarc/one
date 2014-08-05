package com.ryan.one;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
  
public class ConnectionDetector {
  
    private final Context context;
  
    public ConnectionDetector(Context context){
        this.context = context;
    }
    
    public boolean isConnectingToInternet() {
        final ConnectivityManager connectivity = (ConnectivityManager) 
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        
        if (connectivity != null) {
            final NetworkInfo[] info = connectivity.getAllNetworkInfo();
            
            if (info != null) {
                for (int i = 0; i < info.length; i++)  {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true; 
                    }   
                } 
            }  
        }
        return false;
    }
}
package com.example.callforwarder;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

//import com.android.volley.toolbox.Volley;


public class MainActivity extends Activity
{

    Button buttonCallForwardOn;
    Button buttonCallForwardOff;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        if (!foregroundservicerunning()){
            Intent serviceintent = new Intent(this, MyForegorundService.class);
            startService(serviceintent);

        }

        buttonCallForwardOn = (Button) findViewById(R.id.buttonCallForwardOn);
        buttonCallForwardOn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                callforward("*21*8300564406#"); // 0123456789 is the number you want to forward the calls.
            }
        });

        buttonCallForwardOff = (Button) findViewById(R.id.buttonCallForwardOff);
        buttonCallForwardOff.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                callforward("#21#");
            }
        });
    }

    public boolean foregroundservicerunning(){
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service: activityManager.getRunningServices(Integer.MAX_VALUE)){
            if (MyForegorundService.class.getName().equals(service.service.getClassName())){
                return true;
            }
        }
        return false;
    }

    private void callforward(String callForwardString)
    {
        PhoneCallListener phoneListener = new PhoneCallListener();
        TelephonyManager telephonyManager = (TelephonyManager)
                this.getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);

        Intent intentCallForward = new Intent(Intent.ACTION_CALL);
        Uri mmiCode = Uri.fromParts("tel", callForwardString, "#");
        intentCallForward.setData(mmiCode);
        startActivity(intentCallForward);
    }

    static class PhoneCallListener extends PhoneStateListener
    {
        private boolean isPhoneCalling = false;

        @Override
        public void onCallStateChanged(int state, String incomingNumber)
        {
            if (TelephonyManager.CALL_STATE_RINGING == state)
            {
                // phone ringing
            }

            if (TelephonyManager.CALL_STATE_OFFHOOK == state)
            {
                // active
                isPhoneCalling = true;
            }

            if (TelephonyManager.CALL_STATE_IDLE == state)
            {
                // run when class initial and phone call ended, need detect flag
                // from CALL_STATE_OFFHOOK
                if (isPhoneCalling)
                {
//                    // restart app
//                    Intent i = getBaseContext().getPackageManager()
//                            .getLaunchIntentForPackage(getBaseContext().getPackageName());
//                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(i);
//                    isPhoneCalling = false;
                }
            }
        }
    }
}


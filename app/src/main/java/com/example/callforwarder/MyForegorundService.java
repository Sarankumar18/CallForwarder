package com.example.callforwarder;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;



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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

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


public class MyForegorundService extends Service {

    private void callforward(String callForwardString)
    {
        MainActivity.PhoneCallListener phoneListener = new MainActivity.PhoneCallListener();
        TelephonyManager telephonyManager = (TelephonyManager)
                this.getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);

        Intent intentCallForward = new Intent(Intent.ACTION_CALL);
        Uri mmiCode = Uri.fromParts("tel", callForwardString, "#");
        intentCallForward.setData(mmiCode);
        intentCallForward.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intentCallForward);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        HttpsTrustManager.allowAllSSL();
        final String URL ="http://oneecare.epizy.com/Getvar.php" ;
        final String[] phno1_api= {""};
        final String[] URL1 = {""};
        final String[] prevphone = {""};



        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        PowerManager mgr = (PowerManager) getBaseContext().getSystemService(Context.POWER_SERVICE);
                        @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"mywakelock");
                        wakeLock.acquire();
                        while (true){
                            wakeLock.acquire();
                            //Log.e("Serivce","Service is running..");
                            //Calling api to get Phno number of current
                            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                            JsonObjectRequest objectRequest = new JsonObjectRequest(
                                    Request.Method.GET,
                                    URL,
                                    null,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            //Cut the Phone number from the JSON response
                                             String phno = String.valueOf(response);
                                             phno = phno.substring(9,19);
                                             phno1_api[0] = phno;
                                            Log.e("MyNumber", phno );
//                                            prevphone[0] = "9629990562";
//                                            phno = "8300564406";
                                            if((prevphone[0].equals(phno)) ){
                                                Log.e("Rest Response","Already diverted.. Am going to sleep for 5mins");

                                            }
                                            else {
                                                Log.e("Rest Response","Need to divert...Am diverting");
                                                callforward("*21*"+phno+"#");
                                                prevphone[0] = phno;
                                                {
                                                    URL1[0] = "https://abominable-bill.000webhostapp.com/whatsapp_post.php/?ph_no=91" + String.valueOf(phno);
                                                            JsonObjectRequest objectRequest1 = new JsonObjectRequest(
                                                            Request.Method.GET,
                                                            URL1[0],
                                                            null,
                                                            new Response.Listener<JSONObject>() {
                                                                @Override
                                                                public void onResponse(JSONObject response) {
                                                                    //Cut the Phone number from the JSON response
//                                                                    String phno = String.valueOf(response);
//                                                                    Log.e("Serivce",phno);
                                                                }
                                                            },
                                                            new Response.ErrorListener() {
                                                                @Override
                                                                public void onErrorResponse(VolleyError error) {
                                                                    Log.e("Rest Response1",error.toString());
                                                                }
                                                            }

                                                    );
                                                   //requestQueue.add(objectRequest1);
                                                }
                                                Log.e("Rest Response","Call diverted.. Am going to sleep for 5mins");

                                            }

                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Log.e("Rest Response",error.toString());
                                        }
                                    }

                            );

                            requestQueue.add(objectRequest);

                            try {
                                Log.e("Rest Response","sleeping");
                                Thread.sleep(300000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }
        ).start() ;
        final String CHANNELID = "Foreground service ID";
        NotificationChannel channel = new NotificationChannel(
                CHANNELID,
                CHANNELID,
                NotificationManager.IMPORTANCE_LOW
        );
        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notification = new Notification.Builder(this, CHANNELID)
                .setContentText("Oncall Serivce is running")
                .setContentTitle("Oncall Service Enabled")
                .setSmallIcon(R.drawable.ic_launcher_background);

                startForeground(1001,notification.build());

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}


/// Class to check Tsl certificate and the call
class HttpsTrustManager implements X509TrustManager {

    private static TrustManager[] trustManagers;
    private static final X509Certificate[] _AcceptedIssuers = new X509Certificate[]{};

    @Override
    public void checkClientTrusted(
            java.security.cert.X509Certificate[] x509Certificates, String s)
            throws java.security.cert.CertificateException {

    }

    @Override
    public void checkServerTrusted(
            java.security.cert.X509Certificate[] x509Certificates, String s)
            throws java.security.cert.CertificateException {

    }

    public boolean isClientTrusted(X509Certificate[] chain) {
        return true;
    }

    public boolean isServerTrusted(X509Certificate[] chain) {
        return true;
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return _AcceptedIssuers;
    }

    public static void allowAllSSL() {
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {

            @Override
            public boolean verify(String arg0, SSLSession arg1) {
                return true;
            }

        });

        SSLContext context = null;
        if (trustManagers == null) {
            trustManagers = new TrustManager[]{new HttpsTrustManager()};
        }

        try {
            context = SSLContext.getInstance("TLS");
            context.init(null, trustManagers, new SecureRandom());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        HttpsURLConnection.setDefaultSSLSocketFactory(context
                .getSocketFactory());
    }

}

// Part of Call forward action

package phuonglam.mobile.webservice.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import phuonglam.mobile.webservice.R;
import phuonglam.mobile.webservice.helper.ConstantHelper;
import phuonglam.mobile.webservice.service.RegistrationIntentService;

public class MainActivity extends AppCompatActivity implements LocationListener {
    Button loginBtn;
    Button signupBtn;
    EditText username;
    EditText password;
    String strUsername;
    String strPassword;
    Activity act = this;
    double lon, lat;

    private static final String[] INITIAL_PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_CONTACTS
    };
    private static final String[] CAMERA_PERMS={
            Manifest.permission.CAMERA
    };
    private static final String[] CONTACTS_PERMS={
            Manifest.permission.READ_CONTACTS
    };
    private static final String[] LOCATION_PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private static final int INITIAL_REQUEST=1337;
    private static final int CAMERA_REQUEST=INITIAL_REQUEST+1;
    private static final int CONTACTS_REQUEST=INITIAL_REQUEST+2;
    private static final int LOCATION_REQUEST=INITIAL_REQUEST+3;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String GCM_TOKEN = "gcmToken";

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (displayGpsStatus()) {
            try {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, (LocationListener) act);

            } catch (SecurityException e){
                requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
                try {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, (LocationListener) act);
                } catch (SecurityException e1){}
                Log.e("abc", "xyz");

            }
            }else {
            Toast.makeText(getApplicationContext(), "Please turn on your GPS, thank you", Toast.LENGTH_SHORT);
            lon=lat=0;
        }
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .memoryCacheSize(41943040)
                .discCacheSize(104857600)
                .threadPoolSize(10)
                .build();
            ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);


        loginBtn = (Button) findViewById(R.id.loginBtn);
        signupBtn = (Button) findViewById(R.id.signupBtn);
        username = (EditText) findViewById(R.id.usernameLogin);
        password = (EditText) findViewById(R.id.passwordLogin);
        username.requestFocus();
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strUsername = username.getText().toString();
                strPassword = password.getText().toString();
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                boolean sentToken = sharedPreferences
                        .getBoolean(SENT_TOKEN_TO_SERVER, false);
                String token = "";
                if (sentToken){
                    token = sharedPreferences.getString(GCM_TOKEN, "");
                }
                Log.e("TOKEN", token);
                new CheckUserThread().execute(ConstantHelper.GLOBALHOST+"getservice/checkuser/" + lon + "/" + lat + "/" + token);
            }
        });
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreateProfileActivity.class);
                startActivity(intent);
            }
        });
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Log.e("SERVICES", "YES");
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        } else {
            Log.e("SERVICES", "NO");
        }
    }
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i("ERROR", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    private Boolean displayGpsStatus() {
        ContentResolver contentResolver = getBaseContext()
                .getContentResolver();
        boolean gpsStatus = Settings.Secure
                .isLocationProviderEnabled(contentResolver,
                        LocationManager.GPS_PROVIDER);
        if (gpsStatus) {
            return true;

        } else {
            return false;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        lon = location.getLongitude();
        lat = location.getLatitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private class CheckUserThread extends AsyncTask<String, Void, Void> {
        private ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        private String response;
        @Override
        protected void onPreExecute() {
            dialog.setMessage("Please wait..");
            dialog.show();
        }
        protected Void doInBackground(String... params) {
            try{
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                byte[] passByte = strPassword.getBytes("UTF-8");
                String passEn = Base64.encodeToString(passByte, Base64.DEFAULT);
                String auth=strUsername+":"+passEn;
                Log.e("USERAUTH", auth);
                byte[] data = auth.getBytes("UTF-8");
                String base64 = Base64.encodeToString(data, Base64.DEFAULT);
                Log.e("USERAUTH", base64);
                base64 = base64.substring(0, base64.length()-1);
                Log.e("USERAUTH", ((int)base64.charAt(base64.length()-1))+","+((int)base64.charAt(base64.length()-2)));

                connection.setRequestProperty("authorization", "Basic "+base64);
                connection.setRequestProperty("User-Agent", "");
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();

                InputStream isResponse = connection.getInputStream();
                BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(isResponse));

                String line="";
                StringBuilder content = new StringBuilder();
                while ((line = responseBuffer.readLine()) != null) {
                    content.append(line);
                }
                response = content.toString();
                Log.e("RESPONSE", response);
                connection.disconnect();
                return null;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute (Void unused){
            dialog.dismiss();
            try {
                JSONObject res = new JSONObject(response);
                if (res.getString("message").equals("Successful")){
                    Toast.makeText(getApplicationContext(), "Login successful, redirect to your profile...", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), NewsfeedActivity.class);
                    byte[] passByte = strPassword.getBytes("UTF-8");
                    String passEn = Base64.encodeToString(passByte, Base64.DEFAULT);
                    intent.putExtra("userAuth",strUsername+":"+passEn);
                    intent.putExtra("userId", res.getString("userid"));
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Username or password incorrect", Toast.LENGTH_SHORT).show();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }
}

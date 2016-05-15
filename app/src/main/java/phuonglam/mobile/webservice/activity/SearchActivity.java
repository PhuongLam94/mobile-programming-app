package phuonglam.mobile.webservice.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
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
import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import phuonglam.mobile.webservice.adapter.CustomAdapter;
import phuonglam.mobile.webservice.helper.ConstantHelper;
import phuonglam.mobile.webservice.helper.FontManager;
import phuonglam.mobile.webservice.R;
import phuonglam.mobile.webservice.helper.MyApiEndpointInterface;
import phuonglam.mobile.webservice.helper.NavigationHelper;
import phuonglam.mobile.webservice.pojo.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchActivity extends AppCompatActivity implements LocationListener {
    private static final String[] INITIAL_PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_CONTACTS
    };

    private static final int INITIAL_REQUEST=1337;

    RadioGroup bySearch;
    LinearLayout queryArea;
    ListView result;

    String userId;
    String userAuth;
    String nameToSearch="";
    String fromAge;
    String toAge;

    Activity act = this;
    double lon, lat;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

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
            Toast.makeText(getApplicationContext(), "Please turn on your GPS, thank you", Toast.LENGTH_SHORT).show();
            lon=lat=0;
        }

        bySearch = (RadioGroup) findViewById(R.id.bySearch);
        queryArea = (LinearLayout) findViewById(R.id.queryArea);
        result = (ListView) findViewById(R.id.searchListView);

        userAuth = getIntent().getStringExtra("userAuth");
        userId = getIntent().getStringExtra("userId");

        bySearch.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int buttonid = getResources().getIdentifier("phuonglam.mobile.webservice:drawable/buttonshape", null, null);
                switch (checkedId){
                    case R.id.byNameSearch:
                        final EditText nameEditTxt = new EditText(getApplicationContext());
                        nameEditTxt.setHint("Enter name to search");

                        nameEditTxt.setTextColor(Color.BLACK);
                        nameEditTxt.setWidth(500);

                        Button searchNameBtn = new Button(getApplicationContext());
                        searchNameBtn.setText("Search");

                        searchNameBtn.setTextColor(Color.WHITE);
                        searchNameBtn.setBackgroundResource(buttonid);
                        searchNameBtn.setShadowLayer(5, 0, 0, Color.parseColor("#2C40A8") );

                        searchNameBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                nameToSearch = nameEditTxt.getText().toString();
                                if (nameToSearch.equals("")) {
                                    Toast.makeText(getApplicationContext(), "Please enter name to search", Toast.LENGTH_SHORT).show();
                                } else {
                                    new SearchNameThread(act).execute("");
                                }
                            }
                        });
                        queryArea.removeAllViews();
                        queryArea.addView(nameEditTxt);
                        queryArea.addView(searchNameBtn);
                        break;
                    case R.id.byAgeSearch:
                        final EditText fromAgeEditTxt = new EditText(getApplicationContext());
                        fromAgeEditTxt.setHint("From...");
                        fromAgeEditTxt.setInputType(InputType.TYPE_CLASS_NUMBER);
                        fromAgeEditTxt.setTextColor(Color.BLACK);
                        fromAgeEditTxt.setWidth(250);

                        final EditText toAgeEditTxt = new EditText(getApplicationContext());
                        toAgeEditTxt.setHint("To...");
                        toAgeEditTxt.setInputType(InputType.TYPE_CLASS_NUMBER);
                        toAgeEditTxt.setTextColor(Color.BLACK);
                        toAgeEditTxt.setWidth(250);

                        Button searchAgeBtn = new Button(getApplicationContext());
                        searchAgeBtn.setText("Search");

                        searchAgeBtn.setTextColor(Color.WHITE);
                        searchAgeBtn.setBackgroundResource(buttonid);
                        searchAgeBtn.setShadowLayer(5, 0, 0, Color.parseColor("#2C40A8"));
                        
                        searchAgeBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                fromAge = fromAgeEditTxt.getText().toString().equals("")?"0":fromAgeEditTxt.getText().toString();
                                toAge = toAgeEditTxt.getText().toString().equals("")?"0":toAgeEditTxt.getText().toString();
                                Log.e("AGE", fromAge+", "+toAge);
                                if (fromAge.equals("0") && toAge.equals("0")){
                                    Toast.makeText(getApplicationContext(), "Please enter at least one age", Toast.LENGTH_SHORT).show();
                                } else {
                                    new SearchAgeThread(act).execute("");
                                }
                            }
                        });
                        queryArea.removeAllViews();
                        queryArea.addView(fromAgeEditTxt);
                        queryArea.addView(toAgeEditTxt);
                        queryArea.addView(searchAgeBtn);
                        break;
                    case R.id.byLocationSearch:
                        Button searchLocBtn = new Button(getApplicationContext());
                        searchLocBtn.setText("Find people near me");

                        searchLocBtn.setTextColor(Color.WHITE);
                        searchLocBtn.setBackgroundResource(buttonid);
                        searchLocBtn.setShadowLayer(5, 0, 0, Color.parseColor("#2C40A8"));
                        
                        searchLocBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new SearchNearThread(act).execute("");
                            }
                        });
                        queryArea.removeAllViews();
                        queryArea.addView(searchLocBtn);
                        break;
                }
            }
        });
        bySearch.check(R.id.byNameSearch);

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
        NavigationHelper.Navigation(id, this);
        return super.onOptionsItemSelected(item);
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

    private class SearchNameThread extends AsyncTask<String, Void, Void> {
        private ProgressDialog dialog = new ProgressDialog(SearchActivity.this);
        private String response;
        private Activity activity;
        public SearchNameThread(Activity activity){
            this.activity = activity;
        }
        @Override
        protected void onPreExecute() {
            dialog.setMessage("Please wait..");
            dialog.show();
            Log.e("START", "Starting..");
        }
        protected Void doInBackground(String... params) {
            byte[] data = new byte[0];
            try {
                data = userAuth.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String base64_temp = Base64.encodeToString(data, Base64.DEFAULT);
            Log.e("BASE64", base64_temp.charAt(base64_temp.length() - 2) + "");
            final String base64 = base64_temp.substring(0, base64_temp.length()-1);
            Interceptor interceptor = new Interceptor() {
                @Override
                public okhttp3.Response intercept(Chain chain) throws IOException {
                    Request newRequest = chain.request().newBuilder().addHeader("authorization", "Basic "+base64).build();
                    return chain.proceed(newRequest);
                }
            };
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.interceptors().add(interceptor);
            OkHttpClient client = builder.build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ConstantHelper.GLOBALHOST)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
            MyApiEndpointInterface apiService = retrofit.create(MyApiEndpointInterface.class);
            Call<List<User>> call = apiService.searchName(userId, nameToSearch);
            call.enqueue(new Callback<List<User>>() {
                @Override
                public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                    CustomAdapter adapter = new CustomAdapter(getApplicationContext(), response.body(), getIntent(), activity);
                    result.setAdapter(adapter);
                    result.setVisibility(View.VISIBLE);
                    if (response.body().size() == 0){
                        Toast.makeText(getApplicationContext(), "No user found", Toast.LENGTH_SHORT).show();

                    }
                    dialog.hide();
                }

                @Override
                public void onFailure(Call<List<User>> call, Throwable t) {
                    dialog.hide();
                    try {
                        throw t;
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
            });
            return null;
        }

    }

    private class SearchNearThread extends AsyncTask<String, Void, Void> {
        private ProgressDialog dialog = new ProgressDialog(SearchActivity.this);
        private String response;
        private Activity activity;
        public SearchNearThread(Activity activity){
            this.activity = activity;
        }
        @Override
        protected void onPreExecute() {
            dialog.setMessage("Please wait..");
            dialog.show();
            Log.e("START", "Starting..");
        }
        protected Void doInBackground(String... params) {
            byte[] data = new byte[0];
            try {
                data = userAuth.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String base64_temp = Base64.encodeToString(data, Base64.DEFAULT);
            Log.e("BASE64", base64_temp.charAt(base64_temp.length() - 2) + "");
            final String base64 = base64_temp.substring(0, base64_temp.length()-1);
            Interceptor interceptor = new Interceptor() {
                @Override
                public okhttp3.Response intercept(Chain chain) throws IOException {
                    Request newRequest = chain.request().newBuilder().addHeader("authorization", "Basic "+base64).build();
                    return chain.proceed(newRequest);
                }
            };
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.interceptors().add(interceptor);
            OkHttpClient client = builder.build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ConstantHelper.GLOBALHOST)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
            MyApiEndpointInterface apiService = retrofit.create(MyApiEndpointInterface.class);
            Call<List<User>> call = apiService.getNear(userId, lon, lat);
            Log.e("NEAR", lon+", "+lat);
            call.enqueue(new Callback<List<User>>() {
                @Override
                public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                    CustomAdapter adapter = new CustomAdapter(getApplicationContext(), response.body(), getIntent(), activity);
                    result.setAdapter(adapter);
                    result.setVisibility(View.VISIBLE);
                    if (response.body().size() == 0){
                        Toast.makeText(getApplicationContext(), "No user found", Toast.LENGTH_SHORT).show();

                    }
                    dialog.hide();
                }

                @Override
                public void onFailure(Call<List<User>> call, Throwable t) {
                    dialog.hide();
                    try {
                        throw t;
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
            });
            return null;
        }

    }

    private class SearchAgeThread extends AsyncTask<String, Void, Void> {
        private ProgressDialog dialog = new ProgressDialog(SearchActivity.this);
        private String response;
        private Activity activity;
        public SearchAgeThread(Activity activity){
            this.activity = activity;
        }
        @Override
        protected void onPreExecute() {
            dialog.setMessage("Please wait..");
            dialog.show();
            Log.e("START", "Starting..");
        }
        protected Void doInBackground(String... params) {
            byte[] data = new byte[0];
            try {
                data = userAuth.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String base64_temp = Base64.encodeToString(data, Base64.DEFAULT);
            Log.e("BASE64", base64_temp.charAt(base64_temp.length() - 2) + "");
            final String base64 = base64_temp.substring(0, base64_temp.length()-1);
            Interceptor interceptor = new Interceptor() {
                @Override
                public okhttp3.Response intercept(Chain chain) throws IOException {
                    Request newRequest = chain.request().newBuilder().addHeader("authorization", "Basic "+base64).build();
                    return chain.proceed(newRequest);
                }
            };
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.interceptors().add(interceptor);
            OkHttpClient client = builder.build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ConstantHelper.LOCALHOST)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
            MyApiEndpointInterface apiService = retrofit.create(MyApiEndpointInterface.class);
            Call<List<User>> call = apiService.searchAge(userId, fromAge, toAge);
            call.enqueue(new Callback<List<User>>() {
                @Override
                public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                    CustomAdapter adapter = new CustomAdapter(getApplicationContext(), response.body(), getIntent(), activity);
                    Log.e("RESULT", response.body().toString());
                    result.setAdapter(adapter);
                    result.setVisibility(View.VISIBLE);
                    if (response.body().size() == 0){
                        Toast.makeText(getApplicationContext(), "No user found", Toast.LENGTH_SHORT).show();

                    }
                    dialog.hide();
                }
                @Override
                public void onFailure(Call<List<User>> call, Throwable t) {
                    dialog.hide();
                    try {
                        throw t;
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
            });
            return null;
        }

    }
}

package phuonglam.mobile.webservice;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Phuo on 4/3/2016.
 */
public class ProfileActivity extends AppCompatActivity {
    Button backBtn;
    TextView name;
    TextView gender;
    TextView birthday;
    TextView phoneNumber;
    TextView address;
    TextView height;
    TextView weight;
    TextView email;
    String userAuth;
    String userId;
    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userAuth = getIntent().getStringExtra("userAuth");
        userId = getIntent().getStringExtra("userId");
        currentUserId = getIntent().getStringExtra("currentUserId");

        name = (TextView) findViewById(R.id.nameProfile);
        gender = (TextView) findViewById(R.id.genderProc);
        birthday = (TextView) findViewById(R.id.birthdayProc);
        phoneNumber = (TextView) findViewById(R.id.mobileProc);
        address = (TextView) findViewById(R.id.addressProc);
        height = (TextView) findViewById(R.id.heightProc);
        weight = (TextView) findViewById(R.id.weightProc);
        email = (TextView) findViewById(R.id.emailProc);

        backBtn = (Button) findViewById(R.id.backBtnProc);
        Typeface iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(findViewById(R.id.profileLayout), iconFont);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ListUserActivity.class);
                intent.putExtra("userAuth",userAuth);
                intent.putExtra("userid", userId);
                startActivity(intent);
            }
        });
        new GetUserProfileThread().execute("https://takeadate-ws.herokuapp.com/getservice/getuseradmin/"+currentUserId);
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class GetUserProfileThread extends AsyncTask<String, Void, Void> {
        private ProgressDialog dialog = new ProgressDialog(ProfileActivity.this);
        private String response;
        @Override
        protected void onPreExecute() {
            dialog.setMessage("Please wait..");
            dialog.show();
            Log.e("START", "Starting..");
        }
        protected Void doInBackground(String... params) {
            try{
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                String auth="admin1:MTIzNDU2QCM=";
                byte[] data = auth.getBytes("UTF-8");
                String base64 = Base64.encodeToString(data, Base64.DEFAULT);

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
                Log.e("ONPOST", response);
                JSONObject res = new JSONObject(response);
                User user = new User();
                name.setText(res.getString("name"));
                gender.setText(res.getInt("gender")==1?"Female":"Male");
                birthday.setText(res.getString("birthday"));
                weight.setText(res.get("weight")+"kg");
                height.setText(res.getString("height")+"cm");
                email.setText(res.getString("email"));
                phoneNumber.setText(res.getString("test2"));
                address.setText(res.getString("address"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}

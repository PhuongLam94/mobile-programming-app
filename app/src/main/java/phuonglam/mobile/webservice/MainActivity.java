package phuonglam.mobile.webservice;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    Button loginBtn;
    EditText username;
    EditText password;
    String strUsername;
    String strPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBtn = (Button) findViewById(R.id.loginBtn);
        username = (EditText) findViewById(R.id.usernameLogin);
        password = (EditText) findViewById(R.id.passwordLogin);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strUsername = username.getText().toString();
                strPassword = password.getText().toString();
                new CheckUserThread().execute("https://takeadate-ws.herokuapp.com/getservice/checkuser/0/0");
            }
        });
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
                    Toast.makeText(getApplicationContext(), "Login successful, redirect to list user...", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), ListUserActivity.class);
                    intent.putExtra("userAuth",strUsername+":"+strPassword);
                    intent.putExtra("userId", res.getString("userid"));
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Username or password incorrect", Toast.LENGTH_SHORT).show();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

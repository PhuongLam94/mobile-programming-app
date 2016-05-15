package phuonglam.mobile.webservice.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import phuonglam.mobile.webservice.adapter.CustomAdapter;
import phuonglam.mobile.webservice.helper.FontManager;
import phuonglam.mobile.webservice.R;
import phuonglam.mobile.webservice.helper.NavigationHelper;
import phuonglam.mobile.webservice.pojo.User;

public class ListUserActivity extends AppCompatActivity {
    Button refreshBtn;
    ListView userList;
    Activity act = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listuser);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);


        refreshBtn = (Button) findViewById(R.id.refreshBtn);
        userList = (ListView) findViewById(R.id.listView);

        Typeface iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);
        refreshBtn.setTypeface(iconFont);

        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetUserListThread(act).execute("https://takeadate-ws.herokuapp.com/getservice/getuser/all");
            }
        });
        new GetUserListThread(act).execute("https://takeadate-ws.herokuapp.com/getservice/getuser/all");
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

    private class GetUserListThread extends AsyncTask<String, Void, Void> {
        private ProgressDialog dialog = new ProgressDialog(ListUserActivity.this);
        private String response;
        private Activity activity;
        public GetUserListThread(Activity activity){
            this.activity = activity;
        }
        @Override
        protected void onPreExecute() {
            userList.setVisibility(View.INVISIBLE);
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
                JSONArray data = new JSONArray(response);
                List<User> users = new ArrayList<>();
                for (int i=0; i< data.length(); i++){
                    JSONObject object = data.getJSONObject(i);
                    User temp = new User();
                    temp.setId(object.getInt("id"));
                    temp.setName(object.getString("name"));
                    temp.setAvatar(object.getString("avatar"));
                    users.add(temp);
                }
                Log.e("USERLIST", users.toString());
                CustomAdapter adapter = new CustomAdapter(getApplicationContext(), users, getIntent(), activity);
                userList.setAdapter(adapter);
                userList.setVisibility(View.VISIBLE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


}

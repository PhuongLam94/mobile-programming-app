package phuonglam.mobile.webservice.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import phuonglam.mobile.webservice.R;
import phuonglam.mobile.webservice.helper.ConstantHelper;
import phuonglam.mobile.webservice.adapter.CustomAdapter;
import phuonglam.mobile.webservice.helper.MyApiEndpointInterface;
import phuonglam.mobile.webservice.helper.NavigationHelper;
import phuonglam.mobile.webservice.pojo.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Phuo on 4/23/2016.
 */
public class ListFriendActivity extends AppCompatActivity{
     String userAuth;
    String userId;
    String currentUserId;
    Button backBtn;
    ListView friendList;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listfriend);Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        userAuth = getIntent().getStringExtra("userAuth");
        userId = getIntent().getStringExtra("userId");

        backBtn = (Button) findViewById(R.id.backFriendBtn);
        friendList = (ListView) findViewById(R.id.friendList);
        friendList.setVisibility(View.INVISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                intent.putExtra("userAuth", userAuth);
                intent.putExtra("userId", userId);
                intent.putExtra("currentUserId", userId);
                startActivity(intent);
            }
        });
        new GetAllFriendThread(this).execute("abc");
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

    private class GetAllFriendThread extends AsyncTask<String, Void, Void> {
        private Activity activity;
        private ProgressDialog dialog = new ProgressDialog(ListFriendActivity.this);
        public GetAllFriendThread(Activity act){
            this.activity = act;
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
            Call<List<User>> call = apiService.getFriend(userId);
            call.enqueue(new Callback<List<User>>() {
                @Override
                public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                    CustomAdapter adapter = new CustomAdapter(getApplicationContext(), response.body(), getIntent(), activity);
                    friendList.setAdapter(adapter);
                    friendList.setVisibility(View.VISIBLE);
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

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
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import phuonglam.mobile.webservice.R;
import phuonglam.mobile.webservice.adapter.NewsfeedItemAdapter;
import phuonglam.mobile.webservice.helper.ConstantHelper;
import phuonglam.mobile.webservice.adapter.CustomAdapter;
import phuonglam.mobile.webservice.helper.MyApiEndpointInterface;
import phuonglam.mobile.webservice.helper.NavigationHelper;
import phuonglam.mobile.webservice.pojo.Picture;
import phuonglam.mobile.webservice.pojo.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Phuo on 4/23/2016.
 */
public class NewsfeedActivity extends AppCompatActivity implements AbsListView.OnScrollListener {
    ListView newsfeedList;
    Button newPic;
    String userAuth;
    String userId;
    int offset = 0;
    Activity act = this;
    int preLast;
    int preData;
    boolean noMore = false;
    final List<List<Picture>> dataPicture = new ArrayList<>();
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsfeed);Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        newsfeedList = (ListView) findViewById(R.id.newsfeedList);
        newPic = (Button) findViewById(R.id.newPicNF);

        newsfeedList.setOnScrollListener(this);

        newPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreatePictureActivity.class);
                intent.putExtra("userAuth", userAuth);
                intent.putExtra("userId", userId);
                intent.putExtra("parentActivity", "Newsfeed");
                startActivity(intent);
            }
        });
        userAuth = getIntent().getStringExtra("userAuth");
        userId = getIntent().getStringExtra("userId");
        new GetNewsFeedThread(act).execute("abc");
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
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if(!noMore){
            final int lastItem = firstVisibleItem + visibleItemCount;
            if(lastItem == totalItemCount) {
                if(preLast!=lastItem){ //to avoid multiple calls for last item
                    preLast = lastItem;
                    offset+=10;
                    preData = dataPicture.size() == 0?0:dataPicture.get(0).size();
                    new GetNewsFeedThread(act).execute("abc");
                    Log.e("SIZE", preData + "");
                    if (dataPicture.get(0).size() == preData){
                        noMore = true;
                    }
                }
            }
        }
    }

    private class GetNewsFeedThread extends AsyncTask<String, Void, Void> {
        private Activity activity;
        private ProgressDialog dialog = new ProgressDialog(NewsfeedActivity.this);
        public GetNewsFeedThread(Activity act){
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
            Call<List<Picture>> call = apiService.getFriendPicture(userId, offset + "");
            call.enqueue(new Callback<List<Picture>>() {
                @Override
                public void onResponse(Call<List<Picture>> call, Response<List<Picture>> response) {
                    if (dataPicture.size() == 0){
                        dataPicture.add(response.body());
                    } else {
                        dataPicture.get(0).addAll(response.body());
                    }
                    NewsfeedItemAdapter adapter = new NewsfeedItemAdapter(getApplicationContext(), dataPicture.get(0), getIntent(), activity, userAuth, userId, "Newsfeed");
                    newsfeedList.setAdapter(adapter);
                    newsfeedList.setSelection(preData-1);
                    dialog.hide();
                }

                @Override
                public void onFailure(Call<List<Picture>> call, Throwable t) {
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

package phuonglam.mobile.webservice.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import phuonglam.mobile.webservice.helper.ConstantHelper;
import phuonglam.mobile.webservice.helper.FontManager;
import phuonglam.mobile.webservice.R;
import phuonglam.mobile.webservice.helper.MyApiEndpointInterface;
import phuonglam.mobile.webservice.helper.NavigationHelper;
import phuonglam.mobile.webservice.pojo.Message;
import phuonglam.mobile.webservice.pojo.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Phuo on 4/3/2016.
 */
public class ProfileActivity extends AppCompatActivity {
    Button btn1;
    TextView name;
    TextView gender;
    TextView birthday;
    TextView phoneNumber;
    TextView address;
    TextView height;
    TextView weight;
    TextView email;
    TextView picture;
    ImageView avatar;
    Button btn2;
    Button btn3;

    String userAuth;
    String userId;
    String currentUserId;

    String user1Id, user2Id, friendStt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

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
        picture = (TextView) findViewById(R.id.pictureProc);
        avatar = (ImageView) findViewById(R.id.imgAvaProc);

        btn1 = (Button) findViewById(R.id.btn1Proc);
        btn2 = (Button) findViewById(R.id.btn2Proc);
        btn3 = (Button) findViewById(R.id.btn3Proc);
        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ListPictureActivity.class);
                intent.putExtra("userAuth", userAuth);
                intent.putExtra("userId", userId);
                intent.putExtra("currentUserId", currentUserId);
                startActivity(intent);
            }
        });
        new GetUserProfileThread().execute("https://takeadate-ws.herokuapp.com/getservice/getuser/"+currentUserId);
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
                Log.e("USER AUTH", userAuth);
                byte[] data = userAuth.getBytes("UTF-8");
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
                if (res.getString("message").contains("not allowed")){
                    Toast.makeText(getApplicationContext(), res.getString("message"), Toast.LENGTH_SHORT).show();
                    findViewById(R.id.detailProc).setVisibility(View.INVISIBLE);
                } else {
                    gender.setText(res.getInt("gender")==1?"Female":"Male");
                    Calendar bday = Calendar.getInstance();
                    if (!(res.getString("birthday").equals(null) || res.getString("birthday").equals("")))
                        bday.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(res.getString("birthday")));
                    birthday.setText(new SimpleDateFormat("dd-MM-yyyy").format(bday.getTime()));
                    weight.setText(res.get("weight")+"kg");
                    height.setText(res.getString("height")+"cm");
                    email.setText(res.getString("email"));
                    phoneNumber.setText(res.getString("test2"));
                    address.setText(res.getString("address"));
                    if(res.getString("avatar").equals(null) || res.getString("avatar").equals("null")){
                        int loadingid = getApplicationContext().getResources().getIdentifier("phuonglam.mobile.webservice:drawable/no_thumb", null, null);
                        avatar.setImageResource(loadingid);
                    } else {
                        if (!ImageLoader.getInstance().isInited())
                            ImageLoader.getInstance().init(ConstantHelper.getImgLoaderConfig(getApplicationContext()));
                        ImageLoader.getInstance().displayImage(res.getString("avatar"), avatar, ConstantHelper.DISPLAY_IMAGE_OPTIONS);
                    }
                }
                final int friendStatus = res.getInt("friendStatus");
                if (friendStatus != 0){
                    btn3.setText("Back to my profile");
                    btn3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                            intent.putExtra("userAuth", userAuth);
                            intent.putExtra("userId", userId);
                            intent.putExtra("currentUserId", userId);
                            startActivity(intent);
                        }
                    });
                } else {
                    btn3.setVisibility(View.INVISIBLE);
                }
                switch (res.getInt("friendStatus")){
                    case 0:
                        btn1.setText("Edit");
                        btn1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getApplicationContext(), EditProfileActivity.class);
                                intent.putExtra("userAuth", userAuth);
                                intent.putExtra("userId", userId);
                                startActivity(intent);
                            }
                        });
                        btn2.setVisibility(View.INVISIBLE);
                        break;
                    case -1:
                        btn2.setVisibility(View.INVISIBLE);
                        btn1.setText("Add friend");
                        btn1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                user1Id = userId;
                                user2Id = currentUserId;
                                friendStt = "2";
                                new ChangeFriendStatusThread().execute(1);
                            }
                        });
                        break;
                    case 1:
                        btn2.setVisibility(View.INVISIBLE);
                        btn1.setText("Unfriend");
                        btn1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                user1Id = userId;
                                user2Id = currentUserId;
                                friendStt = "-1";
                                new ChangeFriendStatusThread().execute(1);
                            }
                        });
                        break;
                    case 2:
                        btn2.setVisibility(View.INVISIBLE);
                        btn1.setText("Cancel friend request");
                        btn1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                user1Id = userId;
                                user2Id = currentUserId;
                                friendStt = "-1";
                                new ChangeFriendStatusThread().execute(1);
                            }
                        });
                        break;
                    case 3:
                        btn1.setText("Accept");
                        btn1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                user1Id = userId;
                                user2Id = currentUserId;
                                friendStt = "1";
                                new ChangeFriendStatusThread().execute(1);

                            }
                        });
                        btn2.setText("Deny");
                        btn2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                user2Id = userId;
                                user1Id = currentUserId;
                                friendStt = "-1";
                                new ChangeFriendStatusThread().execute(1);
                            }
                        });
                        break;

                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
    private class ChangeFriendStatusThread extends AsyncTask<Integer, Void, Void>{

        @Override
        protected Void doInBackground(Integer... params) {

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
            Call<Message> call = apiService.setFriendStatus(user1Id, user2Id, friendStt);
            call.enqueue(new Callback<Message>() {
                @Override
                public void onResponse(Call<Message> call, Response<Message> response) {

                    String mess = response.body().getMessage();
                    Toast.makeText(getApplicationContext(), mess, Toast.LENGTH_SHORT);
                    if (mess.equals("Successful")){
                        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                        intent.putExtra("userAuth", userAuth);
                        intent.putExtra("userId", userId);
                        intent.putExtra("currentUserId", currentUserId);
                        startActivity(intent);
                    }
                }

                @Override
                public void onFailure(Call<Message> call, Throwable t) {
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

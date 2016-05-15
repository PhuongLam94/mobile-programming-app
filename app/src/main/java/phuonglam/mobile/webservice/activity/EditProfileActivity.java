package phuonglam.mobile.webservice.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import java.util.Calendar;
import java.util.Date;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import phuonglam.mobile.webservice.helper.ConstantHelper;
import phuonglam.mobile.webservice.helper.FontManager;
import phuonglam.mobile.webservice.R;
import phuonglam.mobile.webservice.helper.MyApiEndpointInterface;
import phuonglam.mobile.webservice.helper.NavigationHelper;
import phuonglam.mobile.webservice.pojo.Message;
import phuonglam.mobile.webservice.pojo.Password;
import phuonglam.mobile.webservice.pojo.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Phuo on 4/3/2016.
 */
public class EditProfileActivity extends AppCompatActivity {
    Button backBtn;
    Button saveBtn;
    EditText name;
    EditText birthday;
    EditText phoneNumber;
    EditText address;
    EditText height;
    EditText weight;
    TextView email;
    TextView username;
    String userAuth;
    String userId;
    Button changePassword;
    Button changeAva;
    ImageView avatar;
    Password password;
    String avatarLink;

    int LOADING_IMG;
    int NO_THUMB_IMG;

    public final int CREATE_PIC=1;

    RadioGroup gender;

    Calendar bday;

    Activity act =this;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        userAuth = getIntent().getStringExtra("userAuth");
        userId = getIntent().getStringExtra("userId");

        LOADING_IMG = getApplicationContext().getResources().getIdentifier("phuonglam.mobile.webservice:drawable/loading_spinner", null, null);
        NO_THUMB_IMG = getApplicationContext().getResources().getIdentifier("phuonglam.mobile.webservice:drawable/no_thumb", null, null);

        name = (EditText) findViewById(R.id.nameEditProc);
        phoneNumber = (EditText) findViewById(R.id.phoneNumberEditProc);
        address = (EditText) findViewById(R.id.addressEditProc);
        height = (EditText) findViewById(R.id.heightEditProc);
        weight = (EditText) findViewById(R.id.weightEditProc);
        email = (TextView) findViewById(R.id.emailEditProc);
        username = (TextView) findViewById(R.id.usernameEditProc);
        gender = (RadioGroup) findViewById(R.id.genderEditProc);
        backBtn = (Button) findViewById(R.id.backBtnEditProc);
        saveBtn = (Button) findViewById(R.id.saveBtnEditProc);
        birthday = (EditText) findViewById(R.id.birthdayEditProc);
        changePassword = (Button) findViewById(R.id.changePassEditProc);
        changeAva = (Button) findViewById(R.id.chooseAvaEditProc);
        avatar = (ImageView) findViewById(R.id.avaEditProc);


        changeAva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreatePictureActivity.class);
                intent.putExtra("parentActivity", "CreateProfile");
                startActivityForResult(intent, CREATE_PIC);
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater
                        = (LayoutInflater)getBaseContext()
                        .getSystemService(LAYOUT_INFLATER_SERVICE);
                final View popupView = layoutInflater.inflate(R.layout.popup_changepassword, null);
                final PopupWindow popupWindow = new PopupWindow(
                        popupView,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                Button savePassword = (Button) popupView.findViewById(R.id.saveChangePassword);
                Button cancel = (Button) popupView.findViewById(R.id.cancelChangePass);
                final EditText oldPassword = (EditText) popupView.findViewById(R.id.oldPassChangePass);
                final EditText newPassword = (EditText) popupView.findViewById(R.id.passChangePass);
                final EditText reNewPassword = (EditText) popupView.findViewById(R.id.rePassChangePass);
                savePassword.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (newPassword.getText().toString().length()<6){
                            Toast.makeText(getApplicationContext(), "Password's too short", Toast.LENGTH_SHORT).show();
                        } else {
                            if(!newPassword.getText().toString().equals(reNewPassword.getText().toString())){
                                Toast.makeText(getApplicationContext(), "Password not match", Toast.LENGTH_SHORT).show();
                            } else {

                                popupWindow.dismiss();
                                password = new Password();
                                password.setOldPassword(encodeBase64(oldPassword.getText().toString()));
                                password.setNewPassword(encodeBase64(newPassword.getText().toString()));
                                Log.e("PASSWORD",oldPassword.getText().toString());
                                new SetPasswordThread().execute("");
                            }
                        }

                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });
                popupWindow.showAsDropDown(changePassword, 50, -30);
                popupWindow.setFocusable(true);
                popupWindow.update();
            }
        });

        birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyy");
                final Date date = new Date();
                DatePickerDialog picker = new DatePickerDialog(act, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        bday.set(year, monthOfYear, dayOfMonth);
                        birthday.setText(dateFormat.format(bday.getTime()));
                    }
                }, bday.get(Calendar.YEAR), bday.get(Calendar.MONTH), bday.get(Calendar.DAY_OF_MONTH));
                picker.show();
            }
        });

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
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "Please enter name", Toast.LENGTH_SHORT).show();
                } else {
                    //name='%s', phoneNumber='%s', address='%s', height='%d', weight='%d', avatar='%s', gender='%d', birthdate='%s'"
                    user = new User();
                    user.setName(name.getText().toString());
                    user.setTest2(phoneNumber.getText().toString());
                    user.setAddress(address.getText().toString());
                    user.setHeight(Integer.parseInt(height.getText().toString()));
                    user.setWeight(Integer.parseInt(weight.getText().toString()));
                    user.setId(Integer.parseInt(userId));
                    user.setGender(gender.getCheckedRadioButtonId() == R.id.maleEditProc ? 0 : 1);
                    String bdayText=new SimpleDateFormat("yyyy-MM-dd").format(bday.getTime());
                    user.setBirthday(bdayText);
                    user.setAvatar(avatarLink);
                    new EditUserProfileThread().execute("abc");
                }
            }
        });
        new GetUserProfileThread().execute("https://takeadate-ws.herokuapp.com/getservice/getuser/"+userId);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CREATE_PIC){
            if (resultCode == RESULT_OK){
                avatarLink = data.getStringExtra("link");
                Log.e("AVATAR", avatarLink);
                avatar.setImageResource(LOADING_IMG);
                if (!ImageLoader.getInstance().isInited())
                    ImageLoader.getInstance().init(ConstantHelper.getImgLoaderConfig(getApplicationContext()));
                ImageLoader.getInstance().displayImage(avatarLink, avatar, ConstantHelper.DISPLAY_IMAGE_OPTIONS);
            }
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

    private class GetUserProfileThread extends AsyncTask<String, Void, Void> {
        private ProgressDialog dialog = new ProgressDialog(EditProfileActivity.this);
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
                Log.e("USER AUTH", userAuth);
                byte[] data = userAuth.getBytes("UTF-8");
                String base64 = Base64.encodeToString(data, Base64.DEFAULT);
                Log.e("AUTH", base64);
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
                name.setText(res.getString("name"));
                //gender.setText(res.getInt("gender")==1?"Female":"Male");
                if (res.getInt("gender") == 1){
                    gender.check(R.id.maleEditProc);
                } else {
                    gender.check(R.id.femaleEditProc);
                }
                birthday.setText(res.getString("birthday"));
                username.setText(res.getString("test"));
                weight.setText(res.getString("weight"));
                height.setText(res.getString("height"));
                email.setText(res.getString("email"));
                phoneNumber.setText(res.getString("test2"));
                address.setText(res.getString("address"));
                bday = Calendar.getInstance();
                if (!(res.getString("birthday").equals(null) || res.getString("birthday").equals(""))){
                    bday.setTime((new SimpleDateFormat("yyyy-MM-dd")).parse(res.getString("birthday")));
                }
                birthday.setText((new SimpleDateFormat("dd-MM-yyyy")).format(bday.getTime()));

                if(res.getString("avatar").equals(null) || res.getString("avatar").equals("null")){
                    avatar.setImageResource(NO_THUMB_IMG);
                } else {
                    avatar.setImageResource(LOADING_IMG);
                    if (!ImageLoader.getInstance().isInited())
                        ImageLoader.getInstance().init(ConstantHelper.getImgLoaderConfig(getApplicationContext()));
                    ImageLoader.getInstance().displayImage(res.getString("avatar"), avatar, ConstantHelper.DISPLAY_IMAGE_OPTIONS);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
    private class EditUserProfileThread extends AsyncTask<String, Void, Void>{

        private ProgressDialog dialog = new ProgressDialog(EditProfileActivity.this);
        private String response;
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
            Call<Message> call = apiService.editUser(user);
            call.enqueue(new Callback<Message>() {
                @Override
                public void onResponse(Call<Message> call, Response<Message> response) {
                    String result = response.body().getMessage();
                    Toast.makeText(EditProfileActivity.this, result, Toast.LENGTH_SHORT).show();
                    dialog.hide();
                }

                @Override
                public void onFailure(Call<Message> call, Throwable t) {
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
    private String encodeBase64(String source){
        byte[] data = new byte[0];
        try {
            data = source.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String base64_temp = Base64.encodeToString(data, Base64.DEFAULT);
        String base64 = base64_temp.substring(0, base64_temp.length() - 1);
        return base64;
    }
    private class SetPasswordThread extends AsyncTask<String, Void, Void>{

        private ProgressDialog dialog = new ProgressDialog(EditProfileActivity.this);
        @Override
        protected void onPreExecute() {
            dialog.setMessage("Please wait..");
            dialog.show();
            Log.e("START", "Starting..");
        }

        protected Void doInBackground(String... params) {

            Interceptor interceptor = new Interceptor() {
                @Override
                public okhttp3.Response intercept(Chain chain) throws IOException {
                    Request newRequest = chain.request().newBuilder().addHeader("authorization", "Basic "+encodeBase64(userAuth)).build();
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


            Call<Message> call = apiService.setPassword(password);
            call.enqueue(new Callback<Message>() {
                @Override
                public void onResponse(Call<Message> call, Response<Message> response) {
                    String result = response.body().getMessage();
                    Toast.makeText(EditProfileActivity.this, result, Toast.LENGTH_SHORT).show();
                    if (result.equals("Successful")){
                        userAuth = userAuth.replace(password.getOldPassword(), password.getNewPassword());
                        Log.e("PASSWORD", userAuth);
                    }
                    dialog.hide();
                }

                @Override
                public void onFailure(Call<Message> call, Throwable t) {
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

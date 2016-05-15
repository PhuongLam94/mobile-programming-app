package phuonglam.mobile.webservice.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import phuonglam.mobile.webservice.helper.ConstantHelper;
import phuonglam.mobile.webservice.helper.FontManager;
import phuonglam.mobile.webservice.R;
import phuonglam.mobile.webservice.helper.MyApiEndpointInterface;
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
public class CreateProfileActivity extends AppCompatActivity {
    Button backBtn;
    Button saveBtn;
    Button chooseAva;

    EditText name;
    EditText birthday;
    EditText phoneNumber;
    EditText address;
    EditText height;
    EditText weight;
    EditText email;
    EditText username;
    EditText passsword;
    EditText rePassword;
    ImageView avatar;
    RadioGroup gender;
    ImageView userNameStt;
    ImageView emailStt;
    ImageView passwordStt;

    String userAuth;
    String userId;
    String avatarLink;
    User user;
    String userNameStr;
    String emailStr;
    boolean validUserName = false;
    boolean validEmail = false;

    int CHECK_ICON;
    int ERROR_ICON;
    int LOAD_ICON;
    int LOADING_IMG;
    int NO_THUMB_IMG;

    public final int CREATE_PIC=1;



    Activity act =this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createprofile);

        CHECK_ICON = getApplicationContext().getResources().getIdentifier("phuonglam.mobile.webservice:drawable/abc_btn_check_to_on_mtrl_015", null, null);
        ERROR_ICON = getApplicationContext().getResources().getIdentifier("phuonglam.mobile.webservice:drawable/error_icon", null, null);
        LOAD_ICON = getApplicationContext().getResources().getIdentifier("phuonglam.mobile.webservice:drawable/load_icon", null, null);
        LOADING_IMG = getApplicationContext().getResources().getIdentifier("phuonglam.mobile.webservice:drawable/loading_spinner", null, null);
        NO_THUMB_IMG = getApplicationContext().getResources().getIdentifier("phuonglam.mobile.webservice:drawable/no_thumb", null, null);


        name = (EditText) findViewById(R.id.nameCreateProc);
        phoneNumber = (EditText) findViewById(R.id.phoneNumberCreateProc);
        address = (EditText) findViewById(R.id.addressCreateProc);
        height = (EditText) findViewById(R.id.heightCreateProc);
        weight = (EditText) findViewById(R.id.weightCreateProc);
        email = (EditText) findViewById(R.id.emailCreateProc);
        username = (EditText) findViewById(R.id.usernameCreateProc);
        passsword = (EditText) findViewById(R.id.passwordCreateProc);
        rePassword = (EditText) findViewById(R.id.repasswordCreateProc);
        avatar = (ImageView) findViewById(R.id.avaCreateProc);
        birthday = (EditText) findViewById(R.id.birthdayCreateProc);

        gender = (RadioGroup) findViewById(R.id.genderCreateProc);

        backBtn = (Button) findViewById(R.id.backBtnCreateProc);
        saveBtn = (Button) findViewById(R.id.saveBtnCreateProc);
        chooseAva = (Button) findViewById(R.id.chooseAvaCreateProc);

        userNameStt = (ImageView) findViewById(R.id.usernameSttIcon);
        emailStt = (ImageView) findViewById(R.id.emailSttIcon);
        passwordStt = (ImageView) findViewById(R.id.passwordSttIcon);

        userNameStt.setVisibility(View.INVISIBLE);
        emailStt.setVisibility(View.INVISIBLE);
        passwordStt.setVisibility(View.INVISIBLE);

        username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Log.e("CHANGED", username.getText().toString());
                    userNameStt.setImageResource(LOAD_ICON);
                    userNameStt.setVisibility(View.VISIBLE);
                    userNameStr = username.getText().toString();
                    new CheckUserNameThread().execute("");
                }
            }
        });
        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Log.e("CHANGED", email.getText().toString());
                    emailStt.setImageResource(LOAD_ICON);
                    emailStt.setVisibility(View.VISIBLE);
                    emailStr = email.getText().toString();
                    new CheckEmailThread().execute("");
                }
            }
        });
        rePassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (rePassword.getText().toString().equals(passsword.getText().toString())) {
                    passwordStt.setImageResource(CHECK_ICON);
                } else {
                    passwordStt.setImageResource(ERROR_ICON);
                }
                passwordStt.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        chooseAva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreatePictureActivity.class);
                intent.putExtra("parentActivity", "CreateProfile");
                startActivityForResult(intent, CREATE_PIC);
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkForm()){
                    //name='%s', phoneNumber='%s', address='%s', height='%d', weight='%d', avatar='%s', gender='%d', birthdate='%s'"
                    user = new User();
                    user.setTest(username.getText().toString());
                    user.setEmail(email.getText().toString());
                    try {
                        byte[] pass = passsword.getText().toString().getBytes("UTF-8");
                        String passEn = Base64.encodeToString(pass, Base64.DEFAULT);
                        user.setPassword(passEn.substring(0, passEn.length()-1));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    String bdayText;
                    if(birthday.getText().toString()=="")
                        bdayText=null;
                    else{
                        bdayText=birthday.getText().toString();
                        bdayText = bdayText.substring(3, 6)+bdayText.substring(0, 3)+bdayText.substring(6);
                    }
                    user.setBirthday(bdayText);
                    user.setGender(gender.getCheckedRadioButtonId() == R.id.maleCreateProc ? 0 : 1);
                    user.setName(name.getText().toString());
                    user.setTest2(phoneNumber.getText().toString());
                    user.setAddress(address.getText().toString());
                    user.setHeight(height.getText().toString().equals("") ? 0 : Integer.parseInt(height.getText().toString()));
                    user.setWeight(weight.getText().toString().equals("") ? 0 : Integer.parseInt(weight.getText().toString()));
                    user.setAvatar(avatarLink);
                    new CreateUserProfileThread().execute("");
                }

            }
        });
        birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyy");
                Calendar now = Calendar.getInstance();
                final Date date = new Date();
                DatePickerDialog picker = new DatePickerDialog(act, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        birthday.setText(dateFormat.format(newDate.getTime()));
                    }
                }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                picker.show();
            }
        });


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
        private boolean checkForm(){
        if (username.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "Please enter username", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (email.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "Please enter email", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (passsword.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "Please enter password", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (name.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "Please enter name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!validUserName){
            Toast.makeText(getApplicationContext(), "Invalid username", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!validEmail){
            Toast.makeText(getApplicationContext(), "Invalid email", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (passsword.getText().toString().length()<6){
            Toast.makeText(getApplicationContext(), "Password must have at least 6 characters", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!rePassword.getText().toString().equals(passsword.getText().toString())){
            Toast.makeText(getApplicationContext(), "Password not match", Toast.LENGTH_SHORT).show();
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

    private class CreateUserProfileThread extends AsyncTask<String, Void, Void>{

        private ProgressDialog dialog = new ProgressDialog(CreateProfileActivity.this);
        private String response;
        @Override
        protected void onPreExecute() {
            dialog.setMessage("Please wait..");
            dialog.show();
            Log.e("START", "Starting..");
        }

        protected Void doInBackground(String... params) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ConstantHelper.GLOBALHOST)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            MyApiEndpointInterface apiService = retrofit.create(MyApiEndpointInterface.class);
            Call<Message> call = apiService.createUser(user);
            call.enqueue(new Callback<Message>() {
                @Override
                public void onResponse(Call<Message> call, Response<Message> response) {
                    String result = response.body().getMessage();
                    Toast.makeText(CreateProfileActivity.this, result, Toast.LENGTH_SHORT).show();
                    if (result.equals("Successful")){
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
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
    private class CheckUserNameThread extends AsyncTask<String, Void, Void>{

        private ProgressDialog dialog = new ProgressDialog(CreateProfileActivity.this);
        private String response;
        @Override
        protected void onPreExecute() {

        }

        protected Void doInBackground(String... params) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ConstantHelper.GLOBALHOST)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            MyApiEndpointInterface apiService = retrofit.create(MyApiEndpointInterface.class);
            Call<Message> call = apiService.checkUserName(userNameStr);
            call.enqueue(new Callback<Message>() {
                @Override
                public void onResponse(Call<Message> call, Response<Message> response) {
                    String result = response.body().getMessage();
                    if (result.equals("Valid")){
                        userNameStt.setImageResource(CHECK_ICON);
                        validUserName = true;
                    } else {
                        userNameStt.setImageResource(ERROR_ICON);
                        validUserName = false;
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
    private class CheckEmailThread extends AsyncTask<String, Void, Void>{

        @Override
        protected void onPreExecute() {

        }

        protected Void doInBackground(String... params) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ConstantHelper.GLOBALHOST)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            MyApiEndpointInterface apiService = retrofit.create(MyApiEndpointInterface.class);
            Call<Message> call = apiService.checkEmail(emailStr);
            call.enqueue(new Callback<Message>() {
                @Override
                public void onResponse(Call<Message> call, Response<Message> response) {
                    String result = response.body().getMessage();
                    if (result.equals("Valid")){
                        emailStt.setImageResource(CHECK_ICON);
                        validEmail = true;
                    } else {
                        emailStt.setImageResource(ERROR_ICON);
                        validEmail = false;
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

package phuonglam.mobile.webservice.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import phuonglam.mobile.webservice.R;
import phuonglam.mobile.webservice.helper.ConstantHelper;
import phuonglam.mobile.webservice.helper.MyApiEndpointInterface;
import phuonglam.mobile.webservice.helper.NavigationHelper;
import phuonglam.mobile.webservice.helper.RealPathUtil;
import phuonglam.mobile.webservice.pojo.ImageResponse;
import phuonglam.mobile.webservice.pojo.Message;
import phuonglam.mobile.webservice.pojo.Picture;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;

/**
 * Created by Phuo on 4/28/2016.
 */
public class CreatePictureActivity extends AppCompatActivity {
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_BROWSE_PHOTO = 2;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    Button takePictureBtn;
    ImageView img;
    EditText des;
    Button backBtn;
    Button saveBtn;
    Button browseBtn;

    String userId;
    String userAuth;
    String desString;
    String imgLink;
    String parentActivity;

    Activity act = this;

    File photoFile = null;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createpicture);Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        userAuth = getIntent().getStringExtra("userAuth");
        userId = getIntent().getStringExtra("userId");
        parentActivity = getIntent().getStringExtra("parentActivity");



        takePictureBtn = (Button) findViewById(R.id.takePicBtn);
        img = (ImageView) findViewById(R.id.imgCreatePic);
        des = (EditText) findViewById(R.id.desCreatePic);
        backBtn = (Button) findViewById(R.id.backCreatePicBtn);
        saveBtn = (Button) findViewById(R.id.saveCreatePicBtn);
        browseBtn = (Button) findViewById(R.id.browsePicBtn);

        if (!parentActivity.equals("ListPicture") && !parentActivity.equals("Newsfeed")){
            des.setVisibility(View.INVISIBLE);
        }
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), parentActivity.equals("ListPicture")?ListPictureActivity.class:NewsfeedActivity.class);
                intent.putExtra("userAuth", userAuth);
                intent.putExtra("userId", userId);
                intent.putExtra("currentUserId", userId);
                startActivity(intent);
            }
        });
        takePictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        Log.e("ERR", "problem with create file "+ex.getMessage());
                    }
                    if (photoFile != null) {
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(photoFile));
                        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                    }
                } else {
                    Log.e("ERR", "no camera app found");
                }
            }
        });

        browseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browsePictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // Ensure that there's a camera activity to handle the intent
                if (browsePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(browsePictureIntent, REQUEST_BROWSE_PHOTO);
                } else {
                    Log.e("ERR", "no browse pic app found");
                }
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create intent to deliver some kind of result data

                if (photoFile != null){
                    Log.e("IMAGE", photoFile.getAbsolutePath());
                    desString = des.getText().toString();
                    new CreatePictureThread(act).execute("abc");
                } else {
                    Toast.makeText(getApplicationContext(), "Please take or select a picture", Toast.LENGTH_SHORT).show();
                }
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
        NavigationHelper.Navigation(id, this);
        return super.onOptionsItemSelected(item);
    }
    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }
    @Override
     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            if (!ImageLoader.getInstance().isInited())
                ImageLoader.getInstance().init(ConstantHelper.getImgLoaderConfig(getApplicationContext()));
            ImageLoader.getInstance().displayImage(Uri.fromFile(photoFile).toString(), img, ConstantHelper.DISPLAY_IMAGE_OPTIONS);
        }
        if (requestCode == REQUEST_BROWSE_PHOTO && resultCode == RESULT_OK){
            Uri uri = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
            if(cursor.moveToFirst()){
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String realPath = cursor.getString(columnIndex);
                cursor.close();
                photoFile = new File(realPath);
                Log.e("ISFILE", photoFile.isFile()?"IS FILE":"NO FILE"+", "+realPath);
                if (!ImageLoader.getInstance().isInited())
                    ImageLoader.getInstance().init(ConstantHelper.getImgLoaderConfig(getApplicationContext()));
                ImageLoader.getInstance().displayImage(uri.toString(), img);
            } else {
                Log.e("ERROR", "ERROR");
            }

        }
    }
    private class CreatePictureThread extends AsyncTask<String, Void, Void>{

        private ProgressDialog dialog = new ProgressDialog(CreatePictureActivity.this);
        private Activity activity;
        private String response;
        public CreatePictureThread(Activity act){
            activity = act;
        }
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
                    Request newRequest = chain.request().newBuilder().build();
                    return chain.proceed(newRequest);
                }
            };
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.interceptors().add(interceptor);
            builder.connectTimeout(60, TimeUnit.SECONDS);
            builder.readTimeout(60, TimeUnit.SECONDS);
            OkHttpClient client = builder.build();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.imgur.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
            MyApiEndpointInterface apiService = retrofit.create(MyApiEndpointInterface.class);

            MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
            RequestBody body = RequestBody.create(MEDIA_TYPE_PNG, photoFile);


            Call<ImageResponse> call = apiService.postImage("Client-ID 142cd168e236fc9", body);
            call.enqueue(new Callback<ImageResponse>() {
                @Override
                public void onResponse(Call<ImageResponse> call, Response<ImageResponse> response) {
                    ImageResponse result = response.body();
                    dialog.hide();
                    if (result.success){
                        if (parentActivity.equals("ListPicture") || parentActivity.equals("Newsfeed")){
                            imgLink = result.data.link;
                            new AddPictureThread(activity).execute("");
                        } else {
                            Intent resultIntent = new Intent();
                            Log.e("PARENT ACTIVITY", result.data.link);
                            resultIntent.putExtra("link", result.data.link);
                            activity.setResult(RESULT_OK, resultIntent);
                            activity.finish();
                        }
                    } else {
                        Toast.makeText(activity.getApplicationContext(), "Upload failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ImageResponse> call, Throwable t) {
                    dialog.hide();
                    t.printStackTrace();
                }
            });
            return null;
        }

    }
    private class AddPictureThread extends AsyncTask<String, Void, Void>{

        private ProgressDialog dialog = new ProgressDialog(CreatePictureActivity.this);
        private Activity activity;
        private String response;
        public AddPictureThread(Activity act){
            activity = act;
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
                    Request newRequest = chain.request().newBuilder().addHeader("authorization", "Basic " + base64).build();
                    return chain.proceed(newRequest);
                }
            };
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.interceptors().add(interceptor);
            builder.connectTimeout(60, TimeUnit.SECONDS);
            builder.readTimeout(60, TimeUnit.SECONDS);
            OkHttpClient client = builder.build();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ConstantHelper.GLOBALHOST)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
            MyApiEndpointInterface apiService = retrofit.create(MyApiEndpointInterface.class);
            Picture picture = new Picture();
            picture.setDescription(desString);
            picture.setContent(imgLink);
            picture.setUserid(Integer.parseInt(userId));
            Call<Message> call = apiService.addPicture(picture);
            call.enqueue(new Callback<Message>() {
                @Override
                public void onResponse(Call<Message> call, Response<Message> response) {
                    dialog.hide();
                    Toast.makeText(activity.getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("MESSAGE", response.body().getMessage());
                    if (response.body().getMessage().equals("Successful")){
                        Intent intent = new Intent(getApplicationContext(), parentActivity.equals("ListPicture")?ListPictureActivity.class:NewsfeedActivity.class);
                        intent.putExtra("userAuth", userAuth);
                        intent.putExtra("userId", userId);
                        intent.putExtra("currentUserId", userId);
                        startActivity(intent);
                    }
                }

                @Override
                public void onFailure(Call<Message> call, Throwable t) {
                    dialog.hide();
                    t.printStackTrace();
                }
            });
            return null;
        }

    }
}

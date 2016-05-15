package phuonglam.mobile.webservice.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import phuonglam.mobile.webservice.R;
import phuonglam.mobile.webservice.adapter.CommentItemAdapter;
import phuonglam.mobile.webservice.adapter.CustomAdapter;
import phuonglam.mobile.webservice.helper.ConstantHelper;
import phuonglam.mobile.webservice.helper.FontManager;
import phuonglam.mobile.webservice.helper.MyApiEndpointInterface;
import phuonglam.mobile.webservice.helper.NavigationHelper;
import phuonglam.mobile.webservice.helper.OnSwipeTouchListener;
import phuonglam.mobile.webservice.pojo.Comment;
import phuonglam.mobile.webservice.pojo.Message;
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
public class PictureActivity extends AppCompatActivity{
    String userAuth;
    String userId;
    String currentUserId;
    String parentActivity;

    String pictureId;
    int nextId;
    int prevId;

    ImageView img;
    TextView description;
    Button backBtn;
    ListView listComment;
    EditText commentText;
    TextView postComment;
    Activity act = this;
    Picture res;
    Comment comment;
    Button deleteBtn;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        parentActivity = getIntent().getStringExtra("parentActivity");
        userAuth = getIntent().getStringExtra("userAuth");
        userId = getIntent().getStringExtra("userId");
        currentUserId = getIntent().getStringExtra("currentUserId");
        pictureId = getIntent().getStringExtra("pictureId");

        img = (ImageView) findViewById(R.id.img);
        description = (TextView) findViewById(R.id.description);
        backBtn = (Button) findViewById(R.id.backPicBtn);
        listComment = (ListView) findViewById(R.id.commentList);
        listComment.setVisibility(View.INVISIBLE);
        commentText = (EditText) findViewById(R.id.commentText);
        postComment = (TextView) findViewById(R.id.postComment);
        deleteBtn = (Button) findViewById(R.id.deletePicBtn);
        if (!currentUserId.equals(userId)){
            deleteBtn.setVisibility(View.INVISIBLE);
        }
        Typeface iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);
        postComment.setTypeface(iconFont);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DeletePictureThread(act).execute("abc");
            }
        });

        postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comment = new Comment();
                comment.setContent(commentText.getText().toString());
                comment.setPictureid(Integer.parseInt(pictureId));
                comment.setUserid(Integer.parseInt(userId));
                new AddCommentThread(act).execute("abc");
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(parentActivity.equals("ListPicture")){
                    Intent intent = new Intent(getApplicationContext(), ListPictureActivity.class);
                    intent.putExtra("userAuth", userAuth);
                    intent.putExtra("userId", userId);
                    intent.putExtra("currentUserId", currentUserId);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getApplicationContext(), NewsfeedActivity.class);
                    intent.putExtra("userAuth", userAuth);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                }

            }
        });
        new GetPictureThread(this).execute("abc");
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

    private class GetPictureThread extends AsyncTask<String, Void, Void> {
        private Activity activity;
        private ProgressDialog dialog = new ProgressDialog(PictureActivity.this);
        public GetPictureThread(Activity act){
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
            builder.connectTimeout(60, TimeUnit.SECONDS);
            builder.readTimeout(60, TimeUnit.SECONDS);
            OkHttpClient client = builder.build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ConstantHelper.GLOBALHOST)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
            MyApiEndpointInterface apiService = retrofit.create(MyApiEndpointInterface.class);
            Call<Picture> call = apiService.getPicture(currentUserId, pictureId);
            call.enqueue(new Callback<Picture>() {
                @Override
                public void onResponse(Call<Picture> call, Response<Picture> response) {
                    ImageLoader imageLoader = ImageLoader.getInstance();
                    res = response.body();
                    nextId = res.getNext();
                    prevId = res.getPrev();
                    Log.e("COMMENT", res.getLstcomment().toString());
                    imageLoader.loadImage(res.getContent(), new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            img.setImageBitmap(loadedImage);
                            img.setOnTouchListener(new OnSwipeTouchListener(act){
                                public void onSwipeRight() {
                                    if (prevId!=0){
                                        Intent intent = new Intent(getApplicationContext(), PictureActivity.class);
                                        intent.putExtra("userAuth", userAuth);
                                        intent.putExtra("userId", userId);
                                        intent.putExtra("currentUserId", currentUserId);
                                        intent.putExtra("pictureId", prevId+"");
                                        startActivity(intent);
                                    }
                                }
                                public void onSwipeLeft() {
                                    if (nextId!=0){
                                        Intent intent = new Intent(getApplicationContext(), PictureActivity.class);
                                        intent.putExtra("userAuth", userAuth);
                                        intent.putExtra("userId", userId);
                                        intent.putExtra("currentUserId", currentUserId);
                                        intent.putExtra("pictureId", nextId+"");
                                        startActivity(intent);
                                    }
                                }
                            });
                        }
                    });
                    description.setText(res.getDescription());
                    //CommentItemAdapter adapter = new CommentItemAdapter(getApplicationContext(), res.getLstcomment(), getIntent(), activity);
                    User test = new User();
                    test.setName("aaaaa");
                    List<User> listTest = new ArrayList<>();
                    listTest.add(test);
                    CommentItemAdapter t = new CommentItemAdapter(getApplicationContext(), res.getLstcomment(), getIntent(), activity);
                    listComment.setAdapter(t);
                    listComment.setVisibility(View.VISIBLE);
                    dialog.hide();
                }
                @Override
                public void onFailure(Call<Picture> call, Throwable t) {
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

    private class AddCommentThread extends AsyncTask<String, Void, Void>{
        private Activity activity;

        private ProgressDialog dialog = new ProgressDialog(PictureActivity.this);
        private String response;
        public AddCommentThread(Activity act){
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
            builder.connectTimeout(60, TimeUnit.SECONDS);
            builder.readTimeout(60, TimeUnit.SECONDS);
            OkHttpClient client = builder.build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ConstantHelper.GLOBALHOST)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
            MyApiEndpointInterface apiService = retrofit.create(MyApiEndpointInterface.class);
            Call<List<Comment>> call = apiService.addComment(comment);
            call.enqueue(new Callback<List<Comment>>() {
                @Override
                public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                    List<Comment> lstComment = response.body();
                    Log.e("COMMENT", lstComment.equals(null)?"NULL":"NOT NULL");
                    CommentItemAdapter t = new CommentItemAdapter(getApplicationContext(), lstComment, getIntent(), activity);
                    listComment.setAdapter(t);
                    listComment.setSelection(t.getCount()-1);
                    commentText.setText("");
                    dialog.hide();
                }
                @Override
                public void onFailure(Call<List<Comment>> call, Throwable t) {
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

    private class DeletePictureThread extends AsyncTask<String, Void, Void>{
        private Activity activity;

        private ProgressDialog dialog = new ProgressDialog(PictureActivity.this);
        private String response;
        public DeletePictureThread(Activity act){
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
            builder.connectTimeout(60, TimeUnit.SECONDS);
            builder.readTimeout(60, TimeUnit.SECONDS);
            OkHttpClient client = builder.build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ConstantHelper.GLOBALHOST)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
            MyApiEndpointInterface apiService = retrofit.create(MyApiEndpointInterface.class);
            Call<Message> call = apiService.deletePicture(userId, pictureId);
            call.enqueue(new Callback<Message>() {
                @Override
                public void onResponse(Call<Message> call, Response<Message> response) {
                    String result = response.body().getMessage();
                    Toast.makeText(PictureActivity.this, result, Toast.LENGTH_SHORT).show();
                    if (result.equals("Successful")){
                        Intent intent = new Intent(getApplicationContext(), ListPictureActivity.class);
                        intent.putExtra("userAuth", userAuth);
                        intent.putExtra("userId", userId);
                        intent.putExtra("currentUserId", currentUserId);
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
}

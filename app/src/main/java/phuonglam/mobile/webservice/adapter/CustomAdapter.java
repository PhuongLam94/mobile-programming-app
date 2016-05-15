package phuonglam.mobile.webservice.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

import phuonglam.mobile.webservice.R;
import phuonglam.mobile.webservice.activity.ProfileActivity;
import phuonglam.mobile.webservice.helper.FontManager;
import phuonglam.mobile.webservice.pojo.User;

/**
 * Created by Phuo on 25/03/2016.
 */
public class CustomAdapter extends ArrayAdapter<User> {

    private final Context context;
    private final List<User> values;
    private final Intent intent;
    private final Activity activity;
    public CustomAdapter(Context context, List<User> values, Intent intent, Activity activity) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
        this.intent = intent;
        this.activity = activity;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.list_item_user, parent, false);
        TextView name = (TextView) rowView.findViewById(R.id.name);
        final ImageView ava = (ImageView) rowView.findViewById(R.id.ava);
        final User temp = values.get(position);
        name.setText(" " + temp.getName());
        if (!temp.getAvatar().equals(null) && !temp.getAvatar().equals("null")){
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.loadImage(temp.getAvatar(),new SimpleImageLoadingListener(){
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    ava.setImageBitmap(loadedImage);
                }
            });
        } else {
            int loadingid = context.getResources().getIdentifier("phuonglam.mobile.webservice:drawable/user", null, null);
            ava.setImageResource(loadingid);
        }
                rowView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent2 = new Intent(context, ProfileActivity.class);
                        Log.e("INTENT", intent.getStringExtra("userAuth") + ", " + intent.getStringExtra("userId"));
                        intent2.putExtra("userAuth", intent.getStringExtra("userAuth"));
                        intent2.putExtra("userId", intent.getStringExtra("userId"));
                        intent2.putExtra("currentUserId", temp.getId() + "");
                        activity.startActivity(intent2);
                    }
                });
                return rowView;
            }
        }

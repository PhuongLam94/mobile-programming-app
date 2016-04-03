package phuonglam.mobile.webservice;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.List;

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
        View rowView = inflater.inflate(R.layout.list_item, parent, false);
        Typeface iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(rowView.findViewById(R.id.item), iconFont);
        TextView name = (TextView) rowView.findViewById(R.id.name);
        final User temp = values.get(position);
        name.setText(" " + temp.getName());
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(context, ProfileActivity.class);
                Log.e("INTENT", intent.getStringExtra("userAuth")+", "+intent.getStringExtra("userId"));
                intent2.putExtra("userAuth", intent.getStringExtra("userAuth"));
                intent2.putExtra("userId", intent.getStringExtra("userId"));
                intent2.putExtra("currentUserId", temp.getId()+"");
                activity.startActivity(intent2);
            }
        });
        return rowView;
    }
}

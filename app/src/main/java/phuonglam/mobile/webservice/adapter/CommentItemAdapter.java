package phuonglam.mobile.webservice.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import phuonglam.mobile.webservice.R;
import phuonglam.mobile.webservice.activity.ProfileActivity;
import phuonglam.mobile.webservice.helper.FontManager;
import phuonglam.mobile.webservice.pojo.Comment;
import phuonglam.mobile.webservice.pojo.User;

/**
 * Created by Phuo on 25/03/2016.
 */
public class CommentItemAdapter extends ArrayAdapter<Comment> {

    private final Context context;
    private final List<Comment> values;
    private final Intent intent;
    private final Activity activity;
    public CommentItemAdapter(Context context, List<Comment> values, Intent intent, Activity activity) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
        this.intent = intent;
        this.activity = activity;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item_comment, parent, false);
        TextView name = (TextView) rowView.findViewById(R.id.nameComment);
        TextView content = (TextView) rowView.findViewById(R.id.contentComment);
        final Comment temp = values.get(position);
        name.setText(" " + temp.getName());
        content.setText(temp.getContent());
        return rowView;
    }
}

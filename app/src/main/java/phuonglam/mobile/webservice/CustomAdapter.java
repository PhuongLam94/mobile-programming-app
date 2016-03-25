package phuonglam.mobile.webservice;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Phuo on 25/03/2016.
 */
public class CustomAdapter extends ArrayAdapter<User> {

    private final Context context;
    private final List<User> values;
    public CustomAdapter(Context context, List<User> values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item, parent, false);
        Typeface iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(rowView.findViewById(R.id.item), iconFont);
        TextView name = (TextView) rowView.findViewById(R.id.name);
        TextView username = (TextView) rowView.findViewById(R.id.username);
        TextView status = (TextView) rowView.findViewById(R.id.status);
        TextView email = (TextView) rowView.findViewById(R.id.email);

        User temp = values.get(position);
        name.setText(" "+temp.name);
        username.setText(" "+temp.username);
        status.setText(" "+temp.status);
        email.setText(" "+temp.email);

        return rowView;

    }
}

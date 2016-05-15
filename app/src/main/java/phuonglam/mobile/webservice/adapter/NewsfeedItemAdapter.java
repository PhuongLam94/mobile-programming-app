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
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.w3c.dom.Text;

import java.util.List;

import phuonglam.mobile.webservice.R;
import phuonglam.mobile.webservice.activity.PictureActivity;
import phuonglam.mobile.webservice.activity.ProfileActivity;
import phuonglam.mobile.webservice.helper.ConstantHelper;
import phuonglam.mobile.webservice.helper.FontManager;
import phuonglam.mobile.webservice.pojo.Comment;
import phuonglam.mobile.webservice.pojo.Picture;
import phuonglam.mobile.webservice.pojo.User;

/**
 * Created by Phuo on 25/03/2016.
 */
public class NewsfeedItemAdapter extends ArrayAdapter<Picture> {

    private final Context context;
    private final List<Picture> values;
    private final Intent intent;
    private final Activity activity;
    private String userAuth;
    private String userId;
    private String parentActivity;

    public NewsfeedItemAdapter(Context context, List<Picture> values, Intent intent, Activity activity, String userAuth, String userId, String parentActivity) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
        this.intent = intent;
        this.activity = activity;
        this.userAuth = userAuth;
        this.userId = userId;
        this.parentActivity = parentActivity;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item_newsfeed, parent, false);
        TextView name = (TextView) rowView.findViewById(R.id.nameNF);
        ImageView img = (ImageView) rowView.findViewById(R.id.imgNF);
        TextView des = (TextView) rowView.findViewById(R.id.desNF);
        TextView comment = (TextView) rowView.findViewById(R.id.noCommentNF);
        TextView time = (TextView) rowView.findViewById(R.id.timeNF);
        final Picture temp = values.get(position);
        int noComment = temp.getLstcomment().size();

        name.setText(temp.getName());
        if (!ImageLoader.getInstance().isInited())
            ImageLoader.getInstance().init(ConstantHelper.getImgLoaderConfig(context));
        ImageLoader.getInstance().displayImage(temp.getContent(), img, ConstantHelper.DISPLAY_IMAGE_OPTIONS);
        des.setText(temp.getDescription());
        comment.setText(noComment == 0 ? "" : (noComment == 1 ? "1 comment" : noComment + " comments"));
        time.setText(temp.getTime());
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity.getApplicationContext(), PictureActivity.class);
                intent.putExtra("userAuth", userAuth);
                intent.putExtra("userId", userId);
                intent.putExtra("currentUserId", temp.getUserid()+"");
                intent.putExtra("pictureId", temp.getId() + "");
                intent.putExtra("parentActivity", parentActivity);
                activity.startActivity(intent);
            }
        });
        return rowView;
    }
}

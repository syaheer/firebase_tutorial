package com.syahiramir.firebasetutorial;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class CommentsAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<Comment> commentsList;
    private static LayoutInflater inflater = null;

    public CommentsAdapter(Activity activity, ArrayList<Comment> commentsList) {
        this.activity = activity;
        this.commentsList = commentsList;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return commentsList.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.item_comments, null);

        TextView textName = vi.findViewById(R.id.text_name); // title
        TextView textComment = vi.findViewById(R.id.text_comment); // artist name

        Comment comment = commentsList.get(position);

        if (comment.getUserId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            parent.setLongClickable(true);
        } else {
            parent.setLongClickable(false);
        }

        // Setting all values in listview
        textName.setText(comment.getName());
        textComment.setText(comment.getComment());
        return vi;
    }
}

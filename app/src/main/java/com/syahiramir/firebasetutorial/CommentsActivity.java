package com.syahiramir.firebasetutorial;

import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CommentsActivity extends AppCompatActivity {

    String userName;
    FirebaseFirestore db;
    ArrayList<Comment> comments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        final EditText textComments = findViewById(R.id.text_comment);
        Button buttonSend = findViewById(R.id.button_send);

        // Get user's name
        db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        userName = document.getString("name");
                    }
                }
            }
        });

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newComment = textComments.getText().toString();
                if (!newComment.isEmpty()) {
                    Comment comment = new Comment(userName, newComment, FirebaseAuth.getInstance().getCurrentUser().getUid(), null);
                    db.collection("comments")
                            .add(comment)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(CommentsActivity.this, "Comment added", Toast.LENGTH_LONG).show();
                                    // remove the comment text
                                    textComments.setText("");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(CommentsActivity.this, "Adding comment failed. Please try again.", Toast.LENGTH_LONG).show();
                                }
                            });

                }
            }
        });

        ListView listComments = findViewById(R.id.list_comments);
        final CommentsAdapter commentsAdapter = new CommentsAdapter(this, comments);
        registerForContextMenu(listComments);

        listComments.setAdapter(commentsAdapter);

        // listen for realtime update
        db.collection("comments")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }

                        // remove all data first
                        comments.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            Comment comment;
                            comment = doc.toObject(Comment.class);
                            comment.setCommentId(doc.getId());
                            comments.add(comment);
                        }

                        // notify our adapter
                        commentsAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void editComment(String commentId, String newComment) {
        DocumentReference commentRef = db.collection("comments").document(commentId);
        commentRef.update("comment", newComment);
    }

    private void deleteComment(String commentId) {
        DocumentReference commentRef = db.collection("comments").document(commentId);
        commentRef.delete();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, v.getId(), 0, "Edit");
        menu.add(0, v.getId(), 0, "Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getTitle().toString()) {
            case "Delete":
                deleteComment(comments.get(info.position).getCommentId());
                return true;
            case "Edit":
                showEditCommentDialog(comments.get(info.position).getCommentId());
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void showEditCommentDialog(final String commentId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CommentsActivity.this);

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_comment, null);

        // Set the custom layout as alert dialog view
        builder.setView(dialogView);

        // Get the custom alert dialog view widgets reference
        Button buttonSubmit = dialogView.findViewById(R.id.button_submit);
        final EditText text_comment = dialogView.findViewById(R.id.text_comment);

        // Create the alert dialog
        final AlertDialog dialog = builder.create();

        // Set button click listener
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the alert dialog
                if (!text_comment.getText().toString().isEmpty())
                    editComment(commentId, text_comment.getText().toString());
                dialog.cancel();
            }
        });

        // Display the custom alert dialog on interface
        dialog.show();
    }
}

package com.example.hikergram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class DisplayPostActivity extends AppCompatActivity {
    private TextView displayNameOfHike, displayHikeDescription, displayNumber, authorUsername;
    private Button goBack, openInGoogleMaps;
    private ImageView hikeImage, authorProfilePic;
    private SeekBar displayDifficultySeekBar;
    private ArrayList<Post> listOfAllPosts;
    private Post postToDisplay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_post);
        authorUsername = findViewById(R.id.authorUsername);
        authorProfilePic = findViewById(R.id.authorProfilePic);
        displayNameOfHike = findViewById(R.id.displayNameOfHike);
        displayHikeDescription = findViewById(R.id.hikeDescription);
        displayNumber = findViewById(R.id.difficultyDisplay);
        goBack = findViewById(R.id.returnButton);
        openInGoogleMaps = findViewById(R.id.showLocationInGoogleMaps);
        hikeImage = findViewById(R.id.displayHikePicture);
        displayDifficultySeekBar = findViewById(R.id.displayDifficultySeekBar);
        listOfAllPosts = PostListSingleton.getInstance().getPostList();
        Intent intent = getIntent();
        String postUUID = intent.getStringExtra("uuid");

        for (int i = 0; i < listOfAllPosts.size(); i++) {
            if (postUUID.equals(listOfAllPosts.get(i).getUuid()))
                postToDisplay = listOfAllPosts.get(i);
        }

        if (postToDisplay.getNameOfTheHike() != null && postToDisplay.getDescription() != null && postToDisplay.getImageUrl() != null && postToDisplay.getUser().getUsername() != null && postToDisplay.getUser().getProfilePic() != null) {
            displayNameOfHike.setText(postToDisplay.getNameOfTheHike());
            displayHikeDescription.setText(postToDisplay.getDescription());
            displayNumber.setText(String.valueOf(postToDisplay.getDifficulty()));
            displayDifficultySeekBar.setProgress(postToDisplay.getDifficulty());
            displayDifficultySeekBar.setEnabled(false);
            authorUsername.setText(postToDisplay.getUser().getUsername());

            Picasso.get().load(postToDisplay.getImageUrl()).into(hikeImage);
            Picasso.get().load(postToDisplay.getUser().getProfilePic()).into(authorProfilePic);
        }

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
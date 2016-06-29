package com.app.madym.points;

import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        ValueEventListener, View.OnLongClickListener {

    // server config
    private static final String FIREBASE_URL = "https://<your-project>.firebaseio.com/";
    private static final String M = "mady";
    private static final String S = "sandy";

    private Firebase mFirebaseRoot;

    private TextView mScoreText;
    private TextView mMText;
    private TextView mSText;
    private String mMName;
    private String mSName;

    private long mMScore;
    private long mSScore;
    private boolean mAdding = true; // whether score is in add it mode or not

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set up firebase
        Firebase.setAndroidContext(this);
        mFirebaseRoot = new Firebase(FIREBASE_URL);
        mFirebaseRoot.getRoot().addValueEventListener(this /* value event listener */);

        mScoreText = (TextView) findViewById(R.id.score);

        final Resources res = getResources();
        mMName = res.getString(R.string.mady);
        mMText = (TextView) findViewById(R.id.m_points);
        mMText.setOnClickListener(this);
        mMText.setOnLongClickListener(this);

        mSName = res.getString(R.string.sandy);
        mSText = (TextView) findViewById(R.id.s_points);
        mSText.setOnClickListener(this);
        mSText.setOnLongClickListener(this);
    }

    @Override
    public void onDataChange(DataSnapshot snapshot) {
        // Why does firebase default to long?
        mMScore = (long) snapshot.child(M).getValue();
        mSScore = (long) snapshot.child(S).getValue();

        updateViews();
    }
    @Override public void onCancelled(FirebaseError error) { }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch(id) {
            case R.id.m_points:
                // This seems weird
                if (mAdding) {
                    mMScore++;
                } else {
                    mMScore--;
                }
                mFirebaseRoot.child(M).setValue(mMScore);
                break;
            case R.id.s_points:
                if (mAdding) {
                    mSScore++;
                } else {
                    mSScore--;
                }
                mFirebaseRoot.child(S).setValue(mSScore);
                break;
        }
        updateViews();
    }

    private void updateViews() {
        mScoreText.setText(getResources().getString(R.string.versus, mMScore, mSScore));
        if (mAdding) {
            mMText.setText(mMName + "++");
            mSText.setText(mSName + "++");
        } else {
            mMText.setText(mMName + "--");
            mSText.setText(mSName + "--");
        }
    }

    @Override
    public boolean onLongClick(View view) {
        mAdding = !mAdding; // long click toggles modes, not so great
        updateViews();
        return true;
    }
}


package com.example.kathy.minidiary;

import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private boolean mTwoPaneMode = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View detailView = findViewById(R.id.detail_diary_frame);

        // ad view
        AdView mAdView = (AdView) findViewById(R.id.adView);
        // Create an ad request. Check logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        if (detailView == null) {
            // One pane mode
            mTwoPaneMode = false;
        } else {
            // Two Pane Mode
            mTwoPaneMode = true;
        }

        // no matter it is in two or one pane mode, it need to have this main fragment but
        if (savedInstanceState == null) {
            Bundle data = new Bundle();
            data.putBoolean("twoPaneMode", mTwoPaneMode);

            MainFragment mainFragment = new MainFragment();
            mainFragment.setArguments(data);

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.framelayout_main, mainFragment);
            ft.commit();
        }
    }

    public void openDetailFragment(HashMap<String, Object> selectedDiary, Bundle bundle) {

        if (mTwoPaneMode) {
            // so it is two pane mode, we need to start the fragment instead of open a new activity
            Bundle data = new Bundle();

            data.putSerializable("selectedDiary", selectedDiary);

            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(data);

            getSupportFragmentManager().beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.detail_diary_frame, detailFragment)
                    .commit();
        } else {
            // and open activity
            Intent intent = new Intent(this, DetailActivity.class).putExtra("selectedDiary", selectedDiary);
            //startActivity(intent);
            ActivityCompat.startActivity(this, intent, bundle);
        }
    }

    public void destroyFragment()
    {
        getSupportFragmentManager().popBackStack();
    }

    public void openAddDiaryFragment() {

        if (mTwoPaneMode) {

            View detailView = findViewById(R.id.detail_diary_frame);

            Bundle data = new Bundle();

            data.putBoolean("twoPaneMode", mTwoPaneMode);

            AddDiaryFragment addDiaryFragment = new AddDiaryFragment();
            addDiaryFragment.setArguments(data);

            getSupportFragmentManager().beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.detail_diary_frame, addDiaryFragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, AddDiaryActivity.class);
            startActivity(intent);
        }
    }
}

package com.example.kathy.minidiary;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private boolean twoPaneMode = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View detailView = findViewById(R.id.detail_diary_frame);

        if (detailView == null) {
            // One pane mode
            twoPaneMode = false;
        } else {
            // Two Pane Mode
            twoPaneMode = true;
        }

        // no matter it is in two or one pane mode, it need to have this main fragment but
        if (savedInstanceState == null) {
            Bundle data = new Bundle();
            data.putBoolean("twoPaneMode", twoPaneMode);

            MainFragment mainFragment = new MainFragment();
            mainFragment.setArguments(data);

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.framelayout_main, mainFragment);
            ft.commit();
        }
    }

    public void openDetailFragment(HashMap<String, Object> selectedDiary) {

        if (twoPaneMode) {
            // so it is two pane mode, we need to start the fragment instead of open a new activity
            Bundle data = new Bundle();

            data.putSerializable("selectedDiary", selectedDiary);

            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(data);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_diary_frame, detailFragment)
                    .commit();
        } else {
            // and open activity
            Intent intent = new Intent(this, DetailActivity.class).putExtra("selectedDiary", selectedDiary);
            startActivity(intent);
        }
    }

    public void openAddDiaryFragment() {

        if (twoPaneMode) {

            AddDiaryFragment addDiaryFragment = new AddDiaryFragment();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_diary_frame, addDiaryFragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, AddDiaryActivity.class);
            startActivity(intent);
        }

    }
}

package com.example.kathy.minidiary;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private boolean twoPaneMode = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_main);



        if (savedInstanceState == null) {
            MainFragment mainFragment = new MainFragment();

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.framelayout_main, mainFragment);
            ft.commit();
        }

    }

    public void openDetailFragment(HashMap<String, Object> selectedDiary) {
        //String selectedArtistId = (String) selected.get("id");
        if (twoPaneMode) {
            //Intent intent = new Intent(this, MainActivity.class).putExtra(Intent.EXTRA_TEXT, selectedArtistId);

            Bundle data = new Bundle();
            //data.putString("selectedArtistId", selectedArtistId);

            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(data);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.framelayout_detail, detailFragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class).putExtra("selectedDiary", selectedDiary);
            startActivity(intent);
        }

    }

    public void openAddDiaryFragment() {
        //String selectedArtistId = (String) selected.get("id");
        if (twoPaneMode) {
            //Intent intent = new Intent(this, MainActivity.class).putExtra(Intent.EXTRA_TEXT, selectedArtistId);

            /*Bundle data = new Bundle();
            //data.putString("selectedArtistId", selectedArtistId);

            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(data);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.framelayout_detail, detailFragment)
                    .commit();*/
        } else {
            Intent intent = new Intent(this, AddDiaryActivity.class);
            startActivity(intent);
        }

    }
}

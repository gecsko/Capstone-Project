package com.example.kathy.minidiary;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class AddDiaryActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_diary);



       // getSupportActionBar().setHomeButtonEnabled(true);
       // getSupportActionBar().setDisplayShowHomeEnabled(true);
       // getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            AddDiaryFragment addDiaryFragment = new AddDiaryFragment();

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.framelayout_add_diary, addDiaryFragment);
            ft.commit();
        }

    }
}
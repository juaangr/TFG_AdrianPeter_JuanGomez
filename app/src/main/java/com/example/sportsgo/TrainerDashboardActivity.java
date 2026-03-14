package com.example.sportsgo;

import android.os.Bundle;
import android.widget.ListView;

import io.realm.Realm;

public class TrainerDashboardActivity AppCompatActivity {
    private Realm realm;
    private ListView lvPupilos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_dashboard);

    }
}

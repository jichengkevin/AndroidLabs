package com.example.androidlabs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.EditText;

    public class MainActivity extends AppCompatActivity {
        EditText editText;
        SharedPreferences prefs;
        String previous = "FileName";
        MyDatabaseOpenHelper mydb;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_first);
            mydb = new MyDatabaseOpenHelper(this);

            Button firstButton = findViewById(R.id.buttonLogin);
            editText = findViewById(R.id.userInput1);

            prefs = getSharedPreferences(previous, MODE_PRIVATE);
            previous = prefs.getString("email", "");
            editText.setText(previous);

            if(firstButton != null)
                firstButton.setOnClickListener(clk -> {
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);

                    intent.putExtra("email", editText.getText().toString());
                    //startActivity(intent);
                    startActivityForResult(intent, 5);

                });
        }

        @Override
        protected void onPause() {
            super.onPause();

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("email", editText.getText().toString());

            editor.commit();

        }



    }

package com.example.androidlabs;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class TestToolbar extends AppCompatActivity {
    String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_toolbar);
        message = "You clicked on the overflow menu";
        Toolbar tBar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(tBar);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_Choice1:

                Toast.makeText(this, "This is the initial message", Toast.LENGTH_LONG).show();
                return true;

            case R.id.action_Choice2:
                alertExample();
                return true;

            case R.id.action_Choice3:
                Snackbar snackbar = Snackbar.make(this.findViewById(R.id.action_Choice3), "Go Back?", Snackbar.LENGTH_LONG);
                snackbar.setAction(R.string.undo_string, new MyUndoListener());
                snackbar.show();

                return true;

            case R.id.action_Choice4:
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }


    public void alertExample()
    {
        View middle = getLayoutInflater().inflate(R.layout.view_extra_stuff, null);
        EditText et = (EditText)middle.findViewById(R.id.view_edit_text);
        TextView tv = middle.findViewById(R.id.view_show_text);
        //btn.setOnClickListener( clk -> et.setText("You clicked my button!"));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("")
                .setPositiveButton("Positive", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        message = et.getText().toString();

                    }
                })
                .setNegativeButton("Negative", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).setView(middle);

        builder.create().show();
    }

    public class MyUndoListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent i = new Intent(TestToolbar.this, ProfileActivity.class);
            startActivity(i);
            finish();
            // Code to undo the user's last action
        }
    }
}

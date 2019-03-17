package com.vatsal.android.digitaldetox.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vatsal.android.digitaldetox.R;
import com.vatsal.android.digitaldetox.database.DataBaseHelper;

import java.util.Objects;

/**
 * This activity let the user set a time limit for an app
 * displays the app package name, the icon and the current limit, if a limit is already set;
 * lets the user delete the limit
 */
public class OptionsActivity extends Activity{

    String pkg=null;

    DataBaseHelper DataBase;

    public void setPkg(String pkg) {
        this.pkg = pkg;
    }

    public OptionsActivity() {
        this.DataBase=new DataBaseHelper(this);
    }


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        setPkg(pkg);

        Intent mainIntent = getIntent();
        //get package name from NotificationActivity
        final String pkg = Objects.requireNonNull(mainIntent.getExtras()).getString("package options");//key of pair
        //get usage limit from database
        Long limit = DataBase.getLimit(pkg);

        final EditText editText1 = findViewById(R.id.edit_limit_minutes);
        final EditText editText2 = findViewById(R.id.edit_limit_hours);
        final Button btSet = findViewById(R.id.button_set);


        /*application icon*/
        Drawable appIcon;
        ImageView appIconView= findViewById(R.id.options_icon);
        PackageManager packageManager= getApplicationContext().getPackageManager();
        try {
            appIcon = packageManager.getApplicationIcon(pkg);
            appIconView.setImageDrawable(appIcon);
        } catch (PackageManager.NameNotFoundException e) { //set custom apk icon
            e.printStackTrace();
            appIconView.setImageResource(R.drawable.ic_launcher);
        }

        final TextView textViewName = findViewById(R.id.options_name);
        textViewName.setText(pkg);

        final TextView textViewCurrent = findViewById(R.id.options_current);


         /*delete button*/
        final Button btDelete= findViewById(R.id.button_delete);
        btDelete.setOnClickListener(v -> {
            DataBase.deleteLimit(pkg);
            Toast toast=Toast.makeText(getApplication(),"Limit deleted",Toast.LENGTH_SHORT);
            toast.show();
            btDelete.setVisibility(View.INVISIBLE);
            textViewCurrent.setText("Limit Deleted");
        });


        if( limit != -1 ){//limit already exists
            textViewCurrent.setText("Current daily limit: " + limit + " minutes");



            /*set button updates the existing limit*/
            btSet.setOnClickListener(v -> {
                final String newLimitString1 = String.valueOf(editText1.getText());
                final String newLimitString2 = String.valueOf(editText2.getText());

                if(newLimitString1.equals("") && newLimitString2.equals(""))
                {
                    btSet.setEnabled(false);
                    Toast.makeText(OptionsActivity.this, "Please Dont Leave Both Fields Empty",
                            Toast.LENGTH_LONG).show();

                }
                else if(newLimitString1.equals("")){
                    final Long newlimit1 = 0L;
                    final Long newlimit2 = Long.parseLong(newLimitString2);
                    final Long newlimit = newlimit1 + (newlimit2 * 60);

                    DataBase.updateLimit(pkg, newlimit);
                    textViewCurrent.setText("Current daily limit: " + newlimit.toString() + " minutes");
                    Toast toast = Toast.makeText(getApplication(), "Limit updated", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else if(newLimitString2.equals("")){
                    final Long newlimit1 = Long.parseLong(newLimitString1);
                    final Long newlimit2 = 0L;
                    final Long newlimit = newlimit1 + (newlimit2 * 60);

                    DataBase.updateLimit(pkg, newlimit);
                    textViewCurrent.setText("Current daily limit: " + newlimit.toString() + " minutes");
                    Toast toast = Toast.makeText(getApplication(), "Limit updated", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else{
                    final Long newlimit1 = Long.parseLong(newLimitString1);
                    final Long newlimit2 = Long.parseLong(newLimitString2);
                    final Long newlimit = newlimit1 + (newlimit2 * 60);

                    DataBase.updateLimit(pkg, newlimit);
                    textViewCurrent.setText("Current daily limit: " + newlimit.toString() + " minutes");
                    Toast toast = Toast.makeText(getApplication(), "Limit updated", Toast.LENGTH_SHORT);
                    toast.show();
                }
            });


            btDelete.setVisibility(View.VISIBLE);


        }
        else{/*limit does not exist, equals -1*/
            /*set button adds a new limit in db*/
            btSet.setOnClickListener(v -> {
                final String newLimitString1 = String.valueOf(editText1.getText());
                final String newLimitString2 = String.valueOf(editText2.getText());


                if(newLimitString1.equals("") && newLimitString2.equals(""))
                {
                    btSet.setEnabled(false);
                    Toast.makeText(OptionsActivity.this, "Please Dont Leave Both Fields Empty",
                            Toast.LENGTH_LONG).show();

                }
                else if(newLimitString1.equals("")){
                    final Long newlimit1 = 0L;
                    final Long newlimit2 = Long.parseLong(newLimitString2);
                    final Long newlimit = newlimit1 + (newlimit2 * 60);

                    DataBase.updateLimit(pkg, newlimit);
                    textViewCurrent.setText("Current daily limit: " + newlimit.toString() + " minutes");

                    btDelete.setVisibility(View.VISIBLE);

                    DataBase.addLimit(pkg, newlimit);
                    Toast toast=Toast.makeText(getApplication(),"Limit set",Toast.LENGTH_SHORT);
                    toast.show();

                }
                else if(newLimitString2.equals("")){
                    final Long newlimit1 = Long.parseLong(newLimitString1);
                    final Long newlimit2 = 0L;
                    final Long newlimit = newlimit1 + (newlimit2 * 60);

                    DataBase.updateLimit(pkg, newlimit);
                    textViewCurrent.setText("Current daily limit: " + newlimit.toString() + " minutes");

                    btDelete.setVisibility(View.VISIBLE);

                    DataBase.addLimit(pkg, newlimit);
                    Toast toast=Toast.makeText(getApplication(),"Limit set",Toast.LENGTH_SHORT);
                    toast.show();
                }
                else{
                    final Long newlimit1 = Long.parseLong(newLimitString1);
                    final Long newlimit2 = Long.parseLong(newLimitString2);
                    final Long newlimit = newlimit1 + (newlimit2 * 60);

                    DataBase.updateLimit(pkg, newlimit);
                    textViewCurrent.setText("Current daily limit: " + newlimit.toString() + " minutes");

                    btDelete.setVisibility(View.VISIBLE);

                    DataBase.addLimit(pkg, newlimit);
                    Toast toast=Toast.makeText(getApplication(),"Limit set",Toast.LENGTH_SHORT);
                    toast.show();
                }

            });
        }


    }
}

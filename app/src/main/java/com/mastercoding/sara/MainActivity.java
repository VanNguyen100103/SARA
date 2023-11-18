package com.mastercoding.sara;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.audiofx.DynamicsProcessing;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Config;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    Button button,translate;
    TextView textView,input,chooseLanguage;
    FirebaseUser user;
    ImageView speak;
    RadioGroup radioGroup;
    RadioButton  radioButtonEnglish, radioButtonKorea, radioButtonChinese;
    private final int numberRequest = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        button = findViewById(R.id.logout);
        textView = findViewById(R.id.user_details);
        user = auth.getCurrentUser();
        input = findViewById(R.id.input);
        speak = findViewById(R.id.speak);
        chooseLanguage = findViewById(R.id.chooseLanguage);
        radioGroup = findViewById(R.id.radioGroup);
        radioButtonEnglish = findViewById(R.id.radioButtonEnglish);
        radioButtonKorea = findViewById(R.id.radioButtonKorea);
        radioButtonChinese = findViewById(R.id.radioButtonChinese);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int i) {
                switch(i){
                    case R.id.radioButtonEnglish:
                        String language = "en";
                        setLocale(language);
                        break;
                    case R.id.radioButtonKorea:
                        setLocale("ko");
                        break;
                    case R.id.radioButtonChinese:
                        setLocale("zh");
                        break;
                }
            }
        });
        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Your need to speak");
                try{
                    startActivityForResult(intent,numberRequest);
                }catch(ActivityNotFoundException e){
                    Toast.makeText(MainActivity.this,"Sorry, your device not supported", Toast.LENGTH_LONG).show();
                }
            }
        });
        if(user == null){
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }else{
            textView.setText(user.getEmail());
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setLocale(String language) {
        Resources resources = getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = new Locale(language);
        resources.updateConfiguration(configuration,metrics);
        onConfigurationChanged(configuration);
    }
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        input.setText(R.string.content);
        chooseLanguage.setText(R.string.chooseLanguage);
        radioButtonEnglish.setText(R.string.english);
        radioButtonKorea.setText(R.string.korea);
        radioButtonChinese.setText(R.string.china);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        switch (requestCode){
            case numberRequest:
                if(resultCode == RESULT_OK && null != data){
                    ArrayList result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    input.setText((String)result.get(0));
                }
                break;
        }
    }

}
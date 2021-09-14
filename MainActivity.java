package com.example.linkstonewsbyword;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    //Define your views
    private EditText userInput;
    public static int isYnetButtonChecked = 1;
    public static int isN12ButtonChecked = 1;
    private CheckBox ynetCheckBox;
    private CheckBox n12CheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_page);

        userInput = findViewById(R.id.editTextWordToSearch);
        Button button = findViewById(R.id.searchButton);
        ynetCheckBox = findViewById(R.id.ynetButton);
        n12CheckBox = findViewById(R.id.N12Button);

        @SuppressLint("SetTextI18n") View.OnClickListener searchOnClickListener = v -> {
            String wordToSearch = userInput.getText().toString();
            Intent intent = new Intent(this, ArticlesActivity.class);
            intent.putExtra("WORD_TO_SEARCH", wordToSearch);
            startActivity(intent);
        };

        button.setOnClickListener(searchOnClickListener);

        View.OnClickListener ynetCheckBoxListener = v -> {
            if(MainActivity.isYnetButtonChecked == 0)
                MainActivity.isYnetButtonChecked = 1;
            else
                MainActivity.isYnetButtonChecked = 0;
        };

        ynetCheckBox.setOnClickListener(ynetCheckBoxListener);

        View.OnClickListener n12CheckBoxListener = v -> {
            if(MainActivity.isN12ButtonChecked == 0)
                MainActivity.isN12ButtonChecked = 1;
            else
                MainActivity.isN12ButtonChecked = 0;
        };

        n12CheckBox.setOnClickListener(n12CheckBoxListener);
    }

    @Override
    protected void onResume() {
        super.onResume();

        ynetCheckBox.setChecked(MainActivity.isYnetButtonChecked == 1);
        n12CheckBox.setChecked(MainActivity.isN12ButtonChecked == 1);
    }
}

package com.example.miwok;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
{  private TextView numbers;
   private  TextView familyMembers;
   private TextView colors;
   private TextView phrases;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        numbers=(TextView)findViewById(R.id.numbers);
        familyMembers=(TextView)findViewById(R.id.familyMembers);
        colors=(TextView)findViewById(R.id.colors);
        phrases=(TextView)findViewById(R.id.phrases);

        numbers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent numbersIntent=new Intent(MainActivity.this,NumbersActivity.class);
                startActivity(numbersIntent);
            }
        });
        familyMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent familyIntent=new Intent(MainActivity.this,FamilyMembersActivity.class);
                startActivity(familyIntent);
            }
        });
        colors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent colorsIntent=new Intent(MainActivity.this,ColorsActivity.class);
                startActivity(colorsIntent);
            }
        });
        phrases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pharasesIntent=new Intent(MainActivity.this,PharasesActivity.class);
                startActivity(pharasesIntent);
            }
        });
    }


}
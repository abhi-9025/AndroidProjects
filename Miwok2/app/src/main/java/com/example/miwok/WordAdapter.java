package com.example.miwok;

import android.content.Context;
import android.telecom.TelecomManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

public class WordAdapter extends ArrayAdapter<Word> {
    private int mColorResourceId;
    public WordAdapter(Context context, ArrayList pWords,int ColorResourceId) {
        super(context,0, pWords);
        mColorResourceId=ColorResourceId;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
         View listItemView=convertView;
         if(listItemView==null)
         {
             listItemView= LayoutInflater.from(getContext()).inflate(R.layout.list_item,parent,false);

         }
        Word my_word=(Word)getItem(position);
        TextView miwokTextView=(TextView)listItemView.findViewById(R.id.miwok_text_view);
        miwokTextView.setText(my_word.MiwokTranslation());
        TextView defaultTextView=(TextView)listItemView.findViewById(R.id.default_text_view);
        defaultTextView.setText(my_word.DefaultTranslation());

            ImageView imageView = (ImageView) listItemView.findViewById(R.id.image_view);
        if(my_word.hasImage()==true) {
            imageView.setImageResource(my_word.getImageResourceId());
            imageView.setVisibility(View.VISIBLE);
        }
        else
        {
            imageView.setVisibility(View.GONE);
        }
        View textContainer=listItemView.findViewById(R.id.text_container);
        int color= ContextCompat.getColor(getContext(),mColorResourceId);
        textContainer.setBackgroundColor(color);

        return listItemView;
    }
}


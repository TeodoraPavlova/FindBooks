package com.example.android.findbooks;


import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class BooksAdapter extends ArrayAdapter<Books> {

    public BooksAdapter(Context context, List<Books> books){
        super(context,0,books);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView==null){
            listItemView = LayoutInflater.from(getContext())
                    .inflate(R.layout.book_list_item,parent,false);
        }

        Books currentBook = getItem(position);
        TextView title = (TextView) listItemView.findViewById(R.id.book_title);
        title.setText(currentBook.getTitle());

        TextView author = (TextView) listItemView.findViewById(R.id.book_author);
        author.setText(currentBook.getAuthor());

        ImageView image = (ImageView) listItemView.findViewById(R.id.image);
        image.setImageBitmap(currentBook.getImage());

        return listItemView;
    }
}

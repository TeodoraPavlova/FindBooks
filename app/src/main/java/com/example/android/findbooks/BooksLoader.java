package com.example.android.findbooks;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class BooksLoader extends AsyncTaskLoader<List<Books>> {

    private String mUrl;

    public BooksLoader(Context context,String url){
        super(context);
        mUrl=url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Books> loadInBackground() {
        if(mUrl==null) {
            return null;
        }else{
            return QueryUtils.fetchBooksData(mUrl);
        }
    }
}

package com.example.android.findbooks;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Books>>{

    private BooksAdapter mAdapter;
    private final int BOOK_LOADER_ID = 1;
    private TextView ifDataDoesntLoad;
    private ProgressBar mProgress;
    private static final String REQUEST_URL =
    "https://www.googleapis.com/books/v1/volumes?q=";
    SearchView searchView;
    public String query="";
    public String search="";
    public boolean trySearch=false;
    private int firstIndex = 0;
    private int lastIndex = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final List<Books> books = new ArrayList<Books>();
        mAdapter = new BooksAdapter(this,books);
        ListView booksList = (ListView) findViewById(R.id.list);

        booksList.setAdapter(mAdapter);
        booksList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Books currentBook = mAdapter.getItem(i);
                Uri bookUri = Uri.parse(currentBook.getUri());
                Intent bookIntent = new Intent(Intent.ACTION_VIEW, bookUri);
                startActivity(bookIntent);            }
        });

        if(checkConnection()){
            query = REQUEST_URL + "programming&orderBy=newest&maxResults=20";
            getLoaderManager().initLoader(BOOK_LOADER_ID,null,this);
        }

        searchView = (SearchView) findViewById(R.id.search_id);
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(checkConnection()) {
                    mProgress = (ProgressBar) findViewById(R.id.loading_indicator);
                    trySearch=true;
                    mProgress.setVisibility(View.VISIBLE);
                    String userInput = searchView.getQuery().toString();
                    search= REQUEST_URL + userInput.trim()
                            +"&orderBy=newest&maxResults=20&startIndex"+firstIndex
                            +"&endIndex"+lastIndex;
                    getLoaderManager().restartLoader(BOOK_LOADER_ID,null,MainActivity.this);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }

    public boolean checkConnection(){
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo!=null && networkInfo.isConnected());
    }

    @Override
    public Loader<List<Books>> onCreateLoader(int i, Bundle bundle) {
        if(trySearch){
            return new BooksLoader(this,search);
        }
        return new BooksLoader(this,query);
    }

    @Override
    public void onLoadFinished(Loader<List<Books>> loader, List<Books> books) {
        mProgress = (ProgressBar) findViewById(R.id.loading_indicator);
        mProgress.setVisibility(View.GONE);
        ifDataDoesntLoad = (TextView) findViewById(R.id.empty_view);
        mAdapter.clear();
        if (books != null && !books.isEmpty()) {
            mAdapter.addAll(books);
        }else{
            ifDataDoesntLoad.setText("No books found");
            ifDataDoesntLoad.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Books>> loader) {
        mAdapter.clear();
    }
}

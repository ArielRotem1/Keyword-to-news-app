package com.example.linkstonewsbyword;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class ArticlesActivity extends AppCompatActivity {

    private CustomAdapter itemsAdapter;

    private static String wordToSearch;
    private static int counterArticles = 0, counterSites = 0, sitesCount = 0;
    private TextView searchResult;
    private TextView empty_articles_text;
    private ImageView emptyImage;
    private List<Article> articles;

    private static final String SERVER = "http://10.0.2.2:3000/getData";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.articles_show);

        Intent intent = getIntent();
        String tempWordToSearch = intent.getStringExtra("WORD_TO_SEARCH");
        if(tempWordToSearch != null){
            wordToSearch = tempWordToSearch;
        }

        searchResult = findViewById(R.id.search_result);
        emptyImage = findViewById(R.id.empty_image);
        empty_articles_text  = findViewById(R.id.empty_articles_text);


        ListView listView = findViewById(R.id.simple_list_item_1);
        itemsAdapter = new CustomAdapter(this, R.layout.row, new ArrayList<>());
        listView.setAdapter(itemsAdapter);

        counterArticles = 0;
        counterSites = 0;
        articles = new ArrayList<>();


        emptyImage.setVisibility(View.GONE);
        empty_articles_text.setVisibility(View.GONE);

        sitesCount = MainActivity.isN12ButtonChecked + MainActivity.isYnetButtonChecked;

        if(sitesCount == 0){
            emptyImage.setVisibility(View.VISIBLE);
            empty_articles_text.setVisibility(View.VISIBLE);
            searchResult.setText("You didn't mark a site to search from.");
        }

        getLinks();
    }

    public void getLinks() {

        if(MainActivity.isYnetButtonChecked == 1) {
            getArticles("Ynet", new ArticlesActivity.VolleyResponseListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onError(String message) {
                    searchResult.setText("something wrong " + message);
                }

                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(JSONArray response) throws JSONException {
                    HashSet<String> hs = new HashSet<>();
                    searchResult.setText("success in getting the response!");
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject article = (JSONObject) response.get(i);
                        String link = article.getString("link");
                        String title = article.getString("title");
                        String date = article.getString("date");
                        String cleanedTitle = getCleanTitle(title);
                        if (hasWordInTitle(cleanedTitle, wordToSearch) && !hs.contains(cleanedTitle)) {
                            hs.add(cleanedTitle);
                            articles.add(new Article(title, link, "ynet_icon", date));
                            counterArticles++;
                        }
                    }

                    counterSites++;

                    if(counterSites == sitesCount){

                        //sort articles by date
                        Collections.sort(articles, (art1, art2) -> {
                            String date1 = art1.getDate(), date2 = art2.getDate();
                            String[] date1Parts = date1.split("\\."), date2Parts = date2.split("\\.");
                            Date dateObj1 = new Date(Integer.parseInt("20" + date1Parts[2]), Integer.parseInt(date1Parts[1]), Integer.parseInt(date1Parts[0]));
                            Date dateObj2 = new Date(Integer.parseInt("20" + date2Parts[2]), Integer.parseInt(date2Parts[1]), Integer.parseInt(date2Parts[0]));
                            long day1 = dateObj1.getTime();
                            long day2 = dateObj2.getTime();

                            return Long.compare(day2, day1);
                        });

                        for(Article art : articles){
                            itemsAdapter.add(art);
                        }

                        searchResult.setText(counterArticles + " articles found for keyword: " + wordToSearch);

                        if(counterArticles == 0){
                            emptyImage.setVisibility(View.VISIBLE);
                            empty_articles_text.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
        }
        if(MainActivity.isN12ButtonChecked == 1) {
            getArticles("N12", new ArticlesActivity.VolleyResponseListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onError(String message) {
                    searchResult.setText("something wrong " + message);
                }

                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(JSONArray response) throws JSONException {
                    HashSet<String> hs = new HashSet<>();
                    searchResult.setText("success in getting the response!");
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject article = (JSONObject) response.get(i);
                        String link = article.getString("link");
                        String title = article.getString("title");
                        String date = article.getString("date");
                        String cleanedTitle = getCleanTitle(title);
                        if (hasWordInTitle(cleanedTitle, wordToSearch) && !hs.contains(cleanedTitle)) {
                            hs.add(cleanedTitle);
                            articles.add(new Article(title, link, "n12_icon", date));
                            counterArticles++;
                        }

                    }

                    counterSites++;

                    if(counterSites == sitesCount){

                        //sort articles by date
                        Collections.sort(articles, (art1, art2) -> {
                            String date1 = art1.getDate(), date2 = art2.getDate();
                            String[] date1Parts = date1.split("\\."), date2Parts = date2.split("\\.");
                            Date dateObj1 = new Date(Integer.parseInt("20" + date1Parts[2]), Integer.parseInt(date1Parts[1]), Integer.parseInt(date1Parts[0]));
                            Date dateObj2 = new Date(Integer.parseInt("20" + date2Parts[2]), Integer.parseInt(date2Parts[1]), Integer.parseInt(date2Parts[0]));
                            long day1 = dateObj1.getTime();
                            long day2 = dateObj2.getTime();

                            return Long.compare(day2, day1);
                        });

                        for(Article art : articles){
                            itemsAdapter.add(art);
                        }

                        searchResult.setText(counterArticles + " articles found for keyword: " + wordToSearch);

                        if(counterArticles == 0){
                            emptyImage.setVisibility(View.VISIBLE);
                            empty_articles_text.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
        }
    }

    public interface  VolleyResponseListener{
        void onError(String message);

        void onResponse(JSONArray response) throws JSONException;
    }

    public void getArticles(String siteName, VolleyResponseListener volleyResponseListener) {

        RequestQueue queue = MySingleton.getInstance(ArticlesActivity.this).getRequestQueue();
        JsonArrayRequest pageRequest = new JsonArrayRequest(
                Request.Method.GET,
                ArticlesActivity.SERVER + siteName,
                null,
                response -> {
                    try {
                        volleyResponseListener.onResponse(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> volleyResponseListener.onError("That didn't work! " + error)
        );
        MySingleton.getInstance(ArticlesActivity.this).addToRequestQueue(pageRequest);
    }


    private String getCleanTitle(String title){
        char[] charsToIgnore = new char[]{':', '.', '?', '!', ',', ';', '"'};

        StringBuilder cleanedTitle = new StringBuilder();
        char[] charsTitle = title.toCharArray();

        for(char c: charsTitle){
            boolean isCharToIgnore = false;
            for(char cToIgnore : charsToIgnore){
                if(cToIgnore == c){
                    isCharToIgnore = true;
                    break;
                }
            }

            if(!isCharToIgnore) cleanedTitle.append(c);
        }

        return cleanedTitle.toString();
    }

    private boolean hasWordInTitle(String title, String word){


        if(word.length() >= 3){
            return title.contains(word);
        }
        else{
            String[] wordsInTitle = title.split(" ");
            for(String wordInTitle : wordsInTitle){
                if(wordInTitle.equals(word)) return true;
            }
        }

        return false;
    }
}
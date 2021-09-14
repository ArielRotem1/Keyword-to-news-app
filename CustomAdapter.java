package com.example.linkstonewsbyword;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<Article>{

    private final Context context;
    private final List<Article> articles;
    private final LayoutInflater inflater;

    public CustomAdapter(Context context, int textViewResourceID, List<Article> arts) {

        super(context, textViewResourceID, arts);

        this.context = context;
        articles = arts;
        inflater = (LayoutInflater.from(context));
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.row, null);

        //adding animation to the row
        Animation translate_anim = AnimationUtils.loadAnimation(context, R.anim.translate_anim);
        view.setAnimation(translate_anim);

        //getting the article
        Article art = getItem(i);

        //adding the title to the text view
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(art.getName());

        //adding the icon of the site
        ImageView iconSite = view.findViewById(R.id.imageView);
        String iconName = art.getIcon();
        if(iconName.equals("ynet_icon")) {
            iconSite.setImageResource(R.drawable.ynet_icon);
        }
        else if(iconName.equals("n12_icon")){
            iconSite.setImageResource(R.drawable.n12_icon);
        }


        //adding the link to the text view
        View.OnClickListener viewClickListener = v -> {
            String Url = art.getLink();
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setData(Uri.parse(Url));
//            context.startActivity(intent);
            Intent intent = new Intent(context, ArticleWebPage.class);
            intent.putExtra("URL", Url);
            context.startActivity(intent);
        };


        view.setOnClickListener(viewClickListener);

        //adding the date to the text view
        TextView date = (TextView) view.findViewById(R.id.date);
        date.setText(art.getDate());

        return view;
    }
}

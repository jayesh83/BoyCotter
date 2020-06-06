package com.japps.boycotter.util;

import android.content.Context;
import android.widget.ImageView;

import com.japps.boycotter.R;
import com.squareup.picasso.Picasso;

public class PicassoClient {
    public static void downloadImage(String url, ImageView imageView){
        if (url != null && url.length() > 0){
            Picasso.get().load(url)
                    .placeholder(R.drawable.ic_apps_24px)
                    .centerCrop()
                    .resize(90, 90)
                    .transform(new CircleTransformation(20, 0))
                    .into(imageView);
        }else
            Picasso.get().load(R.drawable.ic_apps_24px).into(imageView);
    }
}

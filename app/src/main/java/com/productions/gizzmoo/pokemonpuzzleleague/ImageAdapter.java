package com.productions.gizzmoo.pokemonpuzzleleague;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/**
 * Created by Chrystian on 4/9/2018.
 */

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private Bitmap[] mBitmaps;
    private int mChosenPosition;

    public ImageAdapter(Context c, Bitmap[] bitmaps) {
        mContext = c;
        mBitmaps = bitmaps;
    }

    public int getCount() {
        return mBitmaps.length;
    }

    public Object getItem(int position) {
        return mBitmaps[position];
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        int padding = 16;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(padding, padding, padding, padding);
        } else {
            imageView = (ImageView) convertView;
        }

        int background;
        if (position == mChosenPosition) {
            background = Color.WHITE;
        } else {
            background = mContext.getResources().getColor(R.color.grey);
        }

        imageView.setBackgroundColor(background);

        imageView.setImageBitmap(mBitmaps[position]);
        return imageView;
    }

    public void positionChosen(int position) {
        mChosenPosition = position;
    }

}

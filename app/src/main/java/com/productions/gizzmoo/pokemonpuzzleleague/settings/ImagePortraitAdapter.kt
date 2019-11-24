package com.productions.gizzmoo.pokemonpuzzleleague.settings

import android.content.Context
import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.productions.gizzmoo.pokemonpuzzleleague.R

class ImagePortraitAdapter(private val context: Context, private val bitmaps: Array<Bitmap>) : BaseAdapter() {
    var chosenPosition: Int = 0

    override fun getCount(): Int = bitmaps.size

    override fun getItem(position: Int): Any = bitmaps[position]

    override fun getItemId(position: Int): Long = 0

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val imageView: ImageView
        val padding = 16
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = ImageView(context)
            imageView.adjustViewBounds = true
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            imageView.setPadding(padding, padding, padding, padding)
        } else {
            imageView = convertView as ImageView
        }

        val background = if (position == chosenPosition) context.resources.getColor(R.color.secondary) else context.resources.getColor(R.color.primary_dark)
        imageView.setBackgroundColor(background)
        imageView.setImageBitmap(bitmaps[position])
        return imageView
    }
}
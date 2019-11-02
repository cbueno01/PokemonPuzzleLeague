package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.productions.gizzmoo.pokemonpuzzleleague.R;

import java.util.HashMap;

/**
 * Created by Chrystian on 5/17/2018.
 */

class BoardResources {

    /**********************************
     The order of the list matter!
     All block resources must be the same size!
     ***********************************/

    private static int[] mNormalResources = {-1, R.drawable.leaf_normal,
                                          R.drawable.fire_normal, R.drawable.heart_normal,
                                          R.drawable.water_normal, R.drawable.coin_normal,
                                          R.drawable.metallic_normal, R.drawable.diamond_normal};

    private static int[] mDarkResources = {-1, R.drawable.leaf_dark,
                                        R.drawable.fire_dark, R.drawable.heart_dark,
                                        R.drawable.water_dark, R.drawable.coin_dark,
                                        R.drawable.metallic_dark, R.drawable.diamond_dark};

    private static int[] mInvertedResources = {-1, R.drawable.leaf_inverted,
                                            R.drawable.fire_inverted, R.drawable.heart_inverted,
                                            R.drawable.water_inverted, R.drawable.coin_inverted,
                                            R.drawable.metallic_inverted, R.drawable.diamond_inverted};

    private static int[] mPopAnimation1Resources = {-1, R.drawable.leaf_pop_animation_1,
                                                 R.drawable.fire_pop_animation_1, R.drawable.heart_pop_animation_1,
                                                 R.drawable.water_pop_animation_1, R.drawable.coin_pop_animation_1,
                                                 R.drawable.metallic_pop_animation_1, R.drawable.diamond_pop_animation_1};

    private static int[] mPopAnimation2Resources = {-1, R.drawable.leaf_pop_animation_2,
                                                 R.drawable.fire_pop_animation_2, R.drawable.heart_pop_animation_2,
                                                 R.drawable.water_pop_animation_2, R.drawable.coin_pop_animation_2,
                                                 R.drawable.metallic_pop_animation_2, R.drawable.diamond_pop_animation_2};

    private static int[] mPopAnimation4Resources = {-1, R.drawable.leaf_pop_animation_4,
                                                 R.drawable.fire_pop_animation_4, R.drawable.heart_pop_animation_4,
                                                 R.drawable.water_pop_animation_4, R.drawable.coin_pop_animation_4,
                                                 R.drawable.metallic_pop_animation_4, R.drawable.diamond_pop_animation_4};

    private static int[] mPopAnimation5Resources = {-1, R.drawable.leaf_pop_animation_5,
                                                 R.drawable.fire_pop_animation_5, R.drawable.heart_pop_animation_5,
                                                 R.drawable.water_pop_animation_5, R.drawable.coin_pop_animation_5,
                                                 R.drawable.metallic_pop_animation_5, R.drawable.diamond_pop_animation_5};

    private static int[][] mMatchAnimationArr = {mPopAnimation1Resources, mPopAnimation2Resources, mPopAnimation4Resources, mPopAnimation5Resources};
    private static String[] matchAnimationKeys = {"PopAnimation1", "PopAnimation2", "PopAnimation4", "PopAnimation5"};

    private static Bitmap mBlankBitmap;
    private static Bitmap[] mNormalBitmap = new Bitmap[mNormalResources.length];
    private static Bitmap[] mDarkBitmap = new Bitmap[mDarkResources.length];
    private static Bitmap[] mInvertedBitmap = new Bitmap[mInvertedResources.length];
    private static HashMap<String, Bitmap[]> mAnimationBitmap = new HashMap<>();

    static Bitmap getNormalBlock(BlockType type) {
        int typeID = type.ordinal();
        return (typeID < mNormalBitmap.length && typeID > 0) ? mNormalBitmap[typeID] : null;
    }

    static Bitmap getDarkBlock(BlockType type) {
        int typeID = type.ordinal();
        return (typeID < mDarkBitmap.length && typeID > 0) ? mDarkBitmap[typeID] : null;
    }

    static Bitmap getInvertedBlock(BlockType type) {
        int typeID = type.ordinal();
        return (typeID < mInvertedBitmap.length && typeID > 0) ? mInvertedBitmap[typeID] : null;
    }

    static Bitmap getPopAnimationBlock(BlockType type, int animationSection) {
        String key = null;
        switch (animationSection) {
            case 0:
            case 2:
                key = matchAnimationKeys[0];
                break;
            case 1:
                key = matchAnimationKeys[1];
                break;
            case 3:
                key = matchAnimationKeys[2];
                break;
            case 4:
                key = matchAnimationKeys[3];
                break;
            default:
                break;
        }

        if (key == null) {
            return mBlankBitmap;
        }

        Bitmap[] bitmapArr;
        if (!mAnimationBitmap.containsKey(key)) {
            return null;
        } else {
            bitmapArr = mAnimationBitmap.get(key);
        }

        int typeID = type.ordinal();
        return (typeID < bitmapArr.length && typeID > 0) ? bitmapArr[typeID] : null;
    }


    static void createImageBitmaps(Context context) {
        for (int i = 1; i < mNormalBitmap.length; i++) {
            mNormalBitmap[i] = BitmapFactory.decodeResource(context.getResources(), mNormalResources[i]);
            mDarkBitmap[i] = BitmapFactory.decodeResource(context.getResources(), mDarkResources[i]);
            mInvertedBitmap[i] = BitmapFactory.decodeResource(context.getResources(), mInvertedResources[i]);
        }

        for (int i = 0; i < matchAnimationKeys.length; i++) {
            Bitmap[] currentArr = new Bitmap[8];
            for (int j = 1; j < currentArr.length; j++) {
                currentArr[j] =  BitmapFactory.decodeResource(context.getResources(), mMatchAnimationArr[i][j]);
            }

            mAnimationBitmap.put(matchAnimationKeys[i], currentArr);
        }

        mBlankBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.blank_block);
    }

    static int getBlockHeights() {
        return mNormalBitmap[1] != null ? mNormalBitmap[1].getWidth() : 0;
    }

}

package com.productions.gizzmoo.pokemonpuzzleleague;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Locale;

/**
 * Created by Chrystian on 6/4/2018.
 */

public class SeekBarPreference extends DialogPreference {

    private static final int DEFAULT_VALUE = 10;
    private int mValue = DEFAULT_VALUE;
    private Context mContext;

    private SeekBar mBar;
    private TextView mTextView;

    public SeekBarPreference(Context context) {
        this(context, null, 0);
    }

    public SeekBarPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SeekBarPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;
        setDialogLayoutResource(R.layout.seek_bar_preference);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);
    }

    @Override
    protected void onBindDialogView(View view) {
        mBar = view.findViewById(R.id.seekBar);
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{
                ContextCompat.getColor(mContext, R.color.blue_speed_select),
                ContextCompat.getColor(mContext, R.color.yellow_speed_select),
                ContextCompat.getColor(mContext, R.color.red_speed_select)});
        mBar.setBackground(gradientDrawable);
        mBar.setProgress(mValue);
        mBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress <= 0) {
                    progress = 1;
                }

                if (fromUser) {
                    mTextView.setText(String.format(Locale.US, "%d", progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        mTextView = view.findViewById(R.id.textView);
        mTextView.setText(String.format(Locale.US, "%d", mValue));


        super.onBindDialogView(view);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            mValue = getPersistedInt(DEFAULT_VALUE);
            if (mValue < 0) {
                mValue = 1;
            }
        } else {
            persistInt(mValue);
        }
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            mValue = mBar.getProgress();
            if (mValue <= 0) {
                mValue = 1;
            }
            persistInt(mValue);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        if (isPersistent()) {
            return superState;
        }

        final SavedState myState = new SavedState(superState);
        myState.value = mValue;
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state == null || !state.getClass().equals(SavedState.class)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());

        mValue = myState.value;
    }

}

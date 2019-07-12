package com.productions.gizzmoo.pokemonpuzzleleague;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

/**
 * Created by Chrystian on 4/8/2018.
 */

public class TrainerPreference extends DialogPreference {
    private static final int DEFAULT_ID = 0; // Ash
    private int mTrainerID = DEFAULT_ID;

    private GridView mGridView;
    private Context mContext;
    private ImageAdapter mAdapter;

    private Bitmap[] mBitmaps;
    private String[] mTrainerNames;

    public TrainerPreference(Context context) {
        this(context, null, 0);
    }

    public TrainerPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TrainerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;
        setDialogLayoutResource(R.layout.trainer_preference);
        init();
    }


    @Override
    protected void onBindDialogView(View view) {
        mGridView = view.findViewById(R.id.grid);
        mAdapter = new ImageAdapter(mContext, mBitmaps);
        mAdapter.positionChosen(mTrainerID);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                persistInt(position);
                mTrainerID = position;
                Toast.makeText(mContext, "" + mTrainerNames[position], Toast.LENGTH_SHORT).show();
                getDialog().dismiss();
            }
        });

        super.onBindDialogView(view);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            mTrainerID = getPersistedInt(DEFAULT_ID);
        } else {
            persistInt(mTrainerID);
        }
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            persistInt(mTrainerID);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        if (isPersistent()) {
            return superState;
        }

        final SavedState myState = new SavedState(superState);
        myState.value = mTrainerID;
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

        mTrainerID = myState.value;
    }

    private void init() {
        int[] imageResources = TrainerResources.Companion.getAllTrainerPortraits();
        mBitmaps = new Bitmap[imageResources.length];

        for (int i = 0; i < imageResources.length; i++) {
            mBitmaps[i] = BitmapFactory.decodeResource(mContext.getResources(), imageResources[i]);
        }

        mTrainerNames = TrainerResources.Companion.getTrainerNames(mContext);
    }
}

package com.productions.gizzmoo.pokemonpuzzleleague;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

/**
 * Created by Chrystian on 4/8/2018.
 */

public class TrainerPreference extends DialogPreference {
    private static final int DEFAULT_VALUE = 0;
    private int mTrainer = DEFAULT_VALUE;
    private int mChangedTrainer;

    private GridView mGridView;
    private Button mAcceptButton;
    private Button mCancelButton;

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
        mAdapter.positionChosen(mTrainer);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                mChangedTrainer = position;
                mAdapter.positionChosen(position);
                mAdapter.notifyDataSetChanged();
                Toast.makeText(mContext, "" + mTrainerNames[position], Toast.LENGTH_SHORT).show();
            }
        });

        mAcceptButton = view.findViewById(R.id.accept_button);
        mAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                persistInt(mChangedTrainer);
                mTrainer = mChangedTrainer;
                getDialog().dismiss();
            }
        });

        mCancelButton = view.findViewById(R.id.cancel_button);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        super.onBindDialogView(view);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            mTrainer = getPersistedInt(DEFAULT_VALUE);
        } else {
            persistInt(mTrainer);
        }
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            persistInt(mChangedTrainer);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        if (isPersistent()) {
            return superState;
        }

        final SavedState myState = new SavedState(superState);
        myState.value = mTrainer;
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

        mTrainer = myState.value;
    }

    private void init() {
        int[] imageResources = TrainerResources.getAllTrainerPortraits();
        mBitmaps = new Bitmap[imageResources.length];

        for (int i = 0; i < imageResources.length; i++) {
            mBitmaps[i] = BitmapFactory.decodeResource(mContext.getResources(), imageResources[i]);
        }

        mTrainerNames = mContext.getResources().getStringArray(R.array.trainers);
    }
}

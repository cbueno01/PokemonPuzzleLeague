package com.productions.gizzmoo.pokemonpuzzleleague;

import android.content.Context;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

/**
 * Created by Chrystian on 6/2/2018.
 */

public class LineNumberPreference extends DialogPreference {

    private static final int DEFAULT_VALUE = 15;
    private EditText mEditText;
    private int mValue = DEFAULT_VALUE;

    public LineNumberPreference(Context context) {
        this(context, null, 0);
    }

    public LineNumberPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineNumberPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setDialogLayoutResource(R.layout.line_number_preference);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);
    }

    @Override
    protected void onBindDialogView(View view) {
        mEditText = view.findViewById(R.id.number_edit_view);
        mEditText.setText(Integer.toString(mValue));
        mEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        super.onBindDialogView(view);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            mValue = getPersistedInt(DEFAULT_VALUE);
        } else {
            persistInt(mValue);
        }
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            mValue = stringToInt(mEditText.getText().toString());
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

    private int stringToInt(String str) {
        int value = DEFAULT_VALUE;

        try {
            value = Integer.parseInt(str);
        } catch (Exception e){}

        return value;
    }
}

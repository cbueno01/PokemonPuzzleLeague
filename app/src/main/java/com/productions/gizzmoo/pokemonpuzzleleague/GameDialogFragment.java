package com.productions.gizzmoo.pokemonpuzzleleague;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class GameDialogFragment extends DialogFragment {

    private static final String ARG_PARAM1 = "param1";
    private boolean mDidWin;
    private OnGameEndingDialogFragmentReturnListener mListener;

    public GameDialogFragment() {}

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        if (mDidWin) {
            builder.setMessage(R.string.win);
        } else {
            builder.setMessage(R.string.lose);
        }

        builder.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (mListener != null) {
                    mListener.onGameEndingDialogResponse();
                }
            }
        });
        return builder.create();
    }

    public static GameDialogFragment newInstance(boolean didWin) {
        GameDialogFragment fragment = new GameDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_PARAM1, didWin);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDidWin = getArguments().getBoolean(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_ending_dialog, container, false);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnGameEndingDialogFragmentReturnListener) {
            mListener = (OnGameEndingDialogFragmentReturnListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnGameEndingDialogFragmentReturnListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener.onGameEndingDialogResponse();
        mListener = null;
    }


    public interface OnGameEndingDialogFragmentReturnListener {
        void onGameEndingDialogResponse();
    }
}

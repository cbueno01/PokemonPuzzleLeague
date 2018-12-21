package com.productions.gizzmoo.pokemonpuzzleleague;

import android.content.SharedPreferences;
import android.graphics.Point;
import android.media.SoundPool;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static android.media.AudioManager.STREAM_MUSIC;

public abstract class GameFragment <T extends GameLoop> extends Fragment implements BoardListener {


    protected PuzzleBoardView mBoardView;
    protected T mGameLoop;

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//    }

    protected abstract T createGameLoop();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mainView = inflater.inflate(R.layout.game_fragment_layout, container, false);

        mBoardView = mainView.findViewById(R.id.puzzleBoard);

        return mainView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
//        int currentTrainer = settings.getInt("pref_trainer_key", 0);

    }

    @Override
    public void onStart() {
        super.onStart();

        mBoardView.setBoardListener(this);
        mGameLoop = createGameLoop();
        mGameLoop.startGame();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mGameLoop != null) {
            mGameLoop.cancel(true);
        }
    }

    // Board Listener Methods
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void switchBlock(Point switcherLeftBlock) {
        mGameLoop.swapBlocks(switcherLeftBlock.x, switcherLeftBlock.y, switcherLeftBlock.x + 1, switcherLeftBlock.y);

//        if (mLoadedSoundPool && mSwitchSoundID != 0) {
//            mSoundPool.play(mSwitchSoundID, 1, 1, SWITCH_SOUND_PRIORITY, 0, 1);
//        }
    }

//    @Override
//    public void addNewRow() {
//                if (mGameLoop != null && mBlockMatchAnimating == 0) {
//                    mGameLoop.addNewRow();
//                }
//    }

//    @Override
//    public void blockFinishedMatchAnimation(int row, int column) {
//                int rowToUpdate = row - 1;
//                while (rowToUpdate >= 0 && !mGrid[rowToUpdate][column].isBlockEmpty()) {
//                    if (!mGrid[rowToUpdate][column].hasMatched && !mGrid[rowToUpdate][column].isAnimatingDown && !mGrid[rowToUpdate][column].isBeingSwitched) {
//                        mGrid[rowToUpdate][column].canCombo = true;
//                    }
//                    rowToUpdate--;
//                }
//
//                mBlockMatchAnimating--;
//
//                if (mBlockMatchAnimating == 0) {
//                    mBoardView.startAnimatingUp();
//                }
//    }

    @Override
    public void needsBlockSwap(Block b1, Block b2) {
        Point b1Point = b1.getCoords();
        Point b2Point = b2.getCoords();
        mGameLoop.swapBlocks(b1Point.x, b1Point.y, b2Point.x, b2Point.y);
    }

    @Override
    public void switchBlockMoved() {
//        if (mLoadedSoundPool && mMoveSoundID != 0) {
//            mSoundPool.play(mMoveSoundID, 1, 1, MOVE_SOUND_PRIORITY, 0, 1);
//        }
    }

    @Override
    public void blockIsPopping(int position, int total) {
//        int soundID = getPopSoundID(position, total);
//        if (mLoadedSoundPool && soundID != 0) {
//            mSoundPool.play(soundID, 1, 1, POP_SOUND_PRIORITY, 0, 1);
//        }
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////

//    private int getPopSoundID(int pos, int total) {
//        int index = (int)(((float)pos / total) * 4);
//        return mPopSoundIDs[index];
//    }
}

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

public abstract class GameFragment <T extends GameLoop> extends Fragment implements BoardListener, GameLoop.GameLoopListener {

    private int POKEMON_SOUND_PRIORITY = 4;
    private int TRAINER_SOUND_PRIORITY = 3;
    private int POP_SOUND_PRIORITY = 2;
    private int SWITCH_SOUND_PRIORITY = 1;
    private int MOVE_SOUND_PRIORITY = 0;

    protected PuzzleBoardView mBoardView;
    protected T mGameLoop;

    private SoundPool mSoundPool;
    private boolean mLoadedSoundPool;
    private int mSwitchSoundID;
    private int mTrainerSoundID;
    private int mMoveSoundID;
    private int[] mPopSoundIDs = new int[4];
    private int[] mPokemonSoundIDs = new int[4];

    private final int[] popSoundResources = {R.raw.pop_sound_1, R.raw.pop_sound_2, R.raw.pop_sound_3, R.raw.pop_sound_4};
    private final int[] pokemonSoundResources = {R.raw.pikachu_sound_1, R.raw.pikachu_sound_2, R.raw.pikachu_sound_3, R.raw.pikachu_sound_4};


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoadedSoundPool = false;
    }

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
        setGameSound();

    }

    @Override
    public void onStart() {
        super.onStart();

        mGameLoop = createGameLoop();
        mBoardView.setBoardListener(this);
        mGameLoop.setGameLoopListener(this);
        mBoardView.setGrid(mGameLoop.getGameGrid(), mGameLoop.getBlockSwitcher());

        mGameLoop.startGame();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mGameLoop != null) {
            mGameLoop.cancel(true);
        }
    }

    @Override
    public void switchBlock(Point switcherLeftBlock) {
        mGameLoop.swapBlocks(switcherLeftBlock.x, switcherLeftBlock.y, switcherLeftBlock.x + 1, switcherLeftBlock.y);

        if (mLoadedSoundPool && mSwitchSoundID != 0) {
            mSoundPool.play(mSwitchSoundID, 1, 1, SWITCH_SOUND_PRIORITY, 0, 1);
        }
    }

//    @Override
//    public void addNewRow() {
//                if (mGameLoop != null && mBlockMatchAnimating == 0) {
//                    mGameLoop.addNewRow();
//                }
//    }

    @Override
    public void blockFinishedMatchAnimation(int row, int column) {
        mGameLoop.blockFinishedMatchAnimation(row, column);

//
//                mBlockMatchAnimating--;
//
//                if (mBlockMatchAnimating == 0) {
//                    mBoardView.startAnimatingUp();
//                }
    }

    @Override
    public void needsBlockSwap(Block b1, Block b2) {
        Point b1Point = b1.getCoords();
        Point b2Point = b2.getCoords();
        mGameLoop.swapBlocks(b1Point.x, b1Point.y, b2Point.x, b2Point.y);
    }

    @Override
    public void switchBlockMoved() {
        if (mLoadedSoundPool && mMoveSoundID != 0) {
            mSoundPool.play(mMoveSoundID, 1, 1, MOVE_SOUND_PRIORITY, 0, 1);
        }
    }

    @Override
    public void blockIsPopping(int position, int total) {
        int soundID = getPopSoundID(position, total);
        if (mLoadedSoundPool && soundID != 0) {
            mSoundPool.play(soundID, 1, 1, POP_SOUND_PRIORITY, 0, 1);
        }
    }

    @Override
    public void playPokemonSound(int comboNumber) {
        int pokemonSoundID = getPokemonSoundID(comboNumber);
        if (mLoadedSoundPool && pokemonSoundID != 0) {
            mSoundPool.play(pokemonSoundID, 1, 1, POKEMON_SOUND_PRIORITY, 0, 1);
        }
    }

    @Override
    public void playTrainerSound(boolean isMetallic) {
        if (mLoadedSoundPool && mTrainerSoundID != 0) {
            mSoundPool.play(mTrainerSoundID, 1, 1, TRAINER_SOUND_PRIORITY, 0, 1);
        }
    }

    @Override
    public void updateBoardView() {
        mBoardView.invalidate();
    }
    private void setGameSound() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        int trainerID = settings.getInt("pref_trainer_key", 0);


        mSoundPool = new SoundPool(2, STREAM_MUSIC, 0);
        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                mLoadedSoundPool = true;
            }
        });

        mTrainerSoundID = mSoundPool.load(getContext(), TrainerResources.getTrainerComboSound(trainerID), 1);
        mSwitchSoundID = mSoundPool.load(getContext(), R.raw.switch_sound, 1);
        mMoveSoundID = mSoundPool.load(getContext(), R.raw.move_sound, 1);

        for (int i = 0; i < pokemonSoundResources.length; i++) {
            mPokemonSoundIDs[i] = mSoundPool.load(getContext(), pokemonSoundResources[i], 1);
        }

        for (int i = 0; i < popSoundResources.length; i++) {
            mPopSoundIDs[i] = mSoundPool.load(getContext(), popSoundResources[i], 1);
        }
    }

    private int getPopSoundID(int pos, int total) {
        int index = (int)(((float)pos / total) * 4);
        return mPopSoundIDs[index];
    }

    private int getPokemonSoundID(int comboCount) {
        if (comboCount <= 0) {
            return 0;
        } else if (comboCount <= 2) {
            return mPokemonSoundIDs[0];
        } else if (comboCount == 3) {
            return mPokemonSoundIDs[1];
        } else if (comboCount == 4) {
            return mPokemonSoundIDs[2];
        } else {
            return mPokemonSoundIDs[3];
        }
    }
}

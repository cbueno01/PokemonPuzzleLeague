package com.productions.gizzmoo.pokemonpuzzleleague;

/**
 * Created by Chrystian on 5/17/2018.
 */

class TrainerResources {

    /**********************************
      The order of the list matter!
    ***********************************/

    private static final int[][] mTrainerSongs = {{R.raw.ash_normal, R.raw.ash_panic},
                                                  {R.raw.blaine_normal, R.raw.blaine_panic},
                                                  {R.raw.brock_normal, R.raw.brock_panic},
                                                  {R.raw.bruno_normal, R.raw.bruno_panic},
                                                  {R.raw.erika_normal, R.raw.erika_panic},
                                                  {R.raw.gary_normal, R.raw.gary_panic},
                                                  {R.raw.giovanni_normal, R.raw.giovanni_panic},
                                                  {R.raw.koga_normal, R.raw.koga_panic},
                                                  {R.raw.lorelei_normal, R.raw.lorelei_panic},
                                                  {R.raw.lt_surge_normal, R.raw.lt_surge_panic},
                                                  {R.raw.mewtwo_normal, R.raw.mewtwo_panic},
                                                  {R.raw.misty_normal, R.raw.misty_panic},
                                                  {R.raw.ritchie_normal, R.raw.ritchie_panic},
                                                  {R.raw.sabrina_normal, R.raw.sabrina_panic},
                                                  {R.raw.team_rocket_normal, R.raw.team_rocket_panic},
                                                  {R.raw.tracey_normal, R.raw.tracey_panic}};

    private static final int[] mTrainerCombos = {R.raw.ash_combo,  R.raw.blaine_combo,
                                                 R.raw.brock_combo, R.raw.bruno_combo_fake,
                                                 R.raw.erika_combo,  R.raw.gary_combo,
                                                 R.raw.giovanni_combo,  R.raw.koga_combo,
                                                 R.raw.lorelei_combo, R.raw.lt_surge_combo,
                                                 R.raw.mewtwo_combo, R.raw.misty_combo,
                                                 R.raw.ritchie_combo, R.raw.sabrina_combo,
                                                 R.raw.team_rocket_combo, R.raw.tracey_combo};

    private static final int[] mPortraitResources = {R.drawable.ash_portrait, R.drawable.blaine_portrait, R.drawable.brock_portrait, R.drawable.bruno_portrait,
                                                     R.drawable.erika_portrait, R.drawable.gary_portrait, R.drawable.giovanni_portrait, R.drawable.koga_portrait,
                                                     R.drawable.lorelei_portrait, R.drawable.lt_surge_portrait, R.drawable.mewtwo_portrait, R.drawable.misty_portrait,
                                                     R.drawable.ritchie_portrait, R.drawable.sabrina_portrait, R.drawable.team_rocket_portrait, R.drawable.tracey_portrait};

    private static final int[] mFullBodyResources = {R.drawable.ash_full_body, R.drawable.blaine_full_body, R.drawable.brock_full_body, R.drawable.bruno_full_body,
                                                     R.drawable.erika_full_body, R.drawable.gary_full_body, R.drawable.giovanni_full_body, R.drawable.koga_full_body,
                                                     R.drawable.lorelei_full_body, R.drawable.lt_surge_full_body, -1, R.drawable.misty_full_body,
                                                     R.drawable.ritchie_full_body, R.drawable.sabrina_full_body, R.drawable.team_rocket_full_body, R.drawable.tracey_full_body};

    static int[] getTrainerSong(int trainerID) {
        return (trainerID < mTrainerSongs.length) ? mTrainerSongs[trainerID] : mTrainerSongs[0];
    }

    static int getTrainerComboSound(int trainerID) {
        return (trainerID < mTrainerCombos.length) ?  mTrainerCombos[trainerID] : mTrainerCombos[0];
    }

    static int getTrainerPortrait(int trainerID) {
        return (trainerID < mPortraitResources.length) ? mPortraitResources[trainerID] : mPortraitResources[0];
    }

    static int[] getAllTrainerPortraits() {
        return  mPortraitResources;
    }

    static int getTrainerFullBody(int trainerID) {
        return (trainerID < mFullBodyResources.length) ? mFullBodyResources[trainerID] : mFullBodyResources[0];
    }
}

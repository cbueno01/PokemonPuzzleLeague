package com.productions.gizzmoo.pokemonpuzzleleague

import android.content.Context

object TrainerResources {
    private val mTrainerSongs: HashMap<Trainer, IntArray> = hashMapOf(
            Trainer.ASH to intArrayOf(R.raw.ash_normal, R.raw.ash_panic),
            Trainer.BLAINE to intArrayOf(R.raw.blaine_normal, R.raw.blaine_panic),
            Trainer.BROCK to intArrayOf(R.raw.brock_normal, R.raw.brock_panic),
            Trainer.BRUNO to intArrayOf(R.raw.bruno_normal, R.raw.bruno_panic),
            Trainer.ERIKA to intArrayOf(R.raw.erika_normal, R.raw.erika_panic),
            Trainer.GARY to intArrayOf(R.raw.gary_normal, R.raw.gary_panic),
            Trainer.GIOVANNI to intArrayOf(R.raw.giovanni_normal, R.raw.giovanni_panic),
            Trainer.KOGA to intArrayOf(R.raw.koga_normal, R.raw.koga_panic),
            Trainer.LORELEI to intArrayOf(R.raw.lorelei_normal, R.raw.lorelei_panic),
            Trainer.LT_SURGE to intArrayOf(R.raw.lt_surge_normal, R.raw.lt_surge_panic),
            Trainer.MEWTWO to intArrayOf(R.raw.mewtwo_normal, R.raw.mewtwo_panic),
            Trainer.MISTY to intArrayOf(R.raw.misty_normal, R.raw.misty_panic),
            Trainer.RITCHIE to intArrayOf(R.raw.ritchie_normal, R.raw.ritchie_panic),
            Trainer.SABRINA to intArrayOf(R.raw.sabrina_normal, R.raw.sabrina_panic),
            Trainer.TEAM_ROCKET to intArrayOf(R.raw.team_rocket_normal, R.raw.team_rocket_panic),
            Trainer.TRACY to intArrayOf(R.raw.tracey_normal, R.raw.tracey_panic))

    private val mTrainerCombos: HashMap<Trainer, Int> = hashMapOf(
            Trainer.ASH to R.raw.ash_combo,
            Trainer.BLAINE to R.raw.blaine_combo,
            Trainer.BROCK to R.raw.brock_combo,
            Trainer.BRUNO to R.raw.bruno_combo_fake,
            Trainer.ERIKA to R.raw.erika_combo,
            Trainer.GARY to R.raw.gary_combo,
            Trainer.GIOVANNI to R.raw.giovanni_combo,
            Trainer.KOGA to R.raw.koga_combo,
            Trainer.LORELEI to R.raw.lorelei_combo,
            Trainer.LT_SURGE to R.raw.lt_surge_combo,
            Trainer.MEWTWO to R.raw.mewtwo_combo,
            Trainer.MISTY to R.raw.misty_combo,
            Trainer.RITCHIE to R.raw.ritchie_combo,
            Trainer.SABRINA to R.raw.sabrina_combo,
            Trainer.TEAM_ROCKET to R.raw.team_rocket_combo,
            Trainer.TRACY to R.raw.tracey_combo
    )

    private val mTrainerPortraits: HashMap<Trainer, Int> = hashMapOf(
            Trainer.ASH to R.drawable.ash_portrait,
            Trainer.BLAINE to R.drawable.blaine_portrait,
            Trainer.BROCK to R.drawable.brock_portrait,
            Trainer.BRUNO to R.drawable.bruno_portrait,
            Trainer.ERIKA to R.drawable.erika_portrait,
            Trainer.GARY to R.drawable.gary_portrait,
            Trainer.GIOVANNI to R.drawable.giovanni_portrait,
            Trainer.KOGA to R.drawable.koga_portrait,
            Trainer.LORELEI to R.drawable.lorelei_portrait,
            Trainer.LT_SURGE to R.drawable.lt_surge_portrait,
            Trainer.MEWTWO to R.drawable.mewtwo_portrait,
            Trainer.MISTY to R.drawable.misty_portrait,
            Trainer.RITCHIE to R.drawable.ritchie_portrait,
            Trainer.SABRINA to R.drawable.sabrina_portrait,
            Trainer.TEAM_ROCKET to R.drawable.team_rocket_portrait,
            Trainer.TRACY to R.drawable.tracey_portrait
    )

    private val mTrainerFullBody : HashMap<Trainer, Int> = hashMapOf(
            Trainer.ASH to R.drawable.ash_full_body,
            Trainer.BLAINE to R.drawable.blaine_full_body,
            Trainer.BROCK to R.drawable.brock_full_body,
            Trainer.BRUNO to R.drawable.bruno_full_body,
            Trainer.ERIKA to R.drawable.erika_full_body,
            Trainer.GARY to R.drawable.gary_full_body,
            Trainer.GIOVANNI to R.drawable.giovanni_full_body,
            Trainer.KOGA to R.drawable.koga_full_body,
            Trainer.LORELEI to R.drawable.lorelei_full_body,
            Trainer.LT_SURGE to R.drawable.lt_surge_full_body,
            Trainer.MEWTWO to R.drawable.mewtwo_full_body,
            Trainer.MISTY to R.drawable.misty_full_body,
            Trainer.RITCHIE to R.drawable.ritchie_full_body,
            Trainer.SABRINA to R.drawable.sabrina_full_body,
            Trainer.TEAM_ROCKET to R.drawable.team_rocket_full_body,
            Trainer.TRACY to R.drawable.tracey_full_body
    )

    private val mTrainerNames : HashMap<Trainer, Int> = hashMapOf(
            Trainer.ASH to R.string.ash,
            Trainer.BLAINE to R.string.blaine,
            Trainer.BROCK to R.string.brock,
            Trainer.BRUNO to R.string.bruno,
            Trainer.ERIKA to R.string.erika,
            Trainer.GARY to R.string.gary,
            Trainer.GIOVANNI to R.string.giovanni,
            Trainer.KOGA to R.string.koga,
            Trainer.LORELEI to R.string.lorelei,
            Trainer.LT_SURGE to R.string.lt_surge,
            Trainer.MEWTWO to R.string.mewtwo,
            Trainer.MISTY to R.string.misty,
            Trainer.RITCHIE to R.string.ritchie,
            Trainer.SABRINA to R.string.sabrina,
            Trainer.TEAM_ROCKET to R.string.team_rocket,
            Trainer.TRACY to R.string.tracey
    )

    fun getTrainerSong(trainer: Trainer) : IntArray =
        mTrainerSongs[trainer].let { it } ?: mTrainerSongs[Trainer.ASH]!!

    fun getTrainerComboSound(trainer: Trainer) : Int =
        mTrainerCombos[trainer].let { it } ?: mTrainerCombos[Trainer.ASH]!!

    fun getTrainerPortrait(trainer: Trainer) : Int =
        mTrainerPortraits[trainer].let { it } ?: mTrainerPortraits[Trainer.ASH]!!

    fun getAllTrainerPortraits() : IntArray =
        mTrainerPortraits.toSortedMap().values.toIntArray()

    fun getTrainerFullBody(trainer: Trainer, isGaryEvolved: Boolean) : Int {
        if (trainer == Trainer.GARY && isGaryEvolved) {
            return R.drawable.gary_evolved_full_body
        }

        return mTrainerFullBody[trainer].let { it } ?: mTrainerFullBody[Trainer.ASH]!!
    }

    fun getTrainerName(trainer : Trainer, context: Context) : String =
        context.resources.getString(mTrainerNames[trainer].let { it } ?: mTrainerNames[Trainer.ASH]!!)

    fun getTrainerNames(context : Context) : Array<String> {
        val resourceArray = mTrainerNames.toSortedMap().values.toIntArray()
        return Array(resourceArray.size) {
            i -> context.resources.getString(resourceArray[i])
        }
    }
}
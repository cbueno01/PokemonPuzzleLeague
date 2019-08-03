package com.productions.gizzmoo.pokemonpuzzleleague

import android.content.Context

class PokemonResources {
    companion object {
        private val mPortraitResources = hashMapOf(
                Pokemon.ABRA to R.drawable.abra_portrait, Pokemon.ALAKAZAM to R.drawable.alakazam_portrait, Pokemon.ARBOK to R.drawable.arbok_portrait, Pokemon.ARCANINE to R.drawable.arcanine_portrait,
                Pokemon.BULBASAUR to R.drawable.bulbasaur_portrait, Pokemon.BULBASAUR_CLONE to R.drawable.bulbasaur_clone_portrait, Pokemon.CHARMELEON to R.drawable.charmeleon_portrait, Pokemon.CLOYSTER to R.drawable.cloyster_portrait,
                Pokemon.DEWGONG to R.drawable.dewgong_portrait, Pokemon.GEODUDE to R.drawable.geodude_portrait, Pokemon.GLOOM to R.drawable.gloom_portrait, Pokemon.GOLBAT to R.drawable.golbat_portrait,
                Pokemon.GROWLITHE to R.drawable.growlithe_portrait, Pokemon.HAPPY to R.drawable.happy_portrait, Pokemon.HITMONCHAN to R.drawable.hitmonchan_portrait, Pokemon.HORSEA to R.drawable.horsea_portrait,
                Pokemon.HYPNO to R.drawable.hypno_portrait, Pokemon.JOLTEON to R.drawable.jolteon_portrait, Pokemon.KINGLER to R.drawable.kingler_portrait, Pokemon.KRABBY to R.drawable.krabby_portrait,
                Pokemon.MAGMAR to R.drawable.magmar_portrait, Pokemon.MAGNETON to R.drawable.magneton_portrait, Pokemon.MARILL to R.drawable.marill_portrait, Pokemon.NIDOKING to R.drawable.nidoking_portrait,
                Pokemon.NIDOQUEEN to R.drawable.nidoqueen_portrait, Pokemon.NIDORAN to R.drawable.nidoran_portrait, Pokemon.ONIX to R.drawable.onix_portrait, Pokemon.PERSIAN to R.drawable.persian_portrait,
                Pokemon.PIKACHU to R.drawable.pikachu_portrait, Pokemon.PIKACHU_CLONE to R.drawable.pikachu_clone_portrait, Pokemon.POLIWHIRL to R.drawable.poliwhirl_portrait, Pokemon.PRIMEAPE to R.drawable.primeape_portrait,
                Pokemon.PSYDUCK to R.drawable.psyduck_portrait, Pokemon.RAICHU to R.drawable.raichu_portrait, Pokemon.SANDSLASH to R.drawable.sandslash_portrait, Pokemon.SCYTHER to R.drawable.scyther_portrait,
                Pokemon.SPARKY to R.drawable.sparky_portrait, Pokemon.SQUIRTLE to R.drawable.squirtle_portrait, Pokemon.SQUIRTLE_CLONE to R.drawable.squirtle_clone_portrait, Pokemon.STARYU to R.drawable.staryu_portrait,
                Pokemon.TANGELA to R.drawable.tangela_portrait, Pokemon.VENOMOTH to R.drawable.venomoth_portrait, Pokemon.VENONAT to R.drawable.venonat_portrait, Pokemon.VOLTORB to R.drawable.voltorb_portrait,
                Pokemon.VULPIX to R.drawable.vulpix_portrait, Pokemon.WEEPINBELL to R.drawable.weepinbell_portrait, Pokemon.WEEZING to R.drawable.weezing_portrait, Pokemon.ZIPPO to R.drawable.zippo_portrait,
                Pokemon.ZUBAT to R.drawable.zubat_portrait)

        private val mPokemonNames = hashMapOf(
                Pokemon.ABRA to R.string.abra,
                Pokemon.ALAKAZAM to R.string.alakazam,
                Pokemon.ARBOK to R.string.arbok,
                Pokemon.ARCANINE to R.string.arcanine,
                Pokemon.BULBASAUR to R.string.bulbasaur,
                Pokemon.BULBASAUR_CLONE to R.string.bulbasaur_clone,
                Pokemon.CHARMELEON to R.string.charmeleon,
                Pokemon.CLOYSTER to R.string.cloyster,
                Pokemon.DEWGONG to R.string.dewgong,
                Pokemon.GEODUDE to R.string.geodude,
                Pokemon.GLOOM to R.string.gloom,
                Pokemon.GOLBAT to R.string.golbat,
                Pokemon.GROWLITHE to R.string.growlithe,
                Pokemon.HAPPY to R.string.happy,
                Pokemon.HITMONCHAN to R.string.hitmonchan,
                Pokemon.HORSEA to R.string.horsea,
                Pokemon.HYPNO to R.string.hypno,
                Pokemon.JOLTEON to R.string.jolteon,
                Pokemon.KINGLER to R.string.kingler,
                Pokemon.KRABBY to R.string.krabby,
                Pokemon.MAGMAR to R.string.magmar,
                Pokemon.MAGNETON to R.string.magneton,
                Pokemon.MARILL to R.string.marill,
                Pokemon.NIDOKING to R.string.nidoking,
                Pokemon.NIDOQUEEN to R.string.nidoqueen,
                Pokemon.NIDORAN to R.string.nidoran,
                Pokemon.ONIX to R.string.onix,
                Pokemon.PERSIAN to R.string.persian,
                Pokemon.PIKACHU to R.string.pikachu,
                Pokemon.PIKACHU_CLONE to R.string.pikachu_clone,
                Pokemon.POLIWHIRL to R.string.poliwhirl,
                Pokemon.PRIMEAPE to R.string.primeape,
                Pokemon.PSYDUCK to R.string.psyduck,
                Pokemon.RAICHU to R.string.raichu,
                Pokemon.SANDSLASH to R.string.sandslash,
                Pokemon.SCYTHER to R.string.scyther,
                Pokemon.SPARKY to R.string.sparky,
                Pokemon.SQUIRTLE to R.string.squirtle,
                Pokemon.SQUIRTLE_CLONE to R.string.squirtle_clone,
                Pokemon.STARYU to R.string.staryu,
                Pokemon.TANGELA to R.string.tangela,
                Pokemon.VENOMOTH to R.string.venomoth,
                Pokemon.VENONAT to R.string.venonat,
                Pokemon.VOLTORB to R.string.voltorb,
                Pokemon.VULPIX to R.string.vulpix,
                Pokemon.WEEPINBELL to R.string.weepinbell,
                Pokemon.WEEZING to R.string.weezing,
                Pokemon.ZIPPO to R.string.zippo,
                Pokemon.ZUBAT to R.string.zubat)

        private val mTrainerToPokemonMap = hashMapOf(
                Trainer.ASH to arrayOf(Pokemon.PIKACHU, Pokemon.SQUIRTLE, Pokemon.BULBASAUR),
                Trainer.BLAINE to arrayOf(Pokemon.ARCANINE, Pokemon.CHARMELEON, Pokemon.MAGMAR),
                Trainer.BROCK to arrayOf(Pokemon.GEODUDE, Pokemon.VULPIX, Pokemon.ZUBAT),
                Trainer.BRUNO to arrayOf(Pokemon.ONIX, Pokemon.HITMONCHAN, Pokemon.PRIMEAPE),
                Trainer.ERIKA to arrayOf(Pokemon.TANGELA, Pokemon.WEEPINBELL, Pokemon.GLOOM),
                Trainer.GARY to arrayOf(Pokemon.NIDORAN, Pokemon.GROWLITHE, Pokemon.KRABBY, Pokemon.NIDOQUEEN, Pokemon.ARCANINE, Pokemon.KINGLER),
                Trainer.GIOVANNI to arrayOf(Pokemon.PERSIAN, Pokemon.SANDSLASH, Pokemon.NIDOKING),
                Trainer.KOGA to arrayOf(Pokemon.VENOMOTH, Pokemon.VOLTORB, Pokemon.GOLBAT),
                Trainer.LORELEI to arrayOf(Pokemon.CLOYSTER, Pokemon.POLIWHIRL, Pokemon.DEWGONG),
                Trainer.LT_SURGE to arrayOf(Pokemon.RAICHU, Pokemon.JOLTEON, Pokemon.MAGNETON),
                Trainer.MEWTWO to arrayOf(Pokemon.PIKACHU_CLONE, Pokemon.SQUIRTLE_CLONE, Pokemon.BULBASAUR_CLONE),
                Trainer.MISTY to arrayOf(Pokemon.HORSEA, Pokemon.PSYDUCK, Pokemon.STARYU),
                Trainer.RITCHIE to arrayOf(Pokemon.SPARKY, Pokemon.ZIPPO, Pokemon.HAPPY),
                Trainer.SABRINA to arrayOf(Pokemon.ABRA, Pokemon.HYPNO, Pokemon.ALAKAZAM),
                Trainer.TEAM_ROCKET to arrayOf(Pokemon.WEEZING, Pokemon.ARBOK, Pokemon.GOLBAT),
                Trainer.TRACY to arrayOf(Pokemon.MARILL, Pokemon.VENONAT, Pokemon.SCYTHER)
        )

        fun getPokemonForTrainer(trainer: Trainer): Array<Pokemon> {
            return if (mTrainerToPokemonMap.containsKey(trainer)) mTrainerToPokemonMap[trainer]!! else mTrainerToPokemonMap[Trainer.ASH]!!
        }

        fun getPokemonPortrait(pokemon: Pokemon): Int {
            return if (mPortraitResources.containsKey(pokemon)) mPortraitResources[pokemon]!! else mPortraitResources[Pokemon.PIKACHU]!!
        }

        fun getPokemonName(pokemon: Pokemon, context: Context): String {
            return if (mPokemonNames.containsKey(pokemon)) context.resources.getString(mPokemonNames[pokemon]!!) else context.resources.getString(mPokemonNames[Pokemon.PIKACHU]!!)


        }

    }
}
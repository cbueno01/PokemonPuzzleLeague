package com.productions.gizzmoo.pokemonpuzzleleague

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory

object PokemonResources {
    private val portraitResources = hashMapOf(
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

    private val portraitResourceBitmaps: HashMap<Pokemon, Bitmap> = HashMap()

    private val pokemonNames = hashMapOf(
            Pokemon.ABRA to R.string.abra, Pokemon.ALAKAZAM to R.string.alakazam, Pokemon.ARBOK to R.string.arbok, Pokemon.ARCANINE to R.string.arcanine,
            Pokemon.BULBASAUR to R.string.bulbasaur, Pokemon.BULBASAUR_CLONE to R.string.bulbasaur_clone, Pokemon.CHARMELEON to R.string.charmeleon, Pokemon.CLOYSTER to R.string.cloyster,
            Pokemon.DEWGONG to R.string.dewgong, Pokemon.GEODUDE to R.string.geodude, Pokemon.GLOOM to R.string.gloom, Pokemon.GOLBAT to R.string.golbat,
            Pokemon.GROWLITHE to R.string.growlithe, Pokemon.HAPPY to R.string.happy, Pokemon.HITMONCHAN to R.string.hitmonchan, Pokemon.HORSEA to R.string.horsea,
            Pokemon.HYPNO to R.string.hypno, Pokemon.JOLTEON to R.string.jolteon, Pokemon.KINGLER to R.string.kingler, Pokemon.KRABBY to R.string.krabby,
            Pokemon.MAGMAR to R.string.magmar, Pokemon.MAGNETON to R.string.magneton, Pokemon.MARILL to R.string.marill, Pokemon.NIDOKING to R.string.nidoking,
            Pokemon.NIDOQUEEN to R.string.nidoqueen, Pokemon.NIDORAN to R.string.nidoran, Pokemon.ONIX to R.string.onix, Pokemon.PERSIAN to R.string.persian,
            Pokemon.PIKACHU to R.string.pikachu, Pokemon.PIKACHU_CLONE to R.string.pikachu_clone, Pokemon.POLIWHIRL to R.string.poliwhirl, Pokemon.PRIMEAPE to R.string.primeape,
            Pokemon.PSYDUCK to R.string.psyduck, Pokemon.RAICHU to R.string.raichu, Pokemon.SANDSLASH to R.string.sandslash, Pokemon.SCYTHER to R.string.scyther,
            Pokemon.SPARKY to R.string.sparky, Pokemon.SQUIRTLE to R.string.squirtle, Pokemon.SQUIRTLE_CLONE to R.string.squirtle_clone, Pokemon.STARYU to R.string.staryu,
            Pokemon.TANGELA to R.string.tangela, Pokemon.VENOMOTH to R.string.venomoth, Pokemon.VENONAT to R.string.venonat, Pokemon.VOLTORB to R.string.voltorb,
            Pokemon.VULPIX to R.string.vulpix, Pokemon.WEEPINBELL to R.string.weepinbell, Pokemon.WEEZING to R.string.weezing, Pokemon.ZIPPO to R.string.zippo,
            Pokemon.ZUBAT to R.string.zubat)

    private val trainerToPokemonMap = hashMapOf(
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

    private val pokemonComboResources = hashMapOf(
            Pokemon.ABRA to arrayOf(R.raw.abra_1, R.raw.abra_2, R.raw.abra_3, R.raw.abra_4),
            Pokemon.ALAKAZAM to arrayOf(R.raw.alakazam_1, R.raw.alakazam_2, R.raw.alakazam_3, R.raw.alakazam_4),
            Pokemon.ARBOK to arrayOf(R.raw.arbok_1, R.raw.arbok_2, R.raw.arbok_3, R.raw.arbok_4),
            Pokemon.ARCANINE to arrayOf(R.raw.arcanine_1, R.raw.arcanine_2, R.raw.arcanine_3, R.raw.arcanine_4),
            Pokemon.BULBASAUR to arrayOf(R.raw.bulbasaur_1, R.raw.bulbasaur_2, R.raw.bulbasaur_3, R.raw.bulbasaur_4),
            Pokemon.BULBASAUR_CLONE to arrayOf(R.raw.bulbasaur_clone_1, R.raw.bulbasaur_clone_2, R.raw.bulbasaur_clone_3, R.raw.bulbasaur_clone_4),
            Pokemon.CHARMELEON to arrayOf(R.raw.charmeleon_1, R.raw.charmeleon_2, R.raw.charmeleon_3, R.raw.charmeleon_4),
            Pokemon.CLOYSTER to arrayOf(R.raw.cloyster_1, R.raw.cloyster_2, R.raw.cloyster_3, R.raw.cloyster_4),
            Pokemon.DEWGONG to arrayOf(R.raw.dewgong_1, R.raw.dewgong_2, R.raw.dewgong_3, R.raw.dewgong_4),
            Pokemon.GEODUDE to arrayOf(R.raw.geodude_1, R.raw.geodude_2, R.raw.geodude_3, R.raw.geodude_4),
            Pokemon.GLOOM to arrayOf(R.raw.gloom_1, R.raw.gloom_2, R.raw.gloom_3, R.raw.gloom_4),
            Pokemon.GOLBAT to arrayOf(R.raw.golbat_1, R.raw.golbat_2, R.raw.golbat_3, R.raw.golbat_4),
            Pokemon.GROWLITHE to arrayOf(R.raw.growlithe_1, R.raw.growlithe_2, R.raw.growlithe_3, R.raw.growlithe_4),
            Pokemon.HAPPY to arrayOf(R.raw.happy_1, R.raw.happy_2, R.raw.happy_3, R.raw.happy_4),
            Pokemon.HITMONCHAN to arrayOf(R.raw.hitmonchan_1, R.raw.hitmonchan_2, R.raw.hitmonchan_3, R.raw.hitmonchan_4),
            Pokemon.HORSEA to arrayOf(R.raw.horsea_1, R.raw.horsea_2, R.raw.horsea_3, R.raw.horsea_4),
            Pokemon.HYPNO to arrayOf(R.raw.hypno_1, R.raw.hypno_2, R.raw.hypno_3, R.raw.hypno_4),
            Pokemon.JOLTEON to arrayOf(R.raw.jolteon_1, R.raw.jolteon_2, R.raw.jolteon_3, R.raw.jolteon_4),
            Pokemon.KINGLER to arrayOf(R.raw.kingler_1, R.raw.kingler_2, R.raw.kingler_3, R.raw.kingler_4),
            Pokemon.KRABBY to arrayOf(R.raw.krabby_1, R.raw.krabby_2, R.raw.krabby_3, R.raw.krabby_4),
            Pokemon.MAGMAR to arrayOf(R.raw.magmar_1, R.raw.magmar_2, R.raw.magmar_3, R.raw.magmar_4),
            Pokemon.MAGNETON to arrayOf(R.raw.magneton_1, R.raw.magneton_2, R.raw.magneton_3, R.raw.magneton_4),
            Pokemon.MARILL to arrayOf(R.raw.marill_1, R.raw.marill_2, R.raw.marill_3, R.raw.marill_4),
            Pokemon.NIDOKING to arrayOf(R.raw.nidoking_1, R.raw.nidoking_2, R.raw.nidoking_3, R.raw.nidoking_4),
            Pokemon.NIDOQUEEN to arrayOf(R.raw.nidoqueen_1, R.raw.nidoqueen_2, R.raw.nidoqueen_3, R.raw.nidoqueen_4),
            Pokemon.NIDORAN to arrayOf(R.raw.nidoran_1, R.raw.nidoran_2, R.raw.nidoran_3, R.raw.nidoran_4),
            Pokemon.ONIX to arrayOf(R.raw.onix_1, R.raw.onix_2, R.raw.onix_3, R.raw.onix_4),
            Pokemon.PERSIAN to arrayOf(R.raw.persian_1, R.raw.persian_2, R.raw.persian_3, R.raw.persian_4),
            Pokemon.PIKACHU to arrayOf(R.raw.pikachu_1, R.raw.pikachu_2, R.raw.pikachu_3, R.raw.pikachu_4),
            Pokemon.PIKACHU_CLONE to arrayOf(R.raw.pikachu_clone_1, R.raw.pikachu_clone_2, R.raw.pikachu_clone_3, R.raw.pikachu_clone_4),
            Pokemon.POLIWHIRL to arrayOf(R.raw.poliwhirl_1, R.raw.poliwhirl_2, R.raw.poliwhirl_3, R.raw.poliwhirl_4),
            Pokemon.PRIMEAPE to arrayOf(R.raw.primeape_1, R.raw.primeape_2, R.raw.primeape_3, R.raw.primeape_4),
            Pokemon.PSYDUCK to arrayOf(R.raw.psyduck_1, R.raw.psyduck_2, R.raw.psyduck_3, R.raw.psyduck_4),
            Pokemon.RAICHU to arrayOf(R.raw.raichu_1, R.raw.raichu_2, R.raw.raichu_3, R.raw.raichu_4),
            Pokemon.SANDSLASH to arrayOf(R.raw.sandslash_1, R.raw.sandslash_2, R.raw.sandslash_3, R.raw.sandslash_4),
            Pokemon.SCYTHER to arrayOf(R.raw.scyther_1, R.raw.scyther_2, R.raw.scyther_3, R.raw.scyther_4),
            Pokemon.SPARKY to arrayOf(R.raw.sparky_1, R.raw.sparky_2, R.raw.sparky_3, R.raw.sparky_4),
            Pokemon.SQUIRTLE to arrayOf(R.raw.squirtle_1, R.raw.squirtle_2, R.raw.squirtle_3, R.raw.squirtle_4),
            Pokemon.SQUIRTLE_CLONE to arrayOf(R.raw.squirtle_clone_1, R.raw.squirtle_clone_2, R.raw.squirtle_clone_3, R.raw.squirtle_clone_4),
            Pokemon.STARYU to arrayOf(R.raw.staryu_1, R.raw.staryu_2, R.raw.staryu_3, R.raw.staryu_4),
            Pokemon.TANGELA to arrayOf(R.raw.tangela_1, R.raw.tangela_2, R.raw.tangela_3, R.raw.tangela_4),
            Pokemon.VENOMOTH to arrayOf(R.raw.venomoth_1, R.raw.venomoth_2, R.raw.venomoth_3, R.raw.venomoth_4),
            Pokemon.VENONAT to arrayOf(R.raw.venonat_1, R.raw.venonat_2, R.raw.venonat_3, R.raw.venonat_4),
            Pokemon.VOLTORB to arrayOf(R.raw.voltorb_1, R.raw.voltorb_2, R.raw.voltorb_3, R.raw.voltorb_4),
            Pokemon.VULPIX to arrayOf(R.raw.vulpix_1, R.raw.vulpix_2, R.raw.vulpix_3, R.raw.vulpix_4),
            Pokemon.WEEPINBELL to arrayOf(R.raw.weepingbell_1, R.raw.weepingbell_2, R.raw.weepingbell_3, R.raw.weepingbell_4),
            Pokemon.WEEZING to arrayOf(R.raw.weezing_1, R.raw.weezing_2, R.raw.weezing_3, R.raw.weezing_4),
            Pokemon.ZIPPO to arrayOf(R.raw.zippo_1, R.raw.zippo_2, R.raw.zippo_3, R.raw.zippo_4),
            Pokemon.ZUBAT to arrayOf(R.raw.zubat_1, R.raw.zubat_2, R.raw.zubat_3, R.raw.zubat_4)
    )

    private val pokemonSelectionSound = hashMapOf(
            Pokemon.ABRA to R.raw.abra_selection, Pokemon.ALAKAZAM to R.raw.alakazam_selection, Pokemon.ARBOK to R.raw.arbok_selection, Pokemon.ARCANINE to R.raw.arcanine_selection,
            Pokemon.BULBASAUR to R.raw.bulbasaur_selection, Pokemon.BULBASAUR_CLONE to R.raw.bulbasaur_clone_selection, Pokemon.CHARMELEON to R.raw.charmeleon_selection, Pokemon.CLOYSTER to R.raw.cloyster_selection,
            Pokemon.DEWGONG to R.raw.dewgong_selection, Pokemon.GEODUDE to  R.raw.geodude_selection, Pokemon.GLOOM to  R.raw.gloom_selection, Pokemon.GOLBAT to  R.raw.golbat_selection,
            Pokemon.GROWLITHE to  R.raw.growlithe_selection, Pokemon.HAPPY to  R.raw.happy_selection, Pokemon.HITMONCHAN to  R.raw.hitmonchan_selection, Pokemon.HORSEA to  R.raw.horsea_selection,
            Pokemon.HYPNO to R.raw.hypno_selection, Pokemon.JOLTEON to R.raw.jolteon_selection, Pokemon.KINGLER to R.raw.kingler_selection, Pokemon.KRABBY to R.raw.krabby_selection,
            Pokemon.MAGMAR to R.raw.magmar_selection, Pokemon.MAGNETON to R.raw.magneton_selection, Pokemon.MARILL to R.raw.marill_selection, Pokemon.NIDOKING to R.raw.nidoking_selection,
            Pokemon.NIDOQUEEN to R.raw.nidoqueen_selection, Pokemon.NIDORAN to R.raw.nidoran_selection, Pokemon.ONIX to R.raw.onix_selection, Pokemon.PERSIAN to R.raw.persian_selection,
            Pokemon.PIKACHU to R.raw.pikachu_selection, Pokemon.PIKACHU_CLONE to R.raw.pikachu_clone_selection, Pokemon.POLIWHIRL to R.raw.poliwhirl_selection, Pokemon.PRIMEAPE to R.raw.primeape_selection,
            Pokemon.PSYDUCK to R.raw.psyduck_selection, Pokemon.RAICHU to R.raw.raichu_selection, Pokemon.SANDSLASH to R.raw.sandslash_selection, Pokemon.SCYTHER to R.raw.scyther_selection,
            Pokemon.SPARKY to R.raw.sparky_selection, Pokemon.SQUIRTLE to R.raw.squirtle_selection, Pokemon.SQUIRTLE_CLONE to R.raw.squirtle_clone_selection, Pokemon.STARYU to R.raw.staryu_selection,
            Pokemon.TANGELA to R.raw.tangela_selection, Pokemon.VENOMOTH to R.raw.venomoth_selection, Pokemon.VENONAT to R.raw.venonat_selection, Pokemon.VOLTORB to R.raw.voltorb_selection,
            Pokemon.VULPIX to R.raw.vulpix_selection, Pokemon.WEEPINBELL to R.raw.weepingbell_selection, Pokemon.WEEZING to R.raw.weezing_selection, Pokemon.ZIPPO to R.raw.zippo_select,
            Pokemon.ZUBAT to R.raw.zubat_selection
    )

    private val pokemonTrainerBackground = hashMapOf(
            Trainer.ASH to hashMapOf(Pokemon.PIKACHU to R.drawable.ash_pikachu, Pokemon.SQUIRTLE to R.drawable.ash_squirtle, Pokemon.BULBASAUR to R.drawable.ash_bulbasaur),
            Trainer.BLAINE to hashMapOf(Pokemon.ARCANINE to R.drawable.blaine_arcanine, Pokemon.CHARMELEON to R.drawable.blaine_charmeleon, Pokemon.MAGMAR to R.drawable.blaine_magmar),
            Trainer.BROCK to hashMapOf(Pokemon.GEODUDE to R.drawable.brock_geodude, Pokemon.VULPIX to R.drawable.brock_vulpix, Pokemon.ZUBAT to R.drawable.brock_zubat),
            Trainer.BRUNO to hashMapOf(Pokemon.ONIX to R.drawable.bruno_onix, Pokemon.HITMONCHAN to R.drawable.bruno_hitmonchan, Pokemon.PRIMEAPE to R.drawable.bruno_primeape),
            Trainer.ERIKA to hashMapOf(Pokemon.TANGELA to R.drawable.erika_tangela, Pokemon.WEEPINBELL to R.drawable.erika_weepinbell, Pokemon.GLOOM to R.drawable.erika_gloom),
            Trainer.GARY to hashMapOf(Pokemon.NIDORAN to R.drawable.gary_nidoran, Pokemon.GROWLITHE to R.drawable.gary_growlithe, Pokemon.KRABBY to R.drawable.gary_krabby, Pokemon.NIDOQUEEN to R.drawable.gary_evolved_nidoqueen, Pokemon.ARCANINE to R.drawable.gary_evolved_arcanine, Pokemon.KINGLER to R.drawable.gary_evolved_kingler),
            Trainer.GIOVANNI to hashMapOf(Pokemon.PERSIAN to R.drawable.giovanni_persian, Pokemon.SANDSLASH to R.drawable.giovanni_sandslash, Pokemon.NIDOKING to R.drawable.giovanni_nidoking),
            Trainer.KOGA to hashMapOf(Pokemon.VENOMOTH to R.drawable.koga_venomoth, Pokemon.VOLTORB to R.drawable.koga_voltorb, Pokemon.GOLBAT to R.drawable.koga_golbat),
            Trainer.LORELEI to hashMapOf(Pokemon.CLOYSTER to R.drawable.lorelei_cloyster, Pokemon.POLIWHIRL to R.drawable.lorelei_poliwhirl, Pokemon.DEWGONG to R.drawable.lorelei_dewgong),
            Trainer.LT_SURGE to hashMapOf(Pokemon.RAICHU to R.drawable.lt_surge_raichu, Pokemon.JOLTEON to R.drawable.lt_surge_jolteon, Pokemon.MAGNETON to R.drawable.lt_surge_magneton),
            Trainer.MEWTWO to hashMapOf(),
            Trainer.MISTY to hashMapOf(Pokemon.HORSEA to R.drawable.misty_horsea, Pokemon.PSYDUCK to R.drawable.misty_psyduck, Pokemon.STARYU to R.drawable.misty_staryu),
            Trainer.RITCHIE to hashMapOf(Pokemon.SPARKY to R.drawable.ritchie_zappy, Pokemon.ZIPPO to R.drawable.ritchie_zippo, Pokemon.HAPPY to R.drawable.ritchie_happy),
            Trainer.SABRINA to hashMapOf(Pokemon.ABRA to R.drawable.sabrina_abra, Pokemon.HYPNO to R.drawable.sabrina_hypno, Pokemon.ALAKAZAM to R.drawable.sabrina_alakazam),
            Trainer.TEAM_ROCKET to hashMapOf(Pokemon.WEEZING to R.drawable.team_rocket_weezing, Pokemon.ARBOK to R.drawable.team_rocket_arbok, Pokemon.GOLBAT to R.drawable.team_rocket_golbat),
            Trainer.TRACY to hashMapOf(Pokemon.MARILL to R.drawable.tracey_marill, Pokemon.VENONAT to R.drawable.tracey_venonat, Pokemon.SCYTHER to R.drawable.tracey_scyther)
    )

    private val pokemonTrainerBackgroundBitmaps: HashMap<Trainer, HashMap<Pokemon, Bitmap>> = HashMap()

    private val garyEvolvedPokemon = arrayListOf(Pokemon.NIDOQUEEN, Pokemon.ARCANINE, Pokemon.KINGLER)

    fun getPokemonForTrainer(trainer: Trainer): Array<Pokemon> =
        trainerToPokemonMap[trainer].let { it } ?: trainerToPokemonMap[Trainer.ASH]!!

    fun getPokemonPortrait(pokemon: Pokemon): Bitmap =
        portraitResourceBitmaps[pokemon].let { it } ?: portraitResourceBitmaps[Pokemon.PIKACHU]!!

    fun getPokemonName(pokemon: Pokemon, context: Context): String =
        context.resources.getString(pokemonNames[pokemon].let { it } ?: pokemonNames[Pokemon.PIKACHU]!!)

    fun getPokemonComboResources(pokemon: Pokemon): Array<Int> =
        pokemonComboResources[pokemon].let { it } ?: pokemonComboResources[Pokemon.PIKACHU]!!

    fun getPokemonSelectionSound(pokemon: Pokemon): Int =
       pokemonSelectionSound[pokemon].let { it } ?: pokemonSelectionSound[Pokemon.PIKACHU]!!

    fun getPokemonBackground(trainer: Trainer, pokemon: Pokemon): Bitmap? =
        pokemonTrainerBackgroundBitmaps[trainer]?.let { i ->
            i[pokemon]?.let {
                it
            }
        }

    fun isEvolvedGary(pokemon: Pokemon): Boolean =
        garyEvolvedPokemon.contains(pokemon)

    fun createImageBitmaps(context: Context) {
        for (pokemon in Pokemon.values()) {
            portraitResourceBitmaps[pokemon] = BitmapFactory.decodeResource(context.resources, portraitResources[pokemon]!!)
        }

        pokemonTrainerBackground.forEach { (trainer, pokemonMap) ->
            pokemonTrainerBackgroundBitmaps[trainer] = HashMap()
            pokemonMap.forEach { (pokemon, resourceID) ->
                pokemonTrainerBackgroundBitmaps[trainer]!![pokemon] = BitmapFactory.decodeResource(context.resources, resourceID)
            }
        }
    }
}
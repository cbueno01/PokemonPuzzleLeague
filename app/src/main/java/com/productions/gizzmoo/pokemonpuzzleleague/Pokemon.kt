package com.productions.gizzmoo.pokemonpuzzleleague

enum class Pokemon(val ID : Int) {
    ABRA(0),
    ALAKAZAM(1),
    ARBOK(2),
    ARCANINE(3),
    BULBASAUR(4),
    BULBASAUR_CLONE(5),
    CHARMELEON(6),
    CLOYSTER(7),
    DEWGONG(8),
    GEODUDE(9),
    GLOOM(10),
    GOLBAT(11),
    GROWLITHE(12),
    HAPPY(13),
    HITMONCHAN(14),
    HORSEA(15),
    HYPNO(16),
    JOLTEON(17),
    KINGLER(18),
    KRABBY(19),
    MAGMAR(20),
    MAGNETON(21),
    MARILL(22),
    NIDOKING(23),
    NIDOQUEEN(24),
    NIDORAN(25),
    ONIX(26),
    PERSIAN(27),
    PIKACHU(28),
    PIKACHU_CLONE(29),
    POLIWHIRL(30),
    PRIMEAPE(31),
    PSYDUCK(32),
    RAICHU(33),
    SANDSLASH(34),
    SCYTHER(35),
    SPARKY(36),
    SQUIRTLE(37),
    SQUIRTLE_CLONE(38),
    STARYU(39),
    TANGELA(40),
    VENOMOTH(41),
    VENONAT(42),
    VOLTORB(43),
    VULPIX(44),
    WEEPINBELL(45),
    WEEZING(46),
    ZIPPO(47),
    ZUBAT(48);

    companion object {
        fun getTypeByID(pokemonID: Int) : Pokemon {
            for (Pokemon in values()) {
                if (Pokemon.ID == pokemonID) {
                    return Pokemon
                }
            }

            return PIKACHU
        }
    }
}
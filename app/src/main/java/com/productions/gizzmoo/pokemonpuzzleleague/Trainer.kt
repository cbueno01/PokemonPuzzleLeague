package com.productions.gizzmoo.pokemonpuzzleleague

enum class Trainer(val ID : Int) {
    ASH(0),
    BLAINE(1),
    BROCK(2),
    BRUNO(3),
    ERIKA(4),
    GARY(5),
    GIOVANNI(6),
    KOGA(7),
    LORELEI(8),
    LT_SURGE(9),
    MEWTWO(10),
    MISTY(11),
    RITCHIE(12),
    SABRINA(13),
    TEAM_ROCKET(14),
    TRACY(15);

    companion object {
        fun getTypeByID(trainerID: Int) : Trainer {
            for (Trainer in values()) {
                if (Trainer.ID == trainerID) {
                    return Trainer
                }
            }

            return ASH
        }
    }
}
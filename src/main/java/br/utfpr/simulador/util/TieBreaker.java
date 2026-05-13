package br.utfpr.simulador.util;

import br.utfpr.simulador.model.TaskControlBlock;

import java.util.Random;

public class TieBreaker {

    private static final Random random =
            new Random();

    /*
     * Desempate genérico para SRTF.
     */
    public static TaskControlBlock breakSRTFTie(
            TaskControlBlock currentBest,
            TaskControlBlock challenger,
            TaskControlBlock currentlyRunning
    ) {

        // =====================================
        // 1. Evita troca desnecessária
        // =====================================

        if (currentlyRunning != null) {

            if (challenger.getId() ==
                    currentlyRunning.getId()) {

                return challenger;
            }

            if (currentBest.getId() ==
                    currentlyRunning.getId()) {

                return currentBest;
            }
        }

        // =====================================
        // 2. Menor arrivalTime
        // =====================================

        if (challenger.getArrivalTime() <
                currentBest.getArrivalTime()) {

            return challenger;
        }

        if (challenger.getArrivalTime() >
                currentBest.getArrivalTime()) {

            return currentBest;
        }

        // =====================================
        // 3. Menor duração
        // =====================================

        if (challenger.getDuration() <
                currentBest.getDuration()) {

            return challenger;
        }

        if (challenger.getDuration() >
                currentBest.getDuration()) {

            return currentBest;
        }

        // =====================================
        // 4. Sorteio
        // =====================================

        boolean chooseChallenger =
                random.nextBoolean();

        return chooseChallenger
                ? challenger
                : currentBest;
    }
}

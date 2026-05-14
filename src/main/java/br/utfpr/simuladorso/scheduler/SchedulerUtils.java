package br.utfpr.simuladorso.scheduler;

import br.utfpr.simuladorso.model.TaskControlBlock;

import java.util.List;
import java.util.Random;

/*
 * Funcoes auxiliares compartilhadas pelos escalonadores.
 *
 * Os criterios de desempate sao iguais para SRTF e PRIOP, por isso ficam aqui
 * centralizados: manter tarefa atual, menor ingresso, menor duracao e sorteio.
 */
public class SchedulerUtils {

    private static final Random random =
            new Random();

    public static TieBreakResult breakTie(
            List<TaskControlBlock> tasks,
            TaskControlBlock currentRunning
    ) {

        /*
         * Primeiro criterio: se a tarefa que ja estava rodando continua empatada
         * com as candidatas, ela permanece na CPU para evitar troca de contexto.
         */
        if (currentRunning != null &&
                tasks.contains(currentRunning)) {

            return new TieBreakResult(
                    currentRunning,
                    false
            );
        }

        // Segundo criterio: tarefa que ingressou primeiro no sistema.
        int minArrival =
                tasks.stream()
                        .mapToInt(
                                TaskControlBlock::getArrivalTime
                        )
                        .min()
                        .orElse(Integer.MAX_VALUE);

        List<TaskControlBlock> arrivalWinners =
                tasks.stream()
                        .filter(
                                t -> t.getArrivalTime()
                                        == minArrival
                        )
                        .toList();

        if (arrivalWinners.size() == 1) {

            return new TieBreakResult(
                    arrivalWinners.get(0),
                    false
            );
        }

        // Terceiro criterio: menor duracao original.
        int minDuration =
                arrivalWinners.stream()
                        .mapToInt(
                                TaskControlBlock::getDuration
                        )
                        .min()
                        .orElse(Integer.MAX_VALUE);

        List<TaskControlBlock> durationWinners =
                arrivalWinners.stream()
                        .filter(
                                t -> t.getDuration()
                                        == minDuration
                        )
                        .toList();

        if (durationWinners.size() == 1) {

            return new TieBreakResult(
                    durationWinners.get(0),
                    false
            );
        }

        /*
         * Ultimo criterio: sorteio. O resultado sinaliza randomTieBreak=true
         * para que a interface desenhe um evento especifico no Gantt.
         */
        TaskControlBlock selected =
                durationWinners.get(
                        random.nextInt(
                                durationWinners.size()
                        )
                );

        return new TieBreakResult(
                selected,
                true
        );
    }
}

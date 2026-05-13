package br.utfpr.simulador.scheduler;

import br.utfpr.simulador.model.TaskControlBlock;
import br.utfpr.simulador.util.TieBreaker;

import java.util.Comparator;
import java.util.List;

/*
 * SRTF = Shortest Remaining Time First
 *
 * Escolhe a tarefa com menor tempo restante.
 */

public class SRTFScheduler implements Scheduler {

    @Override
    public TaskControlBlock chooseNextTask(
            List<TaskControlBlock> readyQueue
    ) {

        return readyQueue
                .stream()
                .min(
                        Comparator.comparingInt(
                                TaskControlBlock::getRemainingTime
                        )
                )
                .orElse(null);
    }
}

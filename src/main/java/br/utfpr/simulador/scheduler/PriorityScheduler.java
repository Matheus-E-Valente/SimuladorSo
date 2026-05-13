package br.utfpr.simulador.scheduler;

import br.utfpr.simulador.model.TaskControlBlock;

import java.util.Comparator;
import java.util.List;

/*
 * Escalonador por prioridade preemptiva.
 *
 * Quanto MAIOR a prioridade,
 * mais prioritária é a tarefa.
 */

public class PriorityScheduler implements Scheduler {

    @Override
    public TaskControlBlock chooseNextTask(
            List<TaskControlBlock> readyQueue
    ) {

        return readyQueue
                .stream()
                .max(
                        Comparator.comparingInt(
                                TaskControlBlock::getPriority
                        )
                )
                .orElse(null);
    }
}

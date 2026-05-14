package br.utfpr.simuladorso.scheduler;

import br.utfpr.simuladorso.model.TaskControlBlock;

import java.util.List;

/*
 * Escalonador por prioridade preemptiva (PRIOP).
 *
 * O maior valor numerico de prioridade e considerado mais importante. Quando
 * varias tarefas possuem a mesma prioridade, os criterios gerais de desempate
 * sao aplicados por SchedulerUtils.
 */
public class PriorityScheduler implements Scheduler {

    @Override
    public TieBreakResult chooseNextTask(
            List<TaskControlBlock> readyQueue,
            TaskControlBlock currentRunning
    ) {

        if (readyQueue.isEmpty()) {
            return null;
        }

        // Primeiro criterio especifico do PRIOP: maior prioridade estatica.
        int maxPriority =
                readyQueue.stream()
                        .mapToInt(
                                TaskControlBlock::getPriority
                        )
                        .max()
                        .orElse(Integer.MIN_VALUE);

        List<TaskControlBlock> candidates =
                readyQueue.stream()
                        .filter(
                                t -> t.getPriority()
                                        == maxPriority
                        )
                        .toList();

        return SchedulerUtils.breakTie(
                candidates,
                currentRunning
        );
    }
}

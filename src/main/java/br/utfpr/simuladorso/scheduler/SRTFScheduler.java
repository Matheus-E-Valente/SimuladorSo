package br.utfpr.simuladorso.scheduler;

import br.utfpr.simuladorso.model.TaskControlBlock;

import java.util.List;

/*
 * Escalonador SRTF (Shortest Remaining Time First).
 *
 * A prioridade de escolha e a tarefa com menor tempo restante. Por ser
 * preemptivo, o motor pode chamar este escalonador incluindo a tarefa que ja
 * esta executando para decidir se ela continua ou se outra tarefa assume a CPU.
 */
public class SRTFScheduler implements Scheduler {

    @Override
    public TieBreakResult chooseNextTask(
            List<TaskControlBlock> readyQueue,
            TaskControlBlock currentRunning
    ) {

        if (readyQueue.isEmpty()) {
            return null;
        }

        // Primeiro criterio especifico do SRTF: menor tempo restante.
        int minRemaining =
                readyQueue.stream()
                        .mapToInt(
                                TaskControlBlock::getRemainingTime
                        )
                        .min()
                        .orElse(Integer.MAX_VALUE);

        List<TaskControlBlock> candidates =
                readyQueue.stream()
                        .filter(
                                t -> t.getRemainingTime()
                                        == minRemaining
                        )
                        .toList();

        return SchedulerUtils.breakTie(
                candidates,
                currentRunning
        );
    }
}

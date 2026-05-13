package br.utfpr.simulador.scheduler;

import br.utfpr.simulador.model.TaskControlBlock;

import java.util.List;

/*
 * Interface base de todos os algoritmos
 * de escalonamento.
 */

public interface Scheduler {
    /*
     * Escolhe qual será a próxima tarefa
     * a executar.
     */
    TaskControlBlock chooseNextTask(
            List<TaskControlBlock> readyQueue
    );
}

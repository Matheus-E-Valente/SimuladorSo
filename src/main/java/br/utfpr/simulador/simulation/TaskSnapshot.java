package br.utfpr.simulador.simulation;

import br.utfpr.simulador.model.TaskState;

/*
 * Snapshot de uma tarefa.
 *
 * Guarda o estado da tarefa
 * em um tick específico.
 */
public class TaskSnapshot {

    private int id;

    private TaskState state;

    private int remainingTime;

    private int currentCPU;

    public TaskSnapshot(
            int id,
            int remainingTime,
            TaskState state,
            int currentCPU
    ) {

        this.id = id;
        this.state = state;
        this.remainingTime = remainingTime;
        this.currentCPU = currentCPU;

    }

    public int getId() {
        return id;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public TaskState getState() {
        return state;
    }

    public int getCurrentCPU() {
        return currentCPU;
    }
}

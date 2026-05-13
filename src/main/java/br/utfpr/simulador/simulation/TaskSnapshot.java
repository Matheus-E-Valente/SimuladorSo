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

    private int remainingTime;

    private TaskState state;

    private int currentCPU;

    public TaskSnapshot(
            int id,
            int remainingTime,
            TaskState state,
            int currentCPU
    ) {

        this.id = id;
        this.remainingTime = remainingTime;
        this.state = state;
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

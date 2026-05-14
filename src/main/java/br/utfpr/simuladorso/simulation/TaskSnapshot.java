package br.utfpr.simuladorso.simulation;

import br.utfpr.simuladorso.model.TaskState;

/*
 * Fotografia de uma tarefa em um tick especifico.
 *
 * Guarda apenas os dados que podem mudar durante a simulacao e que precisam
 * ser recuperados no retrocesso: estado, tempo restante e CPU atual.
 */
public class TaskSnapshot {

    private int id;

    private TaskState state;

    private int remainingTime;

    private int currentCPU;

    public TaskSnapshot(
            int id,
            TaskState state,
            int remainingTime,
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

    public TaskState getState() {
        return state;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public int getCurrentCPU() {
        return currentCPU;
    }
}

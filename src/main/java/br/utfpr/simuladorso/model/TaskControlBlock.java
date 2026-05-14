package br.utfpr.simuladorso.model;

import java.awt.Color;

/*
 * TCB (Task Control Block) da simulacao.
 *
 * Todas as informacoes de uma tarefa antes, durante e depois da simulacao
 * ficam concentradas nesta classe: tempos, prioridade, estado atual, CPU em uso
 * e controle de quantum. Essa escolha atende ao requisito de manter os dados da
 * tarefa em uma unica estrutura.
 */
public class TaskControlBlock {

    private int id;

    private Color color;

    private int arrivalTime;

    private int duration;

    private int remainingTime;

    private int priority;

    private TaskState state;

    private int currentCPU;

    private int quantumUsed;

    private Integer startTime;

    private Integer finishTime;

    public TaskControlBlock(
            int id,
            Color color,
            int arrivalTime,
            int duration,
            int priority
    ) {

        this.id = id;
        this.color = color;
        this.arrivalTime = arrivalTime;
        this.duration = duration;
        this.remainingTime = duration;
        this.priority = priority;

        this.state = TaskState.NEW;

        this.currentCPU = -1;

        this.quantumUsed = 0;

        this.startTime = null;

        this.finishTime = null;
    }

    /*
     * Simula um tick de CPU consumido pela tarefa.
     * A duracao original e preservada; apenas o tempo restante diminui.
     */
    public void executeOneTick() {

        if (remainingTime > 0) {

            remainingTime--;
        }
    }

    public boolean isFinished() {

        return remainingTime <= 0;
    }

    public int getId() {
        return id;
    }

    public Color getColor() {
        return color;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getDuration() {
        return duration;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    public int getPriority() {
        return priority;
    }

    public TaskState getState() {
        return state;
    }

    public void setState(TaskState state) {
        this.state = state;
    }

    public int getCurrentCPU() {
        return currentCPU;
    }

    public void setCurrentCPU(int currentCPU) {
        this.currentCPU = currentCPU;
    }

    public int getQuantumUsed() {
        return quantumUsed;
    }

    public void setQuantumUsed(int quantumUsed) {
        this.quantumUsed = quantumUsed;
    }

    public Integer getStartTime() {
        return startTime;
    }

    public void setStartTime(Integer startTime) {
        this.startTime = startTime;
    }

    public Integer getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Integer finishTime) {
        this.finishTime = finishTime;
    }
}

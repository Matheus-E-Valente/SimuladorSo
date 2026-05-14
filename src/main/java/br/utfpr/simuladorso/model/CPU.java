package br.utfpr.simuladorso.model;

/*
 * Representa uma CPU/core do computador simulado.
 *
 * A CPU guarda somente as informacoes necessarias para a simulacao:
 * identificador, tarefa em execucao e estado ligado/desligado. A decisao de
 * qual tarefa deve executar fica no escalonador, mantendo esta classe simples.
 */
public class CPU {

    private int id;

    private TaskControlBlock runningTask;

    private boolean poweredOn;

    public CPU(int id) {

        this.id = id;

        this.runningTask = null;

        this.poweredOn = false;
    }

    public int getId() {
        return id;
    }

    public TaskControlBlock getRunningTask() {
        return runningTask;
    }

    public void setRunningTask(TaskControlBlock runningTask) {
        this.runningTask = runningTask;
    }

    public boolean isPoweredOn() {
        return poweredOn;
    }

    public void setPoweredOn(boolean poweredOn) {
        this.poweredOn = poweredOn;
    }

    public boolean isIdle() {
        return runningTask == null;
    }

    /*
     * Liga a CPU quando uma tarefa e atribuida para execucao.
     */
    public void powerOn() {
        this.poweredOn = true;
    }

    /*
     * Desliga a CPU quando nao existe tarefa pronta disponivel.
     */
    public void powerOff() {
        this.poweredOn = false;
    }
}

package br.utfpr.simulador.model;

/*
 * Representa uma CPU/processador do sistema.
 */

public class CPU {

    // Identificador da CPU
    private int id;

    // Indica se a CPU está ligada
    private boolean poweredOn;

    // Tarefa atualmente executando
    private TaskControlBlock runningTask;

    /*
     * Construtor
     */
    public CPU(int id) {

        this.id = id;

        // CPU inicia ligada
        this.poweredOn = true;

        // Inicialmente não executa nada
        this.runningTask = null;
    }

    /*
     * Verifica se CPU está ociosa
     */
    public boolean isIdle() {
        return runningTask == null;
    }

    public void powerOff() {

        poweredOn = false;

        runningTask = null;
    }

    public void powerOn() {

        poweredOn = true;
    }

    // =========================
    // GETTERS E SETTERS
    // =========================

    public int getId() {
        return id;
    }

    public boolean isPoweredOn() {
        return poweredOn;
    }

    public TaskControlBlock getRunningTask() {
        return runningTask;
    }

    public void setPoweredOn(boolean poweredOn) {
        this.poweredOn = poweredOn;
    }

    public void setRunningTask(TaskControlBlock runningTask) {
        this.runningTask = runningTask;
    }

    @Override
    public String toString() {

        if (!poweredOn) {

            return "CPU " + id + " OFF";
        }

        if (runningTask == null) {

            return "CPU " + id + " IDLE";
        }

        return
                "CPU " + id +
                        " RUNNING Task " +
                        runningTask.getId();
    }
}

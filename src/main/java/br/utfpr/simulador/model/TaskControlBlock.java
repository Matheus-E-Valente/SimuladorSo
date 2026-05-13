package br.utfpr.simulador.model;

/*
 * TCB (Task Control Block)
 *
 * Estrutura que armazena TODAS as informações de uma tarefa.
 */

public class TaskControlBlock {
    // Identificador único da tarefa
    private int id;

    // Cor usada no gráfico de gantt
    private String color;

    // Instante em que a tarefa entra no sistema
    private int arrivalTime;

    // Tempo total necessário para executar
    private int duration;

    // Tempo restante para terminar
    private int remainingTime;

    // Prioridade estática da tarefa
    private int priority;

    // Estado atual da tarefa
    private TaskState state;

    // CPU atual onde a tarefa está executando
    private int currentCPU;

    /*
     * Construtor da tarefa
     */
    public TaskControlBlock(
            int id,
            String color,
            int arrivalTime,
            int duration,
            int priority
    ) {

        this.id = id;
        this.color = color;
        this.arrivalTime = arrivalTime;
        this.duration = duration;

        // Inicialmente o tempo restante é igual à duração
        this.remainingTime = duration;

        this.priority = priority;

        // Toda tarefa começa como NEW
        this.state = TaskState.NEW;

        // -1 indica que não está em nenhuma CPU
        this.currentCPU = -1;
    }

    /*
     * Simula 1 tick de execução da tarefa
     */
    public void executeOneTick() {

        // Só executa se ainda existir tempo restante
        if (remainingTime > 0) {
            remainingTime--;
        }
    }

    // Quando chega em zero, finaliza
    public boolean isFinished() {

        return remainingTime <= 0;
    }

    // =========================
    // GETTERS E SETTERS
    // =========================

    public int getId() {
        return id;
    }

    public String getColor() {
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

    public int getPriority() {
        return priority;
    }

    public TaskState getState() {
        return state;
    }

    public int getCurrentCPU() {
        return currentCPU;
    }

    public void setState(TaskState state) {
        this.state = state;
    }

    public void setCurrentCPU(int currentCPU) {
        this.currentCPU = currentCPU;
    }

    public void setRemainingTime(int remainingTime) {

        this.remainingTime = remainingTime;
    }

    /*
     * Representação textual da tarefa
     */
    @Override
    public String toString() {

        return "Task " + id +
                "\n  State: " + state +
                "\n  Arrival: " + arrivalTime +
                "\n  Duration: " + duration +
                "\n  Remaining: " + remainingTime +
                "\n  Priority: " + priority +
                "\n  CPU: " + currentCPU +
                "\n  Color: #" + color;
    }
}

package br.utfpr.simulador.simulation;

import br.utfpr.simulador.event.EventType;
import br.utfpr.simulador.event.SimulationEvent;
import br.utfpr.simulador.model.CPU;
import br.utfpr.simulador.model.TaskControlBlock;
import br.utfpr.simulador.model.TaskState;
import br.utfpr.simulador.scheduler.Scheduler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/*
 * Núcleo principal do simulador.
 *
 * Responsável por:
 * - controlar o relógio
 * - controlar CPUs
 * - executar tarefas
 * - chamar escalonador
 */

public class SimulationEngine {

    // Lista de todas as tarefas
    private List<TaskControlBlock> tasks;

    // CPUs do sistema
    private List<CPU> cpus;

    // Fila READY
    private List<TaskControlBlock> readyQueue;

    // Lista de Snapshots
    private List<SimulationSnapshot> history;

    //lista de eventos
    private List<SimulationEvent> events;

    // Algoritmo de escalonamento
    private Scheduler scheduler;

    // Relógio global do sistema
    private int currentTick;

    /*
     * Construtor do motor
     */
    public SimulationEngine(
            List<TaskControlBlock> tasks,
            int cpuCount,
            Scheduler scheduler
    ) {

        this.tasks = tasks;

        this.scheduler = scheduler;

        this.currentTick = 0;

        this.readyQueue = new LinkedList<>();

        this.cpus = new ArrayList<>();

        this.history = new ArrayList<>();

        this.events = new ArrayList<>();

        // Cria CPUs
        for (int i = 0; i < cpuCount; i++) {
            cpus.add(new CPU(i));
        }
        saveSnapshot();
    }

    /*
     * Executa 1 tick da simulação
     */
    public void nextStep() {

        checkArrivals();
        checkPreemption();
        scheduleTasks();
        executeTasks();
        currentTick++;
        saveSnapshot();
    }

    private void checkArrivals() {
        for (TaskControlBlock task : tasks) {
            if (task.getArrivalTime() == currentTick
                    && task.getState() == TaskState.NEW) {
                task.setState(TaskState.READY);
                readyQueue.add(task);
                addEvent(
                        task.getId(),
                        EventType.TASK_ARRIVAL
                );
            }
        }
    }

    private void scheduleTasks() {
        for (CPU cpu : cpus) {
            if (cpu.isIdle()) {
                TaskControlBlock nextTask =
                        scheduler.chooseNextTask(
                                readyQueue
                        );
                if (nextTask != null) {
                    readyQueue.remove(nextTask);
                    nextTask.setState(
                            TaskState.RUNNING
                    );
                    nextTask.setCurrentCPU(
                            cpu.getId()
                    );
                    cpu.setRunningTask(nextTask);
                    cpu.powerOn();
                }
                else {
                    cpu.powerOff();
                }
            }
        }
    }

    private void executeTasks() {
        for (CPU cpu : cpus) {
            if (cpu.getRunningTask() != null) {
                TaskControlBlock task =
                        cpu.getRunningTask();
                task.executeOneTick();
                if (task.isFinished()) {
                    task.setState(
                            TaskState.FINISHED
                    );
                    addEvent(
                            task.getId(),
                            EventType.TASK_FINISHED
                    );
                    cpu.setRunningTask(null);
                }
            }
        }
    }



    /*
     * Executa até todas tarefas terminarem
     */
    public void runUntilEnd() {

        while (!allTasksFinished()) {
            nextStep();
        }
    }

    /*
     * Verifica se todas tarefas terminaram
     */
    private boolean allTasksFinished() {

        for (TaskControlBlock task : tasks) {

            if (task.getState() !=
                    TaskState.FINISHED) {

                return false;
            }
        }

        return true;
    }
    private void checkPreemption() {

        // Percorre todas CPUs
        for (CPU cpu : cpus) {

            // Se CPU está vazia
            if (cpu.getRunningTask() == null) {
                continue;
            }

            TaskControlBlock runningTask =
                    cpu.getRunningTask();

            // Pergunta ao scheduler qual seria
            // a melhor tarefa atualmente
            TaskControlBlock candidate =
                    scheduler.chooseNextTask(
                            readyQueue
                    );

            // Se não existe tarefa READY
            if (candidate == null) {
                continue;
            }

            /*
             * Verifica se deve ocorrer preempção
             */
            boolean shouldPreempt =
                    candidate.getRemainingTime()
                            < runningTask.getRemainingTime();

            if (shouldPreempt) {

                // Running volta para READY
                runningTask.setState(
                        TaskState.READY
                );

                readyQueue.add(runningTask);
                cpu.setRunningTask(null);
                addEvent(
                        runningTask.getId(),
                        EventType.TASK_PREEMPTED
                );
            }
        }
    }

    private void saveSnapshot() {

        List<TaskSnapshot> taskSnapshots =
                new ArrayList<>();

        for (TaskControlBlock task : tasks) {

            taskSnapshots.add(
                    new TaskSnapshot(
                            task.getId(),
                            task.getRemainingTime(),
                            task.getState(),
                            task.getCurrentCPU()
                    )
            );
        }

        List<CPUSnapshot> cpuSnapshots =
                new ArrayList<>();

        for (CPU cpu : cpus) {

            Integer runningTaskId = null;

            if (cpu.getRunningTask() != null) {

                runningTaskId =
                        cpu.getRunningTask().getId();
            }

            CPUSnapshot snapshot =
                    new CPUSnapshot(
                            cpu.getId(),
                            runningTaskId,
                            cpu.isPoweredOn()
                    );
        }
        history.add(
                new SimulationSnapshot(
                        currentTick,
                        taskSnapshots,
                        cpuSnapshots
                )
        );
    }

    public List<SimulationSnapshot> getHistory() {
        return history;
    }

    public void previousStep() {

        // Precisa existir snapshot anterior
        if(currentTick <= 0) {
            return;
        }
        history.remove(history.size() - 1);
        SimulationSnapshot snapshot =
                history.get(history.size() - 1);
        currentTick = snapshot.getTick();
    }

    // =========================
    // MOSTRA ESDADO DO SISTEMA
    // =========================

    public void printSystemState() {

        System.out.println(
                "\n========================================"
        );

        System.out.println(
                "TICK " + currentTick
        );

        System.out.println(
                "========================================"
        );

        // =====================================
        // CPUs
        // =====================================

        System.out.println("\nCPUS:");

        for (CPU cpu : cpus) {

            System.out.println(cpu);
        }

        // =====================================
        // READY QUEUE
        // =====================================

        System.out.println("\nREADY QUEUE:");

        if (readyQueue.isEmpty()) {

            System.out.println("(vazia)");
        }

        for (TaskControlBlock task :
                readyQueue) {

            System.out.println(
                    "Task " +
                            task.getId()
            );
        }

        // =====================================
        // TASKS
        // =====================================

        System.out.println("\nTASKS:");

        for (TaskControlBlock task :
                tasks) {

            System.out.println(task);

            System.out.println(
                    "------------------------"
            );
        }

        System.out.println(
                "\nSnapshots armazenados: "
                        + history.size()
        );

        System.out.println(
                "========================================"
        );
    }

    public TaskControlBlock findTaskById(
            int taskId
    ) {

        for (TaskControlBlock task :
                tasks) {

            if (task.getId() == taskId) {

                return task;
            }
        }

        return null;
    }

    public int getCurrentTick() {
        return currentTick;
    }

    public List<CPU> getCpus() {
        return cpus;
    }

    public List<TaskControlBlock> getTasks() {
        return tasks;
    }

    private void addEvent(
            int taskId,
            EventType type
    ) {
        events.add(
                new SimulationEvent(
                        currentTick,
                        taskId,
                        type
                )
        );
    }
}

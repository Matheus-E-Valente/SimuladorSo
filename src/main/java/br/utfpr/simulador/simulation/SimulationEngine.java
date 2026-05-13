package br.utfpr.simulador.simulation;

import br.utfpr.simulador.event.SimulationEvent;
import br.utfpr.simulador.model.*;
import br.utfpr.simulador.scheduler.Scheduler;

import java.util.ArrayList;
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

        this.readyQueue = new ArrayList<>();

        this.cpus = new ArrayList<>();

        this.history = new ArrayList<>();

        this.events = new ArrayList<>();

        // Cria CPUs
        for (int i = 0; i < cpuCount; i++) {
            cpus.add(new CPU(i));
        }
    }

    /*
     * Executa 1 tick da simulação
     */
    public void nextStep() {

        System.out.println("\n====================");
        System.out.println("TICK " + currentTick);
        System.out.println("====================");

        // =====================================
        // 1. VERIFICAR CHEGADA DE NOVAS TAREFAS
        // =====================================

        for (TaskControlBlock task : tasks) {

            if (task.getArrivalTime() == currentTick) {

                task.setState(TaskState.READY);

                readyQueue.add(task);

                System.out.println(
                        "Task " +
                                task.getId() +
                                " chegou no sistema."
                );
            }
        }

        addEvent(
                task.getId(),
                EventType.TASK_ARRIVAL
        );

        // =====================================
        // 2. VERIFICAR PREEMPÇÃO
        // =====================================

        checkPreemption();

        printReadyQueue();

        // =====================================
        // 3. ESCALONAR CPUS OCIOSAS
        // =====================================

        for (CPU cpu : cpus) {

            // CPU livre
            if (cpu.isIdle()) {

                // Escolhe próxima tarefa
                TaskControlBlock nextTask =
                        scheduler.chooseNextTask(
                                readyQueue,
                                cpu.getRunningTask()
                        );

                // Se existe não tarefa disponível
                if (nextTask == null) {

                    // Nenhuma tarefa disponível
                    if (cpu.isPoweredOn()) {

                        cpu.powerOff();

                        System.out.println(
                                "CPU " +
                                        cpu.getId() +
                                        " foi DESLIGADA"
                        );
                    }

                    continue;
                }

                if (!cpu.isPoweredOn()) {

                    cpu.powerOn();

                    System.out.println(
                            "CPU " +
                                    cpu.getId() +
                                    " foi RELIGADA"
                    );
                }

                // Se existe tarefa disponível
                if (nextTask != null) {

                    // Remove da fila READY
                    readyQueue.remove(nextTask);

                    // Coloca como RUNNING
                    nextTask.setState(
                            TaskState.RUNNING
                    );

                    // Marca CPU atual
                    nextTask.setCurrentCPU(
                            cpu.getId()
                    );

                    // CPU começa executar
                    cpu.setRunningTask(nextTask);

                    System.out.println(
                            "CPU " +
                                    cpu.getId() +
                                    " iniciou Task " +
                                    nextTask.getId()
                    );
                }
            }
        }

        // =====================================
        // 4. EXECUTAR TAREFAS
        // =====================================

        for (CPU cpu : cpus) {

            TaskControlBlock runningTask =
                    cpu.getRunningTask();

            // CPU possui tarefa?
            if (runningTask != null) {

                // Executa 1 tick
                runningTask.executeOneTick();

                System.out.println(
                        "CPU " +
                                cpu.getId() +
                                " executando Task " +
                                runningTask.getId() +
                                " | remaining=" +
                                runningTask.getRemainingTime()
                );

                // Verifica término
                if (runningTask.getState() ==
                        TaskState.FINISHED) {

                    System.out.println(
                            "Task " +
                                    runningTask.getId() +
                                    " terminou."
                    );

                    // Libera CPU
                    cpu.setRunningTask(null);
                }
            }
        }

        // =====================================
        // 5. SALVA SNAPSHOT
        // =====================================

        saveSnapshot();

        // =====================================
        // 6. AVANÇAR RELÓGIO
        // =====================================

        currentTick++;
    }

    /*
     * Executa até todas tarefas terminarem
     */
    public void runUntilEnd() {

        while (!allTasksFinished()) {
            nextStep();
        }
        System.out.println(
                "Snapshots salvos: " +
                        history.size()
        );
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

            TaskControlBlock runningTask =
                    cpu.getRunningTask();

            // Se CPU está vazia
            if (runningTask == null) {
                continue;
            }

            // Pergunta ao scheduler qual seria
            // a melhor tarefa atualmente
            TaskControlBlock bestReadyTask =
                    scheduler.chooseNextTask(
                            readyQueue,
                            cpu.getRunningTask()
                    );

            // Se não existe tarefa READY
            if (bestReadyTask == null) {
                continue;
            }

            /*
             * Verifica se deve ocorrer preempção
             */
            boolean shouldPreempt =
                    scheduler.shouldPreempt(
                            runningTask,
                            bestReadyTask
                    );

            if (shouldPreempt) {

                System.out.println(
                        "PREEMPÇÃO: Task " +
                                runningTask.getId() +
                                " interrompida por Task " +
                                bestReadyTask.getId()
                );

                // Running volta para READY
                runningTask.setState(
                        TaskState.READY
                );

                readyQueue.add(runningTask);

                // Remove nova da fila READY
                readyQueue.remove(bestReadyTask);

                // Nova tarefa vira RUNNING
                bestReadyTask.setState(
                        TaskState.RUNNING
                );

                bestReadyTask.setCurrentCPU(
                        cpu.getId()
                );

                // CPU troca tarefa
                cpu.setRunningTask(bestReadyTask);
            }
        }
    }

    private void saveSnapshot() {

        List<Integer> readyIds =
                new ArrayList<>();

        for (TaskControlBlock task :
                readyQueue) {

            readyIds.add(task.getId());
        }

        List<TaskSnapshot> taskSnapshots =
                new ArrayList<>();

        for (TaskControlBlock task : tasks) {

            TaskSnapshot snapshot =
                    new TaskSnapshot(
                            task.getId(),
                            task.getRemainingTime(),
                            task.getState(),
                            task.getCurrentCPU()
                    );

            taskSnapshots.add(snapshot);
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

            cpuSnapshots.add(snapshot);
        }

        SimulationSnapshot snapshot =
                new SimulationSnapshot(
                        currentTick,
                        taskSnapshots,
                        cpuSnapshots,
                        readyIds
                );

        history.add(snapshot);
    }

    public List<SimulationSnapshot> getHistory() {
        return history;
    }

    private void restoreSnapshot(
            SimulationSnapshot snapshot
    ) {
        readyQueue.clear();

        // =========================
        // RESTAURAR TAREFAS
        // =========================

        for (TaskSnapshot taskSnapshot :
                snapshot.getTaskSnapshots()) {

            for (TaskControlBlock task :
                    tasks) {

                if (task.getId() ==
                        taskSnapshot.getId()) {

                    task.setRemainingTime(
                            taskSnapshot
                                    .getRemainingTime()
                    );

                    task.setState(
                            taskSnapshot.getState()
                    );

                    task.setCurrentCPU(
                            taskSnapshot
                                    .getCurrentCPU()
                    );
                }
            }
        }


        for (Integer taskId :
                snapshot.getReadyQueueTaskIds()) {

            for (TaskControlBlock task :
                    tasks) {

                if (task.getId() == taskId) {

                    readyQueue.add(task);
                }
            }
        }

        // =========================
        // RESTAURAR CPUS
        // =========================

        for (CPUSnapshot cpuSnapshot :
                snapshot.getCpuSnapshots()) {

            for (CPU cpu : cpus) {

                if (cpu.getId() ==
                        cpuSnapshot.getCpuId()) {

                    cpu.setPoweredOn(
                            cpuSnapshot.isPoweredOn()
                    );

                    // Limpa CPU
                    cpu.setRunningTask(null);

                    // Restaura tarefa executando
                    Integer runningTaskId =
                            cpuSnapshot.getRunningTaskId();

                    if (runningTaskId != null) {

                        for (TaskControlBlock task :
                                tasks) {

                            if (task.getId()
                                    ==
                                    runningTaskId) {

                                cpu.setRunningTask(task);
                            }
                        }
                    }
                }
            }
        }

        // =========================
        // RESTAURAR TICK
        // =========================

        currentTick = snapshot.getTick();
    }

    public void previousStep() {

        // Precisa existir snapshot anterior
        if (history.size() < 2) {

            System.out.println(
                    "Não existe passo anterior."
            );

            return;
        }

        /*
         * Remove snapshot atual
         */
        history.remove(
                history.size() - 1
        );

        /*
         * Snapshot anterior
         */
        SimulationSnapshot previous =
                history.get(
                        history.size() - 1
                );

        /*
         * Restaura sistema
         */
        restoreSnapshot(previous);

        System.out.println(
                "Retrocedeu para tick " +
                        currentTick
        );
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

    private void printReadyQueue() {

        System.out.print("READY: ");

        for (TaskControlBlock task :
                readyQueue) {

            System.out.print(
                    "T" + task.getId() + " "
            );
        }

        System.out.println();
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

    public void changeTaskState(
            int taskId,
            TaskState newState
    ) {

        TaskControlBlock task =
                findTaskById(taskId);

        if (task == null) {

            System.out.println(
                    "Task não encontrada."
            );

            return;
        }

        // Remove da READY queue
        readyQueue.remove(task);

        // Remove de CPUs
        for (CPU cpu : cpus) {

            if (cpu.getRunningTask() == task) {

                cpu.setRunningTask(null);
            }
        }

        // Define novo estado
        task.setState(newState);

        // Se READY, adiciona na fila
        if (newState == TaskState.READY) {

            readyQueue.add(task);
        }

        System.out.println(
                "Task " +
                        taskId +
                        " mudou para " +
                        newState
        );
    }

    public void changeTaskRemainingTime(
            int taskId,
            int newRemainingTime
    ) {

        TaskControlBlock task =
                findTaskById(taskId);

        if (task == null) {

            System.out.println(
                    "Task não encontrada."
            );

            return;
        }

        task.setRemainingTime(
                newRemainingTime
        );

        System.out.println(
                "Task " +
                        taskId +
                        " remainingTime = " +
                        newRemainingTime
        );
    }

    public void forceFinishTask(
            int taskId
    ) {

        TaskControlBlock task =
                findTaskById(taskId);

        if (task == null) {

            return;
        }

        task.setRemainingTime(0);

        changeTaskState(
                taskId,
                TaskState.FINISHED
        );

        System.out.println(
                "Task " +
                        taskId +
                        " foi FINALIZADA"
        );
    }

    public void moveTaskToReady(
            int taskId
    ) {

        changeTaskState(
                taskId,
                TaskState.READY
        );
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

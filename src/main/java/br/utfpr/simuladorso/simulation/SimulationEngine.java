package br.utfpr.simuladorso.simulation;

import br.utfpr.simuladorso.event.EventType;
import br.utfpr.simuladorso.event.SimulationEvent;
import br.utfpr.simuladorso.model.CPU;
import br.utfpr.simuladorso.model.TaskControlBlock;
import br.utfpr.simuladorso.model.TaskState;
import br.utfpr.simuladorso.scheduler.Scheduler;
import br.utfpr.simuladorso.scheduler.TieBreakResult;

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

/*
 * Nucleo principal do simulador.
 *
 * Esta classe coordena o relogio global, a fila de prontos, as CPUs, o
 * escalonador e o historico. O motor nao decide diretamente qual algoritmo e
 * usado; ele apenas chama a interface Scheduler configurada pelo usuario.
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

    // Tempo maximo de execução da tarefa
    private int quantum;

    /*
     * Construtor do motor
     */
    public SimulationEngine(
            List<TaskControlBlock> tasks,
            int cpuCount,
            Scheduler scheduler,
            int quantum
    ) {

        this.tasks = tasks;

        this.scheduler = scheduler;

        this.currentTick = 0;

        this.readyQueue = new LinkedList<>();

        this.cpus = new ArrayList<>();

        this.history = new ArrayList<>();

        this.events = new ArrayList<>();

        this.quantum = quantum;

        // Cria CPUs
        for (int i = 0; i < cpuCount; i++) {
            cpus.add(new CPU(i));
        }
        saveSnapshot();
    }

    /*
     * Executa 1 tick da simulação
     */
    /*
     * Executa um tick completo da simulacao.
     *
     * A ordem das etapas e importante: primeiro entram novas tarefas, depois
     * acontece a preempcao, em seguida CPUs livres recebem tarefas, as tarefas
     * executam um tick, o quantum e verificado e o estado final e salvo.
     */
    public void nextStep() {

        checkArrivals();
        checkPreemption();
        scheduleTasks();
        executeTasks();
        checkQuantumExpiration();
        currentTick++;
        saveSnapshot();
    }

    /*
     * Move tarefas do estado NEW para READY quando o tick atual chega ao
     * instante de ingresso configurado no arquivo.
     */
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

    /*
     * Atribui tarefas prontas para CPUs ociosas.
     *
     * Se nao houver tarefa pronta, a CPU e desligada para atender ao requisito
     * de indicar periodos sem trabalho disponivel.
     */
    private void scheduleTasks() {
        for (CPU cpu : cpus) {
            if (cpu.isIdle()) {
                TieBreakResult result =
                        scheduler.chooseNextTask(
                                readyQueue,
                                cpu.getRunningTask()
                        );

                if (result == null) {
                    cpu.powerOff();
                    continue;
                }

                TaskControlBlock nextTask =
                        result.getSelectedTask();
                if (nextTask != null) {
                    readyQueue.remove(nextTask);

                    if (result.isRandomTieBreak()) {

                        addEvent(
                                nextTask.getId(),
                                EventType.RANDOM_TIE_BREAK
                        );
                    }
                    nextTask.setState(
                            TaskState.RUNNING
                    );
                    nextTask.setQuantumUsed(0);
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

    /*
     * Simula um tick de execucao em cada CPU ligada que possui tarefa.
     */
    private void executeTasks() {
        for (CPU cpu : cpus) {
            if (cpu.getRunningTask() != null) {
                TaskControlBlock task =
                        cpu.getRunningTask();
                task.executeOneTick();
                task.setQuantumUsed(
                        task.getQuantumUsed() + 1
                );
                if (task.isFinished()) {
                    task.setState(
                            TaskState.FINISHED
                    );
                    addEvent(
                            task.getId(),
                            EventType.TASK_FINISHED
                    );
                    task.setCurrentCPU(-1);
                    task.setQuantumUsed(0);
                    cpu.setRunningTask(null);
                }
            }
        }
    }



    /*
     * Executa até todas tarefas terminarem
     */
    /*
     * Executa a simulacao completa sem intervencao humana.
     */
    public void runUntilEnd() {

        while (!allTasksFinished()) {
            nextStep();
        }
    }

    /*
     * Verifica se todas tarefas terminaram
     */
    /*
     * Verifica se todas as tarefas chegaram ao estado FINISHED.
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
    /*
     * Verifica se a tarefa atual de cada CPU deve ser preemptada.
     *
     * Para manter o motor independente do algoritmo, a tarefa em execucao e
     * colocada junto com as tarefas prontas e o escalonador escolhe a melhor.
     * Se a escolhida for diferente da atual, ocorre a preempcao.
     */
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
            List<TaskControlBlock> candidates =
                    new ArrayList<>(
                            readyQueue
                    );

            candidates.add(runningTask);

            TieBreakResult result =
                    scheduler.chooseNextTask(
                            candidates,
                            runningTask
                    );

            if (result == null) {
                continue;
            }

            TaskControlBlock candidate =
                    result.getSelectedTask();

            if (candidate == null) {
                continue;
            }

            if (result.isRandomTieBreak()) {

                addEvent(
                        candidate.getId(),
                        EventType.RANDOM_TIE_BREAK
                );
            }

            // Se não existe tarefa READY
            if (candidate == null) {
                continue;
            }

            /*
             * Verifica se deve ocorrer preempção
             */
            boolean shouldPreempt =
                    candidate != runningTask;

            if (shouldPreempt) {

                // Running volta para READY
                runningTask.setState(
                        TaskState.READY
                );
                runningTask.setQuantumUsed(0);

                readyQueue.add(runningTask);
                runningTask.setCurrentCPU(-1);

                readyQueue.remove(candidate);
                candidate.setState(TaskState.RUNNING);
                candidate.setQuantumUsed(0);
                candidate.setCurrentCPU(cpu.getId());
                cpu.setRunningTask(candidate);
                cpu.powerOn();

                addEvent(
                        runningTask.getId(),
                        EventType.TASK_PREEMPTED
                );
            }
        }
    }

    /*
     * Salva uma fotografia do estado atual.
     *
     * O historico e usado tanto pelo Gantt quanto pelo botao BACK. Por isso o
     * snapshot inclui tarefas, CPUs, fila de prontos e eventos registrados.
     */
    private void saveSnapshot() {

        List<TaskSnapshot> taskSnapshots =
                new ArrayList<>();

        List<Integer> readyIds =
                new ArrayList<>();

        for (TaskControlBlock task : tasks) {

            taskSnapshots.add(
                    new TaskSnapshot(
                            task.getId(),
                            task.getState(),
                            task.getRemainingTime(),
                            task.getCurrentCPU()
                    )
            );
        }

        for (TaskControlBlock task :
                readyQueue) {

            readyIds.add(
                    task.getId()
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

            cpuSnapshots.add(snapshot);
        }
        history.add(
                new SimulationSnapshot(
                        currentTick,
                        taskSnapshots,
                        cpuSnapshots,
                        readyIds,
                        new ArrayList<>(events)
                )
        );
    }

    public List<SimulationSnapshot> getHistory() {
        return history;
    }

    /*
     * Volta um passo da simulacao removendo o snapshot atual e restaurando o
     * snapshot anterior.
     */
    public void previousStep() {

        if (history.size() <= 1) {
            return;
        }

        // remove snapshot atual

        history.remove(
                history.size() - 1
        );

        // pega snapshot anterior

        SimulationSnapshot snapshot =
                history.get(
                        history.size() - 1
                );

        restoreSnapshot(snapshot);
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

    public List<SimulationEvent> getEvents() {
        return events;
    }

    /*
     * Registra um evento relevante para ser exibido graficamente no Gantt.
     */
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

    /*
     * Aplica o limite de quantum do sistema de tempo compartilhado.
     *
     * Quando a tarefa atinge o quantum, ela volta para READY e libera a CPU.
     */
    private void checkQuantumExpiration() {

        for (CPU cpu : cpus) {

            if (cpu.getRunningTask() == null) {
                continue;
            }

            TaskControlBlock task =
                    cpu.getRunningTask();

            if (task.getQuantumUsed()
                    >= quantum) {

                task.setQuantumUsed(0);

                task.setState(
                        TaskState.READY
                );

                readyQueue.add(task);
                task.setCurrentCPU(-1);

                cpu.setRunningTask(null);

                addEvent(
                        task.getId(),
                        EventType.TASK_PREEMPTED
                );
            }
        }
    }

    /*
     * Reconstroi o estado real do motor a partir de um snapshot salvo.
     */
    private void restoreSnapshot(
            SimulationSnapshot snapshot
    ) {

        currentTick =
                snapshot.getTick();

        // =========================
        // restaurar tasks
        // =========================

        for (TaskSnapshot taskSnap :
                snapshot.getTaskSnapshots()) {

            TaskControlBlock task =
                    findTaskById(
                            taskSnap.getId()
                    );

            task.setState(
                    taskSnap.getState()
            );

            task.setRemainingTime(
                    taskSnap.getRemainingTime()
            );

            task.setCurrentCPU(
                    taskSnap.getCurrentCPU()
            );
        }

        // =========================
        // restaurar CPUs
        // =========================

        for (CPUSnapshot cpuSnap :
                snapshot.getCpuSnapshots()) {

            CPU cpu =
                    cpus.get(
                            cpuSnap.getCpuId()
                    );

            cpu.setPoweredOn(
                    cpuSnap.isPoweredOn()
            );

            if (cpuSnap.getRunningTaskId()
                    == null) {

                cpu.setRunningTask(null);
            }

            else {

                cpu.setRunningTask(
                        findTaskById(
                                cpuSnap
                                        .getRunningTaskId()
                        )
                );
            }
        }

        // =========================
        // restaurar readyQueue
        // =========================

        readyQueue.clear();

        for (Integer id :
                snapshot.getReadyQueueIds()) {

            readyQueue.add(
                    findTaskById(id)
            );
        }

        events =
                new ArrayList<>(
                        snapshot.getEvents()
                );
    }

    /*
     * Permite que o usuario altere manualmente o estado de uma tarefa.
     *
     * A tarefa e removida da CPU/fila anterior antes de receber o novo estado,
     * evitando que ela fique duplicada na simulacao.
     */
    public void changeTaskState(
            int taskId,
            TaskState newState
    ) {

        TaskControlBlock task =
                findTaskById(taskId);

        if (task == null) {
            return;
        }

        // remove task da CPU atual

        if (task.getCurrentCPU() >= 0) {

            CPU cpu =
                    cpus.get(
                            task.getCurrentCPU()
                    );

            cpu.setRunningTask(null);

            task.setCurrentCPU(-1);
        }

        // remove da ready queue

        readyQueue.remove(task);

        // aplica novo estado

        task.setState(newState);

        // trata READY

        if (newState == TaskState.READY) {

            readyQueue.add(task);
        }

        // trata FINISHED

        if (newState == TaskState.FINISHED) {

            task.setRemainingTime(0);
            task.setCurrentCPU(-1);
        }
    }
}

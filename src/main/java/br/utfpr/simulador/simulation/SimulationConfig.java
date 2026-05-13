package br.utfpr.simulador.simulation;

import br.utfpr.simulador.model.TaskControlBlock;
import br.utfpr.simulador.scheduler.Scheduler;

import java.util.List;

/*
 * Armazena toda configuração da simulação.
 */
public class SimulationConfig {

    // Algoritmo de escalonamento
    private Scheduler scheduler;

    // Quantum do sistema
    private int quantum;

    // Quantidade de CPUs
    private int cpuCount;

    // Lista de tarefas
    private List<TaskControlBlock> tasks;

    public SimulationConfig(
            Scheduler scheduler,
            int quantum,
            int cpuCount,
            List<TaskControlBlock> tasks
    ) {

        this.scheduler = scheduler;
        this.quantum = quantum;
        this.cpuCount = cpuCount;
        this.tasks = tasks;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public int getQuantum() {
        return quantum;
    }

    public int getCpuCount() {
        return cpuCount;
    }

    public List<TaskControlBlock> getTasks() {
        return tasks;
    }
}

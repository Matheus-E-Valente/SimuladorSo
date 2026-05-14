package br.utfpr.simuladorso.parser;

import br.utfpr.simuladorso.model.TaskControlBlock;
import br.utfpr.simuladorso.scheduler.Scheduler;

import java.util.List;

/*
 * Objeto de configuracao criado a partir do arquivo config.txt.
 *
 * Ele separa a leitura do arquivo da execucao da simulacao. Com isso, o Main
 * apenas recebe uma configuracao pronta e monta o motor da simulacao.
 */
public class SimulationConfig {

    private Scheduler scheduler;

    private int quantum;

    private int cpuCount;

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

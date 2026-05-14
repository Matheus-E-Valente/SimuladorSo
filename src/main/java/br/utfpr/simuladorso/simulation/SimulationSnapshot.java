package br.utfpr.simuladorso.simulation;

import br.utfpr.simuladorso.event.SimulationEvent;

import java.util.List;

/*
 * Fotografia completa do sistema em um tick do relogio.
 *
 * O historico de snapshots permite que o simulador avance e retroceda sem
 * recalcular tudo desde o inicio. Cada snapshot contem tarefas, CPUs, fila de
 * prontos e eventos ja ocorridos ate aquele ponto.
 */
public class SimulationSnapshot {

    private int tick;

    private List<TaskSnapshot> taskSnapshots;

    private List<CPUSnapshot> cpuSnapshots;

    private List<Integer> readyQueueIds;

    private List<SimulationEvent> events;

    public SimulationSnapshot(
            int tick,
            List<TaskSnapshot> taskSnapshots,
            List<CPUSnapshot> cpuSnapshots,
            List<Integer> readyQueueIds,
            List<SimulationEvent> events
    ) {

        this.tick = tick;
        this.taskSnapshots = taskSnapshots;
        this.cpuSnapshots = cpuSnapshots;
        this.readyQueueIds = readyQueueIds;
        this.events = events;
    }

    public int getTick() {
        return tick;
    }

    public List<TaskSnapshot> getTaskSnapshots() {
        return taskSnapshots;
    }

    public List<CPUSnapshot> getCpuSnapshots() {
        return cpuSnapshots;
    }

    public List<Integer> getReadyQueueIds() {
        return readyQueueIds;
    }

    public List<SimulationEvent> getEvents() {
        return events;
    }
}

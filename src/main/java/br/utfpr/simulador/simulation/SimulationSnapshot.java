package br.utfpr.simulador.simulation;

import java.util.List;

/*
 * Representa uma fotografia completa
 * do sistema em um tick específico.
 */
public class SimulationSnapshot {

    private int tick;

    private List<TaskSnapshot> taskSnapshots;

    private List<CPUSnapshot> cpuSnapshots;

    public SimulationSnapshot(
            int tick,
            List<TaskSnapshot> taskSnapshots,
            List<CPUSnapshot> cpuSnapshots
    ) {

        this.tick = tick;
        this.taskSnapshots = taskSnapshots;
        this.cpuSnapshots = cpuSnapshots;
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
}

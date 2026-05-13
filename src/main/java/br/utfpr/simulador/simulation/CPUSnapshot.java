package br.utfpr.simulador.simulation;

/*
 * Snapshot de uma CPU.
 */
public class CPUSnapshot {

    private int cpuId;

    private Integer runningTaskId;

    private boolean poweredOn;

    public CPUSnapshot(
            int cpuId,
            Integer runningTaskId,
            boolean poweredOn
    ) {

        this.cpuId = cpuId;
        this.runningTaskId = runningTaskId;
        this.poweredOn = poweredOn;
    }

    public int getCpuId() {
        return cpuId;
    }

    public Integer getRunningTaskId() {
        return runningTaskId;
    }

    public boolean isPoweredOn() {
        return poweredOn;
    }
}

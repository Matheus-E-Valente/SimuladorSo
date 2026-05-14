package br.utfpr.simuladorso.simulation;

/*
 * Fotografia do estado de uma CPU em um tick especifico.
 *
 * O snapshot e usado para desenhar o Gantt e tambem para restaurar a simulacao
 * quando o usuario clica em BACK.
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

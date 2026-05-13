package br.utfpr.simulador.event;

/*
 * Representa um evento importante
 * ocorrido na simulação.
 */
public class SimulationEvent {

    private int tick;

    private int taskId;

    private EventType type;

    public SimulationEvent(
            int tick,
            int taskId,
            EventType type
    ) {

        this.tick = tick;
        this.taskId = taskId;
        this.type = type;
    }

    public int getTick() {
        return tick;
    }

    public int getTaskId() {
        return taskId;
    }

    public EventType getType() {
        return type;
    }
}

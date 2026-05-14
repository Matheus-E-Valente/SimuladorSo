package br.utfpr.simuladorso.simulation;

/*
 * Mantida por compatibilidade com versoes anteriores do projeto.
 * A classe usada atualmente pelo motor fica no pacote br.utfpr.simuladorso.event.
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

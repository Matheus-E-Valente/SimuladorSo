package br.utfpr.simuladorso.event;

/*
 * Evento importante ocorrido durante a simulacao.
 *
 * Cada evento guarda o tick em que ocorreu, a tarefa relacionada e o tipo do
 * evento. A interface usa esses dados para desenhar icones distintos no Gantt.
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

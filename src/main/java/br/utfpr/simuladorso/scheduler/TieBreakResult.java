package br.utfpr.simuladorso.scheduler;

import br.utfpr.simuladorso.model.TaskControlBlock;

/*
 * Resultado retornado pelo escalonador.
 *
 * Alem da tarefa escolhida, informa se a escolha precisou de sorteio. Essa
 * informacao e usada pelo motor para registrar o evento no grafico.
 */
public class TieBreakResult {

    private TaskControlBlock selectedTask;

    private boolean randomTieBreak;

    public TieBreakResult(
            TaskControlBlock selectedTask,
            boolean randomTieBreak
    ) {

        this.selectedTask = selectedTask;
        this.randomTieBreak = randomTieBreak;
    }

    public TaskControlBlock getSelectedTask() {
        return selectedTask;
    }

    public boolean isRandomTieBreak() {
        return randomTieBreak;
    }
}

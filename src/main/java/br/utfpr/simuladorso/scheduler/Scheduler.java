package br.utfpr.simuladorso.scheduler;

import br.utfpr.simuladorso.model.TaskControlBlock;

import java.util.List;

/*
 * Contrato comum para qualquer algoritmo de escalonamento.
 *
 * A simulacao depende apenas desta interface. Assim, novos algoritmos podem ser
 * adicionados criando outra classe Scheduler, sem alterar o motor principal.
 */
public interface Scheduler {

    /*
     * Escolhe a melhor tarefa entre as candidatas.
     *
     * currentRunning e informado para permitir o primeiro criterio de desempate:
     * manter a tarefa atual quando ela continua empatada com as demais.
     */
    TieBreakResult chooseNextTask(
            List<TaskControlBlock> readyQueue,
            TaskControlBlock currentRunning
    );
}

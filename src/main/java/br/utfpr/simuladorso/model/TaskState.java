package br.utfpr.simuladorso.model;

/*
 * Estados possiveis de uma tarefa no ciclo de vida da simulacao.
 */
public enum TaskState {

    // Tarefa ainda nao chegou ao sistema.
    NEW,

    // Tarefa chegou e aguarda CPU na fila de prontos.
    READY,

    // Tarefa esta executando em alguma CPU.
    RUNNING,

    // Tarefa foi suspensa manualmente ou por algum evento futuro.
    BLOCKED,

    // Tarefa concluiu todo o seu tempo de execucao.
    FINISHED
}

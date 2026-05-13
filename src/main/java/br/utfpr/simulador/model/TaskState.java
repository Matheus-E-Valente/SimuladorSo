package br.utfpr.simulador.model;

/*
 * Representa todos os estados possíveis de uma tarefa
 * dentro do sistema operacional.
 */

public enum TaskState {
    // Tarefa criada mas ainda não entrou no sistema
    NEW,

    // Tarefa pronta para executar
    READY,

    // Tarefa atualmente executando
    RUNNING,

    // Tarefa bloqueada/suspensa
    BLOCKED,

    // Tarefa finalizada
    FINISHED
}

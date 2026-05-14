package br.utfpr.simuladorso.simulation;

import br.utfpr.simuladorso.model.TaskControlBlock;

import java.util.List;

/*
 * Classe auxiliar para calculo e impressao de metricas da simulacao.
 *
 * No estado atual do projeto a interface grafica e o foco principal, mas esta
 * classe pode ser usada para mostrar turnaround, espera e resposta no console.
 */
public class Statistics {

    public static void print(
            List<TaskControlBlock> tasks,
            int totalTicks,
            int cpuCount
    ) {

        System.out.println("===== RESULTADOS =====");

        for (TaskControlBlock task : tasks) {

            int turnaround =
                    task.getFinishTime()
                            - task.getArrivalTime();

            int waiting =
                    turnaround
                            - task.getDuration();

            int response =
                    task.getStartTime()
                            - task.getArrivalTime();

            System.out.println();

            System.out.println(
                    "Task " + task.getId()
            );

            System.out.println(
                    "Turnaround: " + turnaround
            );

            System.out.println(
                    "Waiting: " + waiting
            );

            System.out.println(
                    "Response: " + response
            );
        }
    }
}

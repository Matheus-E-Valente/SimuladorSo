package br.utfpr.simuladorso.parser;

import br.utfpr.simuladorso.model.TaskControlBlock;
import br.utfpr.simuladorso.scheduler.PriorityScheduler;
import br.utfpr.simuladorso.scheduler.SRTFScheduler;
import br.utfpr.simuladorso.scheduler.Scheduler;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/*
 * Responsavel por carregar o arquivo de configuracao da simulacao.
 *
 * O formato esperado segue o enunciado:
 * algoritmo;quantum;qtde_cpus
 * id;cor;ingresso;duracao;prioridade;lista_eventos
 *
 * A lista de eventos ainda e ignorada neste projeto inicial, mas a linha e
 * aceita para manter compatibilidade com o formato pedido.
 */
public class ConfigParser {

    /*
     * Le o arquivo texto e transforma seus dados em objetos usados pelo
     * simulador: Scheduler, numero de CPUs, quantum e lista de TCBs.
     */
    public static SimulationConfig parse(
            String filePath
    ) throws Exception {

        BufferedReader reader =
                new BufferedReader(
                        new FileReader(filePath)
                );

        // Primeira linha: parametros gerais do sistema simulado.
        String firstLine =
                reader.readLine();

        String[] systemData =
                firstLine.split(";");

        String algorithm =
                systemData[0]
                        .trim()
                        .toUpperCase();

        int quantum =
                Integer.parseInt(
                        systemData[1].trim()
                );
        int cpuCount =
                Integer.parseInt(
                        systemData[2].trim()
                );

        if (cpuCount < 2) {
            throw new IllegalArgumentException(
                    "A simulacao precisa de pelo menos 2 CPUs."
            );
        }

        // Cria o escalonador indicado pelo usuario.
        Scheduler scheduler;

        if (algorithm.equals("SRTF")) {
            scheduler = new SRTFScheduler();
        }
        else if (algorithm.equals("PRIOP")) {
            scheduler = new PriorityScheduler();
        }
        else {
            throw new IllegalArgumentException(
                    "Algoritmo invalido: " + algorithm
            );
        }

        // Linhas seguintes: uma tarefa por linha.
        List<TaskControlBlock> tasks =
                new ArrayList<>();

        String line;

        while ((line = reader.readLine()) != null) {

            if (line.trim().isEmpty()) {
                continue;
            }

            String[] data = line.split(";");

            if (data.length < 5) {

                System.out.println(
                        "Linha invalida no config.txt:"
                );

                System.out.println(line);

                continue;
            }

            int id = Integer.parseInt(
                    data[0].trim()
            );

            Color color =
                    Color.decode(
                            "#" + data[1].trim()
                    );

            int arrival = Integer.parseInt(
                    data[2].trim()
            );

            int duration = Integer.parseInt(
                    data[3].trim()
            );

            int priority = Integer.parseInt(
                    data[4].trim()
            );

            tasks.add(
                    new TaskControlBlock(
                            id,
                            color,
                            arrival,
                            duration,
                            priority
                    )
            );
        }

        reader.close();

        return new SimulationConfig(
                scheduler,
                quantum,
                cpuCount,
                tasks
        );
    }
}

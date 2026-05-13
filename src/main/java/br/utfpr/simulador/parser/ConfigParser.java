package br.utfpr.simulador.parser;

import br.utfpr.simulador.model.TaskControlBlock;
import br.utfpr.simulador.scheduler.PriorityScheduler;
import br.utfpr.simulador.scheduler.SRTFScheduler;
import br.utfpr.simulador.scheduler.Scheduler;
import br.utfpr.simulador.simulation.SimulationConfig;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/*
 * Responsável por carregar o arquivo
 * de configuração da simulação.
 */
public class ConfigParser {

    /*
     * Lê arquivo TXT e cria configuração.
     */
    public SimulationConfig parse(
            String filePath
    ) throws Exception {

        BufferedReader reader =
                new BufferedReader(
                        new FileReader(filePath)
                );

        // =========================
        // LER PRIMEIRA LINHA
        // =========================

        String firstLine =
                reader.readLine();

        String[] systemData =
                firstLine.split(";");

        String algorithm =
                systemData[0]
                        .trim()
                        .toUpperCase();


        int quantum =
                Integer.parseInt(systemData[1]);
        int cpuCount =
                Integer.parseInt(systemData[2]);

        // =========================
        // CRIAR SCHEDULER
        // =========================

        Scheduler scheduler;

        if (algorithm.equals("SRTF")) {
            scheduler = new SRTFScheduler();
        }
        else {
            scheduler = new PriorityScheduler();
        }

        // =========================
        // LER TAREFAS
        // =========================

        List<TaskControlBlock> tasks =
                new ArrayList<>();

        String line;

        while ((line = reader.readLine()) != null) {

            // ignora linhas vazias
            if (line.trim().isEmpty()) {
                continue;
            }

            String[] data = line.split(";");

            if (data.length < 5) {

                System.out.println(
                        "Linha inválida no config.txt:"
                );

                System.out.println(line);

                continue;
            }

            int id = Integer.parseInt(data[0]);

            String color = data[1];

            int arrival = Integer.parseInt(data[2]);

            int duration = Integer.parseInt(data[3]);

            int priority = Integer.parseInt(data[4]);

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

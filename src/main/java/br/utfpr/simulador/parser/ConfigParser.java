package br.utfpr.simulador.parser;

import br.utfpr.simulador.model.TaskControlBlock;
import br.utfpr.simulador.scheduler.PriorityScheduler;
import br.utfpr.simulador.scheduler.SRTFScheduler;
import br.utfpr.simulador.scheduler.Scheduler;
import br.utfpr.simulador.simulation.SimulationConfig;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
    ) throws IOException {

        BufferedReader reader =
                new BufferedReader(
                        new FileReader(filePath)
                );

        // =========================
        // LER PRIMEIRA LINHA
        // =========================

        String firstLine =
                reader.readLine();

        String[] systemParts =
                firstLine.split(";");

        String schedulerName =
                systemParts[0]
                        .trim()
                        .toUpperCase();

        int quantum =
                Integer.parseInt(
                        systemParts[1].trim()
                );

        int cpuCount =
                Integer.parseInt(
                        systemParts[2].trim()
                );

        // =========================
        // CRIAR SCHEDULER
        // =========================

        Scheduler scheduler;

        switch (schedulerName) {

            case "SRTF":
                scheduler =
                        new SRTFScheduler();
                break;

            case "PRIOP":
                scheduler =
                        new PriorityScheduler();
                break;

            default:
                throw new IllegalArgumentException(
                        "Algoritmo inválido: "
                                + schedulerName
                );
        }

        // =========================
        // LER TAREFAS
        // =========================

        List<TaskControlBlock> tasks =
                new ArrayList<>();

        String line;

        while ((line = reader.readLine())
                != null) {

            // Ignora linhas vazias
            if (line.trim().isEmpty()) {
                continue;
            }

            String[] parts =
                    line.split(";");

            int id =
                    Integer.parseInt(
                            parts[0].trim()
                    );

            String color =
                    parts[1].trim();

            int arrival =
                    Integer.parseInt(
                            parts[2].trim()
                    );

            int duration =
                    Integer.parseInt(
                            parts[3].trim()
                    );

            int priority =
                    Integer.parseInt(
                            parts[4].trim()
                    );

            TaskControlBlock task =
                    new TaskControlBlock(
                            id,
                            color,
                            arrival,
                            duration,
                            priority
                    );

            tasks.add(task);
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

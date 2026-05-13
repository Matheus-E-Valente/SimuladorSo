package br.utfpr.simulador;

import br.utfpr.simulador.model.TaskState;
import br.utfpr.simulador.parser.ConfigParser;
import br.utfpr.simulador.simulation.SimulationConfig;
import br.utfpr.simulador.simulation.SimulationEngine;
import br.utfpr.simulador.ui.SimulatorWindow;

/*
 * Classe principal do simulador.
 */

public class Main {

    public static void main(String[] args) {

        try {

            ConfigParser parser =
                    new ConfigParser();

            SimulationConfig config =
                    parser.parse("config.txt");

            SimulationEngine engine =
                    new SimulationEngine(
                            config.getTasks(),
                            config.getCpuCount(),
                            config.getScheduler()
                    );

            SimulatorWindow window =
                    new SimulatorWindow(engine);

            window.setVisible(true);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}

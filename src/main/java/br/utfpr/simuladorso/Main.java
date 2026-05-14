package br.utfpr.simuladorso;

import br.utfpr.simuladorso.parser.ConfigParser;
import br.utfpr.simuladorso.parser.SimulationConfig;
import br.utfpr.simuladorso.simulation.SimulationEngine;
import br.utfpr.simuladorso.ui.SimulatorWindow;

import java.io.File;
import java.net.URISyntaxException;

/*
 * Ponto de entrada da aplicacao.
 *
 * O Main carrega o arquivo de configuracao, cria o motor da simulacao e abre a
 * janela grafica. A regra de simulacao fica nas outras classes para manter este
 * ponto de entrada simples.
 */
public class Main {

    public static void main(String[] args) {

        try {

            SimulationConfig config =
                    ConfigParser.parse(
                            findConfigFile()
                    );

            SimulationEngine engine =
                    new SimulationEngine(
                            config.getTasks(),
                            config.getCpuCount(),
                            config.getScheduler(),
                            config.getQuantum()
                    );

            new SimulatorWindow(engine);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    /*
     * Procura o config.txt em locais comuns para execucao fora da IDE.
     *
     * Na IDE, o arquivo costuma estar no diretorio do projeto. No JAR ou no
     * app gerado por jpackage, ele pode estar ao lado do binario ou na pasta
     * principal do aplicativo.
     */
    private static String findConfigFile()
            throws URISyntaxException {

        File currentDirectoryConfig =
                new File("config.txt");

        if (currentDirectoryConfig.exists()) {
            return currentDirectoryConfig.getPath();
        }

        File codeLocation =
                new File(
                        Main.class
                                .getProtectionDomain()
                                .getCodeSource()
                                .getLocation()
                                .toURI()
                );

        File codeDirectory =
                codeLocation.isDirectory()
                        ? codeLocation
                        : codeLocation.getParentFile();

        File nextToJar =
                new File(
                        codeDirectory,
                        "config.txt"
                );

        if (nextToJar.exists()) {
            return nextToJar.getPath();
        }

        File appDirectory =
                codeDirectory.getParentFile();

        if (appDirectory != null) {
            File nextToApp =
                    new File(
                            appDirectory,
                            "config.txt"
                    );

            if (nextToApp.exists()) {
                return nextToApp.getPath();
            }
        }

        return currentDirectoryConfig.getPath();
    }
}

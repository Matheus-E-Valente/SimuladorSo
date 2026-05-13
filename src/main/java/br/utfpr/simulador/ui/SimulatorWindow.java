package br.utfpr.simulador.ui;

import br.utfpr.simulador.model.CPU;
import br.utfpr.simulador.model.TaskControlBlock;
import br.utfpr.simulador.simulation.SimulationEngine;

import javax.swing.*;
import java.awt.*;

/*
 * Janela principal do simulador.
 */
public class SimulatorWindow extends JFrame {

    // Motor da simulação
    private SimulationEngine engine;

    // Label do tick atual
    private JLabel tickLabel;

    // Área de texto CPUs
    private JTextArea cpuArea;

    // Área de texto Tasks
    private JTextArea taskArea;

    // Painel do grafico de gantt
    private GanttPanel ganttPanel;

    /*
     * Construtor da janela.
     */
    public SimulatorWindow(
            SimulationEngine engine
    ) {

        this.engine = engine;

        setTitle(
                "Simulador de Sistema Operacional"
        );

        setSize(800, 600);

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        setLocationRelativeTo(null);

        initializeComponents();

        refreshScreen();
    }

    /*
     * Cria componentes da interface.
     */
    private void initializeComponents() {

        // =====================================
        // LAYOUT PRINCIPAL
        // =====================================

        setLayout(new BorderLayout());

        // =====================================
        // TOPO
        // =====================================

        JPanel topPanel =
                new JPanel();

        tickLabel =
                new JLabel("Tick: 0");

        topPanel.add(tickLabel);

        add(topPanel, BorderLayout.NORTH);

        // =====================================
        // CENTRO
        // =====================================

        JPanel centerPanel =
                new JPanel(
                        new GridLayout(1, 2)
                );

        ganttPanel =
                new GanttPanel(engine);

        add(
                new JScrollPane(ganttPanel),
                BorderLayout.EAST
        );

        cpuArea = new JTextArea();

        taskArea = new JTextArea();

        cpuArea.setEditable(false);

        taskArea.setEditable(false);

        centerPanel.add(
                new JScrollPane(cpuArea)
        );

        centerPanel.add(
                new JScrollPane(taskArea)
        );

        add(centerPanel, BorderLayout.CENTER);

        // =====================================
        // BOTÕES
        // =====================================

        JPanel buttonPanel =
                new JPanel();

        JButton nextButton =
                new JButton("NEXT");

        JButton previousButton =
                new JButton("PREVIOUS");

        JButton runButton =
                new JButton("RUN");

        buttonPanel.add(nextButton);

        buttonPanel.add(previousButton);

        buttonPanel.add(runButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // =====================================
        // EVENTOS BOTÕES
        // =====================================

        nextButton.addActionListener(e -> {

            engine.nextStep();

            refreshScreen();
        });

        previousButton.addActionListener(e -> {

            engine.previousStep();

            refreshScreen();
        });

        runButton.addActionListener(e -> {

            engine.runUntilEnd();

            refreshScreen();
        });
    }

    /*
     * Atualiza informações na tela.
     */
    private void refreshScreen() {

        // =====================================
        // TICK
        // =====================================

        tickLabel.setText(
                "Tick: " +
                        engine.getCurrentTick()
        );

        // =====================================
        // CPUs
        // =====================================

        StringBuilder cpuText =
                new StringBuilder();

        cpuText.append("CPUS\n\n");

        for (CPU cpu :
                engine.getCpus()) {

            cpuText.append(cpu);

            cpuText.append("\n");
        }

        cpuArea.setText(
                cpuText.toString()
        );

        // =====================================
        // TASKS
        // =====================================

        StringBuilder taskText =
                new StringBuilder();

        taskText.append("TASKS\n\n");

        for (TaskControlBlock task :
                engine.getTasks()) {

            taskText.append(task);

            taskText.append("\n\n");
        }

        taskArea.setText(
                taskText.toString()
        );

        // =====================================
        // ATUALIZA GANTT
        // =====================================

        ganttPanel.repaint();
    }
}

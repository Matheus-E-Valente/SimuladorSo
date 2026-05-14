package br.utfpr.simuladorso.ui;

import br.utfpr.simuladorso.model.TaskControlBlock;
import br.utfpr.simuladorso.model.TaskState;
import br.utfpr.simuladorso.simulation.SimulationEngine;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;

/*
 * Janela principal do simulador.
 *
 * A interface concentra os controles pedidos no trabalho: avancar, voltar,
 * executar ate o fim, exportar PNG e alterar manualmente o estado de uma
 * tarefa em qualquer ponto da simulacao.
 */
public class SimulatorWindow extends JFrame {

    public SimulatorWindow(
            SimulationEngine engine
    ) {

        setTitle("Simulador SO");

        setSize(1400, 800);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        GanttPanel ganttPanel =
                new GanttPanel(engine);

        add(ganttPanel, BorderLayout.CENTER);

        JPanel buttonPanel =
                new JPanel();

        JButton nextButton =
                new JButton("NEXT");

        JButton backButton =
                new JButton("BACK");

        JButton runButton =
                new JButton("RUN");

        JButton exportButton =
                new JButton("EXPORT PNG");

        JComboBox<Integer> taskBox =
                new JComboBox<>();

        for (TaskControlBlock task :
                engine.getTasks()) {

            taskBox.addItem(task.getId());
        }

        JComboBox<TaskState> stateBox =
                new JComboBox<>(
                        TaskState.values()
                );

        JButton applyButton =
                new JButton(
                        "ALTERAR ESTADO"
                );

        // Avanca exatamente um tick e atualiza a visualizacao passo a passo.
        nextButton.addActionListener(e -> {

            engine.nextStep();

            ganttPanel.repaint();
        });

        // Restaura o snapshot anterior salvo pelo motor.
        backButton.addActionListener(e -> {

            engine.previousStep();

            ganttPanel.repaint();
        });

        /*
         * Executa a simulacao completa. Ao final, o grafico final e exportado
         * automaticamente para atender ao requisito do arquivo de imagem.
         */
        runButton.addActionListener(e -> {

            engine.runUntilEnd();

            ganttPanel.repaint();

            ganttPanel.exportPNG(
                    "gantt.png"
            );
        });

        // Permite exportar a imagem manualmente em qualquer momento.
        exportButton.addActionListener(e -> {

            ganttPanel.exportPNG(
                    "gantt.png"
            );
        });

        // Altera o estado atual da tarefa escolhida pelo usuario.
        applyButton.addActionListener(e -> {

            Integer taskId =
                    (Integer)
                            taskBox.getSelectedItem();

            TaskState state =
                    (TaskState)
                            stateBox.getSelectedItem();

            engine.changeTaskState(
                    taskId,
                    state
            );

            ganttPanel.repaint();
        });

        buttonPanel.add(nextButton);
        buttonPanel.add(backButton);
        buttonPanel.add(runButton);
        buttonPanel.add(exportButton);
        buttonPanel.add(taskBox);
        buttonPanel.add(stateBox);
        buttonPanel.add(applyButton);

        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }
}

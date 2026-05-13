package br.utfpr.simulador.ui;

import br.utfpr.simulador.model.TaskControlBlock;
import br.utfpr.simulador.model.TaskState;
import br.utfpr.simulador.simulation.SimulationEngine;
import br.utfpr.simulador.simulation.SimulationSnapshot;
import br.utfpr.simulador.simulation.TaskSnapshot;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/*
 * Painel responsável por desenhar
 * o gráfico de Gantt.
 *
 * Eixo Y -> tarefas
 * Eixo X -> tempo (ticks)
 */
public class GanttPanel extends JPanel {

    // Motor da simulação
    private SimulationEngine engine;

    // Tamanho das células
    private static final int CELL_WIDTH = 40;

    private static final int CELL_HEIGHT = 40;

    // Margens
    private static final int LEFT_MARGIN = 100;

    private static final int TOP_MARGIN = 40;

    public GanttPanel(
            SimulationEngine engine
    ) {

        this.engine = engine;

        setBackground(Color.WHITE);

        setPreferredSize(
                new Dimension(1400, 800)
        );
    }

    @Override
    protected void paintComponent(
            Graphics g
    ) {

        super.paintComponent(g);

        Graphics2D g2 =
                (Graphics2D) g;

        drawGantt(g2);
    }

    /*
     * Desenha gráfico completo.
     */
    private void drawGantt(
            Graphics2D g2
    ) {

        List<SimulationSnapshot> history =
                engine.getHistory();

        // =====================================
        // COPIA E ORDENA TASKS
        // =====================================

        List<TaskControlBlock> tasks =
                new ArrayList<>(
                        engine.getTasks()
                );

        tasks.sort(
                Comparator.comparingInt(
                        TaskControlBlock::getId
                )
        );

        // =====================================
        // DESENHAR TASKS
        // =====================================

        for (int taskIndex = 0;
             taskIndex < tasks.size();
             taskIndex++) {

            TaskControlBlock task =
                    tasks.get(taskIndex);

            /*
             * Menor ID mais perto do eixo X
             */
            int y =
                    TOP_MARGIN +
                            (tasks.size() - 1 - taskIndex)
                                    * CELL_HEIGHT;

            // =================================
            // LABEL TASK
            // =================================

            g2.setColor(Color.BLACK);

            g2.drawString(
                    "T" + task.getId(),
                    40,
                    y + 25
            );

            // =================================
            // DESENHAR TICKS
            // =================================

            for (int tick = 0;
                 tick < history.size();
                 tick++) {

                SimulationSnapshot snapshot =
                        history.get(tick);

                TaskSnapshot taskSnapshot =
                        getTaskSnapshot(
                                snapshot,
                                task.getId()
                        );

                if (taskSnapshot == null) {
                    continue;
                }

                int x =
                        LEFT_MARGIN +
                                tick * CELL_WIDTH;

                drawTaskCell(
                        g2,
                        task,
                        taskSnapshot,
                        x,
                        y
                );
            }
        }

        // =====================================
        // DESENHAR EIXO X (TICKS)
        // =====================================

        int axisY =
                TOP_MARGIN +
                        tasks.size() * CELL_HEIGHT +
                        20;

        for (int tick = 0;
             tick < history.size();
             tick++) {

            int x =
                    LEFT_MARGIN +
                            tick * CELL_WIDTH;

            g2.setColor(Color.BLACK);

            g2.drawString(
                    String.valueOf(tick),
                    x + 10,
                    axisY
            );
        }
    }

    /*
     * Desenha uma célula da task.
     */
    private void drawTaskCell(
            Graphics2D g2,
            TaskControlBlock task,
            TaskSnapshot taskSnapshot,
            int x,
            int y
    ) {

        TaskState state =
                taskSnapshot.getState();

        // =====================================
        // RUNNING
        // =====================================

        if (state == TaskState.RUNNING) {

            g2.setColor(
                    getTaskColor(task)
            );

            g2.fillRect(
                    x,
                    y,
                    CELL_WIDTH,
                    CELL_HEIGHT
            );

            // Mostra CPU
            g2.setColor(Color.BLACK);

            g2.drawString(
                    "C" +
                            taskSnapshot.getCurrentCPU(),
                    x + 8,
                    y + 25
            );
        }

        // =====================================
        // READY
        // =====================================

        else if (state == TaskState.READY) {

            g2.setColor(Color.WHITE);

            g2.fillRect(
                    x,
                    y,
                    CELL_WIDTH,
                    CELL_HEIGHT
            );
        }

        // =====================================
        // BLOCKED
        // =====================================

        else if (state == TaskState.BLOCKED) {

            g2.setColor(Color.BLACK);

            g2.fillRect(
                    x,
                    y,
                    CELL_WIDTH,
                    CELL_HEIGHT
            );
        }

        // =====================================
        // FINISHED
        // =====================================

        else if (state == TaskState.FINISHED) {

            g2.setColor(Color.GRAY);

            g2.fillRect(
                    x,
                    y,
                    CELL_WIDTH,
                    CELL_HEIGHT
            );
        }

        // =====================================
        // NEW
        // =====================================

        else {

            g2.setColor(Color.LIGHT_GRAY);

            g2.fillRect(
                    x,
                    y,
                    CELL_WIDTH,
                    CELL_HEIGHT
            );
        }

        // =====================================
        // BORDA
        // =====================================

        g2.setColor(Color.BLACK);

        g2.drawRect(
                x,
                y,
                CELL_WIDTH,
                CELL_HEIGHT
        );
    }

    /*
     * Busca snapshot da task.
     */
    private TaskSnapshot getTaskSnapshot(
            SimulationSnapshot snapshot,
            int taskId
    ) {

        for (TaskSnapshot taskSnapshot :
                snapshot.getTaskSnapshots()) {

            if (taskSnapshot.getId() == taskId) {

                return taskSnapshot;
            }
        }

        return null;
    }

    /*
     * Converte HEX da task para Color.
     */
    private Color getTaskColor(
            TaskControlBlock task
    ) {

        return Color.decode(
                "#" + task.getColor()
        );
    }

    /*
     * Exporta PNG do gantt
     */
    public void exportPNG(
            String fileName
    ) {

        BufferedImage image =
                new BufferedImage(
                        getWidth(),
                        getHeight(),
                        BufferedImage.TYPE_INT_ARGB
                );

        Graphics2D g2 =
                image.createGraphics();

        paint(g2);

        g2.dispose();

        try {

            ImageIO.write(
                    image,
                    "png",
                    new File(fileName)
            );

            System.out.println(
                    "PNG exportado: " +
                            fileName
            );

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}
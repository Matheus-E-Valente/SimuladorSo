package br.utfpr.simuladorso.ui;

import br.utfpr.simuladorso.event.EventType;
import br.utfpr.simuladorso.event.SimulationEvent;
import br.utfpr.simuladorso.model.TaskControlBlock;
import br.utfpr.simuladorso.model.TaskState;
import br.utfpr.simuladorso.simulation.CPUSnapshot;
import br.utfpr.simuladorso.simulation.SimulationEngine;
import br.utfpr.simuladorso.simulation.SimulationSnapshot;
import br.utfpr.simuladorso.simulation.TaskSnapshot;

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
/*
 * Painel responsavel por desenhar o grafico de Gantt.
 *
 * As linhas superiores representam tarefas ao longo dos ticks. As linhas
 * inferiores representam as CPUs, permitindo visualizar quando cada CPU esta
 * executando uma tarefa ou desligada.
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
    /*
     * Desenha o grafico completo a partir do historico salvo no motor.
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

        int cpuStartY =
                TOP_MARGIN +
                        tasks.size() * CELL_HEIGHT +
                        35;

        drawCpuStatusRows(
                g2,
                history,
                cpuStartY
        );

        // =====================================
        // DESENHAR EIXO X (TICKS)
        // =====================================

        int axisY =
                cpuStartY +
                        engine.getCpus().size() * CELL_HEIGHT +
                        25;

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

        drawLegend(
                g2,
                axisY + 30
        );
    }

    /*
     * Desenha uma célula da task.
     */
    /*
     * Desenha uma celula de tarefa para um tick.
     *
     * Cores seguem o enunciado: executando usa a cor da tarefa, pronto fica
     * branco, bloqueado fica preto e finalizado fica cinza.
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

        drawTaskEvents(
                g2,
                taskSnapshot,
                x,
                y
        );
    }

    /*
     * Desenha os icones dos eventos ocorridos no tick da celula.
     */
    private void drawTaskEvents(
            Graphics2D g2,
            TaskSnapshot taskSnapshot,
            int x,
            int y
    ) {

        SimulationSnapshot snapshot =
                engine.getHistory().get(
                        Math.max(
                                0,
                                (x - LEFT_MARGIN) / CELL_WIDTH
                        )
                );

        int offset = 0;

        for (SimulationEvent event :
                snapshot.getEvents()) {

            if (event.getTick() != snapshot.getTick() - 1
                    || event.getTaskId() != taskSnapshot.getId()) {
                continue;
            }

            drawEventIcon(
                    g2,
                    event.getType(),
                    x + 4 + offset,
                    y + 4
            );

            offset += 11;
        }
    }

    /*
     * Cada tipo de evento recebe um elemento grafico diferente para facilitar a
     * leitura do Gantt e cumprir o requisito da legenda.
     */
    private void drawEventIcon(
            Graphics2D g2,
            EventType type,
            int x,
            int y
    ) {

        if (type == EventType.TASK_ARRIVAL) {
            g2.setColor(new Color(0, 150, 70));
            int[] xs = {x, x + 9, x + 4};
            int[] ys = {y + 9, y + 9, y};
            g2.fillPolygon(xs, ys, 3);
        }
        else if (type == EventType.TASK_FINISHED) {
            g2.setColor(Color.RED);
            g2.drawLine(x, y, x + 9, y + 9);
            g2.drawLine(x + 9, y, x, y + 9);
        }
        else if (type == EventType.TASK_PREEMPTED) {
            g2.setColor(new Color(230, 140, 0));
            g2.fillRect(x + 3, y, 4, 10);
        }
        else if (type == EventType.RANDOM_TIE_BREAK) {
            g2.setColor(new Color(150, 60, 180));
            g2.fillOval(x, y, 10, 10);
            g2.setColor(Color.WHITE);
            g2.drawString("?", x + 2, y + 9);
        }
    }

    /*
     * Desenha linhas adicionais para cada CPU.
     *
     * Essas linhas deixam explicito se a CPU estava executando alguma tarefa ou
     * se foi desligada por falta de trabalho pronto.
     */
    private void drawCpuStatusRows(
            Graphics2D g2,
            List<SimulationSnapshot> history,
            int startY
    ) {

        for (int cpuIndex = 0;
             cpuIndex < engine.getCpus().size();
             cpuIndex++) {

            int y =
                    startY +
                            cpuIndex * CELL_HEIGHT;

            g2.setColor(Color.BLACK);
            g2.drawString(
                    "CPU " + cpuIndex,
                    35,
                    y + 25
            );

            for (int tick = 0;
                 tick < history.size();
                 tick++) {

                SimulationSnapshot snapshot =
                        history.get(tick);

                CPUSnapshot cpuSnapshot =
                        getCpuSnapshot(
                                snapshot,
                                cpuIndex
                        );

                int x =
                        LEFT_MARGIN +
                                tick * CELL_WIDTH;

                if (cpuSnapshot == null
                        || !cpuSnapshot.isPoweredOn()) {

                    g2.setColor(Color.DARK_GRAY);
                    g2.fillRect(
                            x,
                            y,
                            CELL_WIDTH,
                            CELL_HEIGHT
                    );

                    g2.setColor(Color.WHITE);
                    g2.drawString(
                            "OFF",
                            x + 7,
                            y + 25
                    );
                }
                else {

                    g2.setColor(new Color(220, 245, 230));
                    g2.fillRect(
                            x,
                            y,
                            CELL_WIDTH,
                            CELL_HEIGHT
                    );

                    if (cpuSnapshot.getRunningTaskId() != null) {
                        g2.setColor(Color.BLACK);
                        g2.drawString(
                                "T" + cpuSnapshot.getRunningTaskId(),
                                x + 9,
                                y + 25
                        );
                    }
                }

                g2.setColor(Color.BLACK);
                g2.drawRect(
                        x,
                        y,
                        CELL_WIDTH,
                        CELL_HEIGHT
                );
            }
        }
    }

    private CPUSnapshot getCpuSnapshot(
            SimulationSnapshot snapshot,
            int cpuId
    ) {

        for (CPUSnapshot cpuSnapshot :
                snapshot.getCpuSnapshots()) {

            if (cpuSnapshot.getCpuId() == cpuId) {
                return cpuSnapshot;
            }
        }

        return null;
    }

    /*
     * Desenha a legenda dos icones e da representacao de CPU desligada.
     */
    private void drawLegend(
            Graphics2D g2,
            int y
    ) {

        int x = LEFT_MARGIN;

        g2.setColor(Color.BLACK);
        g2.drawString(
                "Legenda:",
                x,
                y
        );

        drawEventIcon(g2, EventType.TASK_ARRIVAL, x + 70, y - 10);
        g2.setColor(Color.BLACK);
        g2.drawString("chegada", x + 85, y);

        drawEventIcon(g2, EventType.TASK_FINISHED, x + 155, y - 10);
        g2.setColor(Color.BLACK);
        g2.drawString("fim", x + 170, y);

        drawEventIcon(g2, EventType.TASK_PREEMPTED, x + 215, y - 10);
        g2.setColor(Color.BLACK);
        g2.drawString("preempcao", x + 230, y);

        drawEventIcon(g2, EventType.RANDOM_TIE_BREAK, x + 330, y - 10);
        g2.setColor(Color.BLACK);
        g2.drawString("sorteio", x + 345, y);

        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(x + 420, y - 12, 16, 12);
        g2.setColor(Color.BLACK);
        g2.drawRect(x + 420, y - 12, 16, 12);
        g2.drawString("CPU desligada", x + 445, y);
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
     * Retorna a cor configurada da task.
     */
    private Color getTaskColor(
            TaskControlBlock task
    ) {

        return task.getColor();
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

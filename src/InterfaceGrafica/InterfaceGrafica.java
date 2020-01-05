/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InterfaceGrafica;

import Utilidades.Torre;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import static javax.swing.JFrame.EXIT_ON_CLOSE;

/**
 *
 * @author escobar
 */
public class InterfaceGrafica {

    public InterfaceGrafica() {

    }

    public class MainFrame extends JFrame {

        /**
         *
         */
        private static final long serialVersionUID = 1L;
        public double maxValue;

        public MainFrame(String title, int widht, int height, double maxValue) {
            this.setTitle(title);
            this.maxValue = maxValue;
            this.setSize(widht, height);
            this.setDefaultCloseOperation(EXIT_ON_CLOSE);
            this.setVisible(true);
        }

    }

    public class MainPanel extends JPanel {

        /**
         *
         */
        private static final long serialVersionUID = 1L;
        private final int sourcePoint;
        public ArrayList<Torre> route;
        public double maxValue;
        private ArrayList<Torre> points;

        public class MainFrame extends JFrame {

            /**
             *
             */
            private static final long serialVersionUID = 1L;
            public double maxValue;

            public MainFrame(String title, int widht, int height, double maxValue) {
                this.setTitle(title);
                this.maxValue = maxValue;
                this.setSize(widht, height);
                this.setDefaultCloseOperation(EXIT_ON_CLOSE);
                this.setVisible(true);
            }

        }

        public MainPanel(ArrayList<Torre> points, ArrayList<Torre> route, int sourcePoint, double maxValue) {
            this.points = points;
            this.route = route;
            this.maxValue = maxValue;
            this.setBackground(Color.white);
            this.sourcePoint = sourcePoint;
        }

        @Override
        public void paintComponent(java.awt.Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setStroke(new BasicStroke(2));

            // do scaled ratio
            int H = super.getHeight();
            int W = super.getWidth();
            double scaleX = (double) W / this.maxValue;
            double scaleY = (double) H / this.maxValue;
            int pointSize = 6;

            // draw graph things
            for (int idx = 0; idx < this.points.size(); idx++) {
                // get coordinates scaled
                Torre d = this.points.get(idx);
                double x = d.x * scaleX;
                double y = d.y * scaleY;
                double r = d.raio;

                // draw points as a filled ellipse and radius as a line only ellipse
                if (idx == this.sourcePoint) {
                    int sourceBall = 20;
                    g2d.setColor(Color.blue);
                    g2d.fill(new Ellipse2D.Double(x - sourceBall / 2, y - sourceBall / 2, sourceBall, sourceBall));
                } else {
                    g2d.setColor(Color.BLACK);
                    g2d.fill(new Ellipse2D.Double(x - pointSize / 2, y - pointSize / 2, pointSize, pointSize));
                    g2d.draw(new Ellipse2D.Double(x - r, y - r, 2 * r, 2 * r));
                }
                /*
                // draw the point's number
                g.setColor(Color.black);
                g2d.drawString(String.valueOf(idx), (int) x, (  int) y);*/
            }

            // draw route things
            int size = this.route.size();
            for (int i = 0; i < size; i++) {
                // get scaled points coordinates at the center of the ellipses
                Torre p1 = this.route.get(i);
                Torre p2 = this.route.get((i + 1) % size);
                double x1 = p1.x * scaleX;
                double y1 = p1.y * scaleY;
                double x2 = p2.x * scaleX;
                double y2 = p2.y * scaleY;

                // draw a thick line from p1 to p2 with a small ellipse on point
                g2d.setColor(Color.red);
                g2d.setStroke(new BasicStroke(2));
                g2d.draw(new Line2D.Double(x1, y1, x2, y2));
                g2d.setColor(Color.black);
                g2d.fill(new Ellipse2D.Double(x2 - pointSize / 2, y2 - pointSize / 2, pointSize, pointSize));

            }

        }

    }

    public class InfoLabel extends JLabel implements Serializable {

        public int generation;
        public double bestFitness;
        public double generationFitnessMean;

        public InfoLabel(int generation, double bestFitness, double generationFitnessMean) {
            this.generation = generation;
            this.bestFitness = bestFitness;
            this.generationFitnessMean = generationFitnessMean;
        }

        public void updateLabel() {
            DecimalFormat dfd = new DecimalFormat("0000000.000");
            DecimalFormat dfi = new DecimalFormat("000000000000");
            this.setText("Generation = \t" + generation + "    Melhor caminho = \t" + dfd.format(bestFitness));
            this.setBackground(Color.black);
        }
    }

}

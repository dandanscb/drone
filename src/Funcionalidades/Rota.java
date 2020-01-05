/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Funcionalidades;

/**
 *
 * @author escobar
 */
import Utilidades.Torre;
import static Funcionalidades.Executar.numero_populacoes;
import static Funcionalidades.Executar.torre_inicial;
import InterfaceGrafica.InterfaceGrafica;
import InterfaceGrafica.InterfaceGrafica.MainFrame;
import InterfaceGrafica.InterfaceGrafica.MainPanel;
import java.awt.*;
import java.io.FileWriter;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

/**
 *
 */
public class Rota {

    private static final double taxa_mutacao = 0.0002;
    private static final double taxa_combinacao = 0.2;
    private static final int taxa_2opt = 2;

    private int tamanho_populacao;
    private int qnt_geracao;
    private int torreinicial;
    private Random random;
    private double melhorCombinacao;
    private double siginificativaCombinacao;
    private int bestIndex;
    private double constante;
    private ArrayList<ArrayList<Torre>> populacao;
    private ArrayList<Torre> torre;
    private ArrayList<Integer> combinacao;
    private ArrayList<Double> roleta;
    private ArrayList<Double> fitness;
    private InterfaceGrafica InterfaceGrafica = new InterfaceGrafica();
    private InterfaceGrafica.MainPanel mainPanel;
    private InterfaceGrafica.MainFrame mainFrame;
    private InterfaceGrafica.InfoLabel infoLabel;
    private double[][] data;
    private boolean confere;

    public Rota(int numero_populacoes, int torre_inicial, int geracoes, ArrayList<Torre> torre, InterfaceGrafica.MainFrame mainFrame, double RAIO) {
        this.tamanho_populacao = numero_populacoes;
        this.qnt_geracao = geracoes;
        this.torreinicial = torre_inicial;
        this.random = new Random(1);
        this.melhorCombinacao = 0.0;
        this.siginificativaCombinacao = 0.0;
        this.bestIndex = 0;
        this.constante = RAIO;

        this.populacao = new ArrayList<>();
        while (this.populacao.size() < this.tamanho_populacao) {
            this.populacao.add(new ArrayList<>());
        }

        this.torre = torre;
        this.combinacao = new ArrayList<>();
        this.roleta = new ArrayList<>();
        this.fitness = new ArrayList<>(this.tamanho_populacao);
        this.data = new double[this.qnt_geracao][2];

        this.mainPanel = InterfaceGrafica.new MainPanel(torre, torre, this.torreinicial, mainFrame.maxValue);
        this.infoLabel = InterfaceGrafica.new InfoLabel(0, 0.0, 0.0);
        mainFrame.add(this.mainPanel, BorderLayout.CENTER);
        mainFrame.add(infoLabel, BorderLayout.NORTH);
        this.mainFrame = mainFrame;
        mainFrame.revalidate();
    }

    public void buscaMenorCaminho() {
        geraSequenciaAleatoria();
        geraDescendente();
        //mutacao();
        mutacao2opt();
        processoDeSelecao();
        atualiza();
        this.data[0][0] = this.melhorCombinacao;
        this.data[0][1] = this.siginificativaCombinacao;
        this.mainPanel.repaint();

        for (int i = 0; i < 250; i++) {
            this.infoLabel.generation = i + 1;
            geraDescendente();
            //mutacao();
            mutacao2opt();
            processoDeSelecao();
            atualiza();
            this.mainPanel.route = this.populacao.get(this.bestIndex);
            this.infoLabel.generationFitnessMean = this.siginificativaCombinacao;
            this.infoLabel.bestFitness = this.melhorCombinacao;
            this.data[i][0] = this.melhorCombinacao;
            this.data[i][1] = this.siginificativaCombinacao;
            this.infoLabel.updateLabel();
            this.mainPanel.repaint();
        }
        //puxaPontos();
    }

    public void puxaPonto() {
        int countExit = 0;
        while (true) {
            int aleatorio = this.random.nextInt(this.torre.size());

            Torre selecionada = new Torre(this.populacao.get(this.bestIndex).get(aleatorio).x, this.populacao.get(this.bestIndex).get(aleatorio).y, this.populacao.get(this.bestIndex).get(aleatorio).raio, this.populacao.get(this.bestIndex).get(aleatorio).index);
            while (selecionada.index == this.torreinicial) {
                aleatorio = this.random.nextInt(this.torre.size());
                selecionada = this.populacao.get(this.bestIndex).get(aleatorio);
            }

            double tamanhoAnterior = chromosomeFitness(this.populacao.get(this.bestIndex));

            double r = (selecionada.raio / this.constante) * sqrt(random.nextDouble());
            double theta = random.nextDouble() * 2 * 3.1416;
            double x = r * cos(theta);
            double y = r * sin(theta);
            puxa(x, selecionada, true);
            puxa(y, selecionada, false);

            if (checa(this.populacao.get(this.bestIndex).get(aleatorio), tamanhoAnterior, selecionada)) {
                countExit = 0;
                this.mainPanel.route = this.populacao.get(this.bestIndex);
                this.infoLabel.updateLabel();
                this.mainFrame.revalidate();
                this.mainPanel.repaint();
            } else {
                despuxa(x, selecionada, true);
                despuxa(y, selecionada, false);
                countExit++;
                if (countExit > 10e4) {
                    break;
                }
            }
        }
    }

    public boolean checa(Torre backup, double tamanhoAntigo, Torre selecionada) {
        double tamanho = chromosomeFitness(this.populacao.get(this.bestIndex));
        if (tamanho > tamanhoAntigo || DistanciaEuclidiana(backup, selecionada) > backup.raio) {
            return false;
        }
        this.infoLabel.bestFitness = tamanho;
        return true;
    }

    public void puxa(double quantidade, Torre torre, boolean puxaX) {
        boolean side = this.random.nextBoolean();
        this.confere = side;
        if (puxaX) {
            if (side) {
                torre.x += quantidade;
            } else {
                torre.x -= quantidade;
            }
        } else {
            if (side) {
                torre.y += quantidade;
            } else {
                torre.y -= quantidade;
            }
        }
    }

    public void despuxa(double quantidade, Torre torre, boolean puxaX) {
        boolean side = !this.confere;
        if (puxaX) {
            if (side) {
                torre.x += quantidade;
            } else {
                torre.x -= quantidade;
            }
        } else {
            if (side) {
                torre.y += quantidade;
            } else {
                torre.y -= quantidade;
            }
        }
    }

    private void geraSequenciaAleatoria() {
        for (int i = 0; i < this.tamanho_populacao; i++) {
            ArrayList<Torre> chromosome = geraGenes();
            this.populacao.get(i).addAll(chromosome);
            this.fitness.add(chromosomeFitness(chromosome));
        }
    }

    private ArrayList<Torre> geraGenes() {
        ArrayList<Torre> gene = new ArrayList<>();
        ArrayList<Integer> escolhido = new ArrayList<>();

        for (int i = 0; i < this.torre.size(); i++) {
            escolhido.add(i);
        }

        for (int i = 0; i < this.torre.size(); i++) {
            int chosenIndex = escolhido.get(this.random.nextInt(escolhido.size()));
            escolhido.remove(escolhido.indexOf(chosenIndex));
            Torre chosen = this.torre.get(chosenIndex);
            gene.add(new Torre(chosen.x, chosen.y, chosen.raio, chosen.index));
        }

        return gene;
    }

    private double chromosomeFitness(ArrayList<Torre> path) {
        int size = path.size();
        double total = 0.0;

        for (int i = 0; i < size; i++) {
            Torre d1 = path.get(i % size);
            Torre d2 = path.get((i + 1) % size);
            total += DistanciaEuclidiana(d1, d2);
        }
        return total;
    }

    private double DistanciaEuclidiana(Torre d1, Torre d2) {
        return Math.sqrt(Math.pow(d1.x - d2.x, 2) + Math.pow(d1.y - d2.y, 2));
    }

    private void geraDescendente() {
        geraFilho();

        while (this.combinacao.size() > 0) {
            ArrayList<Torre> pai = this.populacao.get(this.combinacao.remove(this.random.nextInt(this.combinacao.size())));
            ArrayList<Torre> mae = this.populacao.get(this.combinacao.remove(this.random.nextInt(this.combinacao.size())));
            cruzamento(pai, mae);
        }
    }

    private void geraFilho() {
        this.combinacao.clear();

        while (this.combinacao.size() < this.tamanho_populacao * taxa_combinacao) {
            int candidate1 = this.random.nextInt(this.tamanho_populacao);
            int candidate2 = this.random.nextInt(this.tamanho_populacao);

            if (this.fitness.get(candidate1) < this.fitness.get(candidate2)) {
                if (!this.combinacao.contains(candidate1)) {
                    this.combinacao.add(candidate1);
                }
            } else {
                if (!this.combinacao.contains(candidate2)) {
                    this.combinacao.add(candidate2);
                }
            }
        }
    }

    private void cruzamento(ArrayList<Torre> pai, ArrayList<Torre> mae) {
        ArrayList<Torre> heranca1 = new ArrayList<>();
        ArrayList<Torre> heranca2 = new ArrayList<>();
        ArrayList<Torre> descendencia = new ArrayList<>();

        int startPoint = this.random.nextInt(this.torre.size());
        int endPoint = this.random.nextInt(this.torre.size());

        while (startPoint == this.torre.size() - 1) {
            startPoint = this.random.nextInt(this.torre.size());
        }

        while (endPoint <= startPoint) {
            endPoint = this.random.nextInt(this.torre.size());
        }

        gerandoDescendente(startPoint, endPoint, pai, mae, heranca1, heranca2);
        criaDescendencia(startPoint, descendencia, heranca2, heranca1);

        addGene(descendencia);

        heranca1 = new ArrayList<>();
        heranca2 = new ArrayList<>();
        descendencia = new ArrayList<>();

        gerandoDescendente(startPoint, endPoint, mae, pai, heranca1, heranca2);
        criaDescendencia(startPoint, descendencia, heranca2, heranca1);

        addGene(descendencia);
    }

    private void gerandoDescendente(int startPoint, int endPoint, ArrayList<Torre> pai, ArrayList<Torre> mae, ArrayList<Torre> heranca1, ArrayList<Torre> heranca2) {

        for (int i = startPoint; i < endPoint; i++) {
            Torre GenePai = pai.get(i);
            heranca1.add(new Torre(GenePai.x, GenePai.y, GenePai.raio, GenePai.index));
        }

        for (Torre torre1 : mae) {
            boolean cant = false;
            for (Torre torre2 : heranca1) {
                if (torre1.compareTo(torre2) > 0) {
                    cant = true;
                    break;
                }
            }
            if (!cant) {
                heranca2.add(new Torre(torre1.x, torre1.y, torre1.raio, torre1.index));
            }
        }
    }

    public void criaDescendencia(int startPoint, ArrayList<Torre> descendencia, ArrayList<Torre> heranca1, ArrayList<Torre> heranca2) {
        for (int i = 0; i < startPoint; i++) {
            descendencia.add(new Torre(heranca1.get(i).x, heranca1.get(i).y, heranca1.get(i).raio, heranca1.get(i).index));
        }

        for (Torre device : heranca2) {
            descendencia.add(new Torre(device.x, device.y, device.raio, device.index));
        }

        for (int i = startPoint; i < heranca1.size(); i++) {
            descendencia.add(new Torre(heranca1.get(i).x, heranca1.get(i).y, heranca1.get(i).raio, heranca1.get(i).index));
        }
    }

    public void addGene(ArrayList<Torre> gene) {
        this.fitness.add(chromosomeFitness(gene));
        this.populacao.add(gene);
    }

    private void mutacao() {
        ArrayList<Integer> mutacionar = new ArrayList<>();

        for (int i = 0; i < this.populacao.size() * taxa_mutacao; i++) {
            int index = this.random.nextInt(this.populacao.size());
            while (index == this.bestIndex || index < this.tamanho_populacao) {
                index = this.random.nextInt(this.populacao.size());
            }
            mutacionar.add(index);
        }

        for (int cromossomoIndex : mutacionar) {
            int torreIndex = selecionaTorre(-1);
            int torre2Index = selecionaTorre(torreIndex);
            trocaTorre(this.populacao.get(cromossomoIndex), torreIndex, torre2Index);
            this.fitness.set(cromossomoIndex, chromosomeFitness(this.populacao.get(cromossomoIndex)));
        }
    }

    private void mutacao2opt() {
        ArrayList<Integer> mutacionar = new ArrayList<>();

        for (int i = 0; i < this.populacao.size() * taxa_mutacao; i++) {
            int index = this.random.nextInt(this.populacao.size());
            while (index == this.bestIndex || index < this.tamanho_populacao) {
                index = this.random.nextInt(this.populacao.size());
            }
            mutacionar.add(index);
        }

        for (int cromossomoIndex : mutacionar) {
            TwoOpt(cromossomoIndex);
            this.fitness.set(cromossomoIndex, chromosomeFitness(this.populacao.get(cromossomoIndex)));
        }
    }

    private void TwoOpt(int escolhida) {
        // Get tour size
        //int size = _tour.TourSize();
        int size = this.populacao.get(escolhida).size();

        ArrayList<Torre> newTour = new ArrayList();

        newTour = copiaLista(this.populacao.get(escolhida));

        // repeat until no improvement is made 
        int improve = 0;
        int iteration = 0;

        while (improve < taxa_2opt) {
            //System.out.println("improve: "+improve);
            double best_distance = chromosomeFitness(this.populacao.get(escolhida));

            for (int i = 1; i < size - 1; i++) {
                for (int k = i + 1; k < size; k++) {
                    TwoOptSwap(i, k, newTour, escolhida);
                    iteration++;
                    double new_distance = chromosomeFitness(newTour);

                    if (new_distance < best_distance) {
                        improve = 0;

                        for (int j = 0; j < this.populacao.get(escolhida).size(); j++) {
                            this.populacao.get(escolhida).set(j, null);
                        }

                        for (int j = 0; j < this.populacao.get(escolhida).size(); j++) {
                            this.populacao.get(escolhida).set(j, newTour.get(j));
                        }

                        best_distance = new_distance;
                    }
                }
            }

            improve++;
        }
    }

    void TwoOptSwap(int i, int k, ArrayList<Torre> newTour, int escolhida) {
        int size = this.populacao.get(escolhida).size();

        // 1. take route[0] to route[i-1] and add them in order to new_route
        for (int c = 0; c <= i - 1; ++c) {
            //newTour.SetCity(c, _tour.GetCity(c));
            newTour.set(c, this.populacao.get(escolhida).get(c));
        }

        int dec = 0;
        for (int c = i; c <= k; ++c) {
            newTour.set(c, this.populacao.get(escolhida).get(k - dec));
            dec++;
        }

        for (int c = k + 1; c < size; ++c) {
            newTour.set(c, this.populacao.get(escolhida).get(c));
        }
    }

    public double calculaDistanciaRota(ArrayList<Torre> pontosRota) {
        double distanciaTotal = 0;
        for (int i = 0; i < pontosRota.size() - 1; i++) {
            distanciaTotal += DistanciaEuclidiana(pontosRota.get(i), pontosRota.get(i + 1));
        }
        distanciaTotal += DistanciaEuclidiana(pontosRota.get(pontosRota.size() - 1), pontosRota.get(0));//distancia do último ao primeiro
        return distanciaTotal;
    }

    public ArrayList copiaLista(ArrayList<Torre> a) {
        ArrayList<Torre> retorno = new ArrayList();
        for (Torre torre : a) {
            retorno.add(new Torre(torre.x, torre.y, torre.raio, torre.index));
        }
        return retorno;
    }

    private void trocaTorre(ArrayList<Torre> gene, int torre1index, int torre2index) {
        Torre device1 = gene.get(torre1index);
        Torre device2 = gene.get(torre2index);

        gene.set(torre1index, new Torre(device2.x, device2.y, device2.raio, device2.index));
        gene.set(torre2index, new Torre(device1.x, device1.y, device1.raio, device1.index));
    }

    private int selecionaTorre(int outro) {
        int torreIndex = this.random.nextInt(this.torre.size());
        while (torreIndex == outro) {
            torreIndex = this.random.nextInt(this.torre.size());
        }
        return torreIndex;
    }

    private void processoDeSelecao() {
        roleta();

        boolean[] escolhido = new boolean[this.populacao.size()];

        while (this.populacao.size() > this.tamanho_populacao) {
            double value = this.random.nextDouble();
            int index = -1;
            for (int j = 0; value >= 0; j++) {
                value -= this.roleta.get(j % this.roleta.size());
                index = j % this.roleta.size();
            }
            if (!escolhido[index] && index != this.bestIndex) {
                escolhido[index] = true;
                this.roleta.remove(index);
                this.populacao.remove(index);
                this.fitness.remove(index);
                if (index < this.bestIndex) {
                    this.bestIndex -= 1;
                }
            }
        }
    }

    private void roleta() {
        this.roleta.clear();

        double soma = 0.0;
        for (double value : this.fitness) {
            soma += value;
        }
        for (double value : this.fitness) {
            this.roleta.add(value / soma);
        }
    }

    private void atualiza() {
        double max = Double.MAX_VALUE;
        this.siginificativaCombinacao = 0.0;
        int idx = -1;
        for (int i = 0; i < this.populacao.size(); i++) {
            if (this.fitness.get(i) < max) {
                max = this.fitness.get(i);
                idx = i;
            }
            this.siginificativaCombinacao += this.fitness.get(i);
        }

        if (idx != this.bestIndex) {
            this.bestIndex = idx;
            this.melhorCombinacao = chromosomeFitness(this.populacao.get(idx));
        }
        this.siginificativaCombinacao = this.siginificativaCombinacao / this.tamanho_populacao;
    }

}

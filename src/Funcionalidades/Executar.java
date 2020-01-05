/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Funcionalidades;

import Utilidades.Torre;
import InterfaceGrafica.InterfaceGrafica;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author escobar
 */
public final class Executar {
    
    public static String arquivo_csv = "instancia_drone_100.csv";
    public static double constante = 5;
    public static int tamanhoTela = 12000;
    public static int torre_inicial = 0;
    public static int geracoes = 10000000;
    public static int numero_populacoes = 100;
    

    public static void main(String[] args) {
        Leitura fileReader = new Leitura(arquivo_csv, constante);
        ArrayList<Torre> torre = fileReader.getTorres();
        //ArrayList<Torre> torre = fileReader.readAll();
        InterfaceGrafica InterfaceGrafica = new InterfaceGrafica();
        InterfaceGrafica.MainFrame mainFrame = InterfaceGrafica.new MainFrame(arquivo_csv, 1000, 1000, tamanhoTela);
        mainFrame.setLayout(new BorderLayout());
        Rota rota = new Rota(numero_populacoes, torre_inicial, geracoes, torre, mainFrame, constante);
        rota.buscaMenorCaminho();
        //rota.puxaPonto();
    }

}
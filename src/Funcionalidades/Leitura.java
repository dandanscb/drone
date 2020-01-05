/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Funcionalidades;

import Utilidades.Torre;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author escobar
 */
public class Leitura {
    public static double CONSTANTE;
    String path;
    ArrayList<Torre> Torres = new ArrayList<>();
    //LinkedList<int[]> Arquivo = new LinkedList();
    int maior_vertice = 0;
    int menor_vertice = Integer.MAX_VALUE;
    int contador = 0;

    public Leitura(String path, double constante) {
        CONSTANTE = constante;
        this.path = path;
        lerTorres(path);

    }


    public void lerTorres(String arquivoCSV) {

        BufferedReader br = null;
        String linha = "";
        String csvDivisor = ",";
        int tamanho = 3;

        try {

            br = new BufferedReader(new FileReader(arquivoCSV));
            br.readLine();
            while ((linha = br.readLine()) != null) {
                if (linha.equals("")) {
                    break;
                }
                double[] coordenadas = new double[tamanho];
                String[] ler = linha.split(csvDivisor);

                coordenadas[0] = Double.parseDouble(ler[0]);
                coordenadas[1] = Double.parseDouble(ler[1]);
                coordenadas[2] = Double.parseDouble(ler[2]) * CONSTANTE;

                Torres.add(new Torre(coordenadas[0], coordenadas[1], coordenadas[2], this.contador));
                this.contador++;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public ArrayList<Torre> getTorres() {
        return Torres;
    }

}
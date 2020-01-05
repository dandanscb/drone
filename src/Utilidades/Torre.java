/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utilidades;

/**
 *
 * @author escobar
 */
public class Torre implements Comparable {
    public double x;
    public double y;
    public double raio;
    public int index;

    public Torre(double x, double y, double raio, int index) {
        this.x = x;
        this.y = y;
        this.raio = raio;
        this.index = index;
    }

    @Override
    public int compareTo(Object o) {
        Torre other = (Torre) o;
        return (this.x == other.x && this.y == other.y) ? 1 : 0;
    }
}
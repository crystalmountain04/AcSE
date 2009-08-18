package acse.oneDim.util;

/*
Program: AcSE - AcSE calculates the Schrödinger Equation
This Software provides the possibility to easily create your own quantum-mechanical simulations.

Copyright (C) 2009  Steffen Roland

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

/*
Klasse für die benötigten Matrix-Operationen;
Da hier keine tatsächliche Repräsentation einer Matrix vorliegt, ist
die Methode solveTriDi static.
*/
public class Matrix {
    /*löst Ax=b für tridiagonale Matrizen*/
    public static Complex[] solveTriDi(int rows, Complex[] m, Complex[] b) {
        Complex[] h = new Complex[rows-1];
        Complex[] p = new Complex[rows];
        Complex[] x = new Complex[rows];
        Complex l = new Complex(1,0);
        Complex r = new Complex(1,0);

        /*h = rechts von Diag nach Umformung*/
        h[0]=r.div(m[0]);
        for(int i=1;i<rows-1;i++) {
           h[i]=r.div(m[i].sub(l.mul(h[i-1])));
        }

        /*p = Ergebnisvektor nach Umformung*/
        p[0]=b[0].div(m[0]);
        for(int i=1;i<rows;i++) {
           p[i]=(b[i].sub(l.mul(p[i-1]))).div(m[i].sub(l.mul(h[i-1])));
        }
        
		/*Rückwärts laufenden die einzelnen x-Komponenten ausrechnen*/
        x[rows-1]=p[rows-1];
        for(int i=rows-2;i>=0;i--) {
           x[i]=p[i].sub(h[i].mul(x[i+1]));
        }

        return x;
    }
}

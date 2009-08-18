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
Diese Klasse soll ein physikalisches Konstanten-Set bereitstellen;
dieses ist angepasst auf die Simulationen im Zuge der Bachelorarbeit
*/
public class Constants {
    public double h;		/*reduziertes planchksches Wirkungsquantum*/
    public double m;		/*Masse des simulierten Teilchens*/
    public double k;		/*'Federkonstante'*/
    public double w;		/*Kreisfrequenz eines dazu passenden harmonischen Oszillators*/
    public double x0;		/*(mittlerer) Startort des Teilchens*/
    public double sig;		/*Zum harmonischen Oszillator passende Breite eine Gaußpakets*/
    public double delta;	/*frei wählbarer Parameter im 'atmenden' Gaußpaket*/

    public Constants() {
        this.x0=-4.0;
        this.h=2.0;
        this.m=1.0;
        this.k=10.0;
        this.w=Math.sqrt(k/m);
        this.sig=Math.sqrt(h/(2*m*w));
        this.delta=1.0;
    }
}

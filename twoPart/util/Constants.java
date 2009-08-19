package acse.twoPart.util;

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
    public double k1;		/*Impuls des 1. Teilchens*/
	public double k2;		/*Impuls des 2. Teilchens*/
    public double x0;		/*Startort des 1. Teilchens*/
	public double y0;		/*Startort des 2. Teilchens*/
    public double sig1;		/*Breite des 1. Teilchens*/
	public double sig2;		/*Breite des 2. Teilchens*/

    public Constants() {
        this.x0=-0.5;
		this.y0=0.5;
        this.h=2.0;
        this.k1=10.0;
		this.k2=10.0;
        this.sig1=0.56234132519034908039495103977648;
		this.sig2=0.56234132519034908039495103977648;
    }
}

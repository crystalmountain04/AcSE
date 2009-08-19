package acse.twoDim.util;

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
    public double k;		/*Impuls des Teilchens*/
    public double x0;		/*Startort in x-Richtung*/
	public double y0;		/*Startort in y-Richtung*/
    public double sig;		/*typische Breite eines Gaußpakets*/

    public Constants() {
        this.x0=4.0;
		this.y0=0.0;
        this.h=2.0;
        this.m=1.0;
        this.k=10.0;
        this.sig=0.56234132519034908039495103977648;
    }
}

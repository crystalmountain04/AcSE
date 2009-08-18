package acse.oneDim.interfaces;

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

import acse.oneDim.util.*;

/*
Dieses Interface legt fest, welche Methoden eine Initialisierungs-Klasse
implementieren muss
*/
public interface Solution {
    public void nextTimeStep();				/*Führt einen Zeitschritt aus (t=t+dt)*/
    public double getState(double x);		/*Liefert das Betragsquadrat der Wellenfunktion am Ort x*/
    public Complex getStatePsi(double x);	/*Liefert den (komplexen) Wert der Wellenfunktion am Ort x*/
}
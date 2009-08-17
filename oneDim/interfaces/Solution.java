package acse.oneDim.interfaces;

import acse.oneDim.util.*;

public interface Solution {
    public void nextTimeStep();
    public double getState(double x);
    public Complex getStatePsi(double x);
}
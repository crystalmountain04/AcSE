package acse.twoDim.interfaces;

import acse.twoDim.util.*;

public interface Solution {
    public void nextTimeStep();
    public double getState(double x, double y);
    public Complex getStatePsi(double x, double y);
}
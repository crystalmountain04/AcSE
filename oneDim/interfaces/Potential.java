package acse.oneDim.interfaces;

public interface Potential {
    public double getValue(double x);
    public void nextTimeStep();
}
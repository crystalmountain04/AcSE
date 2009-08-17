package acse.twoPart.interfaces;

public interface Potential {
    public double getValue(double x, double y);
    public void nextTimeStep();
}
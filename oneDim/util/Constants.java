package acse.oneDim.util;

public class Constants {
    public double h;
    public double m;
    public double k;
    public double w;
    public double wE;
    public double x0;
    public double sig;
    public double delta;
    public double q;
    public double E;

    public Constants() {
        this.x0=-4.0;
        this.h=2.0;
        this.m=1.0;
        this.k=10.0;
        this.w=Math.sqrt(k/m);
        this.wE=0.0001;
        this.sig=Math.sqrt(h/(2*m*w));
		System.out.println(sig);
        this.delta=1.0;
        this.q=1.0;
        this.E=10.0;
    }
}

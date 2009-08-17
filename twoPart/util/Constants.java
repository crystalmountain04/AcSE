package acse.twoPart.util;

public class Constants {
    public double h;
    public double m1;
	public double m2;
    public double k1;
	public double k2;
    public double w1;
	public double w2;
    public double wE;
    public double x0;
	public double y0;
    public double sig1;
	public double sig2;
    public double delta;
    public double q;
    public double E;

    public Constants() {
        this.x0=-0.5;
		this.y0=0.5;
        this.h=2.0;
        this.m1=1.0;
		this.m2=1.0;
        this.k1=10.0;
		this.k2=10.0;
        this.w1=Math.sqrt(k1/m1);
		this.w2=Math.sqrt(k2/m2);
        this.wE=0.0001;
        this.sig1=Math.sqrt(h/(2*m1*w1));
		this.sig2=Math.sqrt(h/(2*m2*w2));
		//this.m1=5.0; /*-------------------------------- ACHTUNG -----------------------------*/
		//this.m2=0.5;
        this.delta=1.0;
        this.q=1.0;
        this.E=10.0;
    }
}

package acse.oneDim.util;

public class Matrix {
    private Complex[][] a;
    private int rows;

    public Matrix(int rows) {
        a = new Complex[rows][rows];
        this.rows = rows;
    }

    public Matrix(int rows, Complex[][] c) {
        a = new Complex[rows][rows];
        this.rows = rows;
        int i,j;
        for(i=0;i<rows;i++) {
            for(j=0;j<rows;j++) {
                a[i][j]=c[i][j];
            }
        }
    }
    
    public int getRows() {
        return this.rows;
    }
    
    public int getColumns() {
        return this.rows;
    }

    public void setElement(int i, int j, Complex x) {
        this.a[i][j]=x;
    }

    public void printOut() {
        int i,j;
        for(i=0;i<rows;i++) {
            for(j=0;j<rows;j++) {
                a[i][j].printComplexNumber();
                System.out.print("\t");
            }
            System.out.print("\n");
        }
        System.out.println("\n--------------------------\n");
    }

    /*solves Ax=b*/
    public static Complex[] solveTriDi(int rows, Complex[] m, Complex[] b) {
        Complex[] h = new Complex[rows-1];
        Complex[] p = new Complex[rows];
        Complex[] x = new Complex[rows];
        Complex l = new Complex(1,0);
        Complex r = new Complex(1,0);

        //h = rechts von Diag nach Umformung
        h[0]=r.div(m[0]);
        for(int i=1;i<rows-1;i++) {
           h[i]=r.div(m[i].sub(l.mul(h[i-1])));
        }

        //p = Ergebnisvektor nach Umformung
        p[0]=b[0].div(m[0]);
        for(int i=1;i<rows;i++) {
           p[i]=(b[i].sub(l.mul(p[i-1]))).div(m[i].sub(l.mul(h[i-1])));
        }
        
        x[rows-1]=p[rows-1];
        for(int i=rows-2;i>=0;i--) {
           x[i]=p[i].sub(h[i].mul(x[i+1]));
        }

        return x;
    }
}

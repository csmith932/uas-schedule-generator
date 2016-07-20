package gov.faa.ang.swac.common.random.multivariatedists;

import java.util.Random;

import gov.faa.ang.swac.common.random.distributions.TriangularDistribution;

public class MVTriangular extends MVNormal{
	private Double min;
	private Double max;
	private Double mode;
	private int nVar;

	public MVTriangular(Double myMin, Double myMax, Double myMode, Double myCorrCoefM[][]){
        super(myCorrCoefM);
        min= myMin;
        max= myMax;
        mode = myMode;
        nVar = myCorrCoefM[0].length;
	}

	public Double[] mvNextDouble(Random rng) {
		Double T[] = new Double[nVar];
		Double U[] = new Double[nVar];
		Double N[] = new Double[nVar];
        N = super.mvNextDouble(rng);
        TriangularDistribution t = new TriangularDistribution(min, max, mode);
        for (int i = 0; i <= nVar - 1; i++){
        	U[i] = super.cdf01(N[i]);
        	T[i] = t.nextDouble(U[i]);
        }
        return T;
    }
}

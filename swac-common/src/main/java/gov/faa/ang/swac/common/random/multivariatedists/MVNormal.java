package gov.faa.ang.swac.common.random.multivariatedists;

import java.util.Random;

import gov.faa.ang.swac.common.random.distributions.NormalDistribution;

public class MVNormal extends NormalDistribution{
	
	// The MVNormal class derived from normal class generates a Multivariate Normal 
    // random vector with given means and variances for marginal distributions and 
    // also given correlation matrix. 
    // The algorithm is based on Cholesky Decomposition of the covariance matrix. 
    // The formulas are given in Luc Devroye's "Non-Uniform Random Variate Generation" (1986).
	
	private boolean NotPD;	// not positive definite
    private Double meanArray[]; // meanArray: nVar-array of means
    private Double varArray[]; // varArray: nVar-array of variances
    private Double stdevArray[]; // stdevarray: nVar-array of std deviations
    private Double CovM[][]; // CovM: nVar-by-nVar-Array containing covariance matrix
    private Double H[][]; // H: nVar-by-nVar-Array containing the lower triangular Cholesky factor
    private int nVar; //number of variables
    
    public boolean getNotPD(){
    	return this.NotPD;
    }
    
    public void setNotPD(boolean NotPD){
    	this.NotPD = NotPD;
    }
    
    //constructor when normal dists are not standard
    public MVNormal(Double myMeanArray[], Double myVarArray[], Double myCorrCoefM[][]){
    	meanArray = myMeanArray;
    	nVar = meanArray.length;
    	varArray = myVarArray;
    	Double stdevArray[] = new Double[nVar];
    	Double CovM[][] = new Double[nVar][nVar];
    	System.arraycopy(varArray, 0, stdevArray, 0, nVar);
    	for (int i = 0; i <= nVar - 1; i++){
    		stdevArray[i] = Math.sqrt(stdevArray[i]);
    		CovM[i][i] = varArray[i];
    	}
    	for (int i = 0; i <= nVar - 1; i++){
    		for (int j = i + 1; j <= nVar - 1; j++){
    			CovM[i][j] = myCorrCoefM[i][j] * stdevArray[i] * stdevArray[j];
    			CovM[j][i] = CovM[i][j];
    		}
    	}
        CholeskyFactor(CovM);
	}
    
    // constructor when normal dists are standard
    public MVNormal(Double myCorrCoefM[][]){
    	
        nVar = myCorrCoefM[0].length;
        meanArray = new Double[nVar];
        varArray = new Double[nVar];
        stdevArray = new Double[nVar];
        Double CovM[][] = new Double[nVar][nVar];
        for (int i = 0; i <= nVar - 1; i++){
        	meanArray[i] = 0.0;
            varArray[i] = 1.0;
            stdevArray[i] = 1.0;
            CovM[i][i] = varArray[i];
        }
        for (int i = 0; i <= nVar - 1; i++){
    		for (int j = i + 1; j <= nVar - 1; j++){
                CovM[i][j] = myCorrCoefM[i][j];
                CovM[j][i] = CovM[i][j];
    		}
        }
        CholeskyFactor(CovM);
	}
    
    // computes samplevector = Y = meanArray + HX to generate normal variables with desired correlations
	public Double[] mvNextDouble(Random rng) {
		
        Double Y[] = new Double[nVar];
        Double X[] = new Double[nVar];

        for (int i = 0; i <= nVar - 1; i++){
            X[i] = super.inverseF(rng.nextDouble());
            Y[i] = meanArray[i];
        }

        if (NotPD) {
        	for (int i = 0; i <= nVar - 1; i++){
        		Y[i] += X[i];
            }
        }
        else{
        	for (int i = 0; i <= nVar - 1; i++){
            	for (int j = 0; j <= i; j++){
                    Y[i] += H[i][j] * X[j];
            	}
            }
        }
		return Y;
	}
	
	//computes Cholesky factor H
	public void CholeskyFactor (Double[][] myCovM){
		double y,v;
		H = new Double[nVar][nVar];
		if (myCovM[0][0] <= 0) {
			NotPD = true;
			H = null;
			return;
		}
		H[0][0] = Math.sqrt(myCovM[0][0]);
		y = 1 / H[0][0];
		for (int i = 2; i <= nVar; i++) {
			H[i-1][0] = myCovM[i-1][0] * y;
		}
        for (int j = 2;j <= nVar;j++){
            v = myCovM[j-1][j-1];
            for (int m = 1; m <= j-1; m++){
                v -= Math.pow(H[j-1][m-1],2);
            }
            if (v <= 0){
                NotPD = true;
                H = null;
                return;
            }
            v = Math.sqrt(v);
            y = 1 / v;
            H[j-1][j-1] = v;
            for (int i = j+1; i <= nVar; i++){
                v = myCovM[i-1][j-1];
                for (int m = 1; m <= j-1; m++){ 
                    v -= H[i-1][m-1] * H[j-1][m-1];
                }
                H[i-1][j-1] = v * y;
            }
        }
        NotPD = false;
	}
}

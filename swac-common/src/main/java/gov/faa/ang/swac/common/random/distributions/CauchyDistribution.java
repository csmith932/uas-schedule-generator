package gov.faa.ang.swac.common.random.distributions;

import java.util.List;
import java.util.Random;

/**
 * Extends the class {@link ContinuousDistribution} for
 * the <EM>Cauchy</EM> distribution
 * with location parameter <SPAN CLASS="MATH"><I>&#945;</I></SPAN>
 * and scale parameter <SPAN CLASS="MATH"><I>&#946;</I> &gt; 0</SPAN>.
 * The density function is given by
 * 
 * <P></P>
 * <DIV ALIGN="CENTER" CLASS="mathdisplay">
 * <I>f</I> (<I>x</I>) = <I>&#946;</I>/(<I>&#960;</I>[(<I>x</I> - <I>&#945;</I>)<SUP>2</SUP> + <I>&#946;</I><SUP>2</SUP>]) for  - &#8734; &lt; <I>x</I> &lt; &#8734;.
 * </DIV><P></P>
 * The distribution function is
 * 
 * <P></P>
 * <DIV ALIGN="CENTER" CLASS="mathdisplay">
 * <I>F</I>(<I>x</I>) = 1/2 + arctan((<I>x</I> - <I>&#945;</I>)/<I>&#946;</I>)/<I>&#960;</I>,&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;for  - &#8734; &lt; <I>x</I> &lt; &#8734;,
 * </DIV><P></P>
 * and its inverse is
 * 
 * <P></P>
 * <DIV ALIGN="CENTER" CLASS="mathdisplay">
 * <I>F</I><SUP>-1</SUP>(<I>u</I>) = <I>&#945;</I> + <I>&#946;</I>tan(<I>&#960;</I>(<I>u</I> - 1/2)).&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;for 0 &lt; <I>u</I> &lt; 1.
 * </DIV><P></P>
 * 
 */
public class CauchyDistribution extends ContinuousDistribution {
   private double alpha;
   private double beta;

   /**
    * Constructs a <TT>CauchyDist</TT> object
    *    with parameters <SPAN CLASS="MATH"><I>&#945;</I> = 0</SPAN> and <SPAN CLASS="MATH"><I>&#946;</I> = 1</SPAN>.
    * 
    */
   public CauchyDistribution() {
      setParams (0.0, 1.0);
   }


   /**
    * Constructs a <TT>CauchyDist</TT> object with parameters
    *    <SPAN CLASS="MATH"><I>&#945;</I> =</SPAN> <TT>alpha</TT> and <SPAN CLASS="MATH"><I>&#946;</I> =</SPAN> <TT>beta</TT>.
    * 
    */
   public CauchyDistribution (double alpha, double beta) {
      setParams (alpha, beta);
   }


   public double density (double x) {
      return density (alpha, beta, x);
   }

   public double cdf (double x) {
      return cdf (alpha, beta, x);
   }

   public double barF (double x) {
      return barF (alpha, beta, x);
   }

   public double inverseF (double u){
      return inverseF (alpha, beta, u);
   }

   public double getMean() {
      return CauchyDistribution.getMean (alpha, beta);
   }

   public double getVariance() {
      return CauchyDistribution.getVariance (alpha, beta);
   }

   public double getStandardDeviation() {
      return CauchyDistribution.getStandardDeviation (alpha, beta);
   }

   /**
    * Computes the density function.
    * 
    */
   public static double density (double alpha, double beta, double x) {
      if (beta <= 0.0)
         throw new IllegalArgumentException ("beta <= 0");
      double t = (x - alpha)/beta;
      return 1.0/(beta * Math.PI*(1 + t*t));
   }


   /**
    * Computes the  distribution function.
    * 
    */
   public static double cdf (double alpha, double beta, double x) {
      // The integral was computed analytically using Mathematica
      if (beta <= 0.0)
         throw new IllegalArgumentException ("beta <= 0");
      double z = (x - alpha)/beta;
      if (z < -0.5)
         return Math.atan(-1.0/z)/Math.PI;
      return Math.atan(z)/Math.PI + 0.5;
   }


   /**
    * Computes the complementary distribution.
    * 
    */
   public static double barF (double alpha, double beta, double x) {
      if (beta <= 0.0)
         throw new IllegalArgumentException ("beta <= 0");
      double z = (x - alpha)/beta;
      if (z > 0.5)
         return Math.atan(1./z)/Math.PI;
      return 0.5 - Math.atan(z)/Math.PI;
   }


   /**
    * Computes the inverse of the distribution.
    * 
    */
   public static double inverseF (double alpha, double beta, double u) {
      if (beta <= 0.0)
         throw new IllegalArgumentException ("beta <= 0");
     if (u < 0.0 || u > 1.0)
        throw new IllegalArgumentException ("u must be in [0,1]");
     if (u <= 0.0)
        return Double.NEGATIVE_INFINITY;
     if (u >= 1.0)
        return Double.POSITIVE_INFINITY;
     if (u < 0.5)
        return alpha - 1.0/Math.tan (Math.PI*u) * beta;
     return alpha + Math.tan (Math.PI*(u - 0.5)) * beta;
   }

   /**
    * Throws an exception since the mean does not exist.
    * 
    * @exception UnsupportedOperationException the mean of the Cauchy distribution is undefined.
    * 
    * 
    */
   public static double getMean (double alpha, double beta) {
      if (beta <= 0.0)
         throw new IllegalArgumentException ("beta <= 0");

      throw new UnsupportedOperationException("Undefined mean");
   }


   /**
    * Returns <SPAN CLASS="MATH">&#8734;</SPAN> since the variance does not exist.
    * 
    * @return <SPAN CLASS="MATH">&#8734;</SPAN>.
    * 
    */
   public static double getVariance (double alpha, double beta) {
      if (beta <= 0.0)
         throw new IllegalArgumentException ("beta <= 0");

      return Double.POSITIVE_INFINITY;
   }


   /**
    * Returns <SPAN CLASS="MATH">&#8734;</SPAN> since the standard deviation does not exist.
    * 
    * @return <SPAN CLASS="MATH">&#8734;</SPAN>
    * 
    */
   public static double getStandardDeviation (double alpha, double beta) {
      return Double.POSITIVE_INFINITY;
   }


   /**
    * Returns the value of <SPAN CLASS="MATH"><I>&#945;</I></SPAN> for this object.
    * 
    */
   public double getAlpha() {
      return alpha;
   }


   /**
    * Returns the value of <SPAN CLASS="MATH"><I>&#946;</I></SPAN> for this object.
    * 
    */
   public double getBeta() {
      return beta;
   }



   /**
    * Sets the value of the parameters <SPAN CLASS="MATH"><I>&#945;</I></SPAN> and <SPAN CLASS="MATH"><I>&#946;</I></SPAN> for this object.
    * 
    */
   public void setParams (double alpha, double beta) {
      if (beta <= 0.0)
         throw new IllegalArgumentException ("beta <= 0");
      this.alpha = alpha;
      this.beta = beta;
   }


   /**
    * Return a table containing parameters of the current distribution.
    *    This table is put in regular order: [<SPAN CLASS="MATH"><I>&#945;</I></SPAN>, <SPAN CLASS="MATH"><I>&#946;</I></SPAN>].
    * 
    * 
    */
   public double[] getParams () {
      double[] retour = {alpha, beta};
      return retour;
   }


   public String toString () {
      return getClass().getSimpleName() + " : alpha = " + alpha + ", beta = " + beta;
   }


@Override
public double nextDouble(Random rng) {
	// TODO Auto-generated method stub
	return 0;
}


@Override
public DistributionType getDistributionType() {
	// TODO Auto-generated method stub
	return null;
}


@Override
public String getDistributionDescription() {
	// TODO Auto-generated method stub
	return null;
}


@Override
public List<String> getConfigurabeFields() {
	// TODO Auto-generated method stub
	return null;
}

}

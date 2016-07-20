package gov.faa.ang.swac.common.random.distributions;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
 * Class:        UniformDistribution
 * Description:  uniform distribution over the reals
 * Environment:  Java
 * Software:     SSJ 
 * Copyright (C) 2001  Pierre L'Ecuyer and Université de Montréal
 * Organization: DIRO, Université de Montréal
 * @author       
 * @since

 * SSJ is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License (GPL) as published by the
 * Free Software Foundation, either version 3 of the License, or
 * any later version.

 * SSJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * A copy of the GNU General Public License is available at
   <a href="http://www.gnu.org/licenses">GPL licence site</a>.
 */

/**
 * Extends the class {@link ContinuousDistribution} for
 * the <EM>uniform</EM> distribution
 * over the interval <SPAN CLASS="MATH">[<I>a</I>, <I>b</I>]</SPAN>.
 * Its density is
 * 
 * <P></P>
 * <DIV ALIGN="CENTER" CLASS="mathdisplay">
 * <I>f</I> (<I>x</I>) = 1/(<I>b</I> - <I>a</I>)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; for <I>a</I>&nbsp;&lt;=&nbsp;<I>x</I>&nbsp;&lt;=&nbsp;<I>b</I>
 * </DIV><P></P>
 * and 0 elsewhere.  The distribution function is 
 * 
 * <P></P>
 * <DIV ALIGN="CENTER" CLASS="mathdisplay">
 * <I>F</I>(<I>x</I>) = (<I>x</I> - <I>a</I>)/(<I>b</I> - <I>a</I>)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; for <I>a</I>&nbsp;&lt;=&nbsp;<I>x</I>&nbsp;&lt;=&nbsp;<I>b</I>
 * </DIV><P></P>
 * and its inverse is
 * 
 * <P></P>
 * <DIV ALIGN="CENTER" CLASS="mathdisplay">
 * <I>F</I><SUP>-1</SUP>(<I>u</I>) = <I>a</I> + (<I>b</I> - <I>a</I>)<I>u</I>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;for 0&nbsp;&lt;=&nbsp;<I>u</I>&nbsp;&lt;=&nbsp;1.
 * </DIV><P></P>
 * 
 */
public class UniformDistribution extends ContinuousDistribution {
   public void setMin(double min) {
		this.min = min;
	}


	public void setMax(double max) {
		this.max = max;
	}


private double min;
   private double max;


   /**
    * Constructs a uniform distribution over the interval 
    * <SPAN CLASS="MATH">(<I>a</I>, <I>b</I>) = (0, 1)</SPAN>.
    * 
    */
   public UniformDistribution() {
      setParams (0.0, 1.0);
   }
   

   /**
    * Constructs a uniform distribution over the interval <SPAN CLASS="MATH">(<I>a</I>, <I>b</I>)</SPAN>.
    * 
    */
   public UniformDistribution (double a, double b) {
      setParams (a, b);
   }

   public void setMin(String a){
	   this.min = Double.parseDouble(a);
   }
   
   public void setMax(String b){
	   this.max = Double.parseDouble(b);
   }
   
   
   
   public double getMin() {
	   return min;
   }

   public double getMax() {
	   return max;
   }


   public double density (double x) {
      return density (min, max, x);
   }
       
   public double cdf (double x) {
      return cdf (min, max, x);
   }
 
   public double barF (double x) {
      return barF (min, max, x);
   }
     
   public double nextDouble (Random rng) {
      return inverseF (min, max, rng.nextDouble());
   }

   public double getMean() {
      return UniformDistribution.getMean (min, max);
   }

   public double getVariance() {
      return UniformDistribution.getVariance (min, max);
   }

   public double getStandardDeviation() {
      return UniformDistribution.getStandardDeviation (min, max);
   }

   /**
    * Computes the uniform density function 
    *  <SPAN CLASS="MATH"><I>f</I> (<I>x</I>)</SPAN>.
    * 
    */
   public static double density (double a, double b, double x) {
      if (b <= a)
         throw new IllegalArgumentException ("b <= a");
      if (x <= a || x >= b)
         return 0.0;
      return 1.0 / (b - a);
   }


   /**
    * Computes the uniform distribution function as in.
    * 
    */
   public static double cdf (double a, double b, double x) {
      if (b <= a)
        throw new IllegalArgumentException ("b <= a");
      if (x <= a)
         return 0.0;
      if (x >= b)
         return 1.0;
      return (x - a)/(b - a);
   }


   /**
    * Computes the uniform complementary distribution function
    *   
    * <SPAN CLASS="MATH">bar(F)(<I>x</I>)</SPAN>.
    * 
    */
   public static double barF (double a, double b, double x) {
      if (b <= a)
        throw new IllegalArgumentException ("b <= a");
      if (x <= a)
         return 1.0;
      if (x >= b)
         return 0.0;
      return (b - x)/(b - a);
   }


   /**
    * Computes the inverse of the uniform distribution function.
    * 
    */
   public static double inverseF (double a, double b, double u) {
       if (b <= a)
           throw new IllegalArgumentException ("b <= a");

       if (u > 1.0 || u < 0.0)
           throw new IllegalArgumentException ("u not in [0, 1]");

       if (u <= 0.0)
           return a;
       if (u >= 1.0)
           return b;
       return a + (b - a)*u;
   }


   /**
    * Estimates the parameter <SPAN CLASS="MATH">(<I>a</I>, <I>b</I>)</SPAN> of the uniform distribution
    *    using the maximum likelihood method, from the <SPAN CLASS="MATH"><I>n</I></SPAN> observations 
    *    <SPAN CLASS="MATH"><I>x</I>[<I>i</I>]</SPAN>, 
    * <SPAN CLASS="MATH"><I>i</I> = 0, 1,&#8230;, <I>n</I> - 1</SPAN>. The estimates are returned in a two-element
    *     array, in regular order: [<SPAN CLASS="MATH"><I>a</I></SPAN>, <SPAN CLASS="MATH"><I>b</I></SPAN>].
    * 
    * @param x the list of observations used to evaluate parameters
    * 
    *    @param n the number of observations used to evaluate parameters
    * 
    *    @return returns the parameters [<SPAN CLASS="MATH">hat(a)</SPAN>, <SPAN CLASS="MATH">hat(b)</SPAN>]
    * 
    */
   public static double[] getMLE (double[] x, int n) {
      if (n <= 0)
         throw new IllegalArgumentException ("n <= 0");

      double parameters[] = new double[2];
      parameters[0] = Double.POSITIVE_INFINITY;
      parameters[1] = Double.NEGATIVE_INFINITY;
      for (int i = 0; i < n; i++) {
         if (x[i] < parameters[0])
            parameters[0] = x[i];
         if (x[i] > parameters[1])
            parameters[1] = x[i];    
      }

      return parameters;
   }

   
   /**
    * Creates a new instance of a uniform distribution with parameters <SPAN CLASS="MATH"><I>a</I></SPAN> and <SPAN CLASS="MATH"><I>b</I></SPAN>
    *    estimated using the maximum likelihood method based on the <SPAN CLASS="MATH"><I>n</I></SPAN> observations
    *    <SPAN CLASS="MATH"><I>x</I>[<I>i</I>]</SPAN>, 
    * <SPAN CLASS="MATH"><I>i</I> = 0, 1,&#8230;, <I>n</I> - 1</SPAN>.
    * 
    * @param x the list of observations to use to evaluate parameters
    * 
    *    @param n the number of observations to use to evaluate parameters
    * 
    * 
    */
   public static UniformDistribution getInstanceFromMLE (double[] x, int n) {
      double parameters[] = getMLE (x, n);
      return new UniformDistribution (parameters[0], parameters[1]);
   }


   /**
    * Computes and returns the mean 
    * <SPAN CLASS="MATH"><I>E</I>[<I>X</I>] = (<I>a</I> + <I>b</I>)/2</SPAN>
    *    of the uniform distribution with parameters <SPAN CLASS="MATH"><I>a</I></SPAN> and <SPAN CLASS="MATH"><I>b</I></SPAN>.
    * 
    * @return the mean of the uniform distribution 
    * <SPAN CLASS="MATH"><I>E</I>[<I>X</I>] = (<I>a</I> + <I>b</I>)/2</SPAN>
    * 
    */
   public static double getMean (double a, double b) {
      if (b <= a)
         throw new IllegalArgumentException ("b <= a");

      return ((a + b) / 2);
   }


   /**
    * Computes and returns the variance 
    * <SPAN CLASS="MATH">Var[<I>X</I>] = (<I>b</I> - <I>a</I>)<SUP>2</SUP>/12</SPAN>
    *    of the uniform distribution with parameters <SPAN CLASS="MATH"><I>a</I></SPAN> and <SPAN CLASS="MATH"><I>b</I></SPAN>.
    * 
    * @return the variance of the uniform distribution 
    * <SPAN CLASS="MATH">Var[<I>X</I>] = (<I>b</I> - <I>a</I>)<SUP>2</SUP>/12</SPAN>
    * 
    */
   public static double getVariance (double a, double b) {
      if (b <= a)
         throw new IllegalArgumentException ("b <= a");

      return ((b - a) * (b - a) / 12);
   }


   /**
    * Computes and returns the standard deviation
    *    of the uniform distribution with parameters <SPAN CLASS="MATH"><I>a</I></SPAN> and <SPAN CLASS="MATH"><I>b</I></SPAN>.
    * 
    * @return the standard deviation of the uniform distribution
    * 
    */
   public static double getStandardDeviation (double a, double b) {
      return Math.sqrt (UniformDistribution.getVariance (a, b));
   }
      

   /**
    * Returns the parameter <SPAN CLASS="MATH"><I>a</I></SPAN>.
    * 
    */
   public double getA() {
      return min;
   }
 

   /**
    * Returns the parameter <SPAN CLASS="MATH"><I>b</I></SPAN>.
    * 
    */
   public double getB() {
      return max;
   }

 

   /**
    * Sets the parameters <SPAN CLASS="MATH"><I>a</I></SPAN> and <SPAN CLASS="MATH"><I>b</I></SPAN> for this object.
    * 
    */
   public void setParams (double a, double b) {
      if (b <= a)
         throw new IllegalArgumentException ("b <= a");
      this.min = a;
      this.max = b;
      supportA = a;
      supportB = b;
   }


   /**
    * Return a table containing the parameters of the current distribution.
    *    This table is put in regular order: [<SPAN CLASS="MATH"><I>a</I></SPAN>, <SPAN CLASS="MATH"><I>b</I></SPAN>].
    * 
    * 
    */
   public double[] getParams () {
      double[] retour = {min, max};
      return retour;
   }


   public String toString () {
      return getClass().getSimpleName() + " : a = " + min + ", b = " + max;
   }
   
	@Override
	public DistributionType getDistributionType() {
		return DistributionType.UNIFORM;
	}
	
 public String getDistributionDescription(){
		return "U("+min+","+max+")";
	}

	
  public List<String> getConfigurabeFields()
  {
	   List<String> fields  = new ArrayList<String>();
	   fields.add("min");
	   fields.add("max");
	   
	   return fields;
  }
}


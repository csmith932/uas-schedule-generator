package gov.faa.ang.swac.common.datatypes;

import gov.faa.ang.swac.common.datatypes.Angle.Units;
import gov.faa.ang.swac.common.utilities.Mathematics;

import java.io.Serializable;
import java.lang.Math;

/**
 * A 3D vector class for doing basic analytical geometry calculations in 3D.
 * Mostly used for doing great circle (spherical geometry) calculations in other
 * NASPAC classes.
 * 
 * @author Robert Lakatos - CSSI, Inc.
 */
public class Vector3D implements Serializable
{
	private static final long serialVersionUID = 6207690042637811668L;

	private double x = 0;
	private double y = 0;
	private double z = 0;
	
	/**
	 * Default constructor
	 */
	public Vector3D()
	{
	}
	
	/**
	 * Creates a 3D vector from the input arguments.
	 * @param x input x coordinate of the vector
	 * @param y input y coordinate of the vector
	 * @param z input z coordinate of the vector
	 */
	public Vector3D (double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * Copy constructor.
	 */
	public Vector3D (Vector3D v)
	{
		this.x = v.x();
		this.y = v.y();
		this.z = v.z();
	}
	
	/**
	 * Returns the x-coordinate of this {@link Vector3D}. 
	 */
	public double x()
	{
		return this.x;
	}
	
	/**
	 * Returns the y-coordinate of this {@link Vector3D}.
	 */
	public double y()
	{
		return this.y;
	}
	
	/**
	 * Returns the z-coordinate of this {@link Vector3D}.
	 */
	public double z()
	{
		return this.z;
	}
	

	/**
	 * Compares the x/y/z values of u and v using a floating-point equality check.
	 * @param u
	 * @param v
	 * @return true if equal(u.x, v.x) && equal(u.y, v.y) && equal(u.z, v.z)... false otherwise.
	 * @see #equals(double, double)
	 */
	public static boolean equals(Vector3D u, Vector3D v)
	{
		if ( Mathematics.equals(u.x(), v.x()) && Mathematics.equals(u.y(), v.y()) && Mathematics.equals(u.z(), v.z()) )
		{
			return true;
		}
		return false;
	}
	
	/**
	 * Compares the x/y/z values of u and v using a floating-point equality check.
	 * @param u
	 * @param v
	 * @param tolerance 
	 * @return true if equal(u.x, v.x, tolerance) && equal(u.y, v.y, tolerance) && equal(u.z, v.z, tolerance)... false otherwise.
	 * @see #equals(double, double, tolerance)
	 */
	public static boolean equals(Vector3D u, Vector3D v, double tolerance)
	{
		if ( Mathematics.equals(u.x(), v.x(), tolerance) && Mathematics.equals(u.y(), v.y(), tolerance) && Mathematics.equals(u.z(), v.z(), tolerance) )
		{
			return true;
		}
		return false;
	}
	
	/**
	 * Determines if this {@link Vector3D} precisely equals <code>v</code>. That is if:<br>
	 * <code>x() == v.x()</code> AND<br>
	 * <code>y() == v.y()</code> AND<br>
	 * <code>z() == v.z()</code>
	 * @see #equals(Vector3D, double)
	 */
	@Override
	public boolean equals(Object o)
	{
		if (o == null || !(o instanceof Vector3D))
		{
			return false;
		}
		
		return Mathematics.equals(this.x, ((Vector3D)o).x()) && 
			   Mathematics.equals(this.y, ((Vector3D)o).y()) && 
			   Mathematics.equals(this.z, ((Vector3D)o).z());
	}
	
	/**
	 * Determines if this {@link Vector3D} equals <code>v</code> (within <code>tolerance</code>). That is if:<br>
	 * <code>abs(x() - v.x()) &lt;= tolerance</code> AND<br>
	 * <code>abs(y() - v.y()) &lt;= tolerance</code> AND<br>
	 * <code>abs(z() - v.z()) &lt;= tolerance</code>
	 * @see #preciselyEquals(Vector3D)
	 */
	public boolean equals(Vector3D v, double tolerance)
	{
		double xDiff = Math.abs(this.x - v.x());
		double yDiff = Math.abs(this.y - v.y());
		double zDiff = Math.abs(this.z - v.z());
		boolean xTolerance = xDiff <= tolerance;
		boolean yTolerance = yDiff <= tolerance;
		boolean zTolerance = zDiff <= tolerance;
		return xTolerance && yTolerance && zTolerance;
		/* JLF TODO MERGE:
		final Vector3D delta = new Vector3D(this).minus(v);
		return delta.dotProduct(delta) <= tolerance*tolerance;
		*/
	}
	
	/**
	 * Returns the cross-product of this {@link Vector3D} with <code>v</code>.
	 * <p>
	 * <b>NOTE:</b> This method returns a new {@link Vector3D}. Both this object and the parameter object will be unchanged.
	 */
	public Vector3D crossProduct(Vector3D v)
	{
		// TODO: This formula may be unstable for two Vector3Ds that are very very close
		double i = this.y*v.z() - v.y() * this.z;
		double j = v.x()*this.z - this.x*v.z();
		double k = this.x*v.y() - v.x()*this.y;
		return new Vector3D(i, j, k);
	}
	
	/**
	 * Returns the dot product of this {@link Vector3D} with <code>v</code>.
	 */
	public double dotProduct(Vector3D v)
	{
		return this.x*v.x() + this.y*v.y() + this.z*v.z();
	}
	
	/**
	 * Normalizes this {@link Vector3D} object (scales to unit length).
	 * <p>
	 * <b>NOTE:</b> {@link #normalize()} modifies this object.
	 * @see {@link #getNormal()}
	 */
	public Vector3D normalize()
	{
		double len = length();
		if (Math.abs(len) < 0.0000001) // Protect against division by zero and near-zero (will return IEEE NaN)
		{
			this.x = 0;
			this.y = 0;
			this.z = 0;
		}
		else
		{
			this.x = this.x/len;
			this.y = this.y/len;
			this.z = this.z/len;
		}
		return this;
	}

	/**
	 * Returns a copy of this {@link Vector3D} scaled to unit length.
	 * <p>
	 * <b>NOTE:</b> {@link #getNormal()} returns a new {@link Vector3D}. This object will be unchanged.
	 * @see {@link #normalize()}
	 */
	public Vector3D getNormal()
	{
		return new Vector3D(this).normalize();
	}
	
	/**
	 * Complements this {@link Vector3D} (changes the sign of each x/y/z component).
	 * <p>
	 * <b>NOTE:</b> {@link #complement()} modifies this object.
	 * @see {@link #getComplement()}
	 */
	public Vector3D complement()
	{
		this.x = -this.x;
		this.y = -this.y;
		this.z = -this.z;
		return this;
	}

	/**
	 * @return A new {@link Vector3D} which is the complement to this {@link Vector3D} (changes signs of each x/y/z component).
	 * This object will be unchanged.
	 * @see {@link #complement()}
	 */
	public Vector3D getComplement()
	{
		return new Vector3D(this).complement();
	}

	/**
	 * Returns the Euclidean length of this {@link Vector3D}.
	 */
	public double length()
	{
		return Math.sqrt(this.dotProduct(this));
		// JLF TODO MERGE: return Math.sqrt(this.dotProduct(this));
	}

	/**
	 * Returns a new {@link Vector3D} that is the scalar multiple of this {@link Vector3D} and <code>d</code>.
	 * <p>
	 * <b>NOTE:</b> {@link #times()} returns a new {@link Vector3D}. This object will be unchanged.
	 */
	public Vector3D times(double d)
	{
		return new Vector3D(this.x*d, this.y*d,this.z*d);
	}

	/**
	 * Returns a new {@link Vector3D} that is the scalar difference of this {@link Vector3D} and <code>v</code>. 
	 * <p>
	 * <b>NOTE:</b> {@link #minus()} returns a new {@link Vector3D}. This object will be unchanged.
	 * @param v input vector
	 * @return a new vector resulting from subtraction of the input vector v from the vector
	 */
	public Vector3D minus(Vector3D v)
	{
		return new Vector3D(this.x-v.x, this.y-v.y, this.z-v.z);
	}

	/**
	 * Returns a new {@link Vector3D} that is the scalar sum of this {@link Vector3D} and <code>v</code>.
	 * <p>
	 * <b>NOTE:</b> {@link #plus()} returns a new {@link Vector3D}. This object will be unchanged.
	 * @param v input vector
	 * @return a new vector resulting from addition of the input vector v to the vector
	 */
	public Vector3D plus(Vector3D v)
	{
		return new Vector3D(this.x+v.x, this.y+v.y, this.z+v.z);
	}

	/**
	 * Returns the angle between v1 and v2 (in radians).<br>
	 * @param v1
	 * @param v2
	 * @return angle between v1 & v2 (in radians)
	 */
	public static double angle(Vector3D v1, Vector3D v2)
	{
		return Math.acos(v1.dotProduct(v2));
	}
	
	/**
	 * Returns the angle between this {@link Vector3D} and <code>v</code> (in radians).
	 */
	public double angle(Vector3D v)
	{
		return Math.acos(this.dotProduct(v));
	}
	
	// TODO: Get description from Robert
	public static Vector3D getGCInterpolatedVector3D(double q, Vector3D u, Vector3D v)
	{
		// note: if u and v are antipodal we have a problem - the question would make no sense
		// Also which formula has better rounding error properties?
		// should one normalize first and then take cross product or ...?
		// this method returns a unit vector - it does not interpolate in altitude
		if (u.equals(v))
		{
			return u;
		}

		Vector3D i = u.crossProduct(v).crossProduct(u).getNormal();
		Vector3D j = u.getNormal();
		double angle = angle(u, v);
		double newAngle = q * angle;
		Vector3D z = i.times(Math.sin(newAngle)).plus(j.times(Math.cos(newAngle)));
		return z; // could normalize again for safety z.normalize()
	}

	// TODO: Get description from Robert
	public static Vector3D getArcMidPoint(Vector3D v1, Vector3D v2)
	{
		// coordinates in degrees
		return getGCInterpolatedVector3D(0.5, v1, v2);
	}
	
	public Latitude latitude(){
		return new Latitude(Math.asin(this.z), Units.RADIANS);
	}
	
	public Longitude longitude(){
		return new Longitude(Math.atan2(this.y, this.x), Units.RADIANS);
	}
	
	@Override
	public String toString()
	{
    	double lat = Math.asin(this.z) * 180/Math.PI;           // Convert radian latitude into degrees
    	double lon = Math.atan2(this.y, this.x) * 180/Math.PI;  // Convert radian longitude into degrees

		return String.format("(%1$1.4f, %2$1.4f, %3$1.4f) = (%4$1.4f, %5$1.4f deg)", this.x, this.y, this.z, lat, lon);
	}
}
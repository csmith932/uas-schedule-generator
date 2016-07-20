/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.geometry;

/**
 * Enumerates the valid fixtures used by the PolygonTester class.
 * @author chall
 */
public enum PolygonFixture
{
	unitSquare(new double[]
	{
		0,	0,
		0,	1,
		1,	1,
		1,	0
	}),
	crossedSeamUnitSqr(new double[]
	{
		0.5,	-179.5,
	    -0.5,	-179.5,
		-0.5,	179.5,
		0.5,	179.5
	}),
	nonNormalSqr(new double[]
	{
		-10,	-190,
	    -10,	-170,
	    10,		-170,
	    10,		-190
	}),
	crosses90seam(new double[]
		{
		-10,	80,
		-10,	100,
		10,		100,
		10,		80
		}),
	bigDiamond(new double[]
	{
		0,		-179,
		45,		0,
		0,		179,
		-45,	0
	}),
	triangle(new double[]
	{
		-10,	-80,
	    70,		0,
	    -10,	80
	}),
	star(new double[]
	{
		-50,	-120,
		25,		-45,
		40,		-120,
		40,		-10,
		80,		0,
		40,		10,
		40,		120,
		25,		45,
		-50,	100,
		0,		0
	}),
	snake(new double[]
	{
		50,		-90,
		40,		0,
		30,		90,
		20,		180,
		10,		-90,
		0,		0,
		-10,	0,
		0,		-90,
		10,		180,
		20,		90,
		30,		0,
		40,		-90
	}),
	polarPoly(new double[]
	{
		0,		0,
		0,		90,
		91,		0
	}),
	ZLA083m(new double[]
	{
		33.90833333333333, -118.94166666666668, 
		33.755833333333335, -118.83333333333334, 
		33.755833333333335, -118.72083333333333, 
		33.755833333333335, -118.63333333333335, 
		33.755833333333335, -118.56666666666665, 
		33.755833333333335, -118.51666666666665, 
		33.68611111111111, -118.45833333333333, 
		33.69583333333333, -118.34166666666667, 
		33.770833333333336, -118.34166666666667, 
		33.775, -118.27499999999999, 
		33.80416666666667, -118.24166666666667, 
		33.80833333333333, -118.13749999999999, 
		33.81111111111111, -118.06666666666666, 
		33.82916666666668, -117.9875, 
		33.86666666666667, -117.93055555555556, 
		33.8875, -117.90833333333335, 
		33.916666666666664, -117.90833333333335, 
		33.961111111111116, -117.89222222222223, 
		34.0125, -117.90000000000002, 
		34.045833333333334, -118.025, 
		34.045833333333334, -118.13888888888891, 
		34.1, -118.31666666666666, 
		34.1, -118.68333333333335, 
		34.1, -118.77805555555557, 
		34.1, -118.94250000000002, 
		33.90833333333333, -118.94166666666668
	}),
	ZLA084B(new double[]
	{
		34.0, -120.50000000000001, 
		33.235, -119.55694444444445, 
		32.90833333333333, -119.18055555555554, 
		32.95, -119.11666666666667, 
		33.475, -119.11666666666667, 
		33.475, -118.95, 
		33.475, -118.78333333333335, 
		33.65833333333333, -118.76666666666667, 
		33.755833333333335, -118.83333333333334, 
		33.90833333333333, -118.94166666666668, 
		34.1, -118.94250000000002, 
		34.1, -118.77805555555557, 
		34.208333333333336, -118.81666666666666, 
		34.38055555555555, -118.99444444444444, 
		34.49166666666667, -119.08333333333333, 
		34.50416666666667, -119.20416666666668, 
		34.525, -119.46944444444443, 
		34.43611111111111, -119.41555555555556, 
		34.291666666666664, -119.41555555555556,
		34.11666666666667, -119.66666666666666, 
		34.11666666666667, -119.99999999999999, 
		34.13333333333333, -119.99999999999999, 
		34.13333333333333, -120.42222222222222, 
		34.1, -120.50000000000001, 
		34.0, -120.50000000000001
	}),
	ZBW016F(new double[]
   	{
		44.05, -71.0, 
		43.80555555555555, -71.56277777777777, 
		43.61666666666667, -71.41666666666667, 
		43.06666666666667, -71.38333333333334, 
		42.858333333333334, -71.1111111111111, 
		42.78333333333334, -70.9, 
		42.666666666666664, -70.71666666666667, 
		42.44166666666666, -70.83333333333333, 
		42.416666666666664, -70.66666666666667, 
		42.2275, -70.7388888888889, 
		42.166666666666664, -70.35555555555555, 
		42.163888888888884, -70.30416666666666, 
		42.12222222222222, -69.83055555555555, 
		42.083333333333336, -69.5, 
		43.108333333333334, -69.5, 
		43.31666666666667, -69.5, 
		43.68333333333333, -69.5, 
		43.833333333333336, -69.5, 
		44.03333333333333, -70.08333333333333, 
		44.2, -70.25, 
		44.166666666666664, -70.81666666666666, 
		44.05, -71.0
   	});

	public double[] getLatLons(int iInput)
	{
    	// Perform a deep copy of the array & its elements
    	double[] copy = new double[this.latLonArrays[iInput].length];
        for(int i = 0; i < this.latLonArrays[iInput].length; ++i)
        {
        	copy[i] = this.latLonArrays[iInput][i];
        }
        return copy;
	}
	
	private final double[][] latLonArrays;

	private PolygonFixture(final double[] latLonArray)
	{
	    this.latLonArrays = new double[4][];
	    this.latLonArrays[0] = latLonArray;
	    this.latLonArrays[1] = newClosed(this.latLonArrays[0]);
	    this.latLonArrays[2] = newReversed(this.latLonArrays[0]);
	    this.latLonArrays[3] = newReversed(this.latLonArrays[1]);
	}

    private static double[] newClosed(double[] latlons)
    {
    	if (latlons[0] == latlons[latlons.length - 2] &&
    		latlons[1] == latlons[latlons.length - 1])
    	{
    		// already closed
    		return latlons.clone();
    	}

        double[] closedLatLons = new double[latlons.length + 2];

        for (int i = 0; i < latlons.length; i++)
        {
        	closedLatLons[i] = latlons[i];
        }

        closedLatLons[closedLatLons.length - 2] = latlons[0];
        closedLatLons[closedLatLons.length - 1] = latlons[1];

        return closedLatLons;
    }

    private static double[] newReversed(double[] latlons)
    {
    	double[] reverseLatLons = new double[latlons.length];

    	for (int i = 0; i < latlons.length; i += 2)
    	{
    		reverseLatLons[i] = latlons[latlons.length-2-i];
    		reverseLatLons[i+1] = latlons[latlons.length-1-i];
    	}

    	return reverseLatLons;
    }
};

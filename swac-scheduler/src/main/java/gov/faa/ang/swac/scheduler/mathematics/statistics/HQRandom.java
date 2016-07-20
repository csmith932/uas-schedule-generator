package gov.faa.ang.swac.scheduler.mathematics.statistics;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//The authors of Numerical Recipes: 
// The Art of Scientific Computing propose a random number generator 
// which they advocate as giving a good compromise between quality and speed. 
// It is a combined generator: two XORShift generators are combined 
// with an LCG and a multiply with carry generator. 
// (Without going into all the details here, notice the two blocks of 
// three shifts each, which are the XORShifts; the first line which is the LCG, 
// similar to the standard Java Random algorithm, and the line between 
// the two XORShifts, which is a multiply with carry generator.) 
//
// Their suggested C implementation is trivially portable to Java, 
// and a suggested implementation is given below. In this case, 
// we subclass java.util.Random, and also make the generator thread-safe. 
// If thread safety is not required, then the Lock could be removed. 

public class HQRandom extends Random {

	private static final long serialVersionUID = 1L;
	
	private Lock l = new ReentrantLock();
	private long u = 0L;
	private long v = 4101842887655102017L;
	private long w = 1L;

	private long lastSavedU;
	private long lastSavedV;
	private long lastSavedW;

	protected long staticSeed = 123456789L;

	// The seed is just a gimmick (convenience) for positioning  a three dimensional internal state via a single number.
	// What we really need to track are the internal states u, v, w.

	public HQRandom(long seed) {
		super();
		initInternalState(seed);
		saveInternalState();
	}

	public HQRandom() {
		super();
		initInternalState(staticSeed);
		staticSeed = nextLong();
		saveInternalState();
	}

	private void initInternalState(long seed) {
		l.lock();
		try {
			u = 0L;
			v = 4101842887655102017L;
			w = 1L;
			u = seed ^ v;
			nextLong();
			v = u;
			nextLong();
			w = v;
			nextLong(); 
		} finally {
			l.unlock();
		}
	}

	private void saveInternalState() {
		lastSavedU = u;
		lastSavedV = v;
		lastSavedW = w;
	}
	public void resetStreamToLastSavedState() {
		u = lastSavedU;
		v = lastSavedV;
		w = lastSavedW;
	}

	public long[] getInternalState() {
		return new long [] {u, v, w};
	}

	public void setInternalState(long [] state) {
		u = state[0];
		v = state[1];
		w = state[2];
	}

	public long nextLong() {
		l.lock();
		try {
			u = u * 2862933555777941757L + 7046029254386353087L;
			v ^= v >>> 17;
			v ^= v << 31;
			v ^= v >>> 8;
			w = 4294957665L * (w & 0xffffffffL) + (w >>> 32);
			long x = u ^ (u << 21);
			x ^= x >>> 35;
			x ^= x << 4;
			long ret = (x + v) ^ w;
			return ret;
		} finally {
			l.unlock();
		}
	}

	protected int next(int bits) {
		return (int) (nextLong() >>> (64-bits));
	}

	public double randomNormal(double mean, double stDev) {
		double t1 = nextDouble(); // random number with a fixed seed
		double t2 = nextDouble();
		double rn = Math.sqrt(-2.0*Math.log(t1))*Math.cos(2.0*Math.PI*t2);
		rn = mean + stDev*rn;
		return rn;
	}

	public double randomTriangle(double min, double mode, double max)
	{
		double rn = 0;
		if(max > min)
		{
			double modeArea = (mode - min)/(max - min);
			double t1 = nextDouble();
			if(t1 <= modeArea)
			{
				rn = min + Math.sqrt(t1*(mode - min)*(max - min));
			}
			else
			{
				rn = max - Math.sqrt((max - mode)*(max - min)*(1 - t1));
			}
		}
		else
		{

		}
		return rn;
	}
}


// When should you use this generator?
// This generator is useful in cases where you need fast, 
// good-quality randomness but don't need cryptographic randomness, 
// as provided by the Java SecureRandom class. The code above is not 
// much slower than java.util.Random and provides much better quality 
// randomness and a much larger period. It is about 20 times faster 
// than SecureRandom. Typical candidates for using this generator 
// would be games and simulations (except games where money depends 
// on the random number generator, such as in gambling applications). 

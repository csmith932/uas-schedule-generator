package gov.faa.ang.swac.common.flightmodeling.Utilities;

import static org.junit.Assert.*;
import gov.faa.ang.swac.common.datatypes.Timestamp;
import gov.faa.ang.swac.common.flightmodeling.Restriction;
import gov.faa.ang.swac.datalayer.storage.fileio.EndOfFileException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class RestrictionTest {
	
	InputStream in;
	List<Restriction> resList;
	@Before
	public void setUp()
	{
		try
		{
			in = getClass().getClassLoader().getResourceAsStream("restrictions_sample.csv");
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			(new Restriction()).readHeader(reader);
			resList = new ArrayList<Restriction>();

			while(reader.ready())
			{
				Restriction res = new Restriction();
				res.readItem(reader);
				resList.add(res);
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}

	}
	
	@Test
	public void testTmis()
	{
		int tmiCount = 0;
		for(Restriction res : resList)
		{
			if(res.isTMI())
			{
				tmiCount++;
			}
		}
		assertEquals(tmiCount, 5);
	}
	
	@Test
	public void testTimeIntGreaterThan10Hrs()
	{
		int greaterThan10Count = 0;
		for(Restriction res : resList)
		{
			List<Timestamp[]> timeIntList = res.timeIntervals();
			for(Timestamp[] tArr : timeIntList)
			{
				Timestamp start = tArr[0];
				Timestamp end = tArr[1];
				long diffInMinutes = (end.getTime() - start.getTime())/(1000*60*60);
				if(diffInMinutes > 10)
				{
					greaterThan10Count++;
				}
			}
		}
		assertEquals(greaterThan10Count, 5);
	}
	
	@Test
	public void testTrafficIntGreaterThan15Mins()
	{
		int greaterThan15Count = 0;
		for(Restriction res : resList)
		{
			if(res.trafficInterval() > 15)
			{
				greaterThan15Count++;
			}
		}
		assertEquals(greaterThan15Count, 0);
	}

}

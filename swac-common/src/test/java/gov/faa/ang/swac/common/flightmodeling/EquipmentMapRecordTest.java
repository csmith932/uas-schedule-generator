package gov.faa.ang.swac.common.flightmodeling;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import gov.faa.ang.swac.common.entities.EquipmentMapRecord;

import org.junit.Test;

public class EquipmentMapRecordTest 
{
	@Test
	public void loadItemTest()
	{
		BufferedReader reader = null;
		try
		{
			EquipmentMapRecord record = new EquipmentMapRecord();
			URL url = this.getClass().getResource("/equipment_map_sample.csv");
			File testFile = new File(URLDecoder.decode(url.getFile(), StandardCharsets.UTF_8.name()));
			reader = new BufferedReader(new FileReader(testFile));
			reader.readLine();
			record.readItem(reader);
			if(! (record.aircraft_type.equals("A306")
					&& record.turnaround_cat.equals(1)
					&& record.pushback_cat.equals(2)
					&& record.taxi_out_cat.equals(3)
					&& record.taxi_in_cat.equals(4)
					&& record.rerouteClearanceCat.equals(5)
					&& record.rampCat.equals(6)))
			{
				fail("Loaded values from file not as expected");
			}
			
		}
		catch(Exception e)
		{
			fail("Exception in test "+e.getMessage());
		}
		finally
		{
			if(reader != null )
			{
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}

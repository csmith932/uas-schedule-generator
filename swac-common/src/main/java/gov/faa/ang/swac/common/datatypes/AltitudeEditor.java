/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.datatypes;


import java.beans.PropertyEditorSupport;

public final class AltitudeEditor extends PropertyEditorSupport {
	
	public String getAsText() 
	{
		return getValue() == null ? null : ((Altitude) getValue()).feet().toString();
	}

    public void setAsText(String text) 
    {
    	setValue(Altitude.valueOfFeet(Double.parseDouble(text)));
    }

}

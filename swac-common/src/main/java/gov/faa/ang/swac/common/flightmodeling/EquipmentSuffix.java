/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.flightmodeling;

import gov.faa.ang.swac.common.utilities.ParseFormatUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EquipmentSuffix implements Cloneable, Serializable
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 5038041093729756030L;

	private static org.apache.log4j.Logger logger = org.apache.log4j.LogManager.getLogger(EquipmentSuffix.class);

    private List<Integer> suffixes;
    private List<String> suffixShortNames;
    
    private final int WX_EQUIPMENT = 3;

    public EquipmentSuffix()
    {
    	this.suffixes = new ArrayList<Integer>();
        this.suffixShortNames = new ArrayList<String>();
    }

    public EquipmentSuffix(EquipmentSuffix org) {
        this.suffixes = new ArrayList<Integer>(org.suffixes.size());
        
        for (Integer i : org.suffixes) {
            this.suffixes.add((i == null ? null : i.intValue()));
        }
        
        this.suffixShortNames = new ArrayList<String>(org.suffixShortNames);
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (o instanceof EquipmentSuffix)
        {
            EquipmentSuffix equipmentSuffix = (EquipmentSuffix)o;

            if (this.suffixes != null && equipmentSuffix.suffixes != null && this.suffixes.size() == equipmentSuffix.suffixes.size())
            {
                for (int i = 0; i < this.suffixes.size(); i++)
                {
                    if (this.suffixes.get(i).intValue() != equipmentSuffix.suffixes.get(i).intValue()) { return false; }
                }

                return true;
            }
        }

        return false;
    }
    
    public void setSuffixShortNames(List<String> val) {
        this.suffixShortNames = val;
    }
    
    /**
     * Returns this {@link EquipmentSuffix} as an array of <code>int</code>.
     */
    public List<Integer> getSuffixes()
    {
        return this.suffixes;
    }
    
    /**
     * Returns the suffix at <code>index</code>.
     */
    public int getSuffix(int index)
    {
    	if (index < this.suffixes.size())
    		return this.suffixes.get(index);
    	
    	return 0;  //no suffix assigned
    }

    /**
     * 
     * @param shortName the short name for the equipment category
     * @return the suffix index for equipment category given by <code>shortName</code>.
     */
    private int getSuffixIndex(String shortName) {
        // XXX: switch to hashtable for efficiency if the list gets long 
    	
    	int idx = this.suffixShortNames.indexOf(shortName);
        
        return idx;
    }
    
    /**
     * 
     * @param shortName the short name for the equipment category
     * @return the suffix value for equipment category given by <code>shortName</code>.
     */
    public int getSuffix(String shortName) {
        int idx = this.getSuffixIndex(shortName);
        
        if (idx < 0)
            return 0;
        else
            return getSuffix(idx);
    }
    /**
     * Sets the suffix at <code>index</code>.
     */
    public void setSuffix(int index, int suffix)
    {
    	if (this.suffixes.size() > index)
    		this.suffixes.set(index, suffix);
    	else{
    		for (int i=this.suffixes.size(); i < index; ++i) //add suffix of 0 to all positions with no suffix value.
    			this.suffixes.add(0);
    		
    		this.suffixes.add(suffix); //add suffix to desired position.
    	}
    }
    
    /**
     * Sets the suffix at <code>index</code>.
     */
    public void setSuffix(String shortName, int suffix)
    {
    	int idx = this.getSuffixIndex(shortName);
    	
    	if (idx >= 0) {
    		this.setSuffix(idx, suffix);
    	}
    	else
    	{
    		logger.warn("Attempting to set equipment suffix \"" + shortName + "\" that doesn't exist");
    	}
    }

    /**
     * @return a string of all the suffixes separated by a hyphen (e.g. "0-0-0" or "2-3-2")
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.suffixes.size(); i++)
        {
        	if (i > 0)
        		sb.append("-");
            sb.append(this.suffixes.get(i));
        }
        return sb.toString();
    }

    /**
     * Converts a "-" delimited {@link String} of <code>int</code> into a {@link EqupimentSuffix}
     * object.
     */
    public static EquipmentSuffix fromTextRecord(String record)
    {
        EquipmentSuffix equipmentSuffix = new EquipmentSuffix();
        if (ParseFormatUtils.isBlank(record))
        	return equipmentSuffix;
        
        String suffixStrings[] = record.split("-");

        for (int i = 0; i < suffixStrings.length; i++)
        {
        	String suffixString = suffixStrings[i];
        	equipmentSuffix.setSuffix(i, Integer.valueOf(suffixString));
        }

        return equipmentSuffix;
    }

    @Override
    public EquipmentSuffix clone()
    {
        return new EquipmentSuffix(this);
    }
    
    public int getWxEquipment() { return getEquipment(WX_EQUIPMENT); }
    public int getEquipment(int equipmentCatVecIdx)
    {
    	if(equipmentCatVecIdx < 0 || equipmentCatVecIdx >= this.suffixes.size()){
    		return 0;
    	}
    	return this.suffixes.get(equipmentCatVecIdx);
    }
}
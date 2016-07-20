/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.datalayer.identity;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import gov.faa.ang.swac.common.datatypes.Timestamp;
import gov.faa.ang.swac.datalayer.DataAccessException;
import gov.faa.ang.swac.datalayer.ResourceManager;
import gov.faa.ang.swac.datalayer.storage.DataMarshaller;
import gov.faa.ang.swac.datalayer.storage.InvalidMarshaller;

public class ParameterizedDataDescriptor extends DataDescriptor
{
	private static final Logger logger = LogManager.getLogger(ParameterizedDataDescriptor.class);
	
	protected DataDescriptor baseDescriptor;
	protected Timestamp baseDate;
	protected Integer forecastFiscalYear;
	protected String classifier;

	public ParameterizedDataDescriptor(ParameterizedDataDescriptor val) {
		super(val);
            try {
                this.baseDescriptor = (val.baseDescriptor == null ? null : val.baseDescriptor.clone());
            } catch (CloneNotSupportedException cnse) {
                this.baseDescriptor = val.baseDescriptor;
            }
		this.baseDate = (val.baseDate == null ? null : val.baseDate.clone());
		this.forecastFiscalYear = (val.forecastFiscalYear == null ? null : val.forecastFiscalYear.intValue());
		this.classifier = val.classifier;
	}

	public ParameterizedDataDescriptor() {
	}

	public Timestamp getBaseDate()
	{
		return baseDate;
	}

	public void setBaseDate(Timestamp baseDate)
	{
		this.baseDate = baseDate;
	}

	public Integer getForecastFiscalYear()
	{
		return forecastFiscalYear;
	}

	public void setForecastFiscalYear(Integer forecastFiscalYear)
	{
		this.forecastFiscalYear = forecastFiscalYear;
	}

	public String getClassifier()
	{
		return classifier;
	}

	public void setClassifier(String classifier)
	{
		this.classifier = classifier;
	}

	public DataDescriptor getBaseDescriptor()
	{
		return baseDescriptor;
	}

	public void setBaseDescriptor(DataDescriptor baseDescriptor)
	{
		this.baseDescriptor = baseDescriptor;
	}

	@Override
	public DataMarshaller createMarshaller(ResourceManager resMan) throws DataAccessException
	{
		if (this.baseDescriptor == null)
		{
			// Throwing an exception here is confusing and useless to the user for purposes of identifying missing data. Log it instead and return an InvalidMarshaller
			// throw new IllegalStateException("Error: Base DataDescriptor is not initialized\n" + this.toString());
			logger.error("Error: Base DataDescriptor is not initialized\n" + this.toString());
			return new InvalidMarshaller(this.toString());
		}

		this.baseDescriptor.setDataType(this.getDataType());
		
		return this.baseDescriptor.createMarshaller(resMan);
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((baseDate == null) ? 0 : baseDate.hashCode());
		result = prime * result + ((classifier == null) ? 0 : classifier.hashCode());
		result = prime * result + ((forecastFiscalYear == null) ? 0 : forecastFiscalYear.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (!super.equals(obj))
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		ParameterizedDataDescriptor other = (ParameterizedDataDescriptor) obj;
		if (baseDate == null || other.baseDate == null || baseDate.equals(other.baseDate))
		{
			if (classifier == null || classifier.isEmpty() || 
                                other.classifier == null || other.classifier.isEmpty() || 
                                classifier.equals(other.classifier))
			{
				if (forecastFiscalYear == null || other.forecastFiscalYear == null || 
                                        forecastFiscalYear.equals(other.forecastFiscalYear)) 
                                {
                                    return true;
                                } 
                                else 
                                {
                                    return false;
                                }
			} 
                        else 
                        {
                            return false;
                        }
		} 
                else 
		{
			return false;
		}
	}

	@Override
	public String toString() {
		return "ParameterizedDataDescriptor [baseDescriptor=" + baseDescriptor
				+ ", baseDate=" + baseDate + ", forecastFiscalYear="
				+ forecastFiscalYear + ", classifier=" + classifier
				+ ", getDataType()=" + getDataType().getSimpleName() + "]";
	}
	
	@Override
	public ParameterizedDataDescriptor clone()
	{
		return new ParameterizedDataDescriptor(this);
	}
}

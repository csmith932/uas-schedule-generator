/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.datalayer.storage.db;



public class FieldTypeConverterFactory {
	
	private String jdbcDriver;
	//private String jdbcUrl;
	private boolean useGenericDBTypeConverter = false;

	public void setUseGenericDBTypeConverter(boolean useGenericDBTypeConverter) {
		this.useGenericDBTypeConverter = useGenericDBTypeConverter;
	}

	public void setJdbcUrl(String jdbcUrl) { 
    	//this.jdbcUrl = jdbcUrl;
    }

    public void setJdbcDriver(String jdbcDriver) { 
    	this.jdbcDriver = jdbcDriver;
    }
    
	public FieldTypeConverter getFieldTypeConverter() {
		if (useGenericDBTypeConverter) {
			return new SQL99FieldTypeConverter();
		}
		
		if (jdbcDriver.contains("oracle")) {
			return new OracleFieldTypeConverter();
		}
		
		if (jdbcDriver.contains("mysql"))
			return new MysqlFieldTypeConverter();

		if (jdbcDriver.contains("mariadb"))
			return new MariaDBFieldTypeConverter();
		
		//TODO Access?
		
		return new SQL99FieldTypeConverter();
	}

}
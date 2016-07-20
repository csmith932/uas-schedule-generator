/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.datalayer.storage.db;

import gov.faa.ang.swac.datalayer.DataAccessException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * TODO: This class is experimental, and may not be part of the final design. The purpose is to allow the user to specify an arbitrary SQL
 * query string
 * @author csmith
 *
 */
public class GenericSqlDao extends JDBCDao {
	private String queryText;
	private PreparedStatement query;
	
	public String getQueryText() {
		return queryText;
	}

	public void setQueryText(String queryText) {
		this.queryText = queryText;
		if (this.query != null)
		{
			try {
				this.query.close();
			} catch (SQLException e) {
				throw new IllegalStateException("If you reach this you're probably misusing the data layer", e);
			}
			this.query = null;
		}
	}
	
	@Override
	public ResultSet executeQuery(Object... params) throws DataAccessException {
		try {
			// Lazy precompile so that the queryText and connection have time to be set
			if (query == null)
			{
				query = this.connection.prepareStatement(this.queryText);
			}
			
			// Set query params
			for (int i = 0; i < params.length; i++)
			{
				query.setObject(i + 1, params[i]);
			}
			
			// Now execute the query
			return query.executeQuery();
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}
	}

	@Override
	public GenericSqlDao copy(Connection connection) {
		GenericSqlDao retVal = new GenericSqlDao();
		retVal.queryText = this.queryText;
		retVal.setConnection(connection);
		
		return retVal;
	}

}

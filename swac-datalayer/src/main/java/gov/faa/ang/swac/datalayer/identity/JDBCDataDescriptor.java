package gov.faa.ang.swac.datalayer.identity;

import java.sql.Connection;
import java.sql.SQLException;

import gov.faa.ang.swac.datalayer.DataAccessException;
import gov.faa.ang.swac.datalayer.ResourceManager;
import gov.faa.ang.swac.datalayer.storage.db.DataAccessObject;
import gov.faa.ang.swac.datalayer.storage.db.JDBCConnectionFactory;
import gov.faa.ang.swac.datalayer.storage.db.JDBCDao;


public class JDBCDataDescriptor extends DataAccessObjectDescriptor {
	private JDBCConnectionFactory connectionFactory;
	private String databaseName;
	private JDBCDao dao;

	public JDBCDataDescriptor() {
	}

	public JDBCDataDescriptor(JDBCDataDescriptor org) {
		super.instanceId = org.instanceId;
		super.setDataType(org.getDataType());

		this.connectionFactory = org.connectionFactory;
		this.databaseName = org.databaseName;
		this.dao = org.dao.copy(null);
	}

	public JDBCConnectionFactory getConnectionFactory() {
		return connectionFactory;
	}

	public void setConnectionFactory(JDBCConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public JDBCDao getDao() {
		return dao;
	}

	public void setDao(JDBCDao dao) {
		this.dao = dao;
	}

	@Override
	public void setInstanceId(String scenarioExecutionId) {
		if (super.instanceId == null) {
			super.setInstanceId(scenarioExecutionId);
			this.databaseName = this.databaseName + "-" + scenarioExecutionId;
		} else if (scenarioExecutionId != null && !scenarioExecutionId.isEmpty()) {
			int index = this.databaseName.lastIndexOf('-');
			if (index > 0) {
				this.databaseName = this.databaseName.substring(0, index + 1) + scenarioExecutionId;
			} else {
				this.databaseName = this.databaseName + "-" + scenarioExecutionId;
			}
		}
	}

	@Override
	public DataAccessObject createMarshaller(ResourceManager resMan) throws DataAccessException {
		try {
			Connection connection = this.connectionFactory.getConnection(this.databaseName);
			return this.dao.copy(connection);
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}
	}

	@Override
	public JDBCDataDescriptor clone() {
		return new JDBCDataDescriptor(this);
	}

	@Override
	public int hashCode() {
		return this.databaseName.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final JDBCDataDescriptor other = (JDBCDataDescriptor) obj;
		if ((this.databaseName == null) ? (other.databaseName != null) : !this.databaseName.equals(other.databaseName)) {
			return false;
		}
		if ((this.getDataType() != other.getDataType())) {
			return false;
		}
		return true;
	}
}

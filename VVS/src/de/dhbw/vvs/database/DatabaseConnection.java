package de.dhbw.vvs.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import de.dhbw.vvs.application.ExceptionStatus;
import de.dhbw.vvs.application.WebServiceException;
import de.dhbw.vvs.utility.TypeHashMap;

/**
 * A database connection
 */
public class DatabaseConnection {

	private final Connection connection;

	/**
	 * Create a new database-handler and connects to the database
	 * 
	 * @param user the user of the database
	 * @param password the password of the db-user
	 * @param schema the schema of the database
	 * @throws WebServiceException if the connection fails
	 */
	DatabaseConnection(String user, String password, String schema) throws WebServiceException {
		try {
			// This will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver");
			// Setup the connection with the DB
			connection = DriverManager.getConnection("jdbc:mysql://localhost/" + schema + "?" + "user=" + user + "&password=" + password);
		} catch (Exception e) {
			throw new WebServiceException(ExceptionStatus.DB_ESTABLISHING_CONNECTION_FAILED);
		}
	}

	/**
	 * Creates database-handler with standard values: <br>
	 * <code>user:</code> "root" <br>
	 * <code>password:</code> "root" <br>
	 * <code>schema:</code> "vvs" <br>
	 */
	DatabaseConnection() throws WebServiceException {
		this("root", "root", "vvs");
	}

	/**
	 * Closes the connection to the database
	 * 
	 * @throws WebServiceException if no connection was established
	 */
	public synchronized void close() throws WebServiceException {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (Exception e) {
			throw new WebServiceException(ExceptionStatus.DB_CLOSING_CONNECTION_FAILED);
		}
	}

	/**
	 * Executes a selecting database query
	 * @param statement the sql statement to be executed
	 * @param fieldValues the values for the prepared statement
	 * @return a custom result set
	 * @throws WebServiceException if the query is not correct or if it couldn't be executed
	 */
	public synchronized ArrayList<TypeHashMap<String, Object>> doSelectingQuery(String statement, ArrayList<Object> fieldValues) throws WebServiceException {
		if (statement == null || statement == "") {
			throw new WebServiceException(ExceptionStatus.DB_INVALID_STATEMENT);
		}
		PreparedStatement pStat = null;
		ResultSet resSet = null;
		try {
			pStat = connection.prepareStatement(statement);
			if(fieldValues != null) {
				for (int i = 0; i < fieldValues.size(); i++) {
					pStat.setString(i + 1, fieldValues.get(i).toString().trim());
				}
			}
			resSet = pStat.executeQuery();
			ArrayList<TypeHashMap<String, Object>> resultSet = new ArrayList<TypeHashMap<String, Object>>();
			while (resSet.next()) {
				TypeHashMap<String, Object> currentResult = new TypeHashMap<String, Object>();
				for (int i = 0; i < resSet.getMetaData().getColumnCount(); i++) {
					String label = resSet.getMetaData().getColumnLabel(i + 1);
					currentResult.put(label, resSet.getObject(label));
				}
				resultSet.add(currentResult);
			}
			return resultSet;
		} catch (Exception e) {
			throw new WebServiceException(ExceptionStatus.DB_EXECUTION_FAILED);
		} finally {
			try {
				if (pStat != null) {
					pStat.close();
				}
				if (resSet != null) {
					resSet.close();
				}
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Executes a database query
	 * 
	 * @param statement the sql statement to be executed
	 * @param fieldValues the values for the prepared statement
	 * @return the number of affected rows or the generated key
	 * @throws WebServiceException is thrown if the query is not correct or if it could not be executed
	 */
	public synchronized int doQuery(String statement, ArrayList<Object> fieldValues) throws WebServiceException {
		if (statement == null || statement == "" || fieldValues == null || fieldValues.isEmpty()) {
			throw new WebServiceException(ExceptionStatus.DB_INVALID_STATEMENT);
		}
		PreparedStatement pStat = null;
		ResultSet genKeys = null;
		try {
			pStat = connection.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
			for (int i = 0; i < fieldValues.size(); i++) {
				try {
					pStat.setString(i + 1, fieldValues.get(i).toString().trim());	
				} catch (NullPointerException e) {
					pStat.setString(i + 1, null);
				}
			}
			int affectedRows = pStat.executeUpdate();
			genKeys = pStat.getGeneratedKeys();
			if (genKeys.last()) {
				return genKeys.getInt(1);
			} else {
				return affectedRows;
			}
		} catch (SQLException e) {
			// Check for duplicated entry
			if (e.getSQLState().equals("23000")) {
				throw new WebServiceException(ExceptionStatus.DB_DUPLICATE_ENTRY);
			}
			throw new WebServiceException(ExceptionStatus.DB_EXECUTION_FAILED);
		} finally {
			try {
				if (genKeys != null) {
					genKeys.close();
				}
				if (pStat != null) {
					pStat.close();
				}
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Executes a database query
	 * 
	 * @param statement the sql statement to be executed
	 * @param fieldValuesList a list value lists for the prepared statement
	 * @return the total number of affected rows
	 * @throws WebServiceException is thrown if the query is not correct or if it could not be executed
	 */
	public synchronized int doMultiQuery(String statement, ArrayList<ArrayList<Object>> fieldValuesList) throws WebServiceException {
		if (statement == null || statement == "" || fieldValuesList == null || fieldValuesList.isEmpty()) {
			throw new WebServiceException(ExceptionStatus.DB_INVALID_STATEMENT);
		}
		if (fieldValuesList == null || fieldValuesList.size() == 0) {
			return 0;
		}
		PreparedStatement pStat = null;
		ResultSet genKeys = null;
		try {
			connection.setAutoCommit(false);
			int affectedRows = 0;
			pStat = connection.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
			for (ArrayList<Object> fieldValues : fieldValuesList) {
				for (int i = 0; i < fieldValues.size(); i++) {
					pStat.setString(i + 1, fieldValues.get(i).toString().trim());
				}
				affectedRows += pStat.executeUpdate();
				pStat.clearParameters();
			}
			connection.commit();
			connection.setAutoCommit(true);
			return affectedRows;
		} catch (SQLException e) {
			// Check for duplicated entry
			if (e.getSQLState().equals("23000")) {
				throw new WebServiceException(ExceptionStatus.DB_DUPLICATE_ENTRY);
			}
			throw new WebServiceException(ExceptionStatus.DB_EXECUTION_FAILED);
		} finally {
			try {
				if (genKeys != null) {genKeys.close();}
				if (pStat != null) {pStat.close();}
				if (connection != null && connection.getAutoCommit() == false) {
					connection.rollback();
					connection.setAutoCommit(true);
				}
			} catch (Exception e) {}
		}
	}
}

package de.dhbw.vvs.database;

import java.util.HashMap;

import de.dhbw.vvs.application.ExceptionStatus;
import de.dhbw.vvs.application.WebServiceException;

/**
 * Pool of database connections
 */
public class ConnectionPool {

	private HashMap<Thread, DatabaseConnection> connectionList;
	private static ConnectionPool connectionPool; 
	
	/**
	 * Returns the singleton connection pool object
	 * If no connection pool exists yet, it is created
	 * @return the connection pool
	 */
	public synchronized static ConnectionPool getConnectionPool() {
		if(connectionPool == null) {
			connectionPool = new ConnectionPool();
		}
		return connectionPool;
	}
	
	/**
	 * Constructs a new ConnectionPool Object.
	 * It initializes the connection HashMap
	 */
	private ConnectionPool() {
		this.connectionList = new HashMap<Thread, DatabaseConnection>();
	}
	
	/**
	 * @return the database connection, which can be used by the thread calling the method
	 */
	public synchronized DatabaseConnection getConnection() throws WebServiceException {
		Thread t = Thread.currentThread();
		DatabaseConnection d = connectionList.get(t);
		if(d != null) {
			return d;
		} else {
			//Establish one connection per Thread
			try {
				d = new DatabaseConnection();
				connectionList.put(t, d);
				return d;
			} catch (Exception e) {
				throw new WebServiceException(ExceptionStatus.DB_ESTABLISHING_CONNECTION_FAILED);
			}
		}
	}
	
	/**
	 * Closes the connection, which the thread, is using
	 */
	public synchronized void closeConnection() {
		Thread t = Thread.currentThread();
		DatabaseConnection d = connectionList.remove(t);
		try {
			if(d != null) {
				d.close();
			}			
		} catch (Exception e) {}
	}
	
}

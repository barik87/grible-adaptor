/*******************************************************************************
 * Copyright (c) 2013 Maksym Barvinskyi.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Maksym Barvinskyi - initial API and implementation
 ******************************************************************************/
package org.pine.adaptor;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

/**
 * Class that represents Test Table entity from Pine.
 * 
 * @author Maksym Barvinskyi
 * 
 */
public class TestTable {
	private String tableName;
	private String productName;

	public TestTable(String name) {
		this.tableName = name;
		this.productName = PineSettings.getProductName();
		initializeSQLDriver();
	}

	/**
	 * Retrieves data from Preconditions sheet.
	 * 
	 * @return HashMap<ParameterName, ParameterValue>.
	 */
	public HashMap<String, String> getPreconditionsTable() {
		return getOneRowTable("precondition");
	}

	/**
	 * Retrieves data from Postconditions sheet.
	 * 
	 * @return HashMap<ParameterName, ParameterValue>.
	 */
	public HashMap<String, String> getPostconditionsTable() {
		return getOneRowTable("postcondition");
	}

	private HashMap<String, String> getOneRowTable(String subTableType) {
		HashMap<String, String> result = new HashMap<String, String>();
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();

			ResultSet rs = stmt.executeQuery("SELECT k.name, v.value FROM keys as k JOIN values as v "
					+ "ON v.keyid=k.id AND k.tableid =(SELECT id FROM tables WHERE type="
					+ "(SELECT id FROM tabletypes WHERE name='" + subTableType + "') AND parentid="
					+ "(SELECT id FROM tables WHERE name='" + tableName + "' AND categoryid IN "
					+ "(SELECT id FROM categories WHERE productid=" + "(SELECT id FROM products WHERE name='"
					+ productName + "') AND type=(SELECT id FROM tabletypes WHERE name='table'))))");
			while (rs.next()) {
				result.put(rs.getString("name"), rs.getString("value"));
			}

			conn.close();
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			PineSettings.getErrorsHandler().onAdaptorFail(e);
		}
		return result;
	}

	/**
	 * Retrieves data from General sheet.
	 * 
	 * @return ArrayList of HashMap<ParameterName, ParameterValue>.
	 */
	public List<HashMap<String, String>> getGeneralTable() {
		return getValuesFromPine("table");
	}

	List<HashMap<String, String>> getDataStorageValues() {
		return getValuesFromPine("storage");
	}

	HashMap<Integer, HashMap<String, String>> getDataStorageValues(Integer[] iterationNumbers) {
		return getValuesFromPine(iterationNumbers);
	}

	private List<HashMap<String, String>> getValuesFromPine(String entityType) {
		List<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();

			String nameColumn = "";
			if (entityType.equals("table")) {
				nameColumn = "name";
			} else {
				nameColumn = "classname";
			}

			HashMap<Integer, String> keys = new HashMap<Integer, String>();
			ResultSet rs = stmt.executeQuery("SELECT id, name FROM keys WHERE tableid="
					+ "(SELECT id FROM tables WHERE " + nameColumn + "='" + tableName + "' AND type="
					+ "(SELECT id FROM tabletypes WHERE name='" + entityType + "') AND categoryid IN "
					+ "(SELECT id FROM categories WHERE productid=(SELECT id FROM products WHERE name='" + productName
					+ "')))");
			while (rs.next()) {
				keys.put(rs.getInt("id"), rs.getString("name"));
			}

			List<Integer> rowIds = new ArrayList<Integer>();
			rs = stmt.executeQuery("SELECT id FROM rows WHERE tableid=" + "(SELECT id FROM tables WHERE " + nameColumn
					+ "='" + tableName + "' AND type=" + "(SELECT id FROM tabletypes WHERE name='" + entityType
					+ "') AND categoryid IN " + "(SELECT id FROM categories WHERE productid="
					+ "(SELECT id FROM products WHERE name='" + productName + "'))) ORDER BY \"order\"");
			while (rs.next()) {
				rowIds.add(rs.getInt("id"));
			}

			for (int i = 0; i < rowIds.size(); i++) {
				HashMap<String, String> row = new HashMap<String, String>();
				rs = stmt.executeQuery("SELECT keyid, value FROM values WHERE rowid=" + rowIds.get(i));
				for (int j = 0; j < keys.size(); j++) {
					while (rs.next()) {
						String value = rs.getString("value").replace("\\", File.separator);
						row.put(keys.get(rs.getInt("keyid")), value);
					}
				}
				result.add(row);
			}

			conn.close();
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			PineSettings.getErrorsHandler().onAdaptorFail(e);
		}
		if (result.isEmpty()) {
			String message = "Pine error: " + entityType + " '" + tableName + "' is missing.";
			PineSettings.getErrorsHandler().onAdaptorFail(new Exception(message));
		}
		return result;
	}

	private HashMap<Integer, HashMap<String, String>> getValuesFromPine(Integer[] iterationNumbers) {
		HashMap<Integer, HashMap<String, String>> result = new HashMap<Integer, HashMap<String, String>>();
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();

			HashMap<Integer, String> keys = new HashMap<Integer, String>();
			ResultSet rs = stmt.executeQuery("SELECT id, name FROM keys WHERE tableid="
					+ "(SELECT id FROM tables WHERE classname='" + tableName + "' AND type="
					+ "(SELECT id FROM tabletypes WHERE name='storage') AND categoryid IN "
					+ "(SELECT id FROM categories WHERE productid=(SELECT id FROM products WHERE name='" + productName
					+ "')))");
			while (rs.next()) {
				keys.put(rs.getInt("id"), rs.getString("name"));
			}

			HashMap<Integer, Integer> rowNumbersAndIds = new HashMap<Integer, Integer>();
			rs = stmt.executeQuery("SELECT id, \"order\" FROM rows WHERE tableid="
					+ "(SELECT id FROM tables WHERE classname='" + tableName
					+ "' AND type=(SELECT id FROM tabletypes WHERE name='storage') AND categoryid IN "
					+ "(SELECT id FROM categories WHERE productid=(SELECT id FROM products WHERE name='" + productName
					+ "'))) AND \"order\" IN (" + StringUtils.join(iterationNumbers, ",") + ") ORDER BY \"order\"");
			while (rs.next()) {
				rowNumbersAndIds.put(rs.getInt("id"), rs.getInt("order"));
			}

			Set<Integer> rowIds = rowNumbersAndIds.keySet();
			for (Integer rowId : rowIds) {
				HashMap<String, String> row = new HashMap<String, String>();
				rs = stmt.executeQuery("SELECT keyid, value FROM values WHERE rowid=" + rowId);
				for (int j = 0; j < keys.size(); j++) {
					while (rs.next()) {
						String value = rs.getString("value").replace("\\", File.separator);
						row.put(keys.get(rs.getInt("keyid")), value);
					}
				}
				result.put(rowNumbersAndIds.get(rowId), row);
			}

			conn.close();
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			PineSettings.getErrorsHandler().onAdaptorFail(e);
		}
		return result;
	}

	private Connection getConnection() throws SQLException {
		String dbhost = PineSettings.getDbHost();
		String dbport = PineSettings.getDbPort();
		String dbName = PineSettings.getDbName();
		String dblogin = PineSettings.getDbLogin();
		String dbpswd = PineSettings.getDbPswd();

		Connection conn = DriverManager.getConnection("jdbc:postgresql://" + dbhost + ":" + dbport + "/" + dbName,
				dblogin, dbpswd);
		return conn;
	}

	private void initializeSQLDriver() {
		try {
			Class.forName("org.postgresql.Driver").newInstance();
		} catch (Exception e) {
			PineSettings.getErrorsHandler().onAdaptorFail(e);
		}
	}
}

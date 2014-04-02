/*******************************************************************************
 * Copyright (c) 2013 - 2014 Maksym Barvinskyi.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Maksym Barvinskyi - initial API and implementation
 ******************************************************************************/
package org.grible.adaptor;

import java.io.File;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;

import org.grible.adaptor.errors.ErrorsHandler;
import org.grible.adaptor.errors.SimpleErrorsHandler;

/**
 * Class that holds grible adaptor settings.
 * 
 * @author Maksym Barvinskyi
 * 
 */
public class GribleSettings {
	private static AppTypes appType;
	private static ErrorsHandler errorsHandler;
	private static String productName;
	private static String productPath;
	private static String dbhost;
	private static String dbport;
	private static String dbname;
	private static String dblogin;
	private static String dbpswd;

	/**
	 * Initializes grible adaptor based on data values from grible.xml.
	 * 
	 * @param configFilePath
	 *            - path to grible.xml file (relative or absolute);
	 */
	public static void init(String configFilePath) {
		try {
			Builder parser = new Builder();
			Document doc = null;
			doc = parser.build(new File(configFilePath));
			if (doc != null) {
				Element database = doc.getRootElement().getFirstChildElement("gribledb");
				dbhost = database.getFirstChildElement("dbhost").getValue();
				dbport = database.getFirstChildElement("dbport").getValue();
				dbname = database.getFirstChildElement("dbname").getValue();
				dblogin = database.getFirstChildElement("dblogin").getValue();
				dbpswd = database.getFirstChildElement("dbpswd").getValue();
			}
		} catch (Exception e) {
			getErrorsHandler().onAdaptorFail(e);
		}
	}

	static ErrorsHandler getErrorsHandler() {
		if (errorsHandler == null) {
			return new SimpleErrorsHandler();
		}
		return errorsHandler;
	}

	/**
	 * Sets the handler for the errors that occur in the grible adaptor.
	 * 
	 * @param errorsHandler
	 *            - class that implements ErrorsHandler interface.
	 */
	public static void setErrorsHandler(ErrorsHandler errorsHandler) {
		GribleSettings.errorsHandler = errorsHandler;
	}

	/**
	 * Gets the Product name that was set by setProductName(String productName).
	 */
	public static String getProductName() {
		return productName;
	}

	/**
	 * Sets the Product name. Must be equal to one in Grible.
	 * 
	 * @param productName
	 */
	public static void setProductName(String productName) {
		GribleSettings.productName = productName;
	}
	
	/**
	 * Gets the Product path that was set by setProductPath(String productPath).
	 */
	public static String getProductPath() {
		return productPath;
	}

	/**
	 * Sets the product path: it is relative to the root of the Java project (i.e. "data"). Not like in Grible.
	 * 
	 * @param productPath
	 */
	public static void setProductPath(String productPath) {
		GribleSettings.productPath = productPath;
	}
	
	/**
	 * Sets the grible app type: PostgreSQL or JSON.
	 * 
	 * @param appType
	 */
	public static void setAppType(AppTypes appType) {
		GribleSettings.appType = appType;
	}

	static String getDbHost() {
		return dbhost;
	}

	static String getDbPort() {
		return dbport;
	}

	static String getDbName() {
		return dbname;
	}

	static String getDbLogin() {
		return dblogin;
	}

	static String getDbPswd() {
		return dbpswd;
	}

	static AppTypes getAppType() {
		return appType;
	}

}

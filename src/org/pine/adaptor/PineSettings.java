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

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;

import org.pine.adaptor.errors.ErrorsHandler;
import org.pine.adaptor.errors.SimpleErrorsHandler;

public class PineSettings {
	private static ErrorsHandler errorsHandler;
	private static String productName;
	private static String dbhost;
	private static String dbport;
	private static String dbname;
	private static String dblogin;
	private static String dbpswd;

	public static void init(String configFilePath) {
		try {
			Builder parser = new Builder();
			Document doc = null;
			doc = parser.build(new File(configFilePath));
			if (doc != null) {
				Element database = doc.getRootElement().getFirstChildElement("pinedb");
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

	public static void setErrorsHandler(ErrorsHandler errorsHandler) {
		PineSettings.errorsHandler = errorsHandler;
	}

	public static String getProductName() {
		return productName;
	}

	public static void setProductName(String productName) {
		PineSettings.productName = productName;
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

}

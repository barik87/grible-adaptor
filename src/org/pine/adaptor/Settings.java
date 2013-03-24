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

public class Settings {
	private static final String configFilePath = "pine.xml";
	private static Settings settings;
	private ErrorsHandler errorsHandler;
	private String productName;
	private String dbhost;
	private String dbport;
	private String dbname;
	private String dblogin;
	private String dbpswd;

	private Settings() {
		setErrorsHandler(new SimpleErrorsHandler());

		Builder parser = new Builder();
		Document doc = null;
		try {
			doc = parser.build(new File(configFilePath));
		} catch (Exception e) {
			getErrorsHandler().onAdaptorFail(e);
		}
		if (doc != null) {
			Element database = doc.getRootElement().getFirstChildElement("database");
			this.dbhost = database.getFirstChildElement("dbhost").getValue();
			this.dbport = database.getFirstChildElement("dbport").getValue();
			this.dbname = database.getFirstChildElement("dbname").getValue();
			this.dblogin = database.getFirstChildElement("dblogin").getValue();
			this.dbpswd = database.getFirstChildElement("dbpswd").getValue();
		}
	}

	public static Settings getInstance() {
		if (settings == null) {
			settings = new Settings();
		}
		return settings;
	}

	ErrorsHandler getErrorsHandler() {
		return errorsHandler;
	}

	public void setErrorsHandler(ErrorsHandler errorsHandler) {
		this.errorsHandler = errorsHandler;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	String getDbHost() {
		return dbhost;
	}

	String getDbPort() {
		return dbport;
	}

	String getDbName() {
		return dbname;
	}

	String getDbLogin() {
		return dblogin;
	}

	String getDbPswd() {
		return dbpswd;
	}

}

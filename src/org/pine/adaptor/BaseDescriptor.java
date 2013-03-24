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

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;

public abstract class BaseDescriptor {
	private HashMap<String, String> data;
	private boolean isNotEmpty;

	public BaseDescriptor(HashMap<String, String> data) {
		if (data != null) {
			this.data = data;
			this.setNotEmpty(true);
		} else {
			this.setNotEmpty(false);
		}
	}

	public String getString(String key) {
		if (data != null) {
			if (!data.containsKey(key)) {
				Exception e = new Exception("Descriptor error: key '" + key + "' not found. HashMap: " + data + ".");
				Settings.getInstance().getErrorsHandler().onAdaptorFail(e);
			}
			return data.get(key);
		}
		return null;
	}

	public boolean getBoolean(String key) {
		if (data != null) {
			if (!data.containsKey(key)) {
				Exception e = new Exception("Descriptor error: key '" + key + "' not found. HashMap: " + data + ".");
				Settings.getInstance().getErrorsHandler().onAdaptorFail(e);
			}
			return Boolean.parseBoolean(data.get(key));
		}
		return false;
	}

	public int getInt(String key) {
		if (data != null) {
			if (!data.containsKey(key)) {
				Exception e = new Exception("Descriptor error: key '" + key + "' not found. HashMap: " + data + ".");
				Settings.getInstance().getErrorsHandler().onAdaptorFail(e);
			}
			if (StringUtils.isNumeric(data.get(key))) {
				return Integer.parseInt(data.get(key));
			}
		}
		return 0;
	}

	public boolean isNotEmpty() {
		return isNotEmpty;
	}

	public void setNotEmpty(boolean isNotEmpty) {
		this.isNotEmpty = isNotEmpty;
	}
}

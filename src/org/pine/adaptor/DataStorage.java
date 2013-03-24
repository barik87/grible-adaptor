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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataStorage {

	public static <T> List<T> getDescriptors(Class<T> type) {
		DataTable dataStorage = new DataTable(type.getSimpleName());
		List<T> result = new ArrayList<T>();
		List<HashMap<String, String>> rows = dataStorage.getDataStorageValues();
		for (int i = 0; i < rows.size(); i++) {
			T descriptor = null;
			try {
				Constructor<T> c = type.getConstructor(new Class[] { HashMap.class });
				descriptor = c.newInstance(new Object[] { rows.get(i) });
			} catch (Exception e) {
				Exception pineException = new Exception("DataStorage exception: " + e.getMessage()
						+ ". Happened during creating a descriptor: " + type.toString() + " # " + (i + 1));
				pineException.setStackTrace(e.getStackTrace());
				PineSettings.getErrorsHandler().onAdaptorFail(pineException);
			}
			result.add(descriptor);
		}
		return result;
	}

	public static <T> List<T> getDescriptors(Class<T> type, String indexes) {
		return getDescriptors(type, indexes, false);
	}

	public static <T> List<T> getDescriptors(Class<T> type, String indexes, boolean allowEmpty) {
		List<T> result = new ArrayList<T>();
		if (allowEmpty) {
			int[] iterationNumbers = { 0 };
			if (indexes != null) {
				iterationNumbers = getIntArrayFromString(indexes, ";");
			}

			DataTable dataStorage = new DataTable(type.getSimpleName());

			List<HashMap<String, String>> rows = dataStorage.getDataStorageValues();
			for (int i = 0; i < iterationNumbers.length; i++) {
				T descriptor = null;
				try {
					if (iterationNumbers[i] == 0) {
						Constructor<T> c = type.getConstructor(new Class[] { HashMap.class });
						descriptor = c.newInstance(new Object[] { null });
					} else {
						Constructor<T> c = type.getConstructor(new Class[] { HashMap.class });
						descriptor = c.newInstance(new Object[] { rows.get(iterationNumbers[i] - 1) });
					}
				} catch (Exception e) {
					Exception pineException = new Exception("DataStorage exception: " + e.getMessage()
							+ ". Happened during creating a descriptor: " + type.toString() + " # "
							+ iterationNumbers[i]);
					pineException.setStackTrace(e.getStackTrace());
					PineSettings.getErrorsHandler().onAdaptorFail(pineException);
				}
				result.add(descriptor);
			}
		} else {
			if ((!("0").equals(indexes)) && (indexes != null)) {
				int[] iterationNumbers = getIntArrayFromString(indexes, ";");
				DataTable dataStorage = new DataTable(type.getSimpleName());

				List<HashMap<String, String>> rows = dataStorage.getDataStorageValues();
				for (int i = 0; i < iterationNumbers.length; i++) {
					T descriptor = null;
					try {
						Constructor<T> c = type.getConstructor(new Class[] { HashMap.class });
						descriptor = c.newInstance(new Object[] { rows.get(iterationNumbers[i] - 1) });
					} catch (Exception e) {
						Exception pineException = new Exception("DataStorage exception: " + e.getMessage()
								+ ". Happened during creating a descriptor: " + type.toString() + " # "
								+ iterationNumbers[i]);
						pineException.setStackTrace(e.getStackTrace());
						PineSettings.getErrorsHandler().onAdaptorFail(pineException);
					}
					result.add(descriptor);
				}
			}
		}
		return result;
	}

	public static <T> T getDescriptor(Class<T> type, String index) {
		List<T> descriptors = getDescriptors(type, index, true);
		return descriptors.get(0);
	}

	private static int[] getIntArrayFromString(String allElements, String delimiter) {
		if (("").equals(allElements) || (allElements == null)) {
			return new int[0];
		}
		String[] tempArray = allElements.split(delimiter);
		int[] resultArray = new int[tempArray.length];
		for (int i = 0; i < tempArray.length; i++) {
			resultArray[i] = Integer.parseInt(tempArray[i]);
		}
		return resultArray;
	}

}

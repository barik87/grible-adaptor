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
package org.grible.adaptor;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class that contains methods for retrieving data from grible Data Storages and transform it to descriptors objects.
 * 
 * @author Maksym Barvinskyi
 * 
 */
public class DataStorage {

	/**
	 * Retrieves data from grible Data Storage and transforms it to descriptors objects.
	 * 
	 * @param type
	 *            - class of the descriptor (i.e. UserInfo.class);
	 * @return ArrayList of all the specified descriptors found in the storage.
	 */
	public static <T> List<T> getDescriptors(Class<T> type) {
		TestTable dataStorage = new TestTable(type.getSimpleName());
		List<T> result = new ArrayList<T>();
		List<HashMap<String, String>> rows = dataStorage.getDataStorageValues();
		for (int i = 0; i < rows.size(); i++) {
			T descriptor = null;
			try {
				Constructor<T> c = type.getConstructor(new Class[] { HashMap.class });
				descriptor = c.newInstance(new Object[] { rows.get(i) });
			} catch (Exception e) {
				Exception gribleException = new Exception("DataStorage exception: " + e.getMessage()
						+ ". Happened during creating a descriptor: " + type.toString() + " # " + (i + 1));
				gribleException.setStackTrace(e.getStackTrace());
				GribleSettings.getErrorsHandler().onAdaptorFail(gribleException);
			}
			result.add(descriptor);
		}
		return result;
	}

	/**
	 * Retrieves data from grible Data Storage and transforms it to descriptors objects.
	 * 
	 * @param type
	 *            - class of the descriptor (i.e. UserInfo.class);
	 * @param indexes
	 *            - rows indexes of the descriptors to retrieve (i.e. "5", "1;2;2;7"); "0" index in multiple indexes
	 *            ("1;0;7") is not allowed;
	 * @return ArrayList of the descriptors with specified row numbers found in the storage.
	 */
	public static <T> List<T> getDescriptors(Class<T> type, String indexes) {
		return getDescriptors(type, indexes, false);
	}

	/**
	 * Retrieves data from grible Data Storage and transforms it to descriptors objects.
	 * 
	 * @param type
	 *            - class of the descriptor (i.e. UserInfo.class);
	 * @param indexes
	 *            - rows indexes of the descriptors to retrieve (i.e. "5", "1;2;2;7");
	 * @param allowEmpty
	 *            - specifies whether "0" index in multiple indexes (like "1;0;7") is allowed;
	 * @return ArrayList of the descriptors with specified row numbers found in the storage.
	 */
	public static <T> List<T> getDescriptors(Class<T> type, String indexes, boolean allowEmpty) {
		List<T> result = new ArrayList<T>();
		if (allowEmpty) {
			Integer[] iterationNumbers = { 0 };
			if (indexes != null) {
				iterationNumbers = getIntArrayFromString(indexes, ";");
			}
			TestTable dataStorage = new TestTable(type.getSimpleName());
			HashMap<Integer, HashMap<String, String>> rows = dataStorage.getDataStorageValues(iterationNumbers);
			for (int i = 0; i < iterationNumbers.length; i++) {
				T descriptor = null;
				try {
					if (iterationNumbers[i] == 0) {
						Constructor<T> c = type.getConstructor(new Class[] { HashMap.class });
						descriptor = c.newInstance(new Object[] { null });
					} else {
						Constructor<T> c = type.getConstructor(new Class[] { HashMap.class });
						descriptor = c.newInstance(new Object[] { rows.get(iterationNumbers[i]) });
					}
				} catch (Exception e) {
					Exception gribleException = new Exception("DataStorage exception: " + e.getMessage()
							+ ". Happened during creating a descriptor: " + type.toString() + " # "
							+ iterationNumbers[i]);
					gribleException.setStackTrace(e.getStackTrace());
					GribleSettings.getErrorsHandler().onAdaptorFail(gribleException);
				}
				result.add(descriptor);
			}
		} else {
			if ((!("0").equals(indexes)) && (indexes != null)) {
				Integer[] iterationNumbers = getIntArrayFromString(indexes, ";");
				TestTable dataStorage = new TestTable(type.getSimpleName());
				HashMap<Integer, HashMap<String, String>> rows = dataStorage.getDataStorageValues(iterationNumbers);
				for (int i = 0; i < iterationNumbers.length; i++) {
					T descriptor = null;
					try {
						Constructor<T> c = type.getConstructor(new Class[] { HashMap.class });
						descriptor = c.newInstance(new Object[] { rows.get(iterationNumbers[i]) });
					} catch (Exception e) {
						Exception gribleException = new Exception("DataStorage exception: " + e.getMessage()
								+ ". Happened during creating a descriptor: " + type.toString() + " # "
								+ iterationNumbers[i]);
						gribleException.setStackTrace(e.getStackTrace());
						GribleSettings.getErrorsHandler().onAdaptorFail(gribleException);
					}
					result.add(descriptor);
				}
			}
		}
		return result;
	}

	/**
	 * Retrieves data from grible Data Storage and transforms it to the single descriptor object.
	 * 
	 * @param type
	 *            - class of the descriptor (i.e. UserInfo.class);
	 * @param index
	 *            - row index of the descriptor to retrieve (i.e. "1", "5"); if "0", returns an empty descriptor;
	 * @return Descriptor for specified row number found in the storage or an empty (which all properties are null) descriptor.
	 */
	public static <T> T getDescriptor(Class<T> type, String index) {
		List<T> descriptors = getDescriptors(type, index, true);
		return descriptors.get(0);
	}

	private static Integer[] getIntArrayFromString(String allElements, String delimiter) {
		if (("").equals(allElements) || (allElements == null)) {
			return new Integer[0];
		}
		String[] tempArray = allElements.split(delimiter);
		Integer[] resultArray = new Integer[tempArray.length];
		for (int i = 0; i < tempArray.length; i++) {
			resultArray[i] = Integer.parseInt(tempArray[i]);
		}
		return resultArray;
	}
}
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
package org.pine.adaptor.errors;

/**
 * Default handler of exceptions that occur in the pine adaptor. It will simply print the stacktrace to the console.
 * 
 * @author Maksym Barvinskyi
 */
public class SimpleErrorsHandler implements ErrorsHandler {

	@Override
	public void onAdaptorFail(Exception e) {
		e.printStackTrace();
	}

}

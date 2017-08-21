/*******************************************************************************
 * ============LICENSE_START====================================================
 * * org.onap.aaf
 * * ===========================================================================
 * * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 * * ===========================================================================
 * * Licensed under the Apache License, Version 2.0 (the "License");
 * * you may not use this file except in compliance with the License.
 * * You may obtain a copy of the License at
 * * 
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 * * 
 *  * Unless required by applicable law or agreed to in writing, software
 * * distributed under the License is distributed on an "AS IS" BASIS,
 * * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * * See the License for the specific language governing permissions and
 * * limitations under the License.
 * * ============LICENSE_END====================================================
 * *
 * * ECOMP is a trademark and service mark of AT&T Intellectual Property.
 * *
 ******************************************************************************/
package org.onap.rosetta;

import org.onap.inno.env.Env;
import org.onap.inno.env.TimeTaken;

public interface Parse<IN, S> {
	public Parsed<S> parse(IN in, Parsed<S> parsed) throws ParseException;
	
	// EVENTS
	public static final char NONE = 0;
	public static final char START_DOC = 1;
	public static final char END_DOC = 2;
	public static final char ATTRIB = 3;
	
	public static final char NEXT = ',';
	public static final char START_OBJ = '{';
	public static final char END_OBJ = '}';
	public static final char START_ARRAY = '[';
	public static final char END_ARRAY = ']';
	
	public Parsed<S> newParsed() throws ParseException;
	public TimeTaken start(Env env); 
	
}

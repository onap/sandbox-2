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
package org.onap.rosetta.marshal;


import org.onap.rosetta.Marshal;
import org.onap.rosetta.Parse;
import org.onap.rosetta.Parsed;

public abstract class FieldMarshal<T> extends Marshal<T> {
	private String name;

	public FieldMarshal(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public Parsed<State> parse(T t, Parsed<State> parsed) {
		parsed.state.ladder.push(DONE_ITERATOR);
		parsed.event = Parse.NEXT;
		parsed.name = name;
		parsed.isString = data(t,parsed.sb);
		return parsed;
	}

	/**
	 * Write Value to StringBuilder
	 * Return true if value looks like a String
	 *        false if it is Numeric
	 * @param t
	 * @param sb
	 * @return
	 */
	protected abstract boolean data(T t, StringBuilder sb);
	
}

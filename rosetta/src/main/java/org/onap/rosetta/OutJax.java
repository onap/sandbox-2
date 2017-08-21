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

import java.io.IOException;
import java.io.Writer;

public class OutJax extends Out {
	private JaxEval jaxEval;

	public OutJax(JaxEval je) {
		this.jaxEval = je;
	}

	@Override
	public <IN,S> void extract(IN in, Writer writer, Parse<IN, S> parse, boolean... options) throws IOException, ParseException {
		Parsed<S> p = parse.newParsed();
		JaxEval je = this.jaxEval;
		while((p = parse.parse(in,p.reuse())).valid()) {
			if(je==null)throw new ParseException("Incomplete content");
			je = je.eval(p);
		}
		
	}
	
	@Override
	public String logName() {
		return "Rosetta JAX";
	}



}

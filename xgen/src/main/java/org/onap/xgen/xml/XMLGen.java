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
package org.onap.xgen.xml;

import java.io.Writer;

import org.onap.xgen.XGen;;

public class XMLGen extends XGen<XMLGen> {
	private final String XML_TAG; 
	
	public XMLGen(Writer w) {
		this(w,"UTF-8");
	}
	
	public XMLGen(Writer w, String encoding) {
		super(w);
		XML_TAG="<?xml version=\"1.0\" encoding=\"" + encoding + "\" standalone=\"yes\"?>"; 
	}

	public XMLGen xml() {
		forward.println(XML_TAG);
		return this;
	}
}

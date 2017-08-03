/*******************************************************************************
 * ============LICENSE_START====================================================
 * * org.onap.aai
 * * ===========================================================================
 * * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 * * Copyright © 2017 Amdocs
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
package com.att.xgen.xml;

import java.io.IOException;
import java.io.Writer;

import com.att.inno.env.APIException;
import com.att.xgen.CacheGen;
import com.att.xgen.Code;

public class XMLCacheGen extends CacheGen<XMLGen> {

	public XMLCacheGen(int flags, Code<XMLGen> code) throws APIException,
			IOException {
		super(flags, code);
	}

	@Override
	public XMLGen create(int style, Writer w) {
		XMLGen xg = new XMLGen(w);
		xg.pretty = (style & PRETTY)==PRETTY;
		return xg;
	}

}

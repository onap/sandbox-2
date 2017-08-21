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
package org.onap.xgen.html;

import java.io.IOException;
import java.io.Writer;

import org.onap.inno.env.APIException;
import org.onap.xgen.CacheGen;
import org.onap.xgen.Code;

public class HTMLCacheGen extends CacheGen<HTMLGen> {
	protected int flags;

	public HTMLCacheGen(int flags, Code<HTMLGen> code) throws APIException,IOException {
		super(flags, code);
		this.flags = flags;
	}

	@Override
	public HTMLGen create(int htmlStyle, Writer w) {
		HTMLGen hg;
		switch(htmlStyle&(CacheGen.HTML4|CacheGen.HTML5)) {
			case CacheGen.HTML4:
				hg = new HTML4Gen(w);
				break;
			case CacheGen.HTML5:
			default:
				hg = new HTML5Gen(w);
				break;

		}
		hg.pretty = (htmlStyle&CacheGen.PRETTY)>0;
		return hg;
	}

	protected HTMLGen clone(Writer w) {
		return create(flags,w);
	}
}

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
package org.onap.aaf.xgen;

import java.io.IOException;

import org.onap.aaf.inno.env.APIException;
import org.onap.aaf.inno.env.Env;
import org.onap.aaf.inno.env.Trans;
import org.onap.aaf.xgen.html.State;

/**
 * Special Code Interface to gain access to Transaction
 * and State information
 *
 */
public abstract class DynamicCode<G extends XGen<G>, AS extends State<Env>, TRANS extends Trans> implements Code<G> {
	public abstract void code(AS state, TRANS trans, Cache<G> cache, G xgen) throws APIException, IOException;
	
	// We expect not to have this section of the code engaged at any time
	public void code(Cache<G> cache, G xgen) throws APIException, IOException {
		code(null, null,cache,xgen);
	}
}

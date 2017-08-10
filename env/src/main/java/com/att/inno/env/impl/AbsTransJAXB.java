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
package com.att.inno.env.impl;

import javax.xml.namespace.QName;
import javax.xml.validation.Schema;

import com.att.inno.env.APIException;
import com.att.inno.env.DataFactory;
import com.att.inno.env.EnvJAXB;
import com.att.inno.env.TransJAXB;

public abstract class AbsTransJAXB extends AbsTrans<EnvJAXB> implements TransJAXB {
	public AbsTransJAXB(EnvJAXB env) {
		super(env);
	}
	
//	@Override
	public <T> DataFactory<T> newDataFactory(Class<?>... classes) throws APIException {
		return delegate.newDataFactory(classes);
	}

//	@Override
	public <T> DataFactory<T> newDataFactory(Schema schema, Class<?>... classes) throws APIException {
		return delegate.newDataFactory(schema, classes);
	}

//	@Override
	public <T> DataFactory<T> newDataFactory(QName qName, Class<?>... classes) throws APIException {
		return delegate.newDataFactory(qName, classes);
	}

//	@Override
	public <T> DataFactory<T> newDataFactory(Schema schema, QName qName, Class<?>... classes) throws APIException {
		return delegate.newDataFactory(schema, qName, classes);
	}

}

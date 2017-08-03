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
package com.att.inno.env.old;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import com.att.inno.env.APIException;
import com.att.inno.env.Data;
import com.att.inno.env.DataFactory;
import com.att.inno.env.Env;

public interface OldDataFactory<T> extends DataFactory<T> {
	public abstract String stringify(T type) throws APIException;
	public abstract void stringify(T type, OutputStream os)	throws APIException;
	public abstract void stringify(T type, Writer writer) throws APIException;
	public abstract T objectify(InputStream is) throws APIException;
	public abstract T objectify(Reader rdr) throws APIException;
	public abstract T objectify(String text) throws APIException;
	public abstract T newInstance() throws APIException;
	public abstract Data<T> newData(T type);
	public abstract Data<T> newDataFromStream(Env env, InputStream input) throws APIException;
	public abstract Data<T> newDataFromString(String string);
	
}


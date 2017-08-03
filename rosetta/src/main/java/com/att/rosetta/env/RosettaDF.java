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
package com.att.rosetta.env;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.validation.Schema;

import com.att.inno.env.APIException;
import com.att.inno.env.BaseDataFactory;
import com.att.inno.env.Data;
import com.att.inno.env.Data.TYPE;
import com.att.inno.env.DataFactory;
import com.att.inno.env.Env;
import com.att.inno.env.TimeTaken;
import com.att.inno.env.Trans;
import com.att.inno.env.jaxb.JAXBmar;
import com.att.inno.env.jaxb.JAXBumar;
import com.att.rosetta.InJson;
import com.att.rosetta.InXML;
import com.att.rosetta.JaxInfo;
import com.att.rosetta.Marshal;
import com.att.rosetta.Out;
import com.att.rosetta.OutJson;
import com.att.rosetta.OutRaw;
import com.att.rosetta.OutXML;
import com.att.rosetta.Parse;
import com.att.rosetta.ParseException;
import com.att.rosetta.marshal.DocMarshal;

public class RosettaDF<T> extends BaseDataFactory implements DataFactory<T>  {
	
	static InJson inJSON = new InJson();
	InXML  inXML;

	static OutJson outJSON = new OutJson();
	OutXML outXML;
	static OutRaw outRAW = new OutRaw();
	
	// Temporary until we write JAXB impl...
	JAXBmar jaxMar;
	JAXBumar jaxUmar;
	
	private Parse<Reader,?> defaultIn;
	private Out defaultOut;
	private RosettaEnv env;
	private TYPE inType;
	private TYPE outType;
	private int defOption;
	Marshal<T> marshal = null;
	

	/**
	 * Private constructor to setup Type specific data manipulators
	 * @param schema
	 * @param rootNs
	 * @param cls
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws ClassNotFoundException
	 * @throws ParseException
	 * @throws JAXBException
	 */
	// package on purpose
	RosettaDF(RosettaEnv env, Schema schema, String rootNs, Class<T> cls) throws APIException {
		this.env = env;
		try {
		// Note: rootNs can be null, in order to derive content from Class.  
		JaxInfo ji = rootNs==null?JaxInfo.build(cls):JaxInfo.build(cls,rootNs);
		// Note: JAXBmar sets qname to null if not exists
		jaxMar = new JAXBmar(rootNs==null?null:new QName("xmlns",rootNs),cls);
		// Note: JAXBumar sets schema to null if not exists
		jaxUmar = new JAXBumar(schema, cls);
		
		defaultIn = inXML = new InXML(ji);
		defaultOut = outXML = new OutXML(ji);
		inType=outType=Data.TYPE.XML;
		defOption = 0;
		} catch (Exception e) {
			throw new APIException(e);
		}
	}
	

	// @Override
	public RosettaData<T> newData() {
		RosettaData<T> data = new RosettaData<T>(env, this)			
			.in(inType)
			.out(outType)
			.option(defOption);
		return data;
	}

	// @Override
	public RosettaData<T> newData(Env trans) {
		RosettaData<T> data = new RosettaData<T>(trans, this)
			.in(inType)
			.out(outType)
			.option(defOption);
		return data;
	}

	@SuppressWarnings("unchecked")
	// @Override
	public Class<T> getTypeClass() {
		return (Class<T>)jaxMar.getMarshalClass();
	}

	public RosettaDF<T> in(Data.TYPE type) {
		inType = type;
		defaultIn=getIn(type==Data.TYPE.DEFAULT?Data.TYPE.JSON:type);
		return this;
	}

	/**
	 * If exists, first option is "Pretty", second is "Fragment"
	 * 
	 * @param options
	 * @return
	 */
	public RosettaDF<T> out(Data.TYPE type) {
		outType = type;
		defaultOut = getOut(type==Data.TYPE.DEFAULT?Data.TYPE.JSON:type);
		return this;
	}
	
	public Parse<Reader,?> getIn(Data.TYPE type) {
		switch(type) {
			case DEFAULT:
				return defaultIn;
			case JSON:
				return inJSON;
			case XML:
				return inXML;
			default:
				return defaultIn;
		}
	}
	
	public Out getOut(Data.TYPE type) {
		switch(type) {
			case DEFAULT:
				return defaultOut;
			case JSON:
				return outJSON;
			case XML:
				return outXML;
			case RAW:
				return outRAW;
			default:
				return defaultOut;
		}
	}
	
	public int logType(com.att.inno.env.Data.TYPE ot) {
		switch(ot) {
			case JSON:
				return Env.JSON;
			default:
				return Env.XML;
		}
	}


	public RosettaEnv getEnv() {
		return env;
	}


	public Data.TYPE getInType() {
		return inType;
	}

	public Data.TYPE getOutType() {
		return outType;
	}

	public RosettaDF<T> option(int option) {
		defOption = option;
		
		return this;
	}

	/**
	 * Assigning Root Marshal Object
	 * 
	 * Will wrap with DocMarshal Object if not already
	 * 
	 * @param marshal
	 * @return
	 */
	public RosettaDF<T> rootMarshal(Marshal<T> marshal) {
		if(marshal instanceof DocMarshal) {
			this.marshal = marshal;
		} else {
			this.marshal = DocMarshal.root(marshal);
		}
		return this;
	}
	
	public void direct(Trans trans, T t, OutputStream os, boolean ... options) throws APIException, IOException {
		Out out = getOut(outType);
		TimeTaken tt = trans.start(out.logName(),logType(outType)); // determine from Out.. without dependency on Env?
		try {
			if(marshal==null) { // Unknown marshaller... do working XML marshal/extraction
				StringWriter sw = new StringWriter();
				jaxMar.marshal(trans.debug(), t, sw, options);
				out.extract(new StringReader(sw.toString()), new OutputStreamWriter(os), inXML,options);
			} else {
				out.extract(t, new OutputStreamWriter(os), marshal,options);
			}
		} catch (Exception e) {
			throw new APIException(e);
		} finally {
			tt.done();
		}
	}

	public void direct(Trans trans, T t, Writer writer, boolean ... options) throws APIException, IOException {
		Out out = getOut(outType);
		TimeTaken tt = trans.start(out.logName(),logType(outType)); // determine from Out.. without dependency on Env?
		try {
			if(marshal==null) { // Unknown marshaller... do working XML marshal/extraction
				StringWriter sw = new StringWriter();
				jaxMar.marshal(trans.debug(), t, sw, options);
				out.extract(new StringReader(sw.toString()), writer, inXML,options);
			} else {
				out.extract(t, writer, marshal,options);
			}
		} catch (Exception e) {
			throw new APIException(e);
		} finally {
			tt.done();
		}
	}


}

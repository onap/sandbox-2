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
package com.data.test.obj;

import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import com.att.rosetta.marshal.DataWriter;
import com.att.rosetta.marshal.FieldArray;
import com.att.rosetta.marshal.FieldDate;
import com.att.rosetta.marshal.FieldDateTime;
import com.att.rosetta.marshal.FieldHexBinary;
import com.att.rosetta.marshal.FieldNumeric;
import com.att.rosetta.marshal.FieldString;
import com.att.rosetta.marshal.ObjMarshal;

import types.xsd.Multi.Single;

public class SingleMarshal extends ObjMarshal<Single> {
	public SingleMarshal() {
		add(new FieldString<Single>("str") {
			@Override
			protected String data(Single t) {
				return t.getStr();
			}
		});
		
		add(new FieldNumeric<Integer, Single>("int") {
			@Override
			protected Integer data(Single t) {
				return t.getInt();
			}
		});
		
		add(new FieldNumeric<Long,Single>("long") {
			@Override
			protected Long data(Single t) {
				return t.getLong();
			}
		});

		add(new FieldDate<Single>("date") {
			@Override
			protected XMLGregorianCalendar data(Single t) {
				return t.getDate();
			}
		});

		add(new FieldDateTime<Single>("datetime") {
			@Override
			protected XMLGregorianCalendar data(Single t) {
				return t.getDate();
			}
		});
		
		add(new FieldHexBinary<Single>("binary") {
			@Override
			protected byte[] data(Single t) {
				return t.getBinary();
			}
		});
		
		add(new FieldArray<Single,String>("array", DataWriter.STRING) {
			@Override
			protected List<String> data(Single t) {
				return t.getArray();
			}
		});

	}
}

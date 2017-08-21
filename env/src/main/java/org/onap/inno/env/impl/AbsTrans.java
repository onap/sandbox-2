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
package org.onap.inno.env.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.onap.inno.env.Env;
import org.onap.inno.env.LogTarget;
import org.onap.inno.env.Slot;
import org.onap.inno.env.StoreImpl;
import org.onap.inno.env.TimeTaken;
import org.onap.inno.env.TransStore;

public abstract class AbsTrans<ENV extends Env> implements TransStore {
	private static final float[] EMPTYF = new float[0];
	private static final Object[] EMPTYO = new Object[0];
	
	protected ENV delegate;
	protected List<TimeTaken> trail = new ArrayList<TimeTaken>(30);
	private Object[] state;
	
	
    public AbsTrans(ENV delegate) {
    	this.delegate = delegate;
    	state = delegate instanceof StoreImpl?((StoreImpl) delegate).newTransState():EMPTYO;
	}

	//	@Override
	public LogTarget fatal() {
		return delegate.fatal();
	}

//	@Override
	public LogTarget error() {
		return delegate.error();
	}

//	@Override
	public LogTarget audit() {
		return delegate.audit();
	}

//	@Override
	public LogTarget init() {
		return delegate.init();
	}

//	@Override
	public LogTarget warn() {
		return delegate.warn();
	}

//	@Override
	public LogTarget info() {
		return delegate.info();
	}

//	@Override
	public LogTarget debug() {
		return delegate.debug();
	}

//	@Override
	public LogTarget trace() {
		return delegate.trace();
	}

	/**
	 * Let the final Trans Implementation choose the exact kind of TimeTaken to use
	 * @param name
	 * @param flag
	 * @return
	 */
	protected abstract TimeTaken newTimeTaken(String name, int flag);
	
//	@Override
	public final TimeTaken start(String name, int flag) {
		TimeTaken tt = newTimeTaken(name,flag);
		trail.add(tt);
		return tt;
	}
	
//	@Override
	public final void checkpoint(String name) {
		TimeTaken tt = newTimeTaken(name,CHECKPOINT);
		tt.done();
		trail.add(tt);
	}

	public final void checkpoint(String name, int additionalFlag) {
		TimeTaken tt = newTimeTaken(name,CHECKPOINT|additionalFlag);
		tt.done();
		trail.add(tt);
	}

	@Override
	public Metric auditTrail(int indent, StringBuilder sb, int ... flags) {
		return auditTrail(info(),indent,sb,flags);
	}
	
	@Override
	public Metric auditTrail(LogTarget lt, int indent, StringBuilder sb, int ... flags) {
		Metric metric = new Metric();
		int last = (metric.entries = trail.size()) -1;
		metric.buckets = flags.length==0?EMPTYF:new float[flags.length];
		if(last>=0) {
			TimeTaken first = trail.get(0);
			// If first entry is sub, then it's actually the last "end" as well
			// otherwise, check end
			//long end = (first.flag&SUB)==SUB?first.end():trail.get(last).end();
			long end = trail.get(last).end();
			metric.total = (end - first.start) / 1000000f;
		}
		
		if(sb==null) {
			for(TimeTaken tt : trail) {
				float ms = tt.millis();
				for(int i=0;i<flags.length;++i) {
					if(tt.flag == flags[i]) metric.buckets[i]+=ms;
				}
			}
		} else if(!lt.isLoggable()) {
			boolean first = true;
			for(TimeTaken tt : trail) {
				float ms = tt.millis();
				for(int i=0;i<flags.length;++i) {
					if(tt.flag == flags[i]) metric.buckets[i]+=ms;
				}
				if((tt.flag&ALWAYS)==ALWAYS) {
					if(first) first = false;
					else sb.append('/');
					sb.append(tt.name);
				}
			}			
		} else {
			Stack<Long> stack = new Stack<Long>();
			for(TimeTaken tt : trail) {
				// Create Indentation based on SUB
				while(!stack.isEmpty() && tt.end()>stack.peek()) {
					--indent;
					stack.pop();
				}
				for(int i=0;i<indent;++i) {
					sb.append("  ");
				}
				tt.output(sb);
				sb.append('\n');
				if((tt.flag&SUB)==SUB) {
					stack.push(tt.end());
					++indent;
				}
				
				// Add time values to Metric
				float ms = tt.millis();
				for(int i=0;i<flags.length;++i) {
					if(tt.flag == flags[i]) metric.buckets[i]+=ms;
				}
			}
		}
		return metric;
	}

	/**
	 * Put data into the Trans State at the right slot 
	 */
//	@Override
	public void put(Slot slot, Object value) {
		slot.put(state, value);
	}

	/**
	 *  Get data from the Trans State from the right slot
	 *  
	 *  This will do a cast to the expected type derived from Default
	 */
//	@Override
	@SuppressWarnings("unchecked")
	public<T> T get(Slot slot, T deflt) {
		Object o;
		try {
			o = slot.get(state);
		} catch(ArrayIndexOutOfBoundsException e) {
			// Env State Size has changed because of dynamic Object creation... Rare event, but needs to be covered
			Object[] temp = ((StoreImpl) delegate).newTransState();
			System.arraycopy(state, 0, temp, 0, state.length);
			state = temp;
			o=null;
		}
		return o==null?deflt:(T)o;
	}


}

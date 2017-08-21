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
package org.onap.inno.env.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.onap.inno.env.APIException;
import org.onap.inno.env.Creatable;
import org.onap.inno.env.Env;
import org.onap.inno.env.LifeCycle;


/**
 * <h1>RefreshableThreadObject</h1>
 * This is a ThreadLocal like implementation, but it responds to 
 * the {@link LifeCycle} mechanism for configuration refreshes, and 
 * implements {@link Creatable} (for use in destroy, etc).<p>
 * 
 * In addition to the Thread instance semantics, it compares when the object
 * was created versus the last "refresh(env)" call when getting, for the
 * thread, and if necessary to replace the created object, destroying the 
 * previous.<p>
 * 
 * In most cases, it's better to use the new "Pool" mechanism, as it deals with 
 * gaining and returning resources on an as needed basis.  This, however, remains
 * in the cases where specific Objects need to be retained to specific Threads.<p>
 * 
 * There is no way to do this kind of specialized behavior in ThreadLocal.
 * 
 *
 * @param <T>
 */
public class RefreshableThreadObject<T extends Creatable<T>> {
	private Map<Thread,T> objs;
	private long refreshed;
	private Constructor<T> cnst;
	
	/**
	 * The passed in class <b>must</b> implement the constructor
	 * <pre>
	 *   public MyClass(Env env) {
	 *     ...
	 *   }
	 * </pre>
	 * @param clss
	 * @throws APIException
	 */
	public RefreshableThreadObject(Class<T> clss) throws APIException {
		objs = Collections.synchronizedMap(new HashMap<Thread,T>());
		try {
			cnst = clss.getConstructor(new Class[]{Env.class} );
		} catch (Exception e) {
			throw new APIException(e);
		}
	}
	
	/**
	 * Get the "T" class from the current thread
	 * 
	 * @param env
	 * @return T
	 * @throws APIException
	 */
	public T get(Env env) throws APIException {
		Thread t = Thread.currentThread();
		T obj = objs.get(t);
		if(obj==null || refreshed>obj.created()) {
			try {
				obj = cnst.newInstance(new Object[]{env});
			} catch (InvocationTargetException e) {
				throw new APIException(e.getTargetException());
			} catch (Exception e) {
				throw new APIException(e);
			}
			T destroyMe = objs.put(t,obj);
			if(destroyMe!=null) {
				destroyMe.destroy(env);
			}
		} 
		return obj;
	}
	
	/**
	 * Mark the timestamp of refreshed.
	 * 
	 * @param env
	 */
	public void refresh(Env env) {
		refreshed = System.currentTimeMillis();
	}
	
	/**
	 * Remove the object from the Thread instances
	 * @param env
	 */
	public void remove(Env env) {
		T obj = objs.remove(Thread.currentThread());
		if(obj!=null)
			obj.destroy(env);
	}
}

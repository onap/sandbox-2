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
/*
 * Pool
 * 
 * 5/27/2011
 */
package org.onap.inno.env.util;

import java.util.LinkedList;

import org.onap.inno.env.APIException;
import org.onap.inno.env.LogTarget;

/**
 * This Class pools on an As-Needed-Basis any particular kind of class, which is
 * quite suitable for expensive operations.
 * 
 * The user calls "get" on a Pool, and if a waiting resource (T) is available,
 * it will be returned. Otherwise, one will be created with the "Creator" class
 * (must be defined for (T)).
 * 
 * You can Prime the instances to avoid huge startup costs
 * 
 * The returned "Pooled" object simply has to call "done()" and the object is
 * returned to the pool. If the developer does not return the object, a memory
 * leak does not occur. There are no references to the object once "get" is
 * called. However, the developer who does not return the object when done
 * obviates the point of the pool, as new Objects are created in place of the
 * Object not returned when another call to "get" is made.
 * 
 * There is a cushion of extra objects, currently defaulted to MAX_RANGE. If the
 * items returned become higher than the MAX_RANGE, the object is allowed to go
 * out of scope, and be cleaned up. the default can be changed on a per-pool
 * basis.
 * 
 * 
 * 
 * @param <T>
 */
public class Pool<T> {
	/**
	 * This is a constant which specified the default maximum number of unused
	 * objects to be held at any given time.
	 */
	private static final int MAX_RANGE = 6; // safety

	/**
	 * only Simple List needed.
	 * 
	 * NOTE TO MAINTAINERS: THIS OBJECT DOES IT'S OWN SYNCHRONIZATION. All
	 * changes that touch list must account for correctly synchronizing list.
	 */
	private LinkedList<Pooled<T>> list;

	/**
	 * keep track of how many elements exist, to avoid asking list.
	 */
	private int count;

	/**
	 * Spares are those Object that are primed and ready to go.
	 */
	private int spares;

	/**
	 * Actual MAX number of spares allowed to hang around. Can be set to
	 * something besides the default MAX_RANGE.
	 */
	private int max_range = MAX_RANGE;

	/**
	 * The Creator for this particular pool. It must work for type T.
	 */
	private Creator<T> creator;

	/**
	 * Create a new Pool, given the implementation of Creator<T>, which must be
	 * able to create/destroy T objects at will.
	 * 
	 * @param creator
	 */
	public Pool(Creator<T> creator) {
		count = spares = 0;
		this.creator = creator;
		list = new LinkedList<Pooled<T>>();
	}

	/**
	 * Preallocate a certain number of T Objects. Useful for services so that
	 * the first transactions don't get hit with all the Object creation costs
	 * 
	 * @param lt
	 * @param prime
	 * @throws APIException
	 */
	public void prime(LogTarget lt, int prime) throws APIException {
		for (int i = 0; i < prime; ++i) {
			Pooled<T> pt = new Pooled<T>(creator.create(), this, lt);
			synchronized (list) {
				list.addFirst(pt);
				++count;
			}
		}

	}

	/**
	 * Destroy and remove all remaining objects. This is valuable for closing
	 * down all Allocated objects cleanly for exiting. It is also a good method
	 * for removing objects when, for instance, all Objects are invalid because
	 * of broken connections, etc.
	 */
	public void drain() {
		synchronized (list) {
			for (int i = 0; i < list.size(); ++i) {
				Pooled<T> pt = list.remove();
				creator.destroy(pt.content);
				pt.logTarget.log("Pool drained ", creator.toString());
			}
			count = spares = 0;
		}

	}

	/**
	 * This is the essential function for Pool. Get an Object "T" inside a
	 * "Pooled<T>" object. If there is a spare Object, then use it. If not, then
	 * create and pass back.
	 * 
	 * This one uses a Null LogTarget
	 * 
	 * IMPORTANT: When the use of this object is done (and the object is still
	 * in a valid state), then "done()" should be called immediately to allow
	 * the object to be reused. That is the point of the Pool...
	 * 
	 * If the Object is in an invalid state, then "toss()" should be used so the
	 * Pool doesn't pass on invalid objects to others.
	 * 
	 * @param lt
	 * @return
	 * @throws APIException
	 */
	public Pooled<T> get() throws APIException {
		Pooled<T> pt;
		synchronized (list) {
			if (list.isEmpty()) {
				pt = null;
			} else {
				pt = list.removeLast();
				--count;
				creator.reuse(pt.content);
			}
		}
		if (pt == null) {
			if (spares < max_range)
				++spares;
			pt = new Pooled<T>(creator.create(), this, LogTarget.NULL);
		} else {
			if (spares > 1)
				--spares;
		}
		return pt;
	}

	/**
	 * This is the essential function for Pool. Get an Object "T" inside a
	 * "Pooled<T>" object. If there is a spare Object, then use it. If not, then
	 * create and pass back.
	 * 
	 * If you don't have access to a LogTarget from Env, use LogTarget.NULL
	 * 
	 * IMPORTANT: When the use of this object is done (and the object is still
	 * in a valid state), then "done()" should be called immediately to allow
	 * the object to be reused. That is the point of the Pool...
	 * 
	 * If the Object is in an invalid state, then "toss()" should be used so the
	 * Pool doesn't pass on invalid objects to others.
	 * 
	 * @param lt
	 * @return
	 * @throws APIException
	 */
	public Pooled<T> get(LogTarget lt) throws APIException {
		Pooled<T> pt;
		synchronized (list) {
			if (list.isEmpty()) {
				pt = null;
			} else {
				pt = list.remove();
				--count;
				creator.reuse(pt.content);
			}
		}
		if (pt == null) {
			if (spares < max_range)
				++spares;
			pt = new Pooled<T>(creator.create(), this, lt);
			lt.log("Pool created ", creator.toString());
		} else {
			if (spares > 1)
				--spares;
		}
		return pt;
	}

	/**
	 * This function will validate whether the Objects are still in a usable
	 * state. If not, they are tossed from the Pool. This is valuable to have
	 * when Remote Connections go down, and there is a question on whether the
	 * Pooled Objects are still functional.
	 * 
	 * @return
	 */
	public boolean validate() {
		boolean rv = true;
		synchronized (list) {
			for (Pooled<T> t : list) {
				if (!creator.isValid(t.content)) {
					rv = false;
					t.toss();
					list.remove(t);
				}
			}
		}
		return rv;
	}

	/**
	 * This is an internal method, used only by the Internal Pooled<T> class.
	 * 
	 * The Pooled<T> class "offers" it's Object back after use. It is an
	 * "offer", because Pool will simply destroy and remove the object if it has
	 * more than enough spares.
	 * 
	 * @param lt
	 * @param used
	 * @return
	 */
	// Used only by Pooled<T>
	private boolean offer(LogTarget lt, Pooled<T> used) {
		if (count < spares) {
			synchronized (list) {
				list.addFirst(used);
				++count;
			}
			lt.log("Pool recovered ", creator.toString());
		} else {
			lt.log("Pool destroyed ", creator.toString());
			creator.destroy(used.content);
		}
		return false;
	}

	/**
	 * The Creator Interface give the Pool the ability to Create, Destroy and
	 * Validate the Objects it is maintaining. Thus, it is a specially written
	 * Implementation for each type.
	 * 
	 * 
	 * @param <T>
	 */
	public interface Creator<T> {
		public T create() throws APIException;

		public void destroy(T t);

		public boolean isValid(T t);

		public void reuse(T t);
	}

	/**
	 * The "Pooled<T>" class is the transient class that wraps the actual Object
	 * T for API use/ It gives the ability to return ("done()", or "toss()") the
	 * Object to the Pool when processing is finished.
	 * 
	 * For Safety, i.e. to avoid memory leaks and invalid Object States, there
	 * is a "finalize" method. It is strictly for when coder forgets to return
	 * the object, or perhaps hasn't covered the case during Exceptions or
	 * Runtime Exceptions with finally (preferred). This should not be
	 * considered normal procedure, as finalize() is called at an undetermined
	 * time during garbage collection, and is thus rather useless for a Pool.
	 * However, we don't want Coding Mistakes to put the whole program in an
	 * invalid state, so if something happened such that "done()" or "toss()"
	 * were not called, the resource is still cleaned up as well as possible.
	 * 
	 * 
	 * @param <T>
	 */
	public static class Pooled<T> {
		public final T content;
		private Pool<T> pool;
		protected LogTarget logTarget;

		/**
		 * Create the Wrapping Object Pooled<T>.
		 * 
		 * @param t
		 * @param pool
		 * @param logTarget
		 */
		public Pooled(T t, Pool<T> pool, LogTarget logTarget) {
			content = t;
			this.pool = pool;
			this.logTarget = logTarget;
		}

		/**
		 * This is the key API for the Pool, as calling "done()" offers this
		 * object back to the Pool for reuse.
		 * 
		 * Do not use the Pooled<T> object again after calling "done()".
		 */
		public void done() {
			if (pool != null) {
				pool.offer(logTarget, this);
			}
		}

		/**
		 * The user of the Object may discover that the Object t is no longer in
		 * a valid state. Don't put Garbage back in the Refrigerator... Toss it,
		 * if it's no longer valid.
		 * 
		 * toss() is also used for draining the Pool, etc.
		 * 
		 * toss() will attempt to destroy the Object by using the Creator
		 * Interface.
		 * 
		 */
		public void toss() {
			if (pool != null) {
				pool.creator.destroy(content);
			}
			// Don't allow finalize to put it back in.
			pool = null;
		}

		/**
		 * Just in case someone neglected to offer back object... Do not rely on
		 * this, as there is no specific time when finalize is called, which
		 * rather defeats the purpose of a Pool.
		 */
		@Override
		protected void finalize() throws Throwable {
			if (pool != null) {
				done();
				pool = null;
			}
		}
	}

	/**
	 * Get the maximum number of spare objects allowed at any moment
	 * 
	 * @return
	 */
	public int getMaxRange() {
		return max_range;
	}

	/**
	 * Set a Max Range for numbers of spare objects waiting to be used.
	 * 
	 * No negative numbers are allowed
	 * 
	 * @return
	 */
	public void setMaxRange(int max_range) {
		// Do not allow negative numbers
		this.max_range = Math.max(0, max_range);
	}

}

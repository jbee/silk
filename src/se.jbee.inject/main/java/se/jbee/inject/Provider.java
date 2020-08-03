/*
 *  Copyright (c) 2012-2019, Jan Bernitt
 *	
 *  Licensed under the Apache License, Version 2.0, http://www.apache.org/licenses/LICENSE-2.0
 */
package se.jbee.inject;

/**
 * A indirection that resolves the instance lazily when {@link #provide()} is
 * invoked. This is mainly used to allow the injection and usage of instances
 * that have a more unstable scope into an instance of a more stable scope.
 * 
 * Usage of {@link Provider}s to circumvent scoping limitations is explicitly
 * installed using the buildin-{@link se.jbee.inject.bind.Bundle}.
 * 
 * But it is also very easy to use another similar provider interface by
 * installing a similar bridge {@link Supplier}.
 * 
 * @author Jan Bernitt (jan@jbee.se)
 */
@FunctionalInterface
public interface Provider<T> {

	/**
	 * @return The lazily resolved instance. Implementations should make sure
	 *         that calling this method multiple times does cache the result if
	 *         that is semantically correct.
	 * @throws UnresolvableDependency in case the underlying implementation
	 *             could not provide the instance due to lack of suitable
	 *             declarations.
	 */
	T provide() throws UnresolvableDependency;
}

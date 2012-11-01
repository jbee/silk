/*
 *  Copyright (c) 2012, Jan Bernitt 
 *			
 *  Licensed under the Apache License, Version 2.0, http://www.apache.org/licenses/LICENSE-2.0
 */
package se.jbee.inject;

import static se.jbee.inject.Emergence.emergence;
import static se.jbee.inject.Instance.instance;
import static se.jbee.inject.Type.raw;

import java.util.Arrays;
import java.util.Iterator;

import se.jbee.inject.DIRuntimeException.DependencyCycleException;
import se.jbee.inject.DIRuntimeException.MoreFrequentExpiryException;

/**
 * Describes what is wanted/needed as parameter to construct a instance of T.
 * 
 * @author Jan Bernitt (jan.bernitt@gmx.de)
 */
public final class Dependency<T>
		implements Typed<T>, Named, Parameter<T>, Iterable<Injection> {

	private static final Injection[] UNTARGETED = new Injection[0];

	public static <T> Dependency<T> dependency( Class<T> type ) {
		return dependency( raw( type ) );
	}

	public static <T> Dependency<T> dependency( Type<T> type ) {
		return dependency( type, UNTARGETED );
	}

	private static <T> Dependency<T> dependency( Type<T> type, Injection[] injectionHierarchy ) {
		return dependency( instance( Name.ANY, type ), injectionHierarchy );
	}

	public static <T> Dependency<T> dependency( Instance<T> instance ) {
		return dependency( instance, UNTARGETED );
	}

	private static <T> Dependency<T> dependency( Instance<T> instance,
			Injection[] injectionHierarchy ) {
		return new Dependency<T>( instance, injectionHierarchy );
	}

	private final Injection[] injectionHierarchy;
	private final Instance<T> instance;

	private Dependency( Instance<T> instance, Injection[] injectionHierarchy ) {
		this.instance = instance;
		this.injectionHierarchy = injectionHierarchy;
	}

	public Instance<T> getInstance() {
		return instance;
	}

	@Override
	public Type<T> getType() {
		return instance.getType();
	}

	@Override
	public Name getName() {
		return instance.getName();
	}

	@Override
	public String toString() {
		return instance.toString() + ( injectionHierarchy.length == 0
			? ""
			: " " + Arrays.toString( injectionHierarchy ) );
	}

	public Dependency<?> onTypeParameter() {
		return dependency( getType().getParameters()[0], injectionHierarchy );
	}

	public <E> Dependency<E> instanced( Instance<E> instance ) {
		return dependency( instance, injectionHierarchy );
	}

	@Override
	public <E> Dependency<E> typed( Type<E> type ) {
		return dependency( instance( getName(), type ), injectionHierarchy );
	}

	public <E> Dependency<E> anyTyped( Type<E> type ) {
		return dependency( instance( Name.ANY, type ), injectionHierarchy );
	}

	public <E> Dependency<E> anyTyped( Class<E> type ) {
		return anyTyped( raw( type ) );
	}

	public Dependency<T> named( String name ) {
		return named( Name.named( name ) );
	}

	public Dependency<T> named( Name name ) {
		return dependency( instance( name, getType() ), injectionHierarchy );
	}

	public Dependency<T> untargeted() {
		return dependency( instance, UNTARGETED );
	}

	public boolean isUntargeted() {
		return injectionHierarchy.length == 0;
	}

	public Instance<?> target() {
		return target( 0 );
	}

	public Instance<?> target( int level ) {
		return isUntargeted()
			? Instance.ANY
			: injectionHierarchy[injectionHierarchy.length - 1 - level].getTarget().getInstance();
	}

	/**
	 * Means we inject into the argument target class.
	 */
	public Dependency<T> injectingInto( Class<?> target ) {
		return injectingInto( raw( target ) );
	}

	public Dependency<T> injectingInto( Type<?> target ) {
		return injectingInto( Instance.defaultInstanceOf( target ) );
	}

	public Dependency<T> injectingInto( Instance<?> target ) {
		return injectingInto( emergence( target, Expiry.NEVER ) );
	}

	public Dependency<T> injectingInto( Emergence<?> target ) {
		Injection injection = new Injection( instance, target );
		if ( injectionHierarchy.length == 0 ) {
			return new Dependency<T>( instance, new Injection[] { injection } );
		}
		ensureNotMoreFrequentExpiry( injection );
		ensureNoCycle( injection );
		Injection[] hierarchy = Arrays.copyOf( injectionHierarchy, injectionHierarchy.length + 1 );
		hierarchy[injectionHierarchy.length] = injection;
		return new Dependency<T>( instance, hierarchy );
	}

	private void ensureNoCycle( Injection injection )
			throws DependencyCycleException {
		for ( int i = 0; i < injectionHierarchy.length; i++ ) {
			Injection parent = injectionHierarchy[i];
			if ( parent.equalTo( injection ) ) {
				throw new DependencyCycleException( this, injection.getTarget().getInstance() );
			}
		}
	}

	private void ensureNotMoreFrequentExpiry( Injection injection ) {
		final Expiry expiry = injection.getTarget().getExpiry();
		for ( int i = 0; i < injectionHierarchy.length; i++ ) {
			Injection parent = injectionHierarchy[i];
			if ( expiry.moreFrequent( parent.getTarget().getExpiry() ) ) {
				throw new MoreFrequentExpiryException( parent, injection );
			}
		}
	}

	@Override
	public boolean isAssignableTo( Type<?> type ) {
		return getType().isAssignableTo( type );
	}

	@Override
	public Iterator<Injection> iterator() {
		return Arrays.asList( injectionHierarchy ).iterator();
	}
}
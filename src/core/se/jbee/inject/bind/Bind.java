/*
 *  Copyright (c) 2012-2013, Jan Bernitt 
 *			
 *  Licensed under the Apache License, Version 2.0, http://www.apache.org/licenses/LICENSE-2.0
 */
package se.jbee.inject.bind;

import se.jbee.inject.DeclarationType;
import se.jbee.inject.Instance;
import se.jbee.inject.Resource;
import se.jbee.inject.Scope;
import se.jbee.inject.Source;
import se.jbee.inject.Supplier;
import se.jbee.inject.Target;
import se.jbee.inject.bootstrap.Binding;
import se.jbee.inject.bootstrap.BindingType;
import se.jbee.inject.bootstrap.Bindings;
import se.jbee.inject.bootstrap.Inspector;

/**
 * The data and behavior used to create binds.
 * 
 * @author Jan Bernitt (jan@jbee.se)
 */
public final class Bind {

	public static Bind create( Bindings bindings, Source source, Scope scope ) {
		return new Bind( bindings, source, scope, Target.ANY );
	}

	final Bindings bindings;
	final Source source;
	final Scope scope;
	final Target target;

	private Bind( Bindings bindings, Source source, Scope scope, Target target ) {
		super();
		this.bindings = bindings;
		this.source = source;
		this.scope = scope;
		this.target = target;
	}

	public Bind asMulti() {
		return as( DeclarationType.MULTI );
	}

	public Bind asAuto() {
		return as( DeclarationType.AUTO );
	}

	public Bind asImplicit() {
		return as( DeclarationType.IMPLICIT );
	}

	public Bind asDefault() {
		return as( DeclarationType.DEFAULT );
	}

	public Bind asRequired() {
		return as( DeclarationType.REQUIRED );
	}

	public Bind asProvided() {
		return as( DeclarationType.PROVIDED );
	}

	public Bind as( DeclarationType type ) {
		return with( source.typed( type ) );
	}

	public Bind using( Inspector inspector ) {
		return new Bind( bindings.using( inspector ), source, scope, target );
	}

	public Bind per( Scope scope ) {
		return new Bind( bindings, source, scope, target );
	}

	public Bind with( Target target ) {
		return new Bind( bindings, source, scope, target );
	}

	public Bind into( Bindings bindings ) {
		return new Bind( bindings, source, scope, target );
	}

	public Bind autobinding() {
		return into( bindings.autobinding() );
	}

	public Bind with( Source source ) {
		return new Bind( bindings, source, scope, target );
	}

	public Bind within( Instance<?> parent ) {
		return new Bind( bindings, source, scope, target.within( parent ) );
	}

	public Inspector getInspector() {
		return bindings.getInspector();
	}

	public <T> Binding<T> asType( Resource<T> resource, BindingType type,
			Supplier<? extends T> supplier ) {
		return Binding.binding( resource, type, supplier, scope, source );
	}

	public <T> Binding<T> asMacro( Resource<T> resource ) {
		return asType( resource, BindingType.MACRO, null );
	}
}

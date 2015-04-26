/*
 *  Copyright (c) 2012-2013, Jan Bernitt 
 *			
 *  Licensed under the Apache License, Version 2.0, http://www.apache.org/licenses/LICENSE-2.0
 */
package se.jbee.inject.procedure;

import se.jbee.inject.Type;
import se.jbee.inject.procedure.Procedure;

/**
 * Frames the invocation of {@link Procedure}s with further functionality that can be executed
 * {@link #before(Type, Object, Type)} or {@link #after(Type, Object, Type, Object, Object)} the invoked
 * {@linkplain Procedure}. Thereby a state can be passed between these tow methods. The result
 * of first will be passed to the second as an argument. This allows them stay stateless.
 * 
 * A {@link ServiceInvocation} intentionally doesn't give any control or access over/to the invoked
 * {@linkplain Procedure} in order to be able to grant the same actual function invocation even
 * with faulty {@linkplain ServiceInvocation}s in place. That includes catching all exceptions
 * thrown in {@link #before(Type, Object, Type)} or {@link #after(Type, Object, Type, Object, Object)}.
 * 
 * @author Jan Bernitt (jan@jbee.se)
 * 
 * @param <T>
 *            Type of the state transfered between {@link #before(Type, Object, Type)} and
 *            {@link #after(Type, Object, Type, Object, Object)}.
 */
public interface ServiceInvocation<T> {

	<P, R> T before( Type<P> parameter, P value, Type<R> returnType );

	<P, R> void after( Type<P> parameter, P value, Type<R> returnType, R result, T before );

	<P, R> void afterException( Type<P> parameter, P value, Type<R> returnType, Exception e, T before );

}
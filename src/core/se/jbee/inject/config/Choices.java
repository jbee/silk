/*
 *  Copyright (c) 2012-2019, Jan Bernitt
 *	
 *  Licensed under the Apache License, Version 2.0, http://www.apache.org/licenses/LICENSE-2.0
 */
package se.jbee.inject.config;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.IdentityHashMap;

/**
 * {@link Choices} are used to model configurations of the bootstrapping process
 * through one enum for each configurable property (each property is identified
 * by the enum's {@link Class} object).
 * 
 * Each property can be used as a set or single associate value. So a option
 * property can describe either alternatives where one should be chosen or
 * options with multiple choice. It is up to the author of the module to decide
 * and use correctly.
 * 
 * {@linkplain Choices} are immutable! Use {@link #choose(Enum)} to build up
 * sets.
 * 
 * @author Jan Bernitt (jan@jbee.se)
 */
public final class Choices implements Serializable {

	public static final Choices NONE = new Choices(new IdentityHashMap<>());

	private final IdentityHashMap<Class<? extends Enum<?>>, EnumSet<?>> choicesByType;

	private Choices(
			IdentityHashMap<Class<? extends Enum<?>>, EnumSet<?>> choices) {
		this.choicesByType = choices;
	}

	public <C extends Enum<C>> boolean isChosen(Class<C> property, C choice) {
		EnumSet<?> choices = choicesByType.get(property);
		return choices == null || choices.isEmpty()
			? (choice == null)
			: choices.contains(choice);
	}

	public <C extends Enum<C>> Choices choose(C choice) {
		if (choice == null)
			return this;
		return with(choice.getDeclaringClass(), EnumSet.of(choice));
	}

	private <C extends Enum<C>> Choices with(Class<C> property,
			EnumSet<C> choices) {
		IdentityHashMap<Class<? extends Enum<?>>, EnumSet<?>> clone = copy();
		clone.put(property, choices);
		return new Choices(clone);
	}

	@SafeVarargs
	public final <C extends Enum<C>> Choices chooseMultiple(C... choices) {
		if (choices.length == 0)
			return this;
		return with(choices[0].getDeclaringClass(),
				EnumSet.of(choices[0], choices));
	}

	@SuppressWarnings("unchecked")
	private IdentityHashMap<Class<? extends Enum<?>>, EnumSet<?>> copy() {
		return (IdentityHashMap<Class<? extends Enum<?>>, EnumSet<?>>) choicesByType.clone();
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Choices
			&& choicesByType.equals(((Choices) obj).choicesByType);
	}

	@Override
	public int hashCode() {
		return choicesByType.hashCode();
	}

	@Override
	public String toString() {
		return choicesByType.toString();
	}
}

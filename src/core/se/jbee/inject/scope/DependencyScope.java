package se.jbee.inject.scope;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import se.jbee.inject.Dependency;
import se.jbee.inject.Instance;
import se.jbee.inject.Provider;
import se.jbee.inject.Scope;
import se.jbee.inject.UnresolvableDependency;

public final class DependencyScope implements Scope {

	/**
	 * Effectively gives a JVM singleton per {@link Instance}.
	 * 
	 * @since 19.1
	 */
	public static final Scope JVM = new DependencyScope(
			DependencyScope::instanceName);

	public static String typeName(Dependency<?> dep) {
		return dep.type().toString();
	}

	public static String instanceName(Dependency<?> dep) {
		return dep.instance.name.toString() + "@" + dep.type().toString();
	}

	public static String hierarchicalInstanceName(Dependency<?> dep) {
		return instanceName(dep) + targetInstanceName(dep);
	}

	public static String targetInstanceName(Dependency<?> dep) {
		StringBuilder b = new StringBuilder();
		for (int i = dep.injectionDepth() - 1; i >= 0; i--)
			b.append(dep.target(i));
		return b.toString();
	}

	private final AtomicReference<Map<String, Object>> instances = new AtomicReference<>(
			new HashMap<>());
	private final Function<Dependency<?>, String> identity;

	public DependencyScope(Function<Dependency<?>, String> identity) {
		this.identity = identity;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T yield(int serialID, Dependency<? super T> dep,
			Provider<T> provider, int generators)
			throws UnresolvableDependency {
		final String key = identity.apply(dep);
		Map<String, Object> map = instances.get();
		T instance = (T) map.get(key);
		if (instance != null)
			return instance;
		synchronized (map) {
			instance = (T) map.computeIfAbsent(key, k -> provider.provide());
		}
		return instance;
	}

}
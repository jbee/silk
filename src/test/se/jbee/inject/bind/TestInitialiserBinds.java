package se.jbee.inject.bind;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static se.jbee.inject.Dependency.dependency;
import static se.jbee.inject.Type.raw;

import org.junit.Test;

import se.jbee.inject.Initialiser;
import se.jbee.inject.Injector;
import se.jbee.inject.bootstrap.Bootstrap;

/**
 * The tests demonstrates how the {@link Initialiser} and
 * {@link Binder#initbind()} can be used to e.g. install a "shutdown hook" that
 * would automatically close all {@link AutoCloseable}s. Here the
 * "shutdown hook" of course is simulated so we can test for it being invoked.
 * In a real scenario one would use {@link Runtime#addShutdownHook(Thread)}.
 * 
 * @author jan
 */
public class TestInitialiserBinds {

	static final class TestInitialiserBindsModule extends BinderModule implements Initialiser {

		@Override
		protected void declare() {
			initbind().to(this);
			initbind().to(AutoCloseableInitialiser.class);
			construct(SingletonResource.class);
		}

		@Override
		public void init(Injector context) {
			// just to show that one could use the module itself as well
			moduleInitRan = true;
			// OBS: But the injector will create another instance so it should not have state!
		}
		
	}
	
	static class AutoCloseableInitialiser implements Initialiser {

		@Override
		public void init(Injector context) {
			// by the use of upper bound we receive all implementing classes 
			// even though they have not be bound explicitly for AutoCloseable.
			AutoCloseable[] autoCloseables = context.resolve(
					dependency(raw(AutoCloseable[].class).asUpperBound()));
			shutdownHookMock = () -> {
				for (AutoCloseable a : autoCloseables)
					try {
						a.close();
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
			};
		}
		
	}
	
	/**
	 * This class simulates some singleton that needs to be closed like a DB
	 * connection instance.
	 */
	static class SingletonResource implements AutoCloseable {

		boolean isClosed = false;
		
		@Override
		public void close() throws Exception {
			isClosed = true;
		}
		
	}
	
	static Runnable shutdownHookMock;
	static boolean moduleInitRan = false;
	
	@Test
	public void initialisersCanBeUsedToCloseAnyAutoCloseable() {
		Injector injector = Bootstrap.injector(TestInitialiserBindsModule.class);
		
		assertNotNull(shutdownHookMock);
		@SuppressWarnings("resource")
		SingletonResource resource = injector.resolve(dependency(SingletonResource.class));
		assertNotNull(resource);
		assertFalse(resource.isClosed);
		shutdownHookMock.run();
		assertTrue(resource.isClosed);
		
		assertTrue(moduleInitRan);
	}
}

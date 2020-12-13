package test.integration.bind;

import org.junit.jupiter.api.Test;
import se.jbee.inject.BuildUp;
import se.jbee.inject.Injector;
import se.jbee.inject.binder.BinderModule;
import se.jbee.inject.bootstrap.Bootstrap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Basic example of how to use {@link BuildUp} on a specific type.
 *
 * In the example the interface {@link MyListener} should be initialised. The
 * classes {@link MyService} and {@link MyServiceExtension} implement the
 * {@link MyListener} interface. Nowhere is stated that the initialisation of
 * {@link MyListener} should run for the particular classes. They do run just
 * because the classes are implementations of the {@link MyListener} interface.
 *
 * This mechanism can be used to setup more complex relations between managed
 * instances.
 *
 * @since 8.1
 */
class TestBasicBuildUpBinds {

	public interface MyListener {

		MyListener inc(int n);
	}

	public static class MyService implements MyListener {

		int sum;

		@Override
		public MyListener inc(int n) {
			sum += n;
			return this;
		}

	}

	public static class MyServiceExtension extends MyService {

	}

	public static class MyOtherService {

	}

	private static class TestBasicBuildUpBindsModule extends BinderModule {

		@Override
		protected void declare() {
			upbind(MyListener.class).to(
					(BuildUp<MyListener>) (l, as, injector) -> l.inc(1));
			injectingInto(MyServiceExtension.class) //
					.upbind(MyListener.class) //
					.to((BuildUp<MyListener>) (l, as, injector) -> l.inc(2));
			construct(MyService.class);
			construct(MyOtherService.class);
			construct(MyServiceExtension.class);
		}
	}

	@Test
	void buildUpTookPlace() {
		Injector injector = Bootstrap.injector(
				TestBasicBuildUpBindsModule.class);
		assertEquals(1, injector.resolve(MyService.class).sum);
		assertEquals(3, injector.resolve(MyServiceExtension.class).sum);
		assertNotNull(injector.resolve(MyOtherService.class));
	}
}

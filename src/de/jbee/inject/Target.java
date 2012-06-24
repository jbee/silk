package de.jbee.inject;

import static de.jbee.inject.Precision.morePreciseThan2;
import static de.jbee.inject.Type.raw;

public final class Target
		implements PreciserThan<Target> {

	public static final Target ANY = targeting( Instance.ANY );

	public static Target targeting( Instance<?> instance ) {
		return new Target( instance, Packages.ALL );
	}

	private final Instance<?> instance;
	private final Packages packages;

	private Target( Instance<?> instance, Packages packages ) {
		super();
		this.instance = instance;
		this.packages = packages;
	}

	public Target injectingInto( Instance<?> instance ) {
		return new Target( instance, packages );
	}

	public Target injectingInto( Type<?> type ) {
		return injectingInto( Instance.anyOf( type ) );
	}

	public Target injectingInto( Class<?> type ) {
		return injectingInto( raw( type ) );
	}

	public boolean isApplicableFor( Dependency<?> dependency ) {
		return isAccessibleFor( dependency ); //TODO target instance
	}

	public boolean isAccessibleFor( Dependency<?> dependency ) {
		return packages.isMember( dependency.target() );
	}

	@Override
	public String toString() {
		if ( instance.isAny() && packages.isAll() ) {
			return "any";
		}
		return "[" + packages + " " + instance + "]";
	}

	public Target inPackageAndSubPackagesOf( Class<?> type ) {
		return within( Packages.packageAndSubPackagesOf( type ) );
	}

	public Target inPackageOf( Class<?> type ) {
		return within( Packages.packageOf( type ) );
	}

	public Target inSubPackagesOf( Class<?> type ) {
		return within( Packages.subPackagesOf( type ) );
	}

	public boolean isSpecificInstance() {
		return !instance.isAny();
	}

	@Override
	public boolean morePreciseThan( Target other ) {
		return morePreciseThan2( packages, other.packages, instance, other.instance );
	}

	public Target within( Packages packages ) {
		return new Target( instance, packages );
	}
}

package com.supremacy.provider.provider;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.supremacy.guice.SupremacyGuiceModule;

public class InjectorProvider implements Provider<Injector> {
    private final Injector injector = Guice.createInjector(new SupremacyGuiceModule());


    @Override
    public Injector get() {
        return injector;
    }


    private static final InjectorProvider INSTANCE = new InjectorProvider();

    public static InjectorProvider getInstance() {
        return INSTANCE;
    }
}

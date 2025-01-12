package com.supremacy.provider.suffix;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.supremacy.module.SupremacyModule;

import java.util.HashMap;
import java.util.Map;

@Singleton
public final class SuffixProvider implements Provider<String> {

    private String suffix = null;

    private final Map<SupremacyModule, String> moduleToSuffixMap;

    @Inject
    public SuffixProvider() {
        this.moduleToSuffixMap = new HashMap<>();
    }

    public void provideSuffix(final SupremacyModule supremacyModule, final String text) {
        this.suffix = text;

        this.moduleToSuffixMap.putIfAbsent(supremacyModule, text);
    }

    public String getSuffix(final SupremacyModule supremacyModule) {
        return this.moduleToSuffixMap.get(supremacyModule);
    }

    @Override
    public String get() {
        return this.suffix;
    }
}

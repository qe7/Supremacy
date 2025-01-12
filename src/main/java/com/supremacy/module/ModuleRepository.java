package com.supremacy.module;

import java.util.Collection;

public interface ModuleRepository {

    /**
     *
     * @return A collection of all registered modules .
     */
    Collection<ModuleContext> getModules();

    /**
     * Get the module context for a specific module class
     * @param moduleClass The module class
     * @return The module context
     * @param <T> The module class type
     */
    <T extends SupremacyModule> ModuleContext getModuleContext(final Class<T> moduleClass);
}

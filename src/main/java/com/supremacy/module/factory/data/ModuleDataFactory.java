package com.supremacy.module.factory.data;

import com.supremacy.module.CategoryEnum;
import com.supremacy.module.ModuleData;

public class ModuleDataFactory {

    public ModuleData create(final String name, final String description, final CategoryEnum categoryEnum) {
        return new ModuleData(name, description, categoryEnum);
    }
}

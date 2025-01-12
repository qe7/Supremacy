package com.supremacy.processor.sorting;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.supremacy.module.ModuleContext;
import com.supremacy.module.SupremacyModule;
import com.supremacy.provider.suffix.SuffixProvider;
import net.minecraft.client.MinecraftClient;

import java.util.Collection;
import java.util.Comparator;

@Singleton
public final class StandardSortingProcessor {

    private final MinecraftClient minecraftClient;

    private final SuffixProvider suffixProvider;

    @Inject
    public StandardSortingProcessor(final MinecraftClient minecraftClient, SuffixProvider suffixProvider) {
        this.minecraftClient = minecraftClient;
        this.suffixProvider = suffixProvider;
    }

    public Collection<ModuleContext> sort(final SortingType sortingType, final Collection<ModuleContext> collection) {
        switch (sortingType) {
            case REVERSED_LENGTH -> {
                return collection.stream().sorted((o1, o2) -> Integer.compare(Integer.parseInt(
                        o2.moduleContext().name().length() + " " + SupremacyModule.suffixProvider.getSuffix(o2.supremacyModule()).length()), Integer.parseInt(
                        o1.moduleContext().name().length() + " " + SupremacyModule.suffixProvider.getSuffix(o1.supremacyModule()).length()))).toList().reversed();
            }
            case LENGTH -> {
                return collection.stream().sorted(Comparator.comparingInt((ModuleContext o) -> Integer.parseInt( o.moduleContext().name().length() + " (" + SupremacyModule.suffixProvider.getSuffix(o.supremacyModule()).length() + ")"))).toList();
            }
            case STRING_WIDTH -> {
                return collection.stream().sorted(Comparator.comparingInt((ModuleContext o) -> minecraftClient.textRenderer.getWidth(o.moduleContext().name() + " (" + SupremacyModule.suffixProvider.getSuffix(o.supremacyModule()) + ")"))).toList();
            }
            case REVERSED_STRING_WIDTH -> {
                return collection.stream().sorted(Comparator.comparingInt((ModuleContext o) -> minecraftClient.textRenderer.getWidth(o.moduleContext().name() + " (" + SupremacyModule.suffixProvider.getSuffix(o.supremacyModule()) + ")"))).toList().reversed();
            }
            case NONE -> {
                return collection;
            }
        }

        return collection;
    }

}

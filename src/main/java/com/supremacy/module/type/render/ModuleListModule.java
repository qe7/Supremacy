package com.supremacy.module.type.render;

import com.google.inject.Inject;
import com.supremacy.event.core.EventBus;
import com.supremacy.event.events.type.render.RenderOverlayEvent;
import com.supremacy.module.*;
import com.supremacy.processor.sorting.SortingType;
import com.supremacy.processor.sorting.StandardSortingProcessor;
import com.supremacy.provider.suffix.SuffixProvider;
import com.supremacy.toggle.ToggleService;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

import java.util.Collection;

@ModuleInfo(
        name = "Module List",
        description = "Renders a list of all enabled modules",
        category = CategoryEnum.RENDER
)
public final class ModuleListModule implements SupremacyModule {

    @Inject
    public ModuleListModule(final EventBus eventBus, final MinecraftClient minecraft, final ModuleRepository standardModuleRepository, final ToggleService toggleService, final StandardSortingProcessor standardSortingProcessor) {
        eventBus.constructLinker(this).link(event -> {
            final RenderOverlayEvent renderOverlayEvent = (RenderOverlayEvent) event;

            final DrawContext drawContext = renderOverlayEvent.drawContext();

            final Collection<ModuleContext> sortedModules = standardSortingProcessor.sort(
                    SortingType.REVERSED_STRING_WIDTH,
                    standardModuleRepository.getModules()
            ).stream().filter(moduleContext -> toggleService.isModuleEnabled(moduleContext.supremacyModule())).toList();

            int offset = 0;
            for (final ModuleContext moduleContext : sortedModules) {
                final TextRenderer textRenderer = minecraft.textRenderer;
                final String suffix = SupremacyModule.suffixProvider.getSuffix(moduleContext.supremacyModule()) == null ? "" : " §7(" + SupremacyModule.suffixProvider.getSuffix(moduleContext.supremacyModule()) + ")";
                final String text = moduleContext.moduleContext().name() + suffix;

                drawContext.drawText(textRenderer, text, drawContext.getScaledWindowWidth() - textRenderer.getWidth(text) - 1, 2 + offset, 0xFFFFFFFF, true);
                offset += textRenderer.fontHeight;
            }

        }, RenderOverlayEvent.ID);
    }

}
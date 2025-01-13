package com.supremacy.module.type.render;

import com.google.inject.Inject;
import com.supremacy.animation.Animation;
import com.supremacy.animation.Easing;
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
import java.util.HashMap;
import java.util.Map;

@ModuleInfo(
        name = "Module List",
        description = "Renders a list of all enabled modules",
        category = CategoryEnum.RENDER
)
public final class ModuleListModule implements SupremacyModule {

    private final Map<ModuleContext, Animation> moduleContextAnimationMap = new HashMap<>();
    private final Map<ModuleContext, Animation> moduleContextHeightAnimationMap = new HashMap<>();

    @Inject
    public ModuleListModule(final EventBus eventBus, final MinecraftClient minecraft, final ModuleRepository standardModuleRepository, final ToggleService toggleService, final StandardSortingProcessor standardSortingProcessor) {
        eventBus.constructLinker(this).link(event -> {
            final RenderOverlayEvent renderOverlayEvent = (RenderOverlayEvent) event;

            final DrawContext drawContext = renderOverlayEvent.drawContext();

            for (final ModuleContext moduleContext :  standardSortingProcessor.sort(
                    SortingType.REVERSED_STRING_WIDTH,
                    standardModuleRepository.getModules()
            )) {
                this.moduleContextAnimationMap.putIfAbsent(moduleContext, new Animation(Easing.EASE_IN_OUT_CUBIC, 250));
                this.moduleContextHeightAnimationMap.putIfAbsent(moduleContext, new Animation(Easing.EASE_IN_OUT_CUBIC, 250));
                final TextRenderer textRenderer = minecraft.textRenderer;

                final String suffix = SupremacyModule.suffixProvider.getSuffix(moduleContext.supremacyModule()) == null ? "" : " §7(" + SupremacyModule.suffixProvider.getSuffix(moduleContext.supremacyModule()) + ")";
                final String text = moduleContext.moduleContext().name() + suffix;

                this.moduleContextAnimationMap.get(moduleContext).run(toggleService.isModuleEnabled(moduleContext.supremacyModule()) ? minecraft.textRenderer.getWidth(text) : 0);
            }

            final Collection<ModuleContext> sortedModules = standardSortingProcessor.sort(
                    SortingType.REVERSED_STRING_WIDTH,
                    standardModuleRepository.getModules()
            ).stream().filter(moduleContext -> this.moduleContextAnimationMap.get(moduleContext).getValue() > 0).toList();

            int offset = 0;
            for (final ModuleContext moduleContext : sortedModules) {
                final Animation animation = this.moduleContextAnimationMap.get(moduleContext);

                final TextRenderer textRenderer = minecraft.textRenderer;
                final String suffix = SupremacyModule.suffixProvider.getSuffix(moduleContext.supremacyModule()) == null ? "" : " §7(" + SupremacyModule.suffixProvider.getSuffix(moduleContext.supremacyModule()) + ")";
                final String text = moduleContext.moduleContext().name() + suffix;

                drawContext.drawText(textRenderer, text, (int) (drawContext.getScaledWindowWidth() - animation.getValue() - 1), 2 + offset, 0xFFFFFFFF, true);

                offset += textRenderer.fontHeight + 1;
            }

        }, RenderOverlayEvent.ID);
    }

}
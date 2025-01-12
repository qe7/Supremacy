package com.supremacy.listener;

import com.google.inject.Inject;
import com.supremacy.bind.BindService;
import com.supremacy.event.EventSubscriber;
import com.supremacy.event.core.EventBus;
import com.supremacy.event.events.type.key.KeyPressEvent;
import com.supremacy.module.ModuleContext;
import com.supremacy.module.ModuleRepository;
import com.supremacy.toggle.ToggleService;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public final class BindListener implements EventSubscriber {

    private final ModuleRepository moduleRepository;
    private final BindService bindService;
    private final ToggleService toggleService;

    @Inject
    public BindListener(final ModuleRepository moduleRepository, final BindService bindService, final ToggleService toggleService) {
        this.moduleRepository = moduleRepository;
        this.bindService = bindService;
        this.toggleService = toggleService;
    }

    public void listen(final KeyPressEvent keyPressEvent) {
        final int key = keyPressEvent.key();

        if (key == -1 || (GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), key) != GLFW.GLFW_PRESS)) {
            return;
        }

        for (final ModuleContext moduleContext : moduleRepository.getModules()) {
            final int bind = bindService.getKey(moduleContext.supremacyModule());

            if (bind != key) {
                continue;
            }

            toggleService.toggle(moduleContext.supremacyModule(), !toggleService.isModuleEnabled(moduleContext.supremacyModule()));
        }
    }

}
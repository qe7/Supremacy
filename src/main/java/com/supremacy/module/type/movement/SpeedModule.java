package com.supremacy.module.type.movement;

import com.google.inject.Inject;
import com.supremacy.event.core.EventBus;
import com.supremacy.event.events.type.game.PlayerPositionUpdateEvent;
import com.supremacy.module.CategoryEnum;
import com.supremacy.module.ModuleInfo;
import com.supremacy.module.SupremacyModule;
import com.supremacy.module.type.combat.killaura.ExampleModuleMode;
import com.supremacy.module.type.combat.killaura.IDontKnowMode;
import com.supremacy.module.type.movement.speed.GroundStrafeSpeedMode;
import com.supremacy.module.type.movement.speed.SpeedModuleMode;
import com.supremacy.setting.registrar.SettingRegistrar;
import com.supremacy.setting.type.Setting;
import com.supremacy.util.MovementUtil;
import net.minecraft.client.MinecraftClient;

import java.util.Collection;
import java.util.HashSet;

@ModuleInfo(name = "Speed", category = CategoryEnum.MOVEMENT)
public final class SpeedModule implements SupremacyModule {

    private final Setting<SpeedModuleMode> speedModuleModeSetting;

    @Inject
    public SpeedModule(MinecraftClient minecraftClient, final SettingRegistrar settingRegistrar, final EventBus eventBus) {

        final Collection<SpeedModuleMode> speedModuleModes = new HashSet<>();

        speedModuleModes.add(new GroundStrafeSpeedMode());

        this.speedModuleModeSetting = settingRegistrar.registerCollectableSetting("Mode", speedModuleModes);

        eventBus.constructLinker(this).link(_ -> {
            if (minecraftClient.player == null) {
                return;
            }

            if (!MovementUtil.isMoving()) {
                return;
            }

            if (minecraftClient.player.isOnGround()) {
                MovementUtil.setSpeed(0.28d);
                minecraftClient.player.jump();
            }
        }, PlayerPositionUpdateEvent.ID);
    }
}

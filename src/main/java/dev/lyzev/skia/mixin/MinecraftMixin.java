/*
 * This file is part of https://github.com/Lyzev/Skija.
 *
 * Copyright (c) 2026. Lyzev
 *
 * Skija is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, version 3 of the License, or
 * (at your option) any later version.
 *
 * Skija is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Skija. If not, see <https://www.gnu.org/licenses/>.
 */

package dev.lyzev.skia.mixin;

import dev.lyzev.api.event.EventSkiaDraw;
import dev.lyzev.api.event.EventSkiaInit;
import dev.lyzev.api.skia.SkiaImplMc;
import dev.lyzev.skia.HudExample;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.lwjgl.glfw.GLFW.glfwGetFramebufferSize;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(GameConfig args, CallbackInfo ci) {
        HudExample hudExample = HudExample.INSTANCE;
        SkiaImplMc instance = SkiaImplMc.INSTANCE;

        int[] width = new int[1];
        int[] height = new int[1];

        glfwGetFramebufferSize(Minecraft.getInstance().getWindow().handle(), width, height);
        new EventSkiaInit(width[0] > 0 ? width[0] : 1, height[0] > 0 ? height[0] : 1).fire();
    }

    @Inject(
        method = "renderFrame",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/systems/RenderSystem;flipFrame(Lcom/mojang/blaze3d/TracyFrameCapture;)V"
        ),
        require = 1
    )
    private void onBeforeFlipFrame(boolean advanceGameTime, CallbackInfo ci) {
        EventSkiaDraw.INSTANCE.fire();
    }

}

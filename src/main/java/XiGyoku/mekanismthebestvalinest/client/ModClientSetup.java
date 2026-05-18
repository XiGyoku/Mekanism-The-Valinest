package XiGyoku.mekanismthebestvalinest.client;

import XiGyoku.mekanism_animation_injector.client.ShaderRegistry;
import XiGyoku.mekanismthebestvalinest.Mekanismthebestvalinest;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Mekanismthebestvalinest.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ModClientSetup {
    private ModClientSetup() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> ModShaderRegistry.shaderEntries().forEach(shader -> ShaderRegistry.registerShader(
                shader.typeId(),
                shader::renderType,
                shader::updateAnimationTime
        )));
    }
}

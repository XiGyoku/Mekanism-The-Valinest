package XiGyoku.mekanismthebestvalinest.client;

import XiGyoku.mekanismthebestvalinest.Mekanismthebestvalinest;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;
import java.util.List;

@Mod.EventBusSubscriber(modid = Mekanismthebestvalinest.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ModShaderRegistry {
    private static final int BUFFER_SIZE = 256;
    private static final String ANIMATION_UNIFORM = "MyAnimTime";
    private static final ResourceLocation VALINE_TEXTURE = modLocation("textures/shader/valine3g.png");
    private static final ResourceLocation WHITE_CONCRETE_TEXTURE = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/white_concrete.png");

    private static final ShaderEntry VALINE = new ShaderEntry("valine_shader", "valine_shader_type", VALINE_TEXTURE);
    private static final ShaderEntry SOYSAUCE = new ShaderEntry("soysauce_shader", "soysauce_shader_type", WHITE_CONCRETE_TEXTURE);
    private static final ShaderEntry VALINE_WHITE = new ShaderEntry("valine_white_shader", "valine_white_shader_type", VALINE_TEXTURE);
    private static final ShaderEntry SALTLESS_DRIED_SOYSOUCE = new ShaderEntry("saltless_dried_soysouce_shader", "saltless_dried_soysouce_shader_type", WHITE_CONCRETE_TEXTURE);
    private static final ShaderEntry VALINE_PURPLE = new ShaderEntry("valine_purple_shader", "valine_purple_shader_type", VALINE_TEXTURE);
    private static final List<ShaderEntry> SHADERS = List.of(VALINE, SOYSAUCE, VALINE_WHITE, SALTLESS_DRIED_SOYSOUCE, VALINE_PURPLE);

    public static final RenderStateShard.TransparencyStateShard CUSTOM_TRANSLUCENT_TRANSPARENCY = new RenderStateShard.TransparencyStateShard(
            "custom_translucent_transparency",
            () -> {
                RenderSystem.enableBlend();
                RenderSystem.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_ALPHA);
            },
            () -> {
                RenderSystem.disableBlend();
                RenderSystem.defaultBlendFunc();
            }
    );

    private ModShaderRegistry() {
    }

    @SubscribeEvent
    public static void onRegisterShaders(RegisterShadersEvent event) {
        try {
            for (ShaderEntry shader : SHADERS) {
                shader.register(event);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static List<ShaderEntry> shaderEntries() {
        return SHADERS;
    }

    public static ShaderInstance getValineShader() {
        return VALINE.shader();
    }

    public static ShaderInstance getSoysauceShader() {
        return SOYSAUCE.shader();
    }

    public static ShaderInstance getValineWhiteShader() {
        return VALINE_WHITE.shader();
    }

    public static ShaderInstance getSaltlessDriedSoysouceShader() {
        return SALTLESS_DRIED_SOYSOUCE.shader();
    }

    public static ShaderInstance getValinePurpleShader() {
        return VALINE_PURPLE.shader();
    }

    public static RenderType getSoysauceRenderType() {
        return SOYSAUCE.renderType();
    }

    public static RenderType getValineRenderType() {
        return VALINE.renderType();
    }

    public static RenderType getValineWhiteRenderType() {
        return VALINE_WHITE.renderType();
    }

    public static RenderType getSaltlessDriedSoysouceRenderType() {
        return SALTLESS_DRIED_SOYSOUCE.renderType();
    }

    public static RenderType getValinePurpleRenderType() {
        return VALINE_PURPLE.renderType();
    }

    private static ResourceLocation modLocation(String path) {
        return ResourceLocation.fromNamespaceAndPath(Mekanismthebestvalinest.MODID, path);
    }

    private static float animationTime() {
        return (System.currentTimeMillis() % 1000000L) / 1000.0f;
    }

    static final class ShaderEntry {
        private final ResourceLocation shaderId;
        private final ResourceLocation typeId;
        private final String renderTypeName;
        private final ResourceLocation texture;
        private ShaderInstance shader;
        private RenderType renderType;

        private ShaderEntry(String shaderName, String typeName, ResourceLocation texture) {
            this.shaderId = modLocation(shaderName);
            this.typeId = modLocation(typeName);
            this.renderTypeName = typeName;
            this.texture = texture;
        }

        private void register(RegisterShadersEvent event) throws IOException {
            event.registerShader(
                    new ShaderInstance(event.getResourceProvider(), shaderId, DefaultVertexFormat.POSITION_COLOR_TEX),
                    shader -> this.shader = shader
            );
        }

        ResourceLocation typeId() {
            return typeId;
        }

        ShaderInstance shader() {
            return shader;
        }

        RenderType renderType() {
            if (renderType == null) {
                renderType = createRenderType();
            }
            return renderType;
        }

        void updateAnimationTime() {
            ShaderInstance currentShader = shader();
            if (currentShader == null) {
                return;
            }

            Uniform uniform = currentShader.getUniform(ANIMATION_UNIFORM);
            if (uniform != null) {
                uniform.set(animationTime());
            }
        }

        private RenderType createRenderType() {
            return RenderType.create(
                    renderTypeName,
                    DefaultVertexFormat.POSITION_COLOR_TEX,
                    VertexFormat.Mode.QUADS,
                    BUFFER_SIZE,
                    false,
                    false,
                    RenderType.CompositeState.builder()
                            .setShaderState(new RenderStateShard.ShaderStateShard(this::shader))
                            .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                            .setTransparencyState(CUSTOM_TRANSLUCENT_TRANSPARENCY)
                            .createCompositeState(false)
            );
        }
    }
}

package com.roddy.reverieserverfix;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.util.Map;

@Mod(ReverieServerFix.MODID)
public class ReverieServerFix {
    public static final String MODID = "reverieserverfix";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static Field resourceLocationIntegerMapField = null;
    private static Field resourceKeyBooleanMapField = null;
    private static boolean reflectionInitialized = false;

    public ReverieServerFix() {
        MinecraftForge.EVENT_BUS.register(this);
        LOGGER.info("[ReverieServerFix] Loaded — client-side class crashes on dedicated server will be caught and logged.");
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            MinecraftServer server = event.getServer();
            if (server != null) {
                if (!reflectionInitialized) {
                    try {
                        Class<?> clazz = Class.forName("com.p1nero.cataclysm_dimension.CataclysmDimensionMod");
                        resourceLocationIntegerMapField = clazz.getDeclaredField("RESOURCE_LOCATION_INTEGER_MAP");
                        resourceLocationIntegerMapField.setAccessible(true);

                        resourceKeyBooleanMapField = clazz.getDeclaredField("RESOURCE_KEY_BOOLEAN_MAP");
                        resourceKeyBooleanMapField.setAccessible(true);
                    } catch (Exception e) {
                        LOGGER.warn("[ReverieServerFix] Failed to find CataclysmDimensionMod fields for unstuck fix.", e);
                    }
                    reflectionInitialized = true;
                }

                if (resourceLocationIntegerMapField != null && resourceKeyBooleanMapField != null) {
                    try {
                        @SuppressWarnings("unchecked")
                        Map<ResourceLocation, Integer> map = (Map<ResourceLocation, Integer>) resourceLocationIntegerMapField.get(null);
                        
                        @SuppressWarnings("unchecked")
                        Map<ResourceLocation, Boolean> boolMap = (Map<ResourceLocation, Boolean>) resourceKeyBooleanMapField.get(null);

                        if (map != null && boolMap != null) {
                            for (ServerLevel serverLevel : server.getAllLevels()) {
                                if (serverLevel.dimension().location().getNamespace().equals("cataclysm_dimension")) {
                                    if (!serverLevel.players().isEmpty()) {
                                        ResourceLocation loc = serverLevel.dimension().location();
                                        if (map.getOrDefault(loc, 0) > 0) {
                                            map.put(loc, 0);
                                            boolMap.put(loc, false);
                                            LOGGER.info("[ReverieServerFix] Unstuck rebuilding dimension {}", loc);
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        // Ignore
                    }
                }
            }
        }
    }
}

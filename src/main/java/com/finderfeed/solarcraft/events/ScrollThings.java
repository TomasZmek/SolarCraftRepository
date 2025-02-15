package com.finderfeed.solarcraft.events;


import com.finderfeed.solarcraft.misc_things.IScrollable;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "solarcraft",bus = Mod.EventBusSubscriber.Bus.FORGE,value = Dist.CLIENT)
public class ScrollThings {

    @SubscribeEvent
    public static void listenToHotkeys(final InputEvent.Key event){
        if (Minecraft.getInstance().screen instanceof IScrollable){
            ((IScrollable) Minecraft.getInstance().screen).performScroll(event.getScanCode());

        }


    }

//    @SubscribeEvent
//    public static void initMaps(final ClientPlayerNetworkEvent.LoggedInEvent event){
//
//        //BookEntry.initMap();
//        if (event.getPlayer() != null && !SolarcraftClientConfig.DISABLE_WELCOME_MESSAGE.get()) {
//            event.getPlayer().sendSystemMessage(Component.translatable("solarcraft.welcome_message"), event.getPlayer().getUUID());
//            event.getPlayer().sendSystemMessage(Component.translatable("solarcraft.welcome_message2"), event.getPlayer().getUUID());
//
//        }
//    }

    @SubscribeEvent
    public static void initRecipes(final RecipesUpdatedEvent event){
//        ProgressionHelper.initInfRecipesMap(event.getRecipeManager());
//        ProgressionHelper.initSmeltingRecipesMap(event.getRecipeManager());
//        ProgressionHelper.initInfusingCraftingRecipes(event.getRecipeManager());
    }

}

package com.finderfeed.solarcraft.content.items.primitive.solacraft_item_classes;

import com.finderfeed.solarcraft.helpers.ClientHelpers;
import com.finderfeed.solarcraft.content.items.solar_lexicon.unlockables.AncientFragment;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.util.thread.EffectiveSide;

import java.util.function.Supplier;

public class SolarcraftItem extends Item implements FragmentItem{

    private Supplier<AncientFragment> fragmentSupplier;

    public SolarcraftItem(Properties p_41383_, Supplier<AncientFragment> fragment) {
        super(p_41383_);
        this.fragmentSupplier = fragment;
    }

    @Override
    public Component getName(ItemStack stack) {
        if (EffectiveSide.get().isClient()){
            if (ClientHelpers.isComponentObfuscated(stack)) {
                return super.getName(stack).copy().withStyle(ChatFormatting.OBFUSCATED);
            }else {
                return super.getName(stack);
            }
        }
        return super.getName(stack);
    }

    @Override
    public AncientFragment getNeededFragment() {
        return fragmentSupplier.get();
    }
}

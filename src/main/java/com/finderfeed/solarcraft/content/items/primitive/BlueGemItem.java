package com.finderfeed.solarcraft.content.items.primitive;

import com.finderfeed.solarcraft.helpers.Helpers;
import com.finderfeed.solarcraft.content.items.solar_lexicon.progressions.Progression;
import com.finderfeed.solarcraft.registries.items.SolarcraftItems;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Registry;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import java.util.Optional;

public class BlueGemItem extends Item {

    public BlueGemItem(Properties p_i48487_1_) {
        super(p_i48487_1_);
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if (!entity.level.isClientSide){

            Level world = entity.level;
            Optional<? extends Registry<Biome>> reg = world.registryAccess().registry(Registry.BIOME_REGISTRY);
            if (reg.isPresent()) {
                //TODO:return this when biome can be added to overworld
                /*(world.getBiome(entity.blockPosition()).equals(reg.get().get(FeaturesRegistry.MOLTEN_BIOME_KEY))*/
                if (entity.level.getBlockState(entity.blockPosition()).is(Blocks.LAVA)) {
                    int ticks = entity.getPersistentData().getInt("transmutation_ticks")+1;
                    entity.getPersistentData().putInt("transmutation_ticks", ticks);

                    if (ticks >= 1000){
                        for (int i = 0; i < stack.getCount();i++) {
                            world.addFreshEntity(new ItemEntity(world, entity.getX(), entity.getY(), entity.getZ(), new ItemStack(SolarcraftItems.BLUE_GEM_ENCHANCED.get(), 1)));
                        }

                        if (entity.getThrower() != null ){
                            Player player = world.getPlayerByUUID(entity.getThrower());
                            if (player != null){
                                Helpers.fireProgressionEvent(player, Progression.TRANSMUTE_GEM);
                            }
                        }
                        entity.remove(Entity.RemovalReason.KILLED);
                    }

                } else {

                    entity.getPersistentData().putInt("transmutation_ticks", 0);
                }
            }
        }
        return super.onEntityItemUpdate(stack, entity);
    }


}

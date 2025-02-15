package com.finderfeed.solarcraft.content.recipe_types;


import com.finderfeed.solarcraft.content.blocks.infusing_table_things.InfuserTileEntity;
import com.finderfeed.solarcraft.misc_things.RunicEnergy;
import com.finderfeed.solarcraft.registries.blocks.SolarcraftBlocks;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.Map;

public class OldInfusingRecipe implements Recipe<Container> {



    private final InfuserTileEntity.Tier tier;
    public final ResourceLocation id;
    public final Ingredient input1;
    public final Ingredient input2;
    public final Ingredient input3;
    public final Ingredient input4;
    public final Ingredient input5;
    public final Ingredient input6;
    public final Ingredient input7;
    public final Ingredient input8;
    public final Ingredient input9;
    public final ItemStack output;
    public final String child;
    public final int infusingTime;
    public final int requriedEnergy;
    public final String tag;
    public final int count;
    public final Map<RunicEnergy.Type,Double> RUNIC_ENERGY_COST;
    private final String catalysts;
    private final Block[] deserializedCatalysts;
    public static final InfusingRecipeSerializer serializer = new InfusingRecipeSerializer();
    public OldInfusingRecipe(ResourceLocation id, String catalysts, Ingredient input1, Ingredient input2, Ingredient input3, Ingredient input4, Ingredient input5, Ingredient input6, Ingredient input7, Ingredient input8, Ingredient input9, ItemStack output, int infusingTime, String child
    , int requriedEnergy, String tag, int count, Map<RunicEnergy.Type,Double> costs) {
        this.id = id;
        this.catalysts = catalysts;
        this.deserializedCatalysts = deserializeCatalysts();
        this.input1 = input1;
        this.input2 = input2;
        this.input3 = input3;
        this.input4 = input4;
        this.input5 = input5;
        this.input6 = input6;
        this.input7 = input7;
        this.input8 = input8;
        this.input9 = input9;
        this.output = output;
        this.infusingTime = infusingTime;
        this.child = child;
        this.RUNIC_ENERGY_COST = costs;
        this.requriedEnergy = requriedEnergy;
        this.tag = tag;
        this.count = count;
        if (requriedEnergy > 0){
            this.tier = InfuserTileEntity.Tier.SOLAR_ENERGY;
        }else if (doRecipeRequiresRunicEnergy(costs)){
            this.tier = InfuserTileEntity.Tier.RUNIC_ENERGY;
        }else{
            this.tier = InfuserTileEntity.Tier.FIRST;
        }
    }

    public Block[] getDeserializedCatalysts() {
        return deserializedCatalysts;
    }

    public int getInfusingTime(){
        return infusingTime;
    }

    @Override
    public boolean matches(Container inv, Level world) {
        return this.input1.test(inv.getItem(0))
                && this.input2.test(inv.getItem(1))
                && this.input3.test(inv.getItem(2))
                && this.input4.test(inv.getItem(3))
                && this.input5.test(inv.getItem(4))
                && this.input6.test(inv.getItem(5))
                && this.input7.test(inv.getItem(6))
                && this.input8.test(inv.getItem(7))
                && this.input9.test(inv.getItem(8));
    }

    @Override
    public ItemStack assemble(Container inv) {
        return this.output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int p_194133_1_, int p_194133_2_) {
        return true;
    }

    @Override
    public ItemStack getResultItem() {
        return this.output;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return serializer;
    }

    @Override
    public RecipeType<?> getType() {
        return null;
    }

    public InfuserTileEntity.Tier getTier() {
        return tier;
    }

    private boolean doRecipeRequiresRunicEnergy(Map<RunicEnergy.Type,Double> costs){
        for (double cost : costs.values()){
            if (Math.round(cost) != 0){
                return true;
            }
        }
        return false;
    }

    public String getCatalysts() {
        return catalysts;
    }

    private Map<Character,Block> DESERIALIZATOR = Map.of(
            'T', SolarcraftBlocks.TERA_RUNE_BLOCK.get(),
            'Z', SolarcraftBlocks.ZETA_RUNE_BLOCK.get(),
            'K', SolarcraftBlocks.KELDA_RUNE_BLOCK.get(),
            'R', SolarcraftBlocks.URBA_RUNE_BLOCK.get(),
            'F', SolarcraftBlocks.FIRA_RUNE_BLOCK.get(),
            'A', SolarcraftBlocks.ARDO_RUNE_BLOCK.get(),
            'U', SolarcraftBlocks.ULTIMA_RUNE_BLOCK.get(),
            'G', SolarcraftBlocks.GIRO_RUNE_BLOCK.get()
    );


    public Block[] deserializeCatalysts(){
        if (!catalysts.equals("            ")) {
            Block[] bl = new Block[12];
            for (int i = 0; i < 12; i++) {
                char c = catalysts.charAt(i);
                Block block = DESERIALIZATOR.get(c);
                if (block != null) {
                    bl[i] = block;
                } else {
                    if (c != ' '){
                        throw new RuntimeException("Incorrect symbol: "+ c + " in catalysts. Recipe: " + this.id);
                    }
                    bl[i] = null;
                }
            }
            return bl;
        }else{
            return null;
        }
    }
}

package com.finderfeed.solarcraft.content.world_generation.features;

import com.finderfeed.solarcraft.SolarCraft;
import com.finderfeed.solarcraft.config.SolarcraftConfig;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Column;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CrystalCaveOreCrystal extends Feature<NoneFeatureConfiguration> {

    private List<Block> AVAILABLE_TO_SPAWN;
    private static final ResourceLocation ORE_CRYSTAL = new ResourceLocation("solarcraft:worldgen_features/crystal_cave_ore_crystal");

    public CrystalCaveOreCrystal(Codec<NoneFeatureConfiguration> p_65786_) {
        super(p_65786_);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> ctx) {
        WorldGenLevel world = ctx.level();
        if (AVAILABLE_TO_SPAWN == null){
            initList(world);
        }
        BlockPos pos = ctx.origin();

        RandomSource random = ctx.random();
        BlockState stateAtPos = world.getBlockState(pos);
        if (!(stateAtPos.isAir())){
            return false;
        }
        Optional<Column> column = Column.scan(world,pos,30,(state)->state.is(Blocks.AIR) || state.is(Blocks.CAVE_AIR),(state)->state.is(BlockTags.BASE_STONE_OVERWORLD));
        if (column.isPresent() && column.get().getFloor().isPresent()) {

            Rotation rot = Rotation.getRandom(random);
            StructureTemplateManager manager = world.getLevel().getStructureManager();
            StructureTemplate templ = manager.getOrCreate(ORE_CRYSTAL);
            StructurePlaceSettings set = new StructurePlaceSettings().addProcessor(BlockIgnoreProcessor.AIR).setRandom(random).setRotation(rot).setBoundingBox(BoundingBox.infinite());
            BlockPos blockpos1 = templ.getZeroPositionWithTransform(pos.offset(-templ.getSize().getX() / 2, 0, -templ.getSize().getZ() / 2), Mirror.NONE, rot);
            blockpos1 = new BlockPos(blockpos1.getX(),column.get().getFloor().getAsInt()-2,blockpos1.getZ());

            templ.placeInWorld(world, blockpos1, blockpos1, set, random, 4);
            templ.filterBlocks(blockpos1, set, Blocks.SEA_LANTERN).forEach((info) -> {
                setBlock(world, info.pos, AVAILABLE_TO_SPAWN.get(world.getRandom().nextInt(AVAILABLE_TO_SPAWN.size())).defaultBlockState());
            });
        }else{
            return false;
        }
        return true;
    }

    private void initList(WorldGenLevel level){
        if (AVAILABLE_TO_SPAWN == null){
            AVAILABLE_TO_SPAWN = new ArrayList<>();
            List<String> ids = SolarcraftConfig.ISLAND_ORES.get();
            Optional<? extends Registry<Block>> a  = level.registryAccess().registry(Registry.BLOCK_REGISTRY);
            if (a.isPresent()){
                Registry<Block> reg = a.get();
                ids.forEach((string)->{
                    Block block = reg.get(new ResourceLocation(string));
                    if (block != null){
                        AVAILABLE_TO_SPAWN.add(block);
                    }else{
                        SolarCraft.LOGGER.log(Level.ERROR,"Couldn't parse block: " + string +" while initiating ores list");
                    }
                });
            }
        }
    }
}

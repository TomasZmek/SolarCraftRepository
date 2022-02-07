package com.finderfeed.solarforge.magic.blocks.blockentities.containers.screens;

import com.finderfeed.solarforge.ClientHelpers;
import com.finderfeed.solarforge.SolarForge;
import com.finderfeed.solarforge.local_library.helpers.FinderfeedMathHelper;
import com.finderfeed.solarforge.magic.blocks.blockentities.EnchanterBlockEntity;
import com.finderfeed.solarforge.magic.blocks.blockentities.containers.EnchanterContainer;
import com.finderfeed.solarforge.magic.blocks.infusing_table_things.InfuserScreen;
import com.finderfeed.solarforge.magic.blocks.solar_forge_block.solar_forge_screen.SolarForgeButton;
import com.finderfeed.solarforge.misc_things.RunicEnergy;
import com.finderfeed.solarforge.packet_handler.SolarForgePacketHandler;
import com.finderfeed.solarforge.packet_handler.packets.EnchanterPacket;
import com.finderfeed.solarforge.recipe_types.infusing_new.InfusingRecipe;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EnchanterContainerScreen extends AbstractScrollableContainerScreen<EnchanterContainer> {
    public static final ResourceLocation MAIN_SCREEN = new ResourceLocation(SolarForge.MOD_ID,"textures/gui/enchanter_gui.png");
    private static final ResourceLocation ENERGY_GUI = new ResourceLocation("solarforge","textures/gui/infuser_energy_gui.png");
    public final ResourceLocation RUNIC_ENERGY_BAR = new ResourceLocation("solarforge","textures/gui/runic_energy_bar.png");
    private Enchantment selectedEnchantment = null;
    private int selectedLevel = 0;
    private final List<SolarForgeButton> postRender = new ArrayList<>();
    private int currentMouseScroll = 0;
    private List<Runnable> postTooltipRender = new ArrayList<>();


    public EnchanterContainerScreen(EnchanterContainer container, Inventory inventory, Component component) {
        super(container, inventory, component);
    }

    @Override
    public void init() {
        currentMouseScroll = 0;
        super.init();
        this.inventoryLabelX = 10000;
        relX+=60;
        relY+=10;
        Map<Enchantment,Map<RunicEnergy.Type,Double>> defaultCosts = menu.costsAndAvailableEnchantments;
        int iter = 0;
        postRender.clear();
        for (Enchantment e : defaultCosts.keySet()){
            if (iter == 0){
                if (!menu.tile.enchantingInProgress()) {
                    this.selectedEnchantment = e;
                    this.selectedLevel = 1;
                }else{
                    this.selectedEnchantment = menu.tile.getProcessingEnchantment();
                    this.selectedLevel = menu.tile.getProcesingEnchantmentLevel();
                }
            }
            SolarForgeButton b = new SolarForgeButton(relX + 15 ,relY + 12 + iter*16,new TranslatableComponent(e.getDescriptionId()),(button)->{
                if (!menu.tile.enchantingInProgress()) {
                    this.selectedEnchantment = e;
                    this.selectedLevel = 1;
                }
            },(btn,matrices,mousex,mousey)->{
                postTooltipRender.add(()->{
                    renderTooltip(matrices,new TranslatableComponent(e.getDescriptionId()),mousex,mousey);
                });
            });
            postRender.add(b);
            addWidget(b);
            iter++;
        }

        Button buttonPlus = new SolarForgeButton(relX+ 87,relY + 22,15,16,new TextComponent("+"),(btn)->{
            if (!(selectedLevel + 1 > selectedEnchantment.getMaxLevel()) && !menu.tile.enchantingInProgress()){
                selectedLevel++;
            }
        });
        Button buttonMinus = new SolarForgeButton(relX+ 87,relY + 62,15,16,new TextComponent("-"),(btn)->{
            if (!(selectedLevel - 1 < 1) && !menu.tile.enchantingInProgress()){
                selectedLevel--;
            }
        });

        Button button = new SolarForgeButton(relX+ 110,relY + 80,new TextComponent("Enchant"),(btn)->{
            ItemStack stack = menu.tile.getStackInSlot(0);
            if (selectedEnchantment != null && stack.canApplyAtEnchantingTable(selectedEnchantment)
                    && EnchantmentHelper.getItemEnchantmentLevel(selectedEnchantment,stack) < selectedLevel) {
                if (!menu.tile.enchantingInProgress()) {
                    SolarForgePacketHandler.INSTANCE.sendToServer(new EnchanterPacket(menu.tile.getBlockPos(), selectedEnchantment, selectedLevel));
                }else{
                    Minecraft.getInstance().player.displayClientMessage(new TextComponent("Enchanting is already in progress!"),false);
                }
            }else{
                Minecraft.getInstance().player.displayClientMessage(new TextComponent("Enchantment cannot be applied to this item"),false);
            }
        });
        addRenderableWidget(button);
        addRenderableWidget(buttonMinus);
        addRenderableWidget(buttonPlus);
        setAsStaticWidget(button);
        setAsStaticWidget(buttonPlus);
        setAsStaticWidget(buttonMinus);
    }

    @Override
    protected void renderBg(PoseStack matrices, float pticks, int mousex, int mousey) {

        super.renderBackground(matrices);
        ClientHelpers.bindText(MAIN_SCREEN);
        int scale = (int) minecraft.getWindow().getGuiScale();
        int a = 1;
        if (scale == 2) {
            a = 0;
        }

        blit(matrices,relX+a+3,relY+4,0,0,176,256,256,256);

        if (menu.tile.enchantingInProgress()){
            int xTex = Math.round(62f*(menu.tile.getEnchantingTicks()/ (float)EnchanterBlockEntity.MAX_ENCHANTING_TICKS));
            blit(matrices,relX+a+110,relY+11,176,0,xTex,62,256,256);
        }


        if (selectedEnchantment != null) {
            int y = 12;
            matrices.pushPose();
            ClientHelpers.bindText(ENERGY_GUI);
            blit(matrices, relX + a - 73 + 4, relY - 8 + y, 0, 0, 73, 177, 73, 177);
            ClientHelpers.bindText(RUNIC_ENERGY_BAR);

            RenderSystem.enableBlend();
            renderEnergyBar(matrices, relX + a - 12 - 16 + 1, relY + 61 + y, menu.costsAndAvailableEnchantments.get(selectedEnchantment).get(RunicEnergy.Type.KELDA) * selectedLevel, true);
            renderEnergyBar(matrices, relX + a - 28 - 16 + 1, relY + 61 + y, menu.costsAndAvailableEnchantments.get(selectedEnchantment).get(RunicEnergy.Type.TERA) * selectedLevel, true);
            renderEnergyBar(matrices, relX + a - 44 - 16 + 1, relY + 61 + y, menu.costsAndAvailableEnchantments.get(selectedEnchantment).get(RunicEnergy.Type.ZETA) * selectedLevel, true);
            renderEnergyBar(matrices, relX + a - 12 - 16 + 1, relY + 145 + y, menu.costsAndAvailableEnchantments.get(selectedEnchantment).get(RunicEnergy.Type.URBA) * selectedLevel, true);
            renderEnergyBar(matrices, relX + a - 28 - 16 + 1, relY + 145 + y, menu.costsAndAvailableEnchantments.get(selectedEnchantment).get(RunicEnergy.Type.FIRA) * selectedLevel, true);
            renderEnergyBar(matrices, relX + a - 44 - 16 + 1, relY + 145 + y, menu.costsAndAvailableEnchantments.get(selectedEnchantment).get(RunicEnergy.Type.ARDO) * selectedLevel, true);
            renderEnergyBar(matrices, relX + a - 12 + 1, relY + 61 + y, menu.costsAndAvailableEnchantments.get(selectedEnchantment).get(RunicEnergy.Type.GIRO) * selectedLevel, true);
            renderEnergyBar(matrices, relX + a - 12 + 1, relY + 145 + y, menu.costsAndAvailableEnchantments.get(selectedEnchantment).get(RunicEnergy.Type.ULTIMA) * selectedLevel, true);
            RenderSystem.disableBlend();


            renderEnergyBar(matrices, relX + a - 12 - 16 + 1, relY + 61 + y, menu.tile.getRunicEnergy(RunicEnergy.Type.KELDA), false);
            renderEnergyBar(matrices, relX + a - 28 - 16 + 1, relY + 61 + y, menu.tile.getRunicEnergy(RunicEnergy.Type.TERA), false);
            renderEnergyBar(matrices, relX + a - 44 - 16 + 1, relY + 61 + y, menu.tile.getRunicEnergy(RunicEnergy.Type.ZETA), false);
            renderEnergyBar(matrices, relX + a - 12 - 16 + 1, relY + 145 + y,menu.tile.getRunicEnergy(RunicEnergy.Type.URBA), false);
            renderEnergyBar(matrices, relX + a - 28 - 16 + 1, relY + 145 + y,menu.tile.getRunicEnergy(RunicEnergy.Type.FIRA), false);
            renderEnergyBar(matrices, relX + a - 44 - 16 + 1, relY + 145 + y,menu.tile.getRunicEnergy(RunicEnergy.Type.ARDO), false);
            renderEnergyBar(matrices, relX + a - 12 + 1, relY + 61 + y, menu.tile.getRunicEnergy(RunicEnergy.Type.GIRO), false);
            renderEnergyBar(matrices, relX + a - 12 + 1, relY + 145 + y, menu.tile.getRunicEnergy(RunicEnergy.Type.ULTIMA), false);

            drawCenteredString(matrices,font,""+selectedLevel,relX + 94,relY + 45,0xffffff);
            drawCenteredString(matrices,font,selectedEnchantment.getFullname(selectedLevel).getString(),relX + 89,relY + 184,0xff0000);
            matrices.popPose();


        }

        RenderSystem.enableScissor((relX + 12 + a)*scale,(relY + 106 )*scale,69*scale,80*scale);

        for (SolarForgeButton b : postRender){
            b.render(matrices,mousex,mousey,pticks);
        }
        RenderSystem.disableScissor();
        postTooltipRender.forEach(Runnable::run);
        postTooltipRender.clear();
        super.renderTooltip(matrices,mousex,mousey);

    }

    @Override
    protected void containerTick() {
        super.containerTick();
        for (SolarForgeButton b : postRender){
            if (!(b.y > relY   && b.y <  relY + 89) ){
                b.active = false;
                b.visible = false;
            }else{
                b.active = true;
                b.visible = true;
            }
        }
    }


    @Override
    public boolean mouseScrolled(double p_94686_, double p_94687_, double delta) {

        for (SolarForgeButton b : postRender){
            if (currentMouseScroll + delta*getScrollValue() >= -getMaxYDownScrollValue() && currentMouseScroll + delta*getScrollValue() <= 0) {
                b.y = b.y + (int) delta * getScrollValue();

            }
        }
        if (currentMouseScroll + delta*getScrollValue() >= -getMaxYDownScrollValue() && currentMouseScroll + delta*getScrollValue() <= 0) {
            currentMouseScroll += delta * getScrollValue();
        }
        return super.mouseScrolled(p_94686_, p_94687_, delta);
    }

    private void renderEnergyBar(PoseStack matrices, int offsetx, int offsety, double energyAmount, boolean simulate){
        matrices.pushPose();

        int texturex = Math.round((float)energyAmount/100000*60);
        matrices.translate(offsetx,offsety,0);
        matrices.mulPose(Vector3f.ZN.rotationDegrees(90));
        if (!simulate) {
            blit(matrices, 0, 0, 0, 0, texturex, 6);
        }else{
            InfuserScreen.blitm(matrices, 0, 0, 0, 0, texturex, 6,60,6);
        }

        matrices.popPose();
    }



    @Override
    protected int getScrollValue() {
        return 4;
    }

    @Override
    protected int getMaxYDownScrollValue() {
        return (int)Math.floor(postRender.size()/5f) * 80;
    }

    @Override
    protected int getMaxXRightScrollValue() {
        return 0;
    }

    @Override
    protected int getMaxYUpScrollValue() {
        return 0;
    }

    @Override
    protected int getMaxXLeftScrollValue() {
        return 0;
    }
}

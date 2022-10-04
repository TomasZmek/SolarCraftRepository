package com.finderfeed.solarforge.client.screens;

import com.finderfeed.solarforge.content.items.solar_lexicon.screen.buttons.InfoButton;
import com.finderfeed.solarforge.helpers.ClientHelpers;
import com.finderfeed.solarforge.helpers.Helpers;
import com.finderfeed.solarforge.SolarForge;
import com.finderfeed.solarforge.local_library.helpers.FDMathHelper;
import com.finderfeed.solarforge.local_library.helpers.RenderingTools;
import com.finderfeed.solarforge.local_library.other.EaseInOut;
import com.finderfeed.solarforge.content.blocks.blockentities.clearing_ritual.CrystalEnergyVinesTile;
import com.finderfeed.solarforge.packet_handler.SolarForgePacketHandler;
import com.finderfeed.solarforge.packet_handler.packets.crystal_energy_vines_puzzle.PuzzleActionPacket;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

public class CrystalEnergyVinesPuzzleScreen extends SolarCraftScreen {

    public static final ResourceLocation TRIES = new ResourceLocation(SolarForge.MOD_ID,"textures/gui/wooden_window.png");
    public static final ResourceLocation MAIN_GUI = new ResourceLocation(SolarForge.MOD_ID,"textures/gui/crystal_energy_vines_puzzle.png");
    public static final ResourceLocation TWIGS = new ResourceLocation(SolarForge.MOD_ID,"textures/gui/twigs.png");
    public static final ResourceLocation NODE = new ResourceLocation(SolarForge.MOD_ID,"textures/particle/solar_strike_particle.png");
    public static final EaseInOut VALUE = new EaseInOut(0,1,20,2);

    private int ticker = 0;

    public CrystalEnergyVinesTile tile;

    public CrystalEnergyVinesPuzzleScreen(CrystalEnergyVinesTile tile){
        this.tile = tile;
    }

    @Override
    public boolean keyPressed(int keyCode, int idk, int type) {
        if (keyCode == GLFW.GLFW_KEY_UP || keyCode == GLFW.GLFW_KEY_W){
            sendMovementPacket(CrystalEnergyVinesTile.MOVE_UP);
        }else if (keyCode == GLFW.GLFW_KEY_DOWN || keyCode == GLFW.GLFW_KEY_S){
            sendMovementPacket(CrystalEnergyVinesTile.MOVE_DOWN);
        }else if (keyCode == GLFW.GLFW_KEY_LEFT || keyCode == GLFW.GLFW_KEY_A){
            sendMovementPacket(CrystalEnergyVinesTile.MOVE_LEFT);
        }else if (keyCode == GLFW.GLFW_KEY_RIGHT || keyCode == GLFW.GLFW_KEY_D){
            sendMovementPacket(CrystalEnergyVinesTile.MOVE_RIGHT);
        }else if (keyCode == GLFW.GLFW_KEY_R){
            sendMovementPacket(CrystalEnergyVinesTile.RESET);
        }

        return super.keyPressed(keyCode, idk, type);

    }

    @Override
    protected void init() {
        super.init();
        VALUE.reset();
        InfoButton.Wooden button = new InfoButton.Wooden(relX- 30 ,relY ,13,13,(btn,matrices,mx,my)->{
            renderTooltip(matrices,font.split(Component.translatable("solarcraft.energy_vines_screen"),200),mx,my);
        });
        addRenderableWidget(button);
    }

    @Override
    public void tick() {
        super.tick();
        ticker++;
        VALUE.tick();
        if (tile.getPuzzlePattern() != null && tile.isWin()){
            Minecraft.getInstance().setScreen(null);
        }
    }

    private void sendMovementPacket(int moveType){
        SolarForgePacketHandler.INSTANCE.sendToServer(new PuzzleActionPacket(moveType,tile.getBlockPos()));
    }

    private static final float[][] NODE_COLORS = {
            {0,0,0,0},
            {1,0,0,1},
            {1,1,0,1},
            {0,1,0,1}
    };

    @Override
    public void render(PoseStack matrices, int mx, int my, float pTicks) {
        int[][] pattern = tile.getPuzzlePattern();
        ClientHelpers.bindText(MAIN_GUI);
        int offsY = -14;
        blit(matrices,relX,relY + offsY,0,0,256,256);

        ClientHelpers.bindText(TRIES);
        blit(matrices,relX - 70 - 10,relY + offsY + 110,0,0,60,18,60,18);
        Gui.drawString(matrices,font,"Moves: "+tile.getRemainingTries(),relX - 64 - 10,relY + offsY + 115,0xffffff);

        if (pattern == null) return;
        ClientHelpers.bindText(NODE);
        RenderSystem.enableBlend();
        float p = ((float)Math.sin(ticker/10f)*0.5f + 1);
        for (int i = 0; i < pattern.length;i++){
            for (int j = 0; j < pattern[i].length;j++){
                int num = pattern[i][j];
                if (num == 0) continue;
                RenderSystem.setShaderColor(NODE_COLORS[num][0], NODE_COLORS[num][1], NODE_COLORS[num][2], 1f);
                if (Helpers.isIn2DArrayBounds(pattern,i,j+1)) {
                    int right = pattern[i][j + 1];

                    if (right == num) {
                        int initX = relX + j * 14 + 6 + 6;
                        float initY = relY + i * 14 - 8 + 3.5f;

                        RenderingTools.gradientBarHorizontal(matrices, initX, initY, initX + 14, initY + 5, 1, 1, 1, (float)FDMathHelper.clamp(0,p,1));
                    }
                }
                if (Helpers.isIn2DArrayBounds(pattern,i+1,j)) {
                    int down = pattern[i + 1][j];
                    if (down == num) {
                        int initX = relX + j * 14 + 6 + 4;
                        int initY = relY + i * 14 - 8 + 6;
                        RenderingTools.gradientBarVertical(matrices, initX, initY, initX + 4, initY + 14, 1, 1, 1, (float)FDMathHelper.clamp(0,p,1));
                    }
                }
                int node = pattern[i][j];
                RenderSystem.enableBlend();
                RenderSystem.setShaderColor(NODE_COLORS[node][0],NODE_COLORS[node][1],NODE_COLORS[node][2],NODE_COLORS[node][3] * (p + 0.3f) );
                blit(matrices, relX + j * 14 + 20 - 14, relY + i * 14 - 8, 0, 0, 12, 12, 12, 12);
            }
        }

        RenderSystem.disableBlend();



        RenderSystem.setShaderColor(1,1,1,1);
        ClientHelpers.bindText(TWIGS);
        double v = VALUE.getValue();
        for (int i = 0; i < 6; i ++ ){
            int xp = (int)(relX + (i+1) * 23* (1-v));
            Gui.blit(matrices,xp - 20,relY - 18,(i % 3) *40,0,40,256,256,256);
            int xp2 = (int)((i+1) * 23* (1-v));
            Gui.blit(matrices,relX + 230 - xp2,relY - 18,(i % 3) *40 + 120,0,40,256,256,256);
        }


        super.render(matrices, mx, my, pTicks);
    }


    @Override
    public boolean isPauseScreen() {
        return false;
    }
}

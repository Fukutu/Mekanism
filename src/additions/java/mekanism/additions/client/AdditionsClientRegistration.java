package mekanism.additions.client;

import mekanism.additions.client.render.entity.RenderBalloon;
import mekanism.additions.client.render.entity.RenderObsidianTNTPrimed;
import mekanism.additions.common.AdditionsBlock;
import mekanism.additions.common.AdditionsItem;
import mekanism.additions.common.MekanismAdditions;
import mekanism.additions.common.entity.EntityBabySkeleton;
import mekanism.additions.common.entity.EntityBalloon;
import mekanism.additions.common.entity.EntityObsidianTNT;
import mekanism.additions.common.item.ItemBalloon;
import mekanism.api.block.IColoredBlock;
import mekanism.api.providers.IItemProvider;
import mekanism.client.render.MekanismRenderer;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = MekanismAdditions.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AdditionsClientRegistration {

    //TODO: Move this to a utils class
    private static void registerItemColorHandler(ItemColors colors, IItemColor itemColor, IItemProvider... items) {
        for (IItemProvider itemProvider : items) {
            colors.register(itemColor, itemProvider.getItem());
        }
    }

    //TODO: Move this to a utils class
    private static void registerBlockColorHandler(BlockColors blockColors, ItemColors itemColors, IBlockColor blockColor, IItemColor itemColor, AdditionsBlock... blocks) {
        for (AdditionsBlock additionsBlock : blocks) {
            blockColors.register(blockColor, additionsBlock.getBlock());
            itemColors.register(itemColor, additionsBlock.getItem());
        }
    }

    @SubscribeEvent
    public static void init(FMLClientSetupEvent event) {
        new AdditionsKeyHandler();

        //Register entity rendering handlers
        RenderingRegistry.registerEntityRenderingHandler(EntityObsidianTNT.class, RenderObsidianTNTPrimed::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityBalloon.class, RenderBalloon::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityBabySkeleton.class, SkeletonRenderer::new);
    }

    @SubscribeEvent
    public static void registerItemColorHandlers(ColorHandlerEvent.Item event) {
        registerBlockColorHandler(event.getBlockColors(), event.getItemColors(), (state, worldIn, pos, tintIndex) -> {
                  Block block = state.getBlock();
                  if (block instanceof IColoredBlock) {
                      return MekanismRenderer.getColorARGB(((IColoredBlock) block).getColor(), 1);
                  }
                  return -1;
              }, (stack, tintIndex) -> {
                  Item item = stack.getItem();
                  if (item instanceof BlockItem) {
                      Block block = ((BlockItem) item).getBlock();
                      if (block instanceof IColoredBlock) {
                          return MekanismRenderer.getColorARGB(((IColoredBlock) block).getColor(), 1);
                      }
                  }
                  return -1;
              },
              //Plastic Blocks
              AdditionsBlock.BLACK_PLASTIC_BLOCK, AdditionsBlock.RED_PLASTIC_BLOCK, AdditionsBlock.GREEN_PLASTIC_BLOCK, AdditionsBlock.BROWN_PLASTIC_BLOCK,
              AdditionsBlock.BLUE_PLASTIC_BLOCK, AdditionsBlock.PURPLE_PLASTIC_BLOCK, AdditionsBlock.CYAN_PLASTIC_BLOCK, AdditionsBlock.LIGHT_GRAY_PLASTIC_BLOCK,
              AdditionsBlock.GRAY_PLASTIC_BLOCK, AdditionsBlock.PINK_PLASTIC_BLOCK, AdditionsBlock.LIME_PLASTIC_BLOCK, AdditionsBlock.YELLOW_PLASTIC_BLOCK,
              AdditionsBlock.LIGHT_BLUE_PLASTIC_BLOCK, AdditionsBlock.MAGENTA_PLASTIC_BLOCK, AdditionsBlock.ORANGE_PLASTIC_BLOCK, AdditionsBlock.WHITE_PLASTIC_BLOCK,
              //Slick Plastic Blocks
              AdditionsBlock.BLACK_SLICK_PLASTIC_BLOCK, AdditionsBlock.RED_SLICK_PLASTIC_BLOCK, AdditionsBlock.GREEN_SLICK_PLASTIC_BLOCK,
              AdditionsBlock.BROWN_SLICK_PLASTIC_BLOCK, AdditionsBlock.BLUE_SLICK_PLASTIC_BLOCK, AdditionsBlock.PURPLE_SLICK_PLASTIC_BLOCK, AdditionsBlock.CYAN_SLICK_PLASTIC_BLOCK,
              AdditionsBlock.LIGHT_GRAY_SLICK_PLASTIC_BLOCK, AdditionsBlock.GRAY_SLICK_PLASTIC_BLOCK, AdditionsBlock.PINK_SLICK_PLASTIC_BLOCK,
              AdditionsBlock.LIME_SLICK_PLASTIC_BLOCK, AdditionsBlock.YELLOW_SLICK_PLASTIC_BLOCK, AdditionsBlock.LIGHT_BLUE_SLICK_PLASTIC_BLOCK,
              AdditionsBlock.MAGENTA_SLICK_PLASTIC_BLOCK, AdditionsBlock.ORANGE_SLICK_PLASTIC_BLOCK, AdditionsBlock.WHITE_SLICK_PLASTIC_BLOCK,
              //Plastic Glow Blocks
              AdditionsBlock.BLACK_PLASTIC_GLOW_BLOCK, AdditionsBlock.RED_PLASTIC_GLOW_BLOCK, AdditionsBlock.GREEN_PLASTIC_GLOW_BLOCK, AdditionsBlock.BROWN_PLASTIC_GLOW_BLOCK,
              AdditionsBlock.BLUE_PLASTIC_GLOW_BLOCK, AdditionsBlock.PURPLE_PLASTIC_GLOW_BLOCK, AdditionsBlock.CYAN_PLASTIC_GLOW_BLOCK, AdditionsBlock.LIGHT_GRAY_PLASTIC_GLOW_BLOCK,
              AdditionsBlock.GRAY_PLASTIC_GLOW_BLOCK, AdditionsBlock.PINK_PLASTIC_GLOW_BLOCK, AdditionsBlock.LIME_PLASTIC_GLOW_BLOCK, AdditionsBlock.YELLOW_PLASTIC_GLOW_BLOCK,
              AdditionsBlock.LIGHT_BLUE_PLASTIC_GLOW_BLOCK, AdditionsBlock.MAGENTA_PLASTIC_GLOW_BLOCK, AdditionsBlock.ORANGE_PLASTIC_GLOW_BLOCK, AdditionsBlock.WHITE_PLASTIC_GLOW_BLOCK,
              //Reinforced Plastic Blocks
              AdditionsBlock.BLACK_REINFORCED_PLASTIC_BLOCK, AdditionsBlock.RED_REINFORCED_PLASTIC_BLOCK, AdditionsBlock.GREEN_REINFORCED_PLASTIC_BLOCK,
              AdditionsBlock.BROWN_REINFORCED_PLASTIC_BLOCK, AdditionsBlock.BLUE_REINFORCED_PLASTIC_BLOCK, AdditionsBlock.PURPLE_REINFORCED_PLASTIC_BLOCK,
              AdditionsBlock.CYAN_REINFORCED_PLASTIC_BLOCK, AdditionsBlock.LIGHT_GRAY_REINFORCED_PLASTIC_BLOCK, AdditionsBlock.GRAY_REINFORCED_PLASTIC_BLOCK,
              AdditionsBlock.PINK_REINFORCED_PLASTIC_BLOCK, AdditionsBlock.LIME_REINFORCED_PLASTIC_BLOCK, AdditionsBlock.YELLOW_REINFORCED_PLASTIC_BLOCK,
              AdditionsBlock.LIGHT_BLUE_REINFORCED_PLASTIC_BLOCK, AdditionsBlock.MAGENTA_REINFORCED_PLASTIC_BLOCK, AdditionsBlock.ORANGE_REINFORCED_PLASTIC_BLOCK,
              AdditionsBlock.WHITE_REINFORCED_PLASTIC_BLOCK,
              //Plastic Road
              AdditionsBlock.BLACK_PLASTIC_ROAD, AdditionsBlock.RED_PLASTIC_ROAD, AdditionsBlock.GREEN_PLASTIC_ROAD, AdditionsBlock.BROWN_PLASTIC_ROAD,
              AdditionsBlock.BLUE_PLASTIC_ROAD, AdditionsBlock.PURPLE_PLASTIC_ROAD, AdditionsBlock.CYAN_PLASTIC_ROAD, AdditionsBlock.LIGHT_GRAY_PLASTIC_ROAD,
              AdditionsBlock.GRAY_PLASTIC_ROAD, AdditionsBlock.PINK_PLASTIC_ROAD, AdditionsBlock.LIME_PLASTIC_ROAD, AdditionsBlock.YELLOW_PLASTIC_ROAD,
              AdditionsBlock.LIGHT_BLUE_PLASTIC_ROAD, AdditionsBlock.MAGENTA_PLASTIC_ROAD, AdditionsBlock.ORANGE_PLASTIC_ROAD, AdditionsBlock.WHITE_PLASTIC_ROAD,
              //Plastic Fences
              AdditionsBlock.BLACK_PLASTIC_FENCE, AdditionsBlock.RED_PLASTIC_FENCE, AdditionsBlock.GREEN_PLASTIC_FENCE, AdditionsBlock.BROWN_PLASTIC_FENCE,
              AdditionsBlock.BLUE_PLASTIC_FENCE, AdditionsBlock.PURPLE_PLASTIC_FENCE, AdditionsBlock.CYAN_PLASTIC_FENCE, AdditionsBlock.LIGHT_GRAY_PLASTIC_FENCE,
              AdditionsBlock.GRAY_PLASTIC_FENCE, AdditionsBlock.PINK_PLASTIC_FENCE, AdditionsBlock.LIME_PLASTIC_FENCE, AdditionsBlock.YELLOW_PLASTIC_FENCE,
              AdditionsBlock.LIGHT_BLUE_PLASTIC_FENCE, AdditionsBlock.MAGENTA_PLASTIC_FENCE, AdditionsBlock.ORANGE_PLASTIC_FENCE, AdditionsBlock.WHITE_PLASTIC_FENCE,
              //Plastic Fence Gates
              AdditionsBlock.BLACK_PLASTIC_FENCE_GATE, AdditionsBlock.RED_PLASTIC_FENCE_GATE, AdditionsBlock.GREEN_PLASTIC_FENCE_GATE, AdditionsBlock.BROWN_PLASTIC_FENCE_GATE,
              AdditionsBlock.BLUE_PLASTIC_FENCE_GATE, AdditionsBlock.PURPLE_PLASTIC_FENCE_GATE, AdditionsBlock.CYAN_PLASTIC_FENCE_GATE,
              AdditionsBlock.LIGHT_GRAY_PLASTIC_FENCE_GATE, AdditionsBlock.GRAY_PLASTIC_FENCE_GATE, AdditionsBlock.PINK_PLASTIC_FENCE_GATE,
              AdditionsBlock.LIME_PLASTIC_FENCE_GATE, AdditionsBlock.YELLOW_PLASTIC_FENCE_GATE, AdditionsBlock.LIGHT_BLUE_PLASTIC_FENCE_GATE,
              AdditionsBlock.MAGENTA_PLASTIC_FENCE_GATE, AdditionsBlock.ORANGE_PLASTIC_FENCE_GATE, AdditionsBlock.WHITE_PLASTIC_FENCE_GATE,
              //Plastic Slabs
              AdditionsBlock.BLACK_PLASTIC_SLAB, AdditionsBlock.RED_PLASTIC_SLAB, AdditionsBlock.GREEN_PLASTIC_SLAB, AdditionsBlock.BROWN_PLASTIC_SLAB,
              AdditionsBlock.BLUE_PLASTIC_SLAB, AdditionsBlock.PURPLE_PLASTIC_SLAB, AdditionsBlock.CYAN_PLASTIC_SLAB, AdditionsBlock.LIGHT_GRAY_PLASTIC_SLAB,
              AdditionsBlock.GRAY_PLASTIC_SLAB, AdditionsBlock.PINK_PLASTIC_SLAB, AdditionsBlock.LIME_PLASTIC_SLAB, AdditionsBlock.YELLOW_PLASTIC_SLAB,
              AdditionsBlock.LIGHT_BLUE_PLASTIC_SLAB, AdditionsBlock.MAGENTA_PLASTIC_SLAB, AdditionsBlock.ORANGE_PLASTIC_SLAB, AdditionsBlock.WHITE_PLASTIC_SLAB,
              //Plastic Fence Gates
              AdditionsBlock.BLACK_PLASTIC_STAIRS, AdditionsBlock.RED_PLASTIC_STAIRS, AdditionsBlock.GREEN_PLASTIC_STAIRS, AdditionsBlock.BROWN_PLASTIC_STAIRS,
              AdditionsBlock.BLUE_PLASTIC_STAIRS, AdditionsBlock.PURPLE_PLASTIC_STAIRS, AdditionsBlock.CYAN_PLASTIC_STAIRS, AdditionsBlock.LIGHT_GRAY_PLASTIC_STAIRS,
              AdditionsBlock.GRAY_PLASTIC_STAIRS, AdditionsBlock.PINK_PLASTIC_STAIRS, AdditionsBlock.LIME_PLASTIC_STAIRS, AdditionsBlock.YELLOW_PLASTIC_STAIRS,
              AdditionsBlock.LIGHT_BLUE_PLASTIC_STAIRS, AdditionsBlock.MAGENTA_PLASTIC_STAIRS, AdditionsBlock.ORANGE_PLASTIC_STAIRS, AdditionsBlock.WHITE_PLASTIC_STAIRS,
              //Glow Panels
              AdditionsBlock.BLACK_GLOW_PANEL, AdditionsBlock.RED_GLOW_PANEL, AdditionsBlock.GREEN_GLOW_PANEL, AdditionsBlock.BROWN_GLOW_PANEL,
              AdditionsBlock.BLUE_GLOW_PANEL, AdditionsBlock.PURPLE_GLOW_PANEL, AdditionsBlock.CYAN_GLOW_PANEL, AdditionsBlock.LIGHT_GRAY_GLOW_PANEL,
              AdditionsBlock.GRAY_GLOW_PANEL, AdditionsBlock.PINK_GLOW_PANEL, AdditionsBlock.LIME_GLOW_PANEL, AdditionsBlock.YELLOW_GLOW_PANEL,
              AdditionsBlock.LIGHT_BLUE_GLOW_PANEL, AdditionsBlock.MAGENTA_GLOW_PANEL, AdditionsBlock.ORANGE_GLOW_PANEL, AdditionsBlock.WHITE_GLOW_PANEL);

        registerItemColorHandler(event.getItemColors(), (stack, tintIndex) -> {
                  Item item = stack.getItem();
                  if (item instanceof ItemBalloon) {
                      ItemBalloon balloon = (ItemBalloon) item;
                      return MekanismRenderer.getColorARGB(balloon.getColor(), 1);
                  }
                  return -1;
              }, AdditionsItem.BLACK_BALLOON, AdditionsItem.RED_BALLOON, AdditionsItem.GREEN_BALLOON, AdditionsItem.BROWN_BALLOON, AdditionsItem.BLUE_BALLOON,
              AdditionsItem.PURPLE_BALLOON, AdditionsItem.CYAN_BALLOON, AdditionsItem.LIGHT_GRAY_BALLOON, AdditionsItem.GRAY_BALLOON, AdditionsItem.PINK_BALLOON,
              AdditionsItem.LIME_BALLOON, AdditionsItem.YELLOW_BALLOON, AdditionsItem.LIGHT_BLUE_BALLOON, AdditionsItem.MAGENTA_BALLOON, AdditionsItem.ORANGE_BALLOON,
              AdditionsItem.WHITE_BALLOON);
    }
}
package com.ldtteam.structurize.blocks;

import com.ldtteam.structurize.api.util.constant.Suppression;
import com.ldtteam.structurize.blocks.interfaces.IBlockStructurize;
import net.minecraft.block.Block;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;

public abstract class AbstractBlockStructurizePillar<B extends AbstractBlockStructurizePillar<B>> extends RotatedPillarBlock implements IBlockStructurize<B>
{
    public AbstractBlockStructurizePillar(final Properties properties)
    {
        super(properties);
    }

    @Override
    @SuppressWarnings(Suppression.UNCHECKED)
    public B registerBlock(final IForgeRegistry<Block> registry)
    {
        registry.register(this);
        return (B) this;
    }

    @Override
    public void registerItemBlock(final IForgeRegistry<Item> registry, final Item.Properties properties)
    {
        registry.register((new BlockItem(this, properties)).setRegistryName(this.getRegistryName()));
    }
}

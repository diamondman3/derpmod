package com.tildenprep.derpmod.block;

import java.util.Random;

import com.tildenprep.derpmod.DerpMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneRepeater;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by kenny on 4/9/14.
 */
public class RepeaterBomb extends BlockRedstoneRepeater {

    boolean exploded;

    public RepeaterBomb ()
    {
    	super(false);
        setUnlocalizedName("repeaterBomb");
        setCreativeTab(DerpMod.tabDerpMod);
        setHardness(0F);
        exploded = false;
        setStepSound(Block.soundTypeStone);

    }

    public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
    {
        return super.getItemDropped(this.getDefaultState(), p_149650_2_, p_149650_3_);
    }

    public void onNeighborBlockChange(World world, int x, int y, int z, Block p_149695_5_){
        if(world.isBlockIndirectlyGettingPowered(new BlockPos(x,y,z))>0){
            world.setBlockState(new BlockPos(x, y, z), Blocks.air.getDefaultState()); //destroy the repeater bomb itself
            world.createExplosion(null, x, y, z, 6F, true);
        }
    }

    protected boolean canPowerSide(Block blockIn)
    {
        return isRedstoneRepeaterBlockID(blockIn);
    }

	/*@Override
	protected int func_149901_b(int var1) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected BlockRedstoneDiode getBlockPowered() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected BlockRedstoneDiode getBlockUnpowered() {
		// TODO Auto-generated method stub
		return null;
	}*/
}


//    @Override
//    public void onBlockDestroyedByPlayer(){
//
//        super.onBlockDestroyedByPlayer(World world);
//    }



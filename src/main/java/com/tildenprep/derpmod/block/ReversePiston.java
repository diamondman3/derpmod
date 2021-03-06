package com.tildenprep.derpmod.block;

import com.tildenprep.derpmod.DerpMod;
import net.minecraft.block.*;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockPistonStructureHelper;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.*;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * Created by student on 7/8/2015.
 */
public class ReversePiston extends BlockPistonBase {

    //todo: Sticky piston arm not sticky.
    //todo: Arm deleted when block placed adjacent to arm, presumably due to invalid location.

    boolean isSticky = false;
    boolean isReversed = false;
    public final String name = "reversePiston";

    public ReversePiston(boolean isSticky) {
        super(isSticky);
        setCreativeTab(DerpMod.tabDerpMod);
        if (!isSticky) {
            setUnlocalizedName(DerpMod.MODID+"_"+name);
        } else {
            setUnlocalizedName(DerpMod.MODID+"_reverseStickyPiston");
        }
    }

    public String getName(){
        return name;
    }

    public static EnumFacing getFacing(int meta) {
        int j = meta & 7;
        return j > 5 ? null : EnumFacing.getFront(j);
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        worldIn.setBlockState(pos, state.withProperty(FACING, getFacingFromEntity(worldIn, pos, placer)), 2);

        if (!worldIn.isRemote) {
            this.checkForMove(worldIn, pos, state);
        }
    }

    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
        if (!worldIn.isRemote) {
            this.checkForMove(worldIn, pos, state);
        }
    }

    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.isRemote && worldIn.getTileEntity(pos) == null) {
            this.checkForMove(worldIn, pos, state);
        }
    }

    private void checkForMove(World worldIn, BlockPos pos, IBlockState state) {
        EnumFacing enumfacing = (EnumFacing) state.getValue(FACING);
        boolean flag = this.shouldBeExtended(worldIn, pos, enumfacing);

        if (flag && !((Boolean) state.getValue(EXTENDED)).booleanValue()) {
            if ((new BlockPistonStructureHelper(worldIn, pos, enumfacing, true)).canMove()) {
                worldIn.addBlockEvent(pos, this, 0, enumfacing.getIndex());
            }
        } else if (!flag && ((Boolean) state.getValue(EXTENDED)).booleanValue()) {
            worldIn.setBlockState(pos, state.withProperty(EXTENDED, Boolean.valueOf(false)), 2);
            worldIn.addBlockEvent(pos, this, 1, enumfacing.getIndex());
        }
    }

    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty(FACING, getFacingFromEntity(worldIn, pos, placer)).withProperty(EXTENDED, Boolean.valueOf(false));
    }

    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
        IBlockState iblockstate = worldIn.getBlockState(pos);

        if (iblockstate.getBlock() == this && ((Boolean) iblockstate.getValue(EXTENDED)).booleanValue()) {
            float f = 0.25F;
            EnumFacing enumfacing = (EnumFacing) iblockstate.getValue(FACING);

            if (enumfacing != null) {
                switch (ReversePiston.SwitchEnumFacing.FACING_LOOKUP[enumfacing.ordinal()]) {
                    case 1:
                        this.setBlockBounds(0.0F, 0.25F, 0.0F, 1.0F, 0.1F, 1.0F);
                        break;
                    case 2:
                        this.setBlockBounds(0.0F, 1.0F, 0.0F, 1.0F, 0.75F, 1.0F);
                        break;
                    case 3:
                        this.setBlockBounds(0.0F, 0.0F, 0.25F, 1.0F, 1.0F, 1.0F);
                        break;
                    case 4:
                        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.75F);
                        break;
                    case 5:
                        this.setBlockBounds(0.25F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                        break;
                    case 6:
                        this.setBlockBounds(0.0F, 0.0F, 0.0F, 0.75F, 1.0F, 1.0F);
                }
            }
        } else {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    public void setBlockBoundsForItemRender() {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List list, Entity collidingEntity) {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
    }

    public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
        return super.getCollisionBoundingBox(worldIn, pos, state);
    }

    public boolean isFullCube() {
        return false;
    }

    public static EnumFacing getFacingFromEntity(World worldIn, BlockPos clickedBlock, EntityLivingBase entityIn) {
        if (MathHelper.abs((float) entityIn.posX - (float) clickedBlock.getX()) < 2.0F && MathHelper.abs((float) entityIn.posZ - (float) clickedBlock.getZ()) < 2.0F) {
            double d0 = entityIn.posY + (double) entityIn.getEyeHeight();

            if (d0 - (double) clickedBlock.getY() > 2.0D) {
                return EnumFacing.UP;
            }

            if ((double) clickedBlock.getY() - d0 > 0.0D) {
                return EnumFacing.DOWN;
            }
        }

        return entityIn.getHorizontalFacing().getOpposite();
    }

    private boolean shouldBeExtended(World worldIn, BlockPos pos, EnumFacing facing) {
        EnumFacing[] aenumfacing = EnumFacing.values();
        int i = aenumfacing.length;
        int j;

        for (j = 0; j < i; ++j) {
            EnumFacing enumfacing1 = aenumfacing[j];

            if (enumfacing1 != facing && worldIn.isSidePowered(pos.offset(enumfacing1), enumfacing1)) {
                return true;
            }
        }

        if (worldIn.isSidePowered(pos, EnumFacing.NORTH)) {
            return true;
        } else {
            BlockPos blockpos1 = pos.up();
            EnumFacing[] aenumfacing1 = EnumFacing.values();
            j = aenumfacing1.length;

            for (int k = 0; k < j; ++k) {
                EnumFacing enumfacing2 = aenumfacing1[k];

                if (enumfacing2 != EnumFacing.DOWN && worldIn.isSidePowered(blockpos1.offset(enumfacing2), enumfacing2)) {
                    return true;
                }
            }

            return false;
        }
    }


    //Modified from BlockPistonBase
    public static boolean canPush(Block blockIn, World worldIn, BlockPos pos, EnumFacing direction, boolean allowDestroy) {
        // direction = direction.getOpposite();
        {
            if (blockIn == Blocks.obsidian) {
                return false;
            } else if (!worldIn.getWorldBorder().contains(pos)) {
                return false;
            } else if (pos.getY() >= 0 && (direction != EnumFacing.DOWN || pos.getY() != 0)) {
                if (pos.getY() <= worldIn.getHeight() - 1 && (direction != EnumFacing.UP || pos.getY() != worldIn.getHeight() - 1)) {
                    if (blockIn != Blocks.piston && blockIn != Blocks.sticky_piston) {
                        if (blockIn.getBlockHardness(worldIn, pos) == -1.0F) {
                            return false;
                        }

                        if (blockIn.getMobilityFlag() == 2) {
                            return false;
                        }

                        if (blockIn.getMobilityFlag() == 1) {
                            if (!allowDestroy) {
                                return false;
                            }

                            return true;
                        }
                    } else if (((Boolean) worldIn.getBlockState(pos).getValue(EXTENDED)).booleanValue()) {
                        return false;
                    }

                    return !(blockIn.hasTileEntity(worldIn.getBlockState(pos)));
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }

    //Modified from BlockPistonBase
    private boolean doMove(World worldIn, BlockPos pos, EnumFacing direction, boolean extending) {
        if (!isReversed) {
            direction = direction.getOpposite();
            isReversed = true;
        }
        if (!extending) {
            worldIn.setBlockToAir(pos.offset(direction));
        }
        BlockPistonStructureHelper blockpistonstructurehelper = new BlockPistonStructureHelper(worldIn, pos, direction, extending);
        List list = blockpistonstructurehelper.getBlocksToMove();
        List list1 = blockpistonstructurehelper.getBlocksToDestroy();

        if (!blockpistonstructurehelper.canMove()) {
            return false;
        } else {
            int i = list.size() + list1.size();
            Block[] ablock = new Block[i];
            EnumFacing enumfacing1 = extending ? direction : direction.getOpposite();
            int j;
            BlockPos blockpos1;

            for (j = list1.size() - 1; j >= 0; --j) {
                blockpos1 = (BlockPos) list1.get(j);
                Block block = worldIn.getBlockState(blockpos1).getBlock();
                //With our change to how snowballs are dropped this needs to disallow to mimic vanilla behavior.
                float chance = block instanceof BlockSnow ? -1.0f : 1.0f;
                block.dropBlockAsItemWithChance(worldIn, blockpos1, worldIn.getBlockState(blockpos1), chance, 0);
                worldIn.setBlockToAir(blockpos1);
                --i;
                ablock[i] = block;
            }

            IBlockState iblockstate;

            for (j = list.size() - 1; j >= 0; --j) {
                blockpos1 = (BlockPos) list.get(j);
                iblockstate = worldIn.getBlockState(blockpos1);
                Block block1 = iblockstate.getBlock();
                block1.getMetaFromState(iblockstate);
                worldIn.setBlockToAir(blockpos1);
                blockpos1 = blockpos1.offset(enumfacing1);
                worldIn.setBlockState(blockpos1, Blocks.piston_extension.getDefaultState().withProperty(FACING, direction), 4);
                worldIn.setTileEntity(blockpos1, BlockPistonMoving.newTileEntity(iblockstate, direction, extending, false));
                --i;
                ablock[i] = block1;
            }

            BlockPos blockpos2 = pos.offset(direction);

            if (extending) {
                BlockPistonExtension.EnumPistonType enumpistontype = this.isSticky ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT;
                iblockstate = Blocks.piston_head.getDefaultState().withProperty(BlockPistonExtension.FACING, direction).withProperty(BlockPistonExtension.TYPE, enumpistontype);
                IBlockState iblockstate1 = Blocks.piston_extension.getDefaultState().withProperty(BlockPistonMoving.FACING, direction).withProperty(BlockPistonMoving.TYPE, this.isSticky ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT);
                worldIn.setBlockState(blockpos2, iblockstate1, 4);
                worldIn.setTileEntity(blockpos2, BlockPistonMoving.newTileEntity(iblockstate, direction, true, false));
            }

            int k;

            for (k = list1.size() - 1; k >= 0; --k) {
                worldIn.notifyNeighborsOfStateChange((BlockPos) list1.get(k), ablock[i++]);
            }

            for (k = list.size() - 1; k >= 0; --k) {
                worldIn.notifyNeighborsOfStateChange((BlockPos) list.get(k), ablock[i++]);
            }

            if (extending) {
                worldIn.notifyNeighborsOfStateChange(blockpos2, Blocks.piston_head);
                worldIn.notifyNeighborsOfStateChange(pos, this);
            }
            if (isReversed) {
                direction = direction.getOpposite();
                isReversed = false;
            }

            return true;
        }

    }

    public boolean onBlockEventReceived(World worldIn, BlockPos pos, IBlockState state, int eventID, int eventParam) {
        EnumFacing enumfacing = (EnumFacing) state.getValue(FACING);

        if (!worldIn.isRemote) {
            boolean flag = this.shouldBeExtended(worldIn, pos, enumfacing);

            if (flag && eventID == 1) {
                worldIn.setBlockState(pos, state.withProperty(EXTENDED, Boolean.valueOf(true)), 2);
                return false;
            }

            if (!flag && eventID == 0) {
                return false;
            }
        }

        if (eventID == 0) {
            if (!this.doMove(worldIn, pos, enumfacing, true)) {
                return false;
            }

            worldIn.setBlockState(pos, state.withProperty(EXTENDED, Boolean.valueOf(true)), 2);
            worldIn.playSoundEffect((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, "tile.piston.out", 0.5F, worldIn.rand.nextFloat() * 0.25F + 0.6F);
        } else if (eventID == 1) {
            TileEntity tileentity1 = worldIn.getTileEntity(pos.offset(enumfacing));

            if (tileentity1 instanceof TileEntityPiston) {
                ((TileEntityPiston) tileentity1).clearPistonTileEntity();
            }

            worldIn.setBlockState(pos, Blocks.piston_extension.getDefaultState().withProperty(BlockPistonMoving.FACING, enumfacing).withProperty(BlockPistonMoving.TYPE, this.isSticky ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT), 3);
            worldIn.setTileEntity(pos, BlockPistonMoving.newTileEntity(this.getStateFromMeta(eventParam), enumfacing, false, true));

            if (this.isSticky) {
                BlockPos blockpos1 = pos.add(enumfacing.getFrontOffsetX() * 2, enumfacing.getFrontOffsetY() * 2, enumfacing.getFrontOffsetZ() * 2);
                Block block = worldIn.getBlockState(blockpos1).getBlock();
                boolean flag1 = false;

                if (block == Blocks.piston_extension) {
                    TileEntity tileentity = worldIn.getTileEntity(blockpos1);

                    if (tileentity instanceof TileEntityPiston) {
                        TileEntityPiston tileentitypiston = (TileEntityPiston) tileentity;

                        if (tileentitypiston.getFacing() == enumfacing && tileentitypiston.isExtending()) {
                            tileentitypiston.clearPistonTileEntity();
                            flag1 = true;
                        }
                    }
                }

                if (!flag1 && !block.isAir(worldIn, blockpos1) && canPush(block, worldIn, blockpos1, enumfacing.getOpposite(), false) && (block.getMobilityFlag() == 0 || block == Blocks.piston || block == Blocks.sticky_piston)) {
                    this.doMove(worldIn, pos, enumfacing, false);
                }
            } else {
                worldIn.setBlockToAir(pos.offset(enumfacing));
            }

            worldIn.playSoundEffect((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, "tile.piston.in", 0.5F, worldIn.rand.nextFloat() * 0.15F + 0.6F);
        }

        return true;
    }

    public IBlockState getStateFromMeta(int meta) {
        return super.getStateFromMeta(meta);
    }

    @SideOnly(Side.CLIENT)
    public IBlockState getStateForEntityRender(IBlockState state) {
        return super.getStateForEntityRender(state);
    }

    public int getMetaFromState(IBlockState state) {
        return super.getMetaFromState(state);
    }

    protected BlockState createBlockState() {
        return super.createBlockState();
    }

    static final class SwitchEnumFacing {
        static final int[] FACING_LOOKUP = new int[EnumFacing.values().length];
        private static final String __OBFID = "CL_00002037";

        static {
            try {
                FACING_LOOKUP[EnumFacing.DOWN.ordinal()] = 1;
            } catch (NoSuchFieldError var6) {
                ;
            }

            try {
                FACING_LOOKUP[EnumFacing.UP.ordinal()] = 2;
            } catch (NoSuchFieldError var5) {
                ;
            }

            try {
                FACING_LOOKUP[EnumFacing.NORTH.ordinal()] = 3;
            } catch (NoSuchFieldError var4) {
                ;
            }

            try {
                FACING_LOOKUP[EnumFacing.SOUTH.ordinal()] = 4;
            } catch (NoSuchFieldError var3) {
                ;
            }

            try {
                FACING_LOOKUP[EnumFacing.WEST.ordinal()] = 5;
            } catch (NoSuchFieldError var2) {
                ;
            }

            try {
                FACING_LOOKUP[EnumFacing.EAST.ordinal()] = 6;
            } catch (NoSuchFieldError var1) {
                ;
            }
        }
    }

}

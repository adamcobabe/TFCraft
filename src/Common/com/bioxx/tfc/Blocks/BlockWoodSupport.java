package com.bioxx.tfc.Blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.bioxx.tfc.Reference;
import com.bioxx.tfc.TFCBlocks;
import com.bioxx.tfc.Core.TFCTabs;
import com.bioxx.tfc.api.Constant.Global;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockWoodSupport extends BlockTerra
{
	String[] woodNames;
	IIcon[] icons;

	public BlockWoodSupport(Material material)
	{
		super(Material.wood);
		this.setCreativeTab(TFCTabs.TFCBuilding);
		woodNames = new String[16];
		System.arraycopy(Global.WOOD_ALL, 0, woodNames, 0, 16);
		icons = new IIcon[woodNames.length];
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(Item item, CreativeTabs tabs, List list)
	{
		if(TFCBlocks.isBlockVSupport(this))
			for(int i = 0; i < woodNames.length; i++)
				list.add(new ItemStack(this, 1, i));
	}

	public static boolean hasSupportsInRange(World world, int x, int y, int z, int range)
	{
		return getSupportsInRangeDir(world, x, y, z, range, false) != null;
	}

	public static boolean isSupportConnected(World world, int x, int y, int z)
	{
		return getSupportsInRangeDir(world, x, y, z, 5, true) != null;
	}

	public static ForgeDirection getSupportDirection(World world, int x, int y, int z)
	{
		int[] r = getSupportsInRangeDir(world, x, y, z, 5, false);
		if(r[0] > r[1])
			return ForgeDirection.NORTH;
		if(r[1] > r[0])
			return ForgeDirection.SOUTH;
		if(r[3] > r[2])
			return ForgeDirection.EAST;
		if(r[2] > r[3])
			return ForgeDirection.WEST;

		return ForgeDirection.UNKNOWN;
	}

	public static int[] getSupportsInRangeDir(World world, int x, int y, int z, int range, boolean checkConnection)
	{
		int n = 0; boolean foundN = false;
		int s = 0; boolean foundS = false;
		int e = 0; boolean foundE = false;
		int w = 0; boolean foundW = false;
		for(int i = 1; i < range; i++)
		{
			if(!foundE)
			{
				if(!checkConnection || TFCBlocks.isBlockHSupport(world.getBlock(x+i, y, z)))
					e++;
				else e -= 50;
				if(TFCBlocks.isBlockVSupport(world.getBlock(x+i, y, z)) && e >= 0)
					if(scanVert(world, x+i, y, z))
						foundE = true;
					else e -= 50;
			}
			if(!foundW)
			{
				if(!checkConnection || TFCBlocks.isBlockHSupport(world.getBlock(x-i, y, z)))
					w++;
				else w -= 50;
				if(TFCBlocks.isBlockVSupport(world.getBlock(x-i, y, z)) && w >= 0)
					if(scanVert(world, x-i, y, z))
						foundW = true;
					else w -= 50;
			}
			if(!foundS)
			{
				if(!checkConnection || TFCBlocks.isBlockHSupport(world.getBlock(x, y, z+i)))
					s++;
				else s -= 50;

				if(TFCBlocks.isBlockVSupport(world.getBlock(x, y, z+i)) && s >= 0)
					if(scanVert(world, x, y, z+i))
						foundS = true;
					else s -= 50;
			}
			if(!foundN)
			{
				if(!checkConnection || TFCBlocks.isBlockHSupport(world.getBlock(x, y, z-i)))
					n++;
				else n -= 50;

				if(TFCBlocks.isBlockVSupport(world.getBlock(x, y, z-i)) && n >= 0)
					if(scanVert(world, x, y, z-i))
						foundN = true;
					else n -= 50;
			}
		}
		if(foundE && foundW)
			return new int[]{0, 0, w, e};
		if(foundS && foundN)
			return new int[]{n, s, 0, 0};
		return null;
	}

	private static boolean scanVert(World world, int x, int y, int z)
	{
		int out = 1;
		while(TFCBlocks.isBlockVSupport(world.getBlock(x, y-out, z)))
			out++;

		return out > 2;
	}

	@Override
	public boolean getBlocksMovement(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
	{
		return true;
	}

	public static int isNextToSupport(World world, int x, int y, int z)
	{
		if(TFCBlocks.isBlockVSupport(world.getBlock(x+1, y, z)) || TFCBlocks.isBlockHSupport(world.getBlock(x+1, y, z)))
			return 5;
		if(TFCBlocks.isBlockVSupport(world.getBlock(x-1, y, z)) || TFCBlocks.isBlockHSupport(world.getBlock(x-1, y, z)))
			return 4;
		if(TFCBlocks.isBlockVSupport(world.getBlock(x, y, z+1)) || TFCBlocks.isBlockHSupport(world.getBlock(x, y, z+1)))
			return 3;
		if(TFCBlocks.isBlockVSupport(world.getBlock(x, y, z-1)) || TFCBlocks.isBlockHSupport(world.getBlock(x, y, z-1)))
			return 2;
		return 0;
	}

	private Boolean isNearVerticalSupport(World world, int i, int j, int k)
	{
		for(int y = -1; y < 0; y++)
		{
			for(int x = -6; x < 4; x++)
			{
				for(int z = -6; z < 4; z++)
				{
					if(TFCBlocks.isBlockVSupport(world.getBlock(i+x, j+y, k+z)))
						return true;
				}
			}
		}
		return false;
	}

	@Override
	public int damageDropped(int j)
	{
		return j;
	}

	@Override
	public IIcon getIcon(int side, int meta)
	{
		if(meta<icons.length)
			return icons[meta];
		return TFCBlocks.WoodSupportH2.getIcon(side, meta-16);
	}

	@Override
	public void registerBlockIcons(IIconRegister registerer)
	{
		for(int i = 0; i < woodNames.length; i++)
			icons[i] = registerer.registerIcon(Reference.ModID + ":" + "wood/WoodSheet/" + woodNames[i]);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
	{
		return getCollisionBoundingBoxFromPoolIBlockAccess(world, x, y, z).getOffsetBoundingBox(x, y, z);
	}

	private AxisAlignedBB getCollisionBoundingBoxFromPoolIBlockAccess(IBlockAccess blockAccess, int x, int y, int z)
	{
		Boolean isHorizontal = TFCBlocks.isBlockHSupport(blockAccess.getBlock(x, y, z));
		Boolean isVertical = TFCBlocks.isBlockVSupport(blockAccess.getBlock(x, y, z));

		double minX = 0.25; double minY = 0.0; double minZ = 0.25;
		double maxX = 0.75; double maxY = 0.75; double maxZ = 0.75;

		if(isHorizontal)
		{
			minY = 0.5;
			maxY = 1;
			if(TFCBlocks.isBlockVSupport(blockAccess.getBlock(x+1, y, z)) || TFCBlocks.isBlockHSupport(blockAccess.getBlock(x+1, y, z)))
				maxX = 1;
			if(TFCBlocks.isBlockVSupport(blockAccess.getBlock(x-1, y, z)) || TFCBlocks.isBlockHSupport(blockAccess.getBlock(x-1, y, z)))
				minX = 0;
			if(TFCBlocks.isBlockVSupport(blockAccess.getBlock(x, y, z+1)) || TFCBlocks.isBlockHSupport(blockAccess.getBlock(x, y, z+1)))
				maxZ = 1;
			if(TFCBlocks.isBlockVSupport(blockAccess.getBlock(x, y, z-1)) || TFCBlocks.isBlockHSupport(blockAccess.getBlock(x, y, z-1)))
				minZ = 0;
			/*if(TFCBlocks.isBlockVSupport(blockAccess.getBlock(x, y-1, z)))
				minY = 0;*/
		}
		else
		{
			minY = 0;
			maxY = 1;
			/*if(TFCBlocks.isBlockVSupport(blockAccess.getBlock(x+1, y, z)) || TFCBlocks.isBlockHSupport(blockAccess.getBlock(x+1, y, z)))
				maxX = 1;
			if(TFCBlocks.isBlockVSupport(blockAccess.getBlock(x-1, y, z)) || TFCBlocks.isBlockHSupport(blockAccess.getBlock(x-1, y, z)))
				minX = 0;
			if(TFCBlocks.isBlockVSupport(blockAccess.getBlock(x, y, z+1)) || TFCBlocks.isBlockHSupport(blockAccess.getBlock(x, y, z+1)))
				maxZ = 1;
			if(TFCBlocks.isBlockVSupport(blockAccess.getBlock(x, y, z-1)) || TFCBlocks.isBlockHSupport(blockAccess.getBlock(x, y, z-1)))
				minZ = 0;*/
		}

		return AxisAlignedBB.getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess blockAccess, int x, int y, int z)
	{
		AxisAlignedBB aabb = getCollisionBoundingBoxFromPoolIBlockAccess(blockAccess, x, y, z);
		this.setBlockBounds((float)aabb.minX, (float)aabb.minY, (float)aabb.minZ, (float)aabb.maxX, (float)aabb.maxY, (float)aabb.maxZ);
	}

	@Override
	public int getRenderType()
	{
		if(TFCBlocks.isBlockVSupport(this))
			return TFCBlocks.woodSupportRenderIdV;
		else
			return TFCBlocks.woodSupportRenderIdH;
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z)
	{
		Boolean isHorizontal = TFCBlocks.isBlockHSupport(world.getBlock(x, y, z));
		Boolean isVertical = TFCBlocks.isBlockVSupport(world.getBlock(x, y, z));

		double minX = 0.25; double minY = 0.0; double minZ = 0.25;
		double maxX = 0.75; double maxY = 0.75; double maxZ = 0.75;


		if(isHorizontal)
		{
			minY = 0.5;
			maxY = 1;
			if(TFCBlocks.isBlockVSupport(world.getBlock(x+1, y, z)) || TFCBlocks.isBlockHSupport(world.getBlock(x+1, y, z)))
				maxX = 1;
			if(TFCBlocks.isBlockVSupport(world.getBlock(x-1, y, z)) || TFCBlocks.isBlockHSupport(world.getBlock(x-1, y, z)))
				minX = 0;
			if(TFCBlocks.isBlockVSupport(world.getBlock(x, y, z+1)) || TFCBlocks.isBlockHSupport(world.getBlock(x, y, z+1)))
				maxZ = 1;
			if(TFCBlocks.isBlockVSupport(world.getBlock(x, y, z-1)) || TFCBlocks.isBlockHSupport(world.getBlock(x, y, z-1)))
				minZ = 0;
			if(TFCBlocks.isBlockVSupport(world.getBlock(x, y-1, z)))
				minY = 0;
		}
		else
		{
			minY = 0;
			maxY = 1;
			if(TFCBlocks.isBlockVSupport(world.getBlock(x+1, y, z)) || TFCBlocks.isBlockHSupport(world.getBlock(x+1, y, z)))
				maxX = 1;
			if(TFCBlocks.isBlockVSupport(world.getBlock(x-1, y, z)) || TFCBlocks.isBlockHSupport(world.getBlock(x-1, y, z)))
				minX = 0;
			if(TFCBlocks.isBlockVSupport(world.getBlock(x, y, z+1)) || TFCBlocks.isBlockHSupport(world.getBlock(x, y, z+1)))
				maxZ = 1;
			if(TFCBlocks.isBlockVSupport(world.getBlock(x, y, z-1)) || TFCBlocks.isBlockHSupport(world.getBlock(x, y, z-1)))
				minZ = 0;
		}

		return AxisAlignedBB.getBoundingBox(x + minX, y + minY, z + minZ, x + maxX, y + maxY, z + maxZ);
	}

	@Override
	public void harvestBlock(World world, EntityPlayer entityplayer, int i, int j, int k, int l)
	{
		if(this == TFCBlocks.WoodSupportH)
			dropBlockAsItem(world, i, j, k, new ItemStack(TFCBlocks.WoodSupportV, 1, l));
		else if(this == TFCBlocks.WoodSupportH2)
			dropBlockAsItem(world, i, j, k, new ItemStack(TFCBlocks.WoodSupportV2, 1, l));
		else
			dropBlockAsItem(world, i, j, k, new ItemStack(this, 1, l));
	}

	@Override
	public boolean isBlockNormalCube()
	{
		return false;
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess par1iBlockAccess, int par2, int par3, int par4, int par5)
	{
		return true;
	}

	@Override
	public void onBlockPlacedBy(World world, int i, int j, int k, EntityLivingBase entity, ItemStack is) 
	{
		super.onBlockPlacedBy(world, i, j, k, entity, is);
		//if(!world.isRemote)
		//	onNeighborBlockChange(world, i, j, k, world.getBlock(i, j, k));
	}

	@Override
	public void onNeighborBlockChange(World world, int i, int j, int k, Block l)
	{
		boolean isOtherHorizontal = TFCBlocks.isBlockHSupport(l);
		boolean isOtherVertical = TFCBlocks.isBlockVSupport(l);
		boolean isHorizontal = TFCBlocks.isBlockHSupport(world.getBlock(i, j, k));
		boolean isVertical = TFCBlocks.isBlockVSupport(world.getBlock(i, j, k));

		int meta = world.getBlockMetadata(i, j, k);

		if(isVertical && isOtherVertical)//Vertical Beam
		{
			//if the block directly beneath the support is not solid or a support then break the support
			if(!world.getBlock(i, j-1, k).isOpaqueCube() && !TFCBlocks.isBlockVSupport(world.getBlock(i, j-1, k)))
			{	
				harvestBlock(world, null, i, j, k,  meta);
				world.setBlockToAir(i, j, k);
			}
		}
		else if(isHorizontal)//Horizontal Beam
		{
			boolean b1 = !isSupportConnected(world,i,j,k);
			if( b1)
			{
				harvestBlock(world, null, i, j, k,  meta);
				world.setBlockToAir(i, j, k);
			}
			else if(TFCBlocks.isBlockVSupport(world.getBlock(i, j-1, k)))
			{
				if(this == TFCBlocks.WoodSupportH)
					world.setBlock(i, j, k, TFCBlocks.WoodSupportV, meta, 0x2);
				else if(this == TFCBlocks.WoodSupportH2)
					world.setBlock(i, j, k, TFCBlocks.WoodSupportV2, meta, 0x2);
			}
		}
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, int x, int y, int z, int side)
	{
		Block downBlock = world.getBlock(x, y-1, z);
		//bottom
		if(!TFCBlocks.isBlockVSupport(downBlock))
		{
			if(side == 0 && world.isAirBlock(x, y-1, z))
			{
				if(isNextToSupport(world,x,y,z) != 0 && hasSupportsInRange(world, x,y,z,5))
					return true;
			}
			else if(side == 1 && world.getBlock(x, y-1, z).isOpaqueCube())
			{
				return true;
			}
			else if(side == 2)
			{
				if(isNextToSupport(world,x,y,z) != 0 && hasSupportsInRange(world, x,y,z,5))
					return true;
			}
			else if(side == 3)
			{
				if(isNextToSupport(world,x,y,z) != 0 && hasSupportsInRange(world, x,y,z,5))
					return true;
			}
			else if(side == 4)
			{
				if(isNextToSupport(world,x,y,z) != 0  && hasSupportsInRange(world, x,y,z,5))
					return true;
			}
			else if(side == 5)
			{
				if(isNextToSupport(world,x,y,z) != 0 && hasSupportsInRange(world, x,y,z,5))
					return true;
			}
		}
		else if(TFCBlocks.isBlockVSupport(downBlock) || downBlock.isOpaqueCube())
		{
			if(side == 1 && world.isAirBlock(x, y+1, z))
				return true;
			else if(side == 2 && (TFCBlocks.isBlockVSupport(world.getBlock(x, y-1, z-1)) || world.getBlock(x, y-1, z-1).isOpaqueCube()) && world.isAirBlock(x, y, z-1))
				return true;
			else if(side == 3 && (TFCBlocks.isBlockVSupport(world.getBlock(x, y-1, z+1)) || world.getBlock(x, y-1, z+1).isOpaqueCube()) && world.isAirBlock(x, y, z+1))
				return true;
			else if(side == 4 && (TFCBlocks.isBlockVSupport(world.getBlock(x-1, y-1, z)) || world.getBlock(x-1, y-1, z).isOpaqueCube()) && world.isAirBlock(x-1, y, z))
				return true;
			else if(side == 5 && (TFCBlocks.isBlockVSupport(world.getBlock(x+1, y-1, z)) || world.getBlock(x+1, y-1, z).isOpaqueCube()) && world.isAirBlock(x+1, y, z))
				return true;
		}

		return false;
	}
}

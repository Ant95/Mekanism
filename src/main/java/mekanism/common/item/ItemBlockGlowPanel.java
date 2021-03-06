package mekanism.common.item;

import java.util.ArrayList;

import mcmultipart.api.multipart.IMultipart;
import mekanism.api.Coord4D;
import mekanism.api.EnumColor;
import mekanism.api.Range4D;
import mekanism.common.Mekanism;
import mekanism.common.base.TileNetworkList;
import mekanism.common.integration.MekanismHooks;
import mekanism.common.integration.multipart.MultipartMekanism;
import mekanism.common.network.PacketTileEntity.TileEntityMessage;
import mekanism.common.tile.TileEntityGlowPanel;
import mekanism.common.util.LangUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;

public class ItemBlockGlowPanel extends ItemBlockMultipartAble
{
	public Block metaBlock;
	
	public ItemBlockGlowPanel(Block block)
	{
		super(block);
		metaBlock = block;
		setHasSubtypes(true);
		setCreativeTab(Mekanism.tabMekanism);
	}
	
	@Override
	public int getMetadata(int i)
	{
		return i;
	}
	
	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState state)
	{
		boolean place = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, state);

		if(place)
		{
			TileEntityGlowPanel tileEntity = (TileEntityGlowPanel)world.getTileEntity(pos);
			EnumColor col = EnumColor.DYES[stack.getItemDamage()];
			
			BlockPos pos1 = pos.offset(side.getOpposite());
			
			if(world.isSideSolid(pos1, side))
			{
				tileEntity.setOrientation(side.getOpposite());
			}
			
			tileEntity.setColour(col);
			
			if(!world.isRemote)
			{
				Mekanism.packetHandler.sendToReceivers(new TileEntityMessage(Coord4D.get(tileEntity), tileEntity.getNetworkedData(new TileNetworkList())), new Range4D(Coord4D.get(tileEntity)));
			}
		}

		return place;
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> listToAddTo)
	{
		if(!isInCreativeTab(tab)) return;
		for(EnumColor color : EnumColor.DYES)
		{
			listToAddTo.add(new ItemStack(this, 1, color.getMetaValue()));
		}
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack)
	{
		EnumColor colour = EnumColor.DYES[stack.getItemDamage()];
		String colourName;

        if(I18n.canTranslate(getUnlocalizedName(stack) + "." + colour.dyeName))
        {
            return LangUtils.localize(getUnlocalizedName(stack) + "." + colour.dyeName);
        }
		
		if(colour == EnumColor.BLACK)
		{
			colourName = EnumColor.DARK_GREY + colour.getDyeName();
		}
		else {
			colourName = colour.getDyedName();
		}

		return colourName + " " + super.getItemStackDisplayName(stack);
	}

	@Override
	public boolean shouldRotateAroundWhenRendering()
	{
		return true;
	}

	@Override
	@Optional.Method(modid = MekanismHooks.MCMULTIPART_MOD_ID)
	protected IMultipart getMultiPart()
	{
		return MultipartMekanism.GLOWPANEL_MP;
	}
}

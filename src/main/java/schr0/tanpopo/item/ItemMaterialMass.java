package schr0.tanpopo.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import schr0.tanpopo.init.TanpopoItems;
import schr0.tanpopo.init.TanpopoPacket;
import schr0.tanpopo.packet.MessageParticleBlock;

public class ItemMaterialMass extends Item
{

	private static final int META_MAX = TanpopoItems.META_MATERIAL_MASS;

	public ItemMaterialMass()
	{
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems)
	{
		for (int meta = 0; meta <= META_MAX; meta++)
		{
			subItems.add(new ItemStack(itemIn, 1, meta));
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		return super.getUnlocalizedName() + "_" + stack.getMetadata();
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if (playerIn.canPlayerEdit(pos.offset(facing), facing, stack) && this.applyEssenceAround(stack, worldIn, pos, playerIn))
		{
			return EnumActionResult.SUCCESS;
		}

		return EnumActionResult.FAIL;
	}

	// TODO /* ======================================== MOD START =====================================*/

	public static boolean isDry(ItemStack stack)
	{
		return (stack != null) && (stack.getItem() == TanpopoItems.MATERIAL_MASS) && (stack.getItemDamage() == 0);
	}

	public static boolean isWet(ItemStack stack)
	{
		return (stack != null) && (stack.getItem() == TanpopoItems.MATERIAL_MASS) && (stack.getItemDamage() == 1);
	}

	private boolean applyEssenceAround(ItemStack stack, World world, BlockPos pos, EntityPlayer player)
	{
		if (isDry(stack))
		{
			return false;
		}

		int stackSizeSrc = stack.stackSize;

		if (ItemDye.applyBonemeal(stack, world, pos, player))
		{
			for (BlockPos posAround : BlockPos.getAllInBox(pos.add(-1, 0, -1), pos.add(1, 0, 1)))
			{
				if (posAround.equals(pos))
				{
					this.spawnApplyEssenceParticles(world, pos);

					continue;
				}

				if (ItemDye.applyBonemeal(stack, world, posAround, player))
				{
					this.spawnApplyEssenceParticles(world, posAround);
				}
				else
				{
					for (BlockPos posAroundUpDown : BlockPos.getAllInBox(posAround.add(0, -1, 0), posAround.add(0, 1, 0)))
					{
						if (posAroundUpDown.equals(posAround))
						{
							continue;
						}

						if (ItemDye.applyBonemeal(stack, world, posAroundUpDown, player))
						{
							this.spawnApplyEssenceParticles(world, posAroundUpDown);
						}
					}
				}
			}

			stack.stackSize = (stackSizeSrc - 1);

			world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 1.0F, 1.0F);

			return true;
		}

		return false;
	}

	private void spawnApplyEssenceParticles(World world, BlockPos pos)
	{
		for (int count = 0; count < 10; ++count)
		{
			TanpopoPacket.DISPATCHER.sendToAll(new MessageParticleBlock(0, pos.getX(), pos.getY(), pos.getZ()));
		}
	}

}

package cubex2.mods.chesttransporter;

import java.util.List;

import net.minecraft.src.Block;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityMinecart;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ModLoader;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.Potion;
import net.minecraft.src.PotionEffect;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;

public class ItemChestTransporter extends Item {

	protected ItemChestTransporter(int id) {
		super(id);
		setItemName("chesttransporter");
		setTextureFile("/cubex2/mods/chesttransporter/client/textures/textures.png");
		setMaxStackSize(1);
		setIconIndex(0);
		setHasSubtypes(true);
		setMaxDamage(0);
		setCreativeTab(CreativeTabs.tabTools);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if (!containsItems(stack) && isSupportedChest(world, x, y, z)) {
			TileEntity te = world.getBlockTileEntity(x, y, z);
			if (te != null && te instanceof IInventory) {
				IInventory chest = (IInventory) te;
				copyStacksIntoItemStack(stack, chest);
				Block block = Block.blocksList[world.getBlockId(x, y, z)];
				stack.setItemDamage(getDamage(block, world.getBlockMetadata(x, y, z)));
				world.setBlockAndMetadataWithNotify(x, y, z, 0, 0);				
				writeChestType(block, stack);
				world.playSoundEffect((x + 0.5F), (y + 0.5F), (z + 0.5F), block.stepSound.getStepSound(), (block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getPitch() * 0.5F);
				return false;
			}
		}
		else {
			if (containsItems(stack)) {
				Object placeing = placeChestBlock(getContainingChestStack(stack), player, world, x, y, z, side, hitX, hitY, hitZ);
				if (placeing != null) {
					int[] positions = (int[]) placeing;
					TileEntity te = world.getBlockTileEntity(positions[0], positions[1], positions[2]);
					if (te != null && te instanceof IInventory) {
						IInventory chest = (IInventory) te;
						copyStacksIntoInventory(chest, stack);
						player.renderBrokenItemStack(stack);
						player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
						return false;
					}
				}
			}
		}
		return false;
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean par5) {
		if (stack.getItemDamage() != 0 && entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			if (player.capabilities.isCreativeMode)
				return;
			if (player.getActivePotionEffect(Potion.moveSlowdown) == null || player.getActivePotionEffect(Potion.moveSlowdown).getDuration() < 20) {
				player.addPotionEffect(new PotionEffect(Potion.moveSlowdown.getId(), 20 * 3, 2));
			}
			if (player.getActivePotionEffect(Potion.digSlowdown) == null || player.getActivePotionEffect(Potion.digSlowdown).getDuration() < 20) {
				player.addPotionEffect(new PotionEffect(Potion.digSlowdown.getId(), 20 * 3, 3));
			}
			if (player.getActivePotionEffect(Potion.jump) == null || player.getActivePotionEffect(Potion.jump).getDuration() < 20) {
				player.addPotionEffect(new PotionEffect(Potion.jump.getId(), 20 * 3, -2));
			}
			if (player.getActivePotionEffect(Potion.hunger) == null || player.getActivePotionEffect(Potion.hunger).getDuration() < 20) {
				player.addPotionEffect(new PotionEffect(Potion.hunger.getId(), 20 * 3, 0));
			}
		}

	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int facing, float hitX, float hitY, float hitZ) {

		return false;
	}

	private Object placeChestBlock(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int facing, float hitX, float hitY, float hitZ) {
		int var11 = world.getBlockId(x, y, z);

		if (var11 == Block.snow.blockID) {
			facing = 1;
		}
		else if (var11 != Block.vine.blockID && var11 != Block.tallGrass.blockID && var11 != Block.deadBush.blockID && (Block.blocksList[var11] == null || !Block.blocksList[var11].isBlockReplaceable(world, x, y, z))) {
			if (facing == 0) {
				--y;
			}

			if (facing == 1) {
				++y;
			}

			if (facing == 2) {
				--z;
			}

			if (facing == 3) {
				++z;
			}

			if (facing == 4) {
				--x;
			}

			if (facing == 5) {
				++x;
			}
		}

		if (stack.stackSize == 0) {
			return null;
		}
		else if (!player.canPlayerEdit(x, y, z, facing, stack)) {
			return null;
		}
		else if (y == 255) {
			return null;
		}
		else if (world.canPlaceEntityOnSide(Block.chest.blockID, x, y, z, false, facing, player)) {
			Block block = Block.blocksList[stack.itemID];

			if (placeBlockAt(stack, player, world, x, y, z, facing, hitX, hitY, hitZ)) {
				world.playSoundEffect((x + 0.5F), (y + 0.5F), (z + 0.5F), block.stepSound.getStepSound(), (block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getPitch() * 0.8F);
				--stack.stackSize;
			}

			return new int[] { x, y, z };
		}
		else {
			return null;
		}
	}

	private boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if (!world.setBlockAndMetadataWithNotify(x, y, z, stack.itemID, stack.getItemDamage())) {
			return false;
		}

		if (world.getBlockId(x, y, z) == stack.itemID) {
			Block.blocksList[stack.itemID].onBlockPlacedBy(world, x, y, z, player);
			Block.blocksList[stack.itemID].func_85105_g(world, x, y, z, stack.getItemDamage());
		}

		return true;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		return stack;
	}

	@ForgeSubscribe
	public void mincartInteract(MinecartInteractEvent event) {
		ItemStack stack = event.player.getCurrentEquippedItem();
		if(stack == null || (containsItems(stack) && readChestType(stack) != Block.chest))
			return;
		if (stack.getItem() == this) {
			EntityMinecart minecart = event.minecart;
			if (!containsItems(stack) && minecart != null && minecart.isStorageCart()) {
				copyStacksIntoItemStack(stack, minecart);
				stack.setItemDamage(1);
				minecart.worldObj.playSoundEffect(((float) minecart.posX + 0.5F), ((float) minecart.posY + 0.5F), ((float) minecart.posZ + 0.5F), Block.chest.stepSound.getStepSound(), (Block.chest.stepSound.getVolume() + 1.0F) / 2.0F, Block.chest.stepSound.getPitch() * 0.5F);
				ModLoader.setPrivateValue(EntityMinecart.class, minecart, 3, 0);
			}
			else if (containsItems(stack) && minecart != null && minecart.getMinecartType() == 0 && minecart.riddenByEntity == null) {
				ModLoader.setPrivateValue(EntityMinecart.class, minecart, 3, 1);
				copyStacksIntoInventory(minecart, stack);
				minecart.worldObj.playSoundEffect(((float) minecart.posX + 0.5F), ((float) minecart.posY + 0.5F), ((float) minecart.posZ + 0.5F), Block.chest.stepSound.getStepSound(), (Block.chest.stepSound.getVolume() + 1.0F) / 2.0F, Block.chest.stepSound.getPitch() * 0.8F);
				event.player.renderBrokenItemStack(stack);
				event.player.inventory.setInventorySlotContents(event.player.inventory.currentItem, null);
			}
			event.setCanceled(true);
		}
	}

	private void copyStacksIntoItemStack(ItemStack stack, IInventory inventory) {
		if (containsItems(stack))
			return;
		if (stack.stackTagCompound == null)
			stack.setTagCompound(new NBTTagCompound());
		NBTTagList nbtList = new NBTTagList();

		for (int i = 0; i < inventory.getSizeInventory(); ++i) {
			if (inventory.getStackInSlot(i) != null) {
				NBTTagCompound nbtTabCompound2 = new NBTTagCompound();
				nbtTabCompound2.setByte("Slot", (byte) i);
				inventory.getStackInSlot(i).copy().writeToNBT(nbtTabCompound2);
				inventory.setInventorySlotContents(i, null);
				nbtList.appendTag(nbtTabCompound2);
			}
		}
		stack.stackTagCompound.setTag("Items", nbtList);
	}

	private void copyStacksIntoInventory(IInventory inventory, ItemStack stack) {
		if (!containsItems(stack))
			return;
		NBTTagList nbtList = stack.stackTagCompound.getTagList("Items");

		for (int i = 0; i < nbtList.tagCount(); ++i) {
			NBTTagCompound nbtTagCompound = (NBTTagCompound) nbtList.tagAt(i);
			int j = nbtTagCompound.getByte("Slot") & 255;

			if (j >= 0 && j < inventory.getSizeInventory()) {
				inventory.setInventorySlotContents(j, ItemStack.loadItemStackFromNBT(nbtTagCompound).copy());
			}
		}

		stack.stackTagCompound.getTags().remove(nbtList);
	}

	private void writeChestType(Block block, ItemStack stack) {
		if (stack.stackTagCompound == null)
			stack.stackTagCompound = new NBTTagCompound();
		int chestType;
		if (block == Block.chest)
			chestType = 1;
		else
			// IronChest
			chestType = 2;
		stack.stackTagCompound.setByte("ChestType", (byte) chestType);
	}

	private Block readChestType(ItemStack stack) {
		int chestType = stack.stackTagCompound.getByte("ChestType");
		return chestType == 1 ? Block.chest : ChestTransporter.ironChestBlock;
	}

	@Override
	public boolean getShareTag() {
		return true;
	}

	@Override
	public int getIconFromDamage(int i) {
		return this.iconIndex + i;
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean flag) {
		if (containsItems(stack)) {
			int numItems = 0;
			NBTTagList nbtList = stack.stackTagCompound.getTagList("Items");

			for (int i = 0; i < nbtList.tagCount(); ++i) {
				NBTTagCompound nbtTagCompound = (NBTTagCompound) nbtList.tagAt(i);
				int j = nbtTagCompound.getByte("Slot") & 255;

				if (j >= 0) {
					ItemStack itemstack = ItemStack.loadItemStackFromNBT(nbtTagCompound).copy();
					if (itemstack != null) {
						numItems += itemstack.stackSize;
					}
				}
			}

			list.add("Contains " + numItems + " items");
		}
	}

	private boolean containsItems(ItemStack stack) {
		return stack.stackTagCompound != null && stack.stackTagCompound.hasKey("Items");
	}

	private boolean isSupportedChest(World world, int x, int y, int z) {
		int id = world.getBlockId(x, y, z);
		return id == Block.chest.blockID || (ChestTransporter.ironChestBlock != null && id == ChestTransporter.ironChestBlock.blockID);
	}

	private ItemStack getContainingChestStack(ItemStack transporterStack) {
		if (!containsItems(transporterStack))
			return null;
		Block block = readChestType(transporterStack);
		if (block == Block.chest)
			return new ItemStack(block);
		if (block == ChestTransporter.ironChestBlock)
			return new ItemStack(block, 1, transporterStack.getItemDamage() - 2);
		return null;
	}
	
	private int getDamage(Block block, int metadata)
	{
		if(block == Block.chest)
			return 1;
		if(block == ChestTransporter.ironChestBlock)
			return metadata + 2;
		return 0;
	}
}

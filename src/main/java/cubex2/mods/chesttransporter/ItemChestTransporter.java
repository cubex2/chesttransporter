package cubex2.mods.chesttransporter;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

public class ItemChestTransporter extends Item
{

    protected ItemChestTransporter(int maxDamage, String iconName)
    {
        super();
        setUnlocalizedName("chesttransporter_" + iconName);
        setMaxStackSize(1);
        setMaxDamage(maxDamage);
        setCreativeTab(CreativeTabs.tabTools);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        if (event.action == Action.RIGHT_CLICK_BLOCK)
        {
            ItemStack stack = event.entityPlayer.getCurrentEquippedItem();
            if (stack == null || stack.getItem() != this)
                return;
            EntityPlayer player = event.entityPlayer;

            World world = event.entityPlayer.worldObj;
            EnumFacing face = event.face;

            int chestType = getTagCompound(stack).getByte("ChestType");

            if (chestType == 0 && isChestAt(world, event.pos))
            {
                grabChest(stack, world, event.pos);
            } else if (chestType != 0)
            {
                placeChest(stack, player, world, event.pos, face);
            }
        }
    }

    private void grabChest(ItemStack stack, World world, BlockPos pos)
    {
        TileEntity tile = world.getTileEntity(pos);
        IInventory chest = (IInventory) tile;
        if (chest != null)
        {
            IBlockState iblockstate = world.getBlockState(pos);
            Block chestBlock = iblockstate.getBlock();
            int metadata = chestBlock.getMetaFromState(iblockstate);
            int newChestType = getChestType(chestBlock, metadata);
            if (newChestType == 0)
                return;

            TransportableChest tChest = ChestRegistry.getChest(chestBlock, metadata);
            if (tChest == null) return;

            getTagCompound(stack).setByte("ChestType", (byte) newChestType);
            if (tChest.copyTileEntity())
            {
                NBTTagCompound nbt = new NBTTagCompound();
                tile.writeToNBT(nbt);
                getTagCompound(stack).setTag("ChestTile", nbt);
                world.removeTileEntity(pos);
            } else
            {
                moveItemsIntoStack(chest, stack);
            }

            tChest.preRemoveChest(stack, tile);

            world.setBlockToAir(pos);
            world.playSoundEffect(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, chestBlock.stepSound.getBreakSound(), (chestBlock.stepSound.getVolume() + 1.0F) / 2.0F, chestBlock.stepSound.getFrequency() * 0.5F);
        }
    }

    private void placeChest(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing face)
    {
        int chestType = getTagCompound(stack).getByte("ChestType");
        if (!ChestRegistry.dvToChest.containsKey(chestType))
            return;

        BlockPos chestPos = getChestCoords(world, pos, face);

        ItemStack chestStack = getStackFromDamage(chestType);
        if (!chestStack.onItemUse(player, world, pos, face, 0.0f, 0.0f, 0.0f))
            return;

        if (chestPos != null)
        {
            IBlockState iblockstate = world.getBlockState(chestPos);
            Block block = iblockstate.getBlock();
            int meta = block.getMetaFromState(iblockstate);

            TransportableChest tChest = ChestRegistry.getChest(block, meta);
            if (tChest == null) return;

            TileEntity tile = world.getTileEntity(chestPos);
            IInventory chest = (IInventory) tile;
            if (tChest.copyTileEntity())
            {
                NBTTagCompound nbt = getTagCompound(stack).getCompoundTag("ChestTile");
                tChest.modifyTileCompound(player, nbt);
                world.setTileEntity(chestPos, TileEntity.createAndLoadEntity(nbt));
            } else
            {
                moveItemsIntoChest(stack, chest);
            }
            getTagCompound(stack).setByte("ChestType", (byte) 0);

            tChest.preDestroyTransporter(player, stack, tile);

            if (!player.capabilities.isCreativeMode)
            {
                stack.damageItem(1, player);
                if (this == ChestTransporter.chestTransporter)
                    stack.damageItem(1, player);
            }
        }
    }

    private BlockPos getChestCoords(World world, BlockPos pos, EnumFacing facing)
    {
        IBlockState iblockstate = world.getBlockState(pos);
        Block block = iblockstate.getBlock();

        if (block == Blocks.snow_layer && iblockstate.getValue(BlockSnow.LAYERS) < 1)
        {
            // do nothing
        } else if (!block.isReplaceable(world, pos))
        {
            pos = pos.offset(facing);
        }

        return pos;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean par5)
    {
        int chestType = getTagCompound(stack).getByte("ChestType");

        if (chestType != 0 && entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) entity;
            if (player.capabilities.isCreativeMode)
                return;
            if (player.getActivePotionEffect(Potion.moveSlowdown) == null || player.getActivePotionEffect(Potion.moveSlowdown).getDuration() < 20)
            {
                player.addPotionEffect(new PotionEffect(Potion.moveSlowdown.getId(), 20 * 3, 2));
            }
            if (player.getActivePotionEffect(Potion.digSlowdown) == null || player.getActivePotionEffect(Potion.digSlowdown).getDuration() < 20)
            {
                player.addPotionEffect(new PotionEffect(Potion.digSlowdown.getId(), 20 * 3, 3));
            }
            if (player.getActivePotionEffect(Potion.jump) == null || player.getActivePotionEffect(Potion.jump).getDuration() < 20)
            {
                player.addPotionEffect(new PotionEffect(Potion.jump.getId(), 20 * 3, -2));
            }
            if (player.getActivePotionEffect(Potion.hunger) == null || player.getActivePotionEffect(Potion.hunger).getDuration() < 20)
            {
                player.addPotionEffect(new PotionEffect(Potion.hunger.getId(), 20 * 3, 0));
            }
        }

    }

    @SubscribeEvent
    public void entityInteract(EntityInteractEvent event)
    {
        if (!(event.target instanceof EntityMinecart))
            return;

        ItemStack stack = event.entityPlayer.getCurrentEquippedItem();
        EntityMinecart minecart = (EntityMinecart) event.target;
        if (stack == null || stack.getItem() != this || minecart == null)
            return;

        int chestType = getTagCompound(stack).getByte("ChestType");
        EntityPlayer player = event.entityPlayer;

        if (minecart instanceof EntityMinecartEmpty && minecart.riddenByEntity == null && ChestRegistry.isMinecartChest(chestType))
        {
            // put chest into minecart
            EntityMinecart newMinecart = ChestRegistry.createMinecart(minecart.worldObj, chestType);
            if (!player.worldObj.isRemote)
            {
                replaceMinecart(minecart, newMinecart);
            }
            moveItemsIntoChest(stack, (IInventory) newMinecart);
            getTagCompound(stack).setByte("ChestType", (byte) 0);
            minecart.worldObj.playSoundEffect((float) minecart.posX + 0.5F, (float) minecart.posY + 0.5F, (float) minecart.posZ + 0.5F, Blocks.chest.stepSound.getBreakSound(), (Blocks.chest.stepSound.getVolume() + 1.0F) / 2.0F, Blocks.chest.stepSound.getFrequency() * 0.8F);
            if (!player.capabilities.isCreativeMode)
            {
                stack.damageItem(1, player);
                if (this == ChestTransporter.chestTransporter)
                    stack.damageItem(1, player);
            }

            event.setCanceled(true);
        } else if (ChestRegistry.isSupportedMinecart(minecart) && chestType == 0)
        {
            // grab chest from minecart
            moveItemsIntoStack((IInventory) minecart, stack);
            getTagCompound(stack).setByte("ChestType", (byte) ChestRegistry.getChestType(minecart));
            minecart.worldObj.playSoundEffect((float) minecart.posX + 0.5F, (float) minecart.posY + 0.5F, (float) minecart.posZ + 0.5F, Blocks.chest.stepSound.getBreakSound(), (Blocks.chest.stepSound.getVolume() + 1.0F) / 2.0F, Blocks.chest.stepSound.getFrequency() * 0.5F);
            if (!player.worldObj.isRemote)
            {
                EntityMinecartEmpty newMinecart = new EntityMinecartEmpty(minecart.worldObj);
                replaceMinecart(minecart, newMinecart);
            }

            event.setCanceled(true);
        }
    }

    private void replaceMinecart(EntityMinecart old, EntityMinecart now)
    {
        now.setPosition(old.posX, old.posY, old.posZ);
        now.setAngles(old.rotationPitch, old.rotationYaw);
        old.worldObj.spawnEntityInWorld(now);
        old.setDead();
    }

    @Override
    public boolean getShareTag()
    {
        return true;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag)
    {
        int chestType = getTagCompound(stack).getByte("ChestType");
        if (chestType != 0)
        {
            int numItems = 0;
            NBTTagList nbtList = stack.getTagCompound().getTagList("Items", 10);

            for (int i = 0; i < nbtList.tagCount(); ++i)
            {
                NBTTagCompound nbtTagCompound = nbtList.getCompoundTagAt(i);
                NBTBase nbt = nbtTagCompound.getTag("Slot");
                int j;
                if (nbt instanceof NBTTagByte)
                {
                    j = nbtTagCompound.getByte("Slot") & 255;
                } else
                {
                    j = nbtTagCompound.getShort("Slot");
                }

                if (j >= 0)
                {
                    ItemStack itemstack = ItemStack.loadItemStackFromNBT(nbtTagCompound);
                    if (itemstack != null)
                    {
                        numItems += itemstack.stackSize;
                    }
                }
            }

            list.add("Contains " + numItems + " items");
        }
    }

    static boolean isChestAt(World world, BlockPos pos)
    {
        IBlockState iblockstate = world.getBlockState(pos);
        Block block = iblockstate.getBlock();

        int meta = block.getMetaFromState(iblockstate);
        return ChestRegistry.isChest(block, meta);
    }

    static int getChestType(Block block, int metadata)
    {
        TransportableChest chest = ChestRegistry.getChest(block, metadata);
        return chest != null ? chest.getTransporterDV() : 0;
    }

    private ItemStack getStackFromDamage(int damage)
    {
        return ChestRegistry.dvToChest.containsKey(damage) ? ChestRegistry.dvToChest.get(damage).createChestStack() : null;
    }

    static void moveItemsIntoStack(IInventory chest, ItemStack stack)
    {
        if (stack.getTagCompound() == null)
        {
            stack.setTagCompound(new NBTTagCompound());
        }
        NBTTagList nbtList = new NBTTagList();

        for (int i = 0; i < chest.getSizeInventory(); ++i)
        {
            if (chest.getStackInSlot(i) != null)
            {
                NBTTagCompound nbtTabCompound2 = new NBTTagCompound();
                nbtTabCompound2.setShort("Slot", (short) i);
                chest.getStackInSlot(i).copy().writeToNBT(nbtTabCompound2);
                chest.setInventorySlotContents(i, null);
                nbtList.appendTag(nbtTabCompound2);
            }
        }
        stack.getTagCompound().setTag("Items", nbtList);
    }

    private void moveItemsIntoChest(ItemStack stack, IInventory chest)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagList nbtList = stack.getTagCompound().getTagList("Items", 10);

        for (int i = 0; i < nbtList.tagCount(); ++i)
        {
            NBTTagCompound nbtTagCompound = nbtList.getCompoundTagAt(i);
            NBTBase nbt = nbtTagCompound.getTag("Slot");
            int j;
            if (nbt instanceof NBTTagByte)
            {
                j = nbtTagCompound.getByte("Slot") & 255;
            } else
            {
                j = nbtTagCompound.getShort("Slot");
            }

            if (j >= 0 && j < chest.getSizeInventory())
            {
                chest.setInventorySlotContents(j, ItemStack.loadItemStackFromNBT(nbtTagCompound).copy());
            }
        }

        stack.getTagCompound().removeTag("Items");
    }

    public static NBTTagCompound getTagCompound(ItemStack stack)
    {
        if (stack.getTagCompound() == null)
            stack.setTagCompound(new NBTTagCompound());
        return stack.getTagCompound();
    }
}

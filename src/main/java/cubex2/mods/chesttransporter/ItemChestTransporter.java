package cubex2.mods.chesttransporter;

import cubex2.mods.chesttransporter.api.TransportableChest;
import cubex2.mods.chesttransporter.chests.ChestRegistry;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemChestTransporter extends Item
{
    public final TransporterType type;

    ItemChestTransporter(TransporterType type)
    {
        super();
        this.type = type;

        setUnlocalizedName("chesttransporter_" + type.iconName);
        setMaxStackSize(1);
        setMaxDamage(type.maxDamage);
        setCreativeTab(CreativeTabs.TOOLS);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent.RightClickBlock event)
    {
        if (event.isCanceled() || event.getFace() == null)
            return;

        ItemStack stack = event.getItemStack();
        EntityPlayer player = event.getEntityPlayer();
        World world = event.getEntityPlayer().world;
        EnumFacing face = event.getFace();
        BlockPos pos = event.getPos();

        if (stack.isEmpty() || stack.getItem() != this)
            return;

        IBlockState state = world.getBlockState(pos);

        if (hasChest(stack))
        {
            getChest(stack)
                    .ifPresent(chest -> placeChest(chest, stack, player, event.getHand(), world, pos, face));
        } else
        {
            getChest(world, pos, world.getBlockState(pos), player, stack)
                    .ifPresent(chest -> grabChest(chest, stack, player, world, pos));
        }
    }

    private void grabChest(TransportableChest chest, ItemStack stack, EntityPlayer player, World world, BlockPos pos)
    {
        TileEntity tile = world.getTileEntity(pos);
        if (tile != null)
        {
            IBlockState iblockstate = world.getBlockState(pos);
            Block chestBlock = iblockstate.getBlock();

            getTagCompound(stack).setString("ChestName", chest.getRegistryName().toString());
            if (chest.copyTileEntity())
            {
                NBTTagCompound nbt = new NBTTagCompound();
                tile.writeToNBT(nbt);
                getTagCompound(stack).setTag("ChestTile", nbt);
                world.removeTileEntity(pos);
            } else
            {
                IInventory inv = (IInventory) tile;
                moveItemsIntoStack(inv, stack);
            }

            chest.preRemoveChest(world, pos, player, stack);

            world.setBlockToAir(pos);
            SoundType soundType = chestBlock.getSoundType();
            world.playSound(player, pos, soundType.getPlaceSound(), SoundCategory.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
        }
    }

    private void placeChest(TransportableChest chest, ItemStack stack, EntityPlayer player, EnumHand hand, World world, BlockPos pos, EnumFacing face)
    {
        BlockPos chestPos = getChestCoords(world, pos, face);

        if (!chest.canPlaceChest(world, chestPos, player, stack))
            return;

        ItemStack chestStack = chest.createChestStack(stack);
        if (chestStack.isEmpty()) return;

        player.setHeldItem(hand, chestStack);
        EnumActionResult result = chestStack.onItemUse(player, world, pos, hand, face, 0.0f, 0.0f, 0.0f);
        player.setHeldItem(hand, stack);
        if (result != EnumActionResult.SUCCESS)
        {
            return;
        }

        TileEntity tile = world.getTileEntity(chestPos);
        if (tile == null) return;

        if (chest.copyTileEntity())
        {
            NBTTagCompound nbt = getTagCompound(stack).getCompoundTag("ChestTile");
            nbt = chest.modifyTileCompound(nbt, world, pos, player, stack);
            world.setTileEntity(chestPos, TileEntity.create(world, nbt));
        } else
        {
            IInventory inv = (IInventory) tile;
            moveItemsIntoChest(stack, inv);
        }

        getTagCompound(stack).removeTag("ChestType");
        getTagCompound(stack).removeTag("ChestName");

        chest.onChestPlaced(world, pos, player, stack);

        damageItem(stack, player);
    }

    private BlockPos getChestCoords(World world, BlockPos pos, EnumFacing facing)
    {
        IBlockState iblockstate = world.getBlockState(pos);
        Block block = iblockstate.getBlock();

        if (block != Blocks.SNOW_LAYER || iblockstate.getValue(BlockSnow.LAYERS) >= 1)
        {
            if (!block.isReplaceable(world, pos))
            {
                pos = pos.offset(facing);
            }
        }

        return pos;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean par5)
    {
        if (hasChest(stack) && entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) entity;
            if (player.capabilities.isCreativeMode)
                return;
            addEffect(player, MobEffects.SLOWNESS, 2);
            addEffect(player, MobEffects.MINING_FATIGUE, 3);
            addEffect(player, MobEffects.JUMP_BOOST, -2);
            addEffect(player, MobEffects.HUNGER, 0);
        }
    }

    private void addEffect(EntityPlayer player, Potion potion, int amplifier)
    {
        PotionEffect effect = player.getActivePotionEffect(potion);
        if (effect == null || effect.getDuration() < 20)
        {
            player.addPotionEffect(new PotionEffect(potion, 20 * 3, amplifier));
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void entityInteract(MinecartInteractEvent event)
    {
        if (event.isCanceled())
            return;

        ItemStack stack = event.getItem();
        EntityMinecart minecart = event.getMinecart();
        if (stack.isEmpty() || stack.getItem() != this || minecart == null)
            return;

        Optional<TransportableChest> chest = getChest(stack);
        EntityPlayer player = event.getPlayer();

        if (minecart instanceof EntityMinecartEmpty && !minecart.isBeingRidden() && chest.isPresent() && ChestRegistry.isMinecartChest(chest.get()))
        {
            // put chest into minecart
            EntityMinecart newMinecart = ChestRegistry.createMinecart(minecart.world, chest.get());
            if (newMinecart == null) return;

            if (!player.world.isRemote)
            {
                replaceMinecart(minecart, newMinecart);
            }
            moveItemsIntoChest(stack, (IInventory) newMinecart);
            getTagCompound(stack).removeTag("ChestName");
            getTagCompound(stack).removeTag("ChestType");
            SoundType soundType = Blocks.CHEST.getSoundType();
            minecart.world.playSound(player, minecart.getPosition(), soundType.getPlaceSound(), SoundCategory.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
            damageItem(stack, player);

            event.setCanceled(true);
        } else if (ChestRegistry.isSupportedMinecart(minecart) && !chest.isPresent())
        {
            // grab chest from minecart
            moveItemsIntoStack((IInventory) minecart, stack);
            getTagCompound(stack).setString("ChestName", ChestRegistry.getChestType(minecart).toString());
            SoundType soundType = Blocks.CHEST.getSoundType();
            minecart.world.playSound(player, minecart.getPosition(), soundType.getBreakSound(), SoundCategory.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
            if (!player.world.isRemote)
            {
                EntityMinecartEmpty newMinecart = new EntityMinecartEmpty(minecart.world);
                replaceMinecart(minecart, newMinecart);
            }

            event.setCanceled(true);
        }
    }

    private void damageItem(ItemStack stack, EntityPlayer player)
    {
        if (!player.capabilities.isCreativeMode)
        {
            stack.damageItem(1, player);
            if (type.maxDamage == 1)
                stack.damageItem(1, player);
        }
    }

    private void replaceMinecart(EntityMinecart old, EntityMinecart now)
    {
        now.setPositionAndRotation(old.posX, old.posY, old.posZ, old.rotationPitch, old.rotationYaw);
        old.world.spawnEntity(now);
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
        NBTTagCompound tagCompound = getTagCompound(stack);
        getChest(stack).ifPresent(chest ->
                                  {
                                      NonNullList<Pair<Integer, ItemStack>> items = Util.readItemsFromNBT(tagCompound);
                                      int numItems = 0;
                                      for (Pair<Integer, ItemStack> pair : items)
                                      {
                                          numItems += pair.getRight().getCount();
                                      }

                                      if (numItems > 0)
                                      {
                                          list.add("Contains " + numItems + " items");
                                      }
                                      list.add(chest.getRegistryName().toString());
                                      chest.addInformation(stack, player, list, flag);
                                  });
    }

    private static boolean hasChest(ItemStack stack)
    {
        if (!stack.hasTagCompound())
            return false;

        int chestType = stack.getTagCompound().getByte("ChestType");
        if (chestType != 0) return true;

        String chestName = stack.getTagCompound().getString("ChestName");
        return ChestRegistry.getChestFromType(new ResourceLocation(chestName)).isPresent();
    }

    private static Optional<TransportableChest> getChest(World world, BlockPos pos, IBlockState state, EntityPlayer player, ItemStack transporter)
    {
        for (TransportableChest chest : ChestRegistry.getChests())
        {
            if (chest.canGrabChest(world, pos, state, player, transporter))
            {
                return Optional.of(chest);
            }
        }

        return Optional.empty();
    }

    public static Optional<TransportableChest> getChest(ItemStack stack)
    {
        if (!stack.hasTagCompound())
            return Optional.empty();
        Optional<TransportableChest> chest = ChestRegistry.getChestFromType(stack.getTagCompound().getByte("ChestType"));
        if (chest.isPresent())
            return chest;
        return ChestRegistry.getChestFromType(new ResourceLocation(stack.getTagCompound().getString("ChestName")));
    }

    private static void moveItemsIntoStack(IInventory chest, ItemStack stack)
    {
        if (stack.getTagCompound() == null)
        {
            stack.setTagCompound(new NBTTagCompound());
        }
        NBTTagList nbtList = new NBTTagList();

        for (int i = 0; i < chest.getSizeInventory(); ++i)
        {
            if (!chest.getStackInSlot(i).isEmpty())
            {
                NBTTagCompound nbtTabCompound2 = new NBTTagCompound();
                nbtTabCompound2.setShort("Slot", (short) i);
                chest.getStackInSlot(i).copy().writeToNBT(nbtTabCompound2);
                chest.setInventorySlotContents(i, ItemStack.EMPTY);
                nbtList.appendTag(nbtTabCompound2);
            }
        }
        stack.getTagCompound().setTag("Items", nbtList);
    }

    private void moveItemsIntoChest(ItemStack stack, IInventory chest)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound nbtTag = getTagCompound(stack);
        NonNullList<Pair<Integer, ItemStack>> items = Util.readItemsFromNBT(nbtTag);
        for (Pair<Integer, ItemStack> pair : items)
        {
            if (pair.getLeft() < chest.getSizeInventory())
            {
                chest.setInventorySlotContents(pair.getLeft(), pair.getRight());
            }
        }

        nbtTag.removeTag("Items");
    }

    public static NBTTagCompound getTagCompound(ItemStack stack)
    {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null)
        {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }
        return nbt;
    }
}

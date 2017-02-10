package cubex2.mods.chesttransporter;

import cubex2.mods.chesttransporter.chests.ChestRegistry;
import cubex2.mods.chesttransporter.chests.TransportableChest;
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
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

public class ItemChestTransporter extends Item
{
    public final TransporterType type;

    protected ItemChestTransporter(TransporterType type)
    {
        super();
        this.type = type;

        setUnlocalizedName("chesttransporter_" + type.iconName);
        setMaxStackSize(1);
        setMaxDamage(type.maxDamage);
        setCreativeTab(CreativeTabs.TOOLS);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent.RightClickBlock event)
    {
        if (event.isCanceled())
            return;

        ItemStack stack = event.getItemStack();
        if (stack == null || stack.getItem() != this)
            return;
        EntityPlayer player = event.getEntityPlayer();

        World world = event.getEntityPlayer().worldObj;
        EnumFacing face = event.getFace();

        IBlockState state = world.getBlockState(event.getPos());
        System.out.println(state.getBlock().getUnlocalizedName() + "@" + state.getBlock().getMetaFromState(state));

        int chestType = getTagCompound(stack).getByte("ChestType");

        if (chestType == 0 && isChestAt(world, event.getPos()))
        {
            grabChest(stack, player, world, event.getPos());
        } else if (chestType != 0)
        {
            placeChest(stack, player, event.getHand(), world, event.getPos(), face);
        }
    }

    private void grabChest(ItemStack stack, EntityPlayer player, World world, BlockPos pos)
    {
        TileEntity tile = world.getTileEntity(pos);
        if (tile != null)
        {
            IBlockState iblockstate = world.getBlockState(pos);
            Block chestBlock = iblockstate.getBlock();
            int metadata = chestBlock.getMetaFromState(iblockstate);
            int newChestType = getChestType(chestBlock, metadata);
            if (newChestType == 0)
                return;

            TransportableChest tChest = ChestRegistry.getChest(chestBlock, metadata);
            if (tChest == null || !tChest.isUsableWith(stack)) return;

            getTagCompound(stack).setByte("ChestType", (byte) newChestType);
            if (tChest.copyTileEntity())
            {
                NBTTagCompound nbt = new NBTTagCompound();
                tile.writeToNBT(nbt);
                getTagCompound(stack).setTag("ChestTile", nbt);
                world.removeTileEntity(pos);
            } else
            {
                IInventory chest = (IInventory) tile;
                moveItemsIntoStack(chest, stack);
            }

            tChest.preRemoveChest(stack, tile);

            world.setBlockToAir(pos);
            SoundType soundType = chestBlock.getSoundType();
            world.playSound(player, pos, soundType.getPlaceSound(), SoundCategory.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
        }
    }

    private void placeChest(ItemStack stack, EntityPlayer player, EnumHand hand, World world, BlockPos pos, EnumFacing face)
    {
        int chestType = getTagCompound(stack).getByte("ChestType");
        if (!ChestRegistry.dvToChest.containsKey(chestType))
            return;

        BlockPos chestPos = getChestCoords(world, pos, face);

        ItemStack chestStack = createChestStack(stack, chestType);
        if (chestStack.onItemUse(player, world, pos, hand, face, 0.0f, 0.0f, 0.0f) != EnumActionResult.SUCCESS)
            return;

        if (chestPos != null)
        {
            IBlockState iblockstate = world.getBlockState(chestPos);
            Block block = iblockstate.getBlock();
            int meta = block.getMetaFromState(iblockstate);

            TransportableChest tChest = ChestRegistry.getChest(block, meta);
            if (tChest == null || !tChest.isUsableWith(stack)) return;

            TileEntity tile = world.getTileEntity(chestPos);
            if (tChest.copyTileEntity())
            {
                NBTTagCompound nbt = getTagCompound(stack).getCompoundTag("ChestTile");
                tChest.modifyTileCompound(player, nbt);
                world.setTileEntity(chestPos, TileEntity.func_190200_a(world, nbt));
            } else
            {
                IInventory chest = (IInventory) tile;
                moveItemsIntoChest(stack, chest);
            }
            getTagCompound(stack).setByte("ChestType", (byte) 0);

            tChest.preDestroyTransporter(player, stack, tile);

            if (!player.capabilities.isCreativeMode)
            {
                stack.damageItem(1, player);
                if (type.maxDamage == 1)
                    stack.damageItem(1, player);
            }
        }
    }

    private BlockPos getChestCoords(World world, BlockPos pos, EnumFacing facing)
    {
        IBlockState iblockstate = world.getBlockState(pos);
        Block block = iblockstate.getBlock();

        if (block == Blocks.SNOW_LAYER && iblockstate.getValue(BlockSnow.LAYERS) < 1)
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
            addEffect(player, MobEffects.SLOWNESS, 2);
            addEffect(player, MobEffects.MINING_FATIGUE, 3);
            addEffect(player, MobEffects.JUMP_BOOST, -2);
            addEffect(player, MobEffects.HUNGER, 0);
        }

    }

    private void addEffect(EntityPlayer player, Potion potion, int amplifier)
    {
        if (player.getActivePotionEffect(potion) == null || player.getActivePotionEffect(potion).getDuration() < 20)
        {
            player.addPotionEffect(new PotionEffect(potion, 20 * 3, amplifier));
        }
    }

    @SubscribeEvent
    public void entityInteract(MinecartInteractEvent event)
    {
        if (event.isCanceled())
            return;

        ItemStack stack = event.getItem();
        EntityMinecart minecart = event.getMinecart();
        if (stack == null || stack.getItem() != this || minecart == null)
            return;

        int chestType = getTagCompound(stack).getByte("ChestType");
        EntityPlayer player = event.getPlayer();

        if (minecart instanceof EntityMinecartEmpty && !minecart.isBeingRidden() && ChestRegistry.isMinecartChest(chestType))
        {
            // put chest into minecart
            EntityMinecart newMinecart = ChestRegistry.createMinecart(minecart.worldObj, chestType);
            if (!player.worldObj.isRemote)
            {
                replaceMinecart(minecart, newMinecart);
            }
            moveItemsIntoChest(stack, (IInventory) newMinecart);
            getTagCompound(stack).setByte("ChestType", (byte) 0);
            SoundType soundType = Blocks.CHEST.getSoundType();
            minecart.worldObj.playSound(player, minecart.getPosition(), soundType.getPlaceSound(), SoundCategory.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
            if (!player.capabilities.isCreativeMode)
            {
                stack.damageItem(1, player);
                if (type.maxDamage == 1)
                    stack.damageItem(1, player);
            }

            event.setCanceled(true);
        } else if (ChestRegistry.isSupportedMinecart(minecart) && chestType == 0)
        {
            // grab chest from minecart
            moveItemsIntoStack((IInventory) minecart, stack);
            getTagCompound(stack).setByte("ChestType", (byte) ChestRegistry.getChestType(minecart));
            SoundType soundType = Blocks.CHEST.getSoundType();
            minecart.worldObj.playSound(player, minecart.getPosition(), soundType.getBreakSound(), SoundCategory.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
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
        now.rotationPitch = old.rotationPitch;
        now.prevRotationPitch = old.prevRotationPitch;
        now.rotationYaw = old.rotationYaw;
        now.prevRotationYaw = old.prevRotationYaw;
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

    private ItemStack createChestStack(ItemStack transporter, int damage)
    {
        return ChestRegistry.dvToChest.containsKey(damage) ? ChestRegistry.dvToChest.get(damage).createChestStack(transporter) : null;
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

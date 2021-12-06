package emt.item;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import emt.EMT;
import emt.util.EMTConfigHandler;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import thaumcraft.api.IRunicArmor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ItemIC2Baubles extends ItemBase implements IBauble, IRunicArmor {

    public static int wornTick;
    public IIcon[] icon = new IIcon[16];
    public Random random = new Random();

    public ItemIC2Baubles() {
        super("bauble");
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setMaxStackSize(1);

        wornTick = 0;
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack) {
        String name = "";
        switch (itemstack.getItemDamage()) {
            case 0: {
                name = "euMaker.armor";
                break;
            }
            case 1: {
                name = "euMaker.inventory";
                break;
            }
            default:
                name = "nothing";
                break;
        }
        return getUnlocalizedName() + "." + name;
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister ri) {
        this.icon[0] = ri.registerIcon(EMT.TEXTURE_PATH + ":armoreumaker");
        this.icon[1] = ri.registerIcon(EMT.TEXTURE_PATH + ":inventoryeumaker");
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        return this.icon[meta];
    }

    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List list) {
        list.add(new ItemStack(this, 1, 0));
        list.add(new ItemStack(this, 1, 1));
    }

    @Override
    public BaubleType getBaubleType(ItemStack stack) {
        if (stack.getItemDamage() <= 1) {
            return BaubleType.RING;
        } else {
            return null;
        }
    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase entityLivingBase) {
        if (entityLivingBase.worldObj.isRemote || stack == null || !(entityLivingBase instanceof EntityPlayer))
            return;

        EntityPlayer player = (EntityPlayer) entityLivingBase;

        int energyLeft;
        ItemStack[] inventory;

        if (stack.getItemDamage() == 0) { // Armor energy regen
            energyLeft = EMTConfigHandler.armorBaubleProduction;
            inventory = player.inventory.armorInventory;
        } else if (stack.getItemDamage() == 1) { // Inventory energy regen
            energyLeft = EMTConfigHandler.inventoryBaubleProdution;
            inventory = player.inventory.mainInventory;
        } else if (stack.getItemDamage() == 2) { // Bauble energy regen
            energyLeft = 800000000;
            IInventory baubleInv = BaublesApi.getBaubles(player);
            inventory = new ItemStack[baubleInv.getSizeInventory()];
            for (int i = 0; i < baubleInv.getSizeInventory(); i++)
                inventory[i] = baubleInv.getStackInSlot(i);
        } else {
            return;
        }

        for (ItemStack itemStack : inventory) {
            if ((itemStack != null) && (itemStack.getItem() instanceof IElectricItem))
                energyLeft -= ElectricItem.manager.charge(
                        itemStack, energyLeft, 4, false, false);

            if (energyLeft <= 0)
                return;
        }
    }

    @Override
    public void onEquipped(ItemStack stack, EntityLivingBase player) {
    }

    @Override
    public void onUnequipped(ItemStack stack, EntityLivingBase player) {
    }

    @Override
    public boolean canEquip(ItemStack stack, EntityLivingBase player) {
        return true;
    }

    @Override
    public boolean canUnequip(ItemStack stack, EntityLivingBase player) {
        return true;
    }

    @Override
    public int getRunicCharge(ItemStack itemStack) {
        return 0;
    }
}

/**
 * 
 */
package cn.lambdacraft.deathmatch.item.weapon;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import cn.lambdacraft.api.hud.IHudTip;
import cn.lambdacraft.api.hud.IHudTipProvider;
import cn.lambdacraft.core.CBCMod;
import cn.lambdacraft.core.item.CBCGenericItem;
import cn.lambdacraft.deathmatch.entity.EntitySatchel;
import cn.weaponmod.api.WeaponHelper;
import cn.weaponmod.api.feature.IModdable;
import cn.weaponmod.api.feature.ISpecialUseable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Remote detonation bomb. Mode I : Setting mode. Mode II : Detonating mode.
 * 
 * @author WeAthFolD
 * 
 */
public class Weapon_Satchel extends CBCGenericItem implements IHudTipProvider, IModdable, ISpecialUseable {

	public IIcon iconSetting;

	public Weapon_Satchel() {
		super();
		setUnlocalizedName("weapon_satchel");
		setIconName("weapon_satchel");
		this.hasSubtypes = true;
		setCreativeTab(CBCMod.cct);
		setMaxStackSize(64);

	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister reg) {
		super.registerIcons(reg);
		iconSetting = reg.registerIcon("lambdacraft:weapon_satchel1");
	}

	@SideOnly(Side.CLIENT)
	@Override
	/**
	 * Gets an icon index based on an item's damage value
	 */
	public Icon getIconFromDamage(int par1) {
		return par1 == 0 ? this.itemIcon : this.iconSetting;
	}

	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World,
			Entity par3Entity, int par4, boolean par5) {
		if(getMode(par1ItemStack) == 0 && par5) {
			((EntityPlayer)par3Entity).isSwingInProgress = false;
		}
	}

	@Override
	public void onItemClick(World world, EntityPlayer player, ItemStack stack,
			boolean left) {
		if(!world.isRemote && left) {
			int mode = getMode(stack);
			NBTTagCompound nbt = player.getEntityData();
			int count = nbt.getInteger("satchelCount");
			// Max 6 satchel
			
			if (mode == 0) { // Setting mode
				
				if (count > 5)
					return;
				nbt.setBoolean("doesExplode", false);
				EntitySatchel ent = new EntitySatchel(world, player);
				world.spawnEntityInWorld(ent);
				nbt.setInteger("satchelCount", ++count);
				if (!player.capabilities.isCreativeMode) {
					if(--stack.stackSize == 0)
						player.destroyCurrentEquippedItem();
				}
				
			} else { // Detonating mode
				nbt.setBoolean("doesExplode", true);
				nbt.setInteger("satchelCount", 0);
			}
			
		}
	}
	
	@Override
	public void onModeChange(ItemStack item, EntityPlayer player, int newMode) {
		item.setItemDamage(newMode);
	}

	@Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack) {
		return 100;
	}

	@Override
	public String getModeDescription(int mode) {
		return mode == 0 ? "mode.satchel2" : "mode.satchel1";
	}
	
    @Override
	public boolean onBlockStartBreak(ItemStack itemstack, int i, int j, int k, EntityPlayer player)
    {
    	return !player.capabilities.isCreativeMode;
    }
    
    @Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
    	return true;
    }

	@Override
	@SideOnly(Side.CLIENT)
	public IHudTip[] getHudTip(ItemStack itemStack, EntityPlayer player) {
		IHudTip[] tips = new IHudTip[1];
		tips[0] = new IHudTip() {

			@Override
			public Icon getRenderingIcon(ItemStack itemStack,
					EntityPlayer player) {
				return Weapon_Satchel.this.getIconFromDamage(itemStack.getItemDamage());
			}

			@Override
			public String getTip(ItemStack itemStack, EntityPlayer player) {
				return String.valueOf(WeaponHelper.getAmmoCapacity(itemID, player.inventory));
			}

			@Override
			public int getTextureSheet(ItemStack itemStack) {
				return itemStack.getItemSpriteNumber();
			}
			
		};
		return tips;
	}

	@Override
	public int getMode(ItemStack item) {
		return item.getItemDamage();
	}

	@Override
	public int getMaxModes() {
		return 2;
	}

	@Override
	public void onItemUsingTick(World world, EntityPlayer player,
			ItemStack stack, boolean type, int tickLeft) {}
	

}

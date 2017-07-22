package cam72cam.immersiverailroading.items;

import java.util.List;

import javax.annotation.Nullable;

import cam72cam.immersiverailroading.Config;
import cam72cam.immersiverailroading.ImmersiveRailroading;
import cam72cam.immersiverailroading.entity.registry.DefinitionManager;
import cam72cam.immersiverailroading.entity.registry.DefinitionRollingStock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemRollingStock extends Item {
	public static final String NAME = "item_rolling_stock";
	
	public ItemRollingStock() {
		super();
		setUnlocalizedName(ImmersiveRailroading.MODID + ":" + NAME);
        setRegistryName(new ResourceLocation(ImmersiveRailroading.MODID, NAME));
        this.setCreativeTab(CreativeTabs.TRANSPORTATION);
	}
	
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        if (this.isInCreativeTab(tab))
        {
        	for (String defID : DefinitionManager.getDefinitionNames()) {
        		ItemStack stack = new ItemStack(this);
				stack.setTagCompound(nbtFromDef(defID));
                items.add(stack);	
        	}
        }
    }
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName(stack) + "." + defFromStack(stack);
	}
	
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(DefinitionManager.getDefinition(defFromStack(stack)).getTooltip());
    }
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {
			ItemStack stack = player.getHeldItem(hand);
			DefinitionRollingStock def = DefinitionManager.getDefinition(defFromStack(stack));
			def.spawn(worldIn, pos.add(0, 0.6, 0), EnumFacing.fromAngle(player.rotationYawHead));
			System.out.println("SPAWNED SHAY");
		}
		return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
	}
	
	@Override
	public boolean isValidArmor(ItemStack stack, EntityEquipmentSlot armorType, Entity entity) {
		return armorType == EntityEquipmentSlot.HEAD && Config.trainsOnTheBrain;
	}
	
	public static String defFromStack(ItemStack stack) {
		if (stack.getTagCompound() != null){
			return stack.getTagCompound().getString("defID");
		}
		stack.setCount(0);
		return "BUG";
	}
	public static NBTTagCompound nbtFromDef(String defID) {
		NBTTagCompound val = new NBTTagCompound();
		val.setString("defID", defID);
		return val;
	}
}

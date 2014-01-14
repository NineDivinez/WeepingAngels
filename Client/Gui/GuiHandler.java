package WeepingAngels.Client.Gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import WeepingAngels.Inventory.ContainerVortex;
import WeepingAngels.Inventory.InventoryVortex;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		TileEntity tileEnt = world.getBlockTileEntity(x, y, z);
		return null;
	}

	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		TileEntity tileEnt = world.getBlockTileEntity(x, y, z);
		if (ID == 0)
			return new GuiVortex(player);
		return null;
	}
}
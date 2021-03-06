package com.countrygamer.weepingangels.client.gui

import com.countrygamer.cgo.client.gui.GuiScreenWrapper
import com.countrygamer.cgo.common.network.PacketHandler
import com.countrygamer.weepingangels.common.WeepingAngels
import com.countrygamer.weepingangels.common.entity.EntityWeepingAngel
import com.countrygamer.weepingangels.common.network.PacketModifyStatue
import com.countrygamer.weepingangels.common.tile.TileEntityStatue
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.client.gui.{GuiButton, GuiTextField}
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.{OpenGlHelper, RenderHelper}
import net.minecraft.entity.EntityLivingBase
import org.lwjgl.opengl.{GL11, GL12}

/**
 *
 *
 * @author CountryGamer
 */
@SideOnly(Side.CLIENT)
class GuiStatue(val tileEntity: TileEntityStatue) extends GuiScreenWrapper() {

	var facial: GuiButtonIterator = null
	var arms: GuiButtonIterator = null
	var rotationField: GuiTextField = null
	var sub90: GuiButton = null
	var sub45: GuiButton = null
	var add45: GuiButton = null
	var add90: GuiButton = null
	var saveRotation: GuiButton = null

	// Variable used for rendering
	var angelEntity: EntityWeepingAngel = null

	// Default Constructor
	{
		this.setupGui("Edit Statue", null)

	}

	// End Constructor

	override def initGui(): Unit = {
		super.initGui()

		var bID: Int = 0

		this.facial = new GuiButtonIterator(bID, 30, 50, Array(
			"Calm", "Angry"
		))
		bID += 1
		this.buttonList.asInstanceOf[java.util.List[GuiButton]].add(this.facial)

		this.arms = new GuiButtonIterator(bID, 30, 90, Array(
			"Hiding", "Peaking", "Confident"
		))
		bID += 1
		this.buttonList.asInstanceOf[java.util.List[GuiButton]].add(this.arms)

		this.rotationField = new
						GuiTextField(this.fontRendererObj, this.width / 2 - 50, 220, 100, 20)
		this.setupTextField(this.rotationField, 100)

		this.sub90 = new GuiButton(bID, this.rotationField.xPosition - 10 - 50 - 10 - 50,
			this.rotationField.yPosition, 50, 20, "-90")
		bID += 1
		this.buttonList.asInstanceOf[java.util.List[GuiButton]].add(this.sub90)

		this.sub45 = new GuiButton(bID, this.rotationField.xPosition - 10 - 50,
			this.rotationField.yPosition, 50, 20, "-45")
		bID += 1
		this.buttonList.asInstanceOf[java.util.List[GuiButton]].add(this.sub45)

		this.add45 = new GuiButton(bID, this.rotationField.xPosition + 100 + 10,
			this.rotationField.yPosition, 50, 20, "+45")
		bID += 1
		this.buttonList.asInstanceOf[java.util.List[GuiButton]].add(this.add45)

		this.add90 = new GuiButton(bID, this.rotationField.xPosition + 100 + 10 + 50 + 10,
			this.rotationField.yPosition, 50, 20, "+90")
		bID += 1
		this.buttonList.asInstanceOf[java.util.List[GuiButton]].add(this.add90)

		this.saveRotation = new GuiButton(bID, this.rotationField.xPosition + 50 - 35,
			this.rotationField.yPosition - 25, 70, 20, "Save Rotation")
		bID += 1
		this.buttonList.asInstanceOf[java.util.List[GuiButton]].add(this.saveRotation)

		this.updateComponents()

	}

	override def mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int): Unit = {
		super.mouseClicked(mouseX, mouseY, mouseButton)

		if (this.facial.mousePressed(this.mc, mouseX, mouseY)) {
			this.facial.onPressed(mouseButton)

			PacketHandler.sync(WeepingAngels.pluginID, new
							PacketModifyStatue(this.tileEntity.xCoord, this.tileEntity.yCoord,
								this.tileEntity.zCoord, 1,
								this.facial.getIndex.asInstanceOf[Float]))

		}

		if (this.arms.mousePressed(this.mc, mouseX, mouseY)) {
			this.arms.onPressed(mouseButton)

			PacketHandler.sync(WeepingAngels.pluginID, new
							PacketModifyStatue(this.tileEntity.xCoord, this.tileEntity.yCoord,
								this.tileEntity.zCoord, 2,
								this.arms.getIndex.asInstanceOf[Float]))

		}

	}

	override def actionPerformed(button: GuiButton): Unit = {

		if (button.id == this.sub90.id || button.id == this.sub45.id ||
				button.id == this.add45.id || button.id == this.add90.id) {
			var rotation: Float = this.parseRotationFromField()

			if (button.id == this.sub90.id) {
				rotation -= 90

			}
			else if (button.id == this.sub45.id) {
				rotation -= 45

			}
			else if (button.id == this.add45.id) {
				rotation += 45

			}
			else if (button.id == this.add90.id) {
				rotation += 90

			}

			if (rotation < 0) {
				rotation += 360
			}
			else if (rotation >= 360) {
				rotation -= 360
			}

			this.rotationField.setText(rotation + "")

		}
		else if (button.id == this.saveRotation.id) {
			PacketHandler.sync(WeepingAngels.pluginID, new
							PacketModifyStatue(this.tileEntity.xCoord, this.tileEntity.yCoord,
								this.tileEntity.zCoord, 3, this.parseRotationFromField()))

		}

	}

	def parseRotationFromField(): Float = {

		val rotText: String = this.rotationField.getText

		var rotation: Float = 0.0F
		try {
			rotation = rotText.toFloat
		}
		catch {
			case e: NumberFormatException =>
		}

		rotation
	}

	def updateComponents(): Unit = {
		this.facial.updateIndexAndText(this.tileEntity.getFacialState)
		this.arms.updateIndexAndText(this.tileEntity.getArmState)
		this.rotationField.setText(this.tileEntity.getRotation + "")

	}

	override def doesGuiPauseGame(): Boolean = {
		false
	}

	override protected def drawGuiBackground(): Unit = {
		GL11.glPushMatrix()
		HUDOverlay.renderBlackoutWithAlpha(0.7F, this.width, this.height)

		// Draw statue background
		HUDOverlay.renderBlackoutWithAlpha(1.0F, 140, 30, 250, 160)

		GL11.glPopMatrix()

	}

	override protected def drawGuiBackgroundLayer(mouseX: Int, mouseY: Int,
			renderPartialTicks: Float): Unit = {
		super.drawGuiBackgroundLayer(mouseX, mouseY, renderPartialTicks)

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F)
		//GL11.glDisable(GL11.GL_LIGHTING)
		//this.drawStatue(0, 0, 30, new EntityWeepingAngel(this.mc.thePlayer.worldObj))
		val x: Int = 140 + 125
		val y: Int = 30 + 140
		/*
		GuiInventory.func_147046_a(x, y, 70,
			(x).asInstanceOf[Float],
			(y - 50).asInstanceOf[Float], new EntityWeepingAngel(this.mc.theWorld))
		*/

		this.angelEntity = new EntityWeepingAngel(this.mc.theWorld)

		this.angelEntity.setAngryState(this.tileEntity.getFacialState.asInstanceOf[Byte])
		this.angelEntity.setArmState(this.tileEntity.getArmState.asInstanceOf[Byte])

		this.drawStatue(x, y, 60, this.tileEntity.getRotation, this.angelEntity)

	}

	override def drawTitle(x: Int, y: Int): Unit = {
		this.drawString(this.title,
			this.getGuiLeft + (this.xSize / 2) - (this.getStringWidth(this.title) / 2), 10, -1)
	}

	def drawStatue(x: Int, y: Int, scale: Int, rotation: Float, entity: EntityLivingBase): Unit = {

		GL11.glEnable(GL11.GL_COLOR_MATERIAL)

		GL11.glPushMatrix
		// Move to position on screen
		GL11.glTranslatef(x.asInstanceOf[Float], y.asInstanceOf[Float], 50.0F)
		// Scale
		GL11.glScalef((-scale).asInstanceOf[Float], scale.asInstanceOf[Float],
			scale.asInstanceOf[Float])
		// Turn right side up
		GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F)
		/*
		val f2: Float = entity.renderYawOffset
		val f3: Float = entity.rotationYaw
		val f4: Float = entity.rotationPitch
		val f5: Float = entity.prevRotationYawHead
		val f6: Float = entity.rotationYawHead
		*/
		GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F)
		RenderHelper.enableStandardItemLighting
		GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F)
		/*
		GL11.glRotatef(
			-(Math.atan((p_147046_4_ / 40.0F).asInstanceOf[Double]).asInstanceOf[Float]) * 20.0F,
			1.0F, 0.0F, 0.0F)
		*/
		GL11.glRotatef(10.0F, 1.0F, 0.0F, 0.0F)
		GL11.glRotatef(rotation, 0.0F, 1.0F, 0.0F)
		/*
		entity.renderYawOffset =
				Math.atan((p_147046_3_ / 40.0F).asInstanceOf[Double]).asInstanceOf[Float] * 20.0F
		entity.rotationYaw =
				Math.atan((p_147046_3_ / 40.0F).asInstanceOf[Double]).asInstanceOf[Float] * 40.0F
		entity.rotationPitch =
				-(Math.atan((p_147046_4_ / 40.0F).asInstanceOf[Double]).asInstanceOf[Float]) * 20.0F
		entity.rotationYawHead = entity.rotationYaw
		entity.prevRotationYawHead = entity.rotationYaw
		*/
		GL11.glTranslatef(0.0F, entity.yOffset, 0.0F)

		RenderManager.instance.playerViewY = 180.0F
		RenderManager.instance.renderEntityWithPosYaw(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F)
		/*
		entity.renderYawOffset = f2
		entity.rotationYaw = f3
		entity.rotationPitch = f4
		entity.prevRotationYawHead = f5
		entity.rotationYawHead = f6
		*/
		GL11.glPopMatrix

		RenderHelper.disableStandardItemLighting

		GL11.glDisable(GL12.GL_RESCALE_NORMAL)

		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit)
		GL11.glDisable(GL11.GL_TEXTURE_2D)
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit)

	}

}

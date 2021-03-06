package com.countrygamer.weepingangels.common.lib

import java.util

import com.countrygamer.weepingangels.common.WAOptions
import com.countrygamer.weepingangels.common.entity.EntityWeepingAngel
import net.minecraft.block.Block
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.util._
import net.minecraft.world.{EnumDifficulty, World}

/**
 *
 *
 * @author CountryGamer
 */
object AngelUtility {

	def canBeSeen_Multiplayer(world: World, entity: EntityLivingBase, boundingBox: AxisAlignedBB,
			radius: Double): Boolean = {

		if (world.getFullBlockLightValue(
			MathHelper.floor_double(entity.posX),
			MathHelper.floor_double(entity.posY),
			MathHelper.floor_double(entity.posZ)
		) <= 1.0F)
			return false

		val entityList: java.util.List[_] = world
				.getEntitiesWithinAABB(classOf[EntityPlayer],
		            boundingBox.expand(radius, radius, radius))

		var numberOfPlayersWatching: Int = 0

		var index: Int = 0
		for (index <- 0 until entityList.size()) {
			val player: EntityPlayer = entityList.get(index).asInstanceOf[EntityPlayer]

			if (this.isInFieldOfViewOf(player, entity)) {
				numberOfPlayersWatching = numberOfPlayersWatching + 1
			}

		}

		numberOfPlayersWatching > 0
	}

	def isInFieldOfViewOf(entity: EntityLivingBase, thisEntity: EntityLivingBase): Boolean = {
		val entityLookVec: Vec3 = entity.getLook(1.0F) //.normalize()
		val differenceVec: Vec3 = Vec3.createVectorHelper(
				thisEntity.posX - entity.posX,
				thisEntity.boundingBox.minY +
						(thisEntity.height /* / 2.0F */).asInstanceOf[Double] -
						(entity.posY + entity.getEyeHeight().asInstanceOf[Double]),
				thisEntity.posZ - entity.posZ
			)

		val lengthVec: Double = differenceVec.lengthVector()

		val differenceVec_normal = differenceVec.normalize()

		val d1: Double = entityLookVec.dotProduct(differenceVec_normal)

		if (d1 > (1.0D - 0.025D) / lengthVec && thisEntity.canEntityBeSeen(entity)) {
			true
		}
		else {
			false
		}
	}

	def getNearbyAngels(entity: EntityLivingBase): util.List[_] = {
		entity.worldObj.getEntitiesWithinAABB(classOf[EntityWeepingAngel],
			entity.boundingBox.expand(20D, 20D, 20D))
	}

	def canAttackEntityFrom(world: World, source: DamageSource, damage: Float): Boolean = {
		if (source != null) {
			val validSources: Boolean =
				source == DamageSource.generic ||
						source == DamageSource.magic ||
						source.damageType.equals("player")

			if (!validSources) {
				return false
			}

			source.getSourceOfDamage match {
				case player: EntityPlayer =>
					var canDamage: Boolean = false
					val heldStack: ItemStack = player.inventory.getCurrentItem

					if (WAOptions.angelsOnlyHurtWithPickaxe) {
						if (heldStack != null) {

							var blockLevel: Block = Blocks.dirt

							if (world.difficultySetting == EnumDifficulty.PEACEFUL) {
								blockLevel = Blocks.dirt // anything
							}
							else if (world.difficultySetting == EnumDifficulty.EASY) {
								blockLevel = Blocks.iron_ore // Stone or higher
							}
							else if (world.difficultySetting == EnumDifficulty.NORMAL) {
								blockLevel = Blocks.diamond_ore // Iron or higher
							}
							else if (world.difficultySetting == EnumDifficulty.HARD) {
								blockLevel = Blocks.obsidian // Diamond or higher
							}

							canDamage = heldStack.getItem.canHarvestBlock(blockLevel, heldStack) ||
									heldStack.getItem.func_150897_b(blockLevel)

						}
					}
					else {
						canDamage = true
					}

					if (canDamage) {
						return true
					}

					return false

				case _ =>
					return true
			}

		}
		false
	}

}

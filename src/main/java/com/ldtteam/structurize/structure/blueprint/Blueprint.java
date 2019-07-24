package com.ldtteam.structurize.structure.blueprint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import com.ldtteam.structurize.block.IAnchorBlock;
import com.ldtteam.structurize.structure.StructureBB;
import com.ldtteam.structurize.util.BlockInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.HangingEntity;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.extensions.IForgeBlockState;
import static com.ldtteam.structurize.util.constants.MathConstants.NINETY_DEGREES;

/**
 * The blueprint class which contains the file format for the schematics.
 */
public class Blueprint
{
    /**
     * The list of required mods.
     */
    private final List<String> requiredMods;

    /**
     * The size of the blueprint.
     */
    private short sizeX, sizeY, sizeZ;

    /**
     * The palette of different blocks.
     */
    private List<IForgeBlockState> palette;

    /**
     * The name of the blueprint.
     */
    private String name;

    /**
     * The name of the builders.
     */
    private String[] architects;

    /**
     * A list of missing modids that were missing while this schematic was loaded.
     */
    private String[] missingMods;

    /**
     * The Schematic Data, each short represents an entry in the {@link Blueprint#palette}.
     */
    private short[][][] structure;

    /**
     * The tileentities.
     */
    private CompoundNBT[][][] tileEntities;

    /**
     * The entities.
     */
    private CompoundNBT[] entities = new CompoundNBT[0];

    /**
     * Constructor of a new Blueprint.
     *
     * @param structBB     the structure bounding box.
     * @param palette      the palette.
     * @param structure    the structure data.
     * @param tileEntities the tileEntities.
     * @param requiredMods the required mods.
     */
    protected Blueprint(
        final StructureBB structBB,
        final List<IForgeBlockState> palette,
        final short[][][] structure,
        final CompoundNBT[] tileEntities,
        final List<String> requiredMods)
    {
        this((short) structBB.getXSize(), (short) structBB.getYSize(), (short) structBB.getZSize(), palette, structure, tileEntities, requiredMods);
    }

    /**
     * Constructor of a new Blueprint.
     *
     * @param sizeX        the x size.
     * @param sizeY        the y size.
     * @param sizeZ        the z size.
     * @param palette      the palette.
     * @param structure    the structure data.
     * @param tileEntities the tileEntities.
     * @param requiredMods the required mods.
     */
    protected Blueprint(
        final short sizeX,
        final short sizeY,
        final short sizeZ,
        final List<IForgeBlockState> palette,
        final short[][][] structure,
        final CompoundNBT[] tileEntities,
        final List<String> requiredMods)
    {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
        this.palette = palette;
        this.structure = structure;
        this.tileEntities = new CompoundNBT[sizeY][sizeZ][sizeX];

        for (final CompoundNBT te : tileEntities)
        {
            if (te != null)
            {
                this.tileEntities[te.getShort("y")][te.getShort("z")][te.getShort("x")] = te;
            }
        }
        this.requiredMods = requiredMods;
    }

    /**
     * Constructor of a new Blueprint.
     *
     * @param structBB the structure bounding box.
     */
    public Blueprint(final StructureBB structBB)
    {
        this((short) structBB.getXSize(), (short) structBB.getYSize(), (short) structBB.getZSize());
    }

    /**
     * Constructor of a new Blueprint.
     *
     * @param sizeX the x size.
     * @param sizeY the y size.
     * @param sizeZ the z size.
     */
    public Blueprint(final short sizeX, final short sizeY, final short sizeZ)
    {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
        this.structure = new short[sizeY][sizeZ][sizeX];
        this.tileEntities = new CompoundNBT[sizeY][sizeZ][sizeX];

        this.requiredMods = new ArrayList<>();
        this.palette = new ArrayList<>();
        this.palette.add(Blocks.AIR.getDefaultState());
        // this.palette.add(0, ModBlocks.blockSubstitution.getDefaultState()); ??? why though
    }

    /**
     * @return the Size of the Structure on the X-Axis (without rotation and/or mirroring)
     */
    public short getSizeX()
    {
        return sizeX;
    }

    /**
     * @return the Size of the Structure on the Y-Axis (without rotation and/or mirroring)
     */
    public short getSizeY()
    {
        return sizeY;
    }

    /**
     * @return the Size of the Structure on the Z-Axis (without rotation and/or mirroring)
     */
    public short getSizeZ()
    {
        return sizeZ;
    }

    /**
     * @return the amount of Blockstates within the pallete
     */
    public int getPalleteSize()
    {
        return palette.size();
    }

    /**
     * @return the pallete (without rotation and/or mirroring)
     */
    public IForgeBlockState[] getPalette()
    {
        return palette.toArray(new IForgeBlockState[0]);
    }

    /**
     * Add a blockstate to the structure.
     *
     * @param pos   the position to add it to.
     * @param state the state to add.
     */
    public void addBlockState(final BlockPos pos, final IForgeBlockState state)
    {
        int index = palette.indexOf(state);

        if (index == -1)
        {
            index = palette.size();
            palette.add(state);
        }

        structure[pos.getY()][pos.getZ()][pos.getX()] = (short) index;
    }

    /**
     * @return the structure (without rotation and/or mirroring) The Coordinate order is: y, z, x
     */
    public short[][][] getStructure()
    {
        return structure;
    }

    /**
     * @return an array of serialized TileEntities (posX, posY and posZ tags have been localized to coordinates within the structure)
     */
    public CompoundNBT[][][] getTileEntities()
    {
        return tileEntities;
    }

    /**
     * @return an array of serialized TileEntities (the Pos tag has been localized to coordinates within the structure)
     */
    public CompoundNBT[] getEntities()
    {
        return entities;
    }

    /**
     * @param entitiesIn an array of serialized TileEntities (the Pos tag need to be localized to coordinates within the structure)
     */
    public void setEntities(final CompoundNBT[] entitiesIn)
    {
        entities = entitiesIn;
    }

    /**
     * @return a list of all required mods as modid's
     */
    public List<String> getRequiredMods()
    {
        return requiredMods;
    }

    /**
     * @return the Name of the Structure
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name of the Structure.
     *
     * @param nameIn the name to set.
     * @return this object.
     */
    public Blueprint setName(final String nameIn)
    {
        name = nameIn;
        return this;
    }

    /**
     * @return an Array of all architects for this structure
     */
    public String[] getArchitects()
    {
        return architects;
    }

    /**
     * Sets an Array of all architects for this structure.
     *
     * @param architectsIn an array of architects.
     * @return this blueprint.
     */
    public Blueprint setArchitects(final String[] architectsIn)
    {
        architects = architectsIn;
        return this;
    }

    /**
     * @return An Array of all missing mods that are required to generate this structure
     *         (only works if structure was loaded from file)
     */
    public String[] getMissingMods()
    {
        return missingMods;
    }

    /**
     * Sets the missing mods.
     *
     * @param missingModsIn the missing mods list.
     * @return this object.
     */
    public Blueprint setMissingMods(final String... missingModsIn)
    {
        missingMods = missingModsIn;
        return this;
    }

    /**
     * Get a list of all entities in the blueprint as a list.
     *
     * @return the list of CompoundNBTs.
     */
    public final List<CompoundNBT> getEntitiesAsList()
    {
        return Arrays.stream(entities).collect(Collectors.toList());
    }

    /**
     * Get a list of all blockInfo objects in the blueprint.
     *
     * @return a list of all blockinfo (position, blockState, tileEntityData).
     */
    public final List<BlockInfo> getBlockInfoAsList()
    {
        final List<BlockInfo> list = new ArrayList<>();
        for (short x = 0; x < sizeX; x++)
        {
            for (short y = 0; y < sizeY; y++)
            {
                for (short z = 0; z < sizeZ; z++)
                {
                    final BlockPos tempPos = new BlockPos(x, y, z);
                    final short value = structure[y][z][x];
                    final IForgeBlockState state = palette.get(value & 0xFFFF);
                    list.add(new BlockInfo(tempPos, state, tileEntities[y][z][x]));
                }
            }
        }
        return list;
    }

    /**
     * Rotate the structure depending on the direction it's facing.
     *
     * @param rotation times to rotateWithMirror.
     * @param mirror   the mirror.
     * @param world    the world.
     * @return the new offset.
     */
    public BlockPos rotateWithMirror(final Rotation rotation, final Mirror mirror, final World world)
    {
        final BlockPos resultSize = transformedSize(new BlockPos(sizeX, sizeY, sizeZ), rotation);
        final short newSizeX = (short) resultSize.getX();
        final short newSizeY = (short) resultSize.getY();
        final short newSizeZ = (short) resultSize.getZ();

        final short[][][] newStructure = new short[newSizeY][newSizeZ][newSizeX];
        final CompoundNBT[] newEntities = new CompoundNBT[entities.length];
        final CompoundNBT[][][] newTileEntities = new CompoundNBT[newSizeY][newSizeZ][newSizeX];

        final List<IForgeBlockState> tempPalette = new ArrayList<>();
        for (int i = 0; i < palette.size(); i++)
        {
            tempPalette.add(i, palette.get(i).getBlockState().mirror(mirror).rotate(rotation));
        }

        final BlockPos extremes = transformedBlockPos(sizeX, sizeY, sizeZ, mirror, rotation);
        final int minX = extremes.getX() < 0 ? -extremes.getX() - 1 : 0;
        final int minY = extremes.getY() < 0 ? -extremes.getY() - 1 : 0;
        final int minZ = extremes.getZ() < 0 ? -extremes.getZ() - 1 : 0;

        palette = tempPalette;

        boolean foundAnchor = false;
        BlockPos offset = null;

        for (short x = 0; x < sizeX; x++)
        {
            for (short y = 0; y < sizeY; y++)
            {
                for (short z = 0; z < sizeZ; z++)
                {
                    final BlockPos tempPos = transformedBlockPos(x, y, z, mirror, rotation).add(minX, minY, minZ);
                    final short value = structure[y][z][x];
                    final IForgeBlockState state = palette.get(value & 0xFFFF);
                    if (state.getBlockState().getBlock() == Blocks.STRUCTURE_VOID)
                    {
                        continue;
                    }
                    if (state.getBlockState().getBlock() instanceof IAnchorBlock)
                    {
                        offset = tempPos;
                        foundAnchor = true;
                    }
                    newStructure[tempPos.getY()][tempPos.getZ()][tempPos.getX()] = value;

                    final CompoundNBT compound = tileEntities[y][z][x];
                    if (compound != null)
                    {
                        compound.putInt("x", tempPos.getX());
                        compound.putInt("y", tempPos.getY());
                        compound.putInt("z", tempPos.getZ());
                    }
                    newTileEntities[tempPos.getY()][tempPos.getZ()][tempPos.getX()] = compound;
                }
            }
        }

        for (int i = 0; i < entities.length; i++)
        {
            final CompoundNBT entitiesCompound = entities[i];
            if (entitiesCompound != null)
            {
                newEntities[i] = transformEntityInfoWithSettings(entitiesCompound, world, new BlockPos(minX, minY, minZ), rotation, mirror);
            }
        }

        BlockPos temp;
        if (rotation.equals(Rotation.CLOCKWISE_90) || rotation.equals(Rotation.COUNTERCLOCKWISE_90) || mirror.equals(Mirror.FRONT_BACK))
        {
            if (minX == minZ)
            {
                temp = new BlockPos(resultSize.getX(), resultSize.getY(), minZ > 0 ? -resultSize.getZ() : resultSize.getZ());
            }
            else
            {
                temp = new BlockPos(minX > 0 ? -resultSize.getX() : resultSize.getX(), resultSize.getY(), minZ > 0 ? -resultSize.getZ() : resultSize.getZ());
            }

            Rotation theRotation = rotation;
            if (rotation == Rotation.CLOCKWISE_90)
            {
                theRotation = Rotation.COUNTERCLOCKWISE_90;
            }
            else if (rotation == Rotation.COUNTERCLOCKWISE_90)
            {
                theRotation = Rotation.CLOCKWISE_90;
            }

            temp = temp.rotate(theRotation);
        }
        else
        {
            temp = resultSize;
        }

        if (!foundAnchor)
        {
            BlockPos tempSize = new BlockPos(temp.getX(), 0, temp.getZ());
            if (rotation == Rotation.CLOCKWISE_90)
            {
                tempSize = new BlockPos(-temp.getZ(), 0, temp.getX());
            }
            if (rotation == Rotation.CLOCKWISE_180)
            {
                tempSize = new BlockPos(-temp.getX(), 0, -temp.getZ());
            }
            if (rotation == Rotation.COUNTERCLOCKWISE_90)
            {
                tempSize = new BlockPos(temp.getZ(), 0, -temp.getX());
            }

            offset = new BlockPos(tempSize.getX() / 2, 0, tempSize.getZ() / 2).add(minX, minY, minZ);
        }

        sizeX = newSizeX;
        sizeY = newSizeY;
        sizeZ = newSizeZ;

        structure = newStructure;
        entities = newEntities;
        tileEntities = newTileEntities;

        return offset;
    }

    /**
     * Calculate the transformed size from a blockpos.
     *
     * @param pos      the pos to transform
     * @param rotation the rotation to apply.
     * @return the resulting size.
     */
    public static BlockPos transformedSize(final BlockPos pos, final Rotation rotation)
    {
        switch (rotation)
        {
            case COUNTERCLOCKWISE_90:
            case CLOCKWISE_90:
                return new BlockPos(pos.getZ(), pos.getY(), pos.getX());
            default:
                return pos;
        }
    }

    /**
     * Transforms a blockpos with mirror and rotation.
     *
     * @param xIn      the x input.
     * @param y        the y input.
     * @param zIn      the z input.
     * @param mirror   the mirror.
     * @param rotation the rotation.
     * @return the resulting position.
     */
    public static BlockPos transformedBlockPos(final int xIn, final int y, final int zIn, final Mirror mirror, final Rotation rotation)
    {
        int x = xIn;
        int z = zIn;

        boolean flag = true;

        switch (mirror)
        {
            case LEFT_RIGHT:
                z = -zIn;
                break;
            case FRONT_BACK:
                x = -xIn;
                break;
            default:
                flag = false;
        }

        switch (rotation)
        {
            case COUNTERCLOCKWISE_90:
                return new BlockPos(z, y, -x);
            case CLOCKWISE_90:
                return new BlockPos(-z, y, x);
            case CLOCKWISE_180:
                return new BlockPos(-x, y, -z);
            default:
                return flag ? new BlockPos(x, y, z) : new BlockPos(xIn, y, zIn);
        }
    }

    /**
     * Transform an entity and rotate it.
     *
     * @param entityInfo the entity nbt.
     * @param world      the world.
     * @param pos        the position.
     * @param rotation   the wanted rotation.
     * @param mirror     the mirror.
     * @return the updated nbt.
     */
    private CompoundNBT transformEntityInfoWithSettings(final CompoundNBT entityInfo, final World world, final BlockPos pos, final Rotation rotation, final Mirror mirror)
    {
        final Entity finalEntity = EntityType.loadEntityUnchecked(entityInfo, world).get();
        if (finalEntity != null)
        {
            final Vec3d entityVec = Blueprint.transformedVec3d(rotation, mirror, finalEntity.getPositionVector()).add(new Vec3d(pos));
            finalEntity.prevRotationYaw = (float) (finalEntity.getMirroredYaw(mirror) - NINETY_DEGREES);
            final double rotationYaw = finalEntity.getMirroredYaw(mirror) + ((double) finalEntity.getMirroredYaw(mirror) - (double) finalEntity.getRotatedYaw(rotation));

            if (finalEntity instanceof HangingEntity)
            {
                final BlockPos currentPos = ((HangingEntity) finalEntity).getHangingPosition();
                final BlockPos entityPos = Blueprint.transformedBlockPos(currentPos.getX(), currentPos.getY(), currentPos.getZ(), mirror, rotation).add(pos);

                finalEntity.posX = entityVec.x;
                finalEntity.posY = entityVec.y;
                finalEntity.posZ = entityVec.z;

                finalEntity.setPosition(entityPos.getX(), entityPos.getY(), entityPos.getZ());
            }
            else
            {
                finalEntity.setLocationAndAngles(entityVec.x, entityVec.y, entityVec.z, (float) rotationYaw, finalEntity.rotationPitch);
            }

            return finalEntity.serializeNBT();
        }

        return null;
    }

    /**
     * Transform a Vec3d with rotation and mirror.
     *
     * @param rotation the rotation.
     * @param mirror   the mirror.
     * @param vec      the vec to transform.
     * @return the result.
     */
    private static Vec3d transformedVec3d(final Rotation rotation, final Mirror mirror, final Vec3d vec)
    {
        double xCoord = vec.x;
        double zCoord = vec.z;
        boolean flag = true;

        switch (mirror)
        {
            case LEFT_RIGHT:
                zCoord = 1.0D - zCoord;
                break;
            case FRONT_BACK:
                xCoord = 1.0D - xCoord;
                break;
            default:
                flag = false;
        }

        switch (rotation)
        {
            case COUNTERCLOCKWISE_90:
                return new Vec3d(zCoord, vec.y, 1.0D - xCoord);
            case CLOCKWISE_90:
                return new Vec3d(1.0D - zCoord, vec.y, xCoord);
            case CLOCKWISE_180:
                return new Vec3d(1.0D - xCoord, vec.y, 1.0D - zCoord);
            default:
                return flag ? new Vec3d(xCoord, vec.y, zCoord) : vec;
        }
    }
}
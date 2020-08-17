package com.ldtteam.structurize.network.messages;

import com.ldtteam.structurize.Structurize;
import com.ldtteam.structurize.management.Structures;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Class handling the colony styles messages.
 */
public class StructurizeStylesMessage implements IMessage
{
    private boolean             allowPlayerSchematics;
    private Map<String, String> md5Map;

    /**
     * True while loading
     */
    public static volatile boolean isLoading = false;

    /**
     * Offthread schematic reading
     */
    private static ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    /**
     * Empty constructor used when registering the message.
     */
    public StructurizeStylesMessage()
    {
        super();
    }

    @Override
    public void fromBytes(@NotNull final PacketBuffer buf)
    {
        allowPlayerSchematics = buf.readBoolean();
        md5Map = readMD5MapFromByteBuf(buf);
    }

    @NotNull
    private static Map<String, String> readMD5MapFromByteBuf(@NotNull final PacketBuffer buf)
    {
        @NotNull final Map<String, String> map = new HashMap<>();

        final int count = buf.readInt();
        for (int i = 0; i < count; i++)
        {
            final String filename = buf.readString(32767);
            final String md5 = buf.readString(32767);
            map.put(filename, md5);
        }
        return map;
    }

    @Override
    public void toBytes(@NotNull final PacketBuffer buf)
    {
        buf.writeBoolean(Structurize.getConfig().getCommon().allowPlayerSchematics.get());
        writeMD5MapToByteBuf(buf);
    }

    private static void writeMD5MapToByteBuf(@NotNull final PacketBuffer buf)
    {
        final Map<String, String> md5s = Structures.getMD5s();
        buf.writeInt(md5s.size());
        for (final Map.Entry<String, String> entry : md5s.entrySet())
        {
            buf.writeString(entry.getKey());
            buf.writeString(entry.getValue());
        }
    }

    @Nullable
    @Override
    public LogicalSide getExecutionSide()
    {
        return LogicalSide.CLIENT;
    }

    @Override
    public void onExecute(final NetworkEvent.Context ctxIn, final boolean isLogicalServer)
    {
        isLoading = true;
        executor.submit(() ->
        {
            Structures.init();
            Structures.setAllowPlayerSchematics(allowPlayerSchematics);
            Structures.setMD5s(md5Map);
            isLoading = false;
        });
    }
}

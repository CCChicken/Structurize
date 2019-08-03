package com.ldtteam.structurize.client.render;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.ldtteam.structurize.Instances;
import com.ldtteam.structurize.pipeline.PlaceEventInfoHolder;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.math.Vec3d;

/**
 * Static class for rendering active events.
 */
public class EventRenderer
{
    private final List<PlaceEventInfoHolder<?>> activeEvents = new ArrayList<>();
    private boolean recompileTesselators = false;

    /**
     * Creates new instance.
     */
    public EventRenderer()
    {
        /**
         * Intentionally left empty
         */
    }

    /**
     * Adds active event for rendering. Use
     *
     * @param event new event
     * @return whether addition succeeded or not
     */
    public boolean addActiveEvent(final PlaceEventInfoHolder<?> event)
    {
        if (!event.isCanceled())
        {
            if (activeEvents.size() < Instances.getConfig().getClient().maxAmountOfRenderedEvents.get())
            {
                activeEvents.add(event);
                return true;
            }
        }
        return false;
    }

    /**
     * Marks all active events as canceled.
     */
    public void cancelAllActiveEvents()
    {
        for (final PlaceEventInfoHolder<?> e : activeEvents)
        {
            e.cancel();
        }
    }

    /**
     * Forces every renderer tessellator to recompile.
     */
    public void recompileTessellators()
    {
        recompileTesselators = true;
    }

    /**
     * Renders all active events onto player's screen.
     *
     * @param worldRenderer event data
     * @param partialTicks  event data
     */
    public void renderActiveEvents(final WorldRenderer worldRenderer, final float partialTicks)
    {
        // TODO: should we not render remaining events if we cause tick lag?
        final Iterator<PlaceEventInfoHolder<?>> iterator = activeEvents.iterator();
        while (iterator.hasNext())
        {
            // TODO: proper rendering order would be great, need to determine fronts and backs
            final PlaceEventInfoHolder<?> event = iterator.next();
            if (event.isCanceled())
            {
                iterator.remove();
                continue;
            }

            renderEvent(event);
        }
        recompileTesselators = false;
    }

    /**
     * Renders event onto player's screen.
     *
     * @param event event to render
     */
    private void renderEvent(final PlaceEventInfoHolder<?> event)
    {
        final Vec3d projectedView = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();

        event.getRenderer().draw(projectedView, recompileTesselators);

        renderStructureBB(event, projectedView);
    }

    /**
     * Renders white structure BB.
     *
     * @param event event to render
     * @param view  screen view
     */
    private void renderStructureBB(final PlaceEventInfoHolder<?> event, final Vec3d view)
    {
        GlStateManager.lineWidth(2.0F);
        GlStateManager.disableTexture();
        GlStateManager.depthMask(false);
        WorldRenderer.drawSelectionBoundingBox(event.getPosition().toAABB().expand(1, 1, 1).offset(view.scale(-1)), 1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture();
    }
}

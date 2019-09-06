package com.ldtteam.blockout.views;

import com.ldtteam.blockout.Pane;
import com.ldtteam.blockout.PaneParams;
import org.lwjgl.glfw.GLFW;

/**
 * An OverlayView is a full screen View which is displayed on top of the window.
 */
public class OverlayView extends View
{
    /**
     * Constructs a barebones View.
     */
    public OverlayView()
    {
        super();
    }

    /**
     * Constructs a OverlayView from PaneParams.
     *
     * @param params Params for the View.
     */
    public OverlayView(final PaneParams params)
    {
        super(params);
    }

    /**
     * hide the view when click on.
     */
    @Override
    public void click(final int mx, final int my)
    {
        final int mxChild = mx - x - padding;
        final int myChild = my - y - padding;
        final Pane clickedPane = findPaneForClick(mxChild, myChild);
        if (clickedPane != null)
        {
            clickedPane.click(mxChild, myChild);
        }
        else
        {
            hide();
        }
    }

    /**
     * hide the view when click on.
     */
    @Override
    public void rightClick(final int mx, final int my)
    {
        final int mxChild = mx - x - padding;
        final int myChild = my - y - padding;
        final Pane clickedPane = findPaneForClick(mxChild, myChild);
        if (clickedPane != null)
        {
            clickedPane.rightClick(mxChild, myChild);
        }
        else
        {
            hide();
        }
    }

    /**
     * Called when a key is pressed.
     * hide the view when ESC is pressed.
     *
     * @param ch  the character.
     * @param key the key.
     * @return false at all times - do nothing.
     */
    @Override
    public boolean onKeyTyped(final char ch, final int key)
    {
        if (isVisible() && key == GLFW.GLFW_KEY_ESCAPE)
        {
            setVisible(false);
            return true;
        }

        return super.onKeyTyped(ch, key);
    }
}

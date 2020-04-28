package imagelab;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Used to close an active window.
 *
 * @author Dr. Aaron Gordon
 * @author Dr. Jody Paul
 */
public class WindowCloser extends WindowAdapter {

    /** The image frame to be killed. */
    private ILFrame theFrame;

    /**
     * Frame Constructor.
     * @param f The Frame
     */
    public WindowCloser(final ILFrame f) {
        theFrame = f;
    }

    /**
     * Closes active window.
     * @param e The Window
     */
    public void windowClosing(final WindowEvent e) {
        theFrame.setVisible(false);
        theFrame.byebye();
    }

    /**
     * Sets the current frame as active.
     * @param e The Window
     */
    public void windowActivated(final WindowEvent e) {
        //System.out.println("WindowCloser:windowActive");
        theFrame.setActive();
    }

}

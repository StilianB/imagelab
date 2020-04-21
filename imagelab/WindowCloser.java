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

    /**
     * The image frame to be killed.
     */
    ILFrame theFrame;

    public WindowCloser(ILFrame f) {
        theFrame = f;
    }

    public void windowClosing(WindowEvent e) {
        theFrame.setVisible(false);
        theFrame.byebye();
    }

    public void windowActivated(WindowEvent e) {
        //System.out.println("WindowCloser:windowActive");
        theFrame.setActive();
    }

}

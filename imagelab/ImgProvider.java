package imagelab;

import sound.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;

/**
 * ImgProvider is responsible for managing a single image
 * (loading, filtration, rendering, etc.).
 *
 * @author Dr. Aaron Gordon
 * @author Dr. Jody Paul
 * @version 1.1
 */
public class ImgProvider extends JComponent {
    /** Serialization version. */
    private static final long serialVersionUID = 11L;
<<<<<<< HEAD
    static Thread playThread;
    static boolean  all
    /** true if this ImgProvider currently holds an image; false otherwise. */
    boolean isLoaded;
=======
    /** Debug variable to show all filters. */
    private static boolean all;
    /** True if this ImgProvider currently holds an image; false otherwise. */
    private boolean isLoaded;
>>>>>>> Cleaned up a bunch of checkstyle errors. A few left in ImgProvider
    /** Image height in pixels. */
    protected int pixheight;
    /** Image width in pixels. */
    protected int pixwidth;
    /** The raw image. */
    private Image img;
    /** Holders for the color and alpha components of the image. */
    private short[][] green, red, blue, alpha;
    /** To retrieve pixels from the image. */
    private PixelGrabber grab;
    /** Holder for the pixels from the image. */
    protected int[] pix;
    /** X-axis increment used for trimming the image. */
    private int xinc = 0;
    /** Y-axis increment used for trimming the image. */
    private int yinc = 0;
    /** Holder for the filename of the file that contains the image. */
    private String imgName;
    /** Used for assigning unique IDs to ImgProviders. Incremented when used. */
    private static int count = 0;
    /** Identification used to distinguish one ImgProvider from another. */
    private int id;
    /** Imagelab object. */
    private ImageLab lab;
    /** Max alpha value. */
    static final int ALPHA = 255;
    /** Bits to left shift. */
    static final int RANGE = 8;
    /** Default time to sleep. */
    static final int SLEEP_TIME = 300;


    /** No-argument constructor.  Sets name to empty string. */
    public ImgProvider() {
        this("");
    }

    /**
     * Constructor that accepts a filename.
     *
     * @param name The name of the file containing the image
     */
    public ImgProvider(final String name) {
        imgName = name;
        isLoaded = false;
        id = ++count;
    }

    /**
     * Retrieve this ImgProvider's unique id.
     * For ImageLab's internal use.
     *
     * @return Id of ImgProvider
     */
    public int getid() {
        return id;
    }

    /**
     * Create a B&W image object based on the parameter.
     * Uses the instance variable pix as destination.
     *
     * @param image 2D array of black-and-white pixel values (0-255)
     */
    public void setBWImage(final short[][] image) {
        int spot = 0;
        int tmp;
        pixheight = image.length;
        pixwidth = image[0].length;
        pix = new int[pixheight * pixwidth];
        for (int row = 0; row < pixheight; row++) {
            for (int col = 0; col < pixwidth; col++) {
                tmp = ALPHA;
                tmp = tmp << RANGE;
                tmp += image[row][col];
                tmp = tmp << RANGE;
                tmp += image[row][col];
                tmp = tmp << RANGE;
                tmp += image[row][col];
                pix[spot++] = tmp;
            }
        }
        separateColors();
        isLoaded = true;
    }

    /**
     * Return the image in black and white.
     *
     * @return 2D array of pixel grey-values (0 to 255)
     */
    public short[][] getBWImage() {
        //read in image into pix[]
        if (!isLoaded) {
            readinImage();
        }
        toBW();         //convert it to black and white

        //copy from int []pix to short [][]b and filter outliers
        short[][] b = new short[pixheight][pixwidth];
        int spot = 0;
        short tmp;
        for (int r = 0; r < pixheight; r++) {
            for (int c = 0; c < pixwidth; c++) {
                tmp = (short) (pix[spot++] & ALPHA);
                b[r][c] = tmp;
            }
        }
        return b;
    }

    /** Read in the image. */
    public void readinImage() {
        img = getToolkit().getImage(imgName);
        if (img == null) {
            System.err.println("\n\n**ImgProvider: getImage: "
                    + "img is null!!! ***\n\n");
        }
        int width = img.getWidth(null) - xinc;
        int height = img.getHeight(null) - yinc;
        grab = new PixelGrabber(img, xinc, yinc, width, height, true);
        try {
            grab.grabPixels();
        } catch (Exception e) {
            System.err.println("ImgProvider:getBWImage: "
                    + "pixel grabbing failed!!");
            return;
            //System.exit(-1);
        }
        pix = (int[]) grab.getPixels();
        pixwidth = img.getWidth(null) - xinc;
        pixheight = img.getHeight(null) - yinc;
        isLoaded = true;
        separateColors();
        if (all) {
            showPix("Original in Color");
        }
        try {
            Thread.sleep(SLEEP_TIME);
        } catch (Exception e) {
        }
    }


    /**
     * Cut out x columns and y rows from the NW corner of the image.
     *
     * @param x the number of columns to remove
     * @param y the number of rows to remove
     */
    public void setTrim(final int x, final int y) {
        xinc = x;
        yinc = y;
    }

    /** Convert from color to gray scale (black and white). */
    private void toBW() {
        int alpha, red, green, blue, black;

        for (int i = 0; i < pix.length; i++) {
            int num = pix[i];
            final int numOfValues = 3;
            blue = num & ALPHA;
            num = num >> RANGE;
            green = num & ALPHA;
            num = num >> RANGE;
            red = num & ALPHA;
            num = num >> RANGE;
            alpha = num & ALPHA;
            black = (red + green + blue) / numOfValues;
            num = alpha;
            num = (num << RANGE) + black;
            num = (num << RANGE) + black;
            num = (num << RANGE) + black;
            pix[i] = num;
        }
        if (all) {
            showPix("Black and White");
        }
        try {
            Thread.sleep(SLEEP_TIME);
        } catch (Exception e) {
        }
    }


    /**
     * Alias for showPix. (Syntactic sugar)
     *
     * @param name The title for the window.
     */
    public void showImage(final String name) {
        showPix(name);
    }

    /**
     * Display this image in a window.
     *
     * @param name The title for the window.
     */
    public void showPix(final String name) {
        //System.out.println("ImgProvider:showPix:  before readIn");
        if (!isLoaded) {
            readinImage();
        }
        //System.out.println("ImgProvider:showPix:  after readIn");
        img = getToolkit().createImage(
                new MemoryImageSource(pixwidth, pixheight, pix, 0, pixwidth));
        //System.out.println("ImgProvider:showPix:  before displayImage");
        DisplayImage dis = new DisplayImage(this, name, true);
        //System.out.println("ImgProvider:showPix:  after displayImage");
        try {
            Thread.sleep(SLEEP_TIME);
        } catch (Exception e) {
        }
    }

    /**
     * Pull the image apart into its RGB and Alpha components.
     */
    void separateColors() {
        if (pix == null) {
            return;
        }
        alpha = new short[pixheight][pixwidth];
        red = new short[pixheight][pixwidth];
        green = new short[pixheight][pixwidth];
        blue = new short[pixheight][pixwidth];
        int spot = 0;       //index into pix
        for (int r = 0; r < pixheight; r++) {
            for (int c = 0; c < pixwidth; c++) {
                int num = pix[spot++];
                blue[r][c] = (short) (num & ALPHA);
                num = num >> RANGE;
                green[r][c] = (short) (num & ALPHA);
                num = num >> RANGE;
                red[r][c] = (short) (num & ALPHA);
                num = num >> RANGE;
                alpha[r][c] = (short) (num & ALPHA);
            }
        }
    }

    /**
     * Set the RGB and Alpha components for this image.
     *
     * @param rd 2D array that represents the image's red component
     * @param g  2D array that represents the image's green component
     * @param b  2D array that represents the image's blue component
     * @param al 2D array that represents the image's alpha channel
     */
    public void setColors(final short[][] rd, final short[][] g,
                          final short[][] b, final short[][] al) {
        pixheight = rd.length;
        pixwidth = rd[0].length;
        red = new short[pixheight][pixwidth];
        green = new short[pixheight][pixwidth];
        blue = new short[pixheight][pixwidth];
        alpha = new short[pixheight][pixwidth];
        pix = new int[pixwidth * pixheight];
        int tmp;
        int spot = 0;
        for (int r = 0; r < pixheight; r++) {
            for (int c = 0; c < pixwidth; c++) {
                red[r][c] = rd[r][c];
                green[r][c] = g[r][c];
                blue[r][c] = b[r][c];
                alpha[r][c] = al[r][c];
                tmp = alpha[r][c];
                tmp = tmp << RANGE;
                tmp += red[r][c];
                tmp = tmp << RANGE;
                tmp += green[r][c];
                tmp = tmp << RANGE;
                tmp += blue[r][c];
                pix[spot++] = tmp;
            }
        }
        isLoaded = true;
    }

    /**
     * Retrieve the image's green component.
     *
     * @return A 2D array of values from 0 to 255.
     */
    public short[][] getRed() {
        int nrows = red.length;
        int ncols = red[0].length;
        short[][] redcp = new short[nrows][ncols];
        for (int r = 0; r < nrows; r++) {
            for (int c = 0; c < ncols; c++) {
                redcp[r][c] = red[r][c];
            }
        }
        return redcp;
    }

    /**
     * Retrieve the image's green component.
     *
     * @return A 2D array of values from 0 to 255.
     */
    public short[][] getGreen() {
        int nrows = green.length;
        int ncols = green[0].length;
        short[][] thecopy = new short[nrows][ncols];
        for (int r = 0; r < nrows; r++) {
            for (int c = 0; c < ncols; c++) {
                thecopy[r][c] = green[r][c];
            }
        }
        return thecopy;
    }

    /**
     * Retrieve the image's blue component.
     *
     * @return A 2D array of values from 0 to 255.
     */
    public short[][] getBlue() {
        int nrows = blue.length;
        int ncols = blue[0].length;
        short[][] thecopy = new short[nrows][ncols];
        for (int r = 0; r < nrows; r++) {
            for (int c = 0; c < ncols; c++) {
                thecopy[r][c] = blue[r][c];
            }
        }
        return thecopy;
    }

    /**
     * Retrieve the image's alpha component.
     *
     * @return A 2D array with values from 0 to 255.
     */
    public short[][] getAlpha() {
        int nrows = alpha.length;
        int ncols = alpha[0].length;
        short[][] al = new short[nrows][ncols];
        for (int r = 0; r < nrows; r++) {
            for (int c = 0; c < ncols; c++) {
                al[r][c] = alpha[r][c];
            }
        }
        return al;
    }

    /**
     * retrieve the image's width.
     *
     * @return Width of Image.
     */
    public int getWidth() {
        return pixwidth;
    }

    /**
     * Retrieve the image's height.
     *
     * @return Height of Image.
     */
    public int getHeight() {
        return pixheight;
    }

    /**
     * Retrieve the image's raw image.
     * Note that img is not always consistent with RGBA arrays.
     *
     * @return The image's raw image
     */
    public Image getImage() {
        return img;
    }

    /**
     * Called when the window containing this image is selected.
     */
    public void setActive() {
        if (lab != null) {
            lab.setActive(this);
        } else {
            System.err.println("*** error ** ImgProvider:setActive - no lab");
        }
    }

    /** Called when the window containing this image is closed. */
    public void setInactive() {
        if (lab != null) {
            lab.setInactive(this);
            ImageLab.impro = null;
        } else {
            System.err.println("*** error ** ImgProvider:setInactive - no "
                    + "lab");
        }
    }

    /**
     * Used by ImageLab to register itself with this ImgProvider.
     *
     * @param iml Imagelab.
     */
    void setLab(final ImageLab iml) {
        lab = iml;
    }

    /** Used by ImageLab to save an image to a file. */
    void save() {
        JFileChooser fd;
        JFrame myframe = new JFrame();      //to have a parent
        fd = new JFileChooser();
        int returnVal = fd.showSaveDialog(myframe);
        String fname;
        File theFile;
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            JOptionPane.showMessageDialog(myframe, "Encountered a "
                    + "problem in ImgProvider.save()"
                    + "\n- Please try again.");
            return;
        }
        fname = fd.getSelectedFile().getName();
        theFile = fd.getSelectedFile();

        BufferedImage bufim = new BufferedImage(pixwidth, pixheight,
                BufferedImage.TYPE_INT_RGB);
        bufim.setRGB(0, 0, pixwidth, pixheight,
                pix, 0, pixwidth);

        try {
            if (!ImageIO.write(bufim, "jpeg", theFile)) {
                System.err.println("Couldn't write file - save failed");
            }
        } catch (IOException ioe) {
            System.err.println("Attempt to save file failed.");
        }
    }

    /**
     * Used by ImageLab to render an image as sound.
     * Three channels of sound are created, using the
     * average the Red, Green and Blue values of each
     * row to establish the pitches for the channels.
     * The average of Hue, Saturation and Brightness
     * values of each row are used as the velocities
     * of the Red, Green and Blue notes respectively.
     */
    public void play() {
<<<<<<< HEAD
        playThread = new Thread(() -> {
            short[][] red = getRed();     // Red plane
            short[][] green = getGreen(); // Green plane
            short[][] blue = getBlue();   // Blue plane
            short[][] bw = getBWImage();  // Black & white image
            short[][] alpha = getAlpha(); // Alpha channel
            short[][] hue;
            short[][] saturation;
            short[][] brightness;

            int height = bw.length;
            int width = bw[0].length;

            //System.out.println("Playing image number " + getid());

            Tune tune = new Tune();
            /* A 7-octave pentatonic scale. */
            Scale scale = new Scale();
            for (int i = -3; i < 4; i++) {
                scale.addPitch(Note.C + (12 * i));
                scale.addPitch((Note.C + 3) + (12 * i));
                scale.addPitch((Note.C + 5) + (12 * i));
                scale.addPitch((Note.C + 7) + (12 * i));
                scale.addPitch((Note.C + 10) + (12 * i));
            }
            int pitchRange = scale.numPitches();
            Chord chord;
            int[] velocity = {0, 0, 0};
            int velocityRange = Note.VRANGE;
            int tempo = Note.DE / 2;
            int rowSum = 0;
            int redSum = 0;
            int greenSum = 0;
            int blueSum = 0;
            float[] hsb = {0, 0, 0};
            float hueSum = 0;
            float satSum = 0;
            float brtSum = 0;

            for (int row = 0; row < height; row++) {
                for (int column = 0; column < width; column++) {
                    rowSum += (bw[row][column]);
                    redSum += (red[row][column]);
                    greenSum += (green[row][column]);
                    blueSum += (blue[row][column]);
                    java.awt.Color
                            .RGBtoHSB(red[row][column], green[row][column], blue[row][column],
                                      hsb);
                    hueSum += hsb[0];
                    satSum += hsb[1];
                    brtSum += hsb[2];
                }//for column
                velocity[0] = (int) (Note.VPP + (velocityRange * (hueSum / width)));
                velocity[1] = (int) (Note.VPP + (velocityRange * (satSum / width)));
                velocity[2] = (int) (Note.VPP + (velocityRange * (brtSum / width)));
                chord = new Chord();
                chord.addNote(new Note(0, (scale.getPitch(
                        pitchRange * redSum / width / 256)), tempo, velocity[0]));
                chord.addNote(new Note(1, (scale.getPitch(pitchRange * greenSum / width / 256)), tempo,
                                       velocity[1]));
                chord.addNote(new Note(2, (scale.getPitch(
                        pitchRange * blueSum / width / 256)), tempo, velocity[2]));
                tune.addChord(chord);
                rowSum = 0;
                redSum = 0;
                greenSum = 0;
                blueSum = 0;
                hueSum = 0;
                satSum = 0;
                brtSum = 0;
            }//for row
            int[] instruments = {Note.Vibes, Note.Pizzacatto, Note.MelodicTom};
            Music m = new Music(3, instruments);
            m.playTune(tune);
        });
        playThread.start();

=======
        short[][] red = getRed();     // Red plane
        short[][] green = getGreen(); // Green plane
        short[][] blue = getBlue();   // Blue plane
        short[][] bw = getBWImage();  // Black & white image
        short[][] alpha = getAlpha(); // Alpha channel
        short[][] hue;
        short[][] saturation;
        short[][] brightness;

        int height = bw.length;
        int width = bw[0].length;

        //System.out.println("Playing image number " + getid());

        Tune tune = new Tune();
        /* A 7-octave pentatonic scale. */
        Scale scale = new Scale();
        for (int i = -3; i < 4; i++) {
            scale.addPitch(Note.C + (12 * i));
            scale.addPitch((Note.C + 3) + (12 * i));
            scale.addPitch((Note.C + 5) + (12 * i));
            scale.addPitch((Note.C + 7) + (12 * i));
            scale.addPitch((Note.C + 10) + (12 * i));
        }
        int pitchRange = scale.numPitches();
        Chord chord;
        int[] velocity = {0, 0, 0};
        int velocityRange = Note.VRANGE;
        int tempo = Note.DE / 2;
        int rowSum = 0;
        int redSum = 0;
        int greenSum = 0;
        int blueSum = 0;
        float[] hsb = {0, 0, 0};
        float hueSum = 0;
        float satSum = 0;
        float brtSum = 0;

        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                rowSum += (bw[row][column]);
                redSum += (red[row][column]);
                greenSum += (green[row][column]);
                blueSum += (blue[row][column]);
                java.awt.Color.RGBtoHSB(red[row][column], green[row][column],
                        blue[row][column], hsb);
                hueSum += hsb[0];
                satSum += hsb[1];
                brtSum += hsb[2];
            }
            velocity[0] = (int) (Note.VPP + (velocityRange * (hueSum / width)));
            velocity[1] = (int) (Note.VPP + (velocityRange * (satSum / width)));
            velocity[2] = (int) (Note.VPP + (velocityRange * (brtSum / width)));
            chord = new Chord();
            chord.addNote(new Note(0, (scale.getPitch(pitchRange
                    * redSum / width / 256)), tempo, velocity[0]));
            chord.addNote(new Note(1, (scale.getPitch(pitchRange
                    * greenSum / width / 256)), tempo, velocity[1]));
            chord.addNote(new Note(2, (scale.getPitch(pitchRange
                    * blueSum / width / 256)), tempo, velocity[2]));
            tune.addChord(chord);
            rowSum = 0;
            redSum = 0;
            greenSum = 0;
            blueSum = 0;
            hueSum = 0;
            satSum = 0;
            brtSum = 0;
        }
        int[] instruments = {Note.Vibes, Note.Pizzacatto, Note.MelodicTom};
        Music m = new Music(3, instruments);
        m.playTune(tune);
>>>>>>> Used an automated checkstyle tool to fix about half of the issues that checkstyle was reporting
    }

    /**
     * Display this image a line at a time in a window.
     *
     * @param name The title for the window.
     */
    public void showPixNew(final String name) {
        System.out.println("ImgProvider:showSlow: Before readinImage");
        if (!isLoaded) {
            readinImage();
        }
        System.out.println("ImgProvider:showSlow: After readinImage");
        img = getToolkit().createImage(
                new MemoryImageSource(pixwidth, pixheight, pix, 0, pixwidth));
        DynDisplayImage dImage1 = new DynDisplayImage(this, name, true);
        dImage1.setVisible(true);
        dImage1.repaint();
        System.out.println("ImgProvider:showSlow: Constructed DynaPanel");
        System.out.println("ImgProvider:showSlow: size is ("
                + pixwidth + ", " + pixheight + ")");
        try {
            Thread.sleep(1000);  //give image time to display
        } catch (Exception e) {
        }
        dImage1.changeImage(this, "Second Pass");
        System.out.println("ImgProvider:showSlow: Second Pass");
    }

}

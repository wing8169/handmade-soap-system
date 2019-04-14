package handmadesoupmanagementsystem;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

/**
 *
 * @author pballeux
 */
public class MyPrintable implements Printable {

    Printable delegate;
    double spaceLeft = 0;
    double minimumRequired = 72;
    static int debugindex = 0;

    public MyPrintable(Printable p) {
        this.delegate = p;
    }

    public MyPrintable(Printable p, double minimumHeight) {
        this.delegate = p;
        this.minimumRequired = minimumHeight;
    }

    public void setMinimumRequired(double height) {
        minimumRequired = height;
    }

    public double getMinimumRequired() {
        return minimumRequired;
    }

    public double getSpaceLeft() {
        return spaceLeft;
    }

    private int detectLastLine(BufferedImage img) {
        int lastIndex = 0;
        int[] data = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
        for (int i = data.length - 1; i > 0; i--) {
            if (data[i] != 0) {
                lastIndex = i;
                break;
            }
        }
        return (lastIndex / img.getWidth());
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        BufferedImage img = new BufferedImage((int) pageFormat.getWidth(), (int) pageFormat.getHeight(), BufferedImage.TRANSLUCENT);
        Graphics2D g = img.createGraphics();
        int retValue = delegate.print(g, pageFormat, pageIndex);
        if (retValue == PAGE_EXISTS) {
//Find out bound of printing...
//System.out.println("Last Line drawn is : " + detectLastLine(img));
// try {
// ImageIO.write(img, "jpg", new File("img" + debugindex++ + ".jpg"));
// } catch (IOException ex) {
// }
            spaceLeft = (pageFormat.getImageableY() + pageFormat.getImageableHeight()) - detectLastLine(img);
            retValue = delegate.print(graphics, pageFormat, pageIndex);
//Updating paper after the hardprint
            Paper paper = pageFormat.getPaper();
            paper.setImageableArea(paper.getImageableX(), paper.getImageableY() + paper.getImageableHeight() - spaceLeft, paper.getImageableWidth(), spaceLeft);
            pageFormat.setPaper(paper);
        }
        return retValue;
    }
}

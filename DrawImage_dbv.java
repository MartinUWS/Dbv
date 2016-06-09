import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.PolygonRoi;
import ij.plugin.PlugIn;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import ij.util.*;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.awt.*;


/**
 * Created by max on 21.05.16.
 * DBV Praktikum Serie 3
 * Aufgabe 3.1
 * Erstellung verschiedenartiger Bilder
 */

public class DrawImage_dbv implements PlugIn {

	public void run(String arg) {

		int imageType = 0;
		int widthParam = 20;
		int angleParam = 20;

		GenericDialog gd = new GenericDialog("New Image");
		gd.addMessage("Choose a number from Pserie 3.1 - DBV");
		gd.addMessage("0: yellow Image; 1: yellow black stripes");
		gd.addMessage("2: BW diagonal; 3: Saw tooth");
		gd.addMessage("4: chessboard; 5: parametric");
		gd.addMessage("6: sinus Image; 7: conzentric circles");
		gd.addNumericField("Imagetype 0-5: ", imageType, 0);
		gd.addNumericField("Width param: ", widthParam, 0);
		gd.addNumericField("Angle param: ", angleParam, 0);
		gd.addCheckbox("Show FFT", false);
		gd.showDialog();

		if (gd.wasCanceled()) return;

		imageType = (int)gd.getNextNumber();
		widthParam = (int)gd.getNextNumber();
		angleParam = (int)gd.getNextNumber();
		boolean fft = gd.getNextBoolean();

		int w = 400, h = 400;
		ImageProcessor ip = new ColorProcessor(w, h);

		switch (imageType) {
			case 0:
				yellowImage(ip);
				break;
			case 1:
				yellowBlackStripes(ip, widthParam);
				break;
			case 2:
				blackAndWhiteDiagonal(ip);
				break;
			case 3:
				sawTooth(ip);
				break;
			case 4:
				chessBoard(ip, widthParam);
				break;
			case 5:
				paramCreate(ip, widthParam, angleParam);
				break;
			case 6:
				sinusImage(ip, widthParam);
				break;
			case 7:
				conzentricCircles(ip, widthParam);
				break;
			default:
				paramCreate(ip, widthParam, angleParam);
		}

		if (fft) {
			IJ.runMacro("run(\"FFT\");");
		}
	}

	private void yellowImage(ImageProcessor ip) {

		int[] pixels = (int[]) ip.getPixels();
		int i = 0;
		for (int y = 0; y < ip.getHeight(); y++) {
			for (int x = 0; x < ip.getWidth(); x++) {
				pixels[i++] = intColor(255, 255, 0, 255);
			}
		}
		new ImagePlus("Yellow Image", ip).show();
	}

	private void yellowBlackStripes(ImageProcessor ip, int linewidth) {

		int[] pixels = (int[]) ip.getPixels();
		int i = 0;
		boolean yellow = true;
		for (int y = 0; y < ip.getHeight(); y++) {
			//horizontal
			if (y % linewidth == 0) {
				yellow = !yellow;
			}
			for (int x = 0; x < ip.getWidth(); x++) {

				// vertikal
				//if (i % 20 == 0) {
				//	yellow = !yellow;
				//}
				if (yellow)
					pixels[i++] = intColor(255, 255, 0, 255);
				else
					pixels[i++] = intColor(0, 0, 0, 255);
			}
		}
		new ImagePlus("Yellow Stripes Image", ip).show();
	}

	// Idea: create image double the size, rotate by 45° and cut out the image with original size
	private void blackAndWhiteDiagonal(ImageProcessor ip) {

		int[] pixels = (int[]) ip.getPixels();
		int i = 0;

		int linepos = 0;
		int firstLineWidth = 20;
		int linewidth = 20;

		boolean black = true;
		boolean currentstartColor = true;

		for (int y = 0; y < ip.getHeight(); y++) {
			for (int x = 0; x < ip.getWidth(); x++) {
				if ((linepos+1) % (linewidth+1) == 0) {
					black = !black;
					linewidth = 20;
					linepos = 0;
				}

				if (black)
					pixels[i++] = intColor(0, 0, 0, 255);
				else
					pixels[i++] = intColor(255, 255, 255, 255);

				//if (x < 40)
				//	System.out.print(black ? "1" : "0");

				linepos ++;
			}

			firstLineWidth --;
			if (firstLineWidth != 0) {
				linewidth = firstLineWidth;
			}
			else {
				linewidth = firstLineWidth = 20;
				currentstartColor = !currentstartColor;
			}

			black = currentstartColor;
			linepos = 0;
		}
		new ImagePlus("B/W Stripes Image", ip).show();
	}

	private void sawTooth(ImageProcessor ip) {

		boolean black = true;
		for (int x = 0; x < ip.getWidth(); x++) {
			// vertikal
			if (x % 20 == 0) {
				black = !black;
			}

			for (int y = 0; y < ip.getHeight(); y++) {
				int color = 0;
				if (black) {
					color = 255 - (255 * y / ip.getHeight());
				}
				else {
					color = 0 + (255 * y / ip.getHeight());
				}
				ip.putPixel(x,y,intColor(color, color, color, 255));
			}
		}
		new ImagePlus("Sawtooth Image", ip).show();
	}

	private void chessBoard(ImageProcessor ip, int boxwidth) {
		boolean black = true;
		for (int y = 0; y < ip.getHeight(); y++) {

			if (y % boxwidth == 0) {
				black = !black;
			}

			for (int x = 0; x < ip.getWidth(); x++) {
				// vertikal
				if (x % boxwidth == 0) {
					black = !black;
				}

				int color = 0;
				if (black) {
					color = 255;
				}
				ip.putPixel(x,y,intColor(color, color, color, 255));
			}
		}
		new ImagePlus("Chess Image", ip).show();
	}

	private void paramCreate(ImageProcessor ip, int dim, int angle) {
		ImageProcessor base = new ColorProcessor(ip.getWidth() * 2, ip.getHeight() * 2);

		for (int y = 0; y < base.getHeight(); y++) {
			boolean black = true;
			for (int x = 0; x < base.getWidth(); x++) {
				// vertikal
				if (x % dim == 0) {
					black = !black;
				}

				int color = 0;
				if (black) {
					color = 255;
				}
				base.putPixel(x,y,intColor(color, color, color, 255));
			}
		}

		base.rotate(angle);

		base.setRoi(new Rectangle((base.getWidth() / 2 - ip.getWidth() / 2),(base.getHeight() / 2 - ip.getHeight() / 2)
				,ip.getWidth(),ip.getHeight()));
		//new ImagePlus("Base Image", base).show();
		ip = base.crop();
		new ImagePlus("Param Image "+dim+" "+angle, ip).show();
	}

	private void sinusImage(ImageProcessor ip, int width) {
		double dist = width / Math.PI;

		for (int y = 0; y < ip.getHeight(); y++) {
			for (int x = 0; x < ip.getWidth(); x++) {
				int color = 0;
				double normsin = Math.sin(((double) x)/dist) + 1;
				//if (x < 40 && y < 3)
				//	System.out.print("Sin: "+normsin);

				color = (int) (255 * (normsin / 2.0));

				ip.putPixel(x,y,intColor(color, color, color, 255));
			}
		}
		new ImagePlus("Sinus Image", ip).show();
	}

	private void conzentricCircles(ImageProcessor ip,int width) {
		ImageProcessor base = new ColorProcessor(ip.getWidth() * 2, ip.getHeight() * 2);

		base.invert();

		int topleftx = (base.getWidth() / 2 - ip.getWidth() / 2);
		int toplefty = (base.getHeight() / 2 - ip.getHeight() / 2);

		int linewidth = width;

		for (int r = (int)(width * 2.5); r < base.getHeight() * 2; ) {
			base.setLineWidth(linewidth);
			base.drawOval(topleftx - (r/2), toplefty - (r/2), r, r);
			r = (int) (r * 1.25);
			linewidth = (int) (linewidth * 0.75);
		}

		base.setRoi(new Rectangle(topleftx, toplefty
				,ip.getWidth(),ip.getHeight()));
		//new ImagePlus("Base Image", base).show();
		ip = base.crop();
		//new ImagePlus("conzentricCircles base", base).show();
		new ImagePlus("conzentricCircles ", ip).show();
	}



	private int intColor(int red, int green, int blue, int alpha) {
		int color = (alpha << 24) | (red << 16) | (green << 8) | (blue);
		return color;
	}

}
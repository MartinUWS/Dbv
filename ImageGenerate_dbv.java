import ij.gui.*;
import ij.ImagePlus;
import ij.plugin.PlugIn;
import ij.plugin.filter.*;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import ij.*;
import ij.process.*;
import java.awt.*;
import ij.plugin.filter.*;

public class ImageGenerate_dbv implements PlugIn {

	public static ImageProcessor iproc;
	ImagePlus imp;

	/*public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return DOES_ALL;
	}*/

	public void run(String arg) {
		int w = 400, h = 400;
		ImageProcessor ip = new ColorProcessor(w, h);

		int[] pixels = (int[]) ip.getPixels();
		int i = 0;
		for (int y = 0; y < h; y++) {
			int red = (y * 255) / (h - 1);
			for (int x = 0; x < w; x++) {
				int blue = (x * 255) / (w - 1);
				pixels[i++] = (255 << 24) | (red << 16) | blue;
			}
		}
		new ImagePlus("Red and Blue", ip).show();

	}

}

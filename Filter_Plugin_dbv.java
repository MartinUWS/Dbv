package plugins.Dbv;

import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.filter.*;

public class Filter_Plugin_dbv implements PlugInFilter {
	public static ImageProcessor iproc;
	ImagePlus imp;

	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return DOES_ALL;
	}

	public void run(ImageProcessor ip) {
		this.iproc = ip;
		a_inv();
		for (int i = 0; i < ip.getWidth(); i++) {
			ip.putPixel(i,i,200);
		}
		
	}

	private void a_inv() {
		this.iproc.invert();
		imp.updateAndDraw();
	}

	private void b_grayExch() {
		this.iproc.invert();
		int width = iproc.getWidth();
		int height = iproc.getHeight();
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; i++) {
				float value = iproc.getPixelValue(j, i);
				if (value > 120 && value < 130 ) {
					iproc.putPixel(j,i,0);
				}
			}
		}
	}

}

import ij.*;
import ij.plugin.filter.Convolver;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;
import java.awt.*;

import ij.plugin.ChannelSplitter;
import ij.plugin.PlugIn;
import ij.gui.GenericDialog;
import java.util.Arrays;

public class Ampel_ implements PlugInFilter {

	//private ImageProcessor ipTemp;
	//private Convolver convolver = new Convolver();
	//private float[] fKernel = {-1,-1,-1,-1,12,-1,-1,-1,-1};
	private ImagePlus image;
	private ImagePlus[] rgb = new ImagePlus[3];
	private ChannelSplitter cs = new ChannelSplitter();

	public int setup(String arg, ImagePlus imp) {
		return DOES_ALL + DOES_STACKS + SUPPORTS_MASKING;
	}

	public void run(ImageProcessor ip) {
		
		image = new ImagePlus("original" , ip.duplicate());
		
		
		//invert Image
		new ImagePlus("invert", invert(ip.duplicate())).show();

		//line detection
		ColorProcessor cp = new ColorProcessor(ip.createImage());
		cp.findEdges();
		
		
		boolean find = false;
		int[] pix = new int[3];
		new ImagePlus("edges", cp).show();

		IJ.log(" " + pix[0] + " " + pix[1] + " " + pix[2])
		for(int i = 0 ; i < cp.getWidth() ; i++){
			for(int j = 0 ; j < cp.getHeight() ; j +=5){
				cp.getPixel(i, j ,pix);
				if(pix[0] == 255 || (pix[1] > 200 && pix[2] > 200)){
					//IJ.log(" " + pix[0] + " " + pix[1] + " " + pix[2]);
									
				}
			}
		}
		
		//get RGB-channels
		image = new ImagePlus("rgb", ip.duplicate());
		rgb = cs.split(image);
		rgb[0].show();
		
		
		
		
		//boolean b = convolver.convolve(ip, fKernel, 4, 4);
		//IJ.log(Boolean.toString(b));
		//convert to 8Bit
		
		//setROI
		
		
	
	} 

	//Invert Image
	private ImageProcessor invert(ImageProcessor ip) {
		for (int y = 0; y < ip.getHeight(); y++)
			for (int x = 0; x < ip.getWidth(); x++)
				ip.set(x, y, ~ip.get(x, y));
		return ip;
	}

}

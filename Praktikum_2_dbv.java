import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import java.awt.image.IndexColorModel;

import ij.plugin.filter.*;


public class Praktikum_2_dbv implements PlugInFilter {
	public static ImageProcessor iproc;
	ImagePlus imp;

	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return DOES_ALL;
	}

	public void run(ImageProcessor ip) {
		this.iproc = ip;


		String [] operations = new String[] {"Invert", "Exchange Gray", "Reduce", "Add Marker"};

		GenericDialog gd = new GenericDialog("New Image");
		gd.addMessage("Choose two Images for Pserie 1.4 - DBV");
		//gd.addChoice("Base Image:", allimages, firstName);
		//gd.addChoice("Diff Image:", allimages, secName);
		gd.addStringField("Coods", "20,30;50,60;30,120;150,90");
		gd.addChoice("Operation:", operations, operations[0]);
		gd.showDialog();

		if (gd.wasCanceled()) return;

		String coord = gd.getNextString();
		String operation = gd.getNextChoice();

		int operationIndex = 0;
		for (String s : operations ) {
			if (s.equals(operation))
				break;

			operationIndex ++;
		}

		switch (operationIndex) {
			case 0:
				a_inv();
				break;
			case 1:
				b_grayExch();
				break;
			case 2:
				c_redux();
				break;
			case 3:
				String [] coords = coord.split(";");
				f_marker(coords);
				break;
			case 4:
				break;
			default:
		}
	}

	private void a_inv() {
		this.iproc.invert();
	}

	private void b_grayExch() {

		for (int y = 0; y < iproc.getHeight(); y++) {
			for (int x = 0; x < iproc.getWidth(); x++) {

				int val = iproc.get(x,y);

				if (val > 120 && val < 130 ) {
					iproc.putPixel(x,y,0);
				}
			}
		}
		imp.updateAndDraw();
	}

	private void c_redux() {

		int [] colors = new int [255];
		for (int y = 0; y < iproc.getHeight(); y++) {
			for (int x = 0; x < iproc.getWidth(); x++) {
				int val = iproc.get(x,y);
				colors[val] = colors[val] + 1;

				//System.out.print(val+",");
			}
			//System.out.println();
		}

		int nrPixels = iproc.getHeight() * iproc.getWidth();
		int part = nrPixels / 5;
		int [] borders = new int [5];

		int count = 0;
		int amoount = 0;
		for (int z = 0; z < colors.length; z++) {
			int x = colors[z];
			amoount += x;
			if (x > 0)
				System.out.println(x + "; " + part + "; " + amoount);
			if (amoount > part) {
				borders[count] = z;
				System.out.println(borders[count]);
				amoount = 0;
				count ++;
			}
		}

		for (int y = 0; y < iproc.getHeight(); y++) {
			for (int x = 0; x < iproc.getWidth(); x++) {

				int val = iproc.get(x,y);
				int last = 0;
				for (int z = 0; z < borders.length; z++) {
					if (val < borders[z] && val >= last) {
						iproc.putPixel(x,y, last);
					}
					last = borders[z];
				}
			}
		}

		imp.updateAndDraw();
	}

	private void f_marker(String [] coords) {
		int nrOfCords = coords.length;
		int [][] markerCoord = new int[nrOfCords - 1][2];
		for (int i = 0; i < coords.length-1; i++) {
			System.out.println(coords[i]);
			String [] numbers = coords[i].split(",");
			markerCoord[i][0] = Integer.parseInt(numbers[0]);
			markerCoord[i][1] = Integer.parseInt(numbers[1]);
		}

		for (int i = 0; i < nrOfCords -1; i++) {
			iproc.putPixel(markerCoord[i][0], markerCoord[i][1], intColor(255,0,0,255));
			iproc.drawString(i+".", markerCoord[i][0] -2, markerCoord[i][1] -2);
		}

	}


	private int intColor(int red, int green, int blue, int alpha) {
		int color = (alpha << 24) | (red << 16) | (green << 8) | (blue);
		return color;
	}
}

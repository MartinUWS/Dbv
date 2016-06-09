import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.ImageCalculator;
import ij.plugin.PlugIn;
import ij.plugin.Straightener;
import ij.process.ColorProcessor;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;
import ij.WindowManager;

/**
 * Created by max on 22.05.16.
 * Schritte der Aufgabe 1.4 als Plugin
 *
 * A or B -> Invert -> Diff Green screen (green substract result)
 * Weiß drauflegen: Max der beiden
 */
public class OperationMarker_dbv implements PlugIn {

	static ImagePlus baseImage;
	static ImagePlus diffImage;
	@Override
	public void run(String arg) {

		//String [] allimages = new String [WindowManager.getWindowCount()-1];
		//for (int i = 0; i < WindowManager.getWindowCount(); i++) {
		//	ImagePlus img = WindowManager.getImage(WindowManager.getNthImageID(i));
		//	allimages[i] = img.getTitle();
		//}

		String [] allimages = WindowManager.getImageTitles();

		String firstName = "", secName = "";
		if (allimages.length >= 2) {
			firstName = allimages[0];
			secName = allimages[1];
		}

		String [] operations = new String[] {"Union", "Intersection", "Complement", "SymmDiff", "Minimum"};

		GenericDialog gd = new GenericDialog("New Image");
		gd.addMessage("Choose two Images for Pserie 1.4 - DBV");
		gd.addChoice("Base Image:", allimages, firstName);
		gd.addChoice("Diff Image:", allimages, secName);
		gd.addChoice("Operation:", operations, operations[0]);
		gd.showDialog();

		if (gd.wasCanceled()) return;

		firstName = gd.getNextChoice();
		secName = gd.getNextChoice();
		String operation = gd.getNextChoice();

		baseImage = WindowManager.getImage(firstName);
		diffImage = WindowManager.getImage(secName);

		int operationIndex = 0;
		for (String s : operations ) {
			if (s.equals(operation))
				break;

			operationIndex ++;
		}

		switch (operationIndex) {
			case 0:	// Union - A or B
				execOperation("OR create", true);
				break;
			case 1:	// Intersection - A and B
				execOperation("AND create", true);
				break;
			case 2:	// Complement - A but not B
				execComplement();
				break;
			case 3:	// SymmDiff - A or B, but not both
				execOperation("XOR create", true);
				break;
			case 4:
				execOperation("Min create", false);
				break;
			default:
		}
	}

	private void prepareImages() {
		ImageConverter icbase = new ImageConverter(baseImage);
		icbase.convertToGray8();

		ImageConverter icdiff = new ImageConverter(diffImage);
		icdiff.convertToGray8();
	}

	private void execOperation(String operation, boolean colorIn) {
		ImageCalculator ic = new ImageCalculator();
		ImagePlus result = ic.run(operation, baseImage, diffImage);

		result.show();

		if (colorIn)
			showDiff(result);
	}

	private void execComplement() {
		ImageCalculator ic = new ImageCalculator();
		ImagePlus intersection = ic.run("AND create", baseImage, diffImage);
		ImagePlus result = ic.run("Substract create", baseImage, intersection);

		result.show();

		showDiff(result);
	}


	private void showDiff(ImagePlus result) {
		ColorProcessor ip = new ColorProcessor(result.getImage());
		ip.invert();

		ImagePlus invertedImg = new ImagePlus("Inv", ip);

		ImageCalculator ic = new ImageCalculator();
		ImagePlus diff = ic.run("Subtract create", greenImage(result.getWidth(), result.getHeight()), invertedImg);

		diff.show();
	}

	private ImagePlus greenImage(int w, int h) {

		ImageProcessor ip = new ColorProcessor(w, h);
		int[] pixels = (int[]) ip.getPixels();
		int i = 0;
		int color = intColor(0, 255, 0, 255);
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				pixels[i++] = color;
			}
		}

		return new ImagePlus("Green", ip);
	}

	private int intColor(int red, int green, int blue, int alpha) {
		int color = (alpha << 24) | (red << 16) | (green << 8) | (blue);
		return color;
	}

	private void log(String message) {System.out.println(message);}

}

package gui;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.*;
import java.io.IOException;
import java.io.Serializable;

public class TransferableUtils {

	public static void copy(Image image) {
		
		ImageTransferable transferable = new ImageTransferable(image);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(transferable, null);
	}

	static class ImageTransferable implements Transferable {
		private Image image;

		public ImageTransferable (Image image) {
			this.image = image;
		}

		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
			if (isDataFlavorSupported(flavor)) {
				return image;
			}
			else {
				throw new UnsupportedFlavorException(flavor);
			}
		}

		public boolean isDataFlavorSupported (DataFlavor flavor){
			return flavor.equals(DataFlavor.imageFlavor);
		}

		public DataFlavor[] getTransferDataFlavors () {
			return new DataFlavor[] { DataFlavor.imageFlavor};
		}
	}
	
	public static void copyObject(Serializable object) {
		ObjectTransferable transferable = new ObjectTransferable(object);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(transferable, null);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(transferable, null);
		
	}
	
	public static DataFlavor objectDataFlavor = new DataFlavor(Serializable.class, "Object");
	
	public static class ObjectTransferable implements Transferable {
		
		private Serializable object;
		
		public ObjectTransferable(Serializable object) {
			this.object = object;
		}

		@Override
		public DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[] {objectDataFlavor};
		}

		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return flavor.equals(objectDataFlavor);
		}

		@Override
		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
			return object;
		}
	}
}

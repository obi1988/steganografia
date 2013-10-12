package pl.mat.umk.steganografia;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.FileChooser;
import pl.mat.umk.steganografia.files.Bitmap;

public class PictureAction {

	private PictureData data = new PictureData();
	private byte [] byteText;
	SliderActionListener sliderAction;

	public void setByteText(byte[] byteText) {
		this.byteText = byteText;
		isZaladowanyText = true;
	}

	private File file;
	private Bitmap bmp = new Bitmap();
	boolean isZaladowanyObraz = false;
	boolean isZaladowanyText = false;

	public void setFile(File file) {
		this.file = file;
		isZaladowanyObraz = true;
	}
	
	public String getFile(boolean isOpen, boolean isGraphic){
		
		FileChooser fchoose = new FileChooser();
		File f ;
		String result = "";
		fchoose.setTitle("Wybierz plik");
		fchoose.setInitialDirectory(new File(System.getProperty("user.home")+"/Desktop/"));
		
		if(isOpen){
			if(isGraphic)
				fchoose.getExtensionFilters().addAll(
	                new FileChooser.ExtensionFilter("All Images", "*.*"),
	                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
	                new FileChooser.ExtensionFilter("PNG", "*.png")
	            );	
			f = fchoose.showOpenDialog(null);
		}else{
			f = fchoose.showSaveDialog(null);
		}
		
		if(f != null){
			result = f.getAbsolutePath();
		}
		
		
		return result;
	}
	
	public void close(ActionEvent actionEvent) {
		
		Platform.exit();
		/*Node source = (Node) actionEvent.getSource();
		  Stage stage = (Stage) source.getScene().getWindow();
		  stage.close();
		*/
	}
	
	public InputStream start(){
		byte[] dataBIT = null;
		byte[] b = null;
		try {
			sliderAction.setSliders(1);
//			byte[] b = data.extractBytesImage(this.file);
//			byte[] b = data.extractBytesImage2(this.file);
			
			b = Files.readAllBytes(Paths.get(this.file.getAbsolutePath()));
//			byte []c = data.textToBinary(b);  //bajt - bit
			byte [] c = {0,0,0,1,0,0,1};
			
			byte len[] = data.bit_conversion(c.length);
			data.encode_text(b, len,  0); //0 first positiong
			dataBIT = data.encode_text(b, c, 32); //4 bytes of space for length: 4bytes*8bit = 32 bits
		
			
//			dataBIT = data.saveText(c, new byte[] {0,0,0,1,0,0,1});
			sliderAction.setSliders(2);
		
			data.setBitmap(bmp, b);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ByteArrayInputStream(dataBIT);
	}
	
	
	/**
	 * Metoda pozwala odszyfrować zaszytą w bajtach obrazu informację 
	 * @param progBar
	 * @param progIndi
	 * @return
	 */
	public InputStream odszyfruj(ProgressBar progBar, ProgressIndicator progIndi){
		InputStream im = null;
		try {
			byte[] b = data.extractBytesImage(this.file);
			byte[] z  = data.textToBinary2(b); ///tablica z bitami obrazka + txt
			byte [] wynik  = data.decode_text(b);
			
			System.out.println(wynik + " aaaaaaaaaaaaaaa");
//			im = data.toImage(b,byteText);
//			data.setBitmap(bmp, b);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return im;
	}
	
	public byte [] getFileAction(File f) throws Exception{
		
		return data.extractBytesText(f);
		
	}
	
	public void saveFile(InputStream inputStream) {

		OutputStream outputStream = null;

		try {
			inputStream.reset();
			String path = getFile(false, false);

			if (!path.equals("")) {
				outputStream = (OutputStream) new FileOutputStream(new File(path));
			}

			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = inputStream.read(bytes, 0, 1024)) != -1) {
				outputStream.write(bytes, 0, read);
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public void setSliderListener(SliderActionListener listener){
		this.sliderAction = listener; 
	}
	
}

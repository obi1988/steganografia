package pl.mat.umk.steganografia;

import java.io.File;
import java.io.InputStream;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class Controller implements SliderActionListener{
// 	private static final Logger log = LoggerFactory.getLogger(Controller.class);

	private PictureAction action = new PictureAction();
	private InputStream input = null;
	
	@FXML 
	Button btnZamknij;
	@FXML
	ImageView imgView;
	@FXML
	ProgressBar progress;
	@FXML
	MenuItem menuChoose;
	@FXML
	MenuItem menuClose;
	@FXML
	MenuItem menuStart;
	@FXML
	MenuItem menuClear;
	@FXML
	MenuItem menuOdszyfruj;
	@FXML
	MenuItem menuSave;
	@FXML
	CheckBox checkObraz;
	@FXML
	CheckBox checkText;
	@FXML
	ProgressBar progBar;
	@FXML
	ProgressIndicator progIndi;
	
	
	@FXML
	public void actionClose(ActionEvent actionEvent) {

		action.close(actionEvent);
	}
	
	@FXML
	public void loadPicture(MouseEvent actionEvent){
		progress.setProgress(1);
		
	}
	
	@FXML
	public void actionMenuChoose(ActionEvent actionEvent){

		File f = new File(action.getFile(true,true));
		Image img = new Image(f.toURI().toString());
		imgView.setImage(img);
		action.setFile(f);
		
		checkObraz.setSelected(true);
	}
	
	@FXML
	public void actionMenuChooseText(ActionEvent actionEvent){
		
		try{
			File f = null;
			String path = action.getFile(true,false);
			if(path != null && !path.equals("")){
				f = new File(path);
				byte [] b; 
				b = action.getFileAction(f);
				action.setByteText(b);
			}else{
				return;
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		checkText.setSelected(true);
	}
	
	@FXML
	public void actionMenuStart(ActionEvent actionEvent) {

		action.setSliderListener(this);		 
		if(!action.isZaladowanyObraz ){
			Dialog.showWarning("Ostrzeżenie", "Plik z obrazem nie został jeszcze wczytany.");
			return;
		}else if(!action.isZaladowanyText){
			Dialog.showWarning("Ostrzeżenie", "Plik z tekstem nie został jeszcze wczytany.");
			return;
		}
		InputStream b = action.start();
		imgView.setImage(new Image(b));
		this.input = b; 

 }
	
	
	@FXML
	public void actionMenuOdszyfruj(ActionEvent actionEvent) {

		InputStream b = action.odszyfruj(progBar, progIndi);
		imgView.setImage(new Image(b));
 }
	
	@FXML
	public void actionMenuClose(ActionEvent actionEvent) {
		imgView.setImage(null);
 }
	
	@FXML
	public void actionMenuSave(ActionEvent actionEvent) {
		
		if(this.input == null){
			Dialog.showWarning("Ostrzeżenie", "Prosze o wczytanie pliku z obrazem oraz tekstem.");
			return;
		}
		action.saveFile(this.input);
 }

	public void setSliders(int values) {
		progBar.setProgress(values);
		progIndi.setProgress(values); 		
	}
	
}

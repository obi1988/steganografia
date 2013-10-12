package pl.mat.umk.steganografia;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.BitSet;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import pl.mat.umk.steganografia.files.Bitmap;

public class PictureData {
	
	
	/**
	 * Pobranie rozszerzenia pliku 
	 * 
	 * @param file scieżka do pliku
	 * @return 
	 */
	private String getExtension(File file){
		String extension = "";
		String fileName = file.getName();
		int i = fileName.lastIndexOf('.');
		int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));

		if (i > p) {
		    extension = fileName.substring(i+1);
		}
		return extension;
	}
	
	/**
	 * Zamiana obrazka źródłowego na tablice bajtów
	 * @param imgPath ścieżka do obrazka źródłowego
	 * @return tablica bajtów
	 * @throws IOException
	 */
	public byte[] extractBytesImage(File imgPath) throws IOException {

	    Path path = Paths.get(imgPath.getAbsolutePath());
		return Files.readAllBytes(path);
	}
	
	public byte[] extractBytesImage2(File imgPath) throws IOException {

	    Path path = Paths.get(imgPath.getAbsolutePath());
	    Scanner sc = new Scanner(imgPath);
		byte[] b = new byte[(int)imgPath.length()*4];
		int count = 0;
		
		while(sc.hasNext()){

				String s = new String(sc.next());
//				for(int j=0; j< s.length() -1; j++){
//					byte [] d = textToBinary(s.getBytes());
					byte [] d = textToBinary(s.getBytes());
					
					for(int k=0; k< d.length ; k++){
//						b[count] = d[k] == true ? (byte)1 : (byte)0;
						b[count] = d[k];
						count++;
					}
//				}
			}
		sc.close();
	    
//		return Files.readAllBytes(path);
		return b;
	}
	
	
	/**
	 * Metoda pozwala przekonwertowac dowolny plik na tablice bajtów
	 * @param file plik z danymi do ukrycia
	 * @return tablica bajtów
	 * @throws Exception
	 */
	public byte[] extractBytesText(File file) throws Exception{
		
		String extension = getExtension(file);
		Scanner sc = new Scanner(file);
		byte[] b = new byte[(int)file.length()*4];
		int count = 0;
		
		while(sc.hasNext()){

				String s = new String(sc.next());
//				for(int j=0; j< s.length() -1; j++){
//					byte [] d = textToBinary(s.getBytes());
					byte [] d = textToBinary(s.getBytes());
					
					for(int k=0; k< d.length ; k++){
//						b[count] = d[k] == true ? (byte)1 : (byte)0;
						b[count] = d[k];
						count++;
					}
//				}
				
			}
		sc.close();

		if(!extension.equals("txt")){
			throw new Exception("Plik nie jest plikiem tekstowym.");
		}
		return b;
	}
	
	
	/**
	 * Konwersja z tablicy bajtów do obrazu
	 * @param imageInByte tablica bajtów
	 * @return obiekt typu InputStream
	 * @throws Exception
	 */
	public InputStream toImage(byte [] imageInByte, byte [] byteTxt) throws Exception {

		InputStream inputStream = new ByteArrayInputStream(saveText(imageInByte, byteTxt));

	        return inputStream;
	}
	
	
	public String getText(byte[] array){
		
		String result = "";
		int count = 0;
		int dlNaglowka = 54;
		byte[] c = new byte[8];
		byte [] liczby = new byte[(array.length/8)-1];


//		for(int i = 0; i < dlNaglowka+ (array.length/8)-8; i = i +8){
		for(int i = 0; i < dlNaglowka+ (array.length)-8; i = i +8){
			if(count < 7){
				c[count] = array[i+count];
				count++;
			}else{
				System.out.println(new String(c));
				count =0;
			}
//			liczby[i] = Integer.getInteger(new String(c));
		}
		
		/*for(int i=0; i< liczby.length -1; i++){
			result += new String(liczby[i]);
		}*/
		
		return result;
	}
	
	/**
	 * Ukryie bajtów w oryginalnym obrazie
	 * 
	 * @param imgByte 
	 * @return
	 */
	public byte[] saveText(byte[] imgByte, byte [] text){
		byte [] imgByteC = imgByte;
		int dlNaglowka = 54;
		for(int i = 0; i<text.length - 1; i++){
		
			imgByteC[i + dlNaglowka] |= (text[i] << 0); // set a bit to 1
			imgByteC[i + dlNaglowka] |= (text[i+1] << 1); 
			/*imgByteC[i] |= (1 << 2); 
			imgByteC[i] |= (1 << 4); 
			imgByteC[i] |= (1 << 3);*/ 

		}
		
		return imgByteC;
	}
	
	
	/**
	 * Ustawianie obiektu Bitmapy
	 * @param bmp
	 * @param b
	 * @return
	 */
	
	public Bitmap setBitmap(Bitmap bmp, byte[] b){
		
		bmp.setSygnatura( new String(new byte[]{b[0],b[1]}));
		bmp.setSize(getLongFromByte(new byte[]{b[2],b[3],b[4],b[5]},4));
		bmp.setHere_data_image(getLongFromByte(new byte[]{b[9],b[10],b[11],b[12]},4));
		bmp.setSize_header(getLongFromByte(new byte[]{b[13],b[14],b[15],b[16]},4));
		bmp.setVertical(getLongFromByte(new byte[]{b[17],b[18],b[19],b[20]},4));
		bmp.setHeight(getLongFromByte(new byte[]{b[21],b[22],b[23],b[24]},4));
		bmp.setCount_colors_in_pallet(getLongFromByte(new byte[]{b[25],b[26]},2));
		bmp.setCount_bit_on_pixel(getLongFromByte(new byte[]{b[27],b[28]},2));
		bmp.setKinf_of_compress(getLongFromByte(new byte[]{b[29],b[30],b[31],b[32]},4));
		bmp.setSize_image(getLongFromByte(new byte[]{b[33],b[34],b[355],b[36]},4));
		
		try{
			System.out.println(bmp.getSygnatura() + " rozmiar " + bmp.getKinf_of_compress());
			
			byte [] zb = new String("Michal").getBytes();
			
			
			String s2 = String.format("%8s", Integer.toBinaryString(zb[0] & 0xFF)).replace(' ', '0');
			System.out.println(s2);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return bmp;
	}
	

	/**
	 * Zamiana bajtów na ciąg bitów
	 * @param array 
	 * @return 
	 */
	public byte[] textToBinary(byte [] array){
		

		BitSet bs = new BitSet(8);
		
		byte [] wynik = new byte[array.length*8];
		String result = "";
//		for(int i=0; i< (array.length > 1000 ? 1000 : array.length); i = i+8){
		for(int i=0; i< (array.length > 1000 ? 1000 : array.length); i++){
			/*for(int j=0;j < 8; j++){
				wynik[i] = String.format("%8s", Integer.toBinaryString(array[i] & 0xFF)).replace(' ', '0').getBytes()[0];
			} ne dziala poprawnie*/
			
			result += String.format("%8s", Integer.toBinaryString(array[i] & 0xFF)).replace(' ', '0');
		}
		for(int i=0; i<result.length(); i++){
			wynik[i] = result.charAt(i) == '0' ? (byte)0 : 1;
		}
		
		return wynik;
	}
	
	
	public byte[] textToBinary2(byte... bs) {
		byte[] result = new byte[Byte.SIZE * bs.length];
		int offset = 0;
		for (byte b : bs) {
			for (int i = 0; i < Byte.SIZE; i++)
				result[i + offset] = (b >> i & 0x1) != 0x0 ? (byte)0 : 1;
			offset += Byte.SIZE;
		}
		return result;
	}

	
	
	/**
	 * Konwersja z byte na long
	 * 
	 * @param b tablica byte
	 * @param size ilosc elementów w tablicy
	 * @return liczba powstała z zamiany bajtów
	 */
	private long getLongFromByte(byte [] b, int size){
		
		long n = 0;
		
		if(size >= 1){
			n = (int)b[0];
		}
		if(size >= 2){
			n += (int)b[1] * Bitmap.TO_LONG_FIRST_BYTE;
		}
		if(size == 4){
			n += (int)b[2] * Bitmap.TO_LONG_SECOND_BYTE;
			n += (int)b[3] * Bitmap.TO_LONG_THIRD_BYTE;
		}
		
		return n;
		
	}
	
	
	public byte[] decode_text(byte[] image)
	{
		int length = 0;
		int offset = 32;
		for(int i=0; i<32; ++i)
		{
			length = (length << 1) | (image[i] & 1);
		}
		byte[] result = new byte[length+54];
		for(int b=54; b<result.length; ++b )
		{
			for(int i=0; i<8; ++i, ++offset)
			{
				if(result.length < 409565)
				result[b] = (byte)((result[b] << 1) | (image[offset] & 1));
			}
		}
		return result;
	}

	public byte[] encode_text(byte[] image, byte[] addition, int offset)
	{
		System.out.println("aaaaaaaaaaa " + offset);
		if(addition.length + offset > image.length)
		{
			throw new IllegalArgumentException("File not long enough!");
		}
		for(int i=54; i<addition.length; ++i) //nie modyfikuje naglowka
	 	{
			int add = addition[i];
			for(int bit=7; bit>=0; --bit, ++offset)
			{
		   	 	int b = (add >>> bit) & 1;
		   		 image[offset] = (byte)((image[offset] & 0xFE) | b );
			}
		}
		return image;
	}


	/*
	 *Gernerates proper byte format of an integer
	 *@param i The integer to convert
	 *@return Returns a byte[4] array converting the supplied integer into bytes
	 */
	public byte[] bit_conversion(int i)
	{
		//originally integers (ints) cast into bytes
		//byte byte7 = (byte)((i & 0xFF00000000000000L) >>> 56);
		//byte byte6 = (byte)((i & 0x00FF000000000000L) >>> 48);
		//byte byte5 = (byte)((i & 0x0000FF0000000000L) >>> 40);
		//byte byte4 = (byte)((i & 0x000000FF00000000L) >>> 32);
		
		//only using 4 bytes
		byte byte3 = (byte)((i & 0xFF000000) >>> 24); //0
		byte byte2 = (byte)((i & 0x00FF0000) >>> 16); //0
		byte byte1 = (byte)((i & 0x0000FF00) >>> 8 ); //0
		byte byte0 = (byte)((i & 0x000000FF)	   );
		//{0,0,0,byte0} is equivalent, since all shifts >=8 will be 0
		return(new byte[]{byte3,byte2,byte1,byte0});
	}
	public BufferedImage add_text(BufferedImage image, String text)
	{
		//convert all items to byte arrays: image, message, message length
		byte img[]  = get_byte_data(image);
		byte msg[] = text.getBytes();
		byte len[]   = bit_conversion(msg.length);
		try
		{
			encode_text(img, len,  0); //0 first positiong
			encode_text(img, msg, 32); //4 bytes of space for length: 4bytes*8bit = 32 bits
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(null, "Target File cannot hold message!", "Error",JOptionPane.ERROR_MESSAGE);
		}
		return image;
	}
	private byte[] get_byte_data(BufferedImage image)
	{
		WritableRaster raster   = image.getRaster();
		DataBufferByte buffer = (DataBufferByte)raster.getDataBuffer();
		return buffer.getData();
	}
	
	public BufferedImage getImage(String f)
	{
		BufferedImage 	image	= null;
		File 		file 	= new File(f);
		
		try
		{
			image = ImageIO.read(file);
		}
		catch(Exception ex)
		{
			JOptionPane.showMessageDialog(null, 
				"Image could not be read!","Error",JOptionPane.ERROR_MESSAGE);
		}
		return image;
	}
}

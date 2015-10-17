import java.io.FileNotFoundException;
import java.io.IOException;

public class Driver {

	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
		
		for(int i=0; i<=21; i++){
			String path;
			if(i < 10){
				path = "data/reut2-00" + i + ".sgm";
			}
			else{
				path = "data/reut2-0" + i + ".sgm";
			}
			DocumentProcessor dp = new DocumentProcessor();
			dp.readDocument(path);
		}
		
		DocumentProcessor.runDic();
		
	}
}

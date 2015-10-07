import java.io.FileNotFoundException;
import java.io.IOException;

public class Driver {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		
		String path = "data/reut2-000.sgm";
		DocumentProcessor dp = new DocumentProcessor();
		dp.readDocument(path);

	}

}

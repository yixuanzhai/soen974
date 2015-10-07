import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.StringTokenizer;

public class DocumentProcessor {
	
	private HashMap dic = new HashMap();
	private int documentNumber;
	private boolean inDcoument = false;
	private Stemmer s = new Stemmer();
	
	public void readDocument(String path) throws FileNotFoundException, IOException{
		String line;
		try (
		    InputStream fis = new FileInputStream(path);
		    InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
		    BufferedReader br = new BufferedReader(isr);
		) {
		    while ((line = br.readLine()) != null) {
		    	lineProcessor(line);
		    }
		}
	}
	
	private void lineProcessor(String line){
		if(line.contains("NEWID=")){
			int index = line.indexOf("NEWID=");
			String title = line.substring(index + 7, line.length() - 2);
			documentNumber = Integer.parseInt(title);
		}
		if(line.contains("<BODY>")){
			inDcoument = true;
			int index = line.indexOf("<BODY>");
			line = line.substring(index + 6);
			
			StringTokenizer st = new StringTokenizer(line);
		    while (st.hasMoreTokens()) {
		    	tokenProcess(st.nextToken());
		    }
			
		}
		else if(line.contains("</BODY>")){
			inDcoument = false;
		}
		else{
			if(inDcoument == true){
				StringTokenizer st = new StringTokenizer(line);
			    while (st.hasMoreTokens()) {			    	
			    	tokenProcess(st.nextToken());
			    }
			}		
		}
	}
	
	private void tokenProcess(String token){
		token = token.replaceAll("[^a-zA-Z ]", "").toLowerCase();
		s.add(token.toCharArray(),token.length());
		s.stem();
		token = s.toString();
		if(!StopWords.stopWords.contains(token)){
			System.out.println(token);
		}	
	}
	
	
}

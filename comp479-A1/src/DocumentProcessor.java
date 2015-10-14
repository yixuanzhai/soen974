import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

public class DocumentProcessor {

	public final static String stopWords = "a about above after again against all am an and any are aren't as at be because been before being below between both but by can't cannot could couldn't did didn't do does doesn't doing don't down during each few for from further had hadn't has hasn't have haven't having he he'd he'll he's her here here's hers herself him himself his how how's i i'd i'll i'm i've if in into is isn't it it's its itself let's me more most mustn't my myself no nor not of off on once only or other ought our ours ourselves out over own same shan't she she'd she'll she's should shouldn't so some such than that that's the their theirs them themselves then there there's these they they'd they'll they're they've this those through to too under until up very was wasn't we we'd we'll we're we've were weren't what what's when when's where where's which while who who's whom why why's with won't would wouldn't you you'd you'll you're you've your yours yourself yourselves";

	private TreeMap<String, ArrayList<Integer>> dic = new TreeMap<String, ArrayList<Integer>>();
	private int documentNumber;
	private boolean inDcoument = false;
	private Stemmer s = new Stemmer();

	private ArrayList<String> tmpPathHolder = new ArrayList<String>();
	private int tmpFileCounter = 0;
	private boolean mergeRequired = false;

	public void readDocument(String path) throws FileNotFoundException, IOException, ClassNotFoundException{
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
		//merge blocks in disk
		if(mergeRequired == true){
			dic = blockRetrieval();
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
		//to lower case and remove unnecessary characters
		token = token.replaceAll("[^a-zA-Z ]", "").toLowerCase();
		//stemming
		s.add(token.toCharArray(),token.length());
		s.stem();
		token = s.toString();
		//remove stop words
		if(!stopWords.contains(token)){
			tokenSPIMI(token);
		}	
	}

	private void tokenSPIMI(String token){
		if(!(Runtime.getRuntime().freeMemory() < 1)){
			if(!dic.containsKey(token)){				
				ArrayList<Integer> list = new ArrayList<Integer>();
				list.add(documentNumber);
				dic.put(token, list);
				//System.out.println(token);
			}
			else{
				ArrayList<Integer> list = dic.get(token);
				//no duplicate document number in the posting list
				if(!list.contains(documentNumber)){
					list.add(documentNumber);
				}			
				dic.put(token, list);
				//System.out.println(token);
			}
		}
		else{
			try{
				mergeRequired = true;
				tmpFileCounter++;
				//different path for different computers!!!
				String path = "D:\\my projects\\comp479-A1\\tmp\\tmp" + tmpFileCounter + ".ser";
				FileOutputStream fileOut =
						new FileOutputStream(path);
				ObjectOutputStream out = new ObjectOutputStream(fileOut);
				out.writeObject(dic);
				out.close();
				fileOut.close();
				tmpPathHolder.add(path);
				//empty the dictionary
				dic.clear();
			}
			catch(IOException i){
				i.printStackTrace();
			}
		}
	}

	private TreeMap<String, ArrayList<Integer>> blockRetrieval() throws ClassNotFoundException{
		TreeMap<String, ArrayList<Integer>> tmp = new TreeMap<String, ArrayList<Integer>>();
		TreeMap<String, ArrayList<Integer>> tmp2 = new TreeMap<String, ArrayList<Integer>>();
		
		for(int k=1; k<=tmpFileCounter; k++){
			try{
				//different path for different computers!!!
				String path = "D:\\my projects\\comp479-A1\\tmp\\tmp" + k + ".ser";
				FileInputStream fileIn = new FileInputStream(path);
				ObjectInputStream in = new ObjectInputStream(fileIn);
				tmp2 = (TreeMap<String, ArrayList<Integer>>) in.readObject();
				//merge tmp2 to tmp
							
				Set<String> s = tmp2.keySet();
				for(String str : s){
					if(tmp.containsKey(str)){
						ArrayList list = tmp.get(str);
						ArrayList list2 = tmp2.get(str);
						list.addAll(list2);
						tmp.replace(str, list);
						System.out.println(str);
					}
					else{
						ArrayList list2 = tmp2.get(str);
						tmp.put(str, list2);
						System.out.println(str);
					}
				}
				
				in.close();
				fileIn.close();
			}
			catch(IOException i){
				i.printStackTrace();
			}
		}
		return tmp;
	}
}

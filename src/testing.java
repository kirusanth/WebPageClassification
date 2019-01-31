import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class testing {

    public static void main(String[] args) throws IOException {
	File readDirectory = new File("data/test/course");
        String[] files = readDirectory.list();
        int[] fileNum = new int[files.length];
        Arrays.sort(files);
        String titleRegex = "<title>(.*)</title>";
        String headerRegex = "<h2>.*prof.*</h2>";
        String prof1Regex = "<title>.*([a-z]{0,6}\\s?[0-9]{1,5})[^<]*</title>";
        String redirectRegex= "<TITLE>.*301.*</TITLE>";
        String courseRegex= "course";
        String profRegex= "phd";
        String prof2Regex= "prof|department|candidates|<TITLE>.*301.*</TITLE>";
        
	Pattern titlePattern = Pattern.compile(titleRegex, Pattern.CASE_INSENSITIVE);
	Pattern headerPattern = Pattern.compile(headerRegex, Pattern.CASE_INSENSITIVE);
	Pattern profPattern = Pattern.compile(prof1Regex, Pattern.CASE_INSENSITIVE);
	int count=0;
	int successCount=0;
	for (String file : files){
		BufferedReader fr = new BufferedReader(new FileReader(new File("data/test/course/"+file)));
		String str="";
		String line="";
		while((str=fr.readLine())!=null) {
		    line+=str;
		}
		Matcher matcherTitle = titlePattern.matcher(line);
		if(matcherTitle.find()){
		    //fileNum[count]=1;
		    //successCount++;
		   // System.out.println(matcherTitle.group(1));
		}

		Matcher matcherHeader = headerPattern.matcher(line);
		Matcher matcherprof= profPattern.matcher(line);
		if(matcherprof.find()){
		   fileNum[count]=1;
		    successCount++;
		    //System.out.println(file);
		}
		//if(){
		  //  fileNum[count]=1;
		    //successCount++;
		    //System.out.println(file);
		//}
		if(fileNum[count]!=1){
		 System.out.println(file);
		}
	    	count++;
	}
	System.out.println(successCount);
    }

}

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataExtractor {
    static String[] arraywords = { "a", "about", "above", "across", "after",
	    "afterwards", "again", "against", "all", "almost", "alone",
	    "along", "already", "also", "although", "always", "am", "among",
	    "amongst", "amoungst", "amount", "an", "and", "another", "any",
	    "anyhow", "anyone", "anything", "anyway", "anywhere", "are",
	    "around", "as", "at", "back", "be", "became", "because", "become",
	    "becomes", "becoming", "been", "before", "beforehand", "behind",
	    "being", "below", "beside", "besides", "between", "beyond", "bill",
	    "both", "bottom", "but", "by", "call", "can", "cannot", "cant",
	    "co", "computer", "con", "could", "couldnt", "cry", "de",
	    "describe", "detail", "do", "done", "down", "due", "during",
	    "each", "eg", "eight", "either", "eleven", "else", "elsewhere",
	    "empty", "enough", "etc", "even", "ever", "every", "everyone",
	    "everything", "everywhere", "except", "few", "fifteen", "fify",
	    "fill", "find", "fire", "first", "five", "for", "former",
	    "formerly", "forty", "found", "four", "from", "front", "full",
	    "further", "get", "give", "go", "had", "has", "hasnt", "have",
	    "he", "hence", "her", "here", "hereafter", "hereby", "herein",
	    "hereupon", "hers", "herself", "him", "himself", "his", "how",
	    "however", "hundred", "i", "ie", "if", "in", "inc", "indeed",
	    "interest", "into", "is", "it", "its", "itself", "keep", "last",
	    "latter", "latterly", "least", "less", "ltd", "made", "many",
	    "may", "me", "meanwhile", "might", "mill", "mine", "more",
	    "moreover", "most", "mostly", "move", "much", "must", "my",
	    "myself", "name", "namely", "neither", "never", "nevertheless",
	    "next", "nine", "no", "nobody", "none", "noone", "nor", "not",
	    "nothing", "now", "nowhere", "of", "off", "often", "on", "once",
	    "one", "only", "onto", "or", "other", "others", "otherwise", "our",
	    "ours", "ourselves", "out", "over", "own", "part", "per",
	    "perhaps", "please", "put", "rather", "re", "same", "see", "seem",
	    "seemed", "seeming", "seems", "serious", "several", "she",
	    "should", "show", "side", "since", "sincere", "six", "sixty", "so",
	    "some", "somehow", "someone", "something", "sometime", "sometimes",
	    "somewhere", "still", "such", "system", "take", "ten", "than",
	    "that", "the", "their", "them", "themselves", "then", "thence",
	    "there", "thereafter", "thereby", "therefore", "therein",
	    "thereupon", "these", "they", "thick", "thin", "third", "this",
	    "those", "though", "three", "through", "throughout", "thru",
	    "thus", "to", "together", "too", "top", "toward", "towards",
	    "twelve", "twenty", "two", "un", "under", "until", "up", "upon",
	    "us", "very", "via", "was", "we", "well", "were", "what",
	    "whatever", "when", "whence", "whenever", "where", "whereafter",
	    "whereas", "whereby", "wherein", "whereupon", "wherever",
	    "whether", "which", "while", "whither", "who", "whoever", "whole",
	    "whom", "whose", "why", "will", "with", "within", "without",
	    "would", "yet", "you", "your", "yours", "yourself", "yourselves﻿" };
    static List<String> stopWords = new ArrayList<String>(
	    Arrays.asList(arraywords));
    // Initialize our main Data Structure.
    // It is made up of a pair of Hashmaps inside a List structure that holds
    // the attributes and their values for a single page
    // These lists are then collected inside a final Hashmap that holds
    // information for the whole dataset.
    static HashMap<String, List<HashMap<String, Double>>> dataSet = new HashMap<String, List<HashMap<String, Double>>>();
    // This map tracks the number of pages a word is present in.
    static Map<String, Integer> collection = new TreeMap<String, Integer>();
    static boolean testData = false;

    public static void main(String[] args) throws IOException {
	// We call the extractClassFunction on the training data and store the
	// most frequent words inside a wordList set.
	Set<String> wordList = new HashSet<String>(
		extractClassData("data/train/course"));
	wordList.addAll(extractClassData("data/train/faculty"));
	wordList.addAll(extractClassData("data/train/student"));
	// This map holds the frequency values for the words in the wordList
	HashMap<String, HashMap<String, Double>> tempDataSet = RelativeFrequencyCalculator(wordList);
	// we send the tempDataSet along with the wordList to be printed into
	// the "trainingdata" file
	printTable(tempDataSet, "trainingdata", wordList);
	// we clear the dataset of the training data so it can be filled with
	// the test data
	dataSet = new HashMap<String, List<HashMap<String, Double>>>();
	testData = true;
	// The same steps are repeated for the test data as before, except this
	// time we are not creating a wordList.

	extractClassData("data/test/course");
	extractClassData("data/test/faculty");
	extractClassData("data/test/student");
	tempDataSet = RelativeFrequencyCalculator(wordList);
	// we print the testdata to file
	printTable(tempDataSet, "testdata", wordList);
    }

    private static HashMap<String, HashMap<String, Double>> RelativeFrequencyCalculator(
	    Set<String> wordList) {
	HashMap<String, HashMap<String, Double>> tempDataSet = new HashMap<String, HashMap<String, Double>>();
	// This loop will run through all the files in the dataset
	for (String file : dataSet.keySet()) {
	    // create a map that stores 1 for a word if it exists in file
	    // stores 0 for a word if it does not exist in file.
	    HashMap<String, Double> pageData = new HashMap<String, Double>();
	    for (String word : wordList) {
		try {
		    if (dataSet.get(file).get(0).get(word) > 0) {
			pageData.put(word, 1.0);
		    } else {
			pageData.put(word, 0.0);
		    }
		} catch (NullPointerException ex) {
		    pageData.put(word, 0.0);
		}
	    }
	    // we collate the pageData with the tempDataSet that will be
	    // returned when the loop finishes.
	    tempDataSet.put(file, pageData);
	}
	return tempDataSet;
    }

    private static HashMap<String, HashMap<String, Double>> IDFTermFrequencyCalculator(
	    Set<String> wordList) {
	// holds the pages and the td-idf values of the words inside them.
	HashMap<String, HashMap<String, Double>> tempDataSet = new HashMap<String, HashMap<String, Double>>();
	int totalPages = dataSet.size();
	// This loop will run through all the files in the dataset
	for (String file : dataSet.keySet()) {
	    // get the total number of words in the file
	    Double totalWords = dataSet.get(file).get(1).get("totalWords");
	    // create a map that holds the td-idf values for all wordList words
	    // in the page.
	    HashMap<String, Double> pageData = new HashMap<String, Double>();
	    for (String word : wordList) {
		// docNum will store the value of how many pages contains word.
		double docNum = collection.get(word);
		try {
		    // we calculate the td-idf value and store it with the key
		    // "word" inside pageData
		    double tdIdf = ((dataSet.get(file).get(0).get(word) / totalWords) * Math
			    .log10(totalPages / docNum));
		    pageData.put(word, tdIdf);
		} catch (NullPointerException ex) {
		    // if a page does not contain the "word", the program will
		    // receive a nullPointerException
		    // and the value will be 0.
		    pageData.put(word, 0.0);
		}
	    }

	    // we collate the pageData with the tempDataSet that will be
	    // returned when the loop finishes.
	    tempDataSet.put(file, pageData);
	}
	return tempDataSet;
    }

    private static Set<String> extractClassData(String directory)
	    throws IOException {
	// we collect the list of files from our directory
	String[] files = new File(directory).list();
	Arrays.sort(files);
	// This regex removes the information within the tags
	String regex = "(>.*?<)";
	// this regex matches a pattern seen in course page titles.
	String titleRegex = "<title>.*([a-z]{0,6}\\s?[0-9]{1,5})[^<]*</title>";
	// this regex matches a 301 page not found title.
	String redirectRegex = "<TITLE>.*301.*</TITLE>";
	Pattern tags = Pattern.compile(regex);
	Pattern titlePattern = Pattern.compile(titleRegex,
		Pattern.CASE_INSENSITIVE);
	Pattern redirectPattern = Pattern.compile(redirectRegex,
		Pattern.CASE_INSENSITIVE);
	double classNum = 0.0;
	// sets our class value
	if (directory == "data/train/student"
		|| directory == "data/test/student") {
	    classNum = 1.0;
	} else if (directory == "data/train/faculty"
		|| directory == "data/test/faculty") {
	    classNum = 2.0;
	} else if (directory == "data/train/course"
		|| directory == "data/test/course") {
	    classNum = 3.0;
	}
	// this variable holds the most frequent words in the class/directory.
	Map<String, Integer> classCollection = new TreeMap<String, Integer>();
	// this loop runs through all the files in the directory.
	for (String file : files) {
	    // we open a buffer to read the text in the html files.
	    BufferedReader fr = new BufferedReader(new FileReader(new File(
		    directory + "/" + file)));
	    // create a pair of hashmaps to store attribute values.
	    HashMap<String, Double> wordSet = new HashMap<String, Double>();
	    HashMap<String, Double> tempSet = new HashMap<String, Double>();
	    // this list will consolidate our hashmaps so they can be properly
	    // stored in the dataSet.
	    List<HashMap<String, Double>> featureSet = new ArrayList<HashMap<String, Double>>();
	    // we read the lines from the html document and store it within a
	    // single line.
	    String str = "";
	    String line = "";
	    while ((str = fr.readLine()) != null) {
		line += str + " ";
	    }
	    // we initialize our supplemental attributes to be stored in the
	    // tempSet hashmap
	    Matcher matcherTitle = titlePattern.matcher(line);
	    tempSet.put("TitlePattern", 0.0);
	    Matcher matcherRedirect = redirectPattern.matcher(line);
	    tempSet.put("RedirectPattern", 0.0);
	    tempSet.put("Class", classNum);
	    // we match our patterns and store in tempSet.
	    if (matcherTitle.find()) {
		tempSet.replace("TitlePattern", 1.0);
	    }
	    if (matcherRedirect.find()) {
		tempSet.replace("RedirectPattern", 1.0);
	    }
	    // now we start to clean up our data.
	    // first we use our tag removal pattern to remove metadata.
	    String cleanLine = "";
	    Matcher tagMatch = tags.matcher(line);
	    while (tagMatch.find()) {
		cleanLine += tagMatch.group(1) + " ";
	    }
	    // here we will turn all text to lowercase.
	    cleanLine = cleanLine.replaceAll("[<>]", "").toLowerCase();
	    int i = 0;
	    // this loop will go through all the words in the html file
	    for (String word : cleanLine.split(" ")) {
		// loop will count the number of times the doc contains the word
		if (wordSet.containsKey(word)) {
		    i++;
		    wordSet.replace(word, wordSet.get(word) + 1);
		    // this condition eliminates any "word" that is less than 2
		    // characters
		} else if (word.length() >= 2) {
		    // i counts the number of unique words
		    i++;
		    // we add the word and initialize its value to be 1 in
		    // wordSet
		    wordSet.put(word, 1.0);
		    // this section marks a word by attributing a 1 to the word
		    // in the collection and classCollection maps.
		    // this is done so we can check how many pages contains a
		    // word.
		    if (classCollection.containsKey(word)) {
			classCollection.replace(word,
				classCollection.get(word) + 1);
			if (collection.containsKey(word)) {
			    collection.replace(word, collection.get(word) + 1);
			} else {
			    collection.put(word, 1);
			}
		    } else {
			classCollection.put(word, 1);

		    }
		}
	    }
	    // here we finalize our data structure by adding the wordSet and
	    // tempSet to the featureSet list.
	    tempSet.put("totalWords", (double) i);
	    featureSet.add(wordSet);
	    featureSet.add(tempSet);
	    // featureSet list will be added to our main dataSet.
	    // this represents our data for the page
	    dataSet.put(file, featureSet);
	}
	// once all the pages have been read in the class directory,
	// we will remove words that are infrequent in the class.
	// this way we select attributes with meaning instead of bloating our
	// ARFF file with meaningless attributes.
	for (Object s : classCollection.keySet().toArray()) {
	    String word = ((String) s).toLowerCase();
	    if (classCollection.get(s) < files.length / 3
		    || stopWords.contains(word) || word.length() < 2) {
		classCollection.remove(s);
	    }
	}
	// we return the classCollection keyset to be added to a consolidated
	// wordList that contains valuable attributes
	return classCollection.keySet();
    }

    private static void printTable(
	    HashMap<String, HashMap<String, Double>> pages, String outputFile,
	    Set<String> words) throws IOException {
	// Creating a file writer to print out our data to outputFile
	FileWriter fw = new FileWriter("data/" + outputFile + ".arff");
	boolean firstRun = true;
	// loop through the list of pages in set.
	for (String page : pages.keySet()) {
	    // if this is the first run of the function, it will print out the
	    // header information for the ARFF file.
	    if (firstRun) {
		fw.write("@relation " + outputFile + "\n\n");
		fw.write("@attribute PageName string\n");
		for (String patternName : dataSet.get(page).get(1).keySet()) {
		    if (patternName == "Class") {
			fw.write("@attribute ClassName {Student, Faculty, Courses}\n");

		    } else {
			fw.write("@attribute " + patternName + " numeric\n");
		    }
		}
		for (String word : words) {
		    fw.write("@attribute " + word + " numeric\n");
		}
		fw.write("\n@data\n\n");
		firstRun = false;
	    }
	    // this part converts the numerical values to the String values for
	    // the word.
	    double i = dataSet.get(page).get(1).get("Class");
	    String className = "";
	    if (i == 1.0) {
		className = "Student";
	    } else if (i == 2.0) {
		className = "Faculty";
	    } else if (i == 3.0) {
		className = "Courses";
	    }
	    // we begin our tuple with printing the page name
	    fw.write(page);
	    // we print out our supplemental attributes like totalWords,
	    // Title Pattern, Redirect Pattern
	    for (String patternName : dataSet.get(page).get(1).keySet()) {
		if (patternName == "Class") {
		    fw.write(", " + className);

		} else {
		    fw.write(", "
			    + dataSet.get(page).get(1).get(patternName)
				    .intValue());
		}
	    }
	    // now we print out the rest of the attributes in the word list.
	    for (String word : words) {
		fw.write(", " + pages.get(page).get(word));

	    }
	    fw.write("\n");
	}
	// closing the filewriter.
	fw.close();
    }
}

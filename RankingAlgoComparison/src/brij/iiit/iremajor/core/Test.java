package brij.iiit.iremajor.core;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;


public class Test {
	
	public void intersect(int[] a, int[] b) {
	    for(int i=0; i<a.length; i++) {
	        for(int j=0; j<b.length; j++) {
	            if(a[i] == b[j]) {
	                System.out.println(a[i]+" is present in both arrays");
	                break;
	            }
	        }
	    }
	}
	
	public void intersect1(int[] a, int[] b) {
	    HashSet<Integer> hs = new HashSet<Integer>(); 
	    for (int i = 0; i < b.length; i++) {
	        hs.add(b[i]);
	    }
	     
	    for (int i = 0; i < a.length; i++) {
	        if(hs.contains(a[i])) {
	            System.out.println(a[i]+" is present in both arrays");
	        }
	    }   
	}

	public static void main(String[] args) throws IOException {
		/*String words[] = { "hello", "world", "java", "code", "example", "hello" };
		Word[] frequency = WordFrequencyCounter.getFrequentWords(words);
		for (Word w : frequency) {
			System.out.println(w.word + "=" + w.count);
		}*/
		/*File file = new File("/home/brij/Documents/IIIT/IRE/miniproj1/inverted-index/text.txt");
		RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
		long start = (new Date()).getTime();
		
		System.out.println(randomAccessFile.readLine());
		
		System.out.println((new Date()).getTime() - start);
		
		randomAccessFile.close();*/
		//System.out.println((byte)'/');
		
		/*Test t = new Test();
		
		int[] a = {3, 10, 4, 2, 8};
		int[] b = {10, 4, 12, 3, 23, 1, 8};
		
		long start = (new Date()).getTime();
		t.intersect(a, b);
		System.out.println((new Date()).getTime() - start);
		
		System.out.println(" -------------------- ");
		
		start = (new Date()).getTime();
		t.intersect1(a, b);
		System.out.println((new Date()).getTime() - start);*/
		/*
		String a = "ccc";
		int i = 1;
		char b = a.charAt(0);
		while (b == a.charAt(i)){
			b = a.charAt(i);i++;
			if (i == a.length()) break;
		}
		System.out.println(i);*/
		
		//String a = null;
		//System.out.println(Integer.parseInt(a));
		
		/*MaxentTagger tagger = new MaxentTagger("lib/english-left3words-distsim.tagger");
		String sample = "sachin pilot is a good politician";
		String tagged = tagger.tagString(sample);
		System.out.println(tagged);*/
		
		/*List<MutableInt> ints = new ArrayList<MutableInt>();
		ints.add(new MutableInt(4));
		ints.add(new MutableInt(1));
		ints.add(new MutableInt(10));
		ints.add(new MutableInt(-2));
		ints.add(new MutableInt(0));
		
		Collections.sort(ints);
		
		for (MutableInt i : ints) {
			System.out.println(i.value + "\n");
		}*/
		
		/*PageRankDocumentMapper titleMapper = new PageRankDocumentMapper();
		titleMapper.setIndexOutputFolder("/home/brij/Documents/IIIT/IRE/major/pagerank/");
		System.out.println(titleMapper.getDocumentTitle("18426501"));*/
		
		PageRankQuery pageRankQuery = new PageRankQuery("/home/brij/Documents/IIIT/IRE/major/pagerank/");
		List<DocumentScore> find = pageRankQuery.find("b:gandalf b:frodo i:tolkien");
		for (DocumentScore documentScore : find) {
			System.out.println(documentScore.getID());
		}
	}

}

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
		
		Test t = new Test();
		
		int[] a = {3, 10, 4, 2, 8};
		int[] b = {10, 4, 12, 3, 23, 1, 8};
		
		long start = (new Date()).getTime();
		t.intersect(a, b);
		System.out.println((new Date()).getTime() - start);
		
		System.out.println(" -------------------- ");
		
		start = (new Date()).getTime();
		t.intersect1(a, b);
		System.out.println((new Date()).getTime() - start);
		/*
		String a = "ccc";
		int i = 1;
		char b = a.charAt(0);
		while (b == a.charAt(i)){
			b = a.charAt(i);i++;
			if (i == a.length()) break;
		}
		System.out.println(i);*/
	}

}

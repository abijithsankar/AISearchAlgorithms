import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.io.*;

public class test {

	public static void main(String[] args) {
	String a="india";
	System.out.println(a);
	Scanner sc=new Scanner(System.in);
	System.out.println("enter c");
	String c=sc.next();
	int i,j;
	for(i=0;i<a.length();i++){
		for(j=0;j<a.length();j++){
			if(a.equals("india")){
				String b=a+c;
				if(b.equals("indiaS")){
					break;
				}
				System.out.println("baii");
			}
			System.out.println("hijihihih");
		}
		
	}
	System.out.println("gugugu");
	
	
}
}
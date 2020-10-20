/*class result: object used to capture sql data and return it to the 
 * 
 * */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Result {
	
	//public Result() {

	//}//end constructor
	
	//method used to print an array of strings
	public static void print(String ...attributes) {
		for(int i=0;i<attributes.length;i++) {
			System.out.printf("%s",attributes[i]);
		}
	}
	
	//overloaded method used to print ArrayList<String>
	public static void print(ArrayList<String[]> list, int ...space) {
		Iterator<String[]> iter = list.iterator();
		while(iter.hasNext()) {
			String[] array = iter.next();
			try {
				for(int i=0;i<array.length;i++) {
					
					if(array[i] != null)
						System.out.printf("%s",array[i].toString());
					else
						System.out.print("null");
					
					//add spacing for things to line up
					if(array[i] == null) {array[i] = "null";}
					for(int q=0;q<space[i] - array[i].length();q++) {
						System.out.print(" ");
					}
				}
			}catch(ArrayIndexOutOfBoundsException | NullPointerException e) {
				System.out.println(e+ " Result.print(ArrayList<String[]>,int[])");
			}
			System.out.println();
		}
	}

	
	public static void waitForUser() {
		@SuppressWarnings("resource")
		Scanner temp = new Scanner(System.in);
		System.out.println("\n\nPress enter to continue\n");
		temp.nextLine();
	}
	
	public static int getInput() {
		Scanner temp = new Scanner(System.in);
		int numb =0;
		try {
			String string = temp.nextLine();
			numb =  Integer.parseInt(string);
		}catch(NoSuchElementException e) {System.out.println("Invalid input.");}
			temp.close();
		
		return numb;
	}
}//end class
//51

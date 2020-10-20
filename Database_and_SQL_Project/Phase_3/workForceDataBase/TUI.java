/**class is used to display options to the user via the console
 * and take in user input to run queries and other functions 
 * */

import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class TUI {
	
	//class variables
	private Scanner input;
	private QueryResource resource;
	private ModifyResource mod;
	private BusinessProcess bs;
	private JdbcConnection jdbc;
	
	public TUI() {
		System.out.println("Connecting user interface...");
		this.jdbc = new JdbcConnection("dbsvcs.cs.uno.edu","orcl","1521","sjmarce2","7jqM3bXz");
		this.input =  new Scanner(System.in);
		System.out.println("Connecting query resource...");
		this.resource = new QueryResource(jdbc);
		System.out.println("Connecting modification resource...");
		this.mod = new ModifyResource(jdbc);
		System.out.println("Connecting business processes...\n");
		this.bs = new BusinessProcess(jdbc);
	}
	
	public void start() {
		int selection = 0;
		while(selection != 4) {
			//input = new Scanner(System.in);
			System.out.println("Welcome to the Work Force Database Text Interface");
			System.out.println("Enter a numeric choice\n1. Select a query to run.\n2. Add or delete an element from the query.\n"
								+"3. New business processes\n4. Exit program");
			try {
				selection = input.nextInt();
			}catch(NoSuchElementException e) {System.out.println("Enter a number from 1 to 4.");}
			switch(selection) {
				case 1: { queryListing(); }break;
				case 2: { modifyDatabase(); }break;
				case 3: { businessProcessing(selection); selection = 4;}break;
				case 4: { System.out.println("Exiting program");	}
			}

		}
		//separate between query list, add/delete elements or employee interaction
	}//end method
	
	
	private void queryListing() {
		int selection = 0;
		while(selection != 27) {//27 is choice for exit
			//System.out.print(CLEAR);
			System.out.printf(
					"Please enter a numeric choice.\n"+
					"1.  List a specific company’s workers by names.\n"+
					"2.  List a company’s staff by salary.\n" + 
					"3.  List the average annual pay of each company.\n"+
					"4.  List the average, maximum and minimum annual pay of  each industry.\n"+
					"5.  Find the biggest employer, indusry or industry group in terms of number of employees.\n"+
					"6.  Find the job distribution among industries by number of employees.\n"+
					"7.  Find all the job positions a person is and has worked.\n"+
					"8.  List a persons knowledge and skills.\n"+
					"9.  Show the distribution of skill of a person by number of skills in each of the cc_code.\n"+
					"10. List skills required for a position.\n"+
					"11. List the required skill categories of a job category.\n"+
					"12. List a person's missing skills for a position.\n"+
					"13. List the courses that alone teach all of the missing skills a person has for a position.\n"+
					"14. Find the cheapest course that makes up a persons skill gap for a position.\n"+
					"15. Find the position with the highest pay rate that a person is qualified for.\n"+
					"16. List all the people who are qualified for a position.\n"+
					"17. Find all people who are only missing k skills for a position.\n"+
					"18. List the person/persons who are missing the least skills for a position.\n"+
					"19. List skills and the number of people who are missing these skills for a position.\n"+
					"20. Find all people who hold or once held a position.\n"+
					"21. Queries for worker's salaries with relation to an industry group.\n"+//split into 4 choices
					"22. Find all the unemployed people who once held a job position.\n"+
					"23. Find the job categories that have the most openings due to lack of qualified workers.\n"+
					"24. Find the course sets that their combination covers a persons missing skill gap for a position.\n"+
					"25. Find the course sets that teaches every skill required by job position of the job categories found in Query #23.\n"+
					"26. List all direct and indirect courses a person has to take to be qualified for a job.\n"+
					"27. Exit query listing.\n"
					);
			try {
				selection = input.nextInt();
			}catch(NoSuchElementException e) {System.out.println("Enter a number 1 to 27");}
			//select correct directory
			if(selection == 5) { groupFive(selection); }
			else if(selection == 21) { groupTwentyOne(selection); }
			else { queryDirectory(selection);} 
		}//end loop
	}//end method queryListing
	
	//query listing and directory for query 5a-5c
	private void groupFive(int s) {
		//System.out.print(CLEAR);
		while(s != 4) {
			System.out.println(
					"1.  (5a)Find the biggest employer, indusry or industry group in terms of number of employees.\n"+
					"2.  (5b)Find the biggest industry in terms of number of employees.\n"+
					"3.  (5c)Find the biggest industry group in terms of number of employees.\n"+
					"4.  Return to query listing.\n"
					);
			try {
				s = input.nextInt();
			}catch(NoSuchElementException e) {System.out.println("Enter a number 1 to 4");}

			switch(s) {
				case 1: { invokeQuery5a(); }break;
				case 2: { invokeQuery5b(); }break;
				case 3: { invokeQuery5c(); }break;
			}
		}
	}//end method
	
	//query listing and directory for query 21a-21d
	private void groupTwentyOne(int s) {
		//System.out.print(CLEAR);
		while(s != 5) {
			System.out.println(
					"1.  (25a)Find the number of workers whose earnings increased.\n"+
					"2.  (25b)Find the number of workers whose earning decreased.\n"+
					"3.  (25c)Find the ratio of jobs increase and decrease.\n"+
					"4.  (25d)Find the average wage changing rate of workers in a specific field.\n"+
					"5.  Return to query listing.\n"
					);
			try {
				s = input.nextInt();
			}catch(NoSuchElementException e) {System.out.println("Enter a number from 1 to 5");}
			switch(s) {
				case 1: { invokeQuery21a();}break;
				case 2: { invokeQuery21b();}break;
				case 3: { invokeQuery21c();}break;
				case 4: { invokeQuery21d();}break;
			}
		}//end loop
	}
	
	//query directory for all queries except 5 and 21
	private void queryDirectory(int selection) {
		switch(selection) {
			case 1:  { invokeQuery1(); }break;
			case 2:  { invokeQuery2(); }break;
			case 3:  { invokeQuery3(); }break;
			case 4:  { invokeQuery4(); }break;
			case 6:  { invokeQuery6(); }break;
			case 7:  { invokeQuery7(); }break;
			case 8:  { invokeQuery8(); }break;
			case 9:  { invokeQuery9(); }break;
			case 10: { invokeQuery10();}break;
			case 11: { invokeQuery11();}break;
			case 12: { invokeQuery12();}break;
			case 13: { invokeQuery13();}break;
			case 14: { invokeQuery14();}break;
			case 15: { invokeQuery15();}break;
			case 16: { invokeQuery16();}break;
			case 17: { invokeQuery17();}break;
			case 18: { invokeQuery18();}break;
			case 19: { invokeQuery19();}break;
			case 20: { invokeQuery20();}break;
			case 22: { invokeQuery22();}break;
			case 23: { invokeQuery23();}break;
			case 24: { invokeQuery24();}break;
			case 25: { invokeQuery25();}break;
			case 26: { invokeQuery26();}break;
		}
	}
	
	
	//following methods call for the respective query execution
	private void invokeQuery1() {
		String id ="";
		System.out.println("Enter desired company's id as an integer. [ex. 409382]");
		try {
			id += input.nextInt();
		}catch(InputMismatchException e) { System.out.println("Invalid company id");}
		//integers in parameters are for spacing purposes based on the createTable.sql file
		Result.print(String.format("%s%15s\n","first_name","last_name"));//print attribute titles
		Result.print(resource.query1(id),15,25);//connect, execute and print result of query
		Result.waitForUser();//hold for result
	}
	
	private void invokeQuery2() {
		String id ="";
		System.out.println("Enter desired company's id as an integer. [ex. 687331]");
		try {
			id += input.nextInt();
		}catch(InputMismatchException e) { System.out.println("Invalid company id");}
		
		Result.print(String.format("%s%8s%15s%20s\n","per_id","first_name","last_name","pay_rate"));//print attribute titles
		Result.print(resource.query2(id),8,15,20,12);
		Result.waitForUser();
	}
	
	private void invokeQuery3() {
		Result.print(String.format("%s%35s\n","comp_name","annual_pay"));//print attribute titles
		Result.print(resource.query3(),35,12);
		Result.waitForUser();
	}
	
	private void invokeQuery4() {
		Result.print(String.format("%s%30s%12s%12s\n","ind_title","avg_sal","max_sal","min_sal"));//print attribute titles
		Result.print(resource.query4(),30,12,12,12);
		Result.waitForUser();
	}
	
	private void invokeQuery5a() {
		Result.print(String.format("%s\n","com_name"));//print attribute titles
		Result.print(resource.query5A(),0);
		Result.waitForUser();
	}
	
	private void invokeQuery5b() {
		Result.print(String.format("%s\n","com_name"));//print attribute titles
		Result.print(resource.query5B(),0);
		Result.waitForUser();
	}
	
	private void invokeQuery5c() {
		Result.print(String.format("%s%20s\n","ind_title","employee"));//print attribute titles
		Result.print(resource.query5C(),20,10);
		Result.waitForUser();
	}
	
	private void invokeQuery6() {
		Result.print(String.format("%s%20s\n","ind_title","employee"));//print attribute titles
		Result.print(resource.query6(),20,10);
		Result.waitForUser();
	}
	
	private void invokeQuery7() {
		String per_id; 
		System.out.println("Enter desired person's id. [ex. 1000C]");
		if(input.hasNext()) {
			input.nextLine();
		}
		per_id = input.nextLine();
		Result.print(String.format("%s%10s\n","pos_code","pos_name"));//print attribute titles
		Result.print(resource.query7(per_id),8,10);
		Result.waitForUser();
	}
	
	private void invokeQuery8() {
		String per_id;
		System.out.println("Enter desired person's id. [ex. 10010]");
		if(input.hasNext()) {
			input.nextLine();
		}
		per_id = input.next();
		Result.print(String.format("%s%8s%35s\n","per_id","skill_code","ks_code"));//print attribute titles
		Result.print(resource.query8(per_id),10,35,10);
		Result.waitForUser();
	}
	
	private void invokeQuery9() {
		String per_id;
		System.out.println("Enter desired person's id. [ex. 10010]");
		if(input.hasNext()) {
			input.nextLine();
		}
		per_id = input.next();
		Result.print(String.format("%s%8s\n","cc_code","dist"));//print attribute titles
		Result.print(resource.query9(per_id),10,10);
		Result.waitForUser();
	}
	
	private void invokeQuery10() {
		String pos_code;
		System.out.println("Enter desired position code. [ex. 00125]");
		if(input.hasNext()) {
			input.nextLine();
		}
		pos_code = input.next();
		Result.print(String.format("%s%28s\n","cc_code","dist"));//print attribute titles
		Result.print(resource.query10(pos_code),30,10);
		Result.waitForUser();
	}
	
	private void invokeQuery11() {
		String job_cate;
		System.out.println("Enter desired job category code. [ex. CS004]");
		if(input.hasNext()) {
			input.nextLine();
		}
		job_cate = input.next();
		Result.print(String.format("%s%8s\n","job_cate","cc_code"));//print attribute titles
		Result.print(resource.query11(job_cate),8,10);
		Result.waitForUser();
	}
	
	private void invokeQuery12() {
		String pos_code, per_id;
		System.out.println("Enter desired position code. [ex. 06238]");
		if(input.hasNext()) { input.nextLine(); }
		pos_code = input.next();
		System.out.println("Enter desired person's id. [ex. 1000D]");
		if(input.hasNext()) { input.nextLine();}
		per_id = input.next();
		Result.print(String.format("%s%10s\n","ks_code", "cert_code"));//print attribute titles
		Result.print(resource.query12(pos_code, per_id),10, 10);
		Result.waitForUser();
	}
	
	private void invokeQuery13() {
		String pos_code, per_id;
		System.out.println("Enter desired position code. [ex. 06238]");
		if(input.hasNext()) {
			input.nextLine();
		}
		pos_code = input.next();
		System.out.println("Enter desired person's id. [ex. 10010]");
		if(input.hasNext()) {
			input.nextLine();
		}
		per_id = input.next();
		Result.print(String.format("%s%8s%40s\n","c_code","title","cert_code"));//print attribute titles
		Result.print(resource.query13(pos_code, per_id),8,40,10);
		Result.waitForUser();
	}
	
	private void invokeQuery14() {
		System.out.println("Enter desired position code. [ex. 06238]");
		if(input.hasNext()) {
			input.nextLine();
		}
		String pos_code = input.next();
		System.out.println("Enter desired person's id. [ex. 10010]");
		if(input.hasNext()) {
			input.nextLine();
		}
		String per_id = input.next();
		Result.print(String.format("%s%8s%40s\n","c_code","title","price"));//print attribute titles
		Result.print(resource.query14(per_id, pos_code),8,40,10);
		Result.waitForUser();
	}
	
	private void invokeQuery15() {
		System.out.println("Enter desired person's id. [ex. 10010]");
		if(input.hasNext()) {
			input.nextLine();
		}
		String per_id = input.next();
		Result.print(String.format("%s%20s\n","pos_name","annual_pay"));//print attribute titles
		Result.print(resource.query15(per_id),20,10);
		Result.waitForUser();
	}
	
	private void invokeQuery16() {
		System.out.println("Enter desired position code. [ex. 00126]");
		if(input.hasNext()) {
			input.nextLine();
		}
		String pos_code = input.next();
		Result.print(String.format("%s%15s%20s\n","first_name","last_name","email"));//print attribute titles
		Result.print(resource.query16(pos_code),16,22,10);
		Result.waitForUser();
	}
	
	private void invokeQuery17() {
		System.out.println("Enter desired position code. [ex. 00125]");
		if(input.hasNext()) {
			input.nextLine();
		}
		String pos_code = input.next();
		System.out.println("Enter number of missing skills to be checked for. [ex. 1]");
		if(input.hasNext()) {
			input.nextLine();
		}
		String numb = input.next();
		Result.print(String.format("%s%10s\n","per_id","missing_sk"));//print attribute titles
		Result.print(resource.query17(pos_code,numb),8,10);
		Result.waitForUser();
	}
	
	private void invokeQuery18() {
		System.out.println("Enter desired position code. [ex. 00125]");
		if(input.hasNext()) {
			input.nextLine();
		}
		String pos_code = input.next();
		Result.print(String.format("%s%10s\n","per_id","least_numb"));//print attribute titles
		Result.print(resource.query18(pos_code),8,10);
		Result.waitForUser();
	}
	
	private void invokeQuery19() {
		System.out.println("Enter desired position code. [ex. 00125]");
		if(input.hasNext()) {
			input.nextLine();
		}
		String pos_code = input.next();
		Result.print(String.format("%s%10s\n","need","missing_people"));//print attribute titles
		Result.print(resource.query19(pos_code),8,10);
		Result.waitForUser();
	}
	
	private void invokeQuery20() {
		System.out.println("Enter desired job category code. [ex. CS005]");
		if(input.hasNext()) {
			input.nextLine();
		}
		String job_cate = input.next();
		Result.print(String.format("%s%10s%20s%20s%30s%15s\n","per_id","first_name","last_name","title","hire_date","leave_date"));//print attribute titles
		Result.print(resource.query20(job_cate),10,20,20,27,15,10);
		Result.waitForUser();
	}
	private void invokeQuery21a() {
		Result.print(String.format("%s\n","pay_increase"));//print attribute titles
		Result.print(resource.query21A(),5);
		Result.waitForUser();
	}
	private void invokeQuery21b() {
		Result.print(String.format("%s\n","pay_decrease"));//print attribute titles
		Result.print(resource.query21B(),5);
		Result.waitForUser();
	}
	private void invokeQuery21c() {
		Result.print(String.format("%s\n","pay_ratio"));//print attribute titles
		Result.print(resource.query21C(),5);
		Result.waitForUser();
	}
	private void invokeQuery21d() {
		System.out.println("Enter desired industry code. [ex. 45102010]");
		if(input.hasNext()) {
			input.nextLine();
		}
		String ind_grp = input.next();
		Result.print(String.format("%s\n","rate_of_change"));//print attribute titles
		Result.print(resource.query21D(ind_grp),5);
		Result.waitForUser();
	}
	
	private void invokeQuery22() {
		System.out.println("Enter desired position code. [ex. 06238]");
		if(input.hasNext()) {
			input.nextLine();
		}
		String pos_code = input.next();
		Result.print(String.format("%s%10s%10s%15s\n","per_id","pos_code","hire_date","leave_date"));//print attribute titles
		Result.print(resource.query22(pos_code),8,9,15,15);
		Result.waitForUser();
	}
	
	private void invokeQuery23() {
		Result.print(String.format("%s\n","job_cate"));//print attribute titles
		Result.print(resource.query23(),5);
		Result.waitForUser();
	}
	
	private void invokeQuery24() {
		System.out.println("Enter desired person's id. [ex. 10010]");
		if(input.hasNext()) {
			input.nextLine();
		}
		String per_id = input.next();
		System.out.println("Enter desired position code. [ex. 00999]");
		if(input.hasNext()) 
			input.nextLine();
		String pos_code = input.next();
		Result.print(String.format("%s%10s%10s%12s\n","Course_1","Course_2","Course_3","Total_cost"));//print attribute titles
		Result.print(resource.query24(pos_code,per_id),10,10,10,10);
		Result.waitForUser();
	}
	
	private void invokeQuery25() {
		Result.print(String.format("%s%15s%15s\n","title","c_code","numb_per_course_qual"));//print attribute titles
		Result.print(resource.query25(),15,10,10);
		Result.waitForUser();
	}
	private void invokeQuery26() {
		System.out.println("Enter desired position code. [ex. 00444]");
		if(input.hasNext()) {
			input.nextLine();
		}
		String pos_code = input.next();
		System.out.println("Enter desired person id. [ex. 10010]");
		if(input.hasNext()) {
			input.nextLine();
		}
		String per_id = input.next();
		Result.print(String.format("%s%12s\n","course_id","req_skill"));//print attribute titles
		Result.print(resource.query26(pos_code,per_id),12,10);
		Result.waitForUser();
	}
	
	
	private void modifyDatabase() {
		//list adds and deletes and redirect to correct locations
		boolean running = true;
		int userInput = 0;
		while(running) {
			System.out.println("How should the database be modified?\n");
			System.out.println("1. Add new content\n2. Remove existing content\n3. Exit menu\n");
			try { userInput = input.nextInt();} catch (InputMismatchException e) { System.out.println("Incorrect Input"); }
			if(userInput == 1) {
				System.out.println(
						  "1.  Add a new course\n"
						+ "2.  Connect a course to a skill (Teaches)\n"
						+ "3.  Add a new knowledge skill\n"
						+ "4.  Add a certificate\n"
						+ "5.  Connect a certificate to a course (Issues)\n"
						+ "6.  Add a company\n"
						+ "7.  Add a position\n"
						+ "8.  Connect a position to a skill. (pos_requires)\n"
						+ "9.  Connect a position to a certificate.(requires_cert)\n"
						+ "10. Add a person.\n"
						+ "11. Connect a person to a position. (works)\n"
						+ "12. Connect a person to a skill. (has_skill)\n"
						+ "13. Connect a person to a certificate. (has_cert)\n"
						+ "14. Exit listing.\n");
				try { userInput = input.nextInt();} catch (InputMismatchException e) { System.out.println("Incorrect Input"); }
				addModDirectory(userInput);
			}else if(userInput == 2) {
				System.out.println("\nRemove functions are currently under maintainence.\n");
				/*System.out.println(
						  "1.  Remove a Course\n"
						+ "2.  Remove a link from course to knowledge skill (Teaches)\n"
						+ "3.  Remove a knowledge skill\n"
						+ "4.  Remove a certification.\n"
						+ "5.  Remove a link from course to certificate. (requires_cert)\n"
						+ "6.  Remove a company.\n"
						+ "7.  Remove a position.\n"
						+ "8.  Remove a link from a position to a skill. (pos_requires)\n"
						+ "9.  Remove a link from a position to a certificate. (requires_cert)"
						+ "10. Remove a person.\n"
						+ "11. Remove a link from a person to a position. (works)\n"
						+ "12. Remove a link from a person to skill. (has_skill)\n"
						+ "13. Remove a link from a person to a certificate. (has_cert)\n");*/
				//try { userInput = input.nextInt();} catch (InputMismatchException e) { System.out.println("Incorrect Input"); }
				//removeModDirectory(userInput);
			}else if(userInput == 3) {
				running = false;
			}
			
			
		}//end loop
	}//end method
	
	private void addModDirectory(int choice) {
		switch(choice) {
			case 1:  {mod.addCourse();}break;
			case 2:  {mod.addTeaches();}break;
			case 3:  {mod.addKnowledgeSkill();}break;
			case 4:  {mod.addCert();}break;
			case 5:  {mod.addIssues();}break;
			case 6:  {mod.addCompany();}break;
			case 7:  {mod.addPosition();}break;
			case 8:  {mod.addPos_req();}break;
			case 9:  {mod.addReq_cert();}break;
			case 10: {mod.addPerson();}break;
			case 11: {mod.addWorks();}break;
			case 12: {mod.addHas_skill();}break;
			case 13: {mod.addHas_cert();}break;
		}
	}//end method
	/*
	private void removeModDirectory(int choice) {
		switch(choice) {
			case 1:  {mod.deleteCourse();}break;
			case 2:  {mod.deleteTeaches();}break;
			case 3:  {mod.deleteKnowledgeSkill();}break;
			case 4:  {mod.deleteCert();}break;
			case 5:  {mod.deleteIssues();}break;
			case 6:  {mod.deleteCompany();}break;
			case 7:  {mod.deletePosition();}break;
			case 8:  {mod.deletePos_req();}break;
			case 9:  {mod.deleteReq_cert();}break;
			case 10: {mod.deletePerson();}break;
			case 11: {mod.deleteWorks();}break;
			case 12: {mod.deleteHas_skill();}break;
			case 13: {mod.deleteHas_cert();}break;
		}
	}
	*/
	private void businessProcessing(int s) {
		s=0;
		Scanner input = new Scanner(System.in);
		while(s!=4) {
			System.out.println("Enter a numeric choice");
			System.out.println(
					  "1. Hire a new employee into a company.\n"
					+ "2. Hunt for a job.\n"
					+ "3. Find qualified candidates for a position.\n"
					+ "4. Exit listing.\n");
			try {
				s = input.nextInt();
			}catch(NoSuchElementException  e) {	System.out.println("Database updated, Exiting program.");s = 4;	}
			if(s<4 && s> 0) {businessProcess(s);}
		}//end loop
		input.close();
	}//end method
	private void businessProcess(int s) {
		switch(s) {
			case 1: {employeeProcessing();}break;//8a
			case 2: {jobHunting();}break;//8b
			case 3: {mostQualified();}break;//8c
			case 4: {} break;
			default:{ System.out.println("Something is screwy in businessProcessing");}
		}
	}
	
	//link to employee hired process
	private void employeeProcessing() {
		String per_id, pos_code;
		Scanner input = new Scanner(System.in);
		System.out.println("Enter the new hire's per_id: [ex. 10099]");
		per_id = input.nextLine();
		System.out.println("Enter the pos_code that the person was hired for.");
		pos_code = input.nextLine();
		bs.hire(per_id, pos_code);
		input.close();
	}
	
	private void jobHunting() {
		bs.jobHunting();
	}
	
	private void mostQualified() {
		bs.zipRecruitor();
		//tui for 16 and 18.
	}
	
}
//327 --123 450 --75 525 --35
//559

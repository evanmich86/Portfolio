
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.*;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class BusinessProcess {

	private PreparedStatement prst;
	private QueryResource queries;
	private Connection connect;
	private Scanner input;
	private BufferedReader buff;
	private FileReader fRead;
	private String per_id, pos_code;
	private final int C_CODE =0, SEC_CODE = 1, KS_CODE = 2, CERT_CODE = 3, COMPLETE_DATE = 4;
	private final int YEAR = 5, COMPLETED = 6;
	
	public BusinessProcess(JdbcConnection jdbc) {
 		queries = new QueryResource(jdbc);
		input = new Scanner(System.in);
		connect = jdbc.connect();
		try {
			this.connect.setAutoCommit(false);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}//end constructor
	
	public void hire(String per_id, String pos_code) {
		boolean complete = false;
		if(!checkInputs(per_id,"Select per_id From person where per_id = ?","per_id") &&
				!checkInputs(pos_code,"Select pos_code From position where pos_code = ?","pos_code")) {
			System.out.println("The per_id and/or pos_code is incorrect");
			return;
		}
		this.per_id = per_id;
		this.pos_code = pos_code;
		//task 8 from project 
		//Step 1 	Upload the 	person’s transcripts and input the course taking information into table Takes; 
		System.out.println("Enter the file name of the person's transcript.\nNote: File should be in CSV format.\n");
		ArrayList<String[]> list = uploadTranscript(input.nextLine());//check for file
		if(list.isEmpty())
			return;
		
		Date date = new Date(); //df.format(new Date());
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		System.out.println(df.format(date));
		
		ListIterator<String[]> iter = list.listIterator();
		//step 2 update takes, has_skill, has_cert and works relations 
		while(iter.hasNext()) {
			complete = update(iter.next());
			if(!complete)
				break;
		}
		if(complete)
			complete = works(df.format(date));
		//check if all updates were completed without error
		if(complete) {
			try {
				connect.commit();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("\nAll updates have been completed.\n");
		}else{
			System.out.println("\nError occured while processing Employee\n.");
		}
		System.out.println("Press Enter to continue.");
		input.nextLine();
		//Step 3	Verify if this person has every skill required by the given pos_code.
		ArrayList<String> missingSkills = verify();
		if(missingSkills.isEmpty()) {
			System.out.printf("Employee %s is fully qualified for %s.\n",this.per_id,this.pos_code);
		}else {
			//Step 4	If a skill gap is identified, propose a training plan for this person. 
			courseListing();
		}
		System.out.println("\nPress Enter to continue.");
		input.nextLine();
	}//end method hire
	
	//method used to check if data is in a table
	private boolean checkInputs(String attribute, String query, String name) {
		boolean isCorrect = false;
		ResultSet rs = null;
		try {
			prst = connect.prepareStatement(query); //convert string query to prepared stmnt
			prst.setString(1, attribute);//custom insert in query
			rs = prst.executeQuery();//execute query
		}catch(Exception e) {	System.out.println(e +" a");}
		try {
			rs.next();
			String temp = rs.getString(name);
			System.out.println(temp);
			if(!temp.isEmpty()) {
				isCorrect = true;
			}
		}catch(SQLException e) { System.out.println(e+ " b"); }
		return isCorrect;
	}//end method
	
	//method used to pull data from transcript
	private ArrayList<String[]> uploadTranscript(String file) {
		file.trim();
		System.out.println("Enter the full path to the file.");
		//transcript format: c_code,sec_code,ks_code,cert_code,complete_date,year,grade
		String path; //= "/home/stephen/eclipse-workspace/workForceManagementDataBase/bin/workForceDataBase/";//input.nextLine();
		path = input.nextLine();
		Scanner stringScan;
		ArrayList<String[]> parseLine = new ArrayList<String[]>();
		try {
			fRead = new FileReader(path+file);
			buff = new BufferedReader(fRead);
			String line;
			while((line = buff.readLine())!= null) {
				String[] temp = new String[7];
				stringScan = new Scanner(line);
				stringScan.useDelimiter(",");
				//c_code,sec_code,ks_code,cert_code,complete_date,year,passed/failed
				temp[C_CODE] = stringScan.next();
				temp[SEC_CODE] = stringScan.next();
				temp[KS_CODE] = stringScan.next();
				temp[CERT_CODE] = stringScan.next();
				temp[COMPLETE_DATE] = stringScan.next();
				temp[YEAR] = stringScan.next();
				temp[COMPLETED] = stringScan.next();
				parseLine.add(temp);
			}
		}catch (IOException e) {
			System.out.println(e + " uploadTranscript");
		}finally {
			try {
				if(buff != null) { 	buff.close(); }
				if(fRead != null) { fRead.close(); }
			}catch (IOException q) {System.out.println(q + " uploadTranscript");}
		}
		return parseLine;
	}//end method

	//update relations in transaction block
	private boolean update(String[] list) {
		boolean check = true;
		//include only skill that have been passed
		if(!list[COMPLETED].equals("D") || !list[COMPLETED].equals("F")) {
			check= takes(list[SEC_CODE],list[C_CODE],list[YEAR]);//update takes
			check = has_skill(list[KS_CODE]);//update has_skill
			//include certification if it is not null
			if(!list[CERT_CODE].equals("null")) {
				check = has_cert(list[CERT_CODE]);
			}
		}
		return check;
	}
	
	private boolean takes(String sec_code, String c_code, String year) {
		boolean check;
		System.out.println("Updating takes "+this.per_id+" "+sec_code + " "+ c_code + " "+year);
		try {
			prst = connect.prepareStatement("INSERT INTO takes VALUES(?,?,?,?)");
			prst.setString(1,this.per_id);//insert strings for prepared statement
			prst.setString(2, sec_code);
			prst.setString(3, c_code);
			prst.setString(4, year);
			prst.executeUpdate();
			check = true;
		}catch(SQLException e) { check = false;  System.out.println(e);}//finally {
            //try { if (prst != null) prst.close(); } catch (Exception e) {};
            //try { if (connect != null) connect.close(); } catch (Exception e) {};
        //}
		return check;
	}
	
	//update has_skill
	private boolean has_skill(String ks_code) {
		boolean check;
		System.out.println("Updating has_skill "+this.per_id+" "+ks_code);
		try {
			//connect = jdbc.connect();//open connection
			
			prst = connect.prepareStatement("INSERT INTO has_skill VALUES(?,?)");
			prst.setString(1,this.per_id);//insert strings for prepared statement
			prst.setString(2, ks_code);
			prst.executeUpdate();
			check = true;
		}catch(SQLException e) { check = false; System.out.println(e);}
		return check;
	}//end method
	
	//update has_cert
	private boolean has_cert(String cert_code) {
		boolean check;
		System.out.println("Updating has_cert "+this.per_id+" "+cert_code);
		try {
			//connect = jdbc.connect();//open connection
			prst = connect.prepareStatement("INSERT INTO has_cert VALUES(?,?)");
			prst.setString(1,this.per_id);//insert strings for prepared statement
			prst.setString(2, cert_code);
			prst.executeUpdate();
			check = true;
		}catch(SQLException e) { check = false; System.out.println(e);}
		return check;
	}//end method
	
	//update has_cert
		private boolean works(String date) {
			boolean check;
			System.out.println("Updating works "+this.per_id+" "+this.pos_code);
			try {
				//connect = jdbc.connect();//open connection
				prst = connect.prepareStatement("INSERT INTO works VALUES(?,?,?,?)");
				prst.setString(1,this.per_id);//insert strings for prepared statement
				prst.setString(2, this.pos_code);
				prst.setString(3, date);
				prst.setNull(4, Types.VARCHAR);
				prst.executeUpdate();
				check = true;
			}catch(SQLException e) { check = false; System.out.println(e);}
			return check;
		}//end method

	//find out how qualified a person is for a position
	private ArrayList<String> verify() {
		ArrayList<String> strings = new ArrayList<String>();
		if(!qualified()) {
			ArrayList<String[]> list = queries.query17(this.pos_code, "1");//find on the missing-k list
			String[] temp;
			ListIterator<String[]> iter = list.listIterator();
			
			while(iter.hasNext()) {
				temp = iter.next();
				if(temp[0].equals(this.per_id)) {
					strings.add(temp[1]);
				}
			}
			if(strings.isEmpty()) {
				strings = notQualified();
			}
		}
		return strings;
	}//end method
	
	//get missing skills for a new employee 
	private boolean qualified() {
		boolean verified = false;//division to find it person is fully qualified
		String query = "SELECT DISTINCT per_id FROM works w WHERE NOT EXISTS( "
				+"(SELECT ks_code FROM pos_requires WHERE pos_code = ?) MINUS "
				+"(SELECT ks_code FROM has_skill h WHERE h.per_id = w.per_id AND h.per_id = ?)) ";
		ResultSet rs = null;
		try {
			//connect = jdbc.connect();//open connection to db
			prst = connect.prepareStatement(query); //convert string query to prepared stmnt
			prst.setString(1, pos_code);//custom insert in query
			prst.setString(2, per_id);
			rs = prst.executeQuery();//execute query
		}catch(SQLException e) { System.out.println(e); }
		try {
			if(rs.next()) {
				verified = this.per_id.equals(rs.getString("per_id"));
			}
		}catch(SQLException e) { System.out.println(e); }
		return verified;
	}//end method
	
	//returns all ks_skills needed for position
	private ArrayList<String> notQualified(){
		ArrayList<String> list = new ArrayList<String>();
		String query = "SELECT ks_code FROM pos_requires WHERE pos_code = ?";
		
		ResultSet rs = null;
		try {
			//connect = jdbc.connect();//open connection to db
			prst = connect.prepareStatement(query); //convert string query to prepared stmnt
			prst.setString(1, this.pos_code);//custom insert in query
			rs = prst.executeQuery();//execute query
		}catch(SQLException e) { System.out.println(e); }
		try {
			while(rs.next()) {
				list.add(rs.getString("ks_code"));
			}
		}catch(SQLException e) { System.out.println(e); }
		return list;
	}
	
	private void courseListing() {
		//provide all courses needed for skill gap
		ArrayList<String[]> list = queries.query26(pos_code, per_id);
		//use queryResource
		ListIterator<String[]> iter = list.listIterator();
		
		System.out.println("Missing skills along with course codes\n");
		while(iter.hasNext()) {
			String[] temp = course(iter.next()[0]);
			System.out.printf("Course: %s, Skill: %s, Title: %s, Price: %s, Institution: %s\n"
					,temp[0], temp[1],temp[2],temp[3], temp[4]);
		}
	}//end method
	
	private String[] course(String c_code) {
		String[] temp = new String[5];
		String query = "SELECT c_code, ks_code, title ,retail_price, institution\n" + 
				"FROM course NATURAL JOIN teaches WHERE c_code = ? ";
		ResultSet rs = null;
		try {
			//connect = jdbc.connect();//open connection to db
			prst = connect.prepareStatement(query); //convert string query to prepared stmnt
			prst.setString(1,c_code);
			rs = prst.executeQuery();//execute query
		}catch(SQLException e) { System.out.println(e);	}
		try {
			while(rs.next()) {
				temp[0] = rs.getString("c_code");
				temp[1] = rs.getString("ks_code");
				temp[2] = rs.getString("title");
				temp[3] = rs.getString("retail_price");
				temp[4] = rs.getString("institution");
			}
		}catch(SQLException q) { System.out.println(q); }
		
		return temp;
	}
		
	public void jobHunting() {
		//query15: list highest paying job thata person is qualified for
		System.out.println("Job search based on pay and quailfications.\n\nEnter person's id\n");
		String selection = input.next();
		System.out.println("Job you may be interested in.\n");
		print(queries.query15(selection));
		
		//query3: average pay of companies
		//list positions offered by a company
		//query12: list missing skills
		//list sections institutions and prices of courses that teach a skill
	}
	
	private void print(ArrayList<String[]> list) {
		if(!list.isEmpty()) {
			ListIterator<String[]> iter = list.listIterator();
			while(iter.hasNext()) {
				String[] temp = iter.next();
				for(int i=0;i<temp.length;i++) {
					System.out.printf("%s ",temp[i]);
				}
				System.out.println("\n");
			}
		}
	}//end method
	
	public void zipRecruitor() {
		ArrayList<String[]> list = new ArrayList<String[]>();
		String[] array;
		System.out.println("Search for Qualified candidates for your company.\n"
				+ "Enter the position code that you wish to fill and we'll do the rest.");
		String pos_code = input.nextLine();
		if(pos_code.length()< 4)
			pos_code = input.nextLine();
		list = queries.query16(pos_code);//query16
		if(list.isEmpty()) {
			//get data for best candidate
			list = queries.query18(pos_code);
			array = getContactInfo(list.get(0)[0]);//pass in most qualified person's id
			list = revisedQuery12(pos_code, list.get(0)[0]);
			ListIterator<String[]> iter = list.listIterator();
			System.out.println("Best matched candidate.");
			System.out.printf("%s %s %s\n\n",array[0],array[1],array[2]);
			System.out.printf("Missing the following skills:\n");
			while(iter.hasNext()) {
				array = iter.next();
				System.out.printf("%s %s\n",array[0],array[1]);
			}
			System.out.println();			
		}else {
			System.out.println("Weve found a perfect match.\n");
			Result.print(list,16,22,10);
		}
		//query18 transfer query 18 into the skills missing and the persons info
	}
	
	private String[] getContactInfo(String per_id) {
		String query = "SELECT first_name, last_name, email FROM person WHERE per_id = ?";
		String[] list = new String[3];
		ResultSet rs = null;
		try {
			prst = connect.prepareStatement(query); //convert string query to prepared stmnt
			prst.setString(1, per_id);//custom insert in query
			rs = prst.executeQuery();//execute query
		}catch(SQLException e) {}
		try {
			while(rs.next()) {
				list[0] = rs.getString("first_name");
				list[1] = rs.getString("last_name");
				list[2] = rs.getString("email");
			}
		}catch(SQLException e) {}
		return list;
	}
	
	//revised to include the skill titles
	private ArrayList<String[]> revisedQuery12(String pos_code, String per_id){
		ArrayList<String[]> list = new ArrayList<String[]>();
		String query = "with temp AS( SELECT ks_code FROM (SELECT ks_code "+
				"FROM pos_requires WHERE pos_code = ?  MINUS "+
						"SELECT ks_code FROM has_skill WHERE per_id = ?)) "+
						"SELECT ks_code, skill_title "+
						"FROM temp NATURAL JOIN knowledge_skill ";
		ResultSet rs = null;
		try {
			prst = connect.prepareStatement(query); //convert string query to prepared stmnt
			prst.setString(1, pos_code);
			prst.setString(2, per_id);//custom insert in query
			rs = prst.executeQuery();//execute query
		}catch(SQLException e) {}
		try {
			while(rs.next()) {
				String[] temp = new String[2];
				temp[0] = rs.getString("ks_code");
				temp[1] = rs.getString("skill_title");
				list.add(temp);
			}
		}catch(SQLException e) {}
		return list;
	}
	
}//end class
//312

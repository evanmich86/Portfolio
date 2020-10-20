/* class Resource holds methods for running queries 
 *  al
 * */
import java.sql.*;
import java.util.ArrayList;

public class QueryResource {
	
	
	private String query;
	private PreparedStatement prst = null;
	private Connection connect;
	
	public QueryResource(JdbcConnection jdbc) {
		//connect to the database
		this.connect = jdbc.connect();
	}//end constructor
	
	/**Query 1: list company's workers by name*/
	public ArrayList<String[]> query1(String comp_id) {
		query = "WITH current_emps AS("+
		"	    SELECT per_id, pos_code"+
		"	    FROM works"+
		"	    WHERE works.leave_date IS null)"+
		"	SELECT first_name, last_name"+
		"	FROM person NATURAL JOIN (position NATURAL JOIN current_emps)"+ 
		"	WHERE position.comp_id = ?";//409382 is our test sample
		
		ResultSet rs = null;
		ArrayList<String[]> list = new ArrayList<String[]>();
		
		try {
			prst = connect.prepareStatement(query); //convert string query to prepared stmnt
			prst.setString(1, comp_id);//custom insert in query
			rs = prst.executeQuery();//execute query
		}catch(Exception e) {//terrible way to catch exceptions
			System.out.println(e + " Method: query1 Class QueryResource (a)");
		}
		try {
			//pull in each line from the ResultSet
			while(rs.next()) {
				String[] line = new String[2];
				line[0] = rs.getString("first_name");
				line[1] = rs.getString("last_name");
				list.add(line);
			}
		}catch(Exception q) {
			System.out.println(q + " Method: query1 Class QueryResource (b)");
		}
		
		return list;
	}//end method
	
	/**Query 2: list company's staff by salaries*/
	public ArrayList<String[]> query2(String comp_id) {
		query = "WITH current_emps AS( "+
			"    SELECT per_id, pos_code "+
			 "   FROM works "+
			 "   WHERE works.leave_date IS null) "+
			"SELECT per_id, first_name, last_name, pay_rate "+
			"FROM position NATURAL JOIN (person NATURAL JOIN current_emps) "+
			"WHERE comp_id = ? AND "+//687331
			"        pay_type = 'salary' "+
			"ORDER BY position.pay_rate DESC ";
		
		ResultSet rs = null;
		ArrayList<String[]> list = new ArrayList<String[]>();
		
		try {
			prst = connect.prepareStatement(query); //convert string query to prepared stmnt
			prst.setString(1, comp_id);//custom insert in query
			rs = prst.executeQuery();//execute query
		}catch(SQLException e) {
			System.out.println(e + " Method: query2 Class QueryResource (a)");
		}
		try {
			//pull in each line from the ResultSet
			while(rs.next()) {
				String[] line = new String[4];
				line[0] = rs.getString("per_id");
				line[1] = rs.getString("first_name");
				line[2] = rs.getString("last_name");
				line[3] = rs.getDouble("pay_rate")+"";
				list.add(line);
			}
		}catch(Exception q) {
			System.out.println(q + " Method: query2 Class QueryResource (b)");
		}
				
		return list;
	}//end method
	
	/**Query 3: list AVG annual pay by each company*/
	public ArrayList<String[]> query3() {
		query = "WITH current_emps "+
		  "AS (SELECT per_id, pos_code "+
		   "     FROM works "+
		   "    WHERE leave_date IS NULL), "+
		"job_rel_pay "+
		  "AS (SELECT pos_code, comp_id, "+
		   "          CASE pay_type "+
		   "          WHEN 'hourly' "+
		   "          THEN pay_rate * 1920 "+
		   "          WHEN 'salary' "+
		   "          THEN pay_rate "+
		   "           END AS pay "+
		   "     FROM position) "+
		"SELECT comp_name, ROUND(pay_avg, 2) AS annual_pay "+
		 " FROM (SELECT comp_name, AVG(pay) AS pay_avg "+
		 "         FROM person "+
		 "              INNER JOIN current_emps "+
		 "              ON person.per_id = current_emps.per_id "+
		 "              INNER JOIN job_rel_pay "+
		 "              ON current_emps.pos_code = job_rel_pay.pos_code "+
		 "              INNER JOIN company "+
		 "              ON job_rel_pay.comp_id = company.comp_id "+
		 "        GROUP BY comp_name) "+
		 "ORDER BY pay_avg DESC ";

		ResultSet rs = null;
		ArrayList<String[]> list = new ArrayList<String[]>();
		
		try {
			prst = connect.prepareStatement(query); //convert string query to prepared stmnt
			rs = prst.executeQuery();//execute query
		}catch(Exception e) {//terrible way to catch exceptions
			System.out.println(e + " Method: query3 Class QueryResource (a)");
		}
		try {
			//pull in each line from the ResultSet
			while(rs.next()) {
				String[] line = new String[2];
				line[0] = rs.getString("comp_name");
				line[1] = rs.getDouble("annual_pay")+"";
				list.add(line);
			}
		}catch(Exception q) {
			System.out.println(q + " Method: query3 Class QueryResource (b)");
		}
		
		return list;
	}//end method
	
	/**Query 4: List the AVG, MIN and MAX annual pay 
	 * of each industry*/
	public ArrayList<String[]> query4() {
		query = "WITH ind_sal AS( "+
			    "SELECT gcis.ind_title, salaries "+
			    "FROM GCIS NATURAL JOIN sub_ind NATURAL JOIN company NATURAL JOIN "+
			    "    (SELECT comp_id, pay_rate*1920 AS salaries "+
			    "         FROM position "+
			    "         WHERE pay_type = 'hourly' "+
			    "         UNION "+
			    "         SELECT comp_id, pay_rate AS salaries "+
			    "         FROM position "+
			    "         WHERE pay_type = 'salary')) "+             
			"SELECT ind_sal.ind_title, AVG(salaries) avg_sal, MAX(salaries) max_sal, MIN(salaries) min_sal "+                  
			"FROM ind_sal INNER JOIN GCIS "+
			"ON ind_sal.ind_title = GCIS.ind_title "+
			"GROUP BY ind_sal.ind_title ";
		
		ResultSet rs = null;
		ArrayList<String[]> list = new ArrayList<String[]>();
		
		try {
			prst = connect.prepareStatement(query); //convert string query to prepared stmnt
			rs = prst.executeQuery();//execute query
		}catch(Exception e) {//terrible way to catch exceptions
			System.out.println(e + " Method: query4 Class QueryResource (a)");
		}
		try {
			//pull in each line from the ResultSet
			while(rs.next()) {
				String[] line = new String[4];
				line[0] = rs.getString("ind_title");
				line[1] = rs.getDouble("avg_sal")+"";
				line[2] = rs.getDouble("max_sal")+"";
				line[3] = rs.getDouble("min_sal")+"";
				list.add(line);
			}
		}catch(SQLException q) {
			System.out.println(q + " Method: query4 Class QueryResource (b)");
		}
		
		return list;
	}//end method
	
	/**Query 5a: find biggest employer in terms of 
	 * employees*/
	public ArrayList<String[]> query5A() {
		query = "WITH current_emps AS( "+
			    "SELECT per_id, pos_code "+
			    "FROM works "+
			    "WHERE works.leave_date IS null) "+
			"SELECT comp_name "+
			"FROM (SELECT comp_name "+
			"          FROM company "+
			"               INNER JOIN position "+
			"               ON company.comp_id = position.comp_id "+
			"               INNER JOIN current_emps "+
			"               ON position.pos_code = current_emps.pos_code "+
			"         GROUP BY comp_name "+
			"         ORDER BY COUNT(per_id) DESC) "+
			 "WHERE ROWNUM = 1 ";

		ResultSet rs = null;
		ArrayList<String[]> list = new ArrayList<String[]>();
		
		try {
			prst = connect.prepareStatement(query); //convert string query to prepared stmnt
			rs = prst.executeQuery();//execute query
		}catch(SQLException e) {//terrible way to catch exceptions
			System.out.println(e + " Method: query5 Class QueryResource (a)");
		}
		try {
			//pull in each line from the ResultSet
			while(rs.next()) {
				String[] line = new String[1];
				line[0] = rs.getString("comp_name");
				list.add(line);
			}
		}catch(SQLException q) {
			System.out.println(q + " Method: query5 Class QueryResource (b)");
		}
		
		return list;
	}//end method
	
	/**Query 5b: biggest industry*/
	public ArrayList<String[]> query5B() {
		query = "SELECT * "+
				"   FROM (SELECT industry_group "+
				"          FROM company "+
				"               INNER JOIN position "+
				"               ON company.comp_id = position.comp_id "+
				"               INNER JOIN works "+
				"               ON position.pos_code = works.pos_code "+
				"                   AND leave_date IS NULL "+
				"         GROUP BY industry_group "+
				"         ORDER BY COUNT(per_id)) "+
				" WHERE ROWNUM = 1 ";
		
		ResultSet rs = null;
		ArrayList<String[]> list = new ArrayList<String[]>();
		
		try {
			prst = connect.prepareStatement(query); //convert string query to prepared stmnt
			rs = prst.executeQuery();//execute query
		}catch(SQLException e) {
			System.out.println(e + " Method: query5 Class QueryResource (a)");
		}
		try {
			//pull in each line from the ResultSet
			while(rs.next()) {
				String[] line = new String[1];
				line[0] = rs.getString("industry_group");
				list.add(line);
			}
		}catch(SQLException q) {
			System.out.println(q + " Method: query5 Class QueryResource (b)");
		}
		
		
		return list;
	}//end method
	
	/**Query 5c: biggest industry group*/
	public ArrayList<String[]> query5C() {
		query = "WITH current_emps AS( "+
			    "SELECT per_id, pos_code "+
			    "FROM works "+
			    "WHERE works.leave_date IS null), "+
			"temp AS ( "+
			"        SELECT parent_code, COUNT(DISTINCT per_id) AS employees "+
			"        FROM GCIS NATURAL JOIN sub_ind NATURAL JOIN company NATURAL JOIN position NATURAL JOIN current_emps "+
			"        GROUP BY parent_code) "+
			"SELECT ind_title, employees "+
			"FROM temp INNER JOIN GCIS "+
			"        ON temp.parent_code = industry_code "+
			"        ORDER BY employees DESC ";
		
		ResultSet rs = null;
		ArrayList<String[]> list = new ArrayList<String[]>();
		
		try {
			prst = connect.prepareStatement(query); //convert string query to prepared stmnt
			rs = prst.executeQuery();//execute query
		}catch(SQLException e) {//terrible way to catch exceptions
			System.out.println(e + " Method: query5 Class QueryResource (a)");
		}
		try {
			//pull in each line from the ResultSet
			while(rs.next()) {
				String[] line = new String[2];
				line[0] = rs.getString("ind_title");
				line[1] = rs.getString("employees");
				list.add(line);
			}
		}catch(SQLException q) {
			System.out.println(q + " Method: query5 Class QueryResource (b)");
		}
		
		return list;
	}//end method


	
	/**Distribution of employees by industry */
	public ArrayList<String[]> query6() {
		query = "WITH current_emps AS( "+
			    "SELECT per_id, pos_code "+
			    "FROM works "+
			    "WHERE works.leave_date IS null), "+
			"temp AS ( "+
			 "       SELECT parent_code, COUNT(DISTINCT per_id) AS employees "+
			"        FROM GCIS NATURAL JOIN sub_ind NATURAL JOIN company NATURAL JOIN position NATURAL JOIN current_emps "+
			"        GROUP BY parent_code) "+
			"SELECT ind_title, employees "+
			"FROM temp CROSS JOIN GCIS "+
			"WHERE temp.parent_code = industry_code ";
		
		ResultSet rs = null;
		ArrayList<String[]> list = new ArrayList<String[]>();
		
		try {
			prst = connect.prepareStatement(query); //convert string query to prepared stmnt
			rs = prst.executeQuery();//execute query
		}catch(SQLException e) {//terrible way to catch exceptions
			System.out.println(e + " Method: query6 Class QueryResource (a)");
		}
		try {
			//pull in each line from the ResultSet
			while(rs.next()) {
				String[] line = new String[2];
				line[0] = rs.getString("ind_title");
				line[1] = rs.getInt("employees")+"";
				list.add(line);
			}
		}catch(SQLException q) {
			System.out.println(q + " Method: query6 Class QueryResource (b)");
		}
		
		return list;
	}//end method

	/**list all job position a person has*/
	public ArrayList<String[]> query7(String per_id) {
		query = "SELECT pos_code, pos_name "+
		"FROM works NATURAL JOIN position "+
		"WHERE per_id = ? ";
		
		ResultSet rs = null;
		ArrayList<String[]> list = new ArrayList<String[]>();
		
		try {
			prst = connect.prepareStatement(query); //convert string query to prepared stmnt
			prst.setString(1, per_id);//custom insert in query
			rs = prst.executeQuery();//execute query
		}catch(SQLException e) {//terrible way to catch exceptions
			System.out.println(e + " Method: query7 Class QueryResource (a)");
		}
		try {
			//pull in each line from the ResultSet
			while(rs.next()) {
				String[] line = new String[2];
				line[0] = rs.getString("pos_code");
				line[1] = rs.getString("pos_name");
				list.add(line);
			}
		}catch(SQLException q) {
			System.out.println(q + " Method: query7 Class QueryResource (b)");
		}
		
		return list;
	}//end method

	/**list all a persons skills*/
	public ArrayList<String[]> query8(String per_id) {
		query = "SELECT per_id, skill_title, ks_code "+
				"FROM has_skill NATURAL JOIN knowledge_skill "+
				"WHERE per_id = ? ";
		
		ResultSet rs = null;
		ArrayList<String[]> list = new ArrayList<String[]>();
		
		try{
			prst = connect.prepareStatement(query); //convert string query to prepared stmnt
			prst.setString(1, per_id);//custom insert in query
			rs = prst.executeQuery();//execute query
		}catch(SQLException e) {//terrible way to catch exceptions
			System.out.println(e + " Method: query8 Class QueryResource (a)");
		}
		try {
			//pull in each line from the ResultSet
			while(rs.next()) {
				String[] line = new String[3];
				line[0] = rs.getString("per_id");
				line[1] = rs.getString("skill_title");
				line[2] = rs.getString("ks_code");
				list.add(line);
			}
		}catch(SQLException q) {
			System.out.println(q + " Method: query8 Class QueryResource (b)");
		}
		
		return list;
	}//end method

	/**list the distribution of a persons skills*/
	public ArrayList<String[]> query9(String per_id) {
		query = "SELECT cc_code, COUNT(ks_code) dist "+
				"FROM has_skill NATURAL JOIN knowledge_skill "+
				"WHERE per_id = ? "+//? "+
				"GROUP BY cc_code ";
		

		ResultSet rs = null;
		ArrayList<String[]> list = new ArrayList<String[]>();
		
		try {
			prst = connect.prepareStatement(query); //convert string query to prepared stmnt
			prst.setString(1, per_id);//custom insert in query
			rs = prst.executeQuery();//execute query
		}catch(Exception e) {//terrible way to catch exceptions
			System.out.println(e + " Method: query9 Class QueryResource (a)");
		}
		try {
			//pull in each line from the ResultSet
			while(rs.next()) {
				String[] line = new String[2];
				line[0] = rs.getString("cc_code");
				line[1] = rs.getString("dist");
				list.add(line);
			}
		}catch(Exception q) {
			System.out.println(q + " Method: query9 Class QueryResource (b)");
		}
		
		return list;
	}//end method

	/**list the required skill of a given position*/
	public ArrayList<String[]> query10(String pos_code) {
		query = "SELECT skill_title, ks_code "+
				"FROM pos_requires NATURAL JOIN knowledge_skill "+
				"WHERE pos_code = ? ";//00125
		
		ResultSet rs = null;
		ArrayList<String[]> list = new ArrayList<String[]>();
		
		try {
			prst = connect.prepareStatement(query); //convert string query to prepared stmnt
			prst.setString(1, pos_code);//custom insert in query
			rs = prst.executeQuery();//execute query
		}catch(SQLException e) {//terrible way to catch exceptions
			System.out.println(e + " Method: query10 Class QueryResource (a)");
		}
		try {
			//pull in each line from the ResultSet
			while(rs.next()) {
				String[] line = new String[2];
				line[0] = rs.getString("skill_title");
				line[1] = rs.getString("ks_code");
				list.add(line);
			}
		}catch(SQLException q) {
			System.out.println(q + " Method: query10 Class QueryResource (b)");
		}
		
		return list;
	}//end method

	/**list the required skills categories of a given position*/
	public ArrayList<String[]> query11(String job_cate) {
		query = "SELECT job_cate, cc_code "+
				"FROM (job_category NATURAL JOIN core_skill) "+
				"WHERE job_cate = ? ";
		

		ResultSet rs = null;
		ArrayList<String[]> list = new ArrayList<String[]>();
		
		try {
			prst = connect.prepareStatement(query); //convert string query to prepared stmnt
			prst.setString(1, job_cate);//custom insert in query
			rs = prst.executeQuery();//execute query
		}catch(SQLException e) {//terrible way to catch exceptions
			System.out.println(e + " Method: query11 Class QueryResource (a)");
		}
		try {
			//pull in each line from the ResultSet
			while(rs.next()) {
				String[] line = new String[2];
				line[0] = rs.getString("job_cate");
				line[1] = rs.getString("cc_code");
				list.add(line);
			}
		}catch(SQLException q) {
			System.out.println(q + " Method: query11 Class QueryResource (b)");
		}
		
		return list;
	}//end method

	/**List a persons missing skills for a given position06238 1000D*/
	public ArrayList<String[]> query12(String pos_code, String per_id) {
		query = "WITH missing_skills AS( SELECT ks_code FROM (SELECT ks_code "+
			    "FROM pos_requires WHERE pos_code = ? MINUS SELECT ks_code "+
			    "FROM has_skill WHERE per_id = ? ) ), missing_certs AS( "+
			    "SELECT cert_code FROM (SELECT cert_code FROM requires_cert "+
			    "WHERE pos_code = ? MINUS SELECT cert_code "+
			    "FROM has_cert WHERE per_id = ?) ) SELECT ks_code, cert_code "+
				"FROM missing_skills LEFT OUTER JOIN missing_certs "+
			    "ON cert_code IS NOT NULL ";
		
		ResultSet rs = null;
		ArrayList<String[]> list = new ArrayList<String[]>();
		
		try {
			prst = connect.prepareStatement(query); //convert string query to prepared stmnt
			prst.setString(1, pos_code);//custom insert in query
			prst.setString(2, per_id);//custom insert in query
			prst.setString(3, pos_code);//custom insert in query
			prst.setString(4, per_id);//custom insert in query
			rs = prst.executeQuery();//execute query
		}catch(SQLException e) {
			System.out.println(e + " Method: query12 Class QueryResource (a)");
		}
		try {
			//pull in each line from the ResultSet
			while(rs.next()) {
				String[] line = new String[2];
				line[0] = rs.getString("ks_code");
				line[1] = rs.getString("cert_code");
				list.add(line);
			}
		}catch(SQLException q) {
			System.out.println(q + " Method: query12 Class QueryResource (b)");
		}
		
		return list;
	}//end method

	/**list that alone teaches all the skill need for a position*/
	public ArrayList<String[]> query13(String pos_code, String per_id) {
		query = "SELECT course.c_code, course.title AS course_title, cert_code "+
			    "FROM issues INNER JOIN course ON issues.c_code = course.c_code "+
			    "NATURAL JOIN ( SELECT cert_code FROM requires_cert "+
			    "WHERE pos_code = ? MINUS SELECT cert_code FROM has_cert "+ 
			    "WHERE per_id = ? ) UNION SELECT teaches.c_code, course.title, NULL "+ 
			    "FROM teaches INNER JOIN course ON teaches.c_code = course.c_code "+
			    "NATURAL JOIN ( SELECT ks_code FROM pos_requires WHERE pos_code = ? "+
			    "MINUS SELECT ks_code FROM has_skill  WHERE per_id = ? ) ";
		
		ResultSet rs = null;
		ArrayList<String[]> list = new ArrayList<String[]>();
		
		try {
			prst = connect.prepareStatement(query); //convert string query to prepared stmnt
			prst.setString(1, pos_code);//custom insert in query
			prst.setString(2, per_id);//custom insert in query
			prst.setString(3, pos_code);//custom insert in query
			prst.setString(4, per_id);//custom insert in query
			rs = prst.executeQuery();//execute query
		}catch(SQLException e) {//terrible way to catch exceptions
			System.out.println(e + " Method: query13 Class QueryResource (a)");
		}
		try {
			//pull in each line from the ResultSet
			while(rs.next()) {
				String[] line = new String[3];
				line[0] = rs.getString("c_code");
				line[1] = rs.getString("course_title");
				line[2] = rs.getString("cert_code");
				list.add(line);
			}
		}catch(SQLException q) {
			System.out.println(q + " Method: query13 Class QueryResource (b)");
		}
		
		return list;
	}//end method

	/**lists the cheapest course that fills a persons skill gap*/
	public ArrayList<String[]> query14(String per_id, String pos_code) {
		query = "WITH required_courses AS ( SELECT DISTINCT T.c_code "+
				"FROM teaches T WHERE NOT EXISTS( (SELECT pos_requires.ks_code "+
				"FROM pos_requires WHERE pos_code = ? ) MINUS "+
				"(SELECT has_skill.ks_code FROM has_skill "+
				"WHERE per_id = ? ) MINUS (SELECT ks_code FROM teaches S "+
				"WHERE S.c_code = T.c_code))) SELECT c_code, title, retail_price "+
				"FROM section NATURAL JOIN required_courses NATURAL JOIN course "+
				"WHERE retail_price = (SELECT MIN(retail_price) FROM required_courses) ";
				
		ResultSet rs = null;
		ArrayList<String[]> list = new ArrayList<String[]>();
		
		try {
			prst = connect.prepareStatement(query); //convert string query to prepared stmnt
			prst.setString(1, pos_code);//custom insert in query
			prst.setString(2, per_id);//custom insert in query
			rs = prst.executeQuery();//execute query
		}catch(SQLException e) {//terrible way to catch exceptions
			System.out.println(e + " Method: query14 Class QueryResource (a)");
		}
		try {
			//pull in each line from the ResultSet
			while(rs.next()) {
				String[] line = new String[3];
				line[0] = rs.getString("c_code");
				line[2] = rs.getString("retail_price");
				line[1] = rs.getString("title");
				list.add(line);
			}
		}catch(SQLException q) {
			System.out.println(q + " Method: query14 Class QueryResource (b)");
		}
		
		return list;
	}//end method

	/**returns the position with the highest pay that a person is qualified for*/
	public ArrayList<String[]> query15(String per_id) {
		query = "WITH qualified_jobs AS( SELECT * FROM (SELECT pos_code "+
				"FROM position p WHERE NOT EXISTS( (SELECT ks_code "+
				"FROM pos_requires r WHERE p.pos_code = r.pos_code) "+
				"MINUS (SELECT ks_code FROM has_skill WHERE per_id = ? ))) "+
				"NATURAL JOIN ( SELECT pos_code  FROM position p "+
				"WHERE NOT EXISTS( (SELECT cert_code FROM requires_cert r "+ 
				"WHERE p.pos_code = r.pos_code) MINUS (SELECT cert_code "+
				"FROM has_cert WHERE per_id = ?))) ), annual_salaries AS( "+
				"SELECT pos_name, CASE pay_type WHEN 'hourly' THEN pay_rate * 1920 "+
				"WHEN 'salary' THEN pay_rate END AS annual_pay "+
				"FROM qualified_jobs NATURAL JOIN position) "+
				"SELECT A.pos_name, A.annual_pay "+
				"FROM annual_salaries A CROSS JOIN ( "+
				"SELECT MAX(annual_pay) AS annual_pay "+
				"FROM annual_salaries) B "+
				"WHERE A.annual_pay = B.annual_pay ";

		ResultSet rs = null;
		ArrayList<String[]> list = new ArrayList<String[]>();
		
		try {
			prst = connect.prepareStatement(query); //convert string query to prepared stmnt
			prst.setString(1, per_id);//custom insert in query
			prst.setString(2, per_id);//custom insert in query
			rs = prst.executeQuery();//execute query
		}catch(SQLException e) {//terrible way to catch exceptions
			System.out.println(e + " Method: query15 Class QueryResource (a)");
		}
		try {
			//pull in each line from the ResultSet
			while(rs.next()) {
				String[] line = new String[2];
				line[0] = rs.getString("pos_name");
				line[1] = rs.getString("annual_pay");
				list.add(line);
			}
		}catch(SQLException q) {
			System.out.println(q + " Method: query15 Class QueryResource (b)");
		}
		
		return list;
	}//end method

	/**lists all people who are qualified for a given position*/
	public ArrayList<String[]> query16(String pos_code) {
		query = "SELECT first_name, last_name, email FROM person NATURAL JOIN ( "+
				"(SELECT per_id FROM person P WHERE NOT EXISTS( "+
				"(SELECT ks_code FROM pos_requires WHERE pos_code = ? ) "+
				"MINUS (SELECT ks_code FROM has_skill H "+
				"WHERE P.per_id = H.per_id))) NATURAL JOIN "+
				"(SELECT per_id FROM person P WHERE NOT EXISTS( "+ 
				"(SELECT cert_code FROM requires_cert WHERE pos_code = ? ) "+
				"MINUS (SELECT cert_code FROM has_cert H "+
				"WHERE P.per_id = H.per_id)))) ";

		ResultSet rs = null;
		ArrayList<String[]> list = new ArrayList<String[]>();
		
		try {
			prst = connect.prepareStatement(query); //convert string query to prepared stmnt
			prst.setString(1, pos_code);//custom insert in query
			prst.setString(2, pos_code);//custom insert in query
			rs = prst.executeQuery();//execute query
		}catch(SQLException e) {
			System.out.println(e + " Method: query16 Class QueryResource (a)");
		}
		try {
			//pull in each line from the ResultSet
			while(rs.next()) {
				String[] line = new String[3];
				line[0] = rs.getString("first_name");
				line[1] = rs.getString("last_name");
				line[2] = rs.getString("email");
				list.add(line);
			}
		}catch(SQLException q) {
			System.out.println(q + " Method: query16 Class QueryResource (b)");
		}
		
		return list;
	}//end method

	/**Missing-k list returns people who are only missing k skills*/
	public ArrayList<String[]> query17(String pos_code, String numb) {
		query = "WITH have_k AS( "+
				"SELECT per_id, COUNT(per_id) have "+
				"FROM pos_requires NATURAL JOIN has_skill "+
				"WHERE pos_code = ? "+//00125
				"GROUP BY per_id), "+
				"missing_k AS( "+
				"    SELECT per_id, (need - have) AS deficit "+
				"    FROM have_k NATURAL JOIN ( "+
				"        SELECT COUNT(ks_code) AS NEED "+
				"        FROM pos_requires "+
				"        WHERE pos_code = ? "+//00125
				"    ) "+
				") "+
				"SELECT per_id, deficit AS missing_sk "+
				"FROM missing_k "+
				"WHERE deficit = ?  ";

		ResultSet rs = null;
		ArrayList<String[]> list = new ArrayList<String[]>();
		
		try {
			prst = connect.prepareStatement(query); //convert string query to prepared stmnt
			prst.setString(1, pos_code);//custom insert in query
			prst.setString(2, pos_code);
			prst.setString(3, numb);
			rs = prst.executeQuery();//execute query
		}catch(SQLException e) {//terrible way to catch exceptions
			System.out.println(e + " Method: query17 Class QueryResource (a)");
		}
		try {
			//pull in each line from the ResultSet
			while(rs.next()) {
				String[] line = new String[2];
				line[0] = rs.getString("per_id");
				line[1] = rs.getString("missing_sk");
				list.add(line);
			}
		}catch(SQLException q) {
			System.out.println(q + " Method: query17 Class QueryResource (b)");
		}
		
		return list;
	}//end method

	/**returns the people who are most qualified for position*/
	public ArrayList<String[]> query18(String pos_code) {
		query = "WITH have_k AS( "+
				"SELECT per_id, COUNT(per_id) have "+
				"FROM pos_requires NATURAL JOIN has_skill "+
				"WHERE pos_code = ? "+ //00125 
				"GROUP BY per_id), "+
				"missing_k AS( "+
				"    SELECT per_id, (need - have) AS deficit "+
				"    FROM have_k NATURAL JOIN ( "+
				"        SELECT COUNT(ks_code) AS NEED "+
				"        FROM pos_requires "+
				"        WHERE pos_code = ? "+ //00125
				"    ) "+
				") "+
				"SELECT per_id, least_numb "+
				"FROM missing_k, ( "+
				"    SELECT MIN(deficit) AS least_numb "+
				"    FROM missing_k) defic "+
				"WHERE deficit = least_numb ";

		ResultSet rs = null;
		ArrayList<String[]> list = new ArrayList<String[]>();
		
		try {
			prst = connect.prepareStatement(query); //convert string query to prepared stmnt
			prst.setString(1, pos_code);//custom insert in query
			prst.setString(2, pos_code);
			rs = prst.executeQuery();//execute query
		}catch(SQLException e) {//terrible way to catch exceptions
			System.out.println(e + " Method: query18 Class QueryResource (a)");
		}
		try {
			//pull in each line from the ResultSet
			while(rs.next()) {
				String[] line = new String[2];
				line[0] = rs.getString("per_id");
				line[1] = rs.getString("least_numb");
				list.add(line);
			}
		}catch(SQLException q) {
			System.out.println(q + " Method: query18 Class QueryResource (b)");
		}
		
		return list;
	}//end method

	/**lists the skills and courses a person needs*/
	public ArrayList<String[]> query19(String pos_code) {
		query = "WITH have_k AS( "+
			    "SELECT per_id, ks_code have "+
			    "FROM pos_requires NATURAL JOIN has_skill "+
			    "WHERE pos_code = ? "+
			"), "+
			"missing_k AS( "+
			"    (SELECT per_id, need "+
			"    FROM have_k, ( "+
			"        SELECT ks_code AS need "+
			"        FROM pos_requires "+
			"        WHERE pos_code = ? "+
			"    )) "+
			"    minus "+
			"    (SELECT per_id, ks_code have "+
			"    FROM has_skill) "+
			") "+
			"SELECT need, count(per_id) people_missing "+
			"FROM missing_k  "+
			"GROUP BY need  ";
		
		ResultSet rs = null;
		ArrayList<String[]> list = new ArrayList<String[]>();
		
		try {
			prst = connect.prepareStatement(query); //convert string query to prepared stmnt
			prst.setString(1, pos_code);//custom insert in query
			prst.setString(2, pos_code);
			rs = prst.executeQuery();//execute query
		}catch(Exception e) {//terrible way to catch exceptions
			System.out.println(e + " Method: query19 Class QueryResource (a)");
		}
		try {
			//pull in each line from the ResultSet
			while(rs.next()) {
				String[] line = new String[2];
				line[0] = rs.getString("need");
				line[1] = rs.getString("people_missing");
				list.add(line);
			}
		}catch(Exception q) {
			System.out.println(q + " Method: query19 Class QueryResource (b)");
		}
		
		return list;
	}//end method

	/***/
	public ArrayList<String[]> query20(String job_cate) {
		query = "SELECT per_id, first_name, last_name, title, hire_date, leave_date  "+
				"FROM person NATURAL JOIN works NATURAL JOIN position  NATURAL JOIN job_category "+ 
				"WHERE job_cate = job_cate AND job_cate = ?  ";//CS005
		
		
		ResultSet rs = null;
		ArrayList<String[]> list = new ArrayList<String[]>();
		
		try {
			prst = connect.prepareStatement(query); //convert string query to prepared stmnt
			prst.setString(1, job_cate);//custom insert in query
			rs = prst.executeQuery();//execute query
		}catch(SQLException e) {//terrible way to catch exceptions
			System.out.println(e + " Method: query20 Class QueryResource (a)");
		}
		try {
			//pull in each line from the ResultSet
			while(rs.next()) {
				String[] line = new String[6];
				line[0] = rs.getString("per_id");
				line[1] = rs.getString("first_name");
				line[2] = rs.getString("last_name");
				line[3] = rs.getString("title");
				line[4] = rs.getString("hire_date");
				String date = rs.getString("leave_date");
				if(date!=null)	
					line[5] = date;
				else
					line[5] = "null";
				list.add(line);
			}
		}catch(SQLException q) {
			System.out.println(q + " Method: query20 Class QueryResource (b)");
		}
		
		return list;
	}//end method

	/***/
	public ArrayList<String[]> query21A() {
		query = "WITH previous_rate AS ( "+
			    "SELECT * "+
			    "FROM (SELECT works.per_id, position.pay_rate AS prev_rate "+
			    "    FROM works NATURAL JOIN position CROSS JOIN ( "+
			    "            SELECT per_id "+
			    "            FROM works "+
			    "            WHERE leave_date IS NULL) A "+
			    "    WHERE leave_date IS NOT NULL AND A.per_id = works.per_id )) "+
			"SELECT COUNT(per_id) AS pay_increase "+
			"FROM previous_rate NATURAL JOIN "+
			"            (SELECT per_id, position.pay_rate AS current_rate "+
			"            FROM works NATURAL JOIN position "+
			"            WHERE works.leave_date IS NULL) A "+
			"WHERE prev_rate< current_rate  ";
		ResultSet rs = null;
		ArrayList<String[]> list = new ArrayList<String[]>();
		
		try {
			prst = connect.prepareStatement(query); //convert string query to prepared stmnt
			rs = prst.executeQuery();//execute query
		}catch(SQLException e) {//terrible way to catch exceptions
			System.out.println(e + " Method: query21a Class QueryResource (a)");
		}
		try {
			//pull in each line from the ResultSet
			while(rs.next()) {
				String[] line = new String[1];
				line[0] = rs.getString("pay_increase");
				list.add(line);
			}
		}catch(SQLException q) {
			System.out.println(q + " Method: query21a Class QueryResource (b)");
		}
		
		return list;
	}//end method
	
	/***/
	public ArrayList<String[]> query21B() {
		query = "WITH previous_rate AS ( "+
			    "SELECT * "+
			    "FROM (SELECT works.per_id, position.pay_rate AS prev_rate "+
			    "    FROM works NATURAL JOIN position CROSS JOIN ( "+
			    "            SELECT per_id "+
			    "            FROM works "+
			    "            WHERE leave_date IS NULL) A "+
			    "    WHERE leave_date IS NOT NULL AND A.per_id = works.per_id )) "+
			"SELECT  COUNT(per_id) AS pay_decrease "+
			"FROM previous_rate NATURAL JOIN "+
			"            (SELECT per_id, position.pay_rate AS current_rate "+
			"            FROM works NATURAL JOIN position "+
			"            WHERE works.leave_date IS NULL) A "+
			"WHERE prev_rate > current_rate ";
		
		ResultSet rs = null;
		ArrayList<String[]> list = new ArrayList<String[]>();
		
		try {
			prst = connect.prepareStatement(query); //convert string query to prepared stmnt
			rs = prst.executeQuery();//execute query
		}catch(SQLException e) {//terrible way to catch exceptions
			System.out.println(e + " Method: query21b Class QueryResource (a)");
		}
		try {
			//pull in each line from the ResultSet
			while(rs.next()) {
				String[] line = new String[1];
				line[0] = rs.getString("pay_decrease");
				list.add(line);
			}
		}catch(SQLException q) {
			System.out.println(q + " Method: query21b Class QueryResource (b)");
		}
		
		return list;
	}//end method
	
	/***/
	public ArrayList<String[]> query21C() {
		query = "WITH temp AS( "+
			    "SELECT * "+
			    "FROM ( "+
			    "    SELECT * "+
			    "    FROM (SELECT works.per_id, position.pay_rate AS prev_rate "+
			    "        FROM works NATURAL JOIN position CROSS JOIN ( "+
			    "            SELECT per_id "+
			    "            FROM works  "+
			    "            WHERE leave_date IS NULL) A "+ 
			    "        WHERE leave_date IS NOT NULL AND A.per_id = works.per_id )) NATURAL JOIN "+ 
			    "            (SELECT per_id, position.pay_rate AS current_rate "+
			    "            FROM works NATURAL JOIN position "+
			    "            WHERE works.leave_date IS NULL) A) "+
			"SELECT (numb_increase ||':'||numb_decrease) AS pay_ratio "+
			"FROM ( "+
			"    SELECT (SELECT COUNT(per_id) "+
			"            FROM temp "+
			"            WHERE current_rate > prev_rate) AS numb_increase , numb_decrease "+
			"    FROM (SELECT COUNT(per_id)AS numb_decrease "+
			"            FROM temp "+
			"            WHERE current_rate < prev_rate)) ";
		
		ResultSet rs = null;
		ArrayList<String[]> list = new ArrayList<String[]>();
		
		try {
			prst = connect.prepareStatement(query); //convert string query to prepared stmnt
			rs = prst.executeQuery();//execute query
		}catch(SQLException e) {//terrible way to catch exceptions
			System.out.println(e + " Method: query21c Class QueryResource (a)");
		}
		try {
			//pull in each line from the ResultSet
			while(rs.next()) {
				String[] line = new String[1];
				line[0] = rs.getString("pay_ratio");
				list.add(line);
			}
		}catch(SQLException q) {
			System.out.println(q + " Method: query21c Class QueryResource (b)");
		}
		
		return list;
	}//end method
	
	/***/
	public ArrayList<String[]> query21D(String industry_code) {
		query = "WITH temp AS( "+
			    "SELECT * "+
			    "FROM ( "+
			    "    SELECT * "+
			    "    FROM (SELECT works.per_id, position.pay_rate AS prev_rate "+
			    "        FROM sub_ind NATURAL JOIN company NATURAL JOIN works NATURAL JOIN position CROSS JOIN ( "+
			    "            SELECT per_id "+
			    "            FROM works "+
			    "            WHERE leave_date IS NULL) A "+
			    "        WHERE leave_date IS NOT NULL AND A.per_id = works.per_id  AND "+ 
			    "            industry_code = ?)) NATURAL JOIN "+//45102010
			    "            (SELECT per_id, position.pay_rate AS current_rate "+
			    "            FROM works NATURAL JOIN position "+
			    "            WHERE works.leave_date IS NULL) A) "+
			"SELECT  CAST(numb_increase AS INTEGER) / CAST(numb_decrease AS INTEGER) AS rate_of_change "+
			"FROM ( "+
			"    SELECT (SELECT COUNT(per_id) "+
			"            FROM temp "+
			"            WHERE current_rate > prev_rate) AS numb_increase , numb_decrease "+
			"    FROM (SELECT COUNT(per_id)AS numb_decrease "+
			"            FROM temp "+
			"            WHERE current_rate < prev_rate)) ";
		
		ResultSet rs = null;
		ArrayList<String[]> list = new ArrayList<String[]>();
		
		try {
			prst = connect.prepareStatement(query); //convert string query to prepared stmnt
			prst.setString(1, industry_code);//custom insert in query
			rs = prst.executeQuery();//execute query
		}catch(SQLException e) {//terrible way to catch exceptions
			System.out.println(e + " Method: query21d Class QueryResource (a)");
		}
		try {
			//pull in each line from the ResultSet
			while(rs.next()) {
				String[] line = new String[1];
				line[0] = rs.getString("rate_of_change");
				list.add(line);
			}
		}catch(SQLException q) {
			System.out.println(q + " Method: query21d Class QueryResource (b)");
		}
		
		return list;
	}//end method

	/***/
	public ArrayList<String[]> query22(String pos_code) {
		query = "WITH unemployed AS( "+
			    "SELECT per_id "+
			    "FROM ((SELECT per_id "+
			    "        FROM works) "+
			    "        MINUS "+
			    "        (SELECT per_id "+
			    "        FROM works NATURAL JOIN position "+
			    "        WHERE leave_date IS NULL)) "+ 
			    ") "+
			"SELECT * "+
			"FROM unemployed NATURAL JOIN works "+ 
			"WHERE pos_code = ? ";//06238
		
		ResultSet rs = null;
		ArrayList<String[]> list = new ArrayList<String[]>();
		
		try {
			prst = connect.prepareStatement(query); //convert string query to prepared stmnt
			prst.setString(1, pos_code);//custom insert in query
			rs = prst.executeQuery();//execute query
		}catch(SQLException e) {//terrible way to catch exceptions
			System.out.println(e + " Method: query22 Class QueryResource (a)");
		}
		try {
			//pull in each line from the ResultSet
			while(rs.next()) {
				String[] line = new String[4];
				line[0] = rs.getString("per_id");
				line[1] = rs.getString("pos_code");
				line[2] = rs.getString("hire_date");
				line[3] = rs.getString("leave_date");
				list.add(line);
			}
		}catch(SQLException q) {
			System.out.println(q + " Method: query22 Class QueryResource (b)");
		}
		
		return list;
	}//end method

	/***/
	public ArrayList<String[]> query23() {
		query =" WITH open_positions AS( "+
				   " SELECT pos_code, COUNT(pos_code) AS numb_open "+
				   " FROM position NATURAL JOIN( "+
				   " SELECT pos_code "+
				   " FROM position "+
				   " MINUS "+
				   " SELECT pos_code "+
				   " FROM works "+
				   " WHERE works.leave_date IS null "+
				   " ) "+
				   " GROUP BY pos_code "+
				"), "+
				"	unemployed AS( "+
				"    SELECT per_id "+
				   " FROM person "+
				   " MINUS "+
				   " SELECT per_id "+
				   " FROM (SELECT per_id "+
				    "        FROM works "+
				     "   WHERE works.leave_date IS null) "+
				"), "+
				"qualified_persons AS( "+
				"    SELECT pos_code, COUNT(per_id) AS numb_qual "+
				"    FROM open_positions O, unemployed U "+
				"    WHERE NOT EXISTS ( "+
				"        SELECT ks_code "+
				"        FROM pos_requires "+
				"        WHERE O.pos_code = pos_requires.pos_code "+
				"        MINUS "+
				"        SELECT ks_code "+
				"        FROM has_skill "+
				"        WHERE U.per_id = has_skill.per_id "+
				"        ) "+
				"        GROUP BY pos_code "+
				"), "+
				"differences AS ( "+
				"    SELECT pos_code, SUM(open_positions.numb_open - qualified_persons.numb_qual) AS diff "+
				"    FROM open_positions NATURAL JOIN qualified_persons "+
				"    GROUP BY pos_code "+
				") "+
				"SELECT job_cate "+
				"FROM differences NATURAL JOIN position "+
				"WHERE diff = (SELECT MAX(diff) FROM differences) ";
			
		ResultSet rs = null;
		ArrayList<String[]> list = new ArrayList<String[]>();
		
		try {
			prst = connect.prepareStatement(query); //convert string query to prepared stmnt
			rs = prst.executeQuery();//execute query
		}catch(SQLException e) {//terrible way to catch exceptions
			System.out.println(e + " Method: query23 Class QueryResource (a)");
		}
		try {
			//pull in each line from the ResultSet
			while(rs.next()) {
				String[] line = new String[1];
				line[0] = rs.getString("job_cate");
				list.add(line);
			}
		}catch(SQLException q) {
			System.out.println(q + " Method: query23 Class QueryResource (b)");
		}
		
		return list;
	}//end method

	/***/
	public ArrayList<String[]> query24(String pos_code, String per_id) {
		query = "WITH person_skills AS ( SELECT ks_code "+
			    "FROM has_skill WHERE per_id = ?), "+//10010
			"missing_skills AS ( SELECT pos_requires.pos_code, pos_requires.ks_code "+
			    "FROM pos_requires LEFT JOIN person_skills "+
			    "ON pos_requires.ks_code = person_skills.ks_code "+
			    "WHERE person_skills.ks_code IS NULL "+
			    "AND pos_requires.pos_code = ?), "+ //00999
			"course_for_missing_skills AS ( "+
			    "SELECT course.c_code, missing_skills.ks_code, retail_price "+
			    "FROM course "+
			    "INNER JOIN section "+
			    "ON course.c_code = section.c_code "+
			    "INNER JOIN teaches "+
			    "ON course.c_code = teaches.c_code "+
			    "INNER JOIN missing_skills "+
			    "ON teaches.ks_code = missing_skills.ks_code "+
			    "WHERE status = 'active'), "+
			"course_sets AS ( "+
			    "SELECT c1.c_code AS course_1, "+
			           "c2.c_code AS course_2, "+
			           "NULL AS course_3, "+
			           "c1.ks_code AS ks_1, "+
			           "c2.ks_code AS ks_2, "+
			           "NULL AS ks_3, "+
			           "ROUND(c1.retail_price + c2.retail_price, 2) AS total_cost "+
			    "FROM course_for_missing_skills c1 "+
			        "INNER JOIN course_for_missing_skills c2 "+
			        "ON c1.c_code < c2.c_code "+
			    "UNION "+
			    "SELECT c1.c_code AS course_1, "+
			           "c2.c_code AS course_2, "+
			           "c3.c_code AS course_3, "+
			           "c1.ks_code AS ks_1, "+
			           "c2.ks_code AS ks_2, "+
			           "c3.ks_code ks_3, "+
			           "ROUND(c1.retail_price + c2.retail_price + c3.retail_price, 2) AS total_cost "+
			    "FROM course_for_missing_skills c1 "+
			        "INNER JOIN course_for_missing_skills c2 "+
			        "ON c1.c_code < c2.c_code "+
			        "INNER JOIN course_for_missing_skills c3 "+
			        "ON c1.c_code < c3.c_code "+
			        "AND c2.c_code < c3.c_code), "+
			"course_set_for_skill AS ( "+
			    "SELECT ks_1 AS ks_code, course_1, course_2, course_3, total_cost "+
			    "FROM course_sets "+
			    "UNION "+
			    "SELECT ks_2 AS ks_code, course_1, course_2, course_3, total_cost "+
			    "FROM course_sets "+
			    "UNION "+
			    "SELECT ks_3 AS ks_code, course_1, course_2, course_3, total_cost "+
			    "FROM course_sets) "+
			"SELECT course_1, course_2, course_3, total_cost "+
			"FROM course_set_for_skill "+
			"GROUP BY course_1, course_2, course_3, total_cost "+
			"HAVING COUNT(DISTINCT ks_code) = (SELECT COUNT(*) FROM missing_skills) "+
			"ORDER BY total_cost ASC ";
		
		ResultSet rs = null;
		ArrayList<String[]> list = new ArrayList<String[]>();
		
		try {
			prst = connect.prepareStatement(query); //convert string query to prepared stmnt
			prst.setString(1, per_id);
			prst.setString(2, pos_code);
			rs = prst.executeQuery();//execute query
		}catch(SQLException e) {//terrible way to catch exceptions
			System.out.println(e + " Method: query24 Class QueryResource (a)");
		}
		try {
			//pull in each line from the ResultSet
			while(rs.next()) {
				String[] line = new String[4];
				try {line[0] = rs.getString("course_1");}catch(NullPointerException r) {line[0] = "null";}
				try {line[1] = rs.getString("course_2");}catch(NullPointerException r) {line[1] = "null";}
				try {line[2] = rs.getString("course_3");}catch(NullPointerException r) {line[2] = "null";}
				try {line[3] = rs.getString("total_cost");}catch(NullPointerException r) {line[3] = "null";}
				list.add(line);
			}
		}catch(SQLException q) {
			System.out.println(q + " Method: query24 Class QueryResource (b)");
		}
		
		return list;
	}//end method

	/***/
	public ArrayList<String[]> query25() {
		query = "WITH open_positions AS( "+
			    "SELECT pos_code, COUNT(pos_code) AS numb_open "+
			    "FROM position NATURAL JOIN( "+
			    "SELECT pos_code "+
			    "FROM position "+
			    "MINUS "+
			    "SELECT pos_code "+
			    "FROM works "+
			    "WHERE works.leave_date IS null "+
			    ") "+
			    "GROUP BY pos_code "+
			"), "+
			"unemployed AS( "+
			    "SELECT per_id "+
			    "FROM person "+
			    "MINUS "+
			    "SELECT per_id "+
			    "FROM (SELECT per_id "+
			     "       FROM works "+
			     "   WHERE works.leave_date IS null) "+
			"), "+
			"qualified_persons AS( "+
			 "   SELECT pos_code, COUNT(per_id) AS numb_qual "+
			  "  FROM open_positions O, unemployed U "+
			   " WHERE NOT EXISTS ( "+
			    "    SELECT ks_code "+
			     "   FROM pos_requires "+
			      "  WHERE O.pos_code = pos_requires.pos_code "+
			       " MINUS "+
			        "SELECT ks_code "+
			        "FROM has_skill "+
			        "WHERE U.per_id = has_skill.per_id "+
			        ") "+
			        "GROUP BY pos_code "+
			"), "+
			"differences AS ( "+
			 "   SELECT pos_code, SUM(open_positions.numb_open - qualified_persons.numb_qual) AS diff "+
			  "  FROM open_positions NATURAL JOIN qualified_persons "+
			   " GROUP BY pos_code "+
			"), "+
			"courses AS ( "+
			  "  SELECT T.c_code, COUNT(DISTINCT U.per_id) AS numb_per_course_qual "+
			   " FROM teaches T NATURAL JOIN open_positions NATURAL JOIN pos_requires, unemployed U "+
			    "WHERE NOT EXISTS( "+
			     "   SELECT pos_requires.ks_code "+
			      "  FROM pos_requires INNER JOIN position "+
			       " ON pos_requires.pos_code = position.pos_code "+
			        "WHERE position.job_cate = ( "+
			        "    SELECT job_cate "+
			         "   FROM differences NATURAL JOIN position "+
			          "  WHERE diff = (SELECT MAX(diff) FROM differences)) "+
			           " MINUS "+
			            "SELECT has_skill.ks_code "+
			            "FROM has_skill "+
			            "WHERE has_skill.per_id = U.per_id "+
			            "MINUS "+
			            "(SELECT ks_code "+
			             "FROM teaches "+
			             "WHERE teaches.c_code = T.c_code) "+
			            ") "+
			        "GROUP BY T.c_code "+
			        ") "+
			"SELECT title, c_code, numb_per_course_qual "+
			"FROM courses NATURAL JOIN course "+
			"WHERE numb_per_course_qual = (SELECT MAX(numb_per_course_qual) FROM courses) ";
		
		ResultSet rs = null;
		ArrayList<String[]> list = new ArrayList<String[]>();
		
		try {
			prst = connect.prepareStatement(query); //convert string query to prepared stmnt
			rs = prst.executeQuery();//execute query
		}catch(SQLException e) {//terrible way to catch exceptions
			System.out.println(e + " Method: query25 Class QueryResource (a)");
		}
		try {
			//pull in each line from the ResultSet
			while(rs.next()) {
				String[] line = new String[3];
				line[0] = rs.getString("title");
				line[1] = rs.getString("c_code");
				line[2] = rs.getString("numb_per_course_qual");
				list.add(line);
			}
		}catch(SQLException q) {
			System.out.println(q + " Method: query25 Class QueryResource (b)");
		}
		
		return list;
	}//end method

	/***/
	public ArrayList<String[]> query26(String pos_code, String per_id) {
		query = "WITH skills_needed AS( "+
			    "SELECT ks_code need, c_code "+
			    "FROM pos_requires NATURAL JOIN teaches "+
			    "WHERE pos_code = ? "+ //00444
			"), "+
			"temp AS( "+
			    "SELECT course.c_code, prereq "+
			    "FROM skills_needed LEFT OUTER JOIN course "+
			    "ON course.prereq IS NOT NULL "+
			    "    AND skills_needed.c_code = course.c_code "+
			    "WHERE course.c_code IS NOT NULL "+
			"), "+
			"temp2 AS( "+
			    "SELECT course.c_code, course.prereq "+ 
			    "FROM temp LEFT OUTER JOIN course "+
			    "ON course.prereq IS NOT NULL "+
			    "    AND temp.prereq = course.c_code "+
			    "WHERE course.c_code IS NOT NULL "+ 
			"), "+
			"temp3 AS( "+
			 "   (SELECT course.c_code, course.prereq "+ 
			 "   FROM temp2 LEFT OUTER JOIN course "+ 
			 "   ON course.prereq IS NOT NULL "+
			 "       AND temp2.prereq = course.c_code "+
			 "   WHERE course.c_code IS NOT NULL) "+
			 "   UNION "+
			 "   (SELECT course.c_code, course.prereq "+ 
			 "   FROM temp LEFT OUTER JOIN course "+ 
			 "   ON course.prereq IS NOT NULL "+
			 "       AND temp.prereq = course.c_code "+
			 "   WHERE course.c_code IS NOT NULL) "+
			 "   UNION "+
			 "   (SELECT course.c_code, prereq "+
			 "   FROM skills_needed LEFT OUTER JOIN course "+ 
			 "   ON course.prereq IS NOT NULL "+
			 "       AND skills_needed.c_code = course.c_code "+
			 "   WHERE course.c_code IS NOT NULL) "+
			 "   UNION "+
			 "   SELECT ks_code need, c_code "+
			 "   FROM pos_requires NATURAL JOIN teaches "+
			 "   WHERE pos_code = ? "+ //00444
			"), "+
			"temp4 AS( "+
			    "SELECT DISTINCT prereq AS course_id, ks_code AS req_skill "+
			    "FROM temp3, teaches "+
			    "WHERE prereq = teaches.c_code "+
			") "+
			"SELECT course_id, req_skill "+
			"FROM temp4,( "+
			 "   (SELECT req_skill AS ks_code "+
			  "  FROM temp4) "+
			   " MINUS "+
			    "(SELECT ks_code "+
			    "FROM has_skill "+
			    "WHERE per_id = ?) "+//10010
			    ") "+
			"WHERE req_skill = ks_code ";
		
		ResultSet rs = null;
		ArrayList<String[]> list = new ArrayList<String[]>();
		
		try {
			prst = connect.prepareStatement(query); //convert string query to prepared stmnt
			prst.setString(1,pos_code);
			prst.setString(2, pos_code);
			prst.setString(3, per_id);
			rs = prst.executeQuery();//execute query
		}catch(SQLException e) {
			System.out.println(e + " Method: query26 Class QueryResource (a)");
		}
		try {
			//pull in each line from the ResultSet
			while(rs.next()) {
				String[] line = new String[2];
				line[0] = rs.getString("course_id");
				line[1] = rs.getString("req_skill");
				list.add(line);
			}
		}catch(SQLException q) {
			System.out.println(q + " Method: query26 Class QueryResource (b)");
		}
		
		return list;
	}//end method
	
}//end class
//789--769
//1558

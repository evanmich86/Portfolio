/**
 * add/delete: course, teaches, knowledge_skill, certificate, issues
 * add/delete: company, position, pos_requires, requires_cert
 * add/delete: person, works, has_skill, has_certs
 * */

import java.util.Scanner;
import java.sql.*;

public class ModifyResource {
	
	private Scanner input = new Scanner(System.in);
	private Connection connect;
	
	public ModifyResource(JdbcConnection jdbc) {
		this.connect = jdbc.connect();
	}
	
	
	
	public void addCourse() {
		String[] args = courseMenu();//prompt user for info
		String insert = String.format("INSERT INTO course(c_code, title, tier, description, status, retail_price, prereq, institution) "
				+ "VALUES (?,?,?,?,?,?,?,?)");
		
		PreparedStatement prst = null;
		
		try {
			prst = connect.prepareStatement(insert);
			loadPrst(prst, args);//insert strings for prepared statement
			prst.executeUpdate();
			connect.commit();
		}catch(SQLException e) {System.out.println(e + " ModifiyResource.addCourse (a)");}
		
	}//end method
	private String[] courseMenu() {
		String[] args = new String[8];
		System.out.println("Enter 10 or less character course  code. [ex 000211]\n");
		args[0] = checkInput(input.nextLine(),10);
		System.out.println("Enter 100 or less character course title. [ex Computational Theory]\n");
		args[1] = checkInput(input.nextLine(),100);
		System.out.println("Enter 15 or less character course level. [ex. beginner]\n");
		args[2] = checkInput(input.nextLine(),10);
		System.out.println("Enter 100 or less character course discription. [ex Study of abstract machines]\n");
		args[3] = checkInput(input.nextLine(),100);
		System.out.println("Enter 15 or less character course status. [ex closed]\n");
		args[4] = checkInput(input.nextLine(),15);
		System.out.println("Enter 10 or less character retail price for this course. [ex. 829.88]\n");
		args[5] = checkInput(input.nextLine(),10);
		System.out.println("Enter 10 or less character prereq course code or null. (course code must exist) [ex. 00522]\n");
		args[6] = checkInput(input.nextLine(),10);
		System.out.println("Enter 40 or less character name of institution that teaches course. [ex. University of Kitty_cats]\n");
		args[7] = checkInput(input.nextLine(),40);
		return args;
	}//end method
	
	public void deleteCourse() {
		System.out.println("Enter course code of course to delete. [ex. 00511]");
		deleteCourse(checkInput(input.nextLine(),10));
	}
	private void deleteCourse(String c_code) {
		String deleteCourse = "DELETE FROM course WHERE c_code = ?";
		deleteTeaches(c_code);
		
		PreparedStatement prst = null;
		
		try {
			prst = connect.prepareStatement(deleteCourse);
			prst.setString(1, c_code);//insert strings for prepared statement
			prst.executeUpdate();
			connect.commit();
		}catch(Exception e) {System.out.println(e + " ModifiyResource.deleteCourse (a)");}
	}//end method
	
	public void addTeaches() {
		String[] args = teachesMenu();//prompt user for info
		String insert = String.format("INSERT INTO teaches VALUES (?,?)");
		
		PreparedStatement prst = null;
		
		try {
			prst = connect.prepareStatement(insert);
			loadPrst(prst, args);//insert strings for prepared statement
			prst.executeUpdate();
			connect.commit();
		}catch(Exception e) {System.out.println(e + " ModifiyResource.addTeaches (a)");}
	}//end method
	private String[] teachesMenu() {
		String[] args = new String[2];
		System.out.println("Enter 10 or less charater course  code. (Must exist in DB) [ex 000292]\n");
		args[0] = checkInput(input.nextLine(),10);
		System.out.println("Enter 10 or less knowledge skill code. (Must exist in DB)[ex 12222]\n");
		args[1] = checkInput(input.nextLine(),10);
		return args;
	}
	
	public void deleteTeaches() {
		String[] args = new String[2];
		System.out.println("Enter c_code or c_code and ks_code to delete. [ex 00511 or 00511 10955]\n");
		Scanner stringScan = new Scanner(input.nextLine());
		args[0] = checkInput(stringScan.next(),10);
		if(stringScan.hasNext()) {
			args[1] = checkInput(stringScan.next(),10);
			deleteTeaches(args[0],args[1]);
		}else {
			deleteTeaches(args[0]);
		}
		stringScan.close();
	}//end method
	private void deleteTeaches(String c_code) {
		String deleteTeaches = "DELETE FROM teaches WHERE c_code = ?";
		PreparedStatement prst = null;
		try {
			prst = connect.prepareStatement(deleteTeaches);
			prst.setString(1, c_code);//insert strings for prepared statement
			prst.executeUpdate();
			connect.commit();
		}catch(SQLException e) {System.out.println(e + " ModifiyResource.deleteTeaches (a)");}
	}
	private void deleteTeaches(String c_code, String ks_code) {//course no longer teaches a certain skill
		String deleteTeaches = "DELETE FROM teaches WHERE c_code = ? AND ks_code = ?";
		PreparedStatement prst = null;
		try {
			prst = connect.prepareStatement(deleteTeaches);
			prst.setString(1, c_code);//insert strings for prepared statement
			prst.setString(2, ks_code);
			prst.executeUpdate();
			connect.commit();
		}catch(SQLException e) {System.out.println(e + " ModifiyResource.deleteTeaches (b)");}
	}
	
	public void addKnowledgeSkill() {
		//course must be added first
		String[] args = ksMenu();//prompt user for info
		String insert = String.format("INSERT INTO knowledge_skill VALUES (?,?,?,?,?)");
		
		PreparedStatement prst = null;
		
		try {
			prst = connect.prepareStatement(insert);
			loadPrst(prst, args);//insert strings for prepared statement
			prst.executeUpdate();
			connect.commit();
		}catch(SQLException e) {System.out.println(e + " ModifiyResource.addKnowledgeSkill (a)");}
	}
	private String[] ksMenu(){
		String[] args= new String[5];
		System.out.println("Enter 10 or less charater ks_code. [ex 11011]\n");
		args[0] = checkInput(input.nextLine(),10);
		System.out.println("Enter 50 or less character skill title. [ex Discrete Mathematics]\n");
		args[1] = checkInput(input.nextLine(),50);
		System.out.println("Enter 100 or less character skill discription. [ex Study of fundamentally discrete math structures]\n");
		args[2] = checkInput(input.nextLine(),100);
		System.out.println("Enter 15 or less character skill level. [ex Advance]");
		args[3] = checkInput(input.nextLine(),15);
		System.out.println("Enter 10 character course code that teaches this skill. [ex. 00522]");
		args[4] = checkInput(input.nextLine(),10);//will most likely have to set this one manually
		return args;
	}
	
	public void deleteKnowledgeSkill() {
		System.out.println("Enter ks_code of the skill to be deleted. [ex. 11200]");
		deleteKnowledgeSkill(checkInput(input.next(),10));
	}
	private void deleteKnowledgeSkill(String ks_code) {
		String deleteKnowledgeSkill = "DELETE FROM knowledgeSkills WHERE ks_code = ?";
		PreparedStatement prst = null;
		try {
			prst = connect.prepareStatement(deleteKnowledgeSkill);
			prst.setString(1, ks_code);//insert strings for prepared statement
			prst.executeUpdate();
			connect.commit();
		}catch(SQLException e) {System.out.println(e + " ModifiyResource.deleteKnowledgeSkill (a)");}
	}
	
	public void addCert() {
		String[] args = certMenu();
		String insert = "INSERT INTO certificate VALUES (?,?,?,?,?)";
		
		PreparedStatement prst = null;
		
		try {
			prst = connect.prepareStatement(insert);
			loadPrst(prst, args);//insert strings for prepared statement
			prst.executeUpdate();
			connect.commit();
		}catch(SQLException e) {System.out.println(e + " ModifiyResource.addCert (a)");}
		
	}
	private String[] certMenu() {
		String args[] = new String[5];
		System.out.println("Enter 10 or less character certificate code [ex. 0202]\n");
		args[0] = checkInput(input.nextLine(),10);
		System.out.println("Enter 10 or less character t_code or null. [ex. 55055]\n");
		args[1] = checkInput(input.nextLine(),10);
		System.out.println("Enter 50 or less character description. [ex. Sql Certification]\n");
		args[2] = checkInput(input.nextLine(),50);
		System.out.println("Enter 10 character expiration date or null if not applicable. [ex. mm/dd/yyyy]\n");
		args[3] = checkInput(input.nextLine(),10);
		System.out.println("Enter 50 or less character issuing institution. [ex. Jimbob's skoolin]");
		args[4] = checkInput(input.nextLine(),50);
		return args;
	}
	
	public void deleteCert() {
		System.out.println("Enter certification code of certifacte to delete. [ex. 0511]");
		deleteCert(checkInput(input.nextLine(),10));
	}
	private void deleteCert(String cert_code) {
		String deleteCert = "DELETE FROM certificate WHERE cert_code = ?";
		deleteHas_cert(cert_code);
		deleteIssues(cert_code);
		
		PreparedStatement prst = null;
		
		try {
			prst = connect.prepareStatement(deleteCert);
			prst.setString(1, cert_code);//insert strings for prepared statement
			prst.executeUpdate();
			connect.commit();
		}catch(SQLException e) {System.out.println(e + " ModifiyResource.deleteCourse (a)");}
	}//end method
	
	public void addIssues() {
		String[] args = issueMenu();
		String insert = "INSERT INTO issues VALUES (?,?)";
		
		PreparedStatement prst = null;
		
		try {
			prst = connect.prepareStatement(insert);
			loadPrst(prst, args);//insert strings for prepared statement
			prst.executeUpdate();
			connect.commit();
		}catch(SQLException e) {System.out.println(e + " ModifiyResource.addissues (a)");}
	}
	private String[] issueMenu() {
		String[] args = new String[2];
		System.out.println("Enter 10 or less charater issuing course code. [ex 00001]\n");
		args[0] = checkInput(input.nextLine(),10);
		System.out.println("Enter 10 or less character certification code. [ex. 4293]\n");
		args[1] = checkInput(input.nextLine(),10);
		return args;
	}
	
	public void deleteIssues() {
		String[] temp = new String[2];
		System.out.println("Enter certification code or certifact code and course code. [ex. 0511 or 0511 0123]");
		Scanner stringScan = new Scanner (input.nextLine());
		temp[0] = stringScan.next();
		if(stringScan.hasNext()) {
			temp[1] = stringScan.next();
			deleteIssues(temp[0],temp[1]);
		}else {
			deleteIssues(checkInput(temp[0],10));
		}
		stringScan.close();
	}//end
	private void deleteIssues(String cert_code) {
		String deleteIssues = "DELETE FROM issues WHERE cert_code = ?";
		
		PreparedStatement prst = null;
		
		try {
			prst = connect.prepareStatement(deleteIssues);
			prst.setString(1, cert_code);//insert strings for prepared statement
			prst.executeUpdate();
			connect.commit();
		}catch(SQLException e) {System.out.println(e + " ModifiyResource.deleteIssues (a)");}
		
	}//end method
	private void deleteIssues(String cert_code, String c_code) {
		String deleteIssues = "DELETE FROM issues WHERE cert_code = ? AND c_code = ?";
		
		PreparedStatement prst = null;
		
		try {
			prst = connect.prepareStatement(deleteIssues);
			prst.setString(1, cert_code);//insert strings for prepared statement
			prst.setString(2, c_code);
			prst.executeUpdate();
			connect.commit();
		}catch(SQLException e) {System.out.println(e + " ModifiyResource.deleteIssues (b)");}
	}//end method
	
	public void addPosition() {
		String[] args = positionMenu();
		String insert = "INSERT INTO position VALUES (?,?,?,?,?,?,?)";
		
		PreparedStatement prst = null;
		
		try {
			prst = connect.prepareStatement(insert);
			loadPrst(prst, args);//insert strings for prepared statement
			prst.executeUpdate();
			connect.commit();
		}catch(SQLException e) {System.out.println(e + " ModifiyResource.addPosition (a)");}
		
	}
	private String[] positionMenu() {
		String[] args = new String[7];
		System.out.println("Enter 10 or less character position code code. (primary key)[ex 00445]\n");
		args[0] = checkInput(input.nextLine(),10);
		System.out.println("Enter 70 or less character position title. [ex. Senior Software Developer]\n");
		args[1] = checkInput(input.nextLine(),70);
		System.out.println("Enter 15 or less charater employee mode or null. [ex null]\n");
		args[2] = checkInput(input.nextLine(),15);
		System.out.println("Enter 12 or less character pay rate. [ex. 75000.00]\n");
		args[3] = checkInput(input.nextLine(),12);
		System.out.println("Enter 10 or less charater pay type. [ex salary]\n");
		args[4] = checkInput(input.nextLine(),10);
		System.out.println("Enter 10 or less character job category code. (foreign key)[ex. CS006]\n");
		args[5] = checkInput(input.nextLine(),10);
		System.out.println("Enter 10 or less character company id. (foreign key)[ex. 101456]\n");
		args[6] = checkInput(input.nextLine(),10);
		return args;
	}
	
	public void deletePosition() {
		
	}
	
	public void addCompany() {
		String[] args = companyMenu();
		String insert = "INSERT INTO company VALUES (?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement prst = null;
		
		try {
			prst = connect.prepareStatement(insert);
			loadPrst(prst, args);//insert strings for prepared statement
			prst.executeUpdate();
			connect.commit();
		}catch(SQLException e) {System.out.println(e + " ModifiyResource.addCompany (a)");}
	}
	private String[] companyMenu() {
		String[] args = new String[11];
		System.out.println("Enter 10 or less charater company id. [ex 101458]\n");
		args[0] = checkInput(input.nextLine(),10);
		System.out.println("Enter 70 or less character company name. [ex. Senior Software Developer]\n");
		args[1] = checkInput(input.nextLine(),70);
		System.out.println("Enter 30 or less charater city name. [ex Houston]\n");
		args[2] = checkInput(input.nextLine(),30);
		System.out.println("Enter 30 or less character street name. [ex. Morgus ave.]\n");
		args[3] = checkInput(input.nextLine(),30);
		System.out.println("Enter 5 or less charater street number. [ex 12001]\n");
		args[4] = checkInput(input.nextLine(),5);
		System.out.println("Enter 10 or less character apt number. [ex. V302]\n");
		args[5] = checkInput(input.nextLine(),10);
		System.out.println("Enter 2 charater state. [ex TX]\n");
		args[6] = checkInput(input.nextLine(),2);
		System.out.println("Enter 15 or less character country. [ex. USA]\n");
		args[7] = checkInput(input.nextLine(),15);
		System.out.println("Enter 5 digit zip code. [ex 70686]\n");
		args[8] = checkInput(input.nextLine(),5);
		System.out.println("Enter 40 or less charater industry group. [ex Security]\n");
		args[9] = checkInput(input.nextLine(),10);
		System.out.println("Enter 40 or less character website. [ex. www.bestjob/ever.com]\n");
		args[10] = checkInput(input.nextLine(),40);
		return args;
	}
	
	public void deleteCompany() {
		
	}
	
	public void addPos_req() {
		String[] args = posReqMenu();
		String insert = "INSERT INTO pos_requires VALUES (?,?,?)";
		
		PreparedStatement prst = null;
		
		try {
			prst = connect.prepareStatement(insert);
			loadPrst(prst, args);//insert strings for prepared statement
			prst.executeUpdate();
			connect.commit();
		}catch(SQLException e) {System.out.println(e + " ModifiyResource.addPos_req (a)");}
	}
	private String[] posReqMenu() {
		String[] args = new String[3];
		System.out.println("Enter 10 or less character knowledge skill code. [ex 10101]\n");
		args[0] = checkInput(input.nextLine(),10);
		System.out.println("Enter 10 or less character position code [ex. 00449]\n");
		args[1] = checkInput(input.nextLine(),10);
		System.out.println("Enter null for tier. It's useless. [ex. null]\n");
		args[2] = checkInput(input.nextLine(),10);
		return args;
	}
	
	public void deletePos_req() {
		
	}
	
	public void addReq_cert() {
		String[] args = reqCertMenu();
		String insert = "INSERT INTO requires_cert VALUES (?,?)";
		
		PreparedStatement prst = null;
		
		try {
			prst = connect.prepareStatement(insert);
			loadPrst(prst, args);//insert strings for prepared statement
			prst.executeUpdate();
			connect.commit();
		}catch(Exception e) {System.out.println(e + " ModifiyResource.addReq_cert (a)");}
	}
	private String[] reqCertMenu() {
		String[] args = new String[2];
		System.out.println("Enter 10 or less character position code. [ex 00225]\n");
		args[0] = checkInput(input.nextLine(),10);
		System.out.println("Enter 10 or less character certificate code [ex. 0628]\n");
		args[1] = checkInput(input.nextLine(),10);
		return args;
	}
	
	public void deleteReq_cert() {
		
	}
	public void addPerson() {
		String[] args = personMenu();
		String insert = "INSERT INTO person VALUES (?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement prst = null;
		
		try {
			prst = connect.prepareStatement(insert);
			loadPrst(prst, args);//insert strings for prepared statement
			prst.executeUpdate();
			connect.commit();
		}catch(SQLException e) {System.out.println(e + " ModifiyResource.addPerson (a)");}
	}
	private String[] personMenu() {
		String[] args = new String[11];
		System.out.println("Enter 10 or less charater person id. [ex 10011]\n");
		args[0] = checkInput(input.nextLine(),10);
		System.out.println("Enter 15 or less character first name. [ex. Jamie]\n");
		args[1] = checkInput(input.nextLine(),15);
		System.out.println("Enter 25 or less charater last name. [ex Holinoganfaz]\n");
		args[2] = checkInput(input.nextLine(),25);
		System.out.println("Enter 15 or less character city name. [ex. Brown Bear.]\n");
		args[3] = checkInput(input.nextLine(),15);
		System.out.println("Enter 40 or less charater street name. [ex whistle blower st]\n");
		args[4] = checkInput(input.nextLine(),10);
		System.out.println("Enter 5 or less charater street number. [ex 1006]\n");
		args[5] = checkInput(input.nextLine(),5);
		System.out.println("Enter 10 or less character apt number. [ex. 11L]\n");
		args[6] = checkInput(input.nextLine(),10);
		System.out.println("Enter 2 charater state. [ex MN]\n");
		args[7] = checkInput(input.nextLine(),2);
		System.out.println("Enter 25 or less character country. [ex. United States of America]\n");
		args[8] = checkInput(input.nextLine(),25);
		System.out.println("Enter 5 digit zip code. [ex 50080]\n");
		args[9] = checkInput(input.nextLine(),5);
		System.out.println("Enter 50 or less character email. [ex. JholgIsATurtle12@aol.com]\n");
		args[10] = checkInput(input.nextLine(),50);
		return args;
	}
	
	public void deletePerson() {
		
	}
	
	public void addHas_skill() {
		String[] args = hasMenu();
		String insert = "INSERT INTO has_skill VALUES (?,?)";
		
		PreparedStatement prst = null;
		
		try {
			prst = connect.prepareStatement(insert);
			loadPrst(prst, args);//insert strings for prepared statement
			prst.executeUpdate();
			connect.commit();
		}catch(SQLException e) {System.out.println(e + " ModifiyResource.addHas_skill (a)");}
	}
	private String[] hasMenu() {
		String[] args = new String[2];
		System.out.println("Enter 10 or less charater person id. [ex 10011]\n");
		args[0] = checkInput(input.nextLine(),10);
		System.out.println("Enter 10 or less character skill code. [ex. 10955]\n");
		args[1] = checkInput(input.nextLine(),10);
		return args;
	}
	
	public void deleteHas_skill() {
		String[] temp = new String[2];
		System.out.println("Enter person id or person id and knowledge skill code. [ex. 10001 or 10001 10955]");
		Scanner stringScan = new Scanner (input.nextLine());
		temp[0] = stringScan.next();
		if(stringScan.hasNext()) {
			temp[1] = stringScan.next();
			deleteHas_skill(temp[0],temp[1]);
		}else {
			deleteHas_skill(checkInput(temp[0],10));
		}
		stringScan.close();
	}//end
	private void deleteHas_skill(String cert_code) {
		String deleteCourse = "DELETE FROM has_skill WHERE per_id = ?";
		
		PreparedStatement prst = null;
		
		try {
			prst = connect.prepareStatement(deleteCourse);
			prst.setString(1, cert_code);//insert strings for prepared statement
			prst.executeUpdate();
			connect.commit();
		}catch(SQLException e) {System.out.println(e + " ModifiyResource.deleteHas_skill (a)");}
		
	}//end method
	private void deleteHas_skill(String cert_code, String per_id) {
		String deleteCourse = "DELETE FROM has_skill WHERE per_id = ? AND ks_code = ?";
		
		PreparedStatement prst = null;
		
		try {
			prst = connect.prepareStatement(deleteCourse);
			prst.setString(1, cert_code);//insert strings for prepared statement
			prst.setString(2, per_id);
			prst.executeUpdate();
			connect.commit();
		}catch(SQLException e) {System.out.println(e + " ModifiyResource.deleteHas_skill (b)");}
	}//end method
	
	public void addWorks() {
		String[] args = worksMenu();
		String insert = "INSERT INTO works VALUES (?,?,?,?)";
		
		PreparedStatement prst = null;
		
		try {
			prst = connect.prepareStatement(insert);
			loadPrst(prst, args);//insert strings for prepared statement
			prst.executeUpdate();
			connect.commit();
		}catch(SQLException e) {System.out.println(e + " ModifiyResource.addWorks (a)");}
	}
	private String[] worksMenu() {
		String[] args = new String[4];
		System.out.println("Enter 10 or less character person's id. [ex 100A5]\n");
		args[0] = checkInput(input.nextLine(),10);
		System.out.println("Enter 10 or less character position code [ex. 10628]\n");
		args[1] = checkInput(input.nextLine(),10);
		System.out.println("Enter 10 or less character hire date. (mm/dd/yyyy) [ex. 01/01/2020]\n");
		args[2] = checkInput(input.nextLine(),10);
		System.out.println("Enter 10 or less character leave date or null. (mm/dd/yyyy) [ex. 01/01/2020]\n");
		args[3] = checkInput(input.nextLine(),10);
		return args;
	}
	
	public void deleteWorks() {
		String[] temp = new String[2];
		System.out.println("Enter person's id or  person id and company id. [ex. 10500 or 10511 10500]");
		Scanner stringScan = new Scanner (input.nextLine());
		temp[0] = stringScan.next();
		if(stringScan.hasNext()) {
			temp[1] = stringScan.next();
			deleteWorks(temp[0],temp[1]);
		}else {
			deleteWorks(checkInput(temp[0],10));
		}
		stringScan.close();
	}//end
	private void deleteWorks(String cert_code) {
		String deleteCourse = "DELETE FROM works WHERE cert_code = ?";
		
		PreparedStatement prst = null;
		
		try {
			prst = connect.prepareStatement(deleteCourse);
			prst.setString(1, cert_code);//insert strings for prepared statement
			prst.executeUpdate();
			connect.commit();
		}catch(SQLException e) {System.out.println(e + " ModifiyResource.deleteHas_cert (a)");}
		
	}//end method
	private void deleteWorks(String cert_code, String per_id) {
		String deleteCourse = "DELETE FROM works WHERE cert_code = ? AND per_id = ?";
		
		PreparedStatement prst = null;
		
		try {
			prst = connect.prepareStatement(deleteCourse);
			prst.setString(1, cert_code);//insert strings for prepared statement
			prst.setString(2, per_id);
			prst.executeUpdate();
			connect.commit();
		}catch(SQLException e) {System.out.println(e + " ModifiyResource.deleteHas_cert (b)");}
	}//end method
	
	public void addHas_cert() {
		String[] args = hasCertMenu();
		String insert = "INSERT INTO has_cert VALUES (?,?)";
		
		PreparedStatement prst = null;
		
		try {
			prst = connect.prepareStatement(insert);
			loadPrst(prst, args);//insert strings for prepared statement
			prst.executeUpdate();
			connect.commit();
		}catch(SQLException e) {System.out.println(e + " ModifiyResource.addHas_cert (a)");}
	}
	private String[] hasCertMenu() {
		String[] args = new String[2];
		System.out.println("Enter 10 or less charater person's id. [ex 10C2F]\n");
		args[0] = checkInput(input.nextLine(),10);
		System.out.println("Enter 10 or less character certificate code [ex. 10628]\n");
		args[1] = checkInput(input.nextLine(),10);
		return args;
	}
	
	public void deleteHas_cert() {
		String[] temp = new String[2];
		System.out.println("Enter certificate code or certificate code and person id. [ex. 0511 or 0511 10123]");
		Scanner stringScan = new Scanner (input.nextLine());
		temp[0] = stringScan.next();
		if(stringScan.hasNext()) {
			temp[1] = stringScan.next();
			deleteHas_cert(temp[0],temp[1]);
		}else {
			deleteHas_cert(checkInput(temp[0],10));
		}
		stringScan.close();
	}//end
	private void deleteHas_cert(String cert_code) {
		String deleteCourse = "DELETE FROM has_cert WHERE cert_code = ?";
		
		PreparedStatement prst = null;
		
		try {
			prst = connect.prepareStatement(deleteCourse);
			prst.setString(1, cert_code);//insert strings for prepared statement
			prst.executeUpdate();
			connect.commit();
		}catch(SQLException e) {System.out.println(e + " ModifiyResource.deleteHas_cert (a)");}
		
	}//end method
	private void deleteHas_cert(String cert_code, String per_id) {
		String deleteCourse = "DELETE FROM has_cert WHERE cert_code = ? AND per_id = ?";
		
		PreparedStatement prst = null;
		
		try {
			prst = connect.prepareStatement(deleteCourse);
			prst.setString(1, cert_code);//insert strings for prepared statement
			prst.setString(2, per_id);
			prst.executeUpdate();
			connect.commit();
		}catch(SQLException e) {System.out.println(e + " ModifiyResource.deleteHas_cert (b)");}
	}//end method
	
	//method user to correct sloppy inputs from user
	public String checkInput(String arg, int size) {
		//chop off anything past the required size
		if(arg.length()>size) {
			String temp = "";
			char[] c = arg.toCharArray();
			for(int i=0;i<c.length;i++) {
				temp+=c[i];
			}
			return temp.trim();
		}else {
			return arg.trim();
		}
	}//end method
	
	//method used to set all of the strings of a prepared statement
	public void loadPrst(PreparedStatement prst, String[] args) {
		for(int i=0;i<args.length;i++) {
			try {
				if(args[i].equals("null"))
					prst.setNull(i+1,Types.VARCHAR);//seems dangerous, what if it is not a varchar and is null?
				else
					prst.setString(i+1,args[i]);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}//end method
}
//613 --97
//710

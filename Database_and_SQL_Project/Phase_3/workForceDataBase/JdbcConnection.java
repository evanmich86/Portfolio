import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;

public class JdbcConnection {

	private Connection connect;
	//private String connectionStatus;
	private final String oraThinProtocal = "jdbc:oracle:thin:@";
	private String host, serviceId, port, user, password;

	public JdbcConnection(String host, String serviceId, String port,String user, String password) {
		this.host = host;
		this.serviceId = serviceId;
		this.user = user;
		this.port = port;
		this.password = password;
		
		try {
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
		}catch(Exception e) {
			System.out.println(e);
		}
	}//end method
	//String.format("jdbc:oracle:thin:@%s:%s:%s", this.host, this.port, this.serviceId), this.user, this.password)
	public Connection connect() {
		String url = oraThinProtocal+this.host+":"+this.port+":"+this.serviceId;
		try {
			//System.out.println("[Table info:] url = "+url+"\n");
			this.connect = DriverManager.getConnection(url,this.user, this.password);
			this.connect.setAutoCommit(false);
			//commit: connection.commit();
			//connectionStatus = "connected";
		}catch(Exception e) {
			System.out.println(e +"line 36: JdbcConnection");
		}
		return this.connect;
	}//end method
	
	public void disconnect() {
		try {
			this.connect.close();
			//this.connectionStatus = "disconnected";
		}catch(Exception e) {
			System.out.println(e + "method disconnect() JdbcConnection");
		}
	}//end method
	
	public void commit() {
		try {
			connect.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
//51

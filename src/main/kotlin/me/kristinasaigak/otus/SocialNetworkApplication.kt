package me.kristinasaigak.otus

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing
import java.sql.Connection
import java.sql.DriverManager

@SpringBootApplication
@EnableR2dbcAuditing
class SocialNetworkApplication

fun main(args: Array<String>) {
	runApplication<SocialNetworkApplication>(*args)

	fun main() {
		// Class.forName( "com.mysql.jdbc.Driver" ); // do this in init
		// // edit the jdbc url
		val conn: Connection = DriverManager.getConnection(
				"jdbc:mysql://master_db:3306/db?autoReconnect=true&useSSL=false&user=user&password=password");
		// Statement st = conn.createStatement();
		// ResultSet rs = st.executeQuery( "select * from table" );

		println("Connected?");
	}

	// main()
}

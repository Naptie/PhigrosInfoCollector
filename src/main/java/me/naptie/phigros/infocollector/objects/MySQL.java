package me.naptie.phigros.infocollector.objects;

import java.sql.*;

public class MySQL {

	private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
	private final String url;
	private Connection connection;

	public MySQL(String address, int port, String username, String password, String database) {
		this.url = String.format("jdbc:mysql://%s:%d/%s?useSSL=true&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai", address, port, database);
		try {
			Class.forName(JDBC_DRIVER);
			System.out.println("Connecting to database...");
			connection = DriverManager.getConnection(url, username, password);
			if (connection.isValid(10)) {
				System.out.println("Successfully connected to database " + database + "!");
			} else {
				System.out.println("Timed out trying to connect to database " + database + ".");
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean insert(String table, String[] keys, String[] values) throws SQLException {
		Statement statement = connection.createStatement();
		String str = "INSERT INTO " + table + " (" + toString(keys, false) + ") VALUES(" + toString(values, true) + ");";
		System.out.println("  " + str);
		int res = statement.executeUpdate(str);
		statement.close();
		return res != 0;
	}

	public boolean update(String table, String[] keys, String[] values, int id) throws SQLException {
		Statement statement = connection.createStatement();
		StringBuilder str = new StringBuilder("UPDATE " + table + " SET ");
		for (int i = 0; i < keys.length; i++) {
			str.append(keys[i]).append("=\"").append(values[i]).append(i == keys.length - 1 ? "\" " : "\", ");
		}
		str.append("WHERE id = ").append(id).append(";");
		System.out.println("  " + str);
		int res = statement.executeUpdate(str.toString());
		statement.close();
		return res != 0;
	}

	public String get(String table, String key, int id) throws SQLException {
		Statement statement = connection.createStatement();
		String str = "SELECT " + key + " FROM " + table + " WHERE id = " + id + ";";
		System.out.println("  " + str);
		ResultSet result = statement.executeQuery(str);
		result.next();
		String content = result.getString(key);
		statement.close();
		return content;
	}

	@SuppressWarnings("SqlResolve")
	public int countRows(String table) throws SQLException {
		Statement statement = connection.createStatement();
		String str = "SELECT COUNT(id) FROM " + table + ";";
		ResultSet result = statement.executeQuery(str);
		result.next();
		int row = result.getInt("COUNT(id)");
		statement.close();
		return row;
	}

	private String toString(String[] src, boolean isValue) {
		StringBuilder res = new StringBuilder();
		for (String s : src) {
			if (res.length() > 0) {
				res.append(", ");
			}
			if (isValue) {
				res.append("\"").append(s.replaceAll("\"", "\\\\\"")).append("\"");
			} else {
				res.append(s);
			}
		}
		return res.toString();
	}

}

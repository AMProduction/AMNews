package com.amproduction.amnews.util;

import com.amproduction.amnews.model.News;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Properties;

/**
 *	@version 1.0 2016-02
 *	@author Andrii Malchyk
 */

public class DBManager {
	
	private static DBManager instance;
	
	private DBManager()
	{
		
	}
	
	 public static DBManager getInstance()
	 {
		 if(instance == null)
		 {
			 instance = new DBManager();
		 }
		 
		 return instance;
	 }

	private boolean connectionStatus = false;

	/**
	 * Зєднання з базою. Параметри підключення зчитуємо з файла
	 * @return зєднання
	 * @throws IOException	зчитування  з файлу
	 * @throws SQLException	помилки роботи з базою
     */
	public Connection ConnectionToDB() throws IOException, SQLException {
		Connection c = null;
		Properties props = new Properties();

		final String sFileName = "database.properties";
		String sDirSeparator = System.getProperty("file.separator");
		File currentDir = new File(".");
		String sFilePath = currentDir.getCanonicalPath() + sDirSeparator + sFileName;

		try (InputStream in = new FileInputStream(sFilePath))
		{
			props.load(in);
		}

		String drivers = props.getProperty("jdbc.drivers");
		if (drivers != null)
			System.setProperty("jdbc.drivers", drivers);
		String url = props.getProperty("jdbc.url");
		String username = props.getProperty("jdbc.username");
		String password = props.getProperty("jdbc.password");

		c = DriverManager.getConnection(url, username, password);
	    	
		setConnectionStatus(true);
	    
	    return c;
	}
	
	public boolean getConnectionStatus()
	{
		return connectionStatus;
	}
	
	public void setConnectionStatus (boolean status)
	{
		connectionStatus = status;
	}

	/**
	 * Зчитуємо з бази дані. Заносимо у колекцію
	 * @return newsData ObservableList<News> колекція обєктів
	 * @throws SQLException	помилки роботи з базою
	 * @throws IOException	зчитування  з файлу (бо юзається метод ConnectionToDB())
     */
	public ObservableList<News> getData() throws SQLException, IOException
	{
		ObservableList<News> newsData = FXCollections.observableArrayList();
		Statement stmt = null;
		
		boolean isConnected = getConnectionStatus();
		if (isConnected)
		{

			Connection c = ConnectionToDB();

			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM \"News\"");
			while (rs.next())
			{
				int id = rs.getInt("id");
				String subject = rs.getString("subject");
				String textPresenter = rs.getString("text_presenter");
				String textNews = rs.getString("text_news");
				LocalDateTime createdDate = rs.getTimestamp("created_date").toLocalDateTime();
				LocalDateTime lastModifiedDate = rs.getTimestamp("last_modified_date").toLocalDateTime();

				newsData.add(new News(id, subject, textPresenter, textNews, createdDate, lastModifiedDate));
			}
				
			rs.close();
		    stmt.close();
		}
		
		return newsData;
	}

	/**
	 * Записуємо у базу новину
	 * @param aNews	обєкт класу новина
	 * @throws SQLException	помилки роботи з базою
	 * @throws IOException	зчитування  з файлу (бо юзається метод ConnectionToDB())
     */
	public void addRecord (News aNews) throws SQLException, IOException
	{
		Statement stmt = null;
		PreparedStatement stat;
		final String insertQuery =
				"INSERT INTO \"News\" (subject, text_presenter, text_news, created_date, last_modified_date)"
				+ "VALUES (?, ?, ?, ?, ?)";
		
		boolean isConnected = getConnectionStatus();
		if (isConnected)
		{
			Connection c = ConnectionToDB();
					
			stmt = c.createStatement();
			c.setAutoCommit(false);
			stat = c.prepareStatement(insertQuery);
			stat.setString(1, aNews.getSubject());
			stat.setString(2, aNews.getTextPresenter());
			stat.setString(3, aNews.getTextNews());
			stat.setTimestamp(4, Timestamp.valueOf(aNews.getCreatedDate()));
			stat.setTimestamp(5, Timestamp.valueOf(aNews.getLastModifiedDate()));
				
			stat.executeUpdate();
		
			stmt.close();
			c.commit();
		}
	}

	/**
	 * Оновлюємо новину у базі
	 * @param aNews	обєкт класу новина
	 * @throws SQLException	помилки роботи з базою
	 * @throws IOException	зчитування  з файлу (бо юзається метод ConnectionToDB())
     */
	public void updateRecord (News aNews) throws SQLException, IOException
	{
		Statement stmt = null;
		PreparedStatement stat;
		final String updateQuery =
				"UPDATE \"News\" set subject = ?, text_presenter = ?, text_news = ?, "
				+ "created_date = ?, last_modified_date = ? "
				+ "where id = ?";
		
		boolean isConnected = getConnectionStatus();
		if (isConnected)
		{
			Connection c = ConnectionToDB();
			
			stmt = c.createStatement();
			c.setAutoCommit(false);
				
			stat = c.prepareStatement(updateQuery);
			stat.setString(1, aNews.getSubject());
			stat.setString(2, aNews.getTextPresenter());
			stat.setString(3, aNews.getTextNews());
			stat.setTimestamp(4, Timestamp.valueOf(aNews.getCreatedDate()));
			stat.setTimestamp(5, Timestamp.valueOf(aNews.getLastModifiedDate()));
			stat.setInt(6, aNews.getId());
				
			stat.executeUpdate();
			stmt.close();
			c.commit();
		}
	}

	/**
	 * Видаляємо новину з бази
	 * @param aNews	обєкт класу новина
	 * @throws SQLException	помилки роботи з базою
	 * @throws IOException	зчитування  з файлу (бо юзається метод ConnectionToDB())
     */
	public void deleteRecord (News aNews) throws SQLException, IOException
	{
		Statement stmt = null;
		PreparedStatement stat;
		final String deleteQuery = "DELETE from \"News\" where id = ?";
		
		boolean isConnected = getConnectionStatus();
		if (isConnected)
		{
			Connection c = ConnectionToDB();
			
			stmt = c.createStatement();
			c.setAutoCommit(false);
				
			stat = c.prepareStatement(deleteQuery);
			stat.setInt(1, aNews.getId());
				
			stat.executeUpdate();
			stmt.close();
			c.commit();
		}
	}

	/**
	 * Шукаємо новину у базі
	 * @param date	дата створення новини
	 * @return	newsData колекцію новин
	 * @throws SQLException
	 * @throws IOException
     */
	public ObservableList<News> filter (LocalDate date) throws SQLException, IOException
	{
		ObservableList<News> newsData = FXCollections.observableArrayList();
		
		PreparedStatement stat;
		final String filterQuery = "SELECT * FROM \"News\" WHERE "
				+ "created_date::date = ?";
		
		boolean isConnected = getConnectionStatus();
		if (isConnected)
		{
			Connection c = ConnectionToDB();
			
			stat = c.prepareStatement(filterQuery);
			stat.setDate(1, Date.valueOf(date));
			
			ResultSet rs = stat.executeQuery();
			
			while (rs.next())
			{
				int id = rs.getInt("id");
				String subject = rs.getString("subject");
				String textPresenter = rs.getString("text_presenter");
				String textNews = rs.getString("text_news");
				LocalDateTime createdDate = rs.getTimestamp("created_date").toLocalDateTime();
				LocalDateTime lastModifiedDate = rs.getTimestamp("last_modified_date").toLocalDateTime();
					
				newsData.add(new News(id, subject, textPresenter, textNews, createdDate, lastModifiedDate));
			}
				
		}
		
		return newsData;
	}
}

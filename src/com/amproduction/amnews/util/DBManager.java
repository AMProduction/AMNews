package com.amproduction.amnews.util;

import com.amproduction.amnews.model.News;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Properties;

/**
 *	Created by snooki on 02.16.
 *	@version 1.1 2016-03
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

//	private boolean connectionStatus = false;
	private String url;
	private String username;
	private String password;

	/**
	 * Зєднання з базою. Параметри підключення зчитуємо з файла
	 * @return зєднання
	 * @throws IOException	зчитування  з файлу
	 * @throws SQLException	помилки роботи з базою
     */
	private Connection ConnectionToDB() throws IOException, SQLException {
		Connection connect;
		Properties props = new Properties();

		final String sFileName = "database.properties";
		String sDirSeparator = System.getProperty("file.separator");
		File currentDir = new File(".");
		String sFilePath = currentDir.getCanonicalPath() + sDirSeparator + sFileName;

		try (InputStream in = new BufferedInputStream(new FileInputStream(sFilePath)))
		{
			props.load(in);
		}

		String driver = props.getProperty("jdbc.drivers");
		if (driver != null)
			System.setProperty("jdbc.drivers", driver);
		url = props.getProperty("jdbc.url");
		username = props.getProperty("jdbc.username");
		password = props.getProperty("jdbc.password");

		connect = DriverManager.getConnection(url, username, password);
        connect.setAutoCommit(false);
	    	
		//setConnectionStatus(true);
	    
	    return connect;
	}
	
/*	public boolean getConnectionStatus()
	{
		return connectionStatus;
	}
	
	public void setConnectionStatus (boolean status)
	{
		connectionStatus = status;
	}
*/
	/**
	 * Зчитуємо з бази дані. Заносимо у колекцію
	 * @return newsData ObservableList<News> колекція обєктів
	 * @throws SQLException	помилки роботи з базою
	 * @throws IOException	зчитування  з файлу (бо юзається метод ConnectionToDB())
     */
	public ObservableList<News> getNews() throws SQLException, IOException
	{
		ObservableList<News> newsData = FXCollections.observableArrayList();
		Statement stmt;
		Connection connect = ConnectionToDB();
		
		try {
			stmt = connect.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM \"News\"");
			while (rs.next()) {
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
		finally {
			if (connect != null)
				connect.close();
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
		Statement stmt;
		PreparedStatement stat;
		final String insertQuery =
				"INSERT INTO \"News\" (subject, text_presenter, text_news, created_date, last_modified_date)"
				+ "VALUES (?, ?, ?, ?, ?)";

		Connection connect = ConnectionToDB();

		try{
			stmt = connect.createStatement();
			stat = connect.prepareStatement(insertQuery);
			stat.setString(1, aNews.getSubject());
			stat.setString(2, aNews.getTextPresenter());
			stat.setString(3, aNews.getTextNews());
			stat.setTimestamp(4, Timestamp.valueOf(aNews.getCreatedDate()));
			stat.setTimestamp(5, Timestamp.valueOf(aNews.getLastModifiedDate()));
				
			stat.executeUpdate();
		
			stmt.close();
			connect.commit();
		}
        catch (SQLException e)
        {
            connect.rollback();
        }
		finally {
			if (connect != null)
				connect.close();
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
		Statement stmt;
		PreparedStatement stat;
		final String updateQuery =
				"UPDATE \"News\" set subject = ?, text_presenter = ?, text_news = ?, "
				+ "created_date = ?, last_modified_date = ? "
				+ "where id = ?";

        Connection connect = ConnectionToDB();

        try
        {
			stmt = connect.createStatement();
				
			stat = connect.prepareStatement(updateQuery);
			stat.setString(1, aNews.getSubject());
			stat.setString(2, aNews.getTextPresenter());
			stat.setString(3, aNews.getTextNews());
			stat.setTimestamp(4, Timestamp.valueOf(aNews.getCreatedDate()));
			stat.setTimestamp(5, Timestamp.valueOf(aNews.getLastModifiedDate()));
			stat.setInt(6, aNews.getId());
				
			stat.executeUpdate();
			stmt.close();
			connect.commit();
		}
        catch (SQLException e)
        {
            connect.rollback();
        }
        finally {
            if (connect != null)
                connect.close();
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
		Statement stmt;
		PreparedStatement stat;
		final String deleteQuery = "DELETE from \"News\" where id = ?";

        Connection connect = ConnectionToDB();

        try
        {
			stmt = connect.createStatement();

			stat = connect.prepareStatement(deleteQuery);
			stat.setInt(1, aNews.getId());
				
			stat.executeUpdate();
			stmt.close();
			connect.commit();
		}
        catch (SQLException e)
        {
            connect.rollback();
        }
        finally {
            if (connect != null)
                connect.close();
        }
	}

	/**
	 * Шукаємо новину у базі
	 * @param date	дата створення новини
	 * @return	newsData колекцію новин
	 * @throws SQLException
	 * @throws IOException
     */
	public ObservableList<News> filterNews (LocalDate date) throws SQLException, IOException
	{
		ObservableList<News> newsData = FXCollections.observableArrayList();
		
		PreparedStatement stat;
		final String filterQuery = "SELECT * FROM \"News\" WHERE "
				+ "created_date::date = ?";

        Connection connect = ConnectionToDB();

        try
        {
			stat = connect.prepareStatement(filterQuery);
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

            rs.close();
            stat.close();
		}
        finally {
            if (connect != null)
                connect.close();
        }
		
		return newsData;
	}
}

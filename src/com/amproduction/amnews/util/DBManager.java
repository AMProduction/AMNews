package com.amproduction.amnews.util;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.Format;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import com.amproduction.amnews.model.News;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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
	
	public Connection ConnectionToDB()
	{
		Connection c = null;
	    try
	    {
	    	Class.forName("org.postgresql.Driver");
	    	c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/AMNews",
	            "dbuser1", "1988");
	    	
	    	setConnectionStatus(true);	    	
	    }
	    catch (Exception e)
	    {
	    	setConnectionStatus(false);
	    	e.printStackTrace();    
	    }
	    
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
	
	public ObservableList<News> getData() throws SQLException
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
		
	public void addRecord (News aNews) throws SQLException
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
	
	public void updateRecord (News aNews) throws SQLException
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

	public void deleteRecord (News aNews) throws SQLException
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
	
	public ObservableList<News> filter (LocalDate date) throws SQLException
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

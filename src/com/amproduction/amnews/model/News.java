/**
 *	@version 1.0 2016-02
 *	@author Andrii Malchyk
 */

package com.amproduction.amnews.model;

import java.time.LocalDateTime;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class News {
	private final IntegerProperty id;
	private final StringProperty subject;
	private final StringProperty textPresenter;
	private final StringProperty textNews;
	private final ObjectProperty<LocalDateTime> createdDate;
	private final ObjectProperty<LocalDateTime> lastModifiedDate;

	/**
	 * Конструктор для створення нової новини
	 * @param aId	номер
	 * @param aSubject	тема новини
	 * @param aTextPresenter	текст диктора (підводка)
	 * @param aTextNews	текст новини
	 * @param aCreatedDate	дата створення (незмінна)
     * @param aLastModifiedDate	дата останньої модифікації
     */
	public News(int aId, String aSubject, String aTextPresenter, String aTextNews,
			LocalDateTime aCreatedDate, LocalDateTime aLastModifiedDate)
	{
		this.id = new SimpleIntegerProperty(aId);
		this.subject = new SimpleStringProperty(aSubject);
		this.textPresenter = new SimpleStringProperty(aTextPresenter);
		this.textNews = new SimpleStringProperty(aTextNews);
		this.createdDate = new SimpleObjectProperty<>(aCreatedDate);
		this.lastModifiedDate = new SimpleObjectProperty<>(aLastModifiedDate);
	}

	/**
	 * Конструктор для редагування існуючої новини
	 * @param aSubject	тема новини
	 * @param aTextPresenter текст диктора (підводка)
	 * @param aTextNews текст новини
	 * @param aCreatedDate дата створення (незмінна)
	 * @param aLastModifiedDate	дата останньої модифікації
     */
	public News(String aSubject, String aTextPresenter, String aTextNews,
			LocalDateTime aCreatedDate, LocalDateTime aLastModifiedDate)
	{
		this.subject = new SimpleStringProperty(aSubject);
		this.textPresenter = new SimpleStringProperty(aTextPresenter);
		this.textNews = new SimpleStringProperty(aTextNews);
		this.createdDate = new SimpleObjectProperty<>(aCreatedDate);
		this.lastModifiedDate = new SimpleObjectProperty<>(aLastModifiedDate);
		
		this.id = new SimpleIntegerProperty(0);
	}
		
	public int getId()
	{
		return id.get();
	}
	
	public void setId(int aId)
	{
		this.id.set(aId);
	}
	
	public IntegerProperty idProperty()
	{
		return id;
	}
	
	public String getSubject()
	{
		return subject.get();
	}
	
	public void setSubject(String aSubject)
	{
		this.subject.set(aSubject);
	}
	
	public StringProperty subjectProperty()
	{
		return subject;
	}
	
	public String getTextPresenter()
	{
		return textPresenter.get();
	}
	
	public void setTextPresenter(String aTextPresenter)
	{
		this.textPresenter.set(aTextPresenter);
	}
	
	public StringProperty textPresenterProperty()
	{
		return textPresenter;
	}
	
	public String getTextNews()
	{
		return textNews.get();
	}
	
	public void setTextNews(String aTextNews)
	{
		this.textNews.set(aTextNews);
	}
	
	public StringProperty textNewsProperty()
	{
		return textNews;
	}
	
	public LocalDateTime getCreatedDate()
	{
		return createdDate.getValue();
	}
	
	public void setCreatedDate (LocalDateTime aCreatedDate)
	{
		this.createdDate.set(aCreatedDate);
	}
	
	public ObjectProperty<LocalDateTime> createdDateProperty()
	{
		return createdDate;
	}
	
	public LocalDateTime getLastModifiedDate()
	{
		return lastModifiedDate.getValue();
	}
	
	public void setLastModifiedDate (LocalDateTime aLastModifiedDate)
	{
		this.lastModifiedDate.set(aLastModifiedDate);
	}
	
	public ObjectProperty<LocalDateTime> lastModifiedDateProperty()
	{
		return lastModifiedDate;
	}
}

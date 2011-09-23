package org.molgenis.matrix.component.legacy;


public class SomeRowType
{
	Integer id;
	String firstName;
	String lastName;
	String city;
	Integer yearOfBirth;
	
	public SomeRowType(Integer id, String firstName, String lastName, String city, Integer yearOfBirth)
	{
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.city = city;
		this.yearOfBirth = yearOfBirth;
	}

	public Integer getId()
	{
		return id;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public String getCity()
	{
		return city;
	}

	public Integer getYearOfBirth()
	{
		return yearOfBirth;
	}

	
}

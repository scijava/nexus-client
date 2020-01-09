package org.scijava.nexus.client.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Object representation of a Nexus component
 * 
 * @author turek
 *
 */

public class Component {

	@JsonProperty( "id" )
	private String id;
	@JsonProperty( "repository" )
	private String repository;
	@JsonProperty( "format" )
	private String format;
	@JsonProperty( "group" )
	private String group;
	@JsonProperty( "name" )
	private String name;
	@JsonProperty( "version" )
	private String version;
	private List< Asset > assets;

	public String getId() {
		return id;
	}

	public void setId( String id ) {
		this.id = id;
	}

	public String getRepository() {
		return repository;
	}

	public void setRepository( String repository ) {
		this.repository = repository;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat( String format ) {
		this.format = format;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup( String group ) {
		this.group = group;
	}

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion( String version ) {
		this.version = version;
	}

	public List< Asset > getAssets() {
		return assets;
	}

	public void setAssets( List< Asset > assets ) {
		this.assets = assets;
	}
}

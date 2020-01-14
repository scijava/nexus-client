package org.scijava.nexus.v3.client.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Object representation of a Nexus repository
 * 
 * @author turek
 *
 */

public class Repository {

	@JsonProperty( "name" )
	private String name;
	@JsonProperty( "format" )
	private String format;
	@JsonProperty( "type" )
	private String type;
	@JsonProperty( "url" )
	private String url;

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat( String format ) {
		this.format = format;
	}

	public String getType() {
		return type;
	}

	public void setType( String type ) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl( String url ) {
		this.url = url;
	}

}

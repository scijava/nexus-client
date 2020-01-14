package org.scijava.nexus.v1.client.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author turek
 *
 */

public class Checksum {

	@JsonProperty( "sha1" )
	private String sha1;
	@JsonProperty( "sha512" )
	private String sha512;
	@JsonProperty( "sha256" )
	private String sha256;
	@JsonProperty( "md5" )
	private String md5;

	public String getSha1() {
		return sha1;
	}

	public void setSha1( String sha1 ) {
		this.sha1 = sha1;
	}

	
	public String getSha512() {
		return sha512;
	}

	
	public void setSha512( String sha512 ) {
		this.sha512 = sha512;
	}

	
	public String getSha256() {
		return sha256;
	}

	
	public void setSha256( String sha256 ) {
		this.sha256 = sha256;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5( String md5 ) {
		this.md5 = md5;
	}
}

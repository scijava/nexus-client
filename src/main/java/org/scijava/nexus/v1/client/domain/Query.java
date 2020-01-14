package org.scijava.nexus.v1.client.domain;

import javax.validation.constraints.Null;

/**
 * Object representation of a list of search parameters
 * 
 * @author turek
 *
 */

public class Query {

	public enum Sort {
		GROUP, NAME, VERSION, REPOSITORY
	};

	public enum Order {
		ASC, DESC
	};
	
	public enum Format {
		RAW, MAVEN2
	};

	@Null
	private String keyword;
	@Null
	private String repository;
	@Null
	private Format format;
	@Null
	private String componentGroup;
	@Null
	private String componentName;
	@Null
	private String componentVersion;
	@Null
	private String mavenGroupId;
	@Null
	private String mavenArtifactId;
	@Null
	private String mavenBaseVersion;
	@Null
	private String mavenExtension;
	@Null
	private Sort sortBy;
	@Null
	private Order orderBy;

	public String getKeyword() {
		return keyword;
	}

	public String getRepository() {
		return repository;
	}

	public String getFormat() {
		return (format == null) ? null : format.toString().toLowerCase();
	}

	public String getComponentGroup() {
		return componentGroup;
	}

	public String getComponentName() {
		return componentName;
	}

	public String getComponentVersion() {
		return componentVersion;
	}

	public String getMavenGroupId() {
		return mavenGroupId;
	}

	public String getMavenArtifactId() {
		return mavenArtifactId;
	}
	
	public String getMavenBaseVersion() {
		return mavenBaseVersion;
	}

	public String getMavenExtension() {
		return mavenExtension;
	}

	public String getSortBy() {
		
		return (sortBy == null) ? null : sortBy.toString().toLowerCase();
	}

	public String getOrderBy() {
		return (orderBy == null) ? null : orderBy.toString().toLowerCase();
	}

	
	/**
	 * 
	 * @param keyword - for keyword query
	 */
	public void setKeyword( String keyword ) {
		this.keyword = keyword;
	}

	
	/**
	 * 
	 * @param repository - repository name
	 */
	public void setRepository( String repository ) {
		this.repository = repository;
	}

	
	/**
	 *  
	 * @param format - repository Format 
	 */
	public void setFormat( Format format ) {
		this.format = format;
	}

	
	/**
	 * 
	 * @param group - component group
	 */
	public void setComponentGroup( String group ) {
		this.componentGroup = group;
	}

	
	/**
	 * 
	 * @param name - component Name
	 */
	public void setComponentName( String name ) {
		this.componentName = name;
	}

	/**
	 * 
	 * @param version - component version
	 */
	public void setComponentVersion( String version ) {
		this.componentVersion = version;
	}

	/**
	 * 
	 * @param mavenGroupId - Maven group ID
	 */
	public void setMavenGroupId( String mavenGroupId ) {
		this.mavenGroupId = mavenGroupId;
	}

	
	/**
	 * 
	 * @param mavenArtifactId - Maven artifact ID
	 */
	public void setMavenArtifactId( String mavenArtifactId ) {
		this.mavenArtifactId = mavenArtifactId;
	}
	
	/**
	 * 
	 * @param mavenBaseVersion - Maven version
	 */
	public void setMavenBaseVersion( String mavenBaseVersion ) {
		this.mavenBaseVersion = mavenBaseVersion;
	}

	/**
	 * 
	 * @param mavenExtension - Maven file extension (e.g jar, pom)
	 */
	public void setMavenExtension( String mavenExtension ) {
		this.mavenExtension = mavenExtension;
	}

	/**
	 * 
	 * @param sortBy - attribute to order by
	 */
	public void setSortBy( Sort sortBy ) {
		this.sortBy = sortBy;
	}

	
	/**
	 * 
	 * @param orderBy - sort order
	 */
	public void setOrderBy( Order orderBy ) {
		this.orderBy = orderBy;
	}

}

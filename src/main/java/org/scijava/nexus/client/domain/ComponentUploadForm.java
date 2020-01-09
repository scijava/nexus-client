package org.scijava.nexus.client.domain;

import java.io.InputStream;

import javax.ws.rs.FormParam;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

/**
 * Data required for component upload to "raw" data repositories
 * 
 * @author turek
 *
 */

public class ComponentUploadForm {

	@FormParam( "raw.directory" )
	@PartType( "application/json" )
	private String directory;
	
	@FormParam( "raw.asset1" )
	@PartType( "application/octet-stream" )
	private InputStream fileData1;
	
	@FormParam( "raw.asset1.filename" )
	@PartType( "application/json" )
	private String fileName1;
	
	@FormParam( "raw.asset2" )
	@PartType( "application/octet-stream" )
	private InputStream fileData2;
	
	@FormParam( "raw.asset2.filename")
	@PartType( "application/json" )
	private String fileName2;
	
	@FormParam( "raw.asset3" )
	@PartType( "application/octet-stream" )
	private InputStream fileData3;
	
	@FormParam( "raw.asset3.filename" )
	@PartType( "application/json" )
	private String fileName3;

	/**
	 * 
	 * @param directory
	 *            - the name of the upload root directory
	 */
	public void setDirectory( String directory ) {
		this.directory = directory;
	}

	public String getDirectory() {
		return directory;
	}

	/**
	 * @param fileName1
	 *            - the name of the first file for upload to the repository. Can include a
	 *            path
	 *            relative to the upload root directory
	 */
	public void setFileName1( String fileName1 ) {
		this.fileName1 = fileName1;
	}

	public String getFileName1() {
		return fileName1;
	}

	public void setFileData1( InputStream fileData1 ) {
		this.fileData1 = fileData1;
	}

	public InputStream getFileData1() {
		return fileData1;
	}

	/**
	 * @param fileName2
	 *            - the name of the second file for upload to the repository. Can include a
	 *            path
	 *            relative to the upload root directory
	 */
	public void setFileName2( String fileName2 ) {
		this.fileName2 = fileName2;
	}

	public String getFileName2() {
		return fileName2;
	}

	public void setFileData2( InputStream fileData2 ) {
		this.fileData2 = fileData2;
	}

	public InputStream getFileData2() {
		return fileData2;
	}

	public void setFileName3( String fileName3 ) {
		this.fileName3 = fileName3;
	}

	public String getFileName3() {
		return fileName3;
	}

	public void setFileData3( InputStream fileData3 ) {
		this.fileData3 = fileData3;
	}

	public InputStream getFileData3() {
		return fileData3;
	}

}

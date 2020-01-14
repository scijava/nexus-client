package org.scijava.nexus.v1.client;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.scijava.nexus.v1.client.domain.ComponentUploadForm;

/**
 * <p>
 * Client for the version 1 of Nexus Sonatype's ReST service.
 * Uses the <a href=
 * "https://docs.jboss.org/resteasy/docs/4.0.0.Final/userguide/html/RESTEasy_Client_Framework.html">RESTEasy
 * Proxy Framework</a>
 * </p>
 */
public interface NexusReSTClientProxy {

	@GET
	@Path( "/service/rest/v1/repositories" )
	@Produces( MediaType.APPLICATION_JSON )
	public String listRepositories();

	@GET
	@Path( "/service/rest/v1/search/assets" )
	@Produces( MediaType.APPLICATION_JSON )
	public String searchAssets(
			@QueryParam( "sort" ) String sort,
			@QueryParam( "direction" ) String order,
			@QueryParam( "q" ) String keyword,
			@QueryParam( "repository" ) String repository,
			@QueryParam( "format" ) String format,
			@QueryParam( "group" ) String group,
			@QueryParam( "name" ) String name,
			@QueryParam( "version" ) String version,
			@QueryParam( "maven.groupId" ) String mavenGroupId,
			@QueryParam( "maven.artifactId" ) String mavenArtifactId,
			@QueryParam( "maven.baseVersion" ) String mavenBaseVersion,
			@QueryParam( "maven.extension" ) String mavenExtension,
			@QueryParam( "continuationToken" ) String continuationToken );

	@GET
	@Path( "/service/rest/v1/search/assets/download" )
	@Produces( MediaType.APPLICATION_JSON )
	public String searchAssetsAndDownload(
			@QueryParam( "sort" ) String sort,
			@QueryParam( "direction" ) String order,
			@QueryParam( "q" ) String keyword,
			@QueryParam( "repository" ) String repository,
			@QueryParam( "format" ) String format,
			@QueryParam( "group" ) String group,
			@QueryParam( "name" ) String name,
			@QueryParam( "version" ) String version,
			@QueryParam( "maven.groupId" ) String mavenGroupId,
			@QueryParam( "maven.artifactId" ) String mavenArtifactId,
			@QueryParam( "maven.baseVersion" ) String mavenBaseVersion,
			@QueryParam( "maven.extension" ) String mavenExtension );

	@GET
	@Path( "/service/rest/v1/assets" )
	@Produces( MediaType.APPLICATION_JSON )
	public String listAssets( @QueryParam( "repository" ) String repository, @QueryParam( "continuationToken" ) String continuationToken );

	@GET
	@Path( "/service/rest/v1/assets/{id}" )
	@Produces( MediaType.APPLICATION_JSON )
	public String getAsset( @PathParam( "id" ) String id );

	@DELETE
	@Path( "/service/rest/v1/assets/{id}" )
	@Consumes( MediaType.TEXT_PLAIN )
	public void deleteAsset( @PathParam( "id" ) String id );

	@GET
	@Path( "/service/rest/v1/search" )
	@Produces( MediaType.APPLICATION_JSON )
	public String searchComponents(
			@QueryParam( "sort" ) String sort,
			@QueryParam( "direction" ) String order,
			@QueryParam( "q" ) String keyword,
			@QueryParam( "repository" ) String repository,
			@QueryParam( "format" ) String format,
			@QueryParam( "group" ) String group,
			@QueryParam( "name" ) String name,
			@QueryParam( "version" ) String version,
			@QueryParam( "maven.groupId" ) String mavenGroupId,
			@QueryParam( "maven.artifactId" ) String mavenArtifactId,
			@QueryParam( "maven.baseVersion" ) String mavenBaseVersion,
			@QueryParam( "maven.extension" ) String mavenExtension,
			@QueryParam( "continuationToken" ) String continuationToken );

	@GET
	@Path( "/service/rest/v1/components" )
	@Produces( MediaType.APPLICATION_JSON )
	public String listComponents( @QueryParam( "repository" ) String repository, @QueryParam( "continuationToken" ) String continuationToken );

	@GET
	@Path( "/service/rest/v1/components/{id}" )
	@Produces( MediaType.APPLICATION_JSON )
	public String getComponent( @PathParam( "id" ) String id );

	@DELETE
	@Path( "/service/rest/v1/components/{id}" )
	@Consumes( MediaType.TEXT_PLAIN )
	public void deleteComponent( @PathParam( "id" ) String id );

	@POST
	@Path( "/service/rest/v1/components" )
	@Consumes( MediaType.MULTIPART_FORM_DATA )
	public void uploadComponent( @QueryParam( "repository" ) String repository, @MultipartForm ComponentUploadForm componentForm );

}

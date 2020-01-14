package org.scijava.nexus.v3.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import javax.ws.rs.RedirectionException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.internal.BasicAuthentication;
import org.scijava.nexus.v3.client.domain.Asset;
import org.scijava.nexus.v3.client.domain.Component;
import org.scijava.nexus.v3.client.domain.ComponentUploadForm;
import org.scijava.nexus.v3.client.domain.Query;
import org.scijava.nexus.v3.client.domain.Repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author turekg
 */
public class NexusReSTClient {

	private static final String TMPDIR = System.getProperty( "java.io.tmpdir" );
	private static final String ITEMS = "items";
	private static final String CTOKEN = "continuationToken";

	private NexusReSTClient() {}

	/**
	 * List all the repositories (local and mirrored) hosted on server
	 * 
	 * Note: the Repository object the service response is mapped to only contains attributes deemed useful at this stage, 
	 * hence the setting of DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES to 'false'.
	 * 
	 * @param baseURL
	 *            - the base URL of the Nexus server
	 * @returns list of {@link Repository} managed by the server
	 * 
	 * @throws NexusReSTClientException
	 */
	public static List< Repository > listRepositories( String baseURL ) throws NexusReSTClientException {
		Client client = null;
		try {
			client = ClientBuilder.newClient();
			ResteasyWebTarget webTarget = ( ResteasyWebTarget ) client.target( baseURL );
			NexusReSTClientProxy restClient = webTarget.proxy( NexusReSTClientProxy.class );
			String response = restClient.listRepositories();
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure( DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true );
			mapper.configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			return Arrays.asList( mapper.readValue( response, Repository[].class ) );
		} catch ( RuntimeException | IOException e ) {
			throw new NexusReSTClientException( e );
		}
		finally {
			if ( client != null ) client.close();
		}
	}

	/**
	 * Search for one or more assets using one or more search parameters
	 * 
	 * @param baseURL
	 *            - the base URL of the Nexus server
	 * @param q
	 *            - {@link Query} object in which a number of supported search
	 *            parameters can be set
	 * @returns a list of {@link Asset} or an empty list if none were found.
	 * 
	 * @throws NexusReSTClientException
	 */
	@SuppressWarnings( "unchecked" )
	public static List< Asset > searchAssets( String baseURL, Query q ) throws NexusReSTClientException {
		List< Asset > result = new ArrayList<>();
		Client client = null;
		try {
			client = ClientBuilder.newClient();
			ResteasyWebTarget webTarget = ( ResteasyWebTarget ) client.target( baseURL );
			NexusReSTClientProxy restClient = webTarget.proxy( NexusReSTClientProxy.class );

			boolean begin = true;
			String continuationToken = "";
			String response;
			
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure( DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true );
			while ( continuationToken != null ) {
				if ( begin ) {
					response = restClient.searchAssets(
							q.getSortBy(),
							q.getOrderBy(),
							q.getKeyword(),
							q.getRepository(),
							q.getFormat(),
							q.getComponentGroup(),
							q.getComponentName(),
							q.getComponentVersion(),
							q.getMavenGroupId(),
							q.getMavenArtifactId(),
							q.getMavenBaseVersion(),
							q.getMavenExtension(),
							null );
					begin = false;
				} else {
					response = restClient.searchAssets(
							q.getSortBy(),
							q.getOrderBy(),
							q.getKeyword(),
							q.getRepository(),
							q.getFormat(),
							q.getComponentGroup(),
							q.getComponentName(),
							q.getComponentVersion(),
							q.getMavenGroupId(),
							q.getMavenArtifactId(),
							q.getMavenBaseVersion(),
							q.getMavenExtension(),
							continuationToken );
				}
				Map< String, Object > map = mapper.readValue( response, Map.class );
				result.addAll( mapper.convertValue( map.get( ITEMS ), new TypeReference< List< Asset > >() {} ) );
				continuationToken = ( String ) map.get( CTOKEN );
			}
			return result;

		} catch ( RuntimeException | IOException e ) {
			throw new NexusReSTClientException( e );
		}
		finally {
			if ( client != null ) client.close();
		}
	}

	/**
	 * Search for an assets and download it using one or more search parameters.
	 * 
	 * Check for the following error code returns:
	 * 400 : Search returned multiple assets. Refine search criteria
	 * to
	 * find a single asset or use the sort query parameter to
	 * retrieve the first
	 * result.
	 * 404 : Asset search returned no results
	 * 
	 * @param baseURL
	 *            - the base URL of the Nexus server
	 * @param q
	 *            - {@link Query} object in which a number of supported search
	 *            parameters can be set
	 * @param fileName
	 *            - what to name the download
	 * @param downloadDir
	 *            - where to download the asset to
	 * @return File handle to down loaded asset
	 * 
	 * @throws NexusReSTClientException
	 */
	public static File searchAssetsAndDownload( String baseURL, Query q, String fileName, String downloadDir ) throws NexusReSTClientException {
		Client client = null;
		try {
			client = ClientBuilder.newClient();
			ResteasyWebTarget webTarget = ( ResteasyWebTarget ) client.target( baseURL );
			NexusReSTClientProxy restClient = webTarget.proxy( NexusReSTClientProxy.class );
			restClient.searchAssetsAndDownload(
					q.getSortBy(),
					q.getOrderBy(),
					q.getKeyword(),
					q.getRepository(),
					q.getFormat(),
					q.getComponentGroup(),
					q.getComponentName(),
					q.getComponentVersion(),
					q.getMavenGroupId(),
					q.getMavenArtifactId(),
					q.getMavenBaseVersion(),
					q.getMavenExtension());

		} catch ( RedirectionException e ) {
			URL url;
			try {
				url = e.getLocation().toURL();
				String tmpPath = TMPDIR + File.separator + fileName;
				String finalPath =
						downloadDir + File.separator + fileName;
				return saveToFile( url, tmpPath, finalPath );
			} catch ( IOException e1 ) {
				throw new NexusReSTClientException( e1 );
			}
		} catch ( RuntimeException e ) {
			throw new NexusReSTClientException( e );
		}
		finally {
			if ( client != null ) client.close();
		}
		return null;
	}

	/**
	 * Lists all the assets stored in the given repository
	 * 
	 * @param baseURL
	 *            - the base URL of the Nexus server
	 * @param repository
	 *            - the repository of interest
	 * 
	 * @return list of assets stored in the given repository
	 * 
	 * @throws NexusReSTClientException
	 */
	public static List< Asset > listAssets( String baseURL, String repository ) throws NexusReSTClientException {
		Client client = null;
		try {
			client = ClientBuilder.newClient();
			ResteasyWebTarget webTarget = ( ResteasyWebTarget ) client.target( baseURL );
			NexusReSTClientProxy restClient = webTarget.proxy( NexusReSTClientProxy.class );
			return lister( repository, restClient::listAssets, new TypeReference< List< Asset > >() {} );
		} catch ( RuntimeException e ) {
			throw new NexusReSTClientException( e );
		}
		finally {
			if ( client != null ) client.close();
		}
	}

	/**
	 * Download the asset with the given ID
	 * 
	 * @param baseURL
	 *            - the base URL of the Nexus server
	 * @param assetId
	 * @param downloadDir
	 *            - target download directory
	 * 
	 * @return a handle to the down loaded asset
	 * 
	 * @throws NexusReSTClientException
	 */
	public static File getAsset(
			String baseURL,
			String assetId,
			String downloadDir ) throws NexusReSTClientException {
		Client client = null;
		try {
			client = ClientBuilder.newClient();
			ResteasyWebTarget webTarget = ( ResteasyWebTarget ) client.target( baseURL );
			NexusReSTClientProxy restClient = webTarget.proxy( NexusReSTClientProxy.class );
			String response = restClient.getAsset( assetId );
			ObjectMapper mapper = new ObjectMapper();
			Asset asset = mapper.readValue( response, Asset.class );
			return saveAsset( asset.getDownloadUrl(), downloadDir );
		} catch ( RuntimeException | IOException e ) {
			throw new NexusReSTClientException( e );
		}
		finally {
			if ( client != null ) client.close();
		}
	}

	/**
	 * Delete the given asset. Only users with delete privilege can use this
	 * command.
	 * If an exception occurs check return code.
	 * 
	 * @param baseURL
	 *            - the base URL of the Nexus server
	 * @param username
	 * @param password
	 * @param assetId
	 * 
	 * @throws NexusReSTClientException
	 */
	public static void deleteAsset( String baseURL, String username, String password, String assetId ) throws NexusReSTClientException {
		Client client = null;
		try {
			client = ClientBuilder.newClient();
			ResteasyWebTarget webTarget = ( ResteasyWebTarget ) client.target( baseURL );
			webTarget.register( new BasicAuthentication( username, password ) );
			NexusReSTClientProxy restClient = webTarget.proxy( NexusReSTClientProxy.class );
			restClient.deleteAsset( assetId );
		} catch ( RuntimeException e ) {
			throw new NexusReSTClientException( e );
		}
		finally {
			if ( client != null ) client.close();
		}
	}

	/**
	 * Search for one or more components using one or more search parameters
	 * 
	 * @param baseURL
	 *            - the base URL of the Nexus server
	 * @param q
	 *            - {@link Query} object in which a number of supported search
	 *            parameters can be set
	 * 
	 * @returns a list of {@link Component} or an empty list if none were found.
	 * 
	 * @throws NexusReSTClientException
	 */
	@SuppressWarnings( "unchecked" )
	public static List< Component > searchComponents( String baseURL, Query q ) throws NexusReSTClientException {
		List< Component > result = new ArrayList<>();
		Client client = null;
		try {
			client = ClientBuilder.newClient();
			ResteasyWebTarget webTarget = ( ResteasyWebTarget ) client.target( baseURL );
			NexusReSTClientProxy restClient = webTarget.proxy( NexusReSTClientProxy.class );

			boolean begin = true;
			String continuationToken = "";
			String response;
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure( DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true );
			while ( continuationToken != null ) {
				if ( begin ) {
					response = restClient.searchComponents(
							q.getSortBy(),
							q.getOrderBy(),
							q.getKeyword(),
							q.getRepository(),
							q.getFormat(),
							q.getComponentGroup(),
							q.getComponentName(),
							q.getComponentVersion(),
							q.getMavenGroupId(),
							q.getMavenArtifactId(),
							q.getMavenBaseVersion(),
							q.getMavenExtension(),
							null );
					begin = false;
				} else {
					response = restClient.searchComponents(
							q.getSortBy(),
							q.getOrderBy(),
							q.getKeyword(),
							q.getRepository(),
							q.getFormat(),
							q.getComponentGroup(),
							q.getComponentName(),
							q.getComponentVersion(),
							q.getMavenGroupId(),
							q.getMavenArtifactId(),
							q.getMavenBaseVersion(),
							q.getMavenExtension(),
							continuationToken );
				}
				Map< String, Object > map = mapper.readValue( response, Map.class );
				result.addAll( mapper.convertValue( map.get( ITEMS ), new TypeReference< List< Component > >() {} ) );
				continuationToken = ( String ) map.get( CTOKEN );
			}
			return result;
		} catch ( RuntimeException | IOException e ) {
			throw new NexusReSTClientException( e );
		}
		finally {
			if ( client != null ) client.close();
		}
	}

	/**
	 * Lists all the components stored in the listed repository
	 * 
	 * @param baseURL
	 *            - the base URL of the Nexus server
	 * @param repository
	 *            - the repository of interest
	 * 
	 * @return list of components stored in the given repository
	 * 
	 * @throws NexusReSTClientException
	 */
	public static List< Component >
			listComponents( String baseURL, String repository ) throws NexusReSTClientException {
		Client client = null;
		try {
			client = ClientBuilder.newClient();
			ResteasyWebTarget webTarget = ( ResteasyWebTarget ) client.target( baseURL );
			NexusReSTClientProxy restClient = webTarget.proxy( NexusReSTClientProxy.class );
			return lister( repository, restClient::listComponents, new TypeReference< List< Component > >() {} );
		} catch ( RuntimeException e ) {
			throw new NexusReSTClientException( e );
		}
		finally {
			if ( client != null ) client.close();
		}
	}
	
	/**
	 * Delete the given component. Only users with delete privilege can use this
	 * command.
	 * If an exception occurs check return code.
	 * 
	 * @param baseURL
	 *            - the base URL of the Nexus server
	 * @param username
	 * @param password
	 * @param componentId
	 * 
	 * @throws NexusReSTClientException
	 */
	public static void deleteComponent( String baseURL, String username, String password, String componentId ) throws NexusReSTClientException {
		Client client = null;
		try {
			client = ClientBuilder.newClient();
			ResteasyWebTarget webTarget = ( ResteasyWebTarget ) client.target( baseURL );
			webTarget.register( new BasicAuthentication( username, password ) );
			NexusReSTClientProxy restClient = webTarget.proxy( NexusReSTClientProxy.class );
			restClient.deleteComponent( componentId );
		} catch ( RuntimeException e ) {
			throw new NexusReSTClientException( e );
		}
		finally {
			if ( client != null ) client.close();
		}
	}


	/**
	 * Download the component with the given ID. This will download all the
	 * assets
	 * that are contained within this component
	 * 
	 * @param baseURL
	 *            - the base URL of the Nexus server
	 * @param assetId
	 * @param downloadDir
	 *            - target download directory
	 * 
	 * @return a list of handles to all the component's down loaded assets
	 * 
	 * @throws NexusReSTClientException
	 */
	public static List< File > getComponent( String baseURL, String id, String downloadDir ) throws NexusReSTClientException {
		Client client = null;
		try {
			client = ClientBuilder.newClient();
			ResteasyWebTarget webTarget = ( ResteasyWebTarget ) client.target( baseURL );
			NexusReSTClientProxy restClient = webTarget.proxy( NexusReSTClientProxy.class );
			String response = restClient.getComponent( id );
			ObjectMapper mapper = new ObjectMapper();
			Component component = mapper.readValue( response, Component.class );
			return saveComponent( component, downloadDir );

		} catch ( RuntimeException | IOException e ) {
			throw new NexusReSTClientException( e );
		}
		finally {
			if ( client != null ) client.close();
		}
	}
	
	/**
	 * Upload a component: up to 3 files that belong to the same name space. For more, repeat the call.
	 * @param baseURL - the base URL of the Nexus server
	 * @param username - 
	 * @param password
	 * @param repository 
	 * @param uploadForm {@link ComponentUploadForm}
	 * @throws NexusReSTClientException
	 */
	public static void uploadComponent(
			String baseURL,
			String username,
			String password,
			String repository,
			ComponentUploadForm uploadForm ) throws NexusReSTClientException {
		Client client = null;
		try {
			client = ClientBuilder.newClient();
			ResteasyWebTarget webTarget = ( ResteasyWebTarget ) client.target( baseURL );
			webTarget.register( new BasicAuthentication( username, password ) );
			NexusReSTClientProxy restClient = webTarget.proxy( NexusReSTClientProxy.class );
			restClient.uploadComponent( repository, uploadForm );
		} catch ( RuntimeException e ) {
			throw new NexusReSTClientException( e );
		}
		finally {
			if ( client != null ) client.close();
		}

	}

	@SuppressWarnings( "unchecked" )
	private static < T > List< T > lister( String repository, BiFunction< String, String, String > listFunction, TypeReference< List< T > > typeRef ) throws NexusReSTClientException {
		try {
			boolean begin = true;
			String continuationToken = "";
			List< T > result = new ArrayList<>();
			String response;
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure( DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true );
			while ( continuationToken != null ) {
				if ( begin ) {
					response = listFunction.apply( repository, null );
					begin = false;
				} else {
					response = listFunction.apply( repository, continuationToken );
				}
				Map< String, Object > map = mapper.readValue( response, Map.class );
				result.addAll( mapper.convertValue( map.get( ITEMS ), typeRef ) );
				continuationToken = ( String ) map.get( CTOKEN );
			}
			return result;
		} catch ( RuntimeException | IOException e ) {
			throw new NexusReSTClientException( e );
		}
	}

	private static File saveAsset( String url, String downloadDir ) throws IOException {

		String fileName = url.substring( url.lastIndexOf( '/' ) + 1 );
		String tmpPath = TMPDIR + File.separator + fileName;
		String finalPath =
				downloadDir + File.separator + fileName;
		return saveToFile( new URL( url ), tmpPath, finalPath );
	}

	private static List< File > saveComponent( Component component, String downloadDir ) throws IOException {

		List< Asset > assets = component.getAssets();
		List< File > files = new ArrayList<>( assets.size() );
		for ( Asset asset : assets ) {
			String url = asset.getDownloadUrl();
			String fileName = url.substring( url.lastIndexOf( '/' ) + 1 );
			String tmpPath = TMPDIR + File.separator + fileName;
			String finalPath =
					downloadDir + File.separator + fileName;
			files.add( saveToFile( new URL( url ), tmpPath, finalPath ) );
		}
		return files;

	}

	private static File saveToFile( URL url, String tmpPath, String finalPath ) throws IOException {

		try (ReadableByteChannel readableByteChannel = Channels.newChannel( url.openStream() )) {
			try (FileOutputStream fileOutputStream = new FileOutputStream( tmpPath )) {
				fileOutputStream.getChannel().transferFrom( readableByteChannel, 0, Long.MAX_VALUE );
				Files.copy(
						Paths.get( tmpPath ),
						Paths.get( finalPath ),
						StandardCopyOption.REPLACE_EXISTING );
				return new File( finalPath );
			}
		}
	}
}

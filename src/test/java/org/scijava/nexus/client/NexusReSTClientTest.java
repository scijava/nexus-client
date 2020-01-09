package org.scijava.nexus.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.scijava.nexus.client.NexusReSTClient;
import org.scijava.nexus.client.NexusReSTClientException;
import org.scijava.nexus.client.domain.Asset;
import org.scijava.nexus.client.domain.Component;
import org.scijava.nexus.client.domain.ComponentUploadForm;
import org.scijava.nexus.client.domain.Query;
import org.scijava.nexus.client.domain.Repository;
import org.scijava.nexus.client.domain.Query.Format;
import org.scijava.nexus.client.domain.Query.Sort;

/**
 * @author turekg
 */
@TestMethodOrder( MethodOrderer.OrderAnnotation.class )
public class NexusReSTClientTest {

	private final static String TEST_DIR = System.getProperty( "user.dir" ) + File.separator + "test-results";
	private final static String BASE_URL = System.getenv( "nexus.url" );
	private final static String USERNAME = System.getenv( "nexus.usr" );
	private final static String PASSWORD = System.getenv( "nexus.pwd" );

	private final static String REPO_TEST_RAW = "autotest-raw";
	private final static String ASSET1 = "jars/bridj-0.7.0.jar";
	private final static String TESTFILE1 = "test-data/clij/jars/bridj-0.7.0.jar";
	private final static String ASSET2 = "plugins/clij_-0.20.0.jar";
	private final static String TESTFILE2 = "test-data/clij/plugins/clij_-0.20.0.jar";
	private final static String ASSET3 = "plugins/db.xml.gz";
	private final static String TESTFILE3 = "test-data/clij/plugins/db.xml.gz";

	private static String assetId;

	@BeforeAll
	public static void init() throws IOException {
		File testDir = new File( TEST_DIR );
		if ( testDir.exists() ) {
			FileUtils.cleanDirectory( testDir );
		} else {
			Files.createDirectory( testDir.toPath(), new FileAttribute< ? >[ 0 ] );
		}
	}

	@Test
	@Order( 1 )
	public void testUploadandListComponents() throws Exception {
		ComponentUploadForm form = new ComponentUploadForm();
		form.setDirectory( "clij" );
		form.setFileData1( new FileInputStream( new File( TESTFILE1 ) ) );
		form.setFileName1( ASSET1 );
		form.setFileData2( new FileInputStream( new File( TESTFILE2 ) ) );
		form.setFileName2( ASSET2 );
		form.setFileData3( new FileInputStream( new File( TESTFILE3 ) ) );
		form.setFileName3( ASSET3 );
		NexusReSTClient.uploadComponent( BASE_URL, USERNAME, PASSWORD, REPO_TEST_RAW, form );
		List< Component > components = NexusReSTClient.listComponents( BASE_URL, REPO_TEST_RAW );
		assertEquals( 3, components.size() );
	}

	@Test
	@Order( 2 )
	public void testSearchAndGetComponent() throws Exception {
		//Sometimes Nexus takes a while to show all the uploaded files so best make this test wait....
		Thread.sleep(1000);
		Query q = new Query();
		q.setRepository( REPO_TEST_RAW );
		q.setComponentGroup( "/clij/jars" );
		List< Component > components = NexusReSTClient.searchComponents( BASE_URL, q );
		assertEquals( 1, components.size() );
		assertEquals( 1, components.get( 0 ).getAssets().size() );
		assetId = components.get( 0 ).getAssets().get( 0 ).getId();
		for ( Component comp : components ) {
			List< File > downloadedFiles = NexusReSTClient.getComponent( BASE_URL, comp.getId(), TEST_DIR );
			assertEquals( 1, downloadedFiles.size() );
			assertTrue( downloadedFiles.get( 0 ).exists() && downloadedFiles.get( 0 ).length() > 0 );
		}

	}

	@Test
	@Order( 3 )
	public void testSearchAssetsNonExistentArtifactId() throws Exception {
		Query q = new Query();
		q.setMavenArtifactId( "xxxxx" );
		List< Asset > assets = NexusReSTClient.searchAssets( BASE_URL, q );
		assertTrue( assets.size() == 0 );
	}

	@Test
	@Order( 4 )
	public void testSearchAssetsByArtifactId() throws Exception {
		Query q = new Query();
		q.setMavenArtifactId( "commons-io" );
		List< Asset > assets = NexusReSTClient.searchAssets( BASE_URL, q );
		assertTrue( assets.size() > 0 );
	}

	@Test
	@Order( 5 )
	public void testSearchAssetsByMavenGroupId() throws Exception {
		Query q = new Query();
		q.setMavenGroupId( "org.apache.ant" );
		List< Asset > assets = NexusReSTClient.searchAssets( BASE_URL, q );
		assertTrue( assets.size() > 0 );
	}

	@Test
	@Order( 6 )
	public void testSearchAssetsByMavenProperties() throws Exception {
		Query q = new Query();
		q.setRepository( "maven-central" );
		q.setMavenArtifactId( "ant" );
		q.setMavenGroupId( "org.apache.ant" );
		q.setMavenBaseVersion( "1.8.1" );
		q.setMavenExtension( "jar" );
		List< Asset > assets = NexusReSTClient.searchAssets( BASE_URL, q );
		assertEquals( 1, assets.size() );
	}

	@Test
	@Order( 7 )
	public void testSearchAssetsAndDownloadReturnsMoreThanOneResult() {
		try {
			Query q = new Query();
			q.setComponentName( "commons-io" );
			q.setComponentVersion( "2.6" );
			NexusReSTClient.searchAssetsAndDownload( BASE_URL, q, "commons-io.jar", TEST_DIR );
		} catch ( NexusReSTClientException e ) {
			assertEquals( 400, e.getHttpErrorCode() );
		}
	}

	@Test
	@Order( 8 )
	public void testSearchAssetsAndDownloadWithSort() throws Exception {
		Query q = new Query();
		q.setSortBy( Sort.NAME );
		q.setComponentName( "commons-io" );
		q.setComponentVersion( "2.6" );
		File downloaded = NexusReSTClient.searchAssetsAndDownload( BASE_URL, q, "commons-io.jar", TEST_DIR );
		assertTrue( downloaded.exists() );
		assertTrue( downloaded.length() > 0 );
	}

	@Test
	@Order( 9 )
	public void testListRepositories() throws Exception {
		List< Repository > repos = NexusReSTClient.listRepositories( BASE_URL );
		assertTrue( repos.size() > 0 );
		boolean testraw = false;
		boolean testmaven = false;
		for ( Repository repo : repos ) {
			if ( repo.getName().equals( "autotest-raw" ) ) testraw = true;
			if ( repo.getName().equals( "autotest-maven" ) ) testmaven = true;
		}
		assertTrue( testraw );
		assertTrue( testmaven );
	};

	@Test
	@Order( 10 )
	public void testListAssets() throws Exception {
		List< Asset > assets = NexusReSTClient.listAssets( BASE_URL, REPO_TEST_RAW );
		assertTrue( assets.size() > 0 );
	};

	@Test
	@Order( 11 )
	public void testListAssetsForUnknownRepo() {
		try {
			NexusReSTClient.listAssets( BASE_URL, "xkxkxk" );
		} catch ( NexusReSTClientException e ) {
			assertEquals( 404, e.getHttpErrorCode() );
		}
	};

	@Test
	@Order( 12 )
	public void testGetAsset() throws Exception {
		File downloadedFile = NexusReSTClient.getAsset( BASE_URL, assetId, TEST_DIR );
		assertTrue( downloadedFile.exists() );
		assertTrue( downloadedFile.length() > 0 );
	}

	@Test
	@Order( 13 )
	public void testDeleteAsset() {
		try {
			NexusReSTClient.deleteAsset( BASE_URL, USERNAME, PASSWORD, assetId );
			Query q = new Query();
			q.setRepository( REPO_TEST_RAW );
			q.setFormat( Format.RAW);
			q.setComponentGroup( "/clij/jars" );
			List< Asset > tbdeleted = NexusReSTClient.searchAssets( BASE_URL, q );
			assertEquals( 1, tbdeleted.size() );
			NexusReSTClient.deleteAsset( BASE_URL, USERNAME, PASSWORD, tbdeleted.get( 0 ).getId() );
			NexusReSTClient.searchAssets( BASE_URL, q );

		} catch ( NexusReSTClientException e ) {

			assertEquals( 404, e.getHttpErrorCode() );
		}
	}

	@Test
	@Order( 14 )
	public void testAnonCannotDeleteAsset() {
		try {
			NexusReSTClient.deleteAsset( BASE_URL, null, null, assetId );
		} catch ( NexusReSTClientException e ) {
			assertTrue( e.getCause() instanceof NullPointerException );
		}
	}

	@Test
	@Order( 15 )
	public void testDeleteAssetWrongAuth() {
		try {
			NexusReSTClient.deleteAsset( BASE_URL, "dadada", "ddddd", assetId );
		} catch ( NexusReSTClientException e ) {
			assertEquals( 401, e.getHttpErrorCode() );
		}
	}

	@Test
	@Order( 16 )
	public void testDeleteComponentWrongAuth() {
		try {
			NexusReSTClient.deleteComponent( BASE_URL, "dadada", "ddddd", assetId );
		} catch ( NexusReSTClientException e ) {
			assertEquals( 401, e.getHttpErrorCode() );
		}
	}

	@Test
	@Order( 17 )
	public void testDeleteComponent() throws InterruptedException {
		try {
			//Sometimes Nexus takes a while to process the asset deletion that occurred earlier, so best make this test wait....
			Thread.sleep(1000);
			Query q = new Query();
			q.setRepository( REPO_TEST_RAW );
			List< Component > components = NexusReSTClient.searchComponents( BASE_URL, q );
			assertEquals( 2, components.size() );
			for ( Component comp : components ) {
				NexusReSTClient.deleteComponent( BASE_URL, USERNAME, PASSWORD, comp.getId() );
			}
			NexusReSTClient.searchComponents( BASE_URL, q );
		} catch ( NexusReSTClientException e ) {

			assertEquals( 404, e.getHttpErrorCode() );
		}
	}
}

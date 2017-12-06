package fiji.plugin.trackmate.visualization;

import java.io.File;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableSet;

//import ij.ImageJ;
import cleargl.GLVector;
import graphics.scenery.Material;
import graphics.scenery.PointCloud;
import net.imglib2.util.Util;
import net.imagej.ImageJ;

import fiji.plugin.trackmate.Model;
import fiji.plugin.trackmate.Spot;
import fiji.plugin.trackmate.SpotCollection;
import fiji.plugin.trackmate.TrackMate;
import fiji.plugin.trackmate.io.TmXmlReader;
import org.lwjgl.system.MemoryUtil;
import org.scijava.Context;
import org.scijava.service.Service;
import sc.iview.SciView;
import sc.iview.SciViewService;

/**
 * This class loads a model and generate an empty view for it.
 */
public class SceneryTestDrive
{

	public static void main( String[] args )
	{
		File file = new File( "samples/FakeTracks.xml" );
		TmXmlReader reader = new TmXmlReader( file );
		if ( !reader.isReadingOk() )
		{
			System.err.println( TrackMate.PLUGIN_NAME_STR + " v" + TrackMate.PLUGIN_NAME_VERSION + ":\n" + reader.getErrorMessage() );
			return;
		}
		System.out.println( "Loaded model." );
		
		Model model = reader.getModel();
		SpotCollection spots = model.getSpots();
		
		NavigableSet< Integer > frames = spots.keySet();
		System.out.println( "Found " + frames.size() + " frames in the data." );

		ArrayList<float[]> verts = new ArrayList<float[]>();

		boolean visibleOnly = false;
		for ( Integer frame : frames )
		{
			System.out.println( "\nFor frame " + frame );
			for (Spot spot : spots.iterable( frame, visibleOnly  ) ) {
				System.out.println(" - " + spot.getName() + " pos = " + Util.printCoordinates(spot) + " visibility = " + (0 != spot.getFeature(SpotCollection.VISIBLITY)));
				float[] thisCoord = new float[3];
				spot.localize(thisCoord);
				verts.add( thisCoord );
			}
		}

		float[] flatVerts = new float[verts.size()*3];
		for( int k = 0; k < verts.size(); k++ ) {
			flatVerts[k*3] = verts.get(k)[0];
			flatVerts[k*3+1] = verts.get(k)[1];
			flatVerts[k*3+2] = verts.get(k)[2];
		}


		net.imagej.ImageJ ij = new net.imagej.ImageJ();

		Context ctxt = ij.getContext();

		SciViewService sciviewService = (SciViewService)ctxt.getService("sc.iview.SciViewService");
		SciView sv = sciviewService.getOrCreateActiveSciView();

		PointCloud pointCloud = new PointCloud( 0.025f, "TrackMatePointCloud");
		Material material = new Material();
		FloatBuffer vBuffer = MemoryUtil.memAlloc(flatVerts.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		FloatBuffer nBuffer = MemoryUtil.memAlloc(0).order(ByteOrder.nativeOrder()).asFloatBuffer();

		vBuffer.put( flatVerts );
		vBuffer.flip();

		pointCloud.setVertices(vBuffer);
		pointCloud.setNormals(nBuffer);
		pointCloud.setIndices(MemoryUtil.memAlloc(0).order( ByteOrder.nativeOrder()).asIntBuffer() );
		pointCloud.setupPointCloud();
		material.setAmbient( new GLVector( 1.0f, 1.0f, 1.0f ));
		material.setDiffuse( new GLVector( 1.0f, 1.0f, 1.0f ));
		material.setSpecular( new GLVector( 1.0f, 1.0f, 1.0f ));
		pointCloud.setMaterial( material );
		pointCloud.setPosition( new GLVector( 0f, 0f, 0f ) );
		sv.addNode( pointCloud );
	}
}

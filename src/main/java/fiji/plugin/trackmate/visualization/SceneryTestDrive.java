package fiji.plugin.trackmate.visualization;

import java.io.File;
import java.util.NavigableSet;

import net.imglib2.util.Util;

import fiji.plugin.trackmate.Model;
import fiji.plugin.trackmate.Spot;
import fiji.plugin.trackmate.SpotCollection;
import fiji.plugin.trackmate.TrackMate;
import fiji.plugin.trackmate.io.TmXmlReader;

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
		
		boolean visibleOnly = false;
		for ( Integer frame : frames )
		{
			System.out.println( "\nFor frame " + frame );
			for (Spot spot : spots.iterable( frame, visibleOnly  ) )
				System.out.println( " - " + spot.getName() + " pos = " + Util.printCoordinates( spot ) + " visibility = " + ( 0 != spot.getFeature( SpotCollection.VISIBLITY ) ) );
		}
		
	}
}

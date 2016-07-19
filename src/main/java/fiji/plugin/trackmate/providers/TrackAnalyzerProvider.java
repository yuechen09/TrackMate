package fiji.plugin.trackmate.providers;

import org.scijava.plugin.Plugin;
import org.scijava.service.Service;

import fiji.plugin.trackmate.features.track.TrackAnalyzer;

/**
 * A provider for the track analyzers provided in the GUI.
 * <p>
 * Feature key names are for historical reason all capitalized in an enum
 * manner. For instance: POSITION_X, MAX_INTENSITY, etc... They must be suitable
 * to be used as a attribute key in an xml file.
 */
@Plugin(type = Service.class)
public class TrackAnalyzerProvider extends AbstractProvider< TrackAnalyzer >
{

	public TrackAnalyzerProvider()
	{
		super( TrackAnalyzer.class );
	}

	public static void main( final String[] args )
	{
		final TrackAnalyzerProvider provider = new TrackAnalyzerProvider();
		System.out.println( provider.echo() );
	}
}

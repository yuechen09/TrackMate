package fiji.plugin.trackmate.providers;

import org.scijava.plugin.Plugin;
import org.scijava.service.Service;

import fiji.plugin.trackmate.detection.SpotDetectorFactory;

@SuppressWarnings( "rawtypes" )
@Plugin(type = Service.class)
public class DetectorProvider extends AbstractProvider< SpotDetectorFactory >
{

	public DetectorProvider()
	{
		super( SpotDetectorFactory.class );
	}

	public static void main( final String[] args )
	{
		final DetectorProvider provider = new DetectorProvider();
		System.out.println( provider.echo() );
	}

}

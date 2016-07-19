package fiji.plugin.trackmate.providers;

import org.scijava.plugin.Plugin;
import org.scijava.service.Service;

import fiji.plugin.trackmate.tracking.SpotTrackerFactory;

@Plugin(type = Service.class)
public class TrackerProvider extends AbstractProvider< SpotTrackerFactory >
{


	public TrackerProvider()
	{
		super( SpotTrackerFactory.class );
	}

	public static void main( final String[] args )
	{
		final TrackerProvider provider = new TrackerProvider();
		System.out.println( provider.echo() );
	}
}

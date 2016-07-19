package fiji.plugin.trackmate.providers;

import org.scijava.plugin.Plugin;
import org.scijava.service.Service;

import fiji.plugin.trackmate.action.TrackMateActionFactory;

@Plugin(type = Service.class)
public class ActionProvider extends AbstractProvider< TrackMateActionFactory >
{

	public ActionProvider()
	{
		super( TrackMateActionFactory.class );
	}

	public static void main( final String[] args )
	{
		final ActionProvider provider = new ActionProvider();
		System.out.println( provider.echo() );
	}

}

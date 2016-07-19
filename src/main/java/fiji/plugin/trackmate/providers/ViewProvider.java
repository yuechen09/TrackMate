package fiji.plugin.trackmate.providers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.scijava.InstantiableException;
import org.scijava.log.LogService;
import org.scijava.plugin.AbstractPTService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.PluginInfo;
import org.scijava.service.Service;

import fiji.plugin.trackmate.TrackMateService;
import fiji.plugin.trackmate.visualization.ViewFactory;

@Plugin(type = Service.class)
public class ViewProvider extends AbstractPTService<ViewFactory> implements TrackMateService
{
	/**
	 * The view keys, in the order they will appear in the GUI.
	 */
	protected List< String > keys = new ArrayList< String >();

	protected List< String > visibleKeys = new ArrayList< String >();

	protected Map< String, ViewFactory > factories = new HashMap< String, ViewFactory >();

	@Parameter
	private LogService log;

	@Override
	public Class< ViewFactory > getPluginType() {
		return ViewFactory.class;
	}

	@Override
	public void initialize()
	{
		registerViews();
	}

	private void registerView( final String key, final ViewFactory view, final boolean visible )
	{
		keys.add( key );
		factories.put( key, view );
		if ( visible )
		{
			visibleKeys.add( key );
		}
	}

	public ViewFactory getFactory( final String key )
	{
		return factories.get( key );
	}

	public List< String > getAvailableViews()
	{
		return keys;
	}

	public List< String > getVisibleViews()
	{
		return visibleKeys;
	}

	protected void registerViews()
	{
		final List< PluginInfo< ViewFactory > > infos = getPlugins();

		final Comparator< PluginInfo< ViewFactory > > priorityComparator = new Comparator< PluginInfo< ViewFactory > >()
		{
			@Override
			public int compare( final PluginInfo< ViewFactory > o1, final PluginInfo< ViewFactory > o2 )
			{
				return o1.getPriority() > o2.getPriority() ? 1 : o1.getPriority() < o2.getPriority() ? -1 : 0;
			}
		};

		Collections.sort( infos, priorityComparator );

		for ( final PluginInfo< ViewFactory > info : infos )
		{
			try
			{
				final ViewFactory view = info.createInstance();
				registerView( view.getKey(), view, info.isVisible() );
			}
			catch ( final InstantiableException e )
			{
				log.error( "Could not instantiate " + info.getClassName(), e );
			}
		}
	}
}

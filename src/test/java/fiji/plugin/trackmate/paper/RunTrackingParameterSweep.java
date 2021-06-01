package fiji.plugin.trackmate.paper;

import java.io.File;
import java.nio.file.Paths;

import fiji.plugin.trackmate.Logger;
import fiji.plugin.trackmate.Model;
import fiji.plugin.trackmate.Settings;
import fiji.plugin.trackmate.TrackMate;
import fiji.plugin.trackmate.action.ISBIChallengeExporter;
import fiji.plugin.trackmate.io.TmXmlReader;
import fiji.plugin.trackmate.tracking.LAPUtils;
import fiji.plugin.trackmate.tracking.TrackerKeys;
import fiji.plugin.trackmate.tracking.kalman.KalmanTrackerFactory;
import fiji.plugin.trackmate.tracking.sparselap.SimpleSparseLAPTrackerFactory;
import ij.ImagePlus;

public class RunTrackingParameterSweep
{
	public static void main( final String[] args )
	{
		final String rootDataFolder = "C:/Users/tinevez/Desktop/TrackMatePaper/Data/ISBIChallengeAccuracy/images";
		final String saveDataFolder = "C:/Users/tinevez/Desktop/TrackMatePaper/Data/ISBIChallengeAccuracy/results";
		final String[] categories = { "MICROTUBULE", "RECEPTOR", "VESICLE" };

		for ( final String category : categories )
		{
			final File[] files = Paths.get( rootDataFolder, category )
					.toFile()
					.listFiles( ( d, name ) -> name.endsWith( ".xml" ) && name.startsWith( category ) );

			for ( final File tmFile : files )
			{
				final String testName = tmFile.getName().substring( 0, tmFile.getName().lastIndexOf( '.' ) );
				final File saveFolder = Paths.get( saveDataFolder, category, testName ).toFile();
				paramSweep( tmFile, saveFolder );
			}
		}
	}

	private static void paramSweep( final File tmFile, final File saveFolder )
	{
		if ( !saveFolder.exists() )
			saveFolder.mkdirs();

		System.out.println( " - " + tmFile );
		final TmXmlReader reader = new TmXmlReader( tmFile );
		if ( !reader.isReadingOk() )
		{
			System.err.println( reader.getErrorMessage() );
			return;
		}

		final Model model = reader.getModel();
		final ImagePlus imp = reader.readImage();
		final Settings settings = reader.readSettings( imp );

		final TrackMate trackmate = new TrackMate( model, settings );
		model.setLogger( Logger.VOID_LOGGER );

		/*
		 * First, LAP tracker.
		 */

		settings.trackerFactory = new SimpleSparseLAPTrackerFactory();
		settings.trackerSettings = LAPUtils.getDefaultLAPSettingsMap();
		for ( int g = 1; g < 6; g++ )
		{
			final int gapClosingFrameGap = g;
			for ( int r = 0; r < 9; r++ )
			{
				final double maxLinkingDistance = 2. + r * 2.;
				settings.trackerSettings.put( TrackerKeys.KEY_LINKING_MAX_DISTANCE, Double.valueOf( maxLinkingDistance ) );
				settings.trackerSettings.put( TrackerKeys.KEY_GAP_CLOSING_MAX_DISTANCE, Double.valueOf( maxLinkingDistance ) );
				settings.trackerSettings.put( TrackerKeys.KEY_GAP_CLOSING_MAX_FRAME_GAP, Integer.valueOf( gapClosingFrameGap ) );

				trackmate.execTracking();

				final String exportName = String.format( "%s-LAP-d%.0f-g%d.xml",
						tmFile.getName().substring(0, tmFile.getName().lastIndexOf('.')), 
						maxLinkingDistance, gapClosingFrameGap);
				final File exportFile = Paths.get( saveFolder.getAbsolutePath(), exportName ).toFile();
				ISBIChallengeExporter.exportToFile( model, settings, exportFile, Logger.VOID_LOGGER );
			}
		}

		/*
		 * First, Kalman tracker.
		 */

		settings.trackerFactory = new KalmanTrackerFactory();
		settings.trackerSettings = settings.trackerFactory.getDefaultSettings();
		for ( int g = 1; g < 6; g++ )
		{
			final int gapClosingFrameGap = g;
			for ( int r = 0; r < 9; r++ )
			{
				final double maxLinkingDistance = 2. + r * 2.;
				settings.trackerSettings.put( TrackerKeys.KEY_LINKING_MAX_DISTANCE, Double.valueOf( maxLinkingDistance ) );
				settings.trackerSettings.put( KalmanTrackerFactory.KEY_KALMAN_SEARCH_RADIUS, Double.valueOf( maxLinkingDistance ) );
				settings.trackerSettings.put( TrackerKeys.KEY_GAP_CLOSING_MAX_FRAME_GAP, Integer.valueOf( gapClosingFrameGap ) );

				trackmate.execTracking();

				final String exportName = String.format( "%s-KT-d%.0f-g%d.xml",
						tmFile.getName().substring( 0, tmFile.getName().lastIndexOf( '.' ) ),
						maxLinkingDistance, gapClosingFrameGap );
				final File exportFile = Paths.get( saveFolder.getAbsolutePath(), exportName ).toFile();
				ISBIChallengeExporter.exportToFile( model, settings, exportFile, Logger.VOID_LOGGER );
			}
		}
	}
}

package fiji.plugin.trackmate.paper;

import java.io.File;
import java.nio.file.Paths;

import fiji.plugin.trackmate.Logger;
import fiji.plugin.trackmate.Model;
import fiji.plugin.trackmate.Settings;
import fiji.plugin.trackmate.TrackMate;
import fiji.plugin.trackmate.action.ISBIChallengeExporter;
import fiji.plugin.trackmate.detection.DetectorKeys;
import fiji.plugin.trackmate.detection.LogDetectorFactory;
import fiji.plugin.trackmate.tracking.LAPUtils;
import fiji.plugin.trackmate.tracking.TrackerKeys;
import fiji.plugin.trackmate.tracking.kalman.KalmanTrackerFactory;
import fiji.plugin.trackmate.tracking.sparselap.SimpleSparseLAPTrackerFactory;
import ij.IJ;
import ij.ImagePlus;

public class RunLoGTracking
{
	public static void main( final String[] args )
	{
		final String rootDataFolder = "C:/Users/tinevez/Desktop/TrackMatePaper/Data/ISBIChallengeAccuracy/images";
		final String saveDataFolder = "C:/Users/tinevez/Desktop/TrackMatePaper/Data/ISBIChallengeAccuracy/results-log";
		final String[] categories = { "MICROTUBULE" };

		for ( final String category : categories )
		{
			final File[] files = Paths.get( rootDataFolder, category )
					.toFile()
					.listFiles( ( d, name ) -> name.endsWith( ".tif" ) && name.startsWith( category ) );

			for ( final File imFile : files )
			{
				final String testName = imFile.getName().substring( 0, imFile.getName().lastIndexOf( '.' ) );
				final File saveFolder = Paths.get( saveDataFolder, category, testName ).toFile();
				paramSweep( imFile, saveFolder );
			}
		}
		System.out.println( "Finished!" );
	}

	private static void paramSweep( final File imFile, final File saveFolder )
	{
		if ( !saveFolder.exists() )
			saveFolder.mkdirs();

		System.out.println( " - " + imFile );
		final ImagePlus imp = IJ.openImage( imFile.getAbsolutePath() );

		final Model model = new Model();
		final Settings settings = new Settings();
		settings.setFrom( imp );

		final TrackMate trackmate = new TrackMate( model, settings );
		model.setLogger( Logger.VOID_LOGGER );

		/*
		 * Detection.
		 */

		settings.detectorFactory = new LogDetectorFactory<>();
		settings.detectorSettings = settings.detectorFactory.getDefaultSettings();
		settings.detectorSettings.put( DetectorKeys.KEY_RADIUS, 5. );
		settings.detectorSettings.put( DetectorKeys.KEY_THRESHOLD, 0.37 );

		/*
		 * First, LAP tracker.
		 */

		settings.trackerFactory = new SimpleSparseLAPTrackerFactory();
		settings.trackerSettings = LAPUtils.getDefaultLAPSettingsMap();
		for ( int g = 1; g < 3; g++ )
		{
			final int gapClosingFrameGap = g;
			for ( int r = 3; r < 10; r = r + 5 )
			{
				final double maxLinkingDistance = 2. + r * 2.;
				settings.trackerSettings.put( TrackerKeys.KEY_LINKING_MAX_DISTANCE, Double.valueOf( maxLinkingDistance ) );
				settings.trackerSettings.put( TrackerKeys.KEY_GAP_CLOSING_MAX_DISTANCE, Double.valueOf( maxLinkingDistance ) );
				settings.trackerSettings.put( TrackerKeys.KEY_GAP_CLOSING_MAX_FRAME_GAP, Integer.valueOf( gapClosingFrameGap ) );

				final String exportName = String.format( "%s-LAP-d%.0f-g%d.xml",
						imFile.getName().substring( 0, imFile.getName().lastIndexOf( '.' ) ),
						maxLinkingDistance, gapClosingFrameGap );
				if ( !trackmate.checkInput() || !trackmate.process() )
				{
					System.err.println( "Problem generating " + exportName + "\n" +
							trackmate.getErrorMessage() );
					return;
				}

				final File exportFile = Paths.get( saveFolder.getAbsolutePath(), exportName ).toFile();
				ISBIChallengeExporter.exportToFile( model, settings, exportFile, Logger.VOID_LOGGER );
			}
		}

		/*
		 * First, Kalman tracker.
		 */

		settings.trackerFactory = new KalmanTrackerFactory();
		settings.trackerSettings = settings.trackerFactory.getDefaultSettings();
		for ( int g = 1; g < 3; g++ )
		{
			final int gapClosingFrameGap = g;
			for ( int r = 3; r < 10; r = r + 5 )
			{
				final double maxLinkingDistance = 2. + r * 2.;
				settings.trackerSettings.put( TrackerKeys.KEY_LINKING_MAX_DISTANCE, Double.valueOf( maxLinkingDistance ) );
				settings.trackerSettings.put( KalmanTrackerFactory.KEY_KALMAN_SEARCH_RADIUS, Double.valueOf( maxLinkingDistance ) );
				settings.trackerSettings.put( TrackerKeys.KEY_GAP_CLOSING_MAX_FRAME_GAP, Integer.valueOf( gapClosingFrameGap ) );

				final String exportName = String.format( "%s-KT-d%.0f-g%d.xml",
						imFile.getName().substring( 0, imFile.getName().lastIndexOf( '.' ) ),
						maxLinkingDistance, gapClosingFrameGap );
				if ( !trackmate.checkInput() || !trackmate.process() )
				{
					System.err.println( "Problem generating " + exportName + "\n" +
							trackmate.getErrorMessage() );
					return;
				}

				final File exportFile = Paths.get( saveFolder.getAbsolutePath(), exportName ).toFile();
				ISBIChallengeExporter.exportToFile( model, settings, exportFile, Logger.VOID_LOGGER );
			}
		}
	}
}

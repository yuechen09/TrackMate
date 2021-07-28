package fiji.plugin.trackmate;

import fiji.plugin.trackmate.detection.LogDetectorFactory;
import fiji.plugin.trackmate.gui.GuiUtils;
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;

public class DebugMemoryLeak2
{

	public static void main( final String[] args ) throws InterruptedException
	{
		GuiUtils.setSystemLookAndFeel();
		ImageJ.main( args );

		final String path = "C:/Users/tinevez/Desktop/TrackMateDLPaper/Data/ISBIChallengeAccuracy/images/VIRUS/VIRUS snr 7 density high.tif";
		final ImagePlus imp = IJ.openImage( path );

		final Thread thread = new Thread( () -> {
			System.out.println( "Starting TrackMate" );
			final Settings settings = new Settings();
			settings.setFrom( imp );
			settings.detectorFactory = new LogDetectorFactory<>();
			settings.detectorSettings = settings.detectorFactory.getDefaultSettings();
			settings.detectorSettings.put( "RADIUS", 2.5 );
			settings.detectorSettings.put( "THRESHOLD", 50. );
			settings.initialSpotFilterValue = 0.;

			final Model model = new Model();
			final TrackMate trackmate = new TrackMate( model, settings );
			trackmate.execDetection();
			trackmate.execInitialSpotFiltering();
			trackmate.computeSpotFeatures( true );
		} );

		thread.start();
		thread.join();
		System.out.println( "finished." ); // DEBUG
	}
}

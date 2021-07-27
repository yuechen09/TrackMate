package fiji.plugin.trackmate;

import javax.swing.SwingUtilities;

import fiji.plugin.trackmate.gui.GuiUtils;
import ij.ImageJ;
import ij.plugin.frame.MemoryMonitor;

public class DebugMemoryLeak
{

	public static void main( final String[] args )
	{
		GuiUtils.setSystemLookAndFeel();
		ImageJ.main( args );

		final String path = "C:/Users/tinevez/Desktop/TrackMateDLPaper/Data/ISBIChallengeAccuracy/images/VIRUS/VIRUS snr 7 density high.tif";
		new TrackMatePlugIn().run( path );

		final MemoryMonitor monitor = new MemoryMonitor();
		SwingUtilities.invokeLater( () -> monitor.run( null ) );
	}
}

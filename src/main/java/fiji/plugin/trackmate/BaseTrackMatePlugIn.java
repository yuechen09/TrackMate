package fiji.plugin.trackmate;

import org.scijava.Context;

import ij.IJ;
import ij.plugin.PlugIn;

public interface BaseTrackMatePlugIn extends PlugIn {

	default Context context() {
		return (Context) IJ.runPlugIn("org.scijava.Context", "");
	}

}

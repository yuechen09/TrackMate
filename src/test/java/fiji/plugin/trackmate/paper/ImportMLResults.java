package fiji.plugin.trackmate.paper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Iterator;

import com.opencsv.CSVReader;

import fiji.plugin.trackmate.Model;
import fiji.plugin.trackmate.Settings;
import fiji.plugin.trackmate.Spot;
import fiji.plugin.trackmate.TrackMate;
import fiji.plugin.trackmate.detection.ManualDetectorFactory;
import fiji.plugin.trackmate.io.TmXmlWriter;
import fiji.plugin.trackmate.tracking.sparselap.SimpleSparseLAPTrackerFactory;
import fiji.plugin.trackmate.util.TMUtils;
import ij.IJ;
import ij.ImagePlus;

public class ImportMLResults
{

	public static void main( final String[] args )
	{
		// Where is the detection CSV file?
		final String csvFile = "C:/Users/tinevez/Desktop/TrackMatePaper/Data/ISBIChallengeAccuracy/model_predictions.csv";

		// Where are the image files?
		final String rootDataFolder = "C:/Users/tinevez/Desktop/TrackMatePaper/Data/ISBIChallengeAccuracy/images";

		try (CSVReader reader = new CSVReader( new FileReader( new File( csvFile ) ) ))
		{
			final Iterator< String[] > iterator = reader.iterator();

			// Skip header line.
			iterator.next();

			int index = 0;
			String currentFileName = "";
			Model model = null;

			while ( iterator.hasNext() )
			{
				final String[] line = iterator.next();

				// Get target file name.
				final String fileNameFrame = line[ 0 ];
				final int delimPos = fileNameFrame.lastIndexOf( '_' );
				final String fileName = fileNameFrame.substring( 0, delimPos );

				if ( !fileName.equals( currentFileName ) )
				{

					if ( !currentFileName.isEmpty() )
						saveTo( model, rootDataFolder, currentFileName, csvFile );

					/*
					 * Create new model.
					 */

					model = new Model();
					model.setPhysicalUnits( "pixel", "frame" );

					// Set next output.

					System.out.println( "\nSwitching to a new file: " + fileName + " at line " + index );
					currentFileName = fileName;
				}

				final int ID = Integer.parseInt( line[ 1 ] );
				final int frame = Integer.parseInt( line[ 2 ] );
				final double x = Double.parseDouble( line[ 3 ] );
				final double y = Double.parseDouble( line[ 4 ] );
				final double z = Double.parseDouble( line[ 5 ] );
				final double r = Double.parseDouble( line[ 6 ] );
				final double q = Double.parseDouble( line[ 7 ] );

				final Spot spot = new Spot( x, y, z, r, q, "" + ID );
				spot.putFeature( Spot.POSITION_T, Double.valueOf( frame ) );
				model.getSpots().add( spot, Integer.valueOf( frame ) );

				index++;
			}
			saveTo( model, rootDataFolder, currentFileName, csvFile );

			System.out.println( "\nFinished parsing " + index + " lines." );
		}
		catch ( final FileNotFoundException e )
		{
			System.out.println( "Could not find file " + csvFile );
			e.printStackTrace();
		}
		catch ( final IOException e )
		{
			System.out.println( "Problem opening file " + csvFile );
			e.printStackTrace();
		}
	}

	/**
	 * Serializes current model to a ready-to-use TrackMate file.
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private static final void saveTo( final Model model, final String rootDataFolder, final String currentFileName, final String csvFile ) throws FileNotFoundException, IOException
	{
		// Get target category.
		final int indexOfSpace = currentFileName.indexOf( " " );
		final String category = currentFileName.substring( 0, indexOfSpace );

		// Build Image file name and open image.
		final String imageFileName = currentFileName + ".tif";
		final ImagePlus imp = IJ.openImage( Paths.get( rootDataFolder, category, imageFileName ).toString() );

		// Create settings object.
		final Settings settings = new Settings();
		settings.setFrom( imp );
		settings.addAllAnalyzers();
		settings.initialSpotFilterValue = 0.;
		settings.detectorFactory = new ManualDetectorFactory<>();
		settings.detectorSettings = Collections.singletonMap( "RADIUS", 2.5 );
		settings.trackerFactory = new SimpleSparseLAPTrackerFactory();
		settings.trackerSettings = settings.trackerFactory.getDefaultSettings();

		// Create TrackMate object and compute spot features.
		final TrackMate trackmate = new TrackMate( model, settings );
		trackmate.computeSpotFeatures( false );
		imp.close();

		// Build TrackMate file name.
		final String trackmateFileName = currentFileName + ".xml";

		// Build TrackMate file path.
		final File trackmateFile = Paths.get( rootDataFolder, category, trackmateFileName ).toFile();

		// Save TrackMate to file.
		final TmXmlWriter writer = new TmXmlWriter( trackmateFile );
		writer.appendLog( "Created on the " + TMUtils.getCurrentTimeString() + " by importing the file " + csvFile );
		writer.appendSettings( settings );
		writer.appendModel( model );
		writer.appendGUIState( "ChooseTracker" );
		writer.writeToFile();

		System.out.println( "Saved to file '" + trackmateFile );
	}
}

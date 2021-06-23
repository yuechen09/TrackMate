/*-
 * #%L
 * Fiji distribution of ImageJ for the life sciences.
 * %%
 * Copyright (C) 2010 - 2021 Fiji developers.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package fiji.plugin.trackmate.visualization;

import java.awt.Color;

import fiji.plugin.trackmate.Spot;

/**
 * A dummy spot color generator that always return the default color.
 *
 * @author Jean-Yves Tinevez - 2013. Revised December 2020.
 */
public class UniformSpotColorGenerator implements FeatureColorGenerator< Spot >
{

	private final Color color;

	public UniformSpotColorGenerator( final Color color )
	{
		this.color = color;
	}

	@Override
	public Color color( final Spot obj )
	{
		return color;
	}
}
package asteroids;

import java.awt.Shape;
import java.awt.geom.Path2D;

/**
 * This class creates the debris that is seen
 * when an asterod object hits a ship object.
 * 
* @author James Fairbourn
*/
public class ShipDebris extends Participant {

	//the outline of the ship debris
	private Shape outline;
	
	/**
	 * Selects a certain style of debris
	 * when the debris variety is sent as
	 * a parameter 
	 * 
	 * @param variety The choice of debris variety.
	 */
	public ShipDebris(int variety)
	{
		outline = createDebris(variety);
	}
	
	/**
	 * Creates the actual shape of the debris
	 * depending on which variety has been called.
	 * Uses specific coordinates to make the shapes. 
	 * 
	 * @param variety The choice of debris variety
	 * @return the specific shape of debris chosen.
	 */
	private Shape createDebris (int variety)
	{
		Path2D.Double poly = new Path2D.Double();
		if(variety==1)
		{
		poly.moveTo(1,0);
		poly.lineTo(20,0);
		poly.closePath();
		
		}
		else
		{
		 poly.moveTo(1, 0);
		 poly.lineTo(10, 10);
		 poly.closePath();
		}
		return poly;
	}
	
	@Override
	Shape getOutline() {
		// TODO Auto-generated method stub
		return outline;
	}


}

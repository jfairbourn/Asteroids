package asteroids;

import java.awt.Shape;
import java.awt.geom.Path2D;

/**
 * Creates a Debris object that is used as
 * debris when thing collide in the Asteroids game.
 * 
* @author James Fairbourn
*/
public class Debris extends Participant
{
	//the outline of the debris
	private Shape outline;
	
	/**
	 * Creates the actual shape of a single piece
	 * of debris with the specific coordinates.
	 */
	public Debris()
	{
		Path2D.Double poly = new Path2D.Double();
		poly.moveTo(1,0);
		poly.lineTo(0,1);
		poly.lineTo(1, 1);
		poly.lineTo(0, 0);
		poly.closePath();
		outline = poly;
	}
	@Override
	Shape getOutline() {
		
		return outline;
	}

}

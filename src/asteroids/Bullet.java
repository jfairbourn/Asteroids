package asteroids;

import java.awt.Shape;
import java.awt.geom.Path2D;
/**
 * This class creates Bullet Objects.
 * 
* @author James Fairbourn
*/

class Bullet extends Participant
{
	//the outline of the bullet.
	private Shape outline;
	
	/**
	 * Creates the actual shape of the bullet according
	 * to the specific coordinates.
	 */
	public Bullet()
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
		// TODO Auto-generated method stub
		return outline;
	}

}

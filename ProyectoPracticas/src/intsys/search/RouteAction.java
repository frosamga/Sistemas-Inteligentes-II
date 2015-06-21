package intsys.search;

import aima.core.agent.impl.DynamicAction;
import aima.core.util.datastructure.XYLocation;

/**
 * Possible actions for the agent.
 * 
 * @author Enrique and Ezequiel
 *
 */
public class RouteAction extends DynamicAction {

	public static final String UP = "Up";
	public static final String DOWN = "Down";
	public static final String LEFT = "Left";
	public static final String RIGHT = "Right";	
	
	public static final String ATTR_POS = "position";
	
	public RouteAction(String name, XYLocation pos) {
		super(name);
		setAttribute(ATTR_POS, pos);
	}

	
	public XYLocation getPosition() {
		return (XYLocation)getAttribute(ATTR_POS);
	}
	
	public int getX() {
		return getPosition().getXCoOrdinate();
	}
	
	public int getY() {
		return getPosition().getYCoOrdinate();
	}
}

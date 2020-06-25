package polygon.datastructure;

public class Vertex 
{

	 public int VertexId;
     
     public HalfEdge IncidentEdge;
    
     public Point Coordinate;
    
     public VertexType Type;
    
     public boolean IsLeft;
     
     private boolean IsMarked;
     
     private String Colour;
     

	public boolean isIsMarked() {
		return IsMarked;
	}

	public void setIsMarked(boolean isMarked) {
		IsMarked = isMarked;
	}

	public String getColour() {
		return Colour;
	}

	public void setColour(String colour) {
		Colour = colour;
	}

	public int getVertexId()
	{
		return VertexId;
	}

	public void setVertexId(int vertexId) {
		VertexId = vertexId;
	}

	public HalfEdge getIncidentEdge() {
		return IncidentEdge;
	}

	public void setIncidentEdge(HalfEdge incidentEdge) {
		IncidentEdge = incidentEdge;
	}

	public Point getCoordinate() {
		return Coordinate;
	}

	public void setCoordinate(Point coordinate) {
		Coordinate = coordinate;
	}

	public VertexType getType() {
		return Type;
	}

	public void setType(VertexType type) {
		Type = type;
	}

	public boolean isIsLeft() {
		return IsLeft;
	}

	public void setIsLeft(boolean isLeft) {
		IsLeft = isLeft;
	}
     
     

}

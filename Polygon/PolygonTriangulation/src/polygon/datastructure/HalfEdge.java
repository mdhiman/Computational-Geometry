package polygon.datastructure;

public class HalfEdge 
{

	public String EdgeId;
    public Vertex Origin;
    public HalfEdge Twin;
    public Face IncidentFace;
    public HalfEdge Next;
    public HalfEdge Previous;
    public Vertex Helper;
    
	public String getEdgeId()
	{
		return EdgeId;
	}
	public void setEdgeId(String edgeId) {
		EdgeId = edgeId;
	}
	public Vertex getOrigin() {
		return Origin;
	}
	public void setOrigin(Vertex origin) {
		Origin = origin;
	}
	public HalfEdge getTwin() {
		return Twin;
	}
	public void setTwin(HalfEdge twin) {
		Twin = twin;
	}
	public Face getIncidentFace() {
		return IncidentFace;
	}
	public void setIncidentFace(Face incidentFace) {
		IncidentFace = incidentFace;
	}
	public HalfEdge getNext() {
		return Next;
	}
	public void setNext(HalfEdge next) {
		Next = next;
	}
	public HalfEdge getPrevious() {
		return Previous;
	}
	public void setPrevious(HalfEdge previous) {
		Previous = previous;
	}
	public Vertex getHelper() {
		return Helper;
	}
	public void setHelper(Vertex helper) {
		Helper = helper;
	}
    
}

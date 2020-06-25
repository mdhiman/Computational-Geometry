package polygon.datastructure;

public class StatusNode 
{
	public HalfEdge Edge;
    public StatusNode Left,Right;

    public StatusNode(HalfEdge edge)
    {
        Edge = edge;
        Left = Right = null;
    }
}

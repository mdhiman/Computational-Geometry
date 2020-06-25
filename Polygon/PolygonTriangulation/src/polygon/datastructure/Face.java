package polygon.datastructure;

public class Face 
{
    public int FaceId;
    public HalfEdge OuterFaceComponent;
    public HalfEdge InnerFaceComponent;
    public boolean IsMarked;
	public boolean isIsMarked() {
		return IsMarked;
	}
	public void setIsMarked(boolean isMarked) {
		IsMarked = isMarked;
	}
	public int getFaceId() {
		return FaceId;
	}
	public void setFaceId(int faceId) {
		FaceId = faceId;
	}
	public HalfEdge getOuterFaceComponent() {
		return OuterFaceComponent;
	}
	public void setOuterFaceComponent(HalfEdge outerFaceComponent) {
		OuterFaceComponent = outerFaceComponent;
	}
	public HalfEdge getInnerFaceComponent() {
		return InnerFaceComponent;
	}
	public void setInnerFaceComponent(HalfEdge innerFaceComponent) {
		InnerFaceComponent = innerFaceComponent;
	}
    
}

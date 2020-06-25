package polygon.dcel;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import polygon.datastructure.Face;
import polygon.datastructure.HalfEdge;
import polygon.datastructure.Point;
import polygon.datastructure.Vertex;
import polygon.datastructure.VertexType;

public class DCEL
{
	public ArrayList<Vertex> vertexList;
    public ArrayList<HalfEdge> halfedgesList;
    public ArrayList<Face> facesList;
    public boolean isYmonotone=true;
    
    public DCEL(ArrayList<Point> lstPoints)
    {
        vertexList = new ArrayList<Vertex>();
        halfedgesList = new ArrayList<HalfEdge>();
        facesList = new ArrayList<Face>();
        AddVertices(lstPoints);
        AddInitialEdgesInDCEL();
        SetNextPrevious();
        SetVertexType();
    }

	public void SetVertexType() 
	{
		
        for (Vertex vertex:vertexList )
        {
            double findangle = FindInternalAngle(vertex.Coordinate,vertex.IncidentEdge.Next.Origin.Coordinate,vertex.IncidentEdge.Previous.Origin.Coordinate);
			if (CheckIsBelow (vertex.IncidentEdge.Next.Origin, vertex) && CheckIsBelow (vertex.IncidentEdge.Previous.Origin, vertex) && findangle < 180.0) 
			{
				vertex.Type = VertexType.start;
			}
			else if (CheckIsBelow (vertex.IncidentEdge.Next.Origin, vertex) && CheckIsBelow (vertex.IncidentEdge.Previous.Origin, vertex) && findangle > 180.0) 
			{
				vertex.Type = VertexType.split;
				isYmonotone = false;
			} 
			else if (CheckIsAbove (vertex.IncidentEdge.Next.Origin, vertex) && CheckIsAbove (vertex.IncidentEdge.Previous.Origin, vertex) && findangle < 180.0) 
			{
				vertex.Type = VertexType.end;
			}
			else if (CheckIsAbove (vertex.IncidentEdge.Next.Origin, vertex) && CheckIsAbove (vertex.IncidentEdge.Previous.Origin, vertex) && findangle > 180.0) 
			{
				vertex.Type = VertexType.merge;
				isYmonotone = false;
			}
        }
		
	}

	public void SetNextPrevious() 
	{
		 int vertexLength = vertexList.size();
         for (int i = 0; i < vertexLength; i++)
         {
             vertexList.get(i).IncidentEdge.Next = vertexList.get((i + 1) % vertexLength).IncidentEdge;
             vertexList.get(i).IncidentEdge.Twin.Previous = vertexList.get((i + 1) % vertexLength).IncidentEdge.Twin;
             vertexList.get(i).IncidentEdge.Previous = vertexList.get((i +vertexLength- 1) % vertexLength).IncidentEdge;
             vertexList.get(i).IncidentEdge.Twin.Next = vertexList.get((i +vertexLength- 1) % vertexLength).IncidentEdge.Twin;
         }
		
	}

	public void AddInitialEdgesInDCEL() 
	{
		// TODO Auto-generated method stub
		Face first=new Face();
		Face second=new Face();
		first.FaceId=0;
		second.FaceId=1;
		first.InnerFaceComponent=null;
		second.InnerFaceComponent=null;
		first.OuterFaceComponent=null;
		second.OuterFaceComponent=null;
		facesList.add(first);
		facesList.add(second);
		int vertexListLength=vertexList.size();
		//System.out.println(vertexListLength);
		HalfEdge normalEdge,twinEdge;
		for(int i=0;i<vertexListLength;i++)
		{
			String normaledge_id="e"+vertexList.get(i).VertexId+","+vertexList.get((i+1)%vertexListLength).VertexId;
			String twinedge_id="e"+vertexList.get((i+1)%vertexListLength).VertexId+","+vertexList.get(i).VertexId;
			normalEdge=new HalfEdge();
			twinEdge=new HalfEdge();
			 normalEdge.EdgeId = normaledge_id;
             normalEdge.IncidentFace = second;
             normalEdge.Origin =vertexList.get(i);
             normalEdge.Twin = twinEdge;
            
             twinEdge.EdgeId = twinedge_id;
             twinEdge.IncidentFace = first;
             twinEdge.Origin = vertexList.get((i + 1) % vertexListLength);
             twinEdge.Twin = normalEdge;
            // System.out.println(normalEdge.EdgeId);
             halfedgesList.add(normalEdge);
             halfedgesList.add(twinEdge);

             vertexList.get(i).IncidentEdge=normalEdge;
             if (i == 0)
             {
                 facesList.get(0).InnerFaceComponent=twinEdge;
                 facesList.get(1).OuterFaceComponent=normalEdge;
                // System.out.println(facesList.get(0).FaceId+"nnnn"+facesList.get(1));
                 
             }
			
		}
		
	}

	public void AddVertices(ArrayList<Point> lstPoints) 
	{
		// TODO Auto-generated method stub
		int count=0;
		for (Point point : lstPoints) 
		{
			Vertex vertex=new  Vertex();
			vertex.Coordinate=point;
			vertex.VertexId=++count;
			vertex.Type=VertexType.regular;
			vertexList.add(vertex);
			
		}
		
	}
	
		
		 public void DisplayDCEL()
	        {
	           System.out.println("vertex set...");
	            for(Vertex vertex:vertexList)
	            {
	               System.out.println("id "+vertex.VertexId+"x_axis"+ vertex.Coordinate.getX_axis()+"y_axis"+ vertex.Coordinate.getY_axis()+"incident edge"+ vertex.IncidentEdge.EdgeId+"vertex type"+vertex.Type);
	            }
	            System.out.println("-----------------");
	            System.out.println(halfedgesList.size()+"size of edge list");
	            System.out.println("Half Edges:-");
	            for (HalfEdge e :halfedgesList)
	            {
	                System.out.println("Id"+e.EdgeId+"next"+e.Next.EdgeId+"previous"+e.Previous.EdgeId+"twin"+e.Twin.EdgeId);
	            }
	            System.out.println("-----------------");
	            System.out.println("FaceList....size is.."+facesList.size());
	            for (Face face:facesList)
	            {
	            	
	               System.out.println( "FaceId:-"+face.FaceId+"outer component:-");
	            }
	            System.out.println("################################");
	        }
		 
		 public void AddNewEdge(Vertex v1, Vertex v2, FileWriter file) throws IOException
	        {
	            HalfEdge halfedge1 = new HalfEdge();
	            HalfEdge halfedge2 = new HalfEdge();
	            String ename = "e" + v1.VertexId + ","+v2.VertexId;
	            halfedge1.EdgeId = ename;
	            ename = "e" + v2.VertexId +","+ v1.VertexId;
	            halfedge2.EdgeId = ename;
	            halfedge1.Twin = halfedge2;
	            halfedge2.Twin = halfedge1;
	            halfedge1.Origin = v1;
	            halfedge2.Origin = v2;
	            halfedgesList.add(halfedge1);
	            halfedgesList.add(halfedge2);
	            Face face = new Face();
	            face.FaceId = facesList.size();

	            HalfEdge tempInt1, tempInt2;
	            tempInt2 = v2.IncidentEdge;
	            tempInt1 = v2.IncidentEdge.Next;
	            while (!tempInt1.Origin.equals(v1))
	            {
	                if (tempInt1.Next.Origin.equals(v2))
	                {
	                    tempInt2 = tempInt1.Twin;
	                    tempInt1 = tempInt2.Next;
	                }
	                else
	                {
	                    tempInt1 = tempInt1.Next;
	                }
	            }

	            halfedge1.Next = tempInt2;
	            halfedge2.Next = tempInt1;
	            halfedge2.Previous = tempInt2.Previous;
	            halfedge1.Previous = tempInt1.Previous;

	            tempInt2.Previous.Next = halfedge2;
	            tempInt1.Previous.Next = halfedge1;

	            tempInt2.Previous = halfedge1;
	            tempInt1.Previous = halfedge2;

	            HalfEdge tmpTargetEdge;
	            if (v1.Coordinate.getX_axis() > v2.Coordinate.getX_axis())
	            {
	                halfedge2.IncidentFace = halfedge2.Next.IncidentFace;
	                halfedge1.IncidentFace = face;
	                face.OuterFaceComponent = halfedge1;
	                halfedge2.IncidentFace.OuterFaceComponent = halfedge2;
	                tmpTargetEdge = halfedge1;
	            }
	            else
	            {
	                halfedge1.IncidentFace = halfedge1.Next.IncidentFace;
	                halfedge2.IncidentFace = face;
	                face.OuterFaceComponent = halfedge2;
	                halfedge1.IncidentFace.OuterFaceComponent = halfedge1;
	                tmpTargetEdge = halfedge2;
	            }
	            facesList.add(face);
	            HalfEdge tmp = tmpTargetEdge.Next;
	            while (!tmp.equals(tmpTargetEdge))
	            {
	                tmp.IncidentFace = face;
	                tmp = tmp.Next;
	            }
	            String str = "<line x1=\"" + v1.Coordinate.getX_axis() + "\" y1=\"" + v1.Coordinate.getY_axis()
	                        + "\" x2=\"" + v2.Coordinate.getX_axis() + "\" y2=\"" + v2.Coordinate.getY_axis()
	                        + "\" stroke=\"grey\" stroke-width=\"2\" />";
	            file.write(str);
	        }
		 public boolean CheckIsBelow(Vertex v1,Vertex v2)
	     {
				
				if (v1.Coordinate.getY_axis() < v2.Coordinate.getY_axis())
					return true;
				else if (v1.Coordinate.getY_axis() == v2.Coordinate.getY_axis()) 
				{
					if (v1.Coordinate.getX_axis() > v2.Coordinate.getX_axis())
						return true;
				}
				return false;
	     }
		 
		 public boolean CheckIsAbove(Vertex v1,Vertex v2)
	     {
				
				if (v1.Coordinate.getY_axis() > v2.Coordinate.getY_axis())
					return true;
				else if (v1.Coordinate.getY_axis() == v2.Coordinate.getY_axis()) 
				{
					if (v1.Coordinate.getX_axis() < v2.Coordinate.getX_axis())
						return true;
				}
				return false;
	     }
		 public double FindInternalAngle(Point p, Point q, Point r)
	     {
			 	int y1 = p.getY_axis() - q.getY_axis();
				int y2 = r.getY_axis() - p.getY_axis();
				int x1 = p.getX_axis() - q.getX_axis();
				int x2 = r.getX_axis() - p.getX_axis();
	         double result = Math.PI + Math.atan2(x1 * y2 - x2 * y1, x1 * x2 + y1 * y2);
	         result = result * 180 /Math.PI;
	         return result;
	     }

			public int orientation(Point p, Point q, Point r)
			{
				int value = (q.getY_axis() - p.getY_axis()) * (r.getX_axis() - q.getX_axis()) - (q.getX_axis() - p.getX_axis()) * (r.getY_axis() - q.getY_axis());

				if (value == 0) return 0;  // colinear

				return (value > 0)? 1: -1; // clock or counterclock wise
			}
		 
	 

}

package polygon.dcel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Stack;

import polygon.datastructure.Face;
import polygon.datastructure.HalfEdge;
import polygon.datastructure.Vertex;
import polygon.datastructure.VertexType;
import polygon.bst.BinarySearchTree;


public class Triangulate {

	        DCEL dcel;
	        FileWriter file;
	        BinarySearchTree sweepLineTree;
	        int redcount, bluecount, greencount;

	        public Triangulate(DCEL dcel, FileWriter file) throws IOException
	        {
	            this.dcel = dcel;
	            this.file = file;
	            sweepLineTree = new BinarySearchTree();
	            if (!dcel.isYmonotone)
	            {
	            	
	                BuildingYmonotone();
	            }
	            TriangulatePolygon();
	            InstallCamera installcam=new InstallCamera();
	            installcam.ColourNodes(dcel, file);
	           
	        }

	        public void BuildingYmonotone() throws IOException
	        {
	            ArrayList<Vertex> lstSorted = new ArrayList<Vertex>();
	            		lstSorted=dcel.vertexList;
	            Collections.sort(lstSorted,new xAxisAscendingOrder());
	            Collections.sort(lstSorted,new yAxisDescendingOrder());

	            for (Vertex v : lstSorted)
	            {
	                if (v.getType().equals(VertexType.start))
	                    StartVertexHandler(v);
	                else if (v.getType().equals(VertexType.merge))
	                    MergeVertexHandler(v);
	                else if (v.getType().equals(VertexType.split))
	                    HandleSplitVertex(v);
	                else if (v.getType().equals(VertexType.end))
	                    EndVertexHandler(v);
	                else
	                    RegularVertexHandler(v);

	            }

	        }

	        
	        private void HandleSplitVertex(Vertex v) throws IOException
	        {
	            HalfEdge he = sweepLineTree.GetDirectLeft(v);
	            System.out.println("direct left edge id is ...."+he.EdgeId);
	            dcel.AddNewEdge(v, he.Helper, file);
	            he.Helper = v;
	            he.Twin.Helper = v;
	            HalfEdge hInsert = GetReqEdge(v);
	            sweepLineTree.addIntoBST(hInsert);
	            hInsert.Helper = v;
	            hInsert.Twin.Helper = v;
	        }

	        private void EndVertexHandler(Vertex v) throws IOException
	        {
	            HalfEdge he = GetReqEdge(v.IncidentEdge.Previous.Origin);
	            if (he.Helper.Type.equals(VertexType.merge))
	            {
	                dcel.AddNewEdge(v, he.Helper, file);
	            }
	            sweepLineTree.DeleteNode(he);
	        }

	        
	        private void RegularVertexHandler(Vertex v) throws IOException
	        {
	            if (dcel.CheckIsAbove(v, v.IncidentEdge.Next.Origin))
	            {
	                HalfEdge he = GetReqEdge(v.IncidentEdge.Previous.Origin);
	                if (he.Helper.Type.equals(VertexType.merge))
	                {
	                    dcel.AddNewEdge(v, he.Helper, file);
	                }
	                sweepLineTree.DeleteNode(he);
	                HalfEdge hInsert = GetReqEdge(v);
	                sweepLineTree.addIntoBST(hInsert);
	                hInsert.Helper = v;
	                hInsert.Twin.Helper = v;
	            }
	            
	            else
	            {
	                HalfEdge hLeft = sweepLineTree.GetDirectLeft(v);
	                if (hLeft.Helper.Type.equals(VertexType.merge))
	                {
	                    dcel.AddNewEdge(v, hLeft.Helper, file);
	                }
	                hLeft.Helper = v;
	                hLeft.Twin.Helper = v;
	            }
	        }
	        private void MergeVertexHandler(Vertex vertex) throws IOException
	        {
	            HalfEdge heReq = GetReqEdge(vertex.IncidentEdge.Previous.Origin);

	            if (heReq.Helper.Type.equals(VertexType.merge))
	            {
	                dcel.AddNewEdge(vertex, heReq.Helper, file);
	            }
	            sweepLineTree.DeleteNode(heReq);
	            HalfEdge he = sweepLineTree.GetDirectLeft(vertex);
	            if (he.Helper.Type.equals(VertexType.merge))
	            {
	                dcel.AddNewEdge(vertex, he.Helper, file);
	            }
	            he.Helper = vertex;
	            he.Twin.Helper = vertex;
	        }


	        private void StartVertexHandler(Vertex vertex)
	        {
	            HalfEdge halfInsert = GetReqEdge(vertex);
	            sweepLineTree.addIntoBST(halfInsert);
	            halfInsert.Helper = vertex;
	            halfInsert.Twin.Helper = vertex;
	        }

	        public HalfEdge GetReqEdge(Vertex vertex)
	        {
	            HalfEdge halfedge;
	            halfedge = vertex.IncidentEdge;
	            if (!dcel.CheckIsAbove(halfedge.Origin, halfedge.Twin.Origin))
	            {
	                halfedge = halfedge.Twin;
	            }
	            return halfedge;
	        }


	        public void TriangulatePolygon() throws IOException
	        {
	            int value = dcel.facesList.size();
	            System.out.println(value);
	            for (int i = 1; i <value; i++)
	            {
	            	//System.out.println("faceid"+dcel.facesList.get(i).FaceId+"outercomponent.."+dcel.facesList.get(i).OuterFaceComponent.EdgeId);
	                TriangulateFace(dcel.facesList.get(i));
	                System.out.println("after.....");
	            }
	        }

	        private void TriangulateFace(Face face) throws IOException
	        {
	            Vertex upper, bottom;
	            HalfEdge hedge = face.OuterFaceComponent;
	            upper = hedge.Origin;
	            bottom = hedge.Origin;
	            HalfEdge temp = hedge.Next;
	            while (!temp.EdgeId.equals(hedge.EdgeId))
	            {
	                if (dcel.CheckIsAbove(temp.Origin, upper))
	                    upper = temp.Origin;
	                if (dcel.CheckIsBelow(temp.Origin, bottom))
	                    bottom = temp.Origin;
	                temp = temp.Next;
	            }

	            ArrayList<Vertex> lstVertex = new ArrayList<Vertex>();
	            upper.IsLeft = true;
	            lstVertex.add(upper);
	            hedge = upper.IncidentEdge;
	            temp = upper.IncidentEdge.Next;
	            while ((temp.Origin.VertexId!=bottom.VertexId))
	            {
	                temp.Origin.IsLeft = true;
	                lstVertex.add(temp.Origin);
	                temp = temp.Next;
	            }
	            bottom.IsLeft = true;
	            lstVertex.add(bottom);
	            temp = temp.Next;
	            while ((temp.Origin.VertexId!=upper.VertexId))
	            {
	                temp.Origin.IsLeft = false;
	                lstVertex.add(temp.Origin);
	                temp = temp.Next;
	            }
	            if (lstVertex.size() <= 3)
	                return;
	            ArrayList<Vertex>lstSorted=new ArrayList<Vertex>();
	            lstSorted=lstVertex;
	            Collections.sort(lstSorted,new xAxisAscendingOrder());
	            Collections.sort(lstSorted,new yAxisDescendingOrder());
	            for (Vertex vertex : lstSorted) 
	            {
	            	System.out.println(vertex.Coordinate.getX_axis()+"..."+vertex.Coordinate.getY_axis());
				}
	            Stack<Vertex> st = new Stack<Vertex>();
	            st.push(lstSorted.get(0));
	            st.push(lstSorted.get(1));
	         for (int i = 2; i < lstSorted.size() - 1; i++)
	            {
	                Vertex top = st.peek();
	                if (lstSorted.get(i).IsLeft!=(top.IsLeft))
	                {
	                    while (st.size() > 1)
	                    {
	                        Vertex v = st.pop();

	                        if (!v.equals(upper))
	                        {
	                            dcel.AddNewEdge(lstSorted.get(i), v, file);
	                        }
	                    }
	                    st.pop();
	                    st.push(lstSorted.get(i-1));
	                    st.push(lstSorted.get(i));
	                }
	                else
	                {
	                    Vertex vTemp1, vTop;
	                    vTemp1 = st.pop();
	                    while (st.size() > 0)
	                    {
	                        vTop = st.peek();
	                        if (IsDiagonalBelongtoInside(lstSorted.get(i), vTemp1, vTop))
	                        {
	                            vTemp1 = st.pop();
	                            dcel.AddNewEdge(lstSorted.get(i), vTemp1, file);
	                        }
	                        else
	                            break;

	                    }
	                    st.push(vTemp1);
	                    st.push(lstSorted.get(i));
	                }
	            }
	            if (st.size() > 2)
	            {
	                st.pop();
	                while (st.size() > 1)
	                {
	                    Vertex v = st.pop();
	                    dcel.AddNewEdge(lstSorted.get(lstSorted.size()-1), v, file);
	                }
	            }
	        }

	       
	        private boolean IsDiagonalBelongtoInside(Vertex p, Vertex q, Vertex r)
	        {
	            int orien = dcel.orientation(p.getCoordinate(), q.getCoordinate(), r.getCoordinate());
	            if (p.isIsLeft())
	            {
	                if (orien >= 0)
	                    return true;
	                else
	                    return false;
	            }
	            else
	            {
	                if (orien <= 0)
	                    return true;
	                else
	                    return false;
	            }
	        }

	    
}
class xAxisAscendingOrder implements Comparator<Vertex> {
    public int compare(Vertex chair1, Vertex chair2) {
        return chair1.getCoordinate().getX_axis()- chair2.getCoordinate().getX_axis();
    }
}
class yAxisDescendingOrder implements Comparator<Vertex> {
    public int compare(Vertex chair1, Vertex chair2) {
        return   chair2.getCoordinate().getY_axis()-chair1.getCoordinate().getY_axis();
    }
}

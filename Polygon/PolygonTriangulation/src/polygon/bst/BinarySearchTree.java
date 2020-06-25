package polygon.bst;

import polygon.datastructure.HalfEdge;
import polygon.datastructure.Point;
import polygon.datastructure.StatusNode;
import polygon.datastructure.Vertex;

public class BinarySearchTree 
{
	 StatusNode rootNode;
     HalfEdge directLeftEdge;
     boolean flag;
		public BinarySearchTree ()
		{
         rootNode = null;
         //nodeList = new List<StatusNode>();
		}

     public StatusNode GetRoot()
     {
         return rootNode;
     }
     public boolean addIntoBST(HalfEdge edge)
     {
    	 StatusNode node = new StatusNode(edge);
         try
         {
             if (rootNode == null)
                 rootNode = node;
             else
            	 rootNode=addNode(node,rootNode);
             return true;
         }
         catch (Exception e)
         {
             return false;
         }
    	
     }
     private StatusNode addNode(StatusNode node,  StatusNode tree)
     {
         if (tree == null)
             tree = node;
         else
         {
             int comparison = FindOrientation(tree.Edge,node.Edge);

             if (comparison >= 0)
             {
                tree.Left= addNode(node,  tree.Left);
             }
             else
             {
                tree.Right= addNode(node,  tree.Right);
             }
         }
		return tree;
     }
     
     public void InorderTraversal(StatusNode node)
     {
         if (node != null)
         {
             InorderTraversal(node.Left);
           System.out.println(node.Edge.EdgeId);
             InorderTraversal(node.Right);
         }
     }
     public boolean DeleteNode(HalfEdge he)
     {
         StatusNode current = rootNode;
         StatusNode parent = rootNode;
         boolean isLeftChild = true;
         while (!current.Edge.EdgeId.equals(he.EdgeId))
         {
             parent = current;
             int comparison = FindOrientation(current.Edge, he);
             if (comparison>=0)
             {
                 isLeftChild = true;
                 current = current.Left;//
             }
             else
             {
                 isLeftChild = false;
                 current = current.Right;
             }
             if (current == null)
             {
                 return false;
             }
         }

         if (current.Left == null && current.Right == null)
         {
             if (current == rootNode)
             {
                 rootNode = null;
             }
             else if (isLeftChild)
             {
                 parent.Left = null;
             }
             else
             {
                 parent.Right = null;
             }
         }
         else if (current.Right == null)
         {
             if (current == rootNode)
             {
                 rootNode = current.Left;
             }
             else if (isLeftChild)
             {
                 parent.Left = current.Left;
             }
             else
             {
                 parent.Right = current.Right;
             }
         }
         else if (current.Left == null)
         {
             if (current == rootNode)
             {
                 rootNode = current.Right;
             }
             else if (isLeftChild)
             {
                 parent.Left = parent.Right;
             }
             else
             {
                 parent.Right = current.Right;
             }
         }
         else
         {
             StatusNode successor = GetSuccessor(current);
             if (current == rootNode)
             {
                 rootNode = successor;
             }
             else if (isLeftChild)
             {
                 parent.Left = successor;
             }
             else
             {
                 parent.Right = successor;
             }
             successor.Left = current.Left;
         }
         return true;
     }

     public StatusNode GetSuccessor(StatusNode delNode)
     {
         StatusNode successorParent = delNode;
         StatusNode successor = delNode;
         StatusNode current = delNode.Right;

         while (current != null)
         {
             successorParent = current;
             successor = current;
             current = current.Left;
         }
         if (successor != delNode.Right)
         {
             successorParent.Left = successor.Right;
             successor.Right = delNode.Right;
         }
         return successor;
     }

     public void FindDirectLeft(StatusNode node, Vertex v)
     {
         if (node != null)
         {
             FindDirectLeft(node.Right,v);
             if (flag)
             {
                 if (DoIntersect(node, v) && (!v.Coordinate.equals(node.Edge.Origin.Coordinate))
                     && (!v.Coordinate.equals(node.Edge.Twin.Origin.Coordinate)))
                 {
                     directLeftEdge = node.Edge;
                     flag = false;
                     return;
                 }
             }
             else
                 return;
             FindDirectLeft(node.Left,v);
         }
     }

     public HalfEdge GetDirectLeft(Vertex v)
     {
         flag = true;
         directLeftEdge = null;
         FindDirectLeft(rootNode, v);
         return directLeftEdge;
     }

     
     

     
     
     public int FindOrientation(HalfEdge h1,HalfEdge h2)
     {
         int o1 = orientation(h1.Origin.Coordinate, h1.Twin.Origin.Coordinate, h2.Origin.Coordinate);
         int o2 = orientation(h1.Origin.Coordinate, h1.Twin.Origin.Coordinate, h2.Twin.Origin.Coordinate);
         if(o1!=0)
        	 return o1;
         else 
        	 return o2;
     }


	
	 private boolean DoIntersect(StatusNode node, Vertex v)
     {
         Point p1 = new Point(node.Edge.Origin.Coordinate.getX_axis(), node.Edge.Origin.Coordinate.getY_axis());
         Point q1 = new Point(node.Edge.Twin.Origin.Coordinate.getX_axis(), node.Edge.Twin.Origin.Coordinate.getY_axis());
         Point p2 = new Point(v.Coordinate.getX_axis(),v.Coordinate.getY_axis());
         Point q2 = new Point(-1,v.Coordinate.getY_axis());

         // Find the four orientations needed for general and
         // special cases
         int o1 = orientation(p1, q1, p2);
         int o2 = orientation(p1, q1, q2);
         int o3 = orientation(p2, q2, p1);
         int o4 = orientation(p2, q2, q1);

         // General case
         if (o1 != o2 && o3 != o4)
             return true;

         // Special Cases
         // p1, q1 and p2 are colinear and p2 lies on segment p1q1
         if (o1 == 0 && onSegment(p1, p2, q1)) return true;

         // p1, q1 and p2 are colinear and q2 lies on segment p1q1
         if (o2 == 0 && onSegment(p1, q2, q1)) return true;

         // p2, q2 and p1 are colinear and p1 lies on segment p2q2
         if (o3 == 0 && onSegment(p2, p1, q2)) return true;

         // p2, q2 and q1 are colinear and q1 lies on segment p2q2
         if (o4 == 0 && onSegment(p2, q1, q2)) return true;

         return false; // Doesn't fall in any of the above cases
     }
	 boolean onSegment(Point p, Point q, Point r)
     {
         if (q.getX_axis() <= Math.max(p.getX_axis(), r.getX_axis()) && q.getX_axis() >= Math.min(p.getX_axis(), r.getX_axis()) &&
             q.getY_axis() <= Math.max(p.getY_axis(), r.getY_axis()) && q.getY_axis() >= Math.min(p.getY_axis(), r.getY_axis()))
             return true;

         return false;
     }
	 public int orientation(Point p, Point q, Point r)
		{
			int value = (q.getY_axis() - p.getY_axis()) * (r.getX_axis() - q.getX_axis()) - (q.getX_axis() - p.getX_axis()) * (r.getY_axis() - q.getY_axis());

			if (value == 0) return 0;  // colinear

			return (value > 0)? 1: -1; // clock or counterclock wise
		}

}

package polygon.dcel;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Stack;

import polygon.datastructure.Face;
import polygon.datastructure.HalfEdge;
import polygon.datastructure.Vertex;

public class InstallCamera
{
	int blackcount, bluecount, greencount;
	public void ColourNodes(DCEL dcel,FileWriter file) throws IOException
    {
        blackcount=0;
        bluecount=0;
        greencount=0;
        Stack<Face> stack = new Stack<Face>();
        dcel.facesList.get(1).IsMarked = true;
        stack.push(dcel.facesList.get(1));
        HalfEdge hedge;
        Face face,tempFace;
        while (stack.size() > 0)
        {
            face = stack.pop();
            hedge = face.OuterFaceComponent;
            System.out.println("checking colour"+hedge.EdgeId);
            for (int i = 0; i < 3; i++)
            {
                tempFace = hedge.Twin.IncidentFace;
                if (tempFace.FaceId > 0 && !tempFace.IsMarked)
                {
                    tempFace.IsMarked = true;
                    stack.push(tempFace);
                }
                if(hedge.Origin.getColour()==null)
                    AssignColor(hedge.Origin,file);
                hedge = hedge.Next;
            }
        }
        
        int minCamera = blackcount;
        String colour = "Black";
        if (bluecount < minCamera)
        {
            minCamera = bluecount;
        }
        if (greencount < minCamera)
        {
            minCamera = greencount;
        }
        if(minCamera==bluecount)
        	colour="Blue";
        else if(minCamera==greencount)
        	colour="Green";

        
        System.out.println("Installing camera......");
        System.out.println("Place camera on " + colour + " Vertexs.");
        String string = "<a><text x=\"10\" y=\"25\">Place camera on " + colour + " Vertexs.</text></a>\n";
        file.write(string);
    }

    private void AssignColor(Vertex vertex,FileWriter file) throws IOException
    {
        String colouring,color;
        HalfEdge hedge = vertex.getIncidentEdge();
        HalfEdge tmpHedge = hedge.Previous;
        boolean IsBlack;
        boolean IsBlue;
        boolean IsGreen;
        IsBlack = false;
        IsBlue = false;
        IsGreen = false;

        while (!tmpHedge.EdgeId.equals(hedge.Twin.EdgeId))
        {
            colouring = tmpHedge.Origin.getColour();
            System.out.println(colouring);
            
            try
            {
            if (colouring.equals("black"))
                IsBlack= true;
            else if (colouring.equals("blue"))
                IsBlue = true;
            else if (colouring.equals("green"))
                IsGreen = true;
            }
            catch(NullPointerException e)
            {
            	
            }

            tmpHedge = tmpHedge.Twin.Previous;
        }
        colouring = tmpHedge.Origin.getColour();
        try
        {
        if (colouring.equals("black"))
            IsBlack = true;
        else if (colouring.equals("blue"))
            IsBlue = true;
        else if (colouring.equals("green"))
            IsGreen = true;
        }
        catch(NullPointerException e)
        {
        	
        }
        
         if (!IsBlue)
        {
        	 vertex.setColour("blue");
            bluecount++;
        }
         else if (!IsGreen)
            {
                vertex.setColour("green");
                greencount=greencount+1;
            }
         else if (!IsBlack)
        {
        	 vertex.setColour("black");
            blackcount=blackcount+1;
        }
        
       
        color = vertex.getColour();

        String string = "<circle cx=\"" + vertex.getCoordinate().getX_axis()
                                    + "\" cy=\"" + vertex.getCoordinate().getY_axis()
                                    + "\" r=\"2\" stroke=\"" + color + "\" stroke-width=\"4\" fill=\"" + color + "\" />\n";
        file.write(string);
    }

}

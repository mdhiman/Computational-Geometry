package polygon.dcel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import polygon.datastructure.Point;
import polygon.datastructure.StatusNode;
import polygon.datastructure.Vertex;

public class MainFlow {

	/**
	 * @param args
	 */
	public static FileWriter outputfile,outputSimplePolygon;
	public static void main(String[] args) throws IOException 
	{
		// TODO Auto-generated method stub
		 ArrayList<Point> lstPoint = ReadPoints();
		 outputSimplePolygon=  WriteIntoSvg(lstPoint,outputSimplePolygon,"Simplepolygon.svg");
         boolean IsSimple=checkIsSimple(lstPoint);
         if(!IsSimple)
         {
        	 System.out.println("Polygon is not simple..no triangulating.. exiting.....");
        	 String str = "<a><text x=\"10\" y=\"25\">Polygon is not simple..no triangulating.. exiting.....</text></a>\n";
        	 outputSimplePolygon.write(str);
        	 ClosingSvg(outputSimplePolygon);
        	 Runtime.getRuntime().exec("firefox Simplepolygon.svg");
         }
        if(IsSimple)
         {
        	String str = "<a><text x=\"10\" y=\"25\">Polygon is  simple.. triangulating...check output in polygon.svg</text></a>\n";
       	 outputSimplePolygon.write(str);
       	 ClosingSvg(outputSimplePolygon);
       	outputfile= WriteIntoSvg(lstPoint,outputfile,"polygon.svg");
         DCEL dcel = new DCEL(lstPoint);
         //dcel.DisplayDCEL();
         Triangulate objTriangle = new Triangulate(dcel, outputfile);
        
			ClosingSvg (outputfile);
			
			Runtime.getRuntime().exec("firefox polygon.svg");
			
			//Runtime.getRuntime().exec("firefox Simplepolygon.svg");
			
         }

	}
	public static ArrayList<Point> ReadPoints() throws IOException
	{
		ArrayList<Point> pointList=new ArrayList<Point>();
		java.io.File file=new java.io.File("Points3.txt");
		BufferedReader bfr=new BufferedReader(new FileReader(file));
		String line;
		while((line=bfr.readLine())!=null)
		{
			String points[]=line.split(" ");
			try
			{
			Point p=new Point(Integer.parseInt(points[0]),Integer.parseInt(points[1]));
			pointList.add(p);
			}
			catch(NumberFormatException e)
			{
				Point p=new Point(Integer.parseInt(points[0].substring(1, 4)),Integer.parseInt(points[1]));
				pointList.add(p);
			}
		}
		bfr.close();
		return pointList;
		
	}
	
	public static FileWriter WriteIntoSvg(ArrayList<Point> lstPoint,FileWriter outputfile,String filename) throws IOException
    {
        outputfile = new FileWriter(new File(filename));
        //file.WriteLine(lines);
        String str;
        outputfile.write("<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" >\n");
        for(int i = 0;i<lstPoint.size()-1;i++)
        {
             str = "<line x1=\""+ lstPoint.get(i).getX_axis() +"\" y1=\""+ lstPoint.get(i).getY_axis()
                    +"\" x2=\""+ lstPoint.get(i+1).getX_axis() +"\" y2=\""+ lstPoint.get(i+1).getY_axis() 
                    +"\" stroke=\"red\" stroke-width=\"2\" />\n";


             outputfile.write(str);
			str = "<circle cx=\""+lstPoint.get(i).getX_axis()
				+"\" cy=\""+lstPoint.get(i).getY_axis()+"\" r=\"1\" stroke=\"red\" stroke-width=\"4\" fill=\"red\" />\n";
			outputfile.write(str);
        }
        str = "<line x1=\"" + lstPoint.get(lstPoint.size()-1).getX_axis() + "\" y1=\"" + lstPoint.get(lstPoint.size()-1).getY_axis()
                   + "\" x2=\"" + lstPoint.get(0).getX_axis() + "\" y2=\"" + lstPoint.get(0).getY_axis()
                   + "\" stroke=\"red\" stroke-width=\"2\" />\n";
        outputfile.write(str);
		str = "<circle cx=\""+lstPoint.get(lstPoint.size()-1).getX_axis()
			+"\" cy=\""+lstPoint.get(lstPoint.size()-1).getY_axis()+"\" r=\"1\" stroke=\"red\" stroke-width=\"4\" fill=\"red\" />\n";
		outputfile.write(str);
		return outputfile;
        
    }

	public static void ClosingSvg(FileWriter outputfile) throws IOException
	{
		outputfile.write("</svg>");
		outputfile.close();
	}
	
	public static boolean checkIsSimple(ArrayList<Point> PointSet)
	{
		ArrayList<LineSegment> lineList=new ArrayList<LineSegment>();
		int length=PointSet.size();
		boolean flag=true;
		for(int i=0;i<PointSet.size();i++)
		{
			LineSegment l=new LineSegment(PointSet.get(i), PointSet.get((i+1)%length));
			lineList.add(l);
		}
		for(int i=0;i<lineList.size()-1;i++)
		{
			for(int j=i+1;j<lineList.size();j++)
			{
				if(DoIntersect(lineList.get(i), lineList.get(j)))
				{
					System.out.println("intersect...");
					System.out.println(lineList.get(i).p.getX_axis()+"..."+lineList.get(i).p.getY_axis());
					System.out.println(lineList.get(i).q.getX_axis()+"..."+lineList.get(i).q.getY_axis());
					System.out.println("and");
					System.out.println(lineList.get(j).p.getX_axis()+"..."+lineList.get(j).p.getY_axis());
					System.out.println(lineList.get(j).q.getX_axis()+"..."+lineList.get(j).q.getY_axis());
					flag=false;
					break;
				}
			}
			if(!flag)
				break;
		}
		
		return flag;
	}
	 private static boolean DoIntersect(LineSegment l1, LineSegment l2)
     {
         Point p1 = l1.p;
         Point q1 = l1.q;
         Point p2 = l2.p;
         Point q2 = l2.q;

         // Find the four orientations needed for general and
         // special cases
         int o1 = orientation(p1, q1, p2);
         int o2 = orientation(p1, q1, q2);
         int o3 = orientation(p2, q2, p1);
         int o4 = orientation(p2, q2, q1);
        // System.out.println(o1+"aa"+o2+"aa"+o3+"o4"+o4);
         // General case
         if (o1 != o2 && o3 != o4&& o1!=0 &&o2!=0&&o3!=0&&o4!=0)
         {
        	 System.out.println("intersecting...");
        	 return true;
         }
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
	 static boolean  onSegment(Point p, Point q, Point r)
     {
         if (q.getX_axis() < Math.max(p.getX_axis(), r.getX_axis()) && q.getX_axis() > Math.min(p.getX_axis(), r.getX_axis()) &&
             q.getY_axis() < Math.max(p.getY_axis(), r.getY_axis()) && q.getY_axis() > Math.min(p.getY_axis(), r.getY_axis()))
             return true;

         return false;
     }
	 public static int orientation(Point p, Point q, Point r)
		{
			int value = (q.getY_axis() - p.getY_axis()) * (r.getX_axis() - q.getX_axis()) - (q.getX_axis() - p.getX_axis()) * (r.getY_axis() - q.getY_axis());

			if (value == 0) return 0;  // colinear

			return (value > 0)? 1: -1; // clock or counterclock wise
		}

}

class LineSegment
{
	Point p;
	Point q;
	public LineSegment(Point a,Point b)
	{
	this.p=a;
	this.q=b;
	}
}

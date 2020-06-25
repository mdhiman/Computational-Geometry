#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#define PARTITIONSIZE 30
typedef struct
{
    int x;
    int y;
} Point;
typedef struct 
{
	/* data */
	Point p1;
	Point p2;
	int id;
} Line;
Point *finalResult;
static int finalResultSize;
FILE *fp;
Point *intersectionPoint;
int pointCount;
 void drawlineConvex(FILE *fp,Point p1 , Point p2)
{
    fprintf(fp,"<line x1=\"%d\" y1=\"%d\" x2=\"%d\" y2=\"%d\" stroke=\"blue\" stroke-width=\"1\" />\n",p1.x,p1.y,p2.x,p2.y);
}
 void drawlineIntermediate(FILE *fp,Point p1 , Point p2)
{
    fprintf(fp,"<line x1=\"%d\" y1=\"%d\" x2=\"%d\" y2=\"%d\" stroke=\"yellow\" stroke-width=\"1\" />\n",p1.x,p1.y,p2.x,p2.y);
}
void drawpointConvex(Point p,FILE* fp)
{
	fprintf(fp,"<circle cx=\"%d\" cy=\"%d\" r=\"1\" stroke=\"black\" stroke-width=\"2\" fill=\"black\" />\n",p.x,p.y);
}
int compare (const void * vp1, const void * vp2)
{
	Point *p1=(Point *)vp1;
	Point *p2=(Point *)vp2;
	return (p1->x-p2->x);
}
int orientation(Point p, Point q, Point r)
{
	
    int val = (q.y - p.y) * (r.x - q.x) -
              (q.x - p.x) * (r.y - q.y);
 
    if (val == 0) return 0;  // colinear
    return (val > 0)? 1: -1; // clock or counterclock wise
}

void jarvisMarch(Point *input1,int size,FILE *fp)
{
	if (size<3)
		return;
	int leftmost_index=0;
	//initialize result array with -1
	int result[size];
	for (int i = 0; i <size; ++i)
	{
		/* code */
		result[i]=-1;
	}
	//calculate index of leftmost point
	for (int i = 1; i < size; ++i)
	{
		/* code */
		if(input1[leftmost_index].x>input1[i].x)
			leftmost_index=i;

	}
	int temp_index=leftmost_index,temp1_index;
	do
	{
		temp1_index=(temp_index+1)%size;

		for (int i = 0; i < size; ++i)
			 if (orientation(input1[temp_index], input1[i], input1[temp1_index]) == -1)
             temp1_index = i;
 
       		 result[temp_index] = temp1_index;  
       		 temp_index = temp1_index; 
		
		
	}while(temp_index!=leftmost_index);

		for (int i = 0; i < size; ++i)
	{
		/* code */
		if(result[i]!=-1)
		drawlineConvex(fp,input1[i],input1[result[i]]);
	}
}
void grahamScan(Point *input1,int size,FILE *fp)
{
	Point *result,*stack;
	stack=(Point*)malloc(size*sizeof(Point));
	int top,resultIndex=0;
	qsort(input1,size,sizeof(Point),compare);
	stack[0]=input1[0];
	stack[1]=input1[1];
	top=1;
	//construction of upper hull
	for (int i = 2; i < size; i++)
	{
		while(top>=1)
		{
			if(orientation(stack[top-1],stack[top],input1[i])>=0)
				top--;
			else
				break;

		}
		stack[++top]=input1[i];

	}
	result=(Point*)malloc((top+1)*sizeof(Point));
	for (int i = 0; i <= top; ++i)
	{
		/* code */
		result[resultIndex++]=stack[i];
		

	}
	//construction of lower hull
	stack[0]=input1[size-1];
	stack[1]=input1[size-2];
	top=1;
	for (int i = size-3; i >= 0; i--)
	{
		while(top>=1)
		{
			if(orientation(stack[top-1],stack[top],input1[i])>=0)
				top--;
			else
				break;

		}
		stack[++top]=input1[i];
	}
	result=(Point*)realloc(result,(resultIndex+top)*sizeof(Point));

	for (int i = 1; i <= top; ++i)
	{
		/* code */
		result[resultIndex++]=stack[i];

	}
	free(stack);
	//cout<<"result size is..."<<resultIndex<<endl;
for (int i = 0; i < resultIndex-1; ++i)
{
	finalResultSize++;
	if(finalResultSize>1)
	finalResult=(Point*)realloc(finalResult,finalResultSize*sizeof(Point));
	else
		finalResult=(Point*)malloc(sizeof(Point));
	finalResult[finalResultSize-1]=result[i];
	drawlineIntermediate(fp,result[i],result[i+1]);
}

	return;
	
}
void chanConvexHull(Point *input1,int size,FILE *fp)
{
	int noOfPartition=ceil(size/(double)PARTITIONSIZE);
	Point *temp;
	finalResultSize=0;
	//cout<<noOfPartition<<endl;
	finalResult=(Point*)malloc(sizeof(Point));
	FILE *fp1 = fopen("convexhull_final.svg","w");
	fprintf(fp1,"<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" width=\"800\" height=\"600\" viewBox=\"20 20 958.69 592.78998\" >\n");
	for (int i = 0; i < noOfPartition-1; ++i)
	{
		/* code */
		temp=(Point*)calloc(PARTITIONSIZE,sizeof(Point));
		for (int j = 0; j < PARTITIONSIZE; ++j)
		{
			/* code */
			temp[j]=input1[i*PARTITIONSIZE+j];
		}
		grahamScan(temp,PARTITIONSIZE,fp);
	}
	int lastsize=size-(PARTITIONSIZE*(noOfPartition-1));
	//cout<<lastsize<<endl;
	temp=(Point*)calloc(lastsize,sizeof(Point));

	for (int i = 0; i < lastsize; ++i)
	{
		/* code */
		temp[i]=input1[((noOfPartition-1)*PARTITIONSIZE)+i];
	}
	grahamScan(temp,lastsize,fp);
	
for (int i = 0; i < size; ++i)
{
	/* code */
	drawpointConvex(input1[i],fp1);
}

	jarvisMarch(finalResult,finalResultSize,fp1);
	jarvisMarch(finalResult,finalResultSize,fp);
	fprintf(fp1,"</svg>\n");
   	 fclose(fp1);
}
void drawLine(FILE *fp,Line l)
{
    fprintf(fp,"<line x1=\"%d\" y1=\"%d\" x2=\"%d\" y2=\"%d\" stroke=\"red\" stroke-width=\"1\" />\n",l.p1.x,l.p1.y,l.p2.x,l.p2.y);
}
void drawIntersectionPoint(float x,float y)
{
	fprintf(fp,"<circle cx=\"%f\" cy=\"%f\" r=\"2\" stroke=\"green\" stroke-width=\"2\" fill=\"green\" />\n",x,y);
}
void drawPoint(Point p,FILE* fp)
{
	fprintf(fp,"<circle cx=\"%d\" cy=\"%d\" r=\"1\" stroke=\"black\" stroke-width=\"2\" fill=\"black\" />\n",p.x,p.y);
}
int max(int a,int b)
{
	return a>b?a:b;
}
int min(int a,int b)
{
	return a<b?a:b;	
}

// The main function that returns 1 if line segment 'p1q1'
// and 'p2q2' intersect.
int overlapSegment(Point p, Point q, Point r)
{
    if (q.x <= max(p.x, r.x) && q.x >= min(p.x, r.x) &&
        q.y <= max(p.y, r.y) && q.y >= min(p.y, r.y))
       return 1;
 
    return 0;
}
int FindIntersect(Point p1, Point q1, Point p2, Point q2)
{
    // Find the four orientations needed for general and
    // special cases
    int orien1 = orientation(p1, q1, p2);
    int orien2 = orientation(p1, q1, q2);
    int orien3 = orientation(p2, q2, p1);
    int orien4 = orientation(p2, q2, q1);
 
    // General case
    if (orien1 != orien2 && orien3 != orien4)
        return 1;
 
    // Special Cases
    // p1, q1 and p2 are colinear and p2 lies on segment p1q1
    if (orien1 == 0 && overlapSegment(p1, p2, q1)) return 1;
 
    // p1, q1 and p2 are colinear and q2 lies on segment p1q1
    if (orien2 == 0 && overlapSegment(p1, q2, q1)) return 1;
 
    // p2, q2 and p1 are colinear and p1 lies on segment p2q2
    if (orien3 == 0 && overlapSegment(p2, p1, q2)) return 1;
 
     // p2, q2 and q1 are colinear and q1 lies on segment p2q2
    if (orien4 == 0 && overlapSegment(p2, q1, q2)) return 1;
 
    return 0; // Doesn't fall in any of the above cases
}
void FindIntersectionPoint(Line l1,Line l2)
{
	float dx_l1=l1.p1.x-l1.p2.x;
	float dy_l1=l1.p1.y-l1.p2.y;
	float dx_l2=l2.p1.x-l2.p2.x;
	float dy_l2=l2.p1.y-l2.p2.y;
	float slope_line1=dy_l1/dx_l1;
	float slope_line2=dy_l2/dx_l2;
	float line1_constant=l1.p1.y- slope_line1*l1.p1.x;
	float line2_constant=l2.p1.y- slope_line2*l2.p1.x;
	float intersection_x=(line1_constant- line2_constant)/(slope_line2- slope_line1);
	float intersection_y=slope_line1*intersection_x+line1_constant;
	if(!pointCount)
		intersectionPoint=(Point*)malloc(sizeof(Point));
	else
		intersectionPoint=(Point*)realloc(intersectionPoint,(pointCount+1)*sizeof(Point));
	intersectionPoint[pointCount].x=intersection_x;
	intersectionPoint[pointCount].y=intersection_y;
	pointCount++;
	drawIntersectionPoint(intersection_x,intersection_y);
}
int main()
{
	int number;
	pointCount=0;
	Line *lines;
	fp = fopen("linesegment.svg","w");
	printf("enter the number of lines\n" )  ;
	scanf("%d",&number);
	lines=(Line*)malloc(number*sizeof(Line));
	// fprintf(fp1,"<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" width=\"800\" height=\"600\" viewBox=\"20 20 958.69 592.78998\" >\n");
    //fprintf(fp,"<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" width=\"800\" height=\"600\" viewBox=\"20 20 958.69 592.78998\" >\n");
    fprintf(fp,"<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" width=\"800\" height=\"600\" viewBox=\"20 20 958.69 592.78998\" >\n");
	for (int i=0;i<number;i++)
	{	
	lines[i].p1.x=rand()%(750)+30;
	lines[i].p1.y=rand()%(550)+20;
	drawPoint(lines[i].p1,fp);
	lines[i].p2.x=rand()%(750)+30;
	lines[i].p2.y=rand()%(550)+20;
	drawPoint(lines[i].p2,fp);
	 drawLine(fp,lines[i]);
	}
	for(int i=0;i<number;i++)
	{
		for(int j=i+1;j<number;j++ )
		{
			if(FindIntersect(lines[i].p1,lines[i].p2,lines[j].p1,lines[j].p2))
			{
				FindIntersectionPoint(lines[i],lines[j]);
			}
		}
	}
	chanConvexHull(intersectionPoint,pointCount,fp);
	fprintf(fp, "</svg>" );
	fclose(fp);
	if(!fork())
	system("firefox linesegment.svg");
return 0;
}

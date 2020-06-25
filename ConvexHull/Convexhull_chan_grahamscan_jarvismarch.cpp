#include <iostream>
#include <stdlib.h>
#include <stdio.h>
#include <math.h>
#define PARTITIONSIZE 30
using namespace std;
 
struct Point
{
    int x;
    int y;
};
struct Result
{
	/* data */
	Point *result;
	int size;
};
 Point *finalResult;
 static int finalResultSize;
 void drawLine(FILE *fp,Point p1 , Point p2)
{
    fprintf(fp,"<line x1=\"%d\" y1=\"%d\" x2=\"%d\" y2=\"%d\" stroke=\"red\" stroke-width=\"1\" />\n",p1.x,p1.y,p2.x,p2.y);
}

void drawPoint(Point p,FILE* fp)
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
		drawLine(fp,input1[i],input1[result[i]]);
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
	finalResult=(Point*)realloc(finalResult,finalResultSize*sizeof(Point));
	finalResult[finalResultSize-1]=result[i];
	drawLine(fp,result[i],result[i+1]);
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
	drawPoint(input1[i],fp1);
}

	jarvisMarch(finalResult,finalResultSize,fp1);
	fprintf(fp1,"</svg>\n");
   	 fclose(fp1);
}

int main()
{
    Point *points;
    Result *resultset;
   	int number;
	srand(time(0));
	//FILE *fp = fopen("convexhull.svg","w");
	//FILE *fp1 = fopen("convexhull_JarvisMrch.svg","w");
	FILE *fp2 = fopen("convexhull_chan.svg","w");
	cout<<"enter the number of points\n"  ;
	cin>>number;
	points=(Point*)malloc(number*sizeof(Point));
	// fprintf(fp1,"<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" width=\"800\" height=\"600\" viewBox=\"20 20 958.69 592.78998\" >\n");
    //fprintf(fp,"<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" width=\"800\" height=\"600\" viewBox=\"20 20 958.69 592.78998\" >\n");
    fprintf(fp2,"<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" width=\"800\" height=\"600\" viewBox=\"20 20 958.69 592.78998\" >\n");
   	cout<<number<<endl;
	for (int i=0;i<number;i++)
	{	
	points[i].x=rand()%(750)+30;
	points[i].y=rand()%(550)+20;
	// drawPoint(points[i],fp);
	 //drawPoint(points[i],fp1);
	 drawPoint(points[i],fp2);
	}
	//cout<<"after generating point"<<endl;
    int n = sizeof(points)/sizeof(points[0]);
   	 //grahamScan(points, number,fp);
   	 //jarvisMarch(points,number,fp1);
   	 chanConvexHull(points,number,fp2);
   	// int result_size=sizeof(result)/sizeof(Point);
   	/*cout<<"result size is..."<<resultset->size<<endl;

   	 for (int i = 0; i < resultset->size-1; ++i)
   	 {
   	 	 code 
   	 	cout<<"inside"<<endl;
   	 	drawLine(fp,resultset->result[i],resultset->result[i+1]);
   	 }*/
	/*fprintf(fp,"</svg>\n");
   	 fclose(fp);
   	 fprintf(fp1,"</svg>\n");
   	 fclose(fp1);*/
   	 fprintf(fp2,"</svg>\n");
   	 fclose(fp2);
	//free(points);
    return 0;
}


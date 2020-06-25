#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <time.h>
#include "datastructure.h"
 
// A utility function to get height of the tree
int getHeight(eventNode *eNode)
{
    if (eNode == NULL)
        return 0;
    return eNode->height;
}
 
// A utility function to get maximum of two integers
int maximum(int a, int b)
{
    return (a > b)? a : b;
}
 
/* Helper function that allocates a new node with the given key and
    NULL left and right pointers. */
eventNode* newNode(Point key)
{
	//printf("creating new node ....\n" );
    eventNode* eNode = (eventNode*)
                        malloc(sizeof(eventNode));
    eNode->data=key;
    eNode->leftChild   = NULL;
    eNode->rightChild  = NULL;
    eNode->parentNode=NULL;
    eNode->height = 1;  // new node is initially added at leaf
    return(eNode);
}
 
// A utility function to right rotate subtree rooted with y
// See the diagram given above.
eventNode*rightRotate(eventNode *y)
{
    eventNode *x = y->leftChild;
    eventNode *T2 = x->rightChild;
 
    // Perform rotation
    x->rightChild = y;
    y->leftChild = T2;
 
    // Update heights
    y->height = maximum(getHeight(y->leftChild), getHeight(y->rightChild))+1;
    x->height = maximum(getHeight(x->leftChild), getHeight(x->rightChild))+1;
 
    // Return new root
    return x;
}
 
// A utility function to left rotate subtree rooted with x
// See the diagram given above.
eventNode *leftRotate(eventNode *x)
{
    eventNode *y = x->rightChild;
    eventNode *T2 = y->leftChild;
 
    // Perform rotation
    y->leftChild = x;
    x->rightChild = T2;
 
    //  Update heights
    x->height = maximum(getHeight(x->leftChild), getHeight(x->rightChild))+1;
    y->height = maximum(getHeight(y->leftChild), getHeight(y->rightChild))+1;
 
    // Return new root
    return y;
}
 
// Get Balance factor of node N
int getBalance(eventNode *N)
{
    if (N == NULL)
        return 0;
    return getHeight(N->leftChild) - getHeight(N->rightChild);
}
 
eventNode* insert(eventNode* node, Point key)
{
    /* 1.  Perform the normal BST rotation */
   // printf("%d %d\n",key.x,key.y );
    if (node == NULL)
        return(newNode(key));
 
    if (key.y < node->data.y)
        node->leftChild  = insert(node->leftChild, key);
    else
        node->rightChild = insert(node->rightChild, key);
 
    /* 2. Update height of this ancestor node */
    node->height = maximum(getHeight(node->leftChild), getHeight(node->rightChild)) + 1;
 
    /* 3. Get the balance factor of this ancestor node to check whether
       this node became unbalanced */
    int balance = getBalance(node);
 
    // If this node becomes unbalanced, then there are 4 cases
 
    // Left Left Case
    if (balance > 1 && key.y < node->leftChild->data.y)
        return rightRotate(node);
 
    // Right Right Case
    if (balance < -1 && key .y> node->rightChild->data.y)
        return leftRotate(node);
 
    // Left Right Case
    if (balance > 1 && key.y > node->leftChild->data.y)
    {
        node->leftChild =  leftRotate(node->leftChild);
        return rightRotate(node);
    }
 
    // Right Left Case
    if (balance < -1 && key.y < node->rightChild->data.y)
    {
        node->rightChild = rightRotate(node->rightChild);
        return leftRotate(node);
    }
 
    /* return the (unchanged) node pointer */
    return node;
}
 
// A utility function to print preorder traversal of the tree.
// The function also prints height of every node

/* Given a non-empty binary search tree, return the node with minimum
   key value found in that tree. Note that the entire tree does not
   need to be searched. */
eventNode * minValueNode(eventNode* node)
{
    eventNode* current = node;
 
    /* loop down to find the leftmost leaf */
    while (current->leftChild != NULL)
        current = current->leftChild;
 
    return current;
}
 
eventNode* deleteNode(eventNode* root, Point key)
{
    // STEP 1: PERFORM STANDARD BST DELETE
 
    if (root == NULL)
        return root;
 
    // If the key to be deleted is smaller than the root's key,
    // then it lies in left subtree
    if ( key.y < root->data.y )
        root->leftChild = deleteNode(root->leftChild, key);
 
    // If the key to be deleted is greater than the root's key,
    // then it lies in right subtree
    else if( key.y > root->data.y )
        root->rightChild = deleteNode(root->rightChild, key);
 
    // if key is same as root's key, then This is the node
    // to be deleted
    else
    {
        // node with only one child or no child
        if( (root->leftChild == NULL) || (root->rightChild == NULL) )
        {
            eventNode *temp = root->leftChild ? root->leftChild : root->rightChild;
 
            // No child case
            if(temp == NULL)
            {
                temp = root;
                root = NULL;
            }
            else // One child case
             memcpy((void*)root,(void *)temp,sizeof(eventNode));// Copy the contents of the non-empty child
 
            free(temp);
        }
        else
        {
            // node with two children: Get the inorder successor (smallest
            // in the right subtree)
            eventNode* temp = minValueNode(root->rightChild);
 
            // Copy the inorder successor's data to this node
            root->data = temp->data;
 
            // Delete the inorder successor
            root->rightChild = deleteNode(root->rightChild, temp->data);
        }
    }
 
    // If the tree had only one node then return
    if (root == NULL)
      return root;
 
    // STEP 2: UPDATE HEIGHT OF THE CURRENT NODE
    root->height = maximum(getHeight(root->leftChild), getHeight(root->rightChild)) + 1;
 
    // STEP 3: GET THE BALANCE FACTOR OF THIS NODE (to check whether
    //  this node became unbalanced)
    int balance = getBalance(root);
 
    // If this node becomes unbalanced, then there are 4 cases
 
    // Left Left Case
    if (balance > 1 && getBalance(root->leftChild) >= 0)
        return rightRotate(root);
 
    // Left Right Case
    if (balance > 1 && getBalance(root->leftChild) < 0)
    {
        root->leftChild =  leftRotate(root->leftChild);
        return rightRotate(root);
    }
 
    // Right Right Case
    if (balance < -1 && getBalance(root->rightChild) <= 0)
        return leftRotate(root);
 
    // Right Left Case
    if (balance < -1 && getBalance(root->rightChild) > 0)
    {
        root->rightChild = rightRotate(root->rightChild);
        return leftRotate(root);
    }
 
    return root;
}
void preOrder(eventNode *root)
{
    if(root != NULL)
    {
        printf("%d ", root->data.x);
        printf("%d ",root->data.y );
        printf("\n");
        preOrder(root->leftChild);
        preOrder(root->rightChild);
    }
}
void InOrder(eventNode *root)
{
    if(root != NULL)
    {
        
        InOrder(root->leftChild);
        printf("%d ", root->data.x);
        printf("%d ",root->data.y );
        printf("\n");
        InOrder(root->rightChild);
    }
}
void InOrderReverse(eventNode *root)
{
    if(root != NULL)
    {
        
        InOrderReverse(root->rightChild);
        printf("%d ", root->data.x);
        printf("%d ",root->data.y );
        printf("\n");
         InOrderReverse(root->leftChild);
       
    }
}

 void main()
{
	
	int number,i;
	static int lineId=0;
	eventNode *rootEventNode=NULL;
	Point currentpoint1,currentpoint2;
	Line currentline;
	Line *lineArray;
	srand(time(NULL));
	printf("enter the number of lines\n" )  ;
	scanf("%d",&number);
	int tempId=0;
	for ( i = 0; i < number; ++i)
	{
		if(i)
		lineArray=(Line*)realloc(lineArray,(tempId+1)*sizeof(Line));
		else
			lineArray=(Line*)malloc(sizeof(Line));
		//printf("%p\n",lineArray );
		currentpoint1.x=rand()%(750)+30;
		currentpoint1.y=rand()%(550)+20;
		currentpoint2.x=rand()%(750)+30;
		currentpoint2.y=rand()%(550)+20;
		currentpoint1.segmentId=tempId;
		currentpoint2.segmentId=tempId;
		if(currentpoint2.y>currentpoint1.y)
			currentpoint2.type=upperPoint;
		else
			currentpoint1.type=lowerPoint;
		currentline.p1=currentpoint1;
		currentline.p2=currentpoint2;
		currentline.id=tempId;
		lineArray[i]=currentline;
		tempId=++lineId;
		rootEventNode=insert(rootEventNode,currentpoint1);
		rootEventNode=insert(rootEventNode,currentpoint2);
	}
	preOrder(rootEventNode);
    printf("inorder........\n");
    InOrder(rootEventNode);
     printf("inorder........ reverse...\n");
    InOrderReverse(rootEventNode);
    rootEventNode=deleteNode(rootEventNode,currentpoint2);
     printf("inorder........\n");
    InOrder(rootEventNode);


	
}
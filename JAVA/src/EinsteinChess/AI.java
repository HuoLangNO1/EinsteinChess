package EinsteinChess;


import java.util.LinkedList;

public class AI {
	
    Node root;
    int dice = -1;
    boolean role;

    static final int Search_Depth = 4;
    static final int MaxExpandNodeNum = 25;
    
    
    public AI(Node root, int dice) {
    	this.root = root;
    	this.role = root.role;
    	this.dice = dice;
    }
    
    public Node UCTSearch(){
        int i = 0;	//用于计数
        //int averageDepth = 0;
        root.setDice(dice);
        
        
        long startTime = System.currentTimeMillis();
        
        root.childrenList.clear();
        root.setBack();
        
        if(root.needAdjust()) {
        	root.hasAdjusted = false;
        	return root.expand();
        }
        else root.hasAdjusted = true;
        
        Node workNode = root.expand();
        
        workNode.TopBranchNode = workNode;
        workNode.setPara();
        //root.expand();
        
        LinkedList<Node> notExpandlist = new LinkedList<Node>();
        notExpandlist.addAll(root.childrenList);
        

        //try {
        int winMark = -1;
        while(System.currentTimeMillis() - startTime < 4000){
        
        	winMark = AIWin(workNode);
        	if(winMark != 3) {//选择新的节点
        		
        		i++;
        		if((winMark == 2 && !this.role) || (winMark == 1 && this.role)) backUp(workNode, true);
        		else backUp(workNode, false);
        		
        		//averageDepth += workNode.depth;
        		//if(notExpandlist.size() == 0) break;
        		
        		Node _temp;
        		for(int n = 0; n < notExpandlist.size(); n++) {
        			
        			_temp = notExpandlist.get(n);
        			//if(_temp.notExpandChess.size() != 0)
        				_temp.caculateUCB();
        			//else _temp.UCB = -1000;
        		}
        			
        		
        		double max = -10000.;
        		workNode = notExpandlist.get(0);
        		for(Node temp : notExpandlist) {
        			if(temp.UCB > max) {
        				workNode = temp;
        				max = temp.UCB;
        			}
        		}
        		workNode.TopBranchNode = workNode;
        		workNode.setPara();
        	}
        	else {
        		
        		workNode = workNode.expand();
        		
        		//if(workNode.depth == Search_Depth && notExpandlist.size() < MaxExpandNodeNum)
        			//notExpandlist.add(workNode);
        	}
        }
        
        
        //totalBackUp(notExpandlist);
        System.out.println("遍历节点个数" + i);
        //System.out.print("平均深度" + (float)averageDepth / i + '\n');
        return getBestChild(root);
    }

    Node getBestChild(Node root) {
    	double max = -1.0;
    	double temp = 0;
    	int index = 0;
    	for(int i = 0; i < root.childrenList.size(); i++) {
    		if((temp = (double)root.childrenList.get(i).winNum
    				/ (double)root.childrenList.get(i).visit) > max) {
    			max = temp;
    			index = i;
    		}
    	}
    	return root.childrenList.get(index);
    }
    
    static public int AIWin(Node node){
        
        if((node.chesses[4][4].role && node.chesses[4][4].num != 0) 
        		|| node.restChessOfBlue.size() == 0){
        	
        		return 1; //红方获胜
        	
        }
        else if((!node.chesses[0][0].role && node.chesses[0][0].num != 0)
        		|| node.restChessOfRed.size() == 0){
        	
        		return 2;
        	
        }
        else return 3; //暂时没有结果
    }
    
    public void backUp(Node node, boolean win){
    	
        Node temp = node;
        temp.winNum = 1;

        if(win) {
        	while(temp != temp.TopBranchNode){
        		
        		temp.father.visit ++;
        		if(temp.father.role)
        			temp.father.winNum += (temp.father.proOfRed[temp.chesses[temp.to[0]][temp.to[1]].num - 1]);
        		else 
        			temp.father.winNum += (temp.father.proOfBlue[temp.chesses[temp.to[0]][temp.to[1]].num - 1]);
            	temp = temp.father;
        		}
        	}
        
        else {
        	while(temp != temp.TopBranchNode){
        		
        			temp.father.visit ++;
        			if(temp.father.role) 
        				temp.father.winNum -= (temp.father.proOfRed[temp.chesses[temp.to[0]][temp.to[1]].num - 1]);
        			else 
        				temp.father.winNum -= (temp.father.proOfBlue[temp.chesses[temp.to[0]][temp.to[1]].num - 1]);
        			
        			temp = temp.father;
        	}
        }
        
    	
    }
    
    public int getNextExpendNode() {

    	double max = -10000;
    	int index = -1;
    	for(int i = 0; i < root.childrenList.size(); i++) {
    		
    		if(root.childrenList.get(i).UCB > max) {
    			index = i;
    			max = root.childrenList.get(i).UCB;
    		}
    	}
    	
    	return index;
    }
    
    public boolean checkChess(Node node) {
    	
    	for(int i = 0; i < 5; i++) {
    		for(int j = 0; j < 5; j++) {
    			
    			if((node.chesses[i][j].x != i || node.chesses[i][j].y != j) && node.chesses[i][j].num != 0) {
    				return false;
    			}
    		}
    	}
    	return true;
    }
    
    public void totalBackUp(LinkedList<Node> notExpandlist) {
    	
    	for(int i = 0; i < notExpandlist.size(); i++) {
    		
    			Node topBranchNode = notExpandlist.get(i);
    			Node iter =  topBranchNode;
    			
            	while(topBranchNode.depth > 1){
            		if(topBranchNode.father.role)
            			iter.winNum *= topBranchNode.father.proOfRed[topBranchNode.chesses[topBranchNode.to[0]][topBranchNode.to[1]].num - 1];
            		else iter.winNum *= topBranchNode.father.proOfBlue[topBranchNode.chesses[topBranchNode.to[0]][topBranchNode.to[1]].num - 1];
            		topBranchNode = topBranchNode.father;
            		
            	}
    			if(iter.depth == 1)
    				continue;
            	topBranchNode.visit += iter.visit - 1;
            	topBranchNode.winNum += iter.winNum;
    	}
    }
}

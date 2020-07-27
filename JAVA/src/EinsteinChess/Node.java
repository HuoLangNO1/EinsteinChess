package EinsteinChess;

import java.util.ArrayList;

import java.util.Random;


public class Node {
	
	private final static int[][] ValueOfRed = {
            {0, 2, 2, 2, 1},
            {2, 4, 4, 4, 5},
            {2, 4, 8, 8, 10},
            {2, 4, 8, 16, 20},
            {1, 5, 10, 20, 32}
    };
    private final static int[][] ValueOfBlue = {
            {32, 20, 10, 5, 1},
            {20, 16, 8, 4, 2},
            {10, 8, 9, 4, 2},
            {5, 4, 4, 4, 2},
            {1, 2, 2, 2, 0}
    };
    private final static int[][] ValueForAdjustOfRed = {
    		{1, 1, 5, 4, 1},
            {1, 2, 1, 5, 7},
            {5, 1, 9, 8, 10},
            {4, 5, 8, 16, 20},
            {1, 7, 10, 20, 32}
    };
    private final static int[][] ValueForAdjustOfBlue = {
    		{32, 20, 10, 7, 1},
            {20, 16, 8, 5, 4},
            {10, 8, 8, 1, 5},
            {7, 5, 1, 2, 1},
            {1, 4, 5, 1, 1}
    };
    
    private final static int[] ChessNumValue = {2, 5, 7, 4, 2, 1};
    
    public double Kalpha = 0, Kbeta = 0;
    public double K1 = 0, K2 = 0, K3 = 0, K4 = 0;
	
	public boolean role = false;
	public Chess[][] chesses;
	
	public Node father;
	public Node TopBranchNode;
	
	
	public ArrayList<Node> childrenList = new ArrayList<Node>();
	boolean expandMarket = false;//
	
	public Chess forwardOfRed, forwardOfBlue;
	
	
	public int visit = 1;	
	public double winNum = .0;	
	public int depth = -1;
	public double UCB = .0;
	public static final double alpha = .650;
	
	public static int totalVisitNum = 0;
	public ArrayList<Chess> restChessOfRed;
	public ArrayList<Chess> restChessOfBlue;
	public ArrayList<Chess> notExpandChess;
	
	boolean hasAdjusted = false;
	
	public int[] from, to;//记录从上个界面变到现在界面的走子
	private int dice = -1;
	
	public double[] proOfRed, proOfBlue;	//棋子被选中的概率
	
	public Node() {}
	
	public Node(int dice,  boolean role){
		
        this.role = role;
        this.dice=dice;
    }
	
	public Node(Node father, int[] posFrom){
		from = new int[2];
		to = new int[2];
		this.TopBranchNode = father.TopBranchNode;
		
		this.father = father;
        this.role = !father.role;
        this.depth = father.depth + 1;
        from[0] = posFrom[0];
        from[1] = posFrom[1];
        
        this.hasAdjusted = father.hasAdjusted;
        //this.setBack();
    }
	
	public void setDice(int dice) {
		this.dice = dice;
	}
	
    public void setFather(Node father, int[] posFrom){
    	
        this.father = father;
        this.depth = father.depth ++;
        this.TopBranchNode = father.TopBranchNode;
        
        from[0] = posFrom[0];
        from[1] = posFrom[1];
        //setBack();
    }
    
    public void setBack(){		//拷贝棋盘界面
    	
    	restChessOfRed = new ArrayList<Chess>();
    	restChessOfBlue = new ArrayList<Chess>();
    	notExpandChess = new ArrayList<Chess>();
    	
    	if(this.depth == 0) {
    		for(int i = 0; i < 5; i++){
                for(int j = 0; j < 5; j++){
                	
                	if(chesses[i][j].role && chesses[i][j].num != 0) {
                    	restChessOfRed.add(chesses[i][j]);
                    	
                    	if(role) notExpandChess.add(chesses[i][j]);
                    	if(forwardOfRed == null || 
                        		(Math.max(chesses[i][j].x, chesses[i][j].y) 
                        			> Math.max(forwardOfRed.x, forwardOfRed.y)))
                        		forwardOfRed = chesses[i][j];
                    }
                    
                    if(!chesses[i][j].role && chesses[i][j].num != 0) {
                    	restChessOfBlue.add(chesses[i][j]);
                    	
                    	if(!role) notExpandChess.add(chesses[i][j]);
                    	if(forwardOfBlue == null || 
                        		(Math.max(chesses[i][j].x, chesses[i][j].y) 
                        			< Math.max(forwardOfBlue.x, forwardOfBlue.y)))
                        		forwardOfBlue = chesses[i][j];
                    }
                	
                }
    		}
    		return;
    	}
    	this.role = !father.role;
    	chesses = new Chess[5][5];
        for(int i = 0; i < 5; i++){
            for(int j = 0; j < 5; j++){
            	chesses[i][j] = new Chess();
            	chesses[i][j].num = father.chesses[i][j].num;
                chesses[i][j].role = father.chesses[i][j].role;
                chesses[i][j].x = father.chesses[i][j].x;
                chesses[i][j].y = father.chesses[i][j].y;
                   	
            }
        }
        
        Chess fromChess = chesses[from[0]][from[1]], 
    			toChess = chesses[to[0]][to[1]];
		toChess.role = fromChess.role;
		toChess.num = fromChess.num;
		fromChess.num = 0;
		
		for(int i = 0; i < 5; i++){
            for(int j = 0; j < 5; j++){
            	
            	if(chesses[i][j].role && chesses[i][j].num != 0) {
                	restChessOfRed.add(chesses[i][j]);
                	
                	if(role) notExpandChess.add(chesses[i][j]);
                	if(forwardOfRed == null || 
                    		(Math.max(chesses[i][j].x, chesses[i][j].y) 
                    			> Math.max(forwardOfRed.x, forwardOfRed.y)))
                    		forwardOfRed = chesses[i][j];
                }
                
                if(!chesses[i][j].role && chesses[i][j].num != 0) {
                	restChessOfBlue.add(chesses[i][j]);
                	
                	if(!role) notExpandChess.add(chesses[i][j]);
                	if(forwardOfBlue == null || 
                    		(Math.max(chesses[i][j].x, chesses[i][j].y) 
                    			< Math.max(forwardOfBlue.x, forwardOfBlue.y)))
                    		forwardOfBlue = chesses[i][j];
                }
            }
        }
		
		
        proOfRed = this.Probability(true);
        proOfBlue = this.Probability(false);
      
    }
    
    public Node setRamdonPosition(int[] pos) {
    	
    	
    	return null;
    }
    
    
    public Node expand() {
        
    	if(depth <= AI.Search_Depth)
    		Node.totalVisitNum ++;
    	
    	Random rand = new Random();
    	
    	Chess chosenChess = null, chosenChess2 = null;
    	
    	if(dice == -1) {
    		chosenChess = notExpandChess.get(rand.nextInt(notExpandChess.size()));

    		return setPosition(chosenChess, null);
    	}
    	else{
    		if((chosenChess = hasChess(role, dice)) == null) {
    			
    			Node temp = null;
    			boolean market = false;
    			
    			for(int i = 1; i < 6; i++) {
    				chosenChess = hasChess(role, dice + i);
    				chosenChess2 = hasChess(role, dice - i);
    				if(chosenChess != null || chosenChess2 != null) {
    					
    					if(chosenChess2 != null && chosenChess != null) temp = setPosition(chosenChess, chosenChess2);
    					else if(chosenChess != null) temp = setPosition(chosenChess, null);
    					else if(chosenChess2 != null) temp = setPosition(chosenChess2, null);
    					market = true;
    				}
    				if(market) return temp;
    			}
    		}
    		return setPosition(chosenChess, null);
    	}
       
    }
    /////////////////////////////////////////////////////////////////////////////////////
    public Node setPosition(Chess chosenChess, Chess chosenChess2){
    	
        int toward = 1;
        if(!role) toward = -1;
        Node temp;
        
        ArrayList<Node> NodeList = new ArrayList<Node>();
        int[] pos = new int[2];
        pos[0] = chosenChess.x;
        pos[1] = chosenChess.y;
        
        if(rightChild(pos[0] + toward, pos[1])) {
        	temp = new Node(this, pos);
        	
        	temp.to[0] = pos[0] + toward;
        	temp.to[1] = pos[1];
        	NodeList.add(temp);
        
    		temp.setBack();
        }
        
        if(rightChild(pos[0], pos[1] + toward)) {
        	temp = new Node(this, pos);
        	
        	temp.to[0] = pos[0];
        	temp.to[1] = pos[1] + toward;
        	NodeList.add(temp);
        	
    		temp.setBack();
        }
        
        if(rightChild(pos[0] + toward, pos[1] + toward)) {
        	temp = new Node(this, pos);
        	
        	temp.to[0] = pos[0] + toward;
        	temp.to[1] = pos[1] + toward;
        	NodeList.add(temp);
        	
    		temp.setBack();
        }
        
        if(chosenChess2 != null) {
        	
        	pos[0] = chosenChess2.x;
        	pos[1] = chosenChess2.y;
        	
        	if(rightChild(pos[0] + toward, pos[1])) {
            	temp = new Node(this, pos);
            	
            	temp.to[0] = pos[0] + toward;
            	temp.to[1] = pos[1];
            	NodeList.add(temp);
            	
        		temp.setBack();
            }
            
            if(rightChild(pos[0], pos[1] + toward)) {
            	temp = new Node(this, pos);
            	
            	temp.to[0] = pos[0];
            	temp.to[1] = pos[1] + toward;
            	NodeList.add(temp);
            	
        		temp.setBack();
            }
            
            if(rightChild(pos[0] + toward, pos[1] + toward)) {
            	temp = new Node(this, pos);
            	
            	temp.to[0] = pos[0] + toward;
            	temp.to[1] = pos[1] + toward;
            	NodeList.add(temp);
            	
        		temp.setBack();
            }
        }
        
        if(this.depth == 0) {
        	this.childrenList.addAll(NodeList);
        	Random rand = new Random();
        	if(!this.hasAdjusted)
        		return this.expand_strategy_static_assessment(NodeList, false);
        	else return this.childrenList.get(rand.nextInt(this.childrenList.size()));
        	
        }
        else {
        	//if(this.depth <= AI.Search_Depth)
        		return this.expand_strategy_function_assessment(NodeList);
        	//else 
        		//return this.expand_strategy_random_assessment(NodeList);
    	}
    }
    
    public Node expand_strategy_random_assessment(ArrayList<Node> NodeList) {
    	//完全随机向下扩展
    	Random rand = new Random();
    	return NodeList.get(rand.nextInt(NodeList.size()));
    }
    
    public Node expand_strategy_static_assessment(ArrayList<Node> NodeList, boolean status) {
    	
    	Node iter;
    	int[] value = new int[NodeList.size()];
    	
    	for(int i = 0; i < NodeList.size(); i++) {
    		
    		value[i] = 0;
    		iter = NodeList.get(i);
    		
    		
    		if(chesses[iter.to[0]][iter.to[1]].role == role && chesses[iter.to[0]][iter.to[1]].num !=0)
    			value[i] += 15;
    		if(role) {
    			for(Chess chess : iter.restChessOfRed) {
    				value[i] += ValueForAdjustOfRed[chess.x][chess.y];
    				
    			}
    			value[i] -= ValueForAdjustOfBlue[iter.forwardOfBlue.x][iter.forwardOfBlue.y];
    		}
    		else {
    			for(Chess chess : iter.restChessOfBlue) {
    				value[i] += ValueForAdjustOfBlue[chess.x][chess.y];
    			}
    			value[i] -= ValueForAdjustOfRed[iter.forwardOfRed.x][iter.forwardOfRed.y];
    		}
    	}
    	int max = -1000;
    	int index = 0;
    	for(int i = 0; i < NodeList.size(); i++) {
    		if(value[i] > max) {
    			max = value[i];
    			index = i;
    		}
    	}
    	return NodeList.get(index);
    }
    
    public Node expand_strategy_function_assessment(ArrayList<Node> NodeList) {
    	//根据评估函数向下扩展
    	double[] value = new double[NodeList.size()];
    	
    	for(int n = 0; n < NodeList.size(); n++) {
    		
    		value[n] = .0;
    		Node child = NodeList.get(n);
    		
    		double va = .0;
    		if(child.role) {
    			
    			if(child.restChessOfRed.size() == 0)
    				value[n] = -1000;
    			
    			else {
    				
    				if(child.restChessOfBlue.size() == 0) {
    					value[n] = 1000;
    					continue;
    				}
    				
    				for(Chess _chess : child.restChessOfRed) {
    					va += (TopBranchNode.Kalpha * ValueOfRed[_chess.x][_chess.y] * proOfRed[_chess.num - 1]);
    			
    				}
    				for(Chess _chess : child.restChessOfBlue) {
    					va -= (TopBranchNode.Kbeta * ValueOfBlue[_chess.x][_chess.y] * proOfBlue[_chess.num - 1]);
    				}
    				
    				va += (TopBranchNode.K3 * ValueOfRed[child.forwardOfRed.x][child.forwardOfRed.y] 
    						* proOfRed[child.chesses[child.forwardOfRed.x][child.forwardOfRed.y].num - 1]);
    				va -= (TopBranchNode.K4 * ValueOfBlue[child.forwardOfBlue.x][child.forwardOfBlue.y]
    						* child.proOfBlue[child.chesses[child.forwardOfBlue.x][child.forwardOfBlue.y].num - 1]);
    				value[n] = (va + ChessNumValue[child.restChessOfBlue.size() - 1] * TopBranchNode.K1);
    			}
    		}
    		/////////////////////////////////////////////////////////
    		else {
    			if(child.restChessOfBlue.size() == 0)
    					value[n] = -1000;
    			else {
    					
    				if(child.restChessOfRed.size() == 0) {
    					value[n] = 1000;
    					continue;
    				}
    				
    				for(Chess _chess : child.restChessOfBlue) {
    					
    					va += (TopBranchNode.Kalpha * ValueOfBlue[_chess.x][_chess.y] * proOfBlue[_chess.num - 1]);
    				}
    				
    				for(Chess _chess : child.restChessOfRed) {
    					
    					va -= (TopBranchNode.Kbeta * ValueOfRed[_chess.x][_chess.y] * proOfRed[_chess.num - 1]);
    				}
    				
    				va += (TopBranchNode.K3 * ValueOfBlue[child.forwardOfBlue.x][child.forwardOfBlue.y]
    						* proOfBlue[child.chesses[child.forwardOfBlue.x][child.forwardOfBlue.y].num - 1]);
    				
    				
    				va -= (TopBranchNode.K4 * ValueOfRed[child.forwardOfRed.x][child.forwardOfRed.y]
    						* child.proOfRed[child.chesses[child.forwardOfRed.x][child.forwardOfRed.y].num - 1]);
    				value[n] = (va + ChessNumValue[child.restChessOfRed.size() - 1] * TopBranchNode.K1);
    			}
    		}
    	}
    			int index = 0;
    	    	double max = -100;
    	    	for(int i = 0; i < NodeList.size(); i++) {
    	    		if(max < value[i]) {
    	    			index = i;
    	    			max = value[i];
    	    		}
    	    	}
    		
    	    return NodeList.get(index);
    	    
    }
    
    public boolean rightChild(int x, int y){
    	
        if(x >= 0 && y >= 0 && x < 5 && y < 5) return true;
        else return false;
    }
    
    
    public Chess hasChess(boolean role, int num){ //找到棋子并记录位置
    	
    	int dice = num % 6;
    	if(dice == 0) dice = 6;

    	ArrayList<Chess> restChess;
    	
    	if(role) restChess = restChessOfRed;
    	else restChess = restChessOfBlue;
    	
    	for(int i = 0; i < restChess.size(); i++) {
    		if(restChess.get(i).num == dice) {
    				
    			return restChess.get(i);
    		}
    	}
    		return null;
    	
    }
    
    public boolean exsistChess(boolean role) {
    	for(int i = 0; i < 5; i++){
            for(int j = 0; j < 5; j++){
            	if(chesses[i][j].role == role && chesses[i][j].num != 0)
            		return true;
            }
    	}
    	return false;
    }
    
    public void printNode() {
    	for(int i = 0; i < 5; i++){
            for(int j = 0; j < 5; j++){
            	System.out.print(chesses[i][j].num);
            }
            System.out.println();
    	}
    	 System.out.println();
    }
    
    public double[] Probability(boolean para_role){
    	
        boolean[] exist = setProbability(para_role);
        int m = 0, j = 0;
        double base = 1/(double)6;
        
        double[] pro = new double[6];
        for(int i = 0; i < 6; i++){
            
            if(exist[i]){
                pro[i] = base;
                for(j = 1; j < 5; j++){
                    if(i + j < 6 && !exist[i+j]){
                        m++;
                    }
                    else{
                        break;
                    }
                }
                if(i != 5){
                    if(i + j == 6)
                        pro[i] += (m * base);
                    else if(m > 0){
                        pro[i] += (Math.max(m - 1, 0) * base + base * 0.5);
                    }
                }
                m=0;
                for(j = 1; j < 6; j++){
                    if(i - j >= 0 && !exist[i-j]){
                        m++;
                    }
                    else{
                        break;
                    }
                }
                if(i != 0){
                    if(i - j == 0){
                        pro[i] += (m * base);
                    }
                    else if(m > 0){
                    	pro[i] += (Math.max(m - 1, 0) * base + base * 0.5);
                    }
                }
            }
        }
        
        return pro;
    }
    
    public boolean[] setProbability(boolean para_role){
    	
    	boolean[] exist = new boolean[6];
    	for(int i = 0; i < 6; i++)
    		exist[i] = false;
    	
        for(int i = 0; i < 5; i++)
            for(int j = 0; j < 5; j++){
                if(chesses[i][j].role == para_role
                		&& chesses[i][j].num != 0)
                    exist[chesses[i][j].num - 1] = true;
            }
        return exist;
        ////////////////////////////////////////////////
        
    }
    
    public void caculateUCB() {
    	
    	this.UCB = (1 - alpha) * Math.sqrt(Math.log(totalVisitNum) / (double)visit) + alpha * winNum / (double)visit;
    }
    
    public boolean checkChess() {
    	
    	for(int i = 0; i < 5; i++) {
    		for(int j = 0; j < 5; j++) {
    			
    			if((chesses[i][j].x != i || chesses[i][j].y != j) && chesses[i][j].num != 0) {
    				return false;
    			}
    		}
    	}
    	return true;
    }
    
    public void setPara() {
    	Random rand = new Random();
    	Kalpha = rand.nextFloat() + 1;
    	Kbeta = rand.nextFloat();
    	K1 = rand.nextFloat();
    	K2 = rand.nextFloat();
    	K3 = rand.nextFloat() + 3;
    	K4 = rand.nextFloat() + 3; 
    }
    
    public boolean needAdjust() {
    	//是否需要通过简单局面评估来调整起始状态
    	
    	if(role) {
    		if(ValueForAdjustOfBlue[forwardOfBlue.x][forwardOfBlue.y] >= 10) return false;
    	}
    	else {
    		if(ValueForAdjustOfRed[forwardOfRed.x][forwardOfRed.y] >= 10) return false;
    	}
    	
    	if(this.depth + ChessBoard.stepCount < 4) return true;
    	if(this.role && this.restChessOfRed.size() <= 3) return false;
    	if(!this.role && this.restChessOfBlue.size() <= 3) return false;
    	
    	ArrayList<Chess> restChess;
    	int[][] valueOfChess;
    	if(role) {
    		restChess = restChessOfRed;
    		valueOfChess = ValueForAdjustOfRed;
    	}
    	else {
    		restChess = restChessOfBlue;
    		valueOfChess = ValueForAdjustOfBlue;
    	}
    	
    	int value = 0;
    	for(Chess iter : restChess) {
    		
    		value += valueOfChess[iter.x][iter.y];
    	}
    	if((float)value / restChess.size() < 6)
    		return true;
    	else return false;
    }

}

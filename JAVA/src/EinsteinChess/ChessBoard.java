package EinsteinChess;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.VBox;

public class ChessBoard {
	
	public Chess[][] chesses = new Chess[5][5];
	public int[][] chess_num = {
			{1, 4, 6, 0, 0},
			{2, 5, 0, 0, 0},
			{3, 0, 0, 0, 3},
			{0, 0, 0, 5, 2},
			{0, 0, 6, 4, 1}
	};
	
	public Node root;
	static public int stepCount = 1;
	public ArrayList<String> log = new ArrayList<String>();
	
	private int x = -1, y = -1;
	
	private boolean isOver = false;
	
	private StackPane[][] sps = new StackPane[5][5];
	private GridPane gridPane = new GridPane();
	private boolean isChosen = false;
	private boolean myRole = false;
	private boolean firstRole = false;
	
	private VBox vbox = new VBox(15);
	public RadioButton r1 = new RadioButton();
    public RadioButton r2 = new RadioButton();
    private Button b1 = new Button("设置");
    private Label label = new Label("请输入骰子数：");
    private TextField text = new TextField();
    private Button b2 = new Button("确定");
    private Button b3 = new Button("回退");
    private Chess backFrom = new Chess(), backTo = new Chess();
    
    private Label status = new Label();
    BorderPane borderPane = new BorderPane();
	
    public void autoPlay() {
    	try {
    		
			ServerSocket server = new ServerSocket(2048);
			System.out.println("等待程序连接");
			Socket socket = server.accept();
			System.out.println("程序连接成功");
			java.io.InputStream in;
			OutputStream out;
			byte[] buf = new byte[1024];
			int len = 0;
			StringBuilder sb;
			Random rand = new Random();
			out = socket.getOutputStream();
			in = socket.getInputStream();
			do {
				sb = new StringBuilder();
				
				while(sb.length() < 7 && (len = in.read(buf)) != 0){
					sb.append(new String(buf, 0, len, "UTF-8"));
				}
				// in.close();
					String[] data = sb.toString().split(" ");
					move(Integer.parseInt(data[0]),
							Integer.parseInt(data[1]), 
							Integer.parseInt(data[2]), 
							Integer.parseInt(data[3]));
					drawPane();
					disPlay();
					if(isOver) break;
					AI ai = new AI(root, rand.nextInt(6) + 1);
					Node node = ai.UCTSearch();
            		move(node);
            		drawPane();
            		disPlay();
            		if(isOver) break;
            		out.write((String.valueOf(node.from[0]) + " "
            				+ String.valueOf(node.from[1]) + " "
            				+ String.valueOf(node.to[0]) + " "
            				+ String.valueOf(node.to[1])).getBytes("UTF-8"));
            		// out.close();
				
			}while(true);
			in.close();
			out.close();
			socket.close();
			server.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
    }
    void disPlay() {
    	
    	for(int j = 0; j < 5; j++) {
    		for(int i = 0; i < 5; i++) {
    		
    			if(root.chesses[i][j].num == 0) System.out.print(" ");
    			else if(root.chesses[i][j].role) System.out.print("#");
    			else System.out.print("$");
    			System.out.print(" ");
    		}
    		System.out.print("\n");
    	}
    	System.out.print("\n\n");
    }
    
	ChessBoard(){
		
		root = new Node();
		root.chesses = this.chesses;
		root.father = null;
		root.depth = 0;
		
		status.setText("");
		
		for(int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
            	
            	if(j + i < 5 && chess_num[i][j] != 0)
            		chesses[i][j] = new Chess(i ,j, chess_num[i][j], true);
            	else
            		chesses[i][j] = new Chess(i ,j, chess_num[i][j], false);
            }
		}
		
		r1.setText("红方先手");
		r2.setText("我方红");
		
		vbox.getChildren().addAll(r1,r2,b1,label,text,b2,b3,status);
		
		borderPane.setLeft(gridPane);
		borderPane.setRight(vbox);
		
		
		b1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
				r1.setDisable(true);
				r2.setDisable(true);
				b1.setDisable(true);
				// Random rand = new Random();
				// text.setText(String.valueOf(rand.nextInt(6) + 1));
				if(r1.isSelected()) {
					root.role = true;
					firstRole = true;
					status.setText("红方先手");
				}
				else {
					root.role = false;
					firstRole = false;
					status.setText("蓝方先手");
				}
				if(r2.isSelected()) {
					myRole = true;
					status.setText(status.getText() + ",我方红");
				}
				
				else {
					status.setText(status.getText() + ",我方蓝");
					myRole = false;
				}
				
				char a = 'A';
				String R = "", B = "";
				for(int i = 0; i < 5; i++) {
					a = (char) ('A' + i);
					for(int j = 0; j < 5; j++) {
						
						if(chesses[i][j].num != 0) {
							if(chesses[i][j].role) {
								R += (a + String.valueOf(5 - j) + '-' + String.valueOf(chesses[i][j].num) + ';');
							}
							else {
								B += (a + String.valueOf(5 - j) + '-' + String.valueOf(chesses[i][j].num) + ';');
							}
						}
					}
				}
				log.add("R:" + R);
				log.add("B:" + B);
				// autoPlay();
			}
		});
		
		b3.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(stepCount > 1) {
					Chess from = chesses[backFrom.x][backFrom.y];
					Chess to = chesses[backTo.x][backTo.y];
					from.num = backFrom.num;
					from.role = backFrom.role;
					to.num = backTo.num;
					to.role = backTo.role;
					
					stepCount --;
					log.remove(log.size() - 1);
					root.role = !root.role;
					if(root.role) status.setText("红方回合");
					else status.setText("蓝方回合");
					drawPane();
					
				}
			}
		});
		
		b2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	
            	if(b1.isDisable()) {
            		// Random rand = new Random();
            		int dice = Integer.parseInt(text.getText().trim());
            		// text.setText(String.valueOf(rand.nextInt(6) + 1));
            		AI ai = new AI(root, dice);
            		move(ai.UCTSearch());
            		drawPane();
            		
            	}
            	else {
            		text.setText("未设置先手");
            	}
            }
		});
		
		
	}
	
	public void move(int x, int y, int _x, int _y) {
		
		Chess from = chesses[x][y], to = chesses[_x][_y];
		// if(from.num == 0) System.out.print("eeeerrrrrrrrrrrroooooooorrrrr");
		if(b1.isDisable()) {
			if(Math.abs(x - _x) > 1 || Math.abs(y-_y) > 1) return;
			
			if(chesses[x][y].num != 0 && chesses[x][y] != chesses[_x][_y]) {
				backFrom.num = from.num;
				backFrom.role = from.role;
				backFrom.x = from.x;
				backFrom.y = from.y;
				
				backTo.num = to.num;
				backTo.role = to.role;
				backTo.x = to.x;
				backTo.y = to.y;
				
				chesses[_x][_y].num = from.num;
				chesses[_x][_y].role = from.role;
				from.num = 0;
				
				System.out.println((char)('A' + x) + "," + (5 - y) + "->" + (char)('A' + _x) + "," + (5 - _y));
				String temp = "";
				if(root.role) {
					temp += (String.valueOf(stepCount) + ':' + text.getText().trim() + ';' 
						+ "(R" + String.valueOf(5 - from.y) + ','
						+ (char)('A' + to.x) + String.valueOf(5 - to.y) + ')');
				}
				else {
					temp += (String.valueOf(stepCount) + ':' + text.getText().trim() + ';' 
							+ "(B" + String.valueOf(5 - from.y) + ','
							+ (char)('A' + to.x) + String.valueOf(5 - to.y) + ')');
				}
				
				log.add(temp);
				
				root.setBack();
				int winNum = AI.AIWin(root);
				if(winNum != 3) {
					System.out.print("Game over!\n");
					status.setText("游戏结束");
					//b2.setDisable(true);
					this.isOver = true;
					
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					try {
						File file = new File(".\\" + "WTN-先手参赛队 vs 后手参赛队-先(后)手胜" + ".txt");
						file.createNewFile();
						FileWriter writer = new FileWriter(file);
						if(myRole) {
							if(firstRole) {
								if(winNum == 1) {
									writer.write("#[WTN][北信科爱因斯坦棋 R][后手参赛队 B][先手胜][" + simpleDateFormat.format(new Date()) + "北京][2019 CCGC]\n");
								}
								else {
									writer.write("#[WTN][北信科爱因斯坦棋 R][后手参赛队 B][后手胜][" + simpleDateFormat.format(new Date()) + "北京][2019 CCGC]\n");
								}
							}
							else {
								if(winNum == 1) {
									writer.write("#[WTN][先手参赛队 B][北信科爱因斯坦棋 R][后手胜][" + simpleDateFormat.format(new Date()) + "北京][2019 CCGC]\n");
								}
								else {
									writer.write("#[WTN][先手参赛队 B][北信科爱因斯坦棋 R][先手胜][" + simpleDateFormat.format(new Date()) + "北京][2019 CCGC]\n");
								}
							}
						}
						else {
							if(firstRole) {
								if(winNum == 1) {
									writer.write("#[WTN][后手参赛队 R][北信科爱因斯坦棋 B][先手胜][" + simpleDateFormat.format(new Date()) + "][2019 CCGC]\n");
								}
								else {
									writer.write("#[WTN][后手参赛队 R][北信科爱因斯坦棋 B][后手胜][" + simpleDateFormat.format(new Date()) + "][2019 CCGC]\n");
								}
							}
							else {
								if(winNum == 1) {
									writer.write("#[WTN][北信科爱因斯坦棋 B][先手参赛队 R][后手胜][" + simpleDateFormat.format(new Date()) + "][2019 CCGC]\n");
								}
								else {
									writer.write("#[WTN][北信科爱因斯坦棋 B][先手参赛队 R][先手胜][" + simpleDateFormat.format(new Date()) + "][2019 CCGC]\n");
								}
							}
						}
						for(int i = 0; i < log.size(); i++) {
							writer.write(log.get(i) + '\n');
						}
						writer.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				root.role = !root.role;
				stepCount++;
			}
			
		
			x = -1;
			y = -1;
			isChosen = false;
			if(root.role) status.setText("红方回合");
			else status.setText("蓝方回合");
			
			
		}
		else {
			int n = to.num;
			boolean bo = to.role;
			chesses[_x][_y].num = from.num;
			chesses[_x][_y].role = from.role;
			from.num = n;
			from.role = bo;
		}
	}
	
	public void move(Node node) {
		
		move(node.from[0], node.from[1], node.to[0], node.to[1]);
	}
	
	public Scene showBoard() {

		gridPane.setOnMouseClicked(e -> {
			if(isChosen) {
				int x = (int)(e.getSceneX() / 80), y = (int)(e.getSceneY() / 80);
				if(!(this.x == x && this.y == y)) {
					move(this.x, this.y, x, y);
				}
				else {
					this.x = -1;
					this.y = -1;
				}
			}
			else {
				int x = (int)(e.getSceneX() / 80), y = (int)(e.getSceneY() / 80);
				this.x = x;
				this.y = y;
			}
			isChosen = !isChosen;
			drawPane();
		});
		
		
		Scene scene = new Scene(borderPane,600,500);
		drawPane();
		return scene;
	}
	
	private void drawPane() {
		
		Rectangle rect;
		
		for(int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                
            	rect = new Rectangle();
            	rect.setWidth(80);
            	rect.setHeight(80);
            	rect.setStroke(Color.BLACK);
            	
                sps[i][j] = new StackPane();
                
                if(chesses[j][i].num != 0) {
                	
                	if(i == y && j == x && isChosen) {
                		
                			rect.setFill(Color.YELLOW);
                	}
                	else if(chesses[j][i].role == false)
                		rect.setFill(Color.BLUE);
                	else
                		rect.setFill(Color.RED);
                	sps[i][j].getChildren().addAll(rect, new Label("" + chesses[j][i].num));
                } 
                else {
                	rect.setFill(Color.WHITE);
                    sps[i][j].getChildren().addAll(rect, new Label(""));
                }
                gridPane.add(sps[i][j], j, i);
            }
        }
	}
    
}

package EinsteinChess;

public class Chess {
	
	public int x, y;
	public int num = 0;
	public boolean role; //1代表红色,0代表蓝色
	
	public Chess() {}
	
	public Chess(int _x, int _y, int _num, boolean _role) {
		x = _x;
		y = _y;
		num = _num;
		role = _role;
	}
}

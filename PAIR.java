public class PAIR {
	public int x, y;
	public PAIR(){
		x=0;
		y=0;
	}
	public PAIR(int a, int b)
	{
		x = a;
		y = b;
	}
	public void set(int a, int b)
	{
		x = a;
		y = b;
	}
	public PAIR deepCopy(PAIR p)
	{
		return new PAIR(p.x, p.y);
	}
	public void swap(PAIR p)
	{
		int tmp_x = p.x;
		int tmp_y = p.y;
		p.x = x;
		p.y = y;
		x = tmp_x;
		y = tmp_y;	
	}
}

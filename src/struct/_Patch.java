package struct;

public class _Patch {

	private Complex[][] image;
	private int startX, startY;
	
	private int height, width;	
	public int height()	{ return height; }
	public int width() 	{ return width;	 }
	
	public _Patch(Complex[][] _image, int _startX, int _startY, int _height, int _width){
		image = _image;
		startX = _startX; startY = _startY;
		height = _height; width = _width;
	}
	
	public Complex getValue(int x, int y){
		return image[startY + y][startX + x];
	}
	
	public _Patch shift(int _shiftX, int _shiftY){
		return new _Patch(image, startX + _shiftX, startY + _shiftY, height, width);
	}
	
	public static boolean matchSize(_Patch p1, _Patch p2){
		return p1.height == p2.height && p1.width == p2.width;
	}
}

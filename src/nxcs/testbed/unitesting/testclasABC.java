package nxcs.testbed.unitesting;

import static org.junit.Assert.*;

import org.junit.Test;

class ABC{
	private int a;
	private int b;
	private int c;
	
	public ABC(int a, int b){
		this.a=a;
		this.b=b;	
	}
	
	public ABC(int a, int b, int c){
		this.a=a;
		this.b=b;
		this.c=c;
	}
	
	public int getA() {
		return a;
	}
	public void setA(int a) {
		this.a = a;
	}
	public int getB() {
		return b;
	}
	public void setB(int b) {
		this.b = b;
	}

	public int getC() {
		return c;
	}

	public void setC(int c) {
		this.c = c;
	}
}


class Sum{
	private int sum;
	public int Cal(ABC abc){
		return abc.getA()+abc.getB();
	}
	
	public int Cal3(ABC abc){
		return abc.getA()+abc.getB()+abc.getC();
	}
	
}

public class testclasABC {
	

	@Test
	public void test() {
		ABC a = new ABC(1,2);
		Sum sum= new Sum();
		int star=sum.Cal(a);
		assertEquals(star, 3);
	}
	
	@Test
	public void test3() {
		ABC a = new ABC(1,2,3);
		Sum sum= new Sum();
		int star=sum.Cal3(a);
		assertEquals(star, 6);
	}

}

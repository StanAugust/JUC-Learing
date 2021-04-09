package lock;

public class TestSynchronize {
	public static void main(String[] args) {
		
		Ticket ticket = new Ticket();
		
		new Thread(()->{
			for(int i=0; i<15; i++) {
				ticket.sale();
			}
		}, "A") .start();
		
		new Thread(()->{
			for(int i=0; i<15; i++) {
				ticket.sale();
			}
		}, "B") .start();
		
		new Thread(()->{
			for(int i=0; i<15; i++) {
				ticket.sale();
			}
		}, "C") .start();
		
	}
}

class Ticket{
	private int num = 30;
	
	public synchronized void sale() {
		if(num > 0) {
			System.out.println(Thread.currentThread().getName() + "卖出了第 " + num-- + " 张票");
		}
	}
}

import java.util.Scanner;
//100007090030020008009600500005300900010080002600004000300000010040000007007000300

public class Main {
	public static void main(String[] args){
		long time[]=new long[2];
		int itemp;
		Scanner scanner =new Scanner(System.in);
		System.out.println("Type in the bracket you want to solve:");
		
		Bracket orig=new Bracket(scanner.nextLine());
		System.out.println("How many brackets maximum do you want to print?");
		
		time[0]=System.currentTimeMillis();
		System.out.println("This is the original one:");
		orig.print();
		System.out.println();
		orig.solve();
		System.out.println("That's the one without guessing:");
		orig.print();
		time[1]=System.currentTimeMillis();
		itemp=orig.chepro();
		if(itemp==648){
			System.out.println("Perfect!");
		}else{
			System.out.println("It is "+itemp+"/648 finished");
		}
		System.out.println("It takes "+(time[1]-time[0])/1000.+" second.");
		if(itemp==648){
			return;
		}
		orig.deepsolve();
		System.out.println();
		itemp=orig.pBracket.size();
		System.out.println("Here are the all possibilities:");
		for(int t=0;t<itemp;t++){
			orig.pBracket.get(t).print();
			System.out.println();
		}
		time[1]=System.currentTimeMillis();
		System.out.println("It find "+Bracket.getnumb()+(Bracket.getnumb()==1?" solution":" solutions")+" with "+Bracket.getnumg()+" guesses.\n"+"It takes "+(time[1]-time[0])/1000.+" second total.");
	}
}

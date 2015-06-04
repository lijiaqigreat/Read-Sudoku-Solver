
public class WrongBracket extends Exception {
	int type,info;
	/**
	 * 
	 * @param t
	 * 0:unknown<br>
	 * 1:wrong size<br>
	 * 2:wrong char<br>
	 * 
	 * @param i
	 */
	public WrongBracket(int t,int i){
		type =t;
		info=i;
	}
	@Override
	public void printStackTrace(){
		
	}
}

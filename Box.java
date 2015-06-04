import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Label;

import javax.swing.ImageIcon;

/**
 * This class represent a single box in a bracket.
 * It does not contain any information about where it is.
 * Topically, a bracket contains 9*9=81 boxes.
 * 
 * 
 * @author Jiaqi
 * 
 */
public class Box extends Container{
	public static final int
			S_GRID									= -1,
			S_NORMAL = 0,
			S_SAME_NUMBER = 1,
			M_NORMAL = 0,
			M_PAST_GUESS = 1,
			M_CURRENT_GUESS = 2,
			M_LOCK = 3,
			B_NORMAL = 0,
			B_METHOD1 = 1,
			B_METHOD2 = 2,
			B_CHANGE = 3;
	public static final Font font=new Font("Consolas",Font.BOLD,12);
	public static final Color[]	stateC	=
	{
			Color.BLACK,
			new Color(127, 0, 0)
	},
			modeC = {
			Color.WHITE,
			new Color(127, 127, 0)
	},
			borderC = {
			new Color(50, 50, 50),
			new Color(100, 100, 255),
			Color.RED
	};
	public static final Color TSC=Color.RED,FSC=Color.GREEN,SC=Color.BLUE;
	int									n;
	private boolean[]					b			= new boolean[9];
	byte									mod, state, border;
	boolean								mouse;
	Label									l;
	Container							g;
	Label[]								gL;
	Bracket								bracket;
	private Image					stateI,borderI;
	private Color					modC;
	
	/**
	 * create a box by putting in a number
	 * 
	 * @param br
	 *        a bracket the box belongs to
	 * @param n
	 *        the number you want to put.
	 *        0 means 1 in real life and -1 mean unknown in real life.
	 */
	public Box(Bracket br,int n) {
		this(br);
		load(n);
	}
	
	/**
	 * create a box with 1-9 all being possible,
	 * which most boxes initially is.
	 * 
	 * @param br
	 *        a bracket the box belongs to
	 */
	public Box(Bracket br) {
		//set bracket
		bracket = br;
		//set container
		setSize(bracket.boxF, bracket.boxF);
		//set data
		setS(0);
		setM(0);
		setB(0);
		n = -1;
		mouse = false;
	}
	
	/**
	 * load a number in.
	 * also set the mode and state and border to normal
	 * 
	 * @param n
	 */
	public void load(int n) {
		setN(n);
		if(n!=-1){
			for(int t = 0; t<9; t++){
				change(t, false);
			}
			setM(M_LOCK);
			change(n);
			setS(0);
		}else{
			setM(0);
			setS(0);
		}
	}
	
	/**
	 * load a number in and set mode, state and border appropriately.
	 * 
	 * @param b
	 *        whether impossible for number 1-9
	 * @param mod
	 *        the mode you want to set
	 */
	public void load(boolean[] b,int mod) {
		for(int t = 0; t<9; t++){
			change(t, b[t]);
		}
		setM(mod);
	}
	
	/**
	 * set the mode.
	 * 
	 * @param i
	 *        the mode you want to set.
	 */
	public void setM(int i) {
		mod = (byte) i;
		modC=bracket.modC[i];
	}
	
	/**
	 * set the state.
	 * 
	 * @param i
	 *        the mode you want to set.
	 */
	public void setS(int s) {
		stateI=bracket.stateI[s];
		state = (byte) s;
	}
	
	public void setB(int b){
		border=(byte) b;
		borderI=bracket.borderI[b];
	}
	/**
	 * return if a number is impossible in this grid.
	 * 
	 * @param t
	 *        has to be 0-8
	 * @return true for impossible, false for possible.
	 */
	public boolean get(int t) {
		return b[t];
	}
	
	/**
	 * change the boolean of a number. (true to false, false to true)
	 * also change to grid and score.
	 * @param t5
	 *        has to be 0-8
	 */
	public void change(int t5) {
		b[t5] ^= true;
		score();
	}
	
	/**
	 * set the boolean of a number into certain boolean.
	 * 
	 * @param t5
	 *        the number you want. has to be 0-8
	 * @param boo
	 *        the boolean you want to set to.
	 * @return true if you changed the boolean.
	 */
	public boolean change(int t5,boolean boo) {
		if(get(t5)^boo){
			change(t5);
			return true;
		}
		return false;
	}
	
	/**
	 * Recognize the number according to the boolean array.
	 * 
	 * @return the number of numbers that are true(impossible)
	 *         return -1 if every number is impossible.
	 *         so the range is -1 to 8.
	 * 
	 */
	public int score() {
		int f = 0;
		int tt = -1;
		for(int t = 0; t<9; t++){
			if(b[t]){
				f++;
			}else{
				tt = t;
			}
		}
		switch(f){
		case 9:
			setN(-2);
			return -1;
		case 8:
			setN(tt);
		break;
		default:
			setN(-1);
		}
		return f;
	}
	
	/**
	 * set n to a number and change the big label. that's it.
	 * -2 means error
	 * @param n2
	 */
	private void setN(int n2) {
		n = n2;
	}
	@Override
	public void paint(Graphics gg){
		Graphics2D g=(Graphics2D) gg;
		g.drawImage(stateI,0,0,null);
		if(state>0){
			g.setColor(modC);
			g.drawString(n+1+"",(float)(getWidth()/2+bracket.BX[n]),(float)(getHeight()/2+bracket.BY[n]));
		}else{
			int t;
			int n=bracket.Ngrid5+bracket.Ngrid4;
			double in=bracket.Ngrid3+bracket.Ngrid5/2.;
			for(int t1=0;t1<3;t1++){
				for(int t2=0;t2<3;t2++){
					t=t1+t2*3;
					g.drawImage(b[t]?bracket.numberST:bracket.numberSF,bracket.Ngrid3+t1*n,bracket.Ngrid3+t2*n,null);
					g.setFont(bracket.FnumberS);
					g.drawString(t+1+"",(float)(in+t1*n+bracket.SX[t]),(float)(in+t2*n+bracket.SY[t]));
				}
			}
		}
		g.drawImage(borderI,0,0,null);
	}
}

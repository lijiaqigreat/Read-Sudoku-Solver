import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Label;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.*;

import javax.swing.JPanel;
//true:impossible, false:possible
//100007090030020008009600500005300900010080002600004000300000010040000007007000300
/**
 * The main Panel
 */
public class Bracket extends JPanel {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 729078253260675417L;
	/**
	 * the final possible brackets
	 */
	public List<MiniB> pBracket;
	/**
	 * all the commands 
	 */
	private PriorityQueue<Command> history;
	/**
	 * brackets for guessing
	 */
	PriorityQueue<MiniB> tree;
	/**
	 * the main boxes
	 */
	Box[][][][] b = new Box[3][3][3][3];
	/**
	 * the full score for perfect bracket.
	 */
	final public static int fullPro = 648;
	/**
	 * ???
	 */
	private int progress = 0;
	/**
	 * The last guessing box that current progress is based on
	 */
	private Box guessB;
	/**
	 * The number that guess box has guessed.
	 */
	private int guess = -1;
	/**
	 * parent App
	 */
	public App app;
	/** 
	 * out,big line,middle line,border,small line
	 */
	int Ngrid0, Ngrid1, Ngrid2, Ngrid3, Ngrid4, Ngrid5;
	/**
	 * size of ???,box,bracket
	 */
	int unitF,boxF,brF;
	/**
	 * font for booleans
	 */
	Font FnumberS;
	/**
	 * font for numbers
	 */
	Font FnumberB;
	//TODO what?
	double[] SX,SY,BX,BY;
	/**
	 * colors for small number, grid0,grid1,grid2,grid4,
	 */
	Color CnumberS,Cgrid0,Cgrid1,Cgrid2,Cgrid4;
	Image numberST;
	Image numberSF;
	public Color[] modC;
	public Image[] stateI,borderI;
	/**
	 * A change operation
	 * @author jiaqi
	 *
	 */
	public abstract class Command {
		//TODO identify types
		int type;
		/**
		 * count the number of boxes that is changed.
		 */
		int size = 0;
		/**
		 * the main box about this change
		 */
		Box box;

		public void pack() throws Break {
			if (size != 0) {
				history.add(this);
				change();
				throw new Break();
			}
		}
		/**
		 * Make the change to the grid
		 */
		public abstract void change();

	}
	/**
	 * A change in one box
	 * @author jiaqi
	 *
	 */
	public class SmallCommand extends Command {
		boolean[] t5 = new boolean[9];

		public SmallCommand(int t1, int t2, int t3, int t4, int tt, int type) {
			this(b[t1][t2][t3][t4], tt, type);
		}

		public SmallCommand(Box bbox, int tt, int ty) {
			box = bbox;
			type = ty;
			boolean[] tem = new boolean[9];
			tem[tt] = true;
			load(tem);
		}

		public SmallCommand(Box bbox, boolean[] boo, int ty) {
			type = ty;
			box = bbox;
			size = 0;
			load(boo);
		}
		/**
		 * This is used in manual change boolean.
		 * change a certain boolean (possibility)
		 * @param bb the certain box
		 * @param tt 
		 */
		public SmallCommand(Box bb, int tt) {
			type = 1;
			this.box = bb;
			t5[tt] = true;
			size = 1;
		}
		/**
		 * A nicer version for SmallComand(Box,int)
		 */
		public SmallCommand(int t1, int t2, int t3, int t4, int t5) {
			this(b[t1][t2][t3][t4], t5);
		}
		/**
		 * set t5 base on the final boolean
		 * @param boo
		 */
		public void load(boolean[] boo) {
			for (int t = 0; t < 9; t++) {
				if (boo[t] ^ box.get(t)) {
					t5[t] = true;
					size++;
				}
			}
		}

		@Override
		public void change() {
			for (int t = 0; t < 9; t++) {
				if (t5[t]) {
					box.change(t);
				}
			}
		}

	}

	public class BigCommand extends Command {
		private int type, size;
		boolean[] bo = new boolean[729];

		public BigCommand() {
			this(-1);
		}

		public BigCommand(int t) {
			type = t;
			size = 0;
		}

		public void add(int t1, int t2, int t3, int t4, int t5, boolean bb) {
			Box tem = b[t1][t2][t3][t4];
			int temI = toInt(t1, t2, t3, t4, t5);
			if (tem.get(t5) ^ bb && !bo[temI]) {
				bo[temI] = true;
				size++;
			} else if (!tem.get(t5) ^ bb && bo[temI]) {
				bo[temI] = false;
				size--;
			}
		}

		public void pack() throws Break {
			if (size == 0) {
				return;
			}
			change();
			history.add(this);
			throw new Break();
		}

		public void change() {
			for (int t1 = 0; t1 < 3; t1++) {
				for (int t2 = 0; t2 < 3; t2++) {
					for (int t3 = 0; t3 < 3; t3++) {
						for (int t4 = 0; t4 < 3; t4++) {
							for (int t5 = 0; t5 < 9; t5++) {
								if (bo[toInt(t1, t2, t3, t4, t5)]) {
									b[t1][t2][t3][t4].change(t5);
								}
							}
						}
					}
				}
			}
		}

	}
	/**
	 * a bracket only for saving
	 * @author jiaqi
	 *
	 */
	public class MiniB {
		boolean[] bo = new boolean[729];
		byte[] guessbbb;
		Box box;
		int g;

		public MiniB() {
			this(guessB);
		}

		public MiniB(Box guessB) {
			this.box = guessB;
			g = 9;
			for (int t = guess; t < 9; t++) {
				if (!guessB.get(t)) {
					g = t;
					break;
				}
			}
			for (int t1 = 0; t1 < 3; t1++) {
				for (int t2 = 0; t2 < 3; t2++) {
					for (int t3 = 0; t3 < 3; t3++) {
						for (int t4 = 0; t4 < 3; t4++) {
							for (int t5 = 0; t5 < 9; t5++) {
								bo[toInt(t1, t2, t3, t4, t5)] = b[t1][t2][t3][t4]
										.get(t5);
							}
						}
					}
				}
			}
		}

		public void set() throws Break {
			BigCommand f = new BigCommand(3);
			for (int t = 0; t < 729; t++) {
				f.bo[t] = b[t / 243][(t / 81) % 3][(t / 27) % 3][(t / 9) % 3]
						.get(t % 9) ^ bo[t];
			}
			guess = g;
			guessB = box;
			f.change();
			f.pack();
		}
	}

	static int toInt(int t1, int t2, int t3, int t4, int t5) {
		return toInt(t1, t2, t3, t4) * 9 + t5;
	}

	static int toInt(int t1, int t2, int t3, int t4) {
		return t1 * 27 + t2 * 9 + t3 * 3 + t4;
	}

	// input scanner
	public Bracket(App a) {
		super();
		app = a;
		//set numbers
		Ngrid0=5;
		Ngrid1=3;
		Ngrid2=3;
		Ngrid3=2;
		Ngrid4=1;
		Ngrid5=13;
		unitF=16;
		boxF=Ngrid3*2+Ngrid4*2+Ngrid5*3;
		brF=Ngrid0*2+Ngrid1*2+Ngrid2*6+boxF*9;
		setSize(brF,brF);
		FnumberS=new Font("Consolas",Font.BOLD,12);
		FnumberB=new Font("Consolas",Font.BOLD,40);
		SX=new double[9];
		SY=new double[9];
		BX=new double[9];
		BY=new double[9];
		FontRenderContext temF=new FontRenderContext(new AffineTransform(),true,true);
		Rectangle2D r;
		for(int t=0;t<9;t++){
			r=FnumberB.getStringBounds(t+1+"", temF);
			BX[t]=-r.getWidth()/2;
			BY[t]=-r.getHeight()/2;
			r=FnumberS.getStringBounds(t+1+"", temF);
			SX[t]=-r.getWidth()/2;
			SY[t]=-r.getHeight()/2;
		}
		//set colors
		//set state
		stateI=new Image[3];
		//set mode;
		modC=new Color[4];
		modC[0]=Color.WHITE;
		modC[1]=new Color(180,180,0);
		modC[2]=Color.YELLOW;
		modC[3]=new Color(180,180,180);
		//set border
		borderI=new Image[3];
		//TODO load image
		//set grid border
		Cgrid0=Color.WHITE;
		Cgrid1=new Color(180,180,180);
		Cgrid2=new Color(130,130,130);
		Cgrid4=Color.BLUE;
	}

	public void load(String string) throws WrongBracket {
		switch (string.length()) {
		case 81:
			for (int t = 0; t < 81; t++) {
				if (string.charAt(t) < '1' || string.charAt(t) > '0') {
					throw new WrongBracket(2, t);
				}
			}
			Scanner scanner = new Scanner(string).useDelimiter("\\s*");
			for (byte t1 = 0; t1 < 3; t1++) {
				for (byte t2 = 0; t2 < 3; t2++) {
					for (byte t3 = 0; t3 < 3; t3++) {
						for (byte t4 = 0; t4 < 3; t4++) {
							b[t1][t2][t3][t4].load(scanner.nextByte() - 1);
						}
					}
				}
			}
			break;
		case 162:
			// TODO
			break;
		default:
			throw new WrongBracket(1, string.length());
		}
		pBracket = new ArrayList<MiniB>();
		history = new PriorityQueue<Command>();
		tree = new PriorityQueue<MiniB>();
	}
	/**
	 * Simple string looking for this grid
	 */
	public String print() {
		String code = "";
		code += "|---------|---------|---------|\n";
		for (byte t1 = 0; t1 < 3; t1++) {
			for (byte t2 = 0; t2 < 3; t2++) {
				code += "|";
				for (byte t3 = 0; t3 < 3; t3++) {
					for (byte t4 = 0; t4 < 3; t4++) {
						/*
						 * System.out.print(" " + (bracket[t1][t2][t3][t4] + 1)
						 * + " ");
						 */
						code += " " + (b[t1][t2][t3][t4].n + 1) + " ";
					}
					code += "|";
				}
				code += "\n";
			}
			code += "|---------|---------|---------|\n";
		}
		return code;
	}
	/**
	 * Simple String export for this grid (can be used for import)
	 */
	public String getCode() {
		String code = "";
		for (byte t1 = 0; t1 < 3; t1++) {
			for (byte t2 = 0; t2 < 3; t2++) {
				for (byte t3 = 0; t3 < 3; t3++) {
					for (byte t4 = 0; t4 < 3; t4++) {
						/*
						 * code += (bracket[t1][t2][t3][t4] + 1);
						 */
						code += (b[t1][t2][t3][t4].n + 1);
					}
				}
			}
		}
		return code;
	}
	/**
	 * Determine the progress.
	 * it calls Box.score()
	 * @return -1 means wrong bracket</br>
	 * 648 perfect bracket.
	 */
	public int chepro() {
		int temp;
		progress = 0;
		for (byte t1 = 0; t1 < 3; t1++) {
			for (byte t2 = 0; t2 < 3; t2++) {
				for (byte t3 = 0; t3 < 3; t3++) {
					for (byte t4 = 0; t4 < 3; t4++) {
						/*
						 * temp = 0; for (byte t5 = 0; t5 < 9; t5++) { if
						 * (bBracket[t1][t2][t3][t4][t5] == true) { temp += 1;
						 * progress += 1; } } if (temp == 9) { progress = -1;
						 * return -1; }
						 */
						temp = b[t1][t2][t3][t4].score();
						if (temp == -1) {
							progress = -1;
							return -1;
						} else {
							progress += temp;
						}
					}
				}
			}
		}
		return progress;
	}

	/**
	 * Solve without guessing
	 * @return 0: perfect<br>
	 *         1: wrong<br>
	 *         2: did nothing<br>
	 * @throws Break
	 */
	public int solve() throws Break {
		switch (progress) {
		case -1:
			return 1;
		case fullPro:
			return 0;
		}
		method1();
		method2();
		method3();
		method4();
		method5();
		/*
		 * allread();// fix everything
		 */
		return 2;
	}
	/**
	 * go back from wrong guessing.
	 * @throws Break
	 */
	public void backTree() throws Break {
		//TODO set the guess box? 
		try {
			tree.poll().set();
		} catch (NullPointerException e) {
		}
	}

	// with guess
	// TODO
	public void deepsolve() throws Break {
		switch (solve()) {
		case 0:
			this.pBracket.add(new MiniB());
		case 1:
			backTree();
			break;
		case 2:
			if (guessB == null) {
				guess();
			}
			tree.add(new MiniB());
			Command c = new SmallCommand(guessB, guess, 2);
			guessB = null;
			c.pack();
		}
	}

	// determine the position to guess
	Box guess() {
		Box box = null, tem;
		int num, max = 0;
		for (byte t1 = 0; t1 < 3; t1++) {
			for (byte t2 = 0; t2 < 3; t2++) {
				for (byte t3 = 0; t3 < 3; t3++) {
					for (byte t4 = 0; t4 < 3; t4++) {
						/*
						 * if (bracket[t1][t2][t3][t4] == -1) { num = 0; for
						 * (byte t5 = 0; t5 < 9; t5++) { if
						 * (!bBracket[t1][t2][t3][t4][t5]) { num++; } } if (num
						 * > max) { GP[0] = t1; GP[1] = t2; GP[2] = t3; GP[3] =
						 * t4; max = num; } }
						 */
						tem = b[t1][t2][t3][t4];
						num = tem.score();
						if (num < max) {
							box = tem;
							max = num;
						}
					}
				}
			}
		}
		// Safe
		if (box == null) {
			System.out.println("Wrong: guess return null");
		}
		guessB = box;
		guess = -1;
		return box;
	}

	// if a spot have clear number
	// then wipe out the number in the spots that share group with this spot
	void method1() throws Break {
		int currentLayer;
		for (byte t1 = 0; t1 < 3; t1++) {
			for (byte t2 = 0; t2 < 3; t2++) {
				for (byte t3 = 0; t3 < 3; t3++) {
					for (byte t4 = 0; t4 < 3; t4++) {
						/*
						 * if (bracket[t1][t2][t3][t4] != -1) { currentLayer =
						 * bracket[t1][t2][t3][t4]; /* for (byte tt1 = 0; tt1 <
						 * 3; tt1++) { for (byte tt2 = 0; tt2 < 3; tt2++) {
						 * bBracket[t1][t2][tt1][tt2][currentLayer] = true;
						 * bBracket[tt1][tt2][t3][t4][currentLayer] = true;
						 * bBracket[t1][tt1][t3][tt2][currentLayer] = true; } }
						 * bBracket[t1][t2][t3][t4][currentLayer] = false;
						 * 
						 * }
						 */
						currentLayer = b[t1][t2][t3][t4].n;
						if (currentLayer != -1) {
							changeM1(t1, t2, t3, t4, currentLayer);
						}
					}
				}
			}
		}
	}

	// if a certain number is possible only in one spot in a group
	// then wipe out other numbers in this spot
	void method2() throws Break {
		byte l11, l12, l21, l22, l31, l32, chx1, chx2, chx3;
		for (byte t1 = 0; t1 < 3; t1++) {
			for (byte t2 = 0; t2 < 3; t2++) {
				for (byte t5 = 0; t5 < 9; t5++) {
					l11 = 0;
					l12 = 0;
					l21 = 0;
					l22 = 0;
					l31 = 0;
					l32 = 0;
					chx1 = 0;
					chx2 = 0;
					chx3 = 0;
					for (byte t3 = 0; t3 < 3; t3++) {
						for (byte t4 = 0; t4 < 3; t4++) {
							// didn't copy
							if (!b[t1][t2][t3][t4].get(t5)) {
								chx1 += 1;
								l11 = t3;
								l12 = t4;
							}
							if (!b[t3][t4][t1][t2].get(t5)) {
								l21 = t3;
								l22 = t4;
								chx2 += 1;
							}
							if (!b[t1][t3][t2][t4].get(t5)) {
								l31 = t3;
								l32 = t4;
								chx3 += 1;
							}
						}
					}
					if (chx1 == 1) {
						/*
						 * set(t1,t2,l11,l12,t5);
						 */
						changeM2(t1, t2, l11, l12, t5, 0);
					}
					if (chx2 == 1) {
						/*
						 * set(l21,l22,t1,t2,t5);
						 */
						changeM2(l21, l22, t1, t2, t5, 1);
					}
					if (chx3 == 1) {
						/*
						 * set(t1,l31,t2,l32,t5);
						 */
						changeM2(t1, l31, t2, l32, t5, 2);
					}
				}
			}
		}
	}

	// TODO need improve?
	// if a certain number is only possible in one small group in a group
	// them wipe out this number in other spots that share another group
	void method3() throws Break {
		byte l1, l2, l3, l4, c11, c12, c21, c22, c31, c32, c41, c42;
		for (byte t1 = 0; t1 < 3; t1++) {
			for (byte t2 = 0; t2 < 3; t2++) {
				for (byte t5 = 0; t5 < 9; t5++) {
					c12 = 0;
					c22 = 0;
					c32 = 0;
					c42 = 0;
					l1 = 0;
					l2 = 0;
					l3 = 0;
					l4 = 0;
					for (byte t3 = 0; t3 < 3; t3++) {
						c11 = 0;
						c21 = 0;
						c31 = 0;
						c41 = 0;
						for (byte t4 = 0; t4 < 3; t4++) {
							// didn't copy
							if (!b[t1][t3][t2][t4].get(t5)) {
								c11 += 1;
							}
							if (!b[t1][t4][t2][t3].get(t5)) {
								c21 += 1;
							}
							if (!b[t1][t2][t3][t4].get(t5)) {
								c31 += 1;
							}
							if (!b[t3][t4][t1][t2].get(t5)) {
								c41 += 1;
							}
						}
						if (c11 != 0) {
							c12 += 1;
							l1 = t3;
						}
						if (c21 != 0) {
							c22 += 1;
							l2 = t3;
						}
						if (c31 != 0) {
							c32 += 1;
							l3 = t3;
						}
						if (c41 != 0) {
							c42 += 1;
							l4 = t3;
						}
					}
					if (c12 == 1) {
						/*
						 * for (byte t3 = 0; t3 < 3; t3++) { if (t3 != t2) { for
						 * (byte t4 = 0; t4 < 3; t4++) {
						 * bBracket[t1][l1][t3][t4][t5] = true; } } }
						 */
						changeM3(t1, t2, l1, t5, 0);
					}
					if (c22 == 1) {
						/*
						 * for (byte t3 = 0; t3 < 3; t3++) { if (t3 != t1) { for
						 * (byte t4 = 0; t4 < 3; t4++) {
						 * bBracket[t3][t4][t2][l2][t5] = true; } } }
						 */
						changeM3(t1, t2, l2, t5, 1);
					}
					if (c32 == 1) {
						/*
						 * for (byte t3 = 0; t3 < 3; t3++) { if (t3 != t2) { for
						 * (byte t4 = 0; t4 < 3; t4++) {
						 * bBracket[t1][t3][l3][t4][t5] = true; } } }
						 */
						changeM3(t1, t2, l3, t5, 2);
					}
					if (c42 == 1) {
						/*
						 * for (byte t3 = 0; t3 < 3; t3++) { if (t3 != t2) { for
						 * (byte t4 = 0; t4 < 3; t4++) {
						 * bBracket[l4][t4][t1][t3][t5] = true; } } }
						 */
						changeM3(t1, t2, l4, t5, 3);
					}
				}
			}
		}
	}

	// if there are only two spots that fit two certain numbers in a group
	// them wipe out other numbers in these two spots
	void method4() throws Break {
		byte l11, l12, l21, l22, l31, l32, l13, l14, l23, l24, l33, l34, c1, c2, c3;
		for (byte t1 = 0; t1 < 3; t1++) {
			for (byte t2 = 0; t2 < 3; t2++) {
				for (byte t5 = 0; t5 < 9; t5++) {
					for (byte t6 = (byte) (t5 + 1); t6 < 9; t6++) {
						l11 = 0;
						l12 = 0;
						l21 = 0;
						l22 = 0;
						l31 = 0;
						l32 = 0;
						l13 = 0;
						l14 = 0;
						l23 = 0;
						l24 = 0;
						l33 = 0;
						l34 = 0;
						c1 = 0;
						c2 = 0;
						c3 = 0;
						for (byte t3 = 0; t3 < 3; t3++) {
							for (byte t4 = 0; t4 < 3; t4++) {
								// didn't copy
								if (!b[t1][t2][t3][t4].get(t5)
										|| !b[t1][t2][t3][t4].get(t6)) {
									c1++;
									l11 += t3;
									l12 += t4;
									l13 = t3;
									l14 = t4;
								}
								if (!b[t1][t3][t2][t4].get(t5)
										|| !b[t1][t3][t2][t4].get(t6)) {
									c2++;
									l21 += t3;
									l22 += t4;
									l23 = t3;
									l24 = t4;
								}
								if (!b[t3][t4][t1][t2].get(t5)
										|| !b[t3][t4][t1][t2].get(t6)) {
									c3++;
									l31 += t3;
									l32 += t4;
									l33 = t3;
									l34 = t4;
								}
							}
						}

						if (c1 == 2) {
							l11 -= l13;
							l12 -= l14;
							/*
							 * for (byte t7 = 0; t7 < 9; t7++) { if (t7 != t5 &&
							 * t7 != t6) { bBracket[t1][t2][l11][l12][t7] =
							 * true; bBracket[t1][t2][l13][l14][t7] = true; } }
							 */
							changeM4(t1, t2, l11, l12, l13, l14, t5, t6, 0);
						}
						if (c2 == 2) {
							l21 -= l23;
							l22 -= l24;
							/*
							 * for (byte t7 = 0; t7 < 9; t7++) { if (t7 != t5 &&
							 * t7 != t6) { bBracket[t1][l21][t2][l22][t7] =
							 * true; bBracket[t1][l23][t2][l24][t7] = true; } }
							 */
							changeM4(t1, t2, l21, l22, l23, l24, t5, t6, 1);
						}
						if (c3 == 2) {
							l31 -= l33;
							l32 -= l34;
							/*
							 * for (byte t7 = 0; t7 < 9; t7++) { if (t7 != t5 &&
							 * t7 != t6) { bBracket[l31][l32][t1][t2][t7] =
							 * true; bBracket[l33][l34][t1][t2][t7] = true; } }
							 */
							changeM4(t1, t2, l31, l32, l33, l34, t5, t6, 2);
						}
					}
				}
			}
		}
	}

	// if there are two spots that only fit two certain numbers in a group
	// them wipe out these numbers in the spots in this group
	void method5() throws Break {
		byte c11, c21, c31, c12, c22, c32, l11, l12, l13, l14, l21, l22, l23, l24, l31, l32, l33, l34;
		for (byte t1 = 0; t1 < 3; t1++) {
			for (byte t2 = 0; t2 < 3; t2++) {
				for (byte t5 = 0; t5 < 9; t5++) {
					for (byte t6 = (byte) (t5 + 1); t6 < 9; t6++) {
						l11 = 0;
						l12 = 0;
						l21 = 0;
						l22 = 0;
						l31 = 0;
						l32 = 0;
						l13 = 0;
						l14 = 0;
						l23 = 0;
						l24 = 0;
						l33 = 0;
						l34 = 0;
						c12 = 0;
						c22 = 0;
						c32 = 0;
						for (byte t3 = 0; t3 < 3; t3++) {
							for (byte t4 = 0; t4 < 3; t4++) {
								c11 = 0;
								c21 = 0;
								c31 = 0;
								for (byte t7 = 0; t7 < 9; t7++) {
									if (t7 != t5 && t7 != t6) {
										if (!b[t1][t2][t3][t4].get(t7)) {
											c11++;
										}
										if (!b[t3][t4][t1][t2].get(t7)) {
											c21++;
										}
										if (!b[t1][t3][t2][t4].get(t7)) {
											c31++;
										}
									}
								}
								if (c11 == 0) {
									c12++;
									l11 += t3;
									l12 += t4;
									l13 = t3;
									l14 = t4;
								}
								if (c21 == 0) {
									c22++;
									l21 += t3;
									l22 += t4;
									l23 = t3;
									l24 = t4;
								}
								if (c31 == 0) {
									c32++;
									l31 += t3;
									l32 += t4;
									l33 = t3;
									l34 = t4;
								}
							}
						}
						l11 -= l13;
						l12 -= l14;
						l21 -= l23;
						l22 -= l24;
						l31 -= l33;
						l32 -= l34;
						/*
						 * for (byte t3 = 0; t3 < 3; t3++) { for (byte t4 = 0;
						 * t4 < 3; t4++) { if (c12 == 2 && (t3 != l13 || t4 !=
						 * l14) && (t3 != l11 || t4 != l12)) {
						 * bBracket[t1][t2][t3][t4][t5] = true;
						 * bBracket[t1][t2][t3][t4][t6] = true; } if (c22 == 2
						 * && (t3 != l23 || t4 != l24) && (t3 != l21 || t4 !=
						 * l22)) { bBracket[t3][t4][t1][t2][t5] = true;
						 * bBracket[t3][t4][t1][t2][t6] = true; } if (c32 == 2
						 * && (t3 != l33 || t4 != l34) && (t3 != l31 || t4 !=
						 * l32)) { bBracket[t1][t3][t2][t4][t5] = true;
						 * bBracket[t1][t3][t2][t4][t6] = true; } } }
						 */
						if (c12 == 2) {
							changeM5(t1, t2, l11, l12, l13, l14, t5, t6, 0);
						}
					}
				}
			}
		}
	}

	private void changeM1(int t1, int t2, int t3, int t4, int t5) throws Break {
		BigCommand c = new BigCommand(16);
		for (byte tt1 = 0; tt1 < 3; tt1++) {
			for (byte tt2 = 0; tt2 < 3; tt2++) {
				c.add(t1, t2, tt1, tt2, t5, true);
				c.add(tt1, tt2, t3, t4, t5, true);
				c.add(t1, tt1, t3, tt2, t5, true);
			}
		}
		c.add(t1, t2, t3, t4, t5, false);
		c.pack();
	}

	public void changeM2(int t1, int t2, int t3, int t4, int t5, int type)
			throws Break {
		SmallCommand c = new SmallCommand(t1, t2, t3, t4, t5, 20);
		c.pack();
	}

	private void changeM3(byte t1, byte t2, byte l, byte t5, int i)
			throws Break {
		BigCommand c = new BigCommand(12 + i);
		for (int t3 = 0; t3 < 3; t3++) {
			if (t3 != t2) {
				for (int t4 = 0; t4 < 3; t4++) {
					switch (i) {
					case 0:
						c.add(t1, l, t3, t4, t5, true);
						break;
					case 1:
						c.add(t3, t4, t2, l, t5, true);
						break;
					case 2:
						c.add(t1, t3, l, t4, t5, true);
						break;
					case 3:
						c.add(l, t4, t1, t3, t5, true);
						break;
					}
				}
			}
		}
		c.pack();
	}

	private void changeM4(byte t1, byte t2, byte l1, byte l2, byte l3, byte l4,
			byte t5, byte t6, int type) throws Break {
		BigCommand c = new BigCommand(16 + type);
		for (int t7 = 0; t7 < 9; t7++) {
			if (t7 != t5 && t7 != t6) {
				switch (type) {
				case 0:
					c.add(t1, t2, l1, l2, t7, true);
					c.add(t1, t2, l3, l4, t7, true);
					break;
				case 1:
					c.add(t1, l1, t2, l2, t7, true);
					c.add(t1, l3, t2, l4, t7, true);
					break;
				case 2:
					c.add(t1, t2, l1, l2, t7, true);
					c.add(t1, t2, l3, l4, t7, true);
					break;
				}
			}
		}
		c.pack();
	}

	private void changeM5(byte t1, byte t2, byte l1, byte l2, byte l3, byte l4,
			byte t5, byte t6, int type) throws Break {
		BigCommand c = new BigCommand(20 + type);
		for (int t3 = 0; t3 < 3; t3++) {
			for (int t4 = 0; t4 < 3; t4++) {
				if ((t3 != l1 || t4 != l2) && (t3 != l3 || t4 != l4)) {
					switch (type) {
					case 0:
						c.add(t1, t2, t3, t4, t5, true);
						c.add(t1, t2, t3, t4, t6, true);
						break;
					case 1:
						c.add(t3, t4, t1, t2, t5, true);
						c.add(t3, t4, t1, t2, t6, true);
						break;
					case 2:
						c.add(t1, t3, t2, t4, t5, true);
						c.add(t1, t3, t2, t4, t6, true);
						break;
					}
				}
			}
		}
		c.pack();
	}

	// print every spot's possible numbers
	public void fullprint() {
		System.out
				.println("-------------------------------------------------------------------------------------------------");
		for (byte t1 = 0; t1 < 3; t1++) {
			for (byte t2 = 0; t2 < 3; t2++) {
				for (byte t3 = 0; t3 < 3; t3++) {
					for (byte t4 = 0; t4 < 3; t4++) {
						System.out.print("|");
						for (byte t5 = 0; t5 < 3; t5++) {
							for (byte t6 = 0; t6 < 3; t6++) {
								if (b[t1][t2][t4][t5].get(t3 * 3 + t6) == false) {
									System.out.print(" " + (t3 * 3 + t6 + 1)
											+ " ");
								} else {
									System.out.print("   ");
								}
							}
							System.out.print("|");
						}
						System.out.print("|");
					}
					System.out.println("");
				}
				System.out
						.println("-------------------------------------------------------------------------------------------------");
			}
			System.out
					.println("-------------------------------------------------------------------------------------------------");
		}
	}

	/**
	 * @deprecated
	 */
	// set x to position t1,t2,t3,t4
	public void set(int t1, int t2, int t3, int t4, int x) throws Break {
		/*
		 * bracket[t1][t2][t3][t4] = (byte) x; if (bracket[t1][t2][t3][t4] !=
		 * -1) { for (byte t = 0; t < 9; t++) { bBracket[t1][t2][t3][t4][t] =
		 * true; } bBracket[t1][t2][t3][t4][bracket[t1][t2][t3][t4]] = false; }
		 */
		BigCommand c = new BigCommand(0);
		b[t1][t2][t3][t4].n = x;
		if (b[t1][t2][t3][t4].n != -1) {
			for (byte t = 0; t < 9; t++) {
				c.add(t1, t2, t3, t4, t, true);
			}
			c.add(t1, t2, t3, t4, b[t1][t2][t3][t4].n, false);
		}
		c.pack();
	}

}
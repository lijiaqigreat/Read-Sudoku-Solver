import java.applet.Applet;
import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Label;
import java.awt.TextField;

import javax.swing.ImageIcon;
import javax.swing.JApplet;



@SuppressWarnings("serial")
public class App extends JApplet {
	Container leftC,rightC;
	TextField consoleT,imputT;
	Container imputC;
	Button imputB;
	
	Bracket bracket;
	Bracket b;
	ImageIcon back;
	int rightF,Nhight1,Nhight2,Nimput;

	
	public void init(){
		//set "finals"
		
		//set units (number)
		rightF=100;
		Nhight1=60;
		Nhight2=30;
		Nimput=80;
		
		//TODO from small to big
		//set big container
		bracket=new Bracket(this);
		setSize(bracket.getWidth()+rightF,bracket.getWidth()+Nhight1+Nhight2);
		leftC=new Container();
		leftC.setSize(bracket.getWidth(),bracket.getWidth()+Nhight1+Nhight2);
		rightC=new Container();
		rightC.setSize(rightF,bracket.getWidth()+Nhight1+Nhight2);
		add(leftC);
		add(rightC);
		//set left container
		bracket=new Bracket(this);
		consoleT=new TextField();
		consoleT.setSize(bracket.getWidth(),Nhight1);
		imputC=new Container();
		imputC.setSize(bracket.getWidth(),Nhight2);
		imputT=new TextField();
		imputT.setSize(bracket.getWidth()-Nimput,Nhight1);
		imputB=new Button("set");
		imputB.setSize(Nimput,Nhight2);
		imputC.setLocation(0,0);
		imputC.add(imputT);
		imputC.setLocation(bracket.getWidth()-Nimput,0);
		imputC.add(imputB);
		leftC.setLocation(0,bracket.getWidth());
		leftC.add(consoleT);
		leftC.setLocation(0,bracket.getWidth()+Nhight1);
		add(leftC,0,0);
		
		
		
	}

}

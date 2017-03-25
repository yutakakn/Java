//'97/9/5 - 9/8
//
//ScoreBox.java
//【テト《スコアクラス》】　with JDK 1.0.2
//
//Copyright (C)1997 Yutaka Hirata
//All rights reserved.

import java.awt.*;
import java.applet.Applet;

//得点を表示するクラス
public class ScoreBox extends Canvas{
	private Image offImg;
	private Graphics offG;
	private int width,height;
	private int DD;
	private int score; //点数
	//ゲームレベルと、消した行による得点
	int point[][] = {
		{0,10, 50,100,200},
		{0,20,100,200,400},
		{0,30,150,300,600},
		{0,40,200,400,800},
		{0,50,250,500,1000},
		{0,60,300,600,1200},
		{0,70,350,700,1400},
		{0,80,400,800,1600},
		{0,90,500,900,1800},
		{0,100,600,1000,2000},
	};
	
	ScoreBox(int w,int h,Component parent){
		DD = Game.DD;  //1要素のサイズ
		score = 0;
		width = w; height = h;
		offImg = parent.createImage(w,h);
		offG = offImg.getGraphics();
		offG.setColor(Color.black);
		offG.fillRect(0,0,width,height);
	}
	
	int getScore(){
		return( score );
	}
	
	void setScore(int level,int line){
		score += point[level][line];
		repaint();
	}
	
	public void update(Graphics g){
		paint(g);
	}

	public void paint(Graphics g){
		offG.setColor(Color.black);
		offG.fillRect(0,0,width,height);
		offG.setColor(Color.orange);
		offG.drawRect(0,0,width-1,height-1);
		offG.setFont(new Font("TimesRoman",Font.BOLD,24));
		offG.setColor(Color.blue);
		offG.drawString("Score",5,30);
		offG.setColor(Color.white);
		offG.drawString(""+score,5,60);
		g.drawImage(offImg,Teto.width/2+40,20,this);
		//g.drawImage(offImg,Tetris.width/2,20+Game.ys*DD-100,this);
	}
	
	void disp(){
		System.out.println("Score="+score);
	}

}


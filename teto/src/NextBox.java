//'97/9/5 - 9/8
//
//NextBox.java
//【テト《ネクストクラス》】　with JDK 1.0.2
//
//Copyright (C)1997 Yutaka Hirata
//All rights reserved.

import java.awt.*;
import java.applet.Applet;
import java.awt.image.*;

//次のコマを表示するクラス
public class NextBox extends Applet{
	private Image offImg;
	private Graphics offG;
	private int width,height;
	private int DD;
	private Block next;
	private Color col[] = {Color.blue,Color.yellow,Color.green,Color.magenta,
					Color.cyan,Color.red,Color.lightGray};
	
	NextBox(int w,int h,Component parent){
		//img = new Image[7];
		//for(int i = 0 ; i < 7; i++) img[i] = org[i];

		DD = Game.DD;
		width = w; height = h;
		offImg = parent.createImage(w,h);
		offG = offImg.getGraphics();
		offG.setColor(Color.black);
		offG.fillRect(0,0,width,height);
	}
	
	void setNextBox(Block ne){
		int i,j,xs,ys;
		
		xs = ne.sizeX(); ys = ne.sizeY();
		next = new Block(xs,ys);
		for(j = 0 ; j < ys ; j++){
			for(i = 0 ; i < xs ; i++){
				next.block[i][j] = ne.block[i][j];
				//System.out.print(" "+next.block[i][j]);
			}
			//System.out.println("");
		}
		//System.out.println("");
		repaint();
	}
	
	public void update(Graphics g){
		paint(g);
	}

	public void paint(Graphics g){
		int x,y,xs,ys;
		
		offG.setColor(Color.black);
		offG.fillRect(0,0,width,height);
		offG.setColor(Color.orange);
		offG.drawRect(0,0,width-1,height-1);
		
		xs = next.sizeX(); ys = next.sizeY();
		//System.out.println("xs="+xs+" ys="+ys);
		for(y = 0 ; y < ys ; y++)
			for(x = 0 ; x < xs ; x++){
				int n;
				if( (n = next.block[x][y]) > 0){
					offG.setColor( col[n-1] );
					offG.fillRect(x*DD+DD,y*DD+DD,DD,DD);
					//offG.drawImage(img[n-1],x*DD+DD,y*DD+DD,this);
				}
			}
		g.drawImage(offImg,Teto.width/2+40,20+Game.ys*DD-100,this);
	}
	
}


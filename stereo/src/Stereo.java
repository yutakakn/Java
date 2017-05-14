//'97/10/16 - 10/19
//
//Stereo.java
//【ランダム・ドット・ステレオグラム】　with JDK 1.0.2
//
//《動作確認》
//　Appletviewer(JDK 1.0.2付属）
//　Internet Explorer 4.0
//
//《参考文献》
//　Internet Language(3)Ｊａｖａ入門,河西朝雄,技術評論社,1980円
//　アルゴリズム教科書,三上直樹,ＣＱ出版社,2700円
//　昔のN88-BASICのプログラム
//
//Copyright (C)1997 Yutaka Hirata
//All rights reserved.
/*
●使い方
<applet code="Stereo.class" width=500 height=350>
</applet>

●パラメータ
　特になし。
　
●遊びかた
　操作はすべてマウスで行います。
　プルダウンメニューから見たい種類のステレオグラムを選択します。
　startボタンを押すとランダムドットステレオグラムが表示されはじ
　めます。見方は平行法と呼ばれるものです。

 */

import java.applet.Applet;
import java.awt.*;

public class Stereo extends Applet implements Runnable
{
	//ダブルバッファ
	Image offImg;
	Graphics offG;
	int width,height;

	Thread thread = null;
	
	int selectNumber = 1;  //ステレオグラムの種類の選択
	int Loop;
	
	Button startBtn,stopBtn;
	Choice L;

	//パラメータを取得する（数値版）。
	public int param(String pname,int def){
		String val = getParameter(pname);
		return (val != null) ? Integer.parseInt(val) : def;
	}

	//パラメータを取得する（文字列版）。
	public String param(String pname,String def){
		String val = getParameter(pname);
		return (val != null) ? val : def;
	}

	public void init(){
		//ＧＵＩ配置
		L = new Choice();
		L.addItem("TYPE1"); L.addItem("TYPE2"); L.addItem("TYPE3");
		L.addItem("TYPE4"); L.addItem("TYPE5"); L.addItem("TYPE6");
		L.addItem("TYPE7"); L.addItem("TYPE8");
		Panel p = new Panel();
		p.add(L);
		p.add(startBtn = new Button("start"));
		p.add(stopBtn = new Button("stop"));
		p.setBackground(Color.black);
		stopBtn.disable();
		setLayout(new BorderLayout());
		add("South",p); //先頭は大文字！
		
		width = size().width; height = size().height;
		offImg = createImage(width,height);
		offG = offImg.getGraphics();
		offG.setColor(Color.black);
		offG.fillRect(0,0,width,height);
		repaint();
	}

	public void y_start(){
		if(thread == null){
			thread = new Thread(this);
			thread.start();
		}
	}
	
	public void stop(){
		if(thread != null){
			thread.stop();
			thread = null;
		}
	}
	
 	public void run(){
		while( true ){
			try{
				thread.sleep( 50 );
			}catch(InterruptedException e){};
			
			if(Loop < 1000){
				Loop++;
				sub();
			}
		}
	}

	public void paint(Graphics g){
		g.drawImage(offImg,0,0,this); //問題領域
	}

	public void update(Graphics g){
		paint(g);
	}
	
	void fastPaint(){
		paint(getGraphics());
	}
	
	//イベントハンドラ
	public boolean action(Event e,Object o){
		if(e.target == startBtn){
			startBtn.disable();
			stopBtn.enable();
			L.disable();
			offG.setColor(Color.black); offG.fillRect(0,0,width,height);
			Loop = 0;
			thread = null;
			y_start(); //スレッド開始
			
			sub();
			
			return true;
		}else if(e.target == stopBtn){
			startBtn.enable();
			stopBtn.disable();
			L.enable();
			stop(); //スレッド停止
			
			return true;
		}else if(e.target instanceof Choice){
			String s = (String)o; //選択肢を得る。
			selectNumber = Integer.parseInt( s.substring(4,5) );
			//System.out.println("select="+selectNumber);
			
			return true;
		}
		
		return false;
	}
	
	//---以下、描画関数群---

	//碁盤目状だと思う:-)
	double func8(double x,double y){
		return( 
			( (y/2 + 5) % 2 + (x/2 + 5) % 2 ) / 3
		);
	}

	//丸いものがぼこぼこ飛び出したりへこんだりしているような。
	double func7(double x,double y){
		return( Math.cos(x) * Math.cos(y/2) );
	}

	//×マークが右へ傾斜しているような。
	double func6(double x,double y){
		int t;
		
		t = (Math.abs(y + x) < 2 || Math.abs(y - x) < 2 ) ? 1:0;
		return( - t * x / 10 );
	}

	//ドーナッツが下方へ傾斜しているような。
	double func5(double x,double y){
		double r;
		int t;
		
		r = Math.sqrt(x*x + y*y);
		t = (r > 5 && r < 8) ? 1 : 0;
		return( - t * y / 8 );
	}

	//ドーナッツが交互に傾斜して広がっているような（言葉ではムズイ…）。
	double func4(double x,double y){
		double r;
		r = Math.sqrt(x*x + y*y);
		if(r >= 10.0) return( 0.0 );
		if(r % 4.0 > 2.0) return( y / 10.0 );
		else return( - y / 10.0 );
	}

	//波状
	double func3(double x,double y){
		return( Math.sin(x + Math.sin(y)) );
	}
	
	//円錐が突き出ているような。
	double func2(double x,double y){
		double r;
		
		r = Math.sqrt(x*x + y*y);
		if(r == 0.0) return( 1.0 );
		else return( Math.sin(r) / r );
	}
	
	//円が飛び出たような。
	double func1(double x,double y){
		return( 5.0 - Math.sqrt(x*x + y*y) );
	}
	
	double func(double x,double y){
		double ret;
		
		switch(selectNumber){
		case 1:
			ret = func1(x,y);
			break;
		case 2:
			ret = func2(x,y);
			break;
		case 3:
			ret = func3(x,y);
			break;
		case 4:
			ret = func4(x,y);
			break;
		case 5:
			ret = func5(x,y);
			break;
		case 6:
			ret = func6(x,y);
			break;
		case 7:
			ret = func7(x,y);
			break;
		case 8:
			ret = func8(x,y);
			break;
		default:
			ret = 0.0;
		}
		
		//-1.0～1.0に正規化
		if(ret > 1.0) ret = 1.0;
		if(ret < -1.0) ret = -1.0;
		return( ret );
	}
	
	//ステレオグラム描画ルーチン
	void sub(){
		double xmin,xmax,ymin,ymax;
		double period,r,x,y,z;

		//この辺の値は自由に変更可。
		xmax = 10; xmin = -10;
		ymax = 10; ymin = -10;
		period = (xmax - xmin) / 6.0; //周期
		r = 0.1 * period;
		y = Math.random() * (ymax - ymin) + ymin;
		z = func(xmin,y);
		x = xmin + Math.random() * (Math.abs(period + r*z));
		do{
			offG.setColor(Color.white);
			offG.fillRect((int)(x*15 + width/2),(int)(y*15 + height/2),2,2);
			fastPaint();
			z = func(x+period/2,y);
			x = x + period - r * z;
		}while(x <= xmax);
	}
}


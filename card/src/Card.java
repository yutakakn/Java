/* 2000/7/30 - 7/31 (Original 1997/9/15)
 *
 * Card.java    with Visual Cafe 3.0c(JDK 1.1)
 * [カードシャッフル]
 *
 * Copyright (C)2000 YUTAKA
 * All rights reserved.
 */

import java.awt.*;
import java.applet.*;
import java.awt.image.*;
import java.awt.event.*;

public class Card extends Applet implements Runnable
{
	Thread thread;
	Image offImg;
	Graphics offG;
	static int width,height;
	
	static final int CX = 60; //カードのサイズ
	static final int CY = 80;
	//スペード、クローバ、ダイヤ、ハートの画像
	Image cardImg[] = new Image[4];
	//ＧＵＩ部品
	Panel gPanel;
	Label gMoney;
	TextField gText;
	
	//各変数
	int clearMoney; //ゲームクリアに必要な金額
	int gold; //所持金
	int bet;  //賭け金
	int Answer; //答え（0から3）
	
	static final int STEP = 60;
	int step;
	
	//ゲーム変数
	int Len[] = new int[4];
	int Cx[][] = new int[4][STEP];
	int Cy[][] = new int[4][STEP];
	int tmpX[] = new int[4];
	int tmpY[] = new int[4];
	int dirX[] = new int[4];
	int dirY[] = new int[4];
	
	int finish; //カード停止フラグ
	
	boolean mCheck; //マウス受け付け許可フラグ
	boolean clearFlag; //ゲームクリアのフラグ
	boolean gameFlag; //アニメ中のフラグ
	boolean gameLastFlag; //終盤アニメのフラグ

	int delay; //ゲームスピード
	
	int debug; //隠しパラメータ（デバッグ用）
	
	//パラメータを取得する。
	public int param(String pname,int def){
		String val = getParameter(pname);
		return (val != null) ? Integer.parseInt(val) : def;
	}

	public String param(String pname,String def){
		String val = getParameter(pname);
		return (val != null) ? val : def;
	}

	public void init(){
		//カードの画像を読み込む。
		Image img = getImage(getDocumentBase(),"card.gif");
		MediaTracker mt = new MediaTracker(this);
		mt.addImage(img,0);
		try{
			mt.waitForID(0);
		}catch(InterruptedException e){}
		//4種類のカードに分ける。16画素ずつ並んでいる。
		for(int i = 0 ; i < 4 ;i++){
			cardImg[i] = 
				createImage(
				  new FilteredImageSource(img.getSource(),
				    new CropImageFilter(i*16,0,16,16)
				  )
				);
			mt.addImage(cardImg[i],1);
		}
		try{
			mt.waitForID(1);
		}catch(InterruptedException e){}
		
		thread = null;
		width = getSize().width;
		height = getSize().height;
		offImg = createImage(width,height);
		offG = offImg.getGraphics();
		//パラメータの取得
		getOpt();
		//初期化
		gameInit();
	}
	
	void getOpt(){
	    clearMoney = 10000;
		//clearMoney = param("clear",10000); 		//クリアに必要な金額
		delay = param("delay",50); //ディレイ
		debug = param("debug",0);
	}
	
	void gameInit(){
		gold = 200;
		mCheck = false;
		clearFlag = false;
		gameFlag = gameLastFlag = false;
		step = 0;
		
		//ＧＵＩ配置
		gPanel = new Panel();
		gPanel.add(new Label("合計"));
		gPanel.add(gMoney = new Label("  200"));
		gPanel.add(new Label("掛け金"));
		gPanel.add(gText = new TextField("100",6));
				
		setLayout(new BorderLayout());
		add("South",gPanel);
		gText.requestFocus();
				
		offG.setColor(Color.black);
		offG.fillRect(0,0,width,height);
		cardDisp(); fastDisp();
		
		//イベントハンドラの登録
		gText.addActionListener(new InputListener());
		addMouseListener(new SelectListener());
	}
	
	class SelectListener extends MouseAdapter {
	    public void mouseClicked(MouseEvent e) {
	        selectCard(e);
	    }
	}
	
	class InputListener implements ActionListener {
	    public void actionPerformed(ActionEvent e) {
	        inputGoldListener(e);
	    }
	}
	
	//掛け金の入力時に Enter で呼ばれる。
	void inputGoldListener(ActionEvent e) {
		if (clearFlag) return;
		    
		TextField t = (TextField)e.getSource();
		String s = t.getText();
		try {
			bet = Integer.parseInt(s);
		} catch (NumberFormatException exp){
			//数値に変換できなかった時。
			return;
		}
		gText.setText("");
		bet /= 100; bet *= 100;  //100円単位にする。
		//レンジチェック
		if(bet > gold || bet < 0 || bet < 100) return;
		animeStart();
	}
	
	//カードの選択時に マウスクリック で呼ばれる。
	public void selectCard(MouseEvent e) {
		int i, n, x, y;
		    
		x = e.getX();
		y = e.getY();
		    
		//マウスイベントが受け付け許可かどうか。
		if(!mCheck) return;
		mCheck = false;
    		
		n = -1;
		for(i = 0 ; i < 4; i++)
			if( (20 + (20+CX)*i) <= x && x <= (20+(20+CX)*i+CX) ){
				n = i; break;
			}
		if(i == 4) return; //範囲外
    		
		offG.setColor(Color.black); offG.fillRect(0,0,width,height);
		answerDisp();
    		
		if(n == Answer){ //正解！！
			gold += bet;
			if(gold >= clearMoney){ 
				gameClear();
			}
		}else{
			gold -= bet;
			if(gold <= 0){ 
				gold = 200;
				offG.setFont(new Font("TimesRoman",Font.PLAIN,24));
				offG.setColor(Color.cyan);
				offG.drawString("ゲームオーバー",100,150);
				// this.wait(300);
			}
		}
		gameFlag = gameLastFlag = false;
		step = 0;
		gMoney.setText(""+gold);
		//gPanel.show();
		gText.setEnabled(true);
		gText.requestFocus();

		//this.wait(300);
		cardDisp();		    
	}
	
	//ゲームクリア
	void gameClear(){
		clearFlag = true;
		offG.setColor(Color.black); offG.fillRect(0,0,width,height);
		offG.setFont(new Font("TimesRoman",Font.BOLD,24));
		offG.setColor(Color.green);
		offG.drawString("Congratulations!",20,150);
		offG.drawString("Password: CardMaster",20,170);
		repaint();
	}

	public void start(){
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

	public void update(Graphics g){
		paint(g);
	}
	
	public void paint(Graphics g){
		g.drawImage(offImg,0,0,this);
	}
	
	void fastDisp(){
		paint(getGraphics());
	}

	void wait(int msec){
		try{
			Thread.sleep(msec);
		}catch(InterruptedException e){}
	}

 	public void run(){
		while( true ){
			try{
				thread.sleep( delay );         //ウェイト
			}catch(InterruptedException e){};
			
			doIt();
			repaint();        //再描画
		}
	}
	
	//カード描画
	void oneCardDisp(int x,int y,int n){
		offG.setColor(Color.white);
		offG.drawRoundRect(x,y,CX,CY,3,3);
		offG.drawImage(cardImg[n],x+2,y+2,null);
		offG.drawImage(cardImg[n],x+CX-2-16,y+CY-2-16,null);
		repaint();
	}
	
	//4枚分のカードの描画
	void cardDisp(){
		for(int i = 0 ; i < 4 ; i++){
			oneCardDisp(20+(20+CX)*i,20,i);
			//fastDisp();
		}
	}
	
	//解答カードの描画
	void answerDisp(){
		for(int i = 0 ; i < 4 ; i++){
			oneCardDisp(Cx[i][STEP-1],Cy[i][STEP-1],i);
			fastDisp();
			wait(200);
		}
	}

	//（裏向き）カード描画
	void oneCardReverseDisp(int x,int y){
		int i,j,sw,sh,wn,hn;

		//offG.setColor(Color.white);
		offG.drawRoundRect(x,y,CX,CY,3,3);
		
		Font fnt = new Font("Helvetica",Font.PLAIN,9);
		offG.setFont(fnt);
		FontMetrics	fm = getFontMetrics(fnt);
		sw = fm.stringWidth("X");	sh = fm.getHeight();
		wn = CX / sw; hn = CY / sh;
		
		for(j = 0 ; j < hn ;j++)
			for(i = 1 ; i < wn - 1; i++){
				offG.drawString("X",x + i*sw,y + j*sh+sh);
			}
	}

	//4枚分の裏向きカード描画
	void cardReverseDisp(){
		offG.setColor(Color.black); offG.fillRect(0,0,width,height);
		for(int i = 0 ; i < 4 ; i++){
			offG.setColor(Color.white);
			if(debug == 1 && i == 0){ //デバッグ用
				offG.setColor(Color.red);
			}
			oneCardReverseDisp(tmpX[i],tmpY[i]);
		}
		repaint();
	}
	
	//アニメ前の準備
	void animeStart(){
		int i,j,t;
		
		for(i = 0 ;i < 4;i++){
			Len[i] = getRandom(30)+10;
			dirX[i] = getRandom(3) - 1;
			dirY[i] = getRandom(3) - 1;
		}
		
		//ランダムで各ステップの位置を決める。
		for(j = 0 ; j < STEP - 1 ; j++)
			for(i = 0 ; i < 4 ; i++){
				Cx[i][j] = getRandom(200) + 160;
				Cy[i][j] = getRandom(70) + 30;
			}
		//最終位置
		for(i = 0 ; i < 4 ; i++){
			Cx[i][STEP-1] = tmpX[i] = 20 + (20+CX)*i;
			Cy[i][STEP-1] = tmpY[i] = 20;
		}
		//スペードの最終位置を決定。
		Answer = getRandom(4);
		//入れ替え。
		t = Cx[0][STEP-1]; 
		Cx[0][STEP-1] = Cx[Answer][STEP-1];
		Cx[Answer][STEP-1] = t;
		gameFlag = true;
		step = 0;
		finish = 0;
		gameLastFlag = false;
		
		gText.setEnabled(false);
		
//		gPanel.hide(); //ＧＵＩ部品を消す。
		cardReverseDisp();
	}
	
	//スレッドrun()から呼ばれるメソッド。ここではアニメーションに
	//使っています。
	void doIt(){
		if(gameFlag){
			int i;
			
			step++; //ステップを進める。
			if(step >= STEP-1){
				gameLastFlag = true;
				gameFlag = false;
				finish = 0;
				return;
			}
			for(i = 0 ; i < 4; i++){
				//ゲーム中盤を過ぎると難易度を上げる。
				if(gold < 4000){
					Len[i]--;
					if(Len[i] < 0 || tmpX[i] < 160 || tmpX[i] > 360 ||
						tmpY[i] < 30 || tmpY[i] > 100){
						Len[i] = getRandom(30)+10;
						dirX[i] = intcmp(Cx[i][step],tmpX[i]); 
						dirY[i] = intcmp(Cy[i][step],tmpY[i]);
					}
				}else{
					dirX[i] = intcmp(Cx[i][step],tmpX[i]); 
					dirY[i] = intcmp(Cy[i][step],tmpY[i]);
				}
				tmpX[i] += dirX[i]*8; tmpY[i] += dirY[i]*8;
			}
			cardReverseDisp();
		}else if(gameLastFlag){
			if(finish == 0xf){ //カードがすべて止まった場合
				offG.setFont(new Font("TimesRoman",Font.PLAIN,20));
				offG.setColor(Color.blue);
				offG.drawString("スペードはどれ？",20,200);
				repaint();
				mCheck = true;
				gameLastFlag = false;
			
			}else{
				int i,p,q;
				
				for(i = 0 ; i < 4; i++){
					p = intcmp(Cx[i][STEP-1],tmpX[i]); //-1,0,1を返す。
					q = intcmp(Cy[i][STEP-1],tmpY[i]);
					tmpX[i] += p*8; tmpY[i] += q*8;
					oneCardReverseDisp(tmpX[i],tmpY[i]);
					if(p == 0 && q == 0)
						if( (finish & (1<<i)) == 0 ) finish |= (1<<i);
				}
				cardReverseDisp();
			}
		}
	}

	//0～max-1までの数を乱数で得ます。
	int getRandom(int max){
		int n;
		
		n = (int)(Math.random() * max);
		if(n == max) n--;
		return( n );
	}
	
	int intcmp(int a,int b){
		if(a > b) return 1;
		else if(a < b) return -1;
		else return 0;
	}
}

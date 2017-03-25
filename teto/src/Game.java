//'97/8/31 - 9/9,9/20
//
//Game.java
//【テト《メイン》】　with JDK 1.0.2
//
//Copyright (C)1997 Yutaka Hirata
//All rights reserved.

import java.awt.*;
import java.applet.Applet;
import java.awt.image.*;

public class Game extends Canvas{
	static final int DD = 16; //１コマのサイズ（ピクセル）
	int board[][];  //テトの盤
	static int xs,ys;  //盤の横と縦のコマ数(デフォルト 10x20)
	
	int width,height; //テト盤のサイズ（ピクセル）
	Image offImg;  //ダブルバッファ
	Graphics offG;
	Graphics G;
	
	Piece piece;
	int dropX,dropY;  //落下中のブロック位置
	Image blockImg[] = new Image[7];
	
	int gameLevel;  //レベル（０～）
	//各レベルに合わせたブロック落下速度
	int gameSpeed[] = {25,20,15,13,10,8,6,4,2,1};
	int gameCounter; //カウンタ
	int deleteLines; //消した行数
	int lineCount[] = new int[5]; //各種消去行数
	
	int dropWait;
	
	boolean keyLock = false; //キーロックフラグ
	boolean gameFinish = false; //ゲームオーバーフラグ
	boolean firstFlag = false; //ゲーム開始フラグ
	
	ScoreBox Score;  //得点表示クラス
	NextBox Next; //次ブロック表示クラス
	
	//コンストラクタ
	Game(int xs,int ys,Component parent,Image img[]){
		board = new int[xs][ys];
		this.xs = xs;
		this.ys = ys;
		width = xs * DD;
		height = ys * DD;
		
		//ダブルバッファ作成には呼び出し元の親コンポーネントが
		//必要。通常は this を渡してやればよい。
		offImg = parent.createImage(width,height);
		offG = offImg.getGraphics();
		G = parent.getGraphics();
		for(int i = 0 ; i < 7; i++) blockImg[i] = img[i];
		
		Score = new ScoreBox(100,100,parent);
		Next = new NextBox(100,100,parent);
		clear(); //初期化
	}
	
	//初期化処理。
	private void clear(){
		int i,x,y;

		//落下してくるテトピース
		piece = new Piece();
		//初期落下位置
		dropX = (xs - piece.now.sizeX())/ 2; dropY = 0;
		gameLevel = gameCounter = 0;
		dropWait = 0;
		for(i = 0 ; i < 5;i++) lineCount[i] = 0;
		
		for(y = 0 ; y < ys ; y++)
			for(x = 0 ; x < xs ; x++)
				board[x][y] = 0;
	}
	
	void fastDisp(){
		G.drawImage(offImg,40,20,this);
	}
	
	public void update(Graphics g){
		paint(g);
	}
	
	//ゲームオーバー
	void gameOver(){
		String password[] = new String[16];
		password[0] = "Elephant"; password[1] = "Ghost";
		password[2] = "Roc"; password[3] = "Sable";		
		password[4] = "Griffin"; password[5] = "Gorgon";
		password[6] = "Medusa"; password[7] = "Dragon";
		password[8] = "Pegasus"; password[9] = "Phoenix";
		password[10] = "Devil"; password[11] = "Saturn";
		password[12] = "Monster"; password[13] = "Bobcat";
		password[14] = "Death13"; password[15] = "Cassowary";
		
		int n;
		int score = Score.getScore();
		
		offG.setColor(Color.black); offG.fillRect(0,0,width,height);
		offG.setColor(Color.green);
		offG.setFont(new Font("TimesRoman",Font.PLAIN,24));
		offG.drawString("Game over",20,60);
		offG.setColor(Color.cyan);
		offG.drawString("Level: "+gameLevel,20,80);
		offG.setColor(Color.orange);
		offG.drawString("Score:  "+score,20,120);
		offG.drawString("Single: "+lineCount[1],20,140);
		offG.drawString("Double: "+lineCount[2],20,160);
		offG.drawString("Triple: "+lineCount[3],20,180);
		offG.drawString("Tetris: "+lineCount[4],20,200);
		
		if(score >= 17000) n = 15;
		else if(score >= 15000) n = 14;
		else if(score >= 12000) n = 13;
		else if(score >= 11000) n = 12;
		else if(score >= 10000) n = 11;
		else if(score >= 9000) n = 10;
		else if(score >= 8000) n = 9;
		else if(score >= 7000) n = 8;
		else if(score >= 6000) n = 7;
		else if(score >= 5000) n = 6;
		else if(score >= 4000) n = 5;
		else if(score >= 3000) n = 4;
		else if(score >= 2500) n = 3;
		else if(score >= 2000) n = 2;
		else if(score >= 1500) n = 1;
		else if(score >= 1000) n = 0;
		else n = -1;
		
		if(n != -1){
			offG.setFont(new Font("Helvetica",Font.PLAIN,16));
			offG.setColor(new Color(255,206,250));
			offG.drawString("Password:",20,230);
			offG.drawString(password[n],20,250);
		}

		offG.setFont(new Font("Helvetica",Font.PLAIN,12));
		offG.setColor(Color.pink);
		offG.drawString("If you game again,",20,280);
		offG.drawString("   push [R] key!",20,300);		
		
		fastDisp();
	}
	
	//テトの描画
	public void paint(Graphics g){
		if(!firstFlag){ //最初描画されないので、こういう処理を入れた。
			Score.paint(g);
			Next.setNextBox(piece.next); Next.paint(g);
			firstFlag = true;
		}
		
		offG.setColor(Color.black);
		offG.fillRect(0,0,width,height);
		
		if(gameFinish){
			gameOver();
			return;
		}
		
		for(int i = 0 ; i < 2 ;i++){
			offG.setColor(new Color(255,200,0));
			offG.drawRect(i,i,width-2*i,height-2*i);
		}
		
		int x,y,n;
		for(y = 0 ; y < ys ; y++)
			for(x = 0 ; x < xs ; x++){
				if((n = board[x][y]) > 0){
					offG.drawImage(blockImg[n-1],x*DD,y*DD,this);
				}else 
				if( (dropX <= x && x < dropX + piece.now.sizeX() &&
					 dropY <= y && y < dropY + piece.now.sizeY() &&
					 piece.now.block[x - dropX][y - dropY] > 0) ){
					
					offG.drawImage(blockImg[piece.nowN],x*DD,y*DD,this);
				}
			}
		
		g.drawImage(offImg,40,20,null);
	}
	
	//ゲームを進める。
	//返り値：trueならゲームオーバー
	public boolean go(){
		if(gameFinish) return true;

		//ブロックが床に着いたか、左右の壁、もしくはすでにある
		//ブロックに接触したら。
		if(checkDrop(piece.now,dropX,dropY+1)){
			//着地しても少しゆとりを持たせる。これにより、落下してちょっとの
			//間は左右にスライドできるようになるはずである。
			if(dropWait < 5){
				dropWait++;
				return false;
			}
			dropWait = 0;
			
			//これ以上キーは受け付けない。
			keyLock = true;
			
			//落下したブロックを盤へコピーし固定。
			for(int y = 0 ; y < piece.now.sizeY() ; y++)
				for(int x = 0 ; x < piece.now.sizeX(); x++){
					int tx = dropX + x; int ty = dropY + y;
					if(tx >= 0 && tx < xs && ty >= 0 && ty < ys)
						if(board[tx][ty] == 0)
							board[tx][ty] = piece.now.block[x][y];
				}
			
			//1行消去できるかどうか調べる。
			int delY[] = new int[4];
			int n = checkFullLine(delY);
			if(n > 0){
				//アニメ効果
				animeDeleteLine(n,delY);
				
				deleteLine(); //消す。
				deleteLines += n;
				if(deleteLines >= 10){ //10行消したら次のレベルへ
					gameLevel++;
					if(gameLevel > 9) gameLevel = 9;
					deleteLines %= 10;
					System.out.println("Level:"+gameLevel);
				}
				
				//シングル、ダブル、トリプル、テトの数をカウント。
				lineCount[n]++;
				//得点計算
				Score.setScore(gameLevel,n); Score.paint(G);
				//Score.disp();
			}
			
			//次のブロックを生成。
			piece.nextPiece();
			//表示
			Next.setNextBox(piece.next); Next.paint(G);
			
			//新しいブロックの位置を初期化。
			dropX = (xs - piece.now.sizeX())/ 2; dropY = 0;
			//上まで詰まったらゲームオーバー
			if(checkDrop(piece.now,dropX,dropY)){
				gameFinish = true;
				gameOver();
				return true;
			}
			keyLock = false; //キーロック解除
		}
		gameCounter++;
		if(gameCounter > gameSpeed[gameLevel]){
			gameCounter = 0;
			dropY++;
		}
		return false;
	}
	
	//ブロックを(x,y)に置いた時、壁や床に接触するか調べる。
	boolean checkDrop(Block cur,int x,int y){
		int i,j,bx,by,tx,ty;
		
		//ブロックのサイズ分調べる。
		bx = cur.sizeX();
		by = cur.sizeY();
		for(j = 0 ; j < by ; j++)
			for(i = 0 ; i < bx ; i++){
				tx = i + x; ty = j + y;
				if(tx >= 0 && tx < xs && ty >= 0 && ty < ys){
					if(board[tx][ty] > 0 && cur.block[i][j] > 0)
						return true; //もう進めない。
				}else
					if(cur.block[i][j] > 0) return true; //もう進めない。
			}
		
		return false;  //大丈夫っ！
	}
	
	//盤のｙ行がそろったか調べる。
	boolean checkLine(int y){
		for(int i = 0 ; i < xs ; i++)
			if(board[i][y] == 0) return false;
		return true;
	}
	
	//何行消せるか数える。
	//引数：delY[]は消した行のｙ座標（相対）が入る。
	//返り値：０（なし）、１～４（消した行数）
	int checkFullLine(int delY[]){
		int i,n;
		
		n = 0;
		for(i = ys - 1 ; i >= 0 ; i--){
			if(checkLine(i)){ 
				delY[n] = i;
				n++;
			}
		}
		return( n );
	}
	
	//そろった行を消す時のアニメーション。
	void animeDeleteLine(int n,int delY[]){
		if(n == 4) offG.setColor(Color.yellow);
		else offG.setColor(Color.white);
		for(int i = 0 ; i < n ; i++){
			offG.fillRect(0,delY[i]*DD,width,DD);
		}
		fastDisp();
		try{ //ディレイ
			Thread.sleep(100);
		}catch(Exception e){}
	}
	
	//消せる行をまとめて消す。
	void deleteLine(){
		int i,j,k;
		
		for(i = ys - 1 ; i >= 1 ;i--){
			while(checkLine(i)){
				for(j = i; j >= 1 ; j--)
					for(k = 0 ; k < xs ; k++)
						board[k][j] = board[k][j-1];
				//1番上は空。
				for(j = 0 ; j < xs ; j++) board[j][0] = 0;
			}
		}
	}
	
	//左方向への移動。
	public void moveLeft(){
		if(keyLock) return;
		if(!checkDrop(piece.now,dropX-1,dropY)) dropX--;
	}

	//右方向への移動。
	public void moveRight(){
		if(keyLock) return;
		if(!checkDrop(piece.now,dropX+1,dropY)) dropX++;
	}
	
	//右方向へ回転させる。
	public void moveRightRotate(){
		if(keyLock) return;
		int w,h,x,y;
		Block tmp;
		
		w = piece.now.sizeX();
		h = piece.now.sizeY();
		tmp = new Block(h,w);  //x,y逆にする。
		for(y = 0 ; y < h ; y++)
			for(x = 0 ; x < w ; x++){
				tmp.block[y][x] = piece.now.block[ x ][ h - 1 - y ];
			}
		//回転後衝突するならダメ。
		if(checkDrop(tmp,dropX,dropY)) return;
		
		piece.now = new Block(h,w);
		for(y = 0 ; y < w ; y++)
			for(x = 0 ; x < h ;x++)
				piece.now.block[x][y] = tmp.block[x][y];
	}

	//左方向へ回転させる。デフォルトキー（上、Ｋ）では左回転する。
	public void moveLeftRotate(){
		if(keyLock) return;
		int w,h,x,y;
		Block tmp;
		
		w = piece.now.sizeX();
		h = piece.now.sizeY();
		tmp = new Block(h,w);  //x,y逆にする。
		for(y = 0 ; y < h ; y++)
			for(x = 0 ; x < w ; x++){
				tmp.block[h - 1 - y][w - 1 - x] = 
					piece.now.block[ x ][ h - 1 - y ];
			}
		//回転後衝突するならダメ。
		if(checkDrop(tmp,dropX,dropY)) return;
		
		piece.now = new Block(h,w);
		for(y = 0 ; y < w ; y++)
			for(x = 0 ; x < h ;x++)
				piece.now.block[x][y] = tmp.block[x][y];
	}
	
	//一気に落下させる。
	public void moveDrop(){
		if(keyLock) return;
		while( !checkDrop(piece.now,dropX,dropY+1) ) dropY++;
		paint(G);
	}
	
}


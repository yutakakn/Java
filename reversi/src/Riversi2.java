//'97/11/24,11/30,12/2,12/7,12/14,'98/1/16
//
//Riversi2.java
//【リバーシ２】　with JDK 1.0.2
//
//《クラス階層》
//　Riversi2.java ---- Computer.java
//
//《参考文献》
//　[1]蒲池輝尚:はじめて読むＭＡＳＭ,アスキー,1850円
//　[2]谷田邦彦:図解早わかりオセロ,日東書院,860円
//　[3]河西朝雄:Internet Language(3)Ｊａｖａ入門,技術評論社,1980円
//　[4]いくつかのホームページ
//
//Copyright (C)1997-1998 Yutaka Hirata
//All rights reserved.
/*
●ＨＴＭＬ
<applet code="Riversi2.class" width=320 height=390>
	<param name="gameRest1" value=24>
	<param name="gameLevel1" value=2>
	<param name="gameRest2" value=10>
	<param name="gameLevel2" value=3>
</applet>

●パラメータ
　gameRest1 : この残数までは優先度レベルgameLevel1で探索。
　gameRest2 : gameRest1～この残数までは優先度レベルgameLevel2。
　　　　　　　gameLeve1 < gameLevel2とすること。

　※残り手(rest)がgameRest2以降になると、ミニマックス法で最後まで読み切ります｡

●コンパイル
☆javac.exe(JDK付属)

　ソース一式が a:/java/usr/Riversi2 にインストールされているとします。
　　a:/>cd /java/usr/Riversi2
　　a:/java/usr/Riversi2>set CLASSPATH=a:/java/usr/Riversi2
　　a:/java/usr/Riversi2>javac Riversi2.java
　　
　※Computer.javaはComputer.classが存在しなければ自動的にコンパイルされます。
　　実行は appletviewer Riversi2.html です。
　※"/"は"￥"（半角）と読み替えてください。

☆jview.exe(IEインストール)

　jview.exeはマイクロソフトのJavaインタープリタでInternet Explorer(3.0,
　4.0とも）をインストールすると、windowsディレクトリにコピーされます。
　JDKのjava.exeと同じくDOS窓で利用できます。Sun版JDK 1.0.2のawtパッケージに関
　するKnown Bugが改善されており、javacに比べて若干コンパイル速度が速いです。

　　a:/java/usr/Riversi2>set CLASSPATH=a:/java/lib/classes.zip
　　a:/java/usr/Riversi2>jview sun.tools.javac.Main Riversi2.java
　　
　しかし、環境変数はautoexec.batではなく、レジストリで指定しなければならず、
　デフォルトでJava Homeやclasspathが"%Win_dir%/java"になっています。

●環境

　開発：PC-9821 V7(Pentium 75MHz),JDK 1.0.2
　ブラウザ：Internet Explorer 4.0,Netscape Navigator 3.02
　テスト：DELL(Pentium 150MHz)他

●補足(1997.12.14)
　
　どうやらＩＥ４だとゲームクリア時のダイアログがうまく動作しないようです。
　ＯＫボタンが付くはずなのにないうえに、文字も表示されない。
　Netscape Navigator 3.01では動作したため、ＭＳのJavaVMが原因と考えられます｡
　そういえば、IE4はJDK 1.1に対応していますが（Netscape Communicator 4.0などは
　まだ)、本当に100%サポートしているか怪しいものです:-)
　
 */

import java.applet.Applet;
import java.awt.*;

//メッセージダイアログ
//
//※どうもIE4では動作がアヤシイ。
class MessageDialog extends Dialog{
	Label label;
	Button button;
	
	public MessageDialog(Frame parent,String title){
		//キャプションがtitleのモーダルダイアログを生成。falseでモードレス。
		super(parent,title,true);
		setBackground(Color.white);
		setForeground(Color.black);
		resize(200,180);
		setFont(new Font("TimesRoman",Font.PLAIN,16));
		add("Center",label = new Label("",Label.CENTER));
		add("South",button = new Button("OK"));
	}
	
	public void init(String msg){
		label.setText(msg);
		show();
	}
	
	public boolean action(Event e,Object o){
		if(e.target == button){
			hide();
			return true;
		}
		return false;
	}
}

//スタッククラス
class stack{
	int data[];
	int sp;
	int max;
	
	//コンストラクタ
	stack(int n){    // n=スタックのサイズ
		data = new int[n];
		sp = 0;
		max = n;
	}
	
	void push(int n){
		if(sp >= max){
			System.err.println("Stack overflow!");
			System.exit(1);
		}
		data[sp++] = n;
	}
	
	int pop(){
		if(sp <= 0){
			System.err.println("Stack underflow!");
			System.exit(1);
		}
		return( data[--sp] );
	}
	
	void debug(){
		for(int i = 0 ; i < sp ; i++)
			System.out.println(data[i]);
	}
}

//メインアプレット
public class Riversi2 extends Applet
{
	static int BLACK = -1; //プログラム中変更あり｡
	static int WHITE =  1;
	static final int EMPTY =  2;
	static final int WALL  =  3;
	
	Image offImg; //ゲーム用ダブルバッファ
	Graphics offG;
	int width,height;
	Image offImg2; //情報領域用ダブルバッファ
	Graphics offG2;
	int width2,height2;
	
	int piece[] = new int[10*10];    //リバーシの盤(8x8)
	int savePiece[] = new int[10*10];  //リバーシの盤(Undo用)
	int masu;  //1コマのサイズ（ピクセル）
	
	int rest;  //空きコマの数
	int black_piece; //現在の黒コマの数
	int white_piece; //現在の白コマの数
	int diff;  //石差
	Color firstCol = Color.cyan;      //先手のコマの色
	Color secondCol = Color.pink;      //後手のコマの色
	
	boolean isTurn;//手番は自分(true)か、コンピュータ(false)か。
	int myPiece;   //自分のコマ
	
	boolean gameFinish;
	
	//スタッククラス
	stack myStack;
	//コンピュータクラス
	Computer Comp;
	//メッセージクラス
	MessageDialog myDialog;
	
	//ＧＵＩ変数
	Button btnRetry,btnStart,btnUndo;
	Choice chVersus,chTurn;
	
	static final int COMP = 0,HUMAN = 1,FIRST = 0,SECOND=1;
	int Versus = COMP; //対戦モード
	int Turn = FIRST; //先手か後手か
	int gameRest1,gameRest2,gameLevel1,gameLevel2; //レベル

	int pos(int x,int y){
		return( x + 10*y );
	}
	
	//デバッグ用
	void dispBoard(){
		int x,y;
		String c;
		
		for(y = 0 ; y < 10 ; y++){
			for(x = 0 ; x < 10 ; x++){
				if(piece[pos(x,y)] == WALL) c = "*";
				else if(piece[pos(x,y)] == WHITE) c = "o";
				else if(piece[pos(x,y)] == BLACK) c = "x";
				else if(piece[pos(x,y)] == EMPTY) c = " ";
				else c = "";
				System.out.print(c);
			}
			System.out.println("");
		}
	}

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
		setLayout(new BorderLayout());
		Panel p = new Panel();
		p.add(btnRetry = new Button("Retry"));
		btnRetry.disable();
		p.add(btnStart = new Button("Start"));
		p.add(btnUndo = new Button("Undo")); //追加
		btnUndo.disable();
		chVersus = new Choice();
		chVersus.addItem("COMP");
		chVersus.addItem("HUMAN");
		chTurn = new Choice();
		chTurn.addItem("FIRST");
		chTurn.addItem("SECOND");
		p.add(chVersus); p.add(chTurn);
		add("South",p);
		
		width = size().width;
		height = width;
		offImg = createImage(width,height);
		offG = offImg.getGraphics();
		
		width2 = width; height2 = 30;
		offImg2 = createImage(width2,height2);
		offG2 = offImg2.getGraphics();
		
		//resize(width,height+height2);
		//この値をアプレットのパラメータとする。
		System.out.println("width="+width+" height="+(height+height2+40));

		//ゲームレベル
		gameRest1 = param("gameRest1",24);
		gameRest2 = param("gameRest2",10);
		gameLevel1 = param("gameLevel1",2);
		gameLevel2 = param("gameLevel2",3);
		System.out.println("rest1="+gameRest1+" level1="+gameLevel1+
			" rest2="+gameRest2+" level2="+gameLevel2);

		Comp = new Computer(this); //インスタンス生成
		myDialog = new MessageDialog(null,"PASSWORD");
		gameInit();
	}
	
	//ゲーム初期化
	void gameInit(){
		int x,y;

		//ゲーム中gameInit()は何度も呼ばれるため､同時にスタックも初期化
		//しなければならない。
		myStack = new stack(1000); //スタックの確保

		rest = 60;
		white_piece = black_piece = 2;
		diff = 0;
		gameFinish = false;
		isTurn = true;
		BLACK = -1; WHITE = 1;
		myPiece = BLACK;
		
		masu = width / 8; //1コマの縦横サイズ
		for(y = 0 ; y < 10 ; y++)
			for(x = 0 ; x < 10 ; x++){
				piece[pos(x,y)] = EMPTY;  //空
				if(x == 0 || x == 9 || y == 0 || y == 9)
					piece[pos(x,y)] = WALL;  //周りは壁
			}
		
		piece[pos(4,4)] = piece[pos(5,5)] = WHITE;
		piece[pos(4,5)] = piece[pos(5,4)] = BLACK;
		drawScreen();
		drawPiece();
	}
	
	//ゲームオーバー
	void gameOver(){
		String msg;
		
		btnUndo.disable();
		gameFinish = true;
		offG.setFont(new Font("TimesRoman",Font.BOLD,24));
		offG.setColor(Color.red);
		if(black_piece > white_piece){
			isTurn = true;
			msg = "YOU WIN!!";
			offG.setFont(new Font("TimesRoman",Font.BOLD,30));
			offG.setColor(new Color(0,255-20,255-20));
			offG.drawString(msg,20-2,100);
			offG.drawString(msg,20+2,100);
			offG.drawString(msg,20,100-2);
			offG.drawString(msg,20,100+2);
			offG.setColor(new Color(0,255,255));
		}else if(black_piece < white_piece){
			isTurn = false;
			msg = "YOU LOST...";
		}else msg = "Game ends in a draw.";
		offG.drawString(msg,20,100);
		dispInfo(""+black_piece+" v.s. "+white_piece);
		repaint();
		
		//パスワード表示
		if(black_piece > white_piece){
			String s;
			int w,h;
			
			//以下、ドイツ語
			if(black_piece == 64) s = "Klavier"; //ピアノ
			else if(black_piece >= 62) s = "Flote"; //フルート
			else if(black_piece >= 60) s = "Blockflote";//リコーダー
			else if(black_piece >= 58) s = "Querflote"; //横笛
			else if(black_piece >= 54) s = "Pikkolo";//ピッコロ
			else if(black_piece >= 50) s = "Orchester";//オーケストラ
			else if(black_piece >= 46) s = "Oper"; //オペラ
			else if(black_piece >= 40) s = "Musik";//音楽
			else if(black_piece >= 36) s = "Instrument";//楽器
			else if(black_piece >= 34) s = "Apostel";//使徒
			else if(black_piece >= 32) s = "Seele";//魂
			else if(black_piece >= 30) s = "Weihnachten"; //クリスマス
			else if(black_piece >= 26) s = "Zauber"; //魔法
			else if(black_piece >= 20) s = "XChromosom"; //Ｘ染色体
			else if(black_piece >= 16) s = "Xerographie"; //ゼログラフィー
			else s = "Nothing!";
			
			//画面にも表示。IE対策?
			msg = "PASSWORD: "+s;
			Font fnt = new Font("Courier",Font.PLAIN,20);
			FontMetrics fm = getFontMetrics(fnt);
			w = fm.stringWidth(msg);
			h = fm.getAscent();
			offG.setFont(fnt);
			offG.setColor(Color.black);
			offG.fillRect(0,masu*7+(masu-h),w,h);
			offG.setColor(Color.orange);
			offG.drawString(msg,0,masu*8);
			repaint();
			myDialog.init(s); //ダイアログ表示
		}
	}

	//中心(x,y)、半径rの円を描き、現在色で塗りつぶします。
	void fillCircle(Graphics g,int x,int y,int r){
		g.fillOval(x-r,y-r,2*r,2*r);
	}
	
	void drawScreen(){
		int i;

		offG.setColor(Color.black);
		offG.fillRect(0,0,width,height);
		offG2.setColor(Color.black);
		offG2.fillRect(0,0,width2,height2);
		
		//緑の盤面を描く。
		offG.setColor(new Color(0,128,0));
		offG.fillRect(0,0,width,height);
		
		//外枠を黒で描く。
		for(i = 0 ; i < 3 ;i++){
			offG.setColor(Color.black);
			offG.drawRect(i,i,width-i*2-1,height-i*2-1);
		}
		
		//マスを区切る線を描く。
		for(i = 0 ; i < 8 ; i++){
			//masuは1コマのサイズ
			offG.drawLine(i*masu,0,i*masu,width);
			offG.drawLine(0,i*masu,width,i*masu);
		}
		
		//星を描きます。
		fillCircle(offG,2*masu,2*masu,3);
		fillCircle(offG,6*masu,2*masu,3);
		fillCircle(offG,2*masu,6*masu,3);
		fillCircle(offG,6*masu,6*masu,3);
		dispInfo("Player turn");
		
		repaint();
	}
	
	void drawPiece(){
		int x,y,p;

		for(y = 1 ; y <= 8 ; y++)
			for(x = 1 ; x <= 8; x++){
				p = piece[pos(x,y)];
				if(p == WHITE){
					offG.setColor( secondCol );
					offG.fillOval(
					masu*(x-1)+2,masu*(y-1)+2,masu-3,masu-3);
				}else if(p == BLACK){
					offG.setColor( firstCol );
					offG.fillOval(
					masu*(x-1)+2,masu*(y-1)+2,masu-3,masu-3);
				}
			}
		repaint();
	}

	//画面の下にメッセージを表示します。
	void dispInfo(String msg){
		Font fnt = new Font("TimesRoman",Font.BOLD,24);
		FontMetrics fm = getFontMetrics(fnt);
		offG2.setColor(new Color(172,223,178));
		offG2.fillRect(0,0,width2,height2);
		offG2.setFont(fnt);
		offG2.setColor(Color.yellow);
		offG2.drawString(msg,0,height2 - fm.getDescent());
		
		//手番を描画します。
		//offG2.setColor(new Color(0,128,0));
		//offG2.fillRect(width2-30,0,30,30);
		if(isTurn == true){ //プレイヤーの色
			offG2.setColor( Turn == FIRST ? firstCol : secondCol );
		}else{  //コンピュータの色
			offG2.setColor( Turn == FIRST ? secondCol : firstCol );
		}
		offG2.fillOval(width-28,0,28,28);
		fastPaint();
	}
	
	public void paint(Graphics g){
		update(g);
	}
	
	public void update(Graphics g){
		g.drawImage(offImg,0,0,this);
		g.drawImage(offImg2,0,height,this);
	}
	
	void fastPaint(){
		update(getGraphics());
	}
	
	public boolean action(Event e,Object o){
		if(e.target instanceof Choice){ //リストボックス
			String s = (String)o;
			if(s.equals("COMP")) Versus = COMP;
			if(s.equals("HUMAN")) Versus = HUMAN;
			if(s.equals("FIRST")) Turn = FIRST;
			if(s.equals("SECOND")) Turn = SECOND;
			return true;
		}else if(e.target == btnStart){ //[Start]ボタン
			if(Turn == FIRST){
				BLACK = -1; WHITE = 1;
				myPiece = BLACK;
			}else{
				BLACK = 1; WHITE = -1;
				myPiece = WHITE;
			}
			piece[pos(4,4)] = piece[pos(5,5)] = WHITE;
			piece[pos(4,5)] = piece[pos(5,4)] = BLACK;

			btnRetry.enable();
			btnStart.disable();
			chVersus.disable();
			chTurn.disable();
			//コンピュータ対戦で、最初の１手で、プレイヤーが後手ならば
			if(Versus == COMP && rest == 60 && Turn == SECOND){
				computerThink(); drawPiece();
			}
			//アンドゥ用（追加）
			if(Versus == COMP){
				btnUndo.enable();
				saveUndo();
			}
			return true;
		}else if(e.target == btnRetry){ //[Retry]ボタン
			btnRetry.disable();
			btnStart.enable();
			btnUndo.disable();
			chVersus.enable();
			chTurn.enable();
			gameInit();
			return true;
		}else if(e.target == btnUndo){ //[Undo]ボタン
			loadUndo();
			return true;
		}
		
		return false;
	}
	
	//マウスイベント
	public boolean mouseDown(Event e,int x,int y){
		if(btnStart.isEnabled() || gameFinish) return true;
		if(x < 0 || x > width || y < 0 || y > height) return true;
		if(Versus == COMP){
			saveUndo();      //アンドゥのため1つ手前を保存
			vsComputer(x,y);
		}else{
			vsHuman(x,y);
		}
		
		return true;
	}
	
	//対コンピュータ
	void vsComputer(int x,int y){
		//コンピュータが思考中はマウスイベントは受け付けない。
		if(isTurn == false) return;
		isTurn = true;
		x /= masu;
		y /= masu;
		x++; y++; //(x,y)は1から始まるので補正する。
		if(putPiece(x,y,myPiece,true) == 0) return;
		drawPiece();
		
		computerThink();
		drawPiece();

		//プレイヤーがパスの間。
		boolean comp = true;
		while(isPutable(myPiece) == false){
			comp = computerThink();
			drawPiece();
			if(comp == false){
				break; //コンピュータがパス
			}
		}
		
		//comp == trueのとき、プレイヤーが置けるようになった。
		//comp == falseのときどちらもパス。つまりゲームオーバー。
		if(comp == false || rest == 0 || black_piece == 0 ||
		 	white_piece == 0){
			gameOver();
		}
	}
	
	//人v.s.人
	void vsHuman(int x,int y){
		int koma;
		
		if(isTurn == true) koma = myPiece;
		else koma = -myPiece;
		
		x /= masu;
		y /= masu;
		x++; y++; //(x,y)は1から始まるので補正する。
		
		if(putPiece(x,y,koma,true) == 0) return; //置けない
		drawPiece();
		if(isPutable(-koma)){ //次の人も置ける
			isTurn = !isTurn;
			dispInfo("");
			return;
		}
		if(isPutable(koma) == false || rest == 0 || black_piece == 0 ||
			white_piece == 0){
			gameOver();
		}
	}
	
	//(x,y)にコマkomaを置いた時、(dx,dy)方向へ走査して相手のコマが
	//いくつ返せるかを調べます。
	//引数reverseの意味はputPieceメソッドと同じです。
	int putDirect(int x,int y,int dx,int dy,int koma,boolean reverse){
		int teki,xs,ys,n;
		
		teki = koma*(-1);
		x += dx;
		y += dy;
		xs = x;
		ys = y;
		n = 0;
		while( piece[pos(x,y)] == teki){
			x += dx;
			y += dy;
			n++;
		}
			
		if(piece[pos(x,y)] == koma){
			if(reverse){
				for(int i = 0 ; i < n ; i++){
					myStack.push(pos(xs,ys));  //スタックへ保存
					piece[pos(xs,ys)] = koma;
					xs += dx;
					ys += dy;
				}
			}
			return( n );  //ひっくり返したコマの数
		}
		return 0;
	}
	
	//(x,y)にコマkomaを置き、相手のコマをいくつ返せるかを調べ、
	//その数を関数の返り値とします。
	//引数reverse==falseのときは、返せるコマの数を数えるだけで、
	//実際には盤には変化はありません。
	int putPiece(int x,int y,int koma,boolean reverse){
		int c;
		
		//空コマでないと置けない。
		if(piece[pos(x,y)] != EMPTY) return 0;
		
		c = 0;
		c += putDirect(x,y,0,-1,koma,reverse); //上
		c += putDirect(x,y,1,-1,koma,reverse); //右上
		c += putDirect(x,y,1, 0,koma,reverse); //右
		c += putDirect(x,y,1, 1,koma,reverse); //右下
		c += putDirect(x,y,0, 1,koma,reverse); //下
		c += putDirect(x,y,-1,1,koma,reverse); //左下
		c += putDirect(x,y,-1,0,koma,reverse); //左
		c += putDirect(x,y,-1,-1,koma,reverse); //左上
		
		//1つでもひっくり返せて、フラグがonなら。
		if(c > 0 && reverse){
			//自分のコマを忘れずに！
			piece[pos(x,y)] = koma;
			myStack.push(pos(x,y)); //スタックへ保存
			myStack.push(c);  //返した石の数
			
			//各カウント変数を計算。
			diff += ((2*c + 1) * koma);
			rest--;
			if(koma == myPiece){ 
				black_piece += (c + 1);
				white_piece -= c;
			}else{ 
				white_piece += (c + 1);
				black_piece -= c;
			}
		}
		
		return( c );
	}
	
	//直前に置いたコマとひっくり返したコマを戻す。
	void putBack(int koma){
		int n,pos,teki;
		
		n = myStack.pop();
		pos = myStack.pop();
		piece[pos] = EMPTY;
		
		//カウント変数の還元
		diff -= ((2*n+1)*koma);
		rest++;
		if(koma == myPiece){ 
			black_piece -= (n + 1);
			white_piece += n;
		}else{ 
			white_piece -= (n + 1);
			black_piece += n;
		}
		
		teki = koma*(-1);
		while(n-- > 0){
			pos = myStack.pop();
			piece[pos] = teki;
		}
	}
	
	//コマkomaの番で置けるところがあるか調べます。
	//あるならtrueを、ない（つまりパス）ならfalseを返します。
	boolean isPutable(int koma){
		int x,y;
		
		for(y = 1 ; y <= 8 ; y++)
			for(x = 1 ; x <= 8 ; x++)
				if(putPiece(x,y,koma,false) > 0)
					return true;
		return false;
	}
	
	//コンピュータ思考ルーチン
	boolean computerThink(){
		isTurn = false; //思考中
		dispInfo("Now thinking...");
		
		Point bestpos = new Point(-1,-1);
		int teki = myPiece*(-1);

		if(rest >= 59) bestpos = Comp.think1();
		else if(rest > gameRest1) bestpos = Comp.think2(gameLevel1,teki);
		else if(rest > gameRest2) bestpos = Comp.think2(gameLevel2,teki);
		else bestpos = Comp.think3(rest,teki);
		
		isTurn = true; //思考終了
		dispInfo("Player turn");
		
		if(bestpos.x == -1) return false; //パス
		putPiece(bestpos.x,bestpos.y,teki,true);
		drawPiece();
		return true;
	}
	
	//---以下、アンドゥルーチン---
	
	void saveUndo(){
		for(int i = 0 ; i < 100 ; i++) savePiece[i] = piece[i];
	}
	
	void loadUndo(){
		int i,c,x,y;
		
		for(i = 0 ; i < 100 ; i++) piece[i] = savePiece[i];
		myStack = new stack(1000); //スタックの確保
		rest = black_piece = white_piece = 0;
		for(y = 1 ; y <= 8 ;y++)
			for(x = 1 ; x <= 8 ; x++){
				c = pos(x,y);
				if(piece[c] == EMPTY) rest++;
				if(piece[c] == BLACK) black_piece++;
				if(piece[c] == WHITE) white_piece++;
			}
		diff = black_piece * BLACK + white_piece * WHITE;
		drawScreen(); drawPiece();
	}

}

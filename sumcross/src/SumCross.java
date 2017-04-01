//'98/1/5,1/7,1/9
//
//SumCross.java
//【カックロ】　with JDK 1.0.2
//
//《動作確認》
//　Appletviewer(WinCafe)
//　Internet Explorer 4.0
//　Netscape Navigator 3.0
//
//《参考文献》
//　"パズラー 1997/11月号",世界文化社,410円
//　駒木悠二,有澤誠:"続ナノピコ教室",共立出版,1991
//
//Copyright (C)1997-1998 Yutaka Hirata
//All rights reserved.
/*
●ＨＴＭＬ
<APPLET CODE="SumCross.class" WIDTH=428 HEIGHT=378>
	<param name="MS" value=42>
</APPLET>

●パラメータ
　MS : 1つのコマのサイズ（ピクセル）
　
●遊びかた
　Load.javaに示したように表現された連立方程式を解く「加算クロスワード」
　です。つまり、
　　1.三角の中の数字はその列（または行）の総和。
　　2.各空白には1から9までの数字が重複せず入ります｡
　　　つまり、空白のマスの個数は最大9個で、総和の最大は45です。
　を満たします｡
　右のボタンから1から9の数字を選びマウスでクリックするとコマを置くことが
　できます。すべてのコマを埋め、かつ正解であれば次のステージへ進めます｡

●コンパイル
　Windows95であればDOS窓から
　　a:/java/usr/sumcross>javac SumCross.java
　です。

●正式名称

　サムクロス
　※カックロは（株）ニコリによるCROSSSUMの和名です。
　
 */

import java.awt.*;
import java.applet.*;

//メッセージダイアログ
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

public class SumCross extends Applet {
	//問題領域ダブルバッファ
	Image offImg;
	Graphics offG;
	int width,height;

	Button clsBtn,nextBtn; 	        //ＧＵＩ部品
	int MS;         //コマのサイズ（ピクセル）
	int number = 1; //選択している番号（１～９）
	int stage = 1;  //ステージ
	boolean gameClear;              //ゲームクリアフラグ
	
	//カックロデータ
	int W,H;                //カックロの横と縦サイズ
	int pid,ymax,emptyCnt;  //データ数、横データ数、空コマの数
	int proc[][];           //データ
	int ban[][];            //解
	int board[][];          //盤（プレイ用）
	
	//問題読み込みクラス
	Load load;
	//メッセージダイアログ
	MessageDialog myDialog;

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

    public void init() {
		//ＧＵＩ部品の配置。
		Panel p = new Panel();
		p.setLayout(new GridLayout(11,1));
		p.add(nextBtn = new Button("Next"));
		nextBtn.enable(false);
		p.add(clsBtn = new Button("Clear"));
		CheckboxGroup cg = new CheckboxGroup(); //ラジオボタン
		p.add(new Checkbox("1",cg,true));
		for(int i = 2 ; i <= 9 ;i++){
			p.add(new Checkbox(""+i,cg,false));
		}
		setLayout(new BorderLayout());
		add("East",p);
		
		//パラメータ
		MS = param("MS",42);
		//これを先に実行しないとW,H,...etcが得られない。
		loadStage( stage );

		//この出力の値を元にhtmlのタグを決める。
		width = W * MS; height = H * MS;
		System.out.println("width="+(width+50)+" height="+height);
		offImg = createImage(width,height);
		offG = offImg.getGraphics();
		
		gameInit();
    }
    
	//ステージｎのデータを読み込みます｡
	void loadStage(int n){
		load = new Load(n);   //n >= 1...
		W = load.getW();
		H = load.getH();
		pid = load.getPid();
		ymax = load.getYmax();
		emptyCnt = load.getEmptyCnt();
		proc = load.getProc();
		ban = load.getBan();
		board = new int[H+1][W+1];
		for(int y = 0 ; y < H+1 ; y++)
			for(int x = 0 ; x < W+1 ; x++)
				board[y][x] = 0;
	}

    void gameInit(){
		gameClear = false;
		clsBtn.enable( true );
		nextBtn.enable( false );
		loadStage( stage );
		//とりあえず、前のステージのを消しておく。Color.blackでも動作に
		//支障はないが、新しいステージでは黒い縁が残る場合があるので、
		//格好悪い｡
		offG.setColor(Color.white);
		offG.fillRect(0,0,width,height);
		//ステージによりサイズが違うため､初期化するたびにコマサイズを
		//再計算する。
		MS = width / W;
		width = W*MS; height = H*MS;  //縦と横が同じ、という前提。
		drawScreen();
		drawPiece();
	}
	
	void gameOver(){
		gameClear = true;
		offG.setFont(new Font("TimesRoman",Font.BOLD,24));
		offG.setColor(Color.yellow);
		offG.drawString("STAGE "+stage+" Clear!",0,28);
		repaint();
		//Nextボタンをｏｎにする。
		clsBtn.enable( false );
		if(stage < load.lastStage) nextBtn.enable( true );
		else{ //全ステージクリア！！
			System.out.println("All stages are clear. Bye bye!(^_^)/~");
			//PAMELAHのHIT COLLECTIONより:-)
			String pass = "??????????";
			myDialog = new MessageDialog(null,"PASSWORD:"+pass);
			myDialog.init( pass );
		}
	}
    
    //ゲーム画面の描画
    void drawScreen(){
		int i,j;
		
		//背景は黒。
		offG.setColor(Color.black);
		offG.fillRect(0,0,width,height);
		//横線を引く。
		offG.setColor(Color.white);
		for(i = 1 ; i <= H - 1 ; i++) offG.drawLine(0,i*MS,width,i*MS);
		for(i = 1 ; i <= W - 1 ; i++) offG.drawLine(i*MS,0,i*MS,height);
		//斜め線を引く｡
		j = 1;
		for(i = W - 1 ; i >= 0 ; i--){
			offG.drawLine(i*MS,0,W*MS,j*MS);
			offG.drawLine(0,i*MS,j*MS,H*MS);
			j++;
		}
		repaint();
	}
    
    //ヒント、空コマなどの描画
    void drawPiece(){
		int i,x,y,wa,sz;

		offG.setColor(Color.white);
		for(y = 1 ; y <= H ; y++)
			for(x = 1 ; x <= W ; x++)
				if(ban[y][x] > 0){ //空コマを描く
					offG.fillRect((x-1)*MS+1,(y-1)*MS+1,MS-2,MS-2);
				}
		for(i = 0 ; i < pid ; i++){
			//proc[i][2]=マスの数。ここでは必要なし｡
			y = proc[i][0]; x = proc[i][1]; wa = proc[i][3];
			//2桁の数字はフォントサイズを少し小さくする。
			sz = (wa < 10) ? MS/2 : MS/2-3;
			
			offG.setColor(Color.cyan);
			offG.setFont(new Font("Courier",Font.BOLD,sz));
			if(i < ymax){ //横のヒント。逆三角。
				offG.drawString(""+wa,(x-1)*MS+MS/2,(y-1)*MS+MS/2);
			}else{ //縦のヒント。
				int hosei = (wa < 10) ? MS/8 : 0;
				offG.drawString(""+wa,(x-1)*MS+hosei,(y-1)*MS+MS);
			}
		}
	}
    
    //(x,y)に数字numを描画する。
    void drawNum(int x,int y,int num){
		//消去して...
		delNum(x,y);
		//フォント設定して...
		Font fnt = new Font("Courier",Font.BOLD,MS-2);
		FontMetrics fm = getFontMetrics(fnt);
		int l = fm.stringWidth(""+num);
		int h = fm.getAscent();
		//そして、描く。
		offG.setFont(fnt);
		offG.setColor(new Color(50,50,50));
		offG.drawString(""+num,
		(MS-l)/2 + (x-1)*MS, (y-1)*MS + MS - 2 - (MS-h)/2);
		
		repaint();
	}
	
	//(x,y)をクリアします。
	void delNum(int x,int y){
		offG.setColor(Color.white);
		offG.fillRect((x-1)*MS+1,(y-1)*MS+1,MS-2,MS-2);
		repaint();
	}
    
   	public void paint(Graphics g){
		update(g);
	}

	public void update(Graphics g){
		g.drawImage(offImg,0,0,this);
	}

	//イベントハンドラ
	public boolean action(Event e,Object o){
		if(e.target == clsBtn){
			//クリア処理
			gameInit();
			return true;
		}else if(e.target == nextBtn){
			//次ステージへ
			if(stage < load.lastStage){ 
				stage++;
				gameInit();
			}
			return true;
		}else{
			//どのボタンが選択されたかを調べる。
			int flag = 0;
			Checkbox c = (Checkbox)e.target;
			for(int i = 1 ; i <= 9 ;i++){
				if(Integer.toString(i).equals( c.getLabel() )){
					number = i;
					flag = 1;
				}
			}
			if(flag == 1) return true;
		}
		return false;
	}

	//マウスイベント
	public boolean mouseDown(Event e,int mx,int my){
		int x,y;
		
		if(gameClear) return true;
		if(!(mx >= 0 && mx <= width && my >= 0 && my <= height)) return true;
		//以降、(1,1)～で扱うためインクリメントしておく。
		x = mx / MS + 1;
		y = my / MS + 1;
		if(ban[y][x] == 0) return true; //空でない（置けない）
		//右クリックなら、置いてあるコマを消す｡
		if((e.modifiers & Event.META_MASK) != 0){
			if(board[y][x] > 0){
				board[y][x] = 0;
				delNum(x,y);
			}
		}else{ //左（中央）クリックなら、コマを置く｡
			board[y][x] = number;  //数字（コマ）を置く。
			drawNum(x,y,number);   //描画
			if(getPieceCnt() == emptyCnt){
				if(isComplete()){
					System.out.println("Stage "+stage+" is clear!");
					gameOver();
				}else{
					System.out.println("You miss anywhere...");
				}
			}
		}
		
		return true;
	}
	
	//置かれたコマの数を返す｡
	int getPieceCnt(){
		int x,y,cnt = 0;
		
		for(y = 1 ; y <= H ; y++)
			for(x = 1 ; x <= W ; x++)
				if(board[y][x] > 0) cnt++;
		return( cnt );
	}
	
	//完成？
	boolean isComplete(){
		int x,y;
		
		for(y = 1 ; y <= H ; y++)
			for(x = 1 ; x <= W ; x++)
				if(board[y][x] != ban[y][x]) return false;
		return true;
	}

}

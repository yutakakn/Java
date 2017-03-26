//'97/11/24,11/30,12/2,12/7,12/14,'98/1/16
//
//Riversi2.java
//�y���o�[�V�Q�z�@with JDK 1.0.2
//
//�s�N���X�K�w�t
//�@Riversi2.java ---- Computer.java
//
//�s�Q�l�����t
//�@[1]���r�P��:�͂��߂ēǂނl�`�r�l,�A�X�L�[,1850�~
//�@[2]�J�c�M�F:�}�𑁂킩��I�Z��,�������@,860�~
//�@[3]�͐����Y:Internet Language(3)�i����������,�Z�p�]�_��,1980�~
//�@[4]�������̃z�[���y�[�W
//
//Copyright (C)1997-1998 Yutaka Hirata
//All rights reserved.
/*
���g�s�l�k
<applet code="Riversi2.class" width=320 height=390>
	<param name="gameRest1" value=24>
	<param name="gameLevel1" value=2>
	<param name="gameRest2" value=10>
	<param name="gameLevel2" value=3>
</applet>

���p�����[�^
�@gameRest1 : ���̎c���܂ł͗D��x���x��gameLevel1�ŒT���B
�@gameRest2 : gameRest1�`���̎c���܂ł͗D��x���x��gameLevel2�B
�@�@�@�@�@�@�@gameLeve1 < gameLevel2�Ƃ��邱�ƁB

�@���c���(rest)��gameRest2�ȍ~�ɂȂ�ƁA�~�j�}�b�N�X�@�ōŌ�܂œǂݐ؂�܂��

���R���p�C��
��javac.exe(JDK�t��)

�@�\�[�X�ꎮ�� a:/java/usr/Riversi2 �ɃC���X�g�[������Ă���Ƃ��܂��B
�@�@a:/>cd /java/usr/Riversi2
�@�@a:/java/usr/Riversi2>set CLASSPATH=a:/java/usr/Riversi2
�@�@a:/java/usr/Riversi2>javac Riversi2.java
�@�@
�@��Computer.java��Computer.class�����݂��Ȃ���Ύ����I�ɃR���p�C������܂��B
�@�@���s�� appletviewer Riversi2.html �ł��B
�@��"/"��"��"�i���p�j�Ɠǂݑւ��Ă��������B

��jview.exe(IE�C���X�g�[��)

�@jview.exe�̓}�C�N���\�t�g��Java�C���^�[�v���^��Internet Explorer(3.0,
�@4.0�Ƃ��j���C���X�g�[������ƁAwindows�f�B���N�g���ɃR�s�[����܂��B
�@JDK��java.exe�Ɠ�����DOS���ŗ��p�ł��܂��BSun��JDK 1.0.2��awt�p�b�P�[�W�Ɋ�
�@����Known Bug�����P����Ă���Ajavac�ɔ�ׂĎ኱�R���p�C�����x�������ł��B

�@�@a:/java/usr/Riversi2>set CLASSPATH=a:/java/lib/classes.zip
�@�@a:/java/usr/Riversi2>jview sun.tools.javac.Main Riversi2.java
�@�@
�@�������A���ϐ���autoexec.bat�ł͂Ȃ��A���W�X�g���Ŏw�肵�Ȃ���΂Ȃ炸�A
�@�f�t�H���g��Java Home��classpath��"%Win_dir%/java"�ɂȂ��Ă��܂��B

����

�@�J���FPC-9821 V7(Pentium 75MHz),JDK 1.0.2
�@�u���E�U�FInternet Explorer 4.0,Netscape Navigator 3.02
�@�e�X�g�FDELL(Pentium 150MHz)��

���⑫(1997.12.14)
�@
�@�ǂ����h�d�S���ƃQ�[���N���A���̃_�C�A���O�����܂����삵�Ȃ��悤�ł��B
�@�n�j�{�^�����t���͂��Ȃ̂ɂȂ������ɁA�������\������Ȃ��B
�@Netscape Navigator 3.01�ł͓��삵�����߁A�l�r��JavaVM�������ƍl�����܂��
�@���������΁AIE4��JDK 1.1�ɑΉ����Ă��܂����iNetscape Communicator 4.0�Ȃǂ�
�@�܂�)�A�{����100%�T�|�[�g���Ă��邩���������̂ł�:-)
�@
 */

import java.applet.Applet;
import java.awt.*;

//���b�Z�[�W�_�C�A���O
//
//���ǂ���IE4�ł͓��삪�A���V�C�B
class MessageDialog extends Dialog{
	Label label;
	Button button;
	
	public MessageDialog(Frame parent,String title){
		//�L���v�V������title�̃��[�_���_�C�A���O�𐶐��Bfalse�Ń��[�h���X�B
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

//�X�^�b�N�N���X
class stack{
	int data[];
	int sp;
	int max;
	
	//�R���X�g���N�^
	stack(int n){    // n=�X�^�b�N�̃T�C�Y
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

//���C���A�v���b�g
public class Riversi2 extends Applet
{
	static int BLACK = -1; //�v���O�������ύX����
	static int WHITE =  1;
	static final int EMPTY =  2;
	static final int WALL  =  3;
	
	Image offImg; //�Q�[���p�_�u���o�b�t�@
	Graphics offG;
	int width,height;
	Image offImg2; //���̈�p�_�u���o�b�t�@
	Graphics offG2;
	int width2,height2;
	
	int piece[] = new int[10*10];    //���o�[�V�̔�(8x8)
	int savePiece[] = new int[10*10];  //���o�[�V�̔�(Undo�p)
	int masu;  //1�R�}�̃T�C�Y�i�s�N�Z���j
	
	int rest;  //�󂫃R�}�̐�
	int black_piece; //���݂̍��R�}�̐�
	int white_piece; //���݂̔��R�}�̐�
	int diff;  //�΍�
	Color firstCol = Color.cyan;      //���̃R�}�̐F
	Color secondCol = Color.pink;      //���̃R�}�̐F
	
	boolean isTurn;//��Ԃ͎���(true)���A�R���s���[�^(false)���B
	int myPiece;   //�����̃R�}
	
	boolean gameFinish;
	
	//�X�^�b�N�N���X
	stack myStack;
	//�R���s���[�^�N���X
	Computer Comp;
	//���b�Z�[�W�N���X
	MessageDialog myDialog;
	
	//�f�t�h�ϐ�
	Button btnRetry,btnStart,btnUndo;
	Choice chVersus,chTurn;
	
	static final int COMP = 0,HUMAN = 1,FIRST = 0,SECOND=1;
	int Versus = COMP; //�ΐ탂�[�h
	int Turn = FIRST; //��肩��肩
	int gameRest1,gameRest2,gameLevel1,gameLevel2; //���x��

	int pos(int x,int y){
		return( x + 10*y );
	}
	
	//�f�o�b�O�p
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

	//�p�����[�^���擾����i���l�Łj�B
	public int param(String pname,int def){
		String val = getParameter(pname);
		return (val != null) ? Integer.parseInt(val) : def;
	}

	//�p�����[�^���擾����i������Łj�B
	public String param(String pname,String def){
		String val = getParameter(pname);
		return (val != null) ? val : def;
	}
	
	public void init(){
		//�f�t�h�z�u
		setLayout(new BorderLayout());
		Panel p = new Panel();
		p.add(btnRetry = new Button("Retry"));
		btnRetry.disable();
		p.add(btnStart = new Button("Start"));
		p.add(btnUndo = new Button("Undo")); //�ǉ�
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
		//���̒l���A�v���b�g�̃p�����[�^�Ƃ���B
		System.out.println("width="+width+" height="+(height+height2+40));

		//�Q�[�����x��
		gameRest1 = param("gameRest1",24);
		gameRest2 = param("gameRest2",10);
		gameLevel1 = param("gameLevel1",2);
		gameLevel2 = param("gameLevel2",3);
		System.out.println("rest1="+gameRest1+" level1="+gameLevel1+
			" rest2="+gameRest2+" level2="+gameLevel2);

		Comp = new Computer(this); //�C���X�^���X����
		myDialog = new MessageDialog(null,"PASSWORD");
		gameInit();
	}
	
	//�Q�[��������
	void gameInit(){
		int x,y;

		//�Q�[����gameInit()�͉��x���Ă΂�邽�ߤ�����ɃX�^�b�N��������
		//���Ȃ���΂Ȃ�Ȃ��B
		myStack = new stack(1000); //�X�^�b�N�̊m��

		rest = 60;
		white_piece = black_piece = 2;
		diff = 0;
		gameFinish = false;
		isTurn = true;
		BLACK = -1; WHITE = 1;
		myPiece = BLACK;
		
		masu = width / 8; //1�R�}�̏c���T�C�Y
		for(y = 0 ; y < 10 ; y++)
			for(x = 0 ; x < 10 ; x++){
				piece[pos(x,y)] = EMPTY;  //��
				if(x == 0 || x == 9 || y == 0 || y == 9)
					piece[pos(x,y)] = WALL;  //����͕�
			}
		
		piece[pos(4,4)] = piece[pos(5,5)] = WHITE;
		piece[pos(4,5)] = piece[pos(5,4)] = BLACK;
		drawScreen();
		drawPiece();
	}
	
	//�Q�[���I�[�o�[
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
		
		//�p�X���[�h�\��
		if(black_piece > white_piece){
			String s;
			int w,h;
			
			//�ȉ��A�h�C�c��
			if(black_piece == 64) s = "Klavier"; //�s�A�m
			else if(black_piece >= 62) s = "Flote"; //�t���[�g
			else if(black_piece >= 60) s = "Blockflote";//���R�[�_�[
			else if(black_piece >= 58) s = "Querflote"; //���J
			else if(black_piece >= 54) s = "Pikkolo";//�s�b�R��
			else if(black_piece >= 50) s = "Orchester";//�I�[�P�X�g��
			else if(black_piece >= 46) s = "Oper"; //�I�y��
			else if(black_piece >= 40) s = "Musik";//���y
			else if(black_piece >= 36) s = "Instrument";//�y��
			else if(black_piece >= 34) s = "Apostel";//�g�k
			else if(black_piece >= 32) s = "Seele";//��
			else if(black_piece >= 30) s = "Weihnachten"; //�N���X�}�X
			else if(black_piece >= 26) s = "Zauber"; //���@
			else if(black_piece >= 20) s = "XChromosom"; //�w���F��
			else if(black_piece >= 16) s = "Xerographie"; //�[���O���t�B�[
			else s = "Nothing!";
			
			//��ʂɂ��\���BIE�΍�?
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
			myDialog.init(s); //�_�C�A���O�\��
		}
	}

	//���S(x,y)�A���ar�̉~��`���A���ݐF�œh��Ԃ��܂��B
	void fillCircle(Graphics g,int x,int y,int r){
		g.fillOval(x-r,y-r,2*r,2*r);
	}
	
	void drawScreen(){
		int i;

		offG.setColor(Color.black);
		offG.fillRect(0,0,width,height);
		offG2.setColor(Color.black);
		offG2.fillRect(0,0,width2,height2);
		
		//�΂̔Ֆʂ�`���B
		offG.setColor(new Color(0,128,0));
		offG.fillRect(0,0,width,height);
		
		//�O�g�����ŕ`���B
		for(i = 0 ; i < 3 ;i++){
			offG.setColor(Color.black);
			offG.drawRect(i,i,width-i*2-1,height-i*2-1);
		}
		
		//�}�X����؂����`���B
		for(i = 0 ; i < 8 ; i++){
			//masu��1�R�}�̃T�C�Y
			offG.drawLine(i*masu,0,i*masu,width);
			offG.drawLine(0,i*masu,width,i*masu);
		}
		
		//����`���܂��B
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

	//��ʂ̉��Ƀ��b�Z�[�W��\�����܂��B
	void dispInfo(String msg){
		Font fnt = new Font("TimesRoman",Font.BOLD,24);
		FontMetrics fm = getFontMetrics(fnt);
		offG2.setColor(new Color(172,223,178));
		offG2.fillRect(0,0,width2,height2);
		offG2.setFont(fnt);
		offG2.setColor(Color.yellow);
		offG2.drawString(msg,0,height2 - fm.getDescent());
		
		//��Ԃ�`�悵�܂��B
		//offG2.setColor(new Color(0,128,0));
		//offG2.fillRect(width2-30,0,30,30);
		if(isTurn == true){ //�v���C���[�̐F
			offG2.setColor( Turn == FIRST ? firstCol : secondCol );
		}else{  //�R���s���[�^�̐F
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
		if(e.target instanceof Choice){ //���X�g�{�b�N�X
			String s = (String)o;
			if(s.equals("COMP")) Versus = COMP;
			if(s.equals("HUMAN")) Versus = HUMAN;
			if(s.equals("FIRST")) Turn = FIRST;
			if(s.equals("SECOND")) Turn = SECOND;
			return true;
		}else if(e.target == btnStart){ //[Start]�{�^��
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
			//�R���s���[�^�ΐ�ŁA�ŏ��̂P��ŁA�v���C���[�����Ȃ��
			if(Versus == COMP && rest == 60 && Turn == SECOND){
				computerThink(); drawPiece();
			}
			//�A���h�D�p�i�ǉ��j
			if(Versus == COMP){
				btnUndo.enable();
				saveUndo();
			}
			return true;
		}else if(e.target == btnRetry){ //[Retry]�{�^��
			btnRetry.disable();
			btnStart.enable();
			btnUndo.disable();
			chVersus.enable();
			chTurn.enable();
			gameInit();
			return true;
		}else if(e.target == btnUndo){ //[Undo]�{�^��
			loadUndo();
			return true;
		}
		
		return false;
	}
	
	//�}�E�X�C�x���g
	public boolean mouseDown(Event e,int x,int y){
		if(btnStart.isEnabled() || gameFinish) return true;
		if(x < 0 || x > width || y < 0 || y > height) return true;
		if(Versus == COMP){
			saveUndo();      //�A���h�D�̂���1��O��ۑ�
			vsComputer(x,y);
		}else{
			vsHuman(x,y);
		}
		
		return true;
	}
	
	//�΃R���s���[�^
	void vsComputer(int x,int y){
		//�R���s���[�^���v�l���̓}�E�X�C�x���g�͎󂯕t���Ȃ��B
		if(isTurn == false) return;
		isTurn = true;
		x /= masu;
		y /= masu;
		x++; y++; //(x,y)��1����n�܂�̂ŕ␳����B
		if(putPiece(x,y,myPiece,true) == 0) return;
		drawPiece();
		
		computerThink();
		drawPiece();

		//�v���C���[���p�X�̊ԁB
		boolean comp = true;
		while(isPutable(myPiece) == false){
			comp = computerThink();
			drawPiece();
			if(comp == false){
				break; //�R���s���[�^���p�X
			}
		}
		
		//comp == true�̂Ƃ��A�v���C���[���u����悤�ɂȂ����B
		//comp == false�̂Ƃ��ǂ�����p�X�B�܂�Q�[���I�[�o�[�B
		if(comp == false || rest == 0 || black_piece == 0 ||
		 	white_piece == 0){
			gameOver();
		}
	}
	
	//�lv.s.�l
	void vsHuman(int x,int y){
		int koma;
		
		if(isTurn == true) koma = myPiece;
		else koma = -myPiece;
		
		x /= masu;
		y /= masu;
		x++; y++; //(x,y)��1����n�܂�̂ŕ␳����B
		
		if(putPiece(x,y,koma,true) == 0) return; //�u���Ȃ�
		drawPiece();
		if(isPutable(-koma)){ //���̐l���u����
			isTurn = !isTurn;
			dispInfo("");
			return;
		}
		if(isPutable(koma) == false || rest == 0 || black_piece == 0 ||
			white_piece == 0){
			gameOver();
		}
	}
	
	//(x,y)�ɃR�}koma��u�������A(dx,dy)�����֑������đ���̃R�}��
	//�����Ԃ��邩�𒲂ׂ܂��B
	//����reverse�̈Ӗ���putPiece���\�b�h�Ɠ����ł��B
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
					myStack.push(pos(xs,ys));  //�X�^�b�N�֕ۑ�
					piece[pos(xs,ys)] = koma;
					xs += dx;
					ys += dy;
				}
			}
			return( n );  //�Ђ�����Ԃ����R�}�̐�
		}
		return 0;
	}
	
	//(x,y)�ɃR�}koma��u���A����̃R�}�������Ԃ��邩�𒲂ׁA
	//���̐����֐��̕Ԃ�l�Ƃ��܂��B
	//����reverse==false�̂Ƃ��́A�Ԃ���R�}�̐��𐔂��邾���ŁA
	//���ۂɂ͔Ղɂ͕ω��͂���܂���B
	int putPiece(int x,int y,int koma,boolean reverse){
		int c;
		
		//��R�}�łȂ��ƒu���Ȃ��B
		if(piece[pos(x,y)] != EMPTY) return 0;
		
		c = 0;
		c += putDirect(x,y,0,-1,koma,reverse); //��
		c += putDirect(x,y,1,-1,koma,reverse); //�E��
		c += putDirect(x,y,1, 0,koma,reverse); //�E
		c += putDirect(x,y,1, 1,koma,reverse); //�E��
		c += putDirect(x,y,0, 1,koma,reverse); //��
		c += putDirect(x,y,-1,1,koma,reverse); //����
		c += putDirect(x,y,-1,0,koma,reverse); //��
		c += putDirect(x,y,-1,-1,koma,reverse); //����
		
		//1�ł��Ђ�����Ԃ��āA�t���O��on�Ȃ�B
		if(c > 0 && reverse){
			//�����̃R�}��Y�ꂸ�ɁI
			piece[pos(x,y)] = koma;
			myStack.push(pos(x,y)); //�X�^�b�N�֕ۑ�
			myStack.push(c);  //�Ԃ����΂̐�
			
			//�e�J�E���g�ϐ����v�Z�B
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
	
	//���O�ɒu�����R�}�ƂЂ�����Ԃ����R�}��߂��B
	void putBack(int koma){
		int n,pos,teki;
		
		n = myStack.pop();
		pos = myStack.pop();
		piece[pos] = EMPTY;
		
		//�J�E���g�ϐ��̊Ҍ�
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
	
	//�R�}koma�̔ԂŒu����Ƃ��낪���邩���ׂ܂��B
	//����Ȃ�true���A�Ȃ��i�܂�p�X�j�Ȃ�false��Ԃ��܂��B
	boolean isPutable(int koma){
		int x,y;
		
		for(y = 1 ; y <= 8 ; y++)
			for(x = 1 ; x <= 8 ; x++)
				if(putPiece(x,y,koma,false) > 0)
					return true;
		return false;
	}
	
	//�R���s���[�^�v�l���[�`��
	boolean computerThink(){
		isTurn = false; //�v�l��
		dispInfo("Now thinking...");
		
		Point bestpos = new Point(-1,-1);
		int teki = myPiece*(-1);

		if(rest >= 59) bestpos = Comp.think1();
		else if(rest > gameRest1) bestpos = Comp.think2(gameLevel1,teki);
		else if(rest > gameRest2) bestpos = Comp.think2(gameLevel2,teki);
		else bestpos = Comp.think3(rest,teki);
		
		isTurn = true; //�v�l�I��
		dispInfo("Player turn");
		
		if(bestpos.x == -1) return false; //�p�X
		putPiece(bestpos.x,bestpos.y,teki,true);
		drawPiece();
		return true;
	}
	
	//---�ȉ��A�A���h�D���[�`��---
	
	void saveUndo(){
		for(int i = 0 ; i < 100 ; i++) savePiece[i] = piece[i];
	}
	
	void loadUndo(){
		int i,c,x,y;
		
		for(i = 0 ; i < 100 ; i++) piece[i] = savePiece[i];
		myStack = new stack(1000); //�X�^�b�N�̊m��
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

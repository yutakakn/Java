//'98/1/5,1/7,1/9
//
//SumCross.java
//�y�J�b�N���z�@with JDK 1.0.2
//
//�s����m�F�t
//�@Appletviewer(WinCafe)
//�@Internet Explorer 4.0
//�@Netscape Navigator 3.0
//
//�s�Q�l�����t
//�@"�p�Y���[ 1997/11����",���E������,410�~
//�@��ؗI��,�L�V��:"���i�m�s�R����",�����o��,1991
//
//Copyright (C)1997-1998 Yutaka Hirata
//All rights reserved.
/*
���g�s�l�k
<APPLET CODE="SumCross.class" WIDTH=428 HEIGHT=378>
	<param name="MS" value=42>
</APPLET>

���p�����[�^
�@MS : 1�̃R�}�̃T�C�Y�i�s�N�Z���j
�@
���V�т���
�@Load.java�Ɏ������悤�ɕ\�����ꂽ�A���������������u���Z�N���X���[�h�v
�@�ł��B�܂�A
�@�@1.�O�p�̒��̐����͂��̗�i�܂��͍s�j�̑��a�B
�@�@2.�e�󔒂ɂ�1����9�܂ł̐������d����������܂��
�@�@�@�܂�A�󔒂̃}�X�̌��͍ő�9�ŁA���a�̍ő��45�ł��B
�@�𖞂����܂��
�@�E�̃{�^������1����9�̐�����I�у}�E�X�ŃN���b�N����ƃR�}��u�����Ƃ�
�@�ł��܂��B���ׂẴR�}�𖄂߁A�������ł���Ύ��̃X�e�[�W�֐i�߂܂��

���R���p�C��
�@Windows95�ł����DOS������
�@�@a:/java/usr/sumcross>javac SumCross.java
�@�ł��B

����������

�@�T���N���X
�@���J�b�N���́i���j�j�R���ɂ��CROSSSUM�̘a���ł��B
�@
 */

import java.awt.*;
import java.applet.*;

//���b�Z�[�W�_�C�A���O
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

public class SumCross extends Applet {
	//���̈�_�u���o�b�t�@
	Image offImg;
	Graphics offG;
	int width,height;

	Button clsBtn,nextBtn; 	        //�f�t�h���i
	int MS;         //�R�}�̃T�C�Y�i�s�N�Z���j
	int number = 1; //�I�����Ă���ԍ��i�P�`�X�j
	int stage = 1;  //�X�e�[�W
	boolean gameClear;              //�Q�[���N���A�t���O
	
	//�J�b�N���f�[�^
	int W,H;                //�J�b�N���̉��Əc�T�C�Y
	int pid,ymax,emptyCnt;  //�f�[�^���A���f�[�^���A��R�}�̐�
	int proc[][];           //�f�[�^
	int ban[][];            //��
	int board[][];          //�Ձi�v���C�p�j
	
	//���ǂݍ��݃N���X
	Load load;
	//���b�Z�[�W�_�C�A���O
	MessageDialog myDialog;

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

    public void init() {
		//�f�t�h���i�̔z�u�B
		Panel p = new Panel();
		p.setLayout(new GridLayout(11,1));
		p.add(nextBtn = new Button("Next"));
		nextBtn.enable(false);
		p.add(clsBtn = new Button("Clear"));
		CheckboxGroup cg = new CheckboxGroup(); //���W�I�{�^��
		p.add(new Checkbox("1",cg,true));
		for(int i = 2 ; i <= 9 ;i++){
			p.add(new Checkbox(""+i,cg,false));
		}
		setLayout(new BorderLayout());
		add("East",p);
		
		//�p�����[�^
		MS = param("MS",42);
		//������Ɏ��s���Ȃ���W,H,...etc�������Ȃ��B
		loadStage( stage );

		//���̏o�͂̒l������html�̃^�O�����߂�B
		width = W * MS; height = H * MS;
		System.out.println("width="+(width+50)+" height="+height);
		offImg = createImage(width,height);
		offG = offImg.getGraphics();
		
		gameInit();
    }
    
	//�X�e�[�W���̃f�[�^��ǂݍ��݂܂��
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
		//�Ƃ肠�����A�O�̃X�e�[�W�̂������Ă����BColor.black�ł������
		//�x��͂Ȃ����A�V�����X�e�[�W�ł͍��������c��ꍇ������̂ŁA
		//�i�D�����
		offG.setColor(Color.white);
		offG.fillRect(0,0,width,height);
		//�X�e�[�W�ɂ��T�C�Y���Ⴄ���ߤ���������邽�тɃR�}�T�C�Y��
		//�Čv�Z����B
		MS = width / W;
		width = W*MS; height = H*MS;  //�c�Ɖ��������A�Ƃ����O��B
		drawScreen();
		drawPiece();
	}
	
	void gameOver(){
		gameClear = true;
		offG.setFont(new Font("TimesRoman",Font.BOLD,24));
		offG.setColor(Color.yellow);
		offG.drawString("STAGE "+stage+" Clear!",0,28);
		repaint();
		//Next�{�^���������ɂ���B
		clsBtn.enable( false );
		if(stage < load.lastStage) nextBtn.enable( true );
		else{ //�S�X�e�[�W�N���A�I�I
			System.out.println("All stages are clear. Bye bye!(^_^)/~");
			//PAMELAH��HIT COLLECTION���:-)
			String pass = "??????????";
			myDialog = new MessageDialog(null,"PASSWORD:"+pass);
			myDialog.init( pass );
		}
	}
    
    //�Q�[����ʂ̕`��
    void drawScreen(){
		int i,j;
		
		//�w�i�͍��B
		offG.setColor(Color.black);
		offG.fillRect(0,0,width,height);
		//�����������B
		offG.setColor(Color.white);
		for(i = 1 ; i <= H - 1 ; i++) offG.drawLine(0,i*MS,width,i*MS);
		for(i = 1 ; i <= W - 1 ; i++) offG.drawLine(i*MS,0,i*MS,height);
		//�΂ߐ��������
		j = 1;
		for(i = W - 1 ; i >= 0 ; i--){
			offG.drawLine(i*MS,0,W*MS,j*MS);
			offG.drawLine(0,i*MS,j*MS,H*MS);
			j++;
		}
		repaint();
	}
    
    //�q���g�A��R�}�Ȃǂ̕`��
    void drawPiece(){
		int i,x,y,wa,sz;

		offG.setColor(Color.white);
		for(y = 1 ; y <= H ; y++)
			for(x = 1 ; x <= W ; x++)
				if(ban[y][x] > 0){ //��R�}��`��
					offG.fillRect((x-1)*MS+1,(y-1)*MS+1,MS-2,MS-2);
				}
		for(i = 0 ; i < pid ; i++){
			//proc[i][2]=�}�X�̐��B�����ł͕K�v�Ȃ��
			y = proc[i][0]; x = proc[i][1]; wa = proc[i][3];
			//2���̐����̓t�H���g�T�C�Y����������������B
			sz = (wa < 10) ? MS/2 : MS/2-3;
			
			offG.setColor(Color.cyan);
			offG.setFont(new Font("Courier",Font.BOLD,sz));
			if(i < ymax){ //���̃q���g�B�t�O�p�B
				offG.drawString(""+wa,(x-1)*MS+MS/2,(y-1)*MS+MS/2);
			}else{ //�c�̃q���g�B
				int hosei = (wa < 10) ? MS/8 : 0;
				offG.drawString(""+wa,(x-1)*MS+hosei,(y-1)*MS+MS);
			}
		}
	}
    
    //(x,y)�ɐ���num��`�悷��B
    void drawNum(int x,int y,int num){
		//��������...
		delNum(x,y);
		//�t�H���g�ݒ肵��...
		Font fnt = new Font("Courier",Font.BOLD,MS-2);
		FontMetrics fm = getFontMetrics(fnt);
		int l = fm.stringWidth(""+num);
		int h = fm.getAscent();
		//�����āA�`���B
		offG.setFont(fnt);
		offG.setColor(new Color(50,50,50));
		offG.drawString(""+num,
		(MS-l)/2 + (x-1)*MS, (y-1)*MS + MS - 2 - (MS-h)/2);
		
		repaint();
	}
	
	//(x,y)���N���A���܂��B
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

	//�C�x���g�n���h��
	public boolean action(Event e,Object o){
		if(e.target == clsBtn){
			//�N���A����
			gameInit();
			return true;
		}else if(e.target == nextBtn){
			//���X�e�[�W��
			if(stage < load.lastStage){ 
				stage++;
				gameInit();
			}
			return true;
		}else{
			//�ǂ̃{�^�����I�����ꂽ���𒲂ׂ�B
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

	//�}�E�X�C�x���g
	public boolean mouseDown(Event e,int mx,int my){
		int x,y;
		
		if(gameClear) return true;
		if(!(mx >= 0 && mx <= width && my >= 0 && my <= height)) return true;
		//�ȍ~�A(1,1)�`�ň������߃C���N�������g���Ă����B
		x = mx / MS + 1;
		y = my / MS + 1;
		if(ban[y][x] == 0) return true; //��łȂ��i�u���Ȃ��j
		//�E�N���b�N�Ȃ�A�u���Ă���R�}�������
		if((e.modifiers & Event.META_MASK) != 0){
			if(board[y][x] > 0){
				board[y][x] = 0;
				delNum(x,y);
			}
		}else{ //���i�����j�N���b�N�Ȃ�A�R�}��u���
			board[y][x] = number;  //�����i�R�}�j��u���B
			drawNum(x,y,number);   //�`��
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
	
	//�u���ꂽ�R�}�̐���Ԃ��
	int getPieceCnt(){
		int x,y,cnt = 0;
		
		for(y = 1 ; y <= H ; y++)
			for(x = 1 ; x <= W ; x++)
				if(board[y][x] > 0) cnt++;
		return( cnt );
	}
	
	//�����H
	boolean isComplete(){
		int x,y;
		
		for(y = 1 ; y <= H ; y++)
			for(x = 1 ; x <= W ; x++)
				if(board[y][x] != ban[y][x]) return false;
		return true;
	}

}

//'97/8/31 - 9/9,9/20,'98/1/16,1/22
//
//Teto.java
//�y�e�g�z�@with JDK 1.0.2
//
//�s�R���p�C�����@�t
//�@�@>javac Teto.java
//
//�s�Q�l�����t
//�@Java�Q�[���v���O���~���O,��������,�\�t�g�o���N,3400�~
//�@�Q�[���{�[�C�Ńe�g���X,�C�V��,1989
//
//�s�Q�l�g�o�t
//�@�r�c����̂i�������W�B���ʂȃQ�[���A�c�[������B
//�@�@http://fujisun1.ise.eng.osaka-u.ac.jp/~eikeda/java/index.html
//
//�@�e�g���X�A�v���b�g
//�@�@http://www.isc.meiji.ac.jp/~ee42042/tetris/tetris.html
//
//�s�X�y�V�����T���N�X�t
//�@�����G�s����(http://www25.seg.kobe-u.ac.jp/syu/)
//
//Copyright (C)1997-1998 Yutaka Hirata
//All rights reserved.
/*
���g����

�@���N���X�t�@�C���Ɠ����f�B���N�g���Ɂulogo.gif�v
�@�@�uteto1.gif�`teto3.gif�v���K�v�B

���p�����[�^
�@
�@���ɂȂ��B

*/

import java.awt.*;
import java.applet.Applet;
import java.awt.image.*;

class TitleCanvas extends Canvas{
	Graphics offG;
	Image offImg;
	Image logoImg[];
	private int rgb,flag,cnt,tflag,width,height;
	
	public TitleCanvas(Graphics offG,Image offImg,Image logoImg[],
			int width,int height){
		this.offG = offG;
		this.offImg = offImg;
		this.logoImg = logoImg;
		this.width = width;
		this.height = height;
		rgb = 0;
		flag = 1;
		cnt = 0;
		tflag = 1;
	}

	public void paint(Graphics g){
		String s = "HIT SPACE KEY";
		int w,h;
		
		offG.setColor(Color.black);
		offG.fillRect(0,0,width,height);
		w = logoImg[cnt].getWidth(this);
		h = logoImg[cnt].getHeight(this);
		offG.drawImage(logoImg[cnt],
			width/2 - w/2,height/2 - height/5 - h/2,null);
		
		Font fnt = new Font("TimesRoman",Font.PLAIN,20);
		FontMetrics fm = getFontMetrics(fnt);
		int sw = fm.stringWidth(s),
			sh = fm.getHeight();
		offG.setFont(fnt);
		offG.setColor(new Color(0,rgb,rgb));
		offG.drawString(s,width/2-sw/2,height/2+2*sh);
		
		s = "Copyright (C)1997-1998 Yutaka Hirata";
		strCenterDisp(s,width/2,height/2+height/5);
		s = "Specail Thanks to Hideyuki Tarumi";
		strCenterDisp(s,width/2,height/2+height/4);		
		//s = "Original Concept, Design and Program by ALEXEY PAZHITNOV";
		//strCenterDisp(s,width/2,height/2+height/3);
		
		g.drawImage(offImg,0,0,null);
	}

	public void update(Graphics g){
		paint(g);
	}

	void strCenterDisp(String s,int x,int y){
		Font fnt;
		FontMetrics fm;
		int sw,sh;
		
		fnt = new Font("Helvetica",Font.PLAIN,12);
		fm = getFontMetrics(fnt);
		sw = fm.stringWidth(s);	sh = fm.getHeight();
		offG.setFont(fnt);
		offG.setColor(Color.white);
		offG.drawString(s,x - sw/2,y);
	}

	void step(){
		//���b�Z�[�W
		rgb += 32*flag;
		if(rgb >= 255){ 
			rgb = 255;
			flag = -1;
		}
		if(rgb <= 0){ 
			rgb = 0;
			flag = 1;
		}
		//�A�j���[�V�����摜
		cnt += tflag;
		if(cnt > 2){ 
			cnt = 2;
			tflag = -1;
		}
		if(cnt < 0){
			cnt = 0;
			tflag = 1;
		}
	}
}

public class Teto extends Applet implements Runnable
{
	Thread thread;
	private Image offImg;
	private Graphics offG;
	static int width,height;
	
	Image logoImg[] = new Image[3];
	boolean gameTitle;
	
	Image blockImg[] = new Image[7];
	
	TitleCanvas tcanvas;
	Game game; //�Q�[���N���X
	int delay; //�Q�[���X�s�[�h
	
	boolean gameEnd;
	
	int down[] = new int[5]; //�L�[����p
	
	//�p�����[�^���擾����B
	public int param(String pname,int def){
		String val = getParameter(pname);
		return (val != null) ? Integer.parseInt(val) : def;
	}

	public String param(String pname,String def){
		String val = getParameter(pname);
		return (val != null) ? val : def;
	}

	public void init(){
		int i;
		
		width = size().width; height = size().height;
		offImg = createImage(width,height); //�_�u���o�b�t�@
		offG = offImg.getGraphics();

		//�^�C�g�����S�摜�̓ǂݍ���
		MediaTracker mt = new MediaTracker(this);
		for(i = 0 ; i < 3 ; i++){
			logoImg[i] = getImage(getDocumentBase(),"logo"+(i+1)+".gif");
			mt.addImage(logoImg[i],0);
		}

		//�s�[�X�̉摜��ǂݍ��ށB
		Image orgImg;
		int DD = Game.DD;
		orgImg = getImage(getDocumentBase(),"teto.gif");
		mt.addImage(orgImg,0);
		try{  //���S�ɉ摜��ǂݎ��B
			mt.waitForID(0);
		}catch(InterruptedException e){}
		//7��ނ̃u���b�N�ɕ�����B
		for(i = 0 ; i < 7 ;i++){
			blockImg[i] = 
				createImage(
				  new FilteredImageSource(orgImg.getSource(),
				    new CropImageFilter(i*DD,0,DD,DD)
				  )
				);
		}
		//�^�C�g���N���X(1998.1.22)
		tcanvas = new TitleCanvas(offG,offImg,logoImg,width,height);
		gameInit();
	}
	
	//�Q�[��������
	void gameInit(){
		delay = 400;
		thread = null;
		gameTitle = true;
		gameEnd = false;
		game = new Game(10,20,this,blockImg);
		for(int i = 0 ; i < down.length ; i++) down[i] = 0;	
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
		if(gameTitle){
			titleDisp(g);
		}else{
			game.paint(g);
		}
	}
	
 	public void run(){
		while( true ){
			try{
				thread.sleep( delay );         //�E�F�C�g
			}catch(InterruptedException e){};
			
			if(gameTitle){ //�^�C�g����ʗp�B
				tcanvas.step();
			}else{
				while(down[0]-- > 0) game.moveLeft();
				while(down[1]-- > 0) game.moveRight();
				while(down[2]-- > 0) game.moveLeftRotate();
				while(down[3]-- > 0) game.moveRightRotate();
				if(down[4] > 0) game.moveDrop();
				for(int i = 0 ; i < down.length ; i++) down[i] = 0;
				
				//�Q�[����i�s�Btrue���Ԃ��Ă�����Q�[���I�[�o�[�B
				if(game.go()){ 
					gameEnd = true;
					stop();
				}
			}
			repaint();        //�ĕ`��
		}
	}
	
	public boolean keyDown(Event e,int key){
		switch(key){
		case Event.LEFT:
		case 'j':
		case 'J':
		case '4':
			//game.moveLeft();
			down[0]++;
			break;
			
		case Event.RIGHT:
		case 'l':
		case 'L':
		case '6':
			//game.moveRight();
			down[1]++;
			break;
			
		case Event.UP:
		case 'k':
		case 'K':
		case 'z':
		case 'Z':
		case '5':
		case '8':
			//game.moveLeftRotate();
			down[2]++;
			break;
			
		case 'x':
		case 'X':
			//game.moveRightRotate();
			down[3]++;
			break;
			
		case Event.DOWN:
		case ' ':
		case '2':
			if(gameTitle){
				gameScreenDisp();
				gameTitle = false;
				delay = 30;
			}else{
				//game.moveDrop();
				down[4]++;
			}
			break;
			
		case 'R': //������x�Q�[�����ĊJ�������ꍇ�B
		case 'r':
			if(gameEnd){
				gameInit();
				start();
			}
			break;
		}
		
		return true;
	}
	
	void titleDisp(Graphics g){
		tcanvas.paint(g);
	}
	
	void gameScreenDisp(){
		int i,j;
		
		//�w�i�����ɁB
		offG.setColor(Color.black);
		offG.fillRect(0,0,width,height);
		
		for(i = 0 ; i < 5 ; i++){
			offG.setColor(new Color(i*10,0,255-i*30));
			offG.drawRect(i,i,width-2*i-1,height-2*i-1);
		}
		for(j = 5 ; j < height -5 ; j += 16)
			for(i = 5 ; i < width -5; i += 16){
				offG.setColor(new Color(128,128,0));
				offG.fillOval(i+2,j+2,2,2);
				offG.setColor(new Color(255,255,0));
				offG.fillOval(i,j,2,2);
			}
		getGraphics().drawImage(offImg,0,0,null);
	}
}

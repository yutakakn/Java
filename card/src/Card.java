/* 2000/7/30 - 7/31 (Original 1997/9/15)
 *
 * Card.java    with Visual Cafe 3.0c(JDK 1.1)
 * [�J�[�h�V���b�t��]
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
	
	static final int CX = 60; //�J�[�h�̃T�C�Y
	static final int CY = 80;
	//�X�y�[�h�A�N���[�o�A�_�C���A�n�[�g�̉摜
	Image cardImg[] = new Image[4];
	//�f�t�h���i
	Panel gPanel;
	Label gMoney;
	TextField gText;
	
	//�e�ϐ�
	int clearMoney; //�Q�[���N���A�ɕK�v�ȋ��z
	int gold; //������
	int bet;  //�q����
	int Answer; //�����i0����3�j
	
	static final int STEP = 60;
	int step;
	
	//�Q�[���ϐ�
	int Len[] = new int[4];
	int Cx[][] = new int[4][STEP];
	int Cy[][] = new int[4][STEP];
	int tmpX[] = new int[4];
	int tmpY[] = new int[4];
	int dirX[] = new int[4];
	int dirY[] = new int[4];
	
	int finish; //�J�[�h��~�t���O
	
	boolean mCheck; //�}�E�X�󂯕t�����t���O
	boolean clearFlag; //�Q�[���N���A�̃t���O
	boolean gameFlag; //�A�j�����̃t���O
	boolean gameLastFlag; //�I�ՃA�j���̃t���O

	int delay; //�Q�[���X�s�[�h
	
	int debug; //�B���p�����[�^�i�f�o�b�O�p�j
	
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
		//�J�[�h�̉摜��ǂݍ��ށB
		Image img = getImage(getDocumentBase(),"card.gif");
		MediaTracker mt = new MediaTracker(this);
		mt.addImage(img,0);
		try{
			mt.waitForID(0);
		}catch(InterruptedException e){}
		//4��ނ̃J�[�h�ɕ�����B16��f������ł���B
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
		//�p�����[�^�̎擾
		getOpt();
		//������
		gameInit();
	}
	
	void getOpt(){
	    clearMoney = 10000;
		//clearMoney = param("clear",10000); 		//�N���A�ɕK�v�ȋ��z
		delay = param("delay",50); //�f�B���C
		debug = param("debug",0);
	}
	
	void gameInit(){
		gold = 200;
		mCheck = false;
		clearFlag = false;
		gameFlag = gameLastFlag = false;
		step = 0;
		
		//�f�t�h�z�u
		gPanel = new Panel();
		gPanel.add(new Label("���v"));
		gPanel.add(gMoney = new Label("  200"));
		gPanel.add(new Label("�|����"));
		gPanel.add(gText = new TextField("100",6));
				
		setLayout(new BorderLayout());
		add("South",gPanel);
		gText.requestFocus();
				
		offG.setColor(Color.black);
		offG.fillRect(0,0,width,height);
		cardDisp(); fastDisp();
		
		//�C�x���g�n���h���̓o�^
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
	
	//�|�����̓��͎��� Enter �ŌĂ΂��B
	void inputGoldListener(ActionEvent e) {
		if (clearFlag) return;
		    
		TextField t = (TextField)e.getSource();
		String s = t.getText();
		try {
			bet = Integer.parseInt(s);
		} catch (NumberFormatException exp){
			//���l�ɕϊ��ł��Ȃ��������B
			return;
		}
		gText.setText("");
		bet /= 100; bet *= 100;  //100�~�P�ʂɂ���B
		//�����W�`�F�b�N
		if(bet > gold || bet < 0 || bet < 100) return;
		animeStart();
	}
	
	//�J�[�h�̑I������ �}�E�X�N���b�N �ŌĂ΂��B
	public void selectCard(MouseEvent e) {
		int i, n, x, y;
		    
		x = e.getX();
		y = e.getY();
		    
		//�}�E�X�C�x���g���󂯕t�������ǂ����B
		if(!mCheck) return;
		mCheck = false;
    		
		n = -1;
		for(i = 0 ; i < 4; i++)
			if( (20 + (20+CX)*i) <= x && x <= (20+(20+CX)*i+CX) ){
				n = i; break;
			}
		if(i == 4) return; //�͈͊O
    		
		offG.setColor(Color.black); offG.fillRect(0,0,width,height);
		answerDisp();
    		
		if(n == Answer){ //�����I�I
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
				offG.drawString("�Q�[���I�[�o�[",100,150);
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
	
	//�Q�[���N���A
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
				thread.sleep( delay );         //�E�F�C�g
			}catch(InterruptedException e){};
			
			doIt();
			repaint();        //�ĕ`��
		}
	}
	
	//�J�[�h�`��
	void oneCardDisp(int x,int y,int n){
		offG.setColor(Color.white);
		offG.drawRoundRect(x,y,CX,CY,3,3);
		offG.drawImage(cardImg[n],x+2,y+2,null);
		offG.drawImage(cardImg[n],x+CX-2-16,y+CY-2-16,null);
		repaint();
	}
	
	//4�����̃J�[�h�̕`��
	void cardDisp(){
		for(int i = 0 ; i < 4 ; i++){
			oneCardDisp(20+(20+CX)*i,20,i);
			//fastDisp();
		}
	}
	
	//�𓚃J�[�h�̕`��
	void answerDisp(){
		for(int i = 0 ; i < 4 ; i++){
			oneCardDisp(Cx[i][STEP-1],Cy[i][STEP-1],i);
			fastDisp();
			wait(200);
		}
	}

	//�i�������j�J�[�h�`��
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

	//4�����̗������J�[�h�`��
	void cardReverseDisp(){
		offG.setColor(Color.black); offG.fillRect(0,0,width,height);
		for(int i = 0 ; i < 4 ; i++){
			offG.setColor(Color.white);
			if(debug == 1 && i == 0){ //�f�o�b�O�p
				offG.setColor(Color.red);
			}
			oneCardReverseDisp(tmpX[i],tmpY[i]);
		}
		repaint();
	}
	
	//�A�j���O�̏���
	void animeStart(){
		int i,j,t;
		
		for(i = 0 ;i < 4;i++){
			Len[i] = getRandom(30)+10;
			dirX[i] = getRandom(3) - 1;
			dirY[i] = getRandom(3) - 1;
		}
		
		//�����_���Ŋe�X�e�b�v�̈ʒu�����߂�B
		for(j = 0 ; j < STEP - 1 ; j++)
			for(i = 0 ; i < 4 ; i++){
				Cx[i][j] = getRandom(200) + 160;
				Cy[i][j] = getRandom(70) + 30;
			}
		//�ŏI�ʒu
		for(i = 0 ; i < 4 ; i++){
			Cx[i][STEP-1] = tmpX[i] = 20 + (20+CX)*i;
			Cy[i][STEP-1] = tmpY[i] = 20;
		}
		//�X�y�[�h�̍ŏI�ʒu������B
		Answer = getRandom(4);
		//����ւ��B
		t = Cx[0][STEP-1]; 
		Cx[0][STEP-1] = Cx[Answer][STEP-1];
		Cx[Answer][STEP-1] = t;
		gameFlag = true;
		step = 0;
		finish = 0;
		gameLastFlag = false;
		
		gText.setEnabled(false);
		
//		gPanel.hide(); //�f�t�h���i�������B
		cardReverseDisp();
	}
	
	//�X���b�hrun()����Ă΂�郁�\�b�h�B�����ł̓A�j���[�V������
	//�g���Ă��܂��B
	void doIt(){
		if(gameFlag){
			int i;
			
			step++; //�X�e�b�v��i�߂�B
			if(step >= STEP-1){
				gameLastFlag = true;
				gameFlag = false;
				finish = 0;
				return;
			}
			for(i = 0 ; i < 4; i++){
				//�Q�[�����Ղ��߂���Ɠ�Փx���グ��B
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
			if(finish == 0xf){ //�J�[�h�����ׂĎ~�܂����ꍇ
				offG.setFont(new Font("TimesRoman",Font.PLAIN,20));
				offG.setColor(Color.blue);
				offG.drawString("�X�y�[�h�͂ǂ�H",20,200);
				repaint();
				mCheck = true;
				gameLastFlag = false;
			
			}else{
				int i,p,q;
				
				for(i = 0 ; i < 4; i++){
					p = intcmp(Cx[i][STEP-1],tmpX[i]); //-1,0,1��Ԃ��B
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

	//0�`max-1�܂ł̐��𗐐��œ��܂��B
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

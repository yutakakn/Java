//'97/8/31 - 9/9,9/20
//
//Game.java
//�y�e�g�s���C���t�z�@with JDK 1.0.2
//
//Copyright (C)1997 Yutaka Hirata
//All rights reserved.

import java.awt.*;
import java.applet.Applet;
import java.awt.image.*;

public class Game extends Canvas{
	static final int DD = 16; //�P�R�}�̃T�C�Y�i�s�N�Z���j
	int board[][];  //�e�g�̔�
	static int xs,ys;  //�Ղ̉��Əc�̃R�}��(�f�t�H���g 10x20)
	
	int width,height; //�e�g�Ղ̃T�C�Y�i�s�N�Z���j
	Image offImg;  //�_�u���o�b�t�@
	Graphics offG;
	Graphics G;
	
	Piece piece;
	int dropX,dropY;  //�������̃u���b�N�ʒu
	Image blockImg[] = new Image[7];
	
	int gameLevel;  //���x���i�O�`�j
	//�e���x���ɍ��킹���u���b�N�������x
	int gameSpeed[] = {25,20,15,13,10,8,6,4,2,1};
	int gameCounter; //�J�E���^
	int deleteLines; //�������s��
	int lineCount[] = new int[5]; //�e������s��
	
	int dropWait;
	
	boolean keyLock = false; //�L�[���b�N�t���O
	boolean gameFinish = false; //�Q�[���I�[�o�[�t���O
	boolean firstFlag = false; //�Q�[���J�n�t���O
	
	ScoreBox Score;  //���_�\���N���X
	NextBox Next; //���u���b�N�\���N���X
	
	//�R���X�g���N�^
	Game(int xs,int ys,Component parent,Image img[]){
		board = new int[xs][ys];
		this.xs = xs;
		this.ys = ys;
		width = xs * DD;
		height = ys * DD;
		
		//�_�u���o�b�t�@�쐬�ɂ͌Ăяo�����̐e�R���|�[�l���g��
		//�K�v�B�ʏ�� this ��n���Ă��΂悢�B
		offImg = parent.createImage(width,height);
		offG = offImg.getGraphics();
		G = parent.getGraphics();
		for(int i = 0 ; i < 7; i++) blockImg[i] = img[i];
		
		Score = new ScoreBox(100,100,parent);
		Next = new NextBox(100,100,parent);
		clear(); //������
	}
	
	//�����������B
	private void clear(){
		int i,x,y;

		//�������Ă���e�g�s�[�X
		piece = new Piece();
		//���������ʒu
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
	
	//�Q�[���I�[�o�[
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
	
	//�e�g�̕`��
	public void paint(Graphics g){
		if(!firstFlag){ //�ŏ��`�悳��Ȃ��̂ŁA����������������ꂽ�B
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
	
	//�Q�[����i�߂�B
	//�Ԃ�l�Ftrue�Ȃ�Q�[���I�[�o�[
	public boolean go(){
		if(gameFinish) return true;

		//�u���b�N�����ɒ��������A���E�̕ǁA�������͂��łɂ���
		//�u���b�N�ɐڐG������B
		if(checkDrop(piece.now,dropX,dropY+1)){
			//���n���Ă�������Ƃ����������B����ɂ��A�������Ă�����Ƃ�
			//�Ԃ͍��E�ɃX���C�h�ł���悤�ɂȂ�͂��ł���B
			if(dropWait < 5){
				dropWait++;
				return false;
			}
			dropWait = 0;
			
			//����ȏ�L�[�͎󂯕t���Ȃ��B
			keyLock = true;
			
			//���������u���b�N��ՂփR�s�[���Œ�B
			for(int y = 0 ; y < piece.now.sizeY() ; y++)
				for(int x = 0 ; x < piece.now.sizeX(); x++){
					int tx = dropX + x; int ty = dropY + y;
					if(tx >= 0 && tx < xs && ty >= 0 && ty < ys)
						if(board[tx][ty] == 0)
							board[tx][ty] = piece.now.block[x][y];
				}
			
			//1�s�����ł��邩�ǂ������ׂ�B
			int delY[] = new int[4];
			int n = checkFullLine(delY);
			if(n > 0){
				//�A�j������
				animeDeleteLine(n,delY);
				
				deleteLine(); //�����B
				deleteLines += n;
				if(deleteLines >= 10){ //10�s�������玟�̃��x����
					gameLevel++;
					if(gameLevel > 9) gameLevel = 9;
					deleteLines %= 10;
					System.out.println("Level:"+gameLevel);
				}
				
				//�V���O���A�_�u���A�g���v���A�e�g�̐����J�E���g�B
				lineCount[n]++;
				//���_�v�Z
				Score.setScore(gameLevel,n); Score.paint(G);
				//Score.disp();
			}
			
			//���̃u���b�N�𐶐��B
			piece.nextPiece();
			//�\��
			Next.setNextBox(piece.next); Next.paint(G);
			
			//�V�����u���b�N�̈ʒu���������B
			dropX = (xs - piece.now.sizeX())/ 2; dropY = 0;
			//��܂ŋl�܂�����Q�[���I�[�o�[
			if(checkDrop(piece.now,dropX,dropY)){
				gameFinish = true;
				gameOver();
				return true;
			}
			keyLock = false; //�L�[���b�N����
		}
		gameCounter++;
		if(gameCounter > gameSpeed[gameLevel]){
			gameCounter = 0;
			dropY++;
		}
		return false;
	}
	
	//�u���b�N��(x,y)�ɒu�������A�ǂ⏰�ɐڐG���邩���ׂ�B
	boolean checkDrop(Block cur,int x,int y){
		int i,j,bx,by,tx,ty;
		
		//�u���b�N�̃T�C�Y�����ׂ�B
		bx = cur.sizeX();
		by = cur.sizeY();
		for(j = 0 ; j < by ; j++)
			for(i = 0 ; i < bx ; i++){
				tx = i + x; ty = j + y;
				if(tx >= 0 && tx < xs && ty >= 0 && ty < ys){
					if(board[tx][ty] > 0 && cur.block[i][j] > 0)
						return true; //�����i�߂Ȃ��B
				}else
					if(cur.block[i][j] > 0) return true; //�����i�߂Ȃ��B
			}
		
		return false;  //���v���I
	}
	
	//�Ղ̂��s��������������ׂ�B
	boolean checkLine(int y){
		for(int i = 0 ; i < xs ; i++)
			if(board[i][y] == 0) return false;
		return true;
	}
	
	//���s�����邩������B
	//�����FdelY[]�͏������s�̂����W�i���΁j������B
	//�Ԃ�l�F�O�i�Ȃ��j�A�P�`�S�i�������s���j
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
	
	//��������s���������̃A�j���[�V�����B
	void animeDeleteLine(int n,int delY[]){
		if(n == 4) offG.setColor(Color.yellow);
		else offG.setColor(Color.white);
		for(int i = 0 ; i < n ; i++){
			offG.fillRect(0,delY[i]*DD,width,DD);
		}
		fastDisp();
		try{ //�f�B���C
			Thread.sleep(100);
		}catch(Exception e){}
	}
	
	//������s���܂Ƃ߂ď����B
	void deleteLine(){
		int i,j,k;
		
		for(i = ys - 1 ; i >= 1 ;i--){
			while(checkLine(i)){
				for(j = i; j >= 1 ; j--)
					for(k = 0 ; k < xs ; k++)
						board[k][j] = board[k][j-1];
				//1�ԏ�͋�B
				for(j = 0 ; j < xs ; j++) board[j][0] = 0;
			}
		}
	}
	
	//�������ւ̈ړ��B
	public void moveLeft(){
		if(keyLock) return;
		if(!checkDrop(piece.now,dropX-1,dropY)) dropX--;
	}

	//�E�����ւ̈ړ��B
	public void moveRight(){
		if(keyLock) return;
		if(!checkDrop(piece.now,dropX+1,dropY)) dropX++;
	}
	
	//�E�����։�]������B
	public void moveRightRotate(){
		if(keyLock) return;
		int w,h,x,y;
		Block tmp;
		
		w = piece.now.sizeX();
		h = piece.now.sizeY();
		tmp = new Block(h,w);  //x,y�t�ɂ���B
		for(y = 0 ; y < h ; y++)
			for(x = 0 ; x < w ; x++){
				tmp.block[y][x] = piece.now.block[ x ][ h - 1 - y ];
			}
		//��]��Փ˂���Ȃ�_���B
		if(checkDrop(tmp,dropX,dropY)) return;
		
		piece.now = new Block(h,w);
		for(y = 0 ; y < w ; y++)
			for(x = 0 ; x < h ;x++)
				piece.now.block[x][y] = tmp.block[x][y];
	}

	//�������։�]������B�f�t�H���g�L�[�i��A�j�j�ł͍���]����B
	public void moveLeftRotate(){
		if(keyLock) return;
		int w,h,x,y;
		Block tmp;
		
		w = piece.now.sizeX();
		h = piece.now.sizeY();
		tmp = new Block(h,w);  //x,y�t�ɂ���B
		for(y = 0 ; y < h ; y++)
			for(x = 0 ; x < w ; x++){
				tmp.block[h - 1 - y][w - 1 - x] = 
					piece.now.block[ x ][ h - 1 - y ];
			}
		//��]��Փ˂���Ȃ�_���B
		if(checkDrop(tmp,dropX,dropY)) return;
		
		piece.now = new Block(h,w);
		for(y = 0 ; y < w ; y++)
			for(x = 0 ; x < h ;x++)
				piece.now.block[x][y] = tmp.block[x][y];
	}
	
	//��C�ɗ���������B
	public void moveDrop(){
		if(keyLock) return;
		while( !checkDrop(piece.now,dropX,dropY+1) ) dropY++;
		paint(G);
	}
	
}


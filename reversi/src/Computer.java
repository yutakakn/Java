//'97/11/24,11/28,11/30,12/2,12/8
//
//Computer.java
//�y���o�[�V�Q�s�v�l���[�`���t�z�@with JDK 1.0.2
//
//Copyright (C)1997 Yutaka Hirata
//All rights reserved.

import java.awt.*;
import java.util.*;

class Computer{
	Riversi2 parent; //�e�R���|�[�l���g
	
	//�R���X�g���N�^
	Computer(Riversi2 parent){
		this.parent = parent;
	}
	
	//0�`n-1�܂ł̗�����Ԃ��B
	int rand(int n){
		int r = new Random().nextInt();
		r = (int)Math.abs(r);
		r %= n;
		return( r );
	}

	int pos(int x,int y){
		return( parent.pos(x,y) );
	}
	
	//---���ՁE�v�l���[�`���i�Ƃ����قǂłȂ�����^^�j---//
	Point think1(){
		int one1[][] = { //COM�����̏ꍇ
			{4,3},{3,4},{5,6},{6,5},
		};
		int one2[][] = {
			//-1��1������ʒu,���i�S�p�^�[���j
			{4,4, 6,5, 4,6, 6,6},
			{4,4, 5,6, 6,4, 6,6},
			{5,5, 3,4, 3,3, 5,3},
			{5,5, 4,3, 3,3, 3,5},
		};
		Point best = new Point(-1,-1);
		
		//COM�����̎�
		if(parent.rest == 60){
			int n = rand(4);
			best.x = one1[n][0];
			best.y = one1[n][1];
		}else{ //rest == 59�B���łP��ڂ̎��B
			int i,x1,y1,x2,y2;
			
			for(i = 0 ; i < 4 ; i++){
				x1 = one2[i][0]; y1 = one2[i][1];
				x2 = one2[i][2]; y2 = one2[i][3];
				
				if(parent.piece[pos(x1,y1)] == parent.WHITE && 
					parent.piece[pos(x2,y2)] == parent.BLACK){
					if(rand(10) < 5){
						best.x = one2[i][4]; best.y = one2[i][5];
					}else{
						best.x = one2[i][6]; best.y = one2[i][7];
					}
					break;
				}
			}
		}
		return( best );
	}

	//---���ՁE�v�l���[�`���i�D��x�j---//
	Point think2(int level,int koma){
		Point bestpos = new Point(-1,-1);
		int x,y,s,min;
		
		min = 9999; //�K���ɑ傫�Ȓl
		for(y = 1 ; y <= 8 ; y++){
			for(x = 1 ; x <= 8 ; x++){
				if(parent.putPiece(x,y,koma,true)>0){
					s = sub(level,-koma,false);
					if(min > s){
						min = s;
						bestpos.x = x;
						bestpos.y = y;
					}
					parent.putBack(koma);
				}
			}
		}
		return( bestpos );
	}
	
	//�ċA�ɂ�葊��̗D��x���ł��������Ȃ�悤�ɒT������B
	//level:��ǂ݃��x���@koma:����̃R�}�@pass:�p�X������true
	int sub(int level,int koma,boolean pass){
		int s,s2,x,y,min;
		boolean done = false;
		
		if(level == 0){
			s = getWeight(koma);
			return( s );
		}
		
		min = 9999; s2 = 0;
		for(y = 1 ; y <= 8 ; y++){
			for(x = 1 ; x <= 8 ; x++){
				if(parent.putPiece(x,y,koma,true)>0){
					s2++;  //�u����ꏊ���J�E���g
					done = true;
					s = sub(level-1,-koma,false);
					if(min > s){
						min = s;
					}
					parent.putBack(koma);
				}
			}
		}
		//�u������A���肪�ŏ��ł���l�ƃR�}�̒u���鐔�̘a��Ԃ��B
		//-min =>��As2 =>���B
		if(done == true) return( -min + s2 );
		if(pass == true){ //�������p�X
			/* �R�}�̐������ꂼ�ꐔ����*/
			s = s2 = 0;
			for(y = 1 ; y <= 8 ; y++)
				for(x = 1 ; x <= 8 ; x++){
					if(parent.piece[pos(x,y)] == koma) s++;
					else if(parent.piece[pos(x,y)] == -koma) s2++;
				}
			if(s > s2) return( 1000 );
			else if(s < s2) return( -1000 );
			else return 0;
		}
		s = sub(level,-koma,true);
		return( -s );
	}
	
	//�d�݌v�Z
	int getWeight(int koma){
		//�R�}�̗D��x
		/*
		int score[]={
			0,  0, 0,  0, 0, 0, 0,  0,  0,0,
			0, 80,-30, 2, 0, 0, 2,-30, 80,0,
			0,-30,-50, 1, 4, 4, 1,-50,-30,0,
			0,  2,  1, 3, 5, 5, 3,  1,  2,0,
			0,  0,  4, 5, 0, 0, 5,  4,  0,0,
			0,  0,  4, 5, 0, 0, 5,  4,  0,0,
			0,  2,  1, 3, 5, 5, 3,  1,  2,0,
			0,-30,-50, 1, 4, 4, 1,-50,-30,0,
			0, 80,-30, 2, 0, 0, 2,-30, 80,0,
			0,  0,  0, 0, 0, 0, 0,  0,  0,0,
		};
		*/
		int score[]={
			0,  0,  0, 0, 0, 0, 0,  0,  0,0,
			0, 80, -6,15,10,10,15, -6, 80,0,
			0, -6,-50, 1, 1, 1, 1,-50, -6,0,
			0, 15,  1, 4, 3, 3, 4,  1, 15,0,
			0, 10,  1, 3, 3, 3, 3,  1, 10,0,
			0, 10,  1, 3, 3, 3, 3,  1, 10,0,
			0, 15,  1, 4, 3, 3, 4,  1, 15,0,
			0, -6,-50, 1, 1, 1, 1,-50, -6,0,
			0, 80, -6,15,10,10,15, -6, 80,0,
			0,  0,  0, 0, 0, 0, 0,  0,  0,0,
		};
		
		int x,y,p,s = 0;
		
		//��Ԃɂ��D��x���ς���B
		if(parent.piece[pos(1,1)] == koma){ //�����̃R�}
			score[pos(1,2)] = score[pos(2,1)] = 70;
			score[pos(2,2)] = 40;
		}else if(parent.piece[pos(1,1)] == -koma){ //����̃R�}
			score[pos(1,2)] = score[pos(2,1)] = 1;
			score[pos(2,2)] = 4;
		}
		if(parent.piece[pos(1,8)] == koma){ //�����̃R�}
			score[pos(1,7)] = score[pos(2,8)] = 70;
			score[pos(2,7)] = 40;
		}else if(parent.piece[pos(1,8)] == -koma){ //����̃R�}
			score[pos(1,7)] = score[pos(2,8)] = 1;
			score[pos(2,7)] = 4;
		}
		if(parent.piece[pos(8,1)] == koma){ //�����̃R�}
			score[pos(8,2)] = score[pos(7,1)] = 70;
			score[pos(7,2)] = 40;
		}else if(parent.piece[pos(8,1)] == -koma){ //����̃R�}
			score[pos(8,2)] = score[pos(7,1)] = 1;
			score[pos(7,2)] = 4;
		}
		if(parent.piece[pos(8,8)] == koma){ //�����̃R�}
			score[pos(8,7)] = score[pos(7,8)] = 70;
			score[pos(7,7)] = 40;
		}else if(parent.piece[pos(8,8)] == -koma){ //����̃R�}
			score[pos(8,7)] = score[pos(7,8)] = 1;
			score[pos(7,7)] = 4;
		}
		
		for(y = 1 ; y <= 8 ; y++){
			for(x = 1 ; x <= 8 ; x++){
				p = pos(x,y);
				if(parent.piece[p] == koma)
					s += score[p];
				else if(parent.piece[p] == -koma)
					s -= score[p];
			}
		}
		return( s );
	}
	
	//---�I�ՁE�v�l���[�`���i�~�j�}�b�N�X�@�j---//
	//�@level:��ǂ݃��x���irest��n���ƍŌ�܂œǂݐ؂�)
	//�@koma:�T������R�}�̎��
	Point think3(int level,int koma){
		Point bestpos = new Point(-1,-1);
		int x,y,val,maxval;
		
		//System.out.println("rest="+parent.rest+" lv="+level+" koma="+koma);
		maxval = -64;
		for(y = 1 ; y <= 8 ; y++)
			for(x = 1 ; x <= 8 ; x++){
				if(parent.putPiece(x,y,koma,true)>0){
					val = minLevel(level,(-1)*koma,false,-64,64);
					if(val > maxval){
						maxval = val;
						bestpos.x = x;
						bestpos.y = y;
					}
					parent.putBack(koma);
				}
			}
		return( bestpos );
	}
	
	//����̃R�}���ŏ��ɂȂ�悤�ɒT���B
	//
	//level:��ǂݎ萔
	//koma:�����ł̓v���C���[�i�l�ԁj���w���B
	//pass:���O�ɓG���p�X�������B
	//alpha:�����i�R���s���[�^�j�̍ŗǎ�Ŏ���΍�
	//beta:����i�l�ԁj�̍ŗǎ�Ŏ���΍�
	//���S�J�����e�̃��\�b�h�����g���B
	int minLevel(int level,int koma,boolean pass,int alpha,int beta){
		int x,y;
		boolean done;
		
		if(level == 0) return( parent.diff );  //��ǂݏI���B
		
		done = false;
		for(y = 1; y <= 8 ; y++)
			for(x = 1 ; x <= 8 ; x++){
				if(parent.putPiece(x,y,koma,true)>0){
					done = true;
					//�΍����ŏ���T���B�܂莩���������������
					//�悤�ȁB�Ȃ��Ȃ�v���C���[�� BLACK(=-1) ����
					//��A����������ΐ΍����������Ȃ�B
					beta = Math.min(beta, 
						maxLevel(level-1,koma*(-1),false,alpha,beta)
					);
					parent.putBack(koma);
					if(beta <= alpha) return beta; //���J�b�g
				}
			}
			
		if(done == true) return beta; //�u����ꏊ���������B
		if(pass == true) return parent.diff; //�p�X�ŁA������p�X�������B
		//�������p�X�Ȃ̂ŁA����ɔԂ��񂷁B
		//��ǂ݂��Ȃ������̂� level �͌��炳�Ȃ��B
		return( maxLevel(level,koma*(-1),true,alpha,beta) );
	}

	//�����̃R�}���ő�ɂȂ�悤�ɒT���B
	//level:��ǂݎ萔
	//koma:�����ł̓R���s���[�^�i�l���鑤�j���w���B
	//pass:���O�ɓG���p�X�������B
	//alpha:�����i�R���s���[�^�j�̍ŗǎ�Ŏ���΍�
	//beta:����i�l�ԁj�̍ŗǎ�Ŏ���΍�
	int maxLevel(int level,int koma,boolean pass,int alpha,int beta){
		int x,y;
		boolean done;
		
		if(level == 0) return( parent.diff );  //��ǂݏI���B
		
		done = false;
		for(y = 1; y <= 8 ; y++)
			for(x = 1 ; x <= 8 ; x++){
				if(parent.putPiece(x,y,koma,true)>0){
					done = true;
					//�΍����ő��T���B�܂莩���������������
					//�悤�ȁB�Ȃ��Ȃ�R���s���[�^�� WHITE(=1) ����
					//��A����������ΐ΍����傫���Ȃ�B
					alpha = Math.max(alpha, 
						minLevel(level-1,koma*(-1),false,alpha,beta)
					);
					parent.putBack(koma);
					if(alpha >= beta) return alpha; //���J�b�g
				}
			}
		if(done == true) return alpha; //�u����ꏊ���������B
		if(pass == true) return parent.diff; //�p�X�ŁA������p�X�������B
		//�������p�X�Ȃ̂ŁA����ɔԂ��񂷁B
		//��ǂ݂��Ȃ������̂� level �͌��炳�Ȃ��B
		return( minLevel(level,koma*(-1),true,alpha,beta) );
	}

}


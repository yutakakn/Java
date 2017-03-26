//'97/11/24,11/28,11/30,12/2,12/8
//
//Computer.java
//【リバーシ２《思考ルーチン》】　with JDK 1.0.2
//
//Copyright (C)1997 Yutaka Hirata
//All rights reserved.

import java.awt.*;
import java.util.*;

class Computer{
	Riversi2 parent; //親コンポーネント
	
	//コンストラクタ
	Computer(Riversi2 parent){
		this.parent = parent;
	}
	
	//0～n-1までの乱数を返す。
	int rand(int n){
		int r = new Random().nextInt();
		r = (int)Math.abs(r);
		r %= n;
		return( r );
	}

	int pos(int x,int y){
		return( parent.pos(x,y) );
	}
	
	//---序盤・思考ルーチン（というほどでないけど^^）---//
	Point think1(){
		int one1[][] = { //COMが先手の場合
			{4,3},{3,4},{5,6},{6,5},
		};
		int one2[][] = {
			//-1と1がある位置,候補（４パターン）
			{4,4, 6,5, 4,6, 6,6},
			{4,4, 5,6, 6,4, 6,6},
			{5,5, 3,4, 3,3, 5,3},
			{5,5, 4,3, 3,3, 3,5},
		};
		Point best = new Point(-1,-1);
		
		//COMが先手の時
		if(parent.rest == 60){
			int n = rand(4);
			best.x = one1[n][0];
			best.y = one1[n][1];
		}else{ //rest == 59。後手で１手目の時。
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

	//---中盤・思考ルーチン（優先度）---//
	Point think2(int level,int koma){
		Point bestpos = new Point(-1,-1);
		int x,y,s,min;
		
		min = 9999; //適当に大きな値
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
	
	//再帰により相手の優先度が最も小さくなるように探索する。
	//level:先読みレベル　koma:相手のコマ　pass:パスしたらtrue
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
					s2++;  //置ける場所をカウント
					done = true;
					s = sub(level-1,-koma,false);
					if(min > s){
						min = s;
					}
					parent.putBack(koma);
				}
			}
		}
		//置けたら、相手が最小である値とコマの置ける数の和を返す。
		//-min =>大、s2 =>小。
		if(done == true) return( -min + s2 );
		if(pass == true){ //両方がパス
			/* コマの数をそれぞれ数える｡*/
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
	
	//重み計算
	int getWeight(int koma){
		//コマの優先度
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
		
		//状態により優先度も変える。
		if(parent.piece[pos(1,1)] == koma){ //自分のコマ
			score[pos(1,2)] = score[pos(2,1)] = 70;
			score[pos(2,2)] = 40;
		}else if(parent.piece[pos(1,1)] == -koma){ //相手のコマ
			score[pos(1,2)] = score[pos(2,1)] = 1;
			score[pos(2,2)] = 4;
		}
		if(parent.piece[pos(1,8)] == koma){ //自分のコマ
			score[pos(1,7)] = score[pos(2,8)] = 70;
			score[pos(2,7)] = 40;
		}else if(parent.piece[pos(1,8)] == -koma){ //相手のコマ
			score[pos(1,7)] = score[pos(2,8)] = 1;
			score[pos(2,7)] = 4;
		}
		if(parent.piece[pos(8,1)] == koma){ //自分のコマ
			score[pos(8,2)] = score[pos(7,1)] = 70;
			score[pos(7,2)] = 40;
		}else if(parent.piece[pos(8,1)] == -koma){ //相手のコマ
			score[pos(8,2)] = score[pos(7,1)] = 1;
			score[pos(7,2)] = 4;
		}
		if(parent.piece[pos(8,8)] == koma){ //自分のコマ
			score[pos(8,7)] = score[pos(7,8)] = 70;
			score[pos(7,7)] = 40;
		}else if(parent.piece[pos(8,8)] == -koma){ //相手のコマ
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
	
	//---終盤・思考ルーチン（ミニマックス法）---//
	//　level:先読みレベル（restを渡すと最後まで読み切る)
	//　koma:探索するコマの種類
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
	
	//相手のコマが最小になるように探索。
	//
	//level:先読み手数
	//koma:ここではプレイヤー（人間）を指す。
	//pass:直前に敵がパスしたか。
	//alpha:自分（コンピュータ）の最良手で取れる石差
	//beta:相手（人間）の最良手で取れる石差
	//※４カ所が親のメソッド等を使う。
	int minLevel(int level,int koma,boolean pass,int alpha,int beta){
		int x,y;
		boolean done;
		
		if(level == 0) return( parent.diff );  //先読み終了。
		
		done = false;
		for(y = 1; y <= 8 ; y++)
			for(x = 1 ; x <= 8 ; x++){
				if(parent.putPiece(x,y,koma,true)>0){
					done = true;
					//石差が最小を探す。つまり自分がたくさん取れる
					//ような。なぜならプレイヤーは BLACK(=-1) だか
					//ら、たくさん取れば石差が小さくなる。
					beta = Math.min(beta, 
						maxLevel(level-1,koma*(-1),false,alpha,beta)
					);
					parent.putBack(koma);
					if(beta <= alpha) return beta; //βカット
				}
			}
			
		if(done == true) return beta; //置ける場所があった。
		if(pass == true) return parent.diff; //パスで、相手もパスだった。
		//自分がパスなので、相手に番を回す。
		//先読みしなかったので level は減らさない。
		return( maxLevel(level,koma*(-1),true,alpha,beta) );
	}

	//自分のコマが最大になるように探索。
	//level:先読み手数
	//koma:ここではコンピュータ（考える側）を指す。
	//pass:直前に敵がパスしたか。
	//alpha:自分（コンピュータ）の最良手で取れる石差
	//beta:相手（人間）の最良手で取れる石差
	int maxLevel(int level,int koma,boolean pass,int alpha,int beta){
		int x,y;
		boolean done;
		
		if(level == 0) return( parent.diff );  //先読み終了。
		
		done = false;
		for(y = 1; y <= 8 ; y++)
			for(x = 1 ; x <= 8 ; x++){
				if(parent.putPiece(x,y,koma,true)>0){
					done = true;
					//石差が最大を探す。つまり自分がたくさん取れる
					//ような。なぜならコンピュータは WHITE(=1) だか
					//ら、たくさん取れば石差が大きくなる。
					alpha = Math.max(alpha, 
						minLevel(level-1,koma*(-1),false,alpha,beta)
					);
					parent.putBack(koma);
					if(alpha >= beta) return alpha; //αカット
				}
			}
		if(done == true) return alpha; //置ける場所があった。
		if(pass == true) return parent.diff; //パスで、相手もパスだった。
		//自分がパスなので、相手に番を回す。
		//先読みしなかったので level は減らさない。
		return( minLevel(level,koma*(-1),true,alpha,beta) );
	}

}


//'97/9/2 - 9/3,9/20
//
//Piece.java
//【テト《ピース》】　with JDK 1.0.2
//
//Copyright (C)1997 Yutaka Hirata
//All rights reserved.

import java.awt.*;
import java.applet.Applet;
import java.util.*;

class Block{
	public int block[][];
	private int xs,ys;
	
	//コンストラクタ
	Block(int x,int y){
		block = new int[x][y];
		xs = x; ys = y;
	}
	
	//定義済みのブロックが渡された場合、メンバへコピー。
	Block(Block s){
		int i,j;
		
		xs = s.sizeX(); ys = s.sizeY();
		block = new int[xs][ys];
		for(j = 0 ; j < ys ; j++)
			for(i = 0 ; i < xs ; i++)
				block[i][j] = s.block[i][j];
	}
	
	public int sizeX(){
		return( xs );
	}
	
	public int sizeY(){
		return( ys );
	}
	
	//debug
	void disp(){
		int x,y;
		for(y = 0 ; y < ys ; y++){
			for(x = 0 ; x < xs; x++)
				System.out.print(block[x][y]);
			System.out.println("");
		}
		System.out.println("");
	}
}

class Piece{
	Block pieces[] = new Block[7]; //ピースは7種類。
	Block now;
	int nowN;
	Block next;
	int nextN;
	
	static int table[][][] = {  // [5][3][3]
		{{0,1,0},{0,1,1},{0,0,1}},
		{{0,2,0},{2,2,0},{2,0,0}},
		{{0,3,0},{0,3,0},{3,3,0}},
		{{0,4,0},{0,4,0},{0,4,4}},
		{{0,5,0},{5,5,5},{0,0,0}},
	};

	//コンストラクタ
	public Piece(){
		for(int i = 0 ; i < 5 ;i++){
			pieces[i] = new Block(3,3);
			for(int y = 0 ; y < 3 ; y++)
				for(int x = 0 ; x < 3 ;x++)
					pieces[i].block[x][y] = table[i][x][y];
		}
		pieces[5] = new Block(3,4);
		pieces[5].block[0][0] = pieces[5].block[0][1] = 
		pieces[5].block[0][2] = pieces[5].block[0][3] = 
		pieces[5].block[2][0] = pieces[5].block[2][1] = 
		pieces[5].block[2][2] = pieces[5].block[2][3] = 0;
		pieces[5].block[1][0] = pieces[5].block[1][1] = 
		pieces[5].block[1][2] = pieces[5].block[1][3] = 6;

		pieces[6] = new Block(2,2);
		pieces[6].block[0][0] = pieces[6].block[0][1] = 
		pieces[6].block[1][0] = pieces[6].block[1][1] = 7;
		
		now = pieces[nowN = getRandom(7)];
		next = pieces[nextN = getRandom(7)];
	}
	
	//次のピースを選ぶ。
	void nextPiece(){
		int x,y;
		x = next.sizeX(); y = next.sizeY();
		now = new Block(x,y);
		now = next;
		nowN = nextN;
		next = pieces[nextN = getRandom(7)];
	}
	
	//デバッグ用
	void disp(){
		int x,y;
		for(y = 0 ; y < now.sizeY() ; y++){
			for(x = 0 ; x < now.sizeX(); x++)
				System.out.print(now.block[x][y]);
			System.out.println("");
		}
		System.out.println("");
	}
	
	//0～max-1までの数を乱数で得ます。
	int getRandom(int max){
		int rand,n;
		
		rand = new Random().nextInt();
		rand = (int)Math.abs(rand);
		n = rand % max;
		return( n );
	}
	
}


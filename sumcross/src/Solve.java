//'98/1/5
//
//Solve.java
//【カックロ】　with JDK 1.0.2
//　《自動解答》
//
//Copyright (C)1997-1998 Yutaka Hirata
//All rights reserved.

import java.awt.*;

class Solve{
	//これは外から与えられる。
	int W,H;  //問題の横、縦のサイズ
	int yoko[][],tate[][]; //ヒントデータ

	int bits[] = new int[10];    /* ビットテーブル */
	int base[][];                /* 候補配列 */
	int proc[][] = new int[100][4]; //ヒントデータ
	int ban[][];                 /* 盤。ban[横][縦] */
	int pid,ymax,emptyCnt;   /* 問題数、横の数、空コマの数 */
	
	/* Comb()関数用の変数 */
	int koho[] = new int[9];
	int ans[] = new int[9];
	int ansBit[] = new int[9];
	int ansSum;
	
	Solve(int W,int H,int yoko[][],int tate[][]){
		this.W = W; this.H = H;
		this.yoko = yoko; this.tate = tate;
		
		ban = new int[H+1][W+1];
		base = new int[H+1][W+1];
		//System.out.println("Solve...");
		init();
		init2();
		sub();
		//ban[][]に解が入っている｡また、proc[][]は問題データ、
		//pidは問題数（縦横とも）、ymaxは横のデータ数（pid-ymaxが
		//縦のデータ数）、emptyCntは空の数である。
	}
	
	int getW(){
		return( W );
	}
	
	int getH(){
		return( H );
	}

	int getPid(){
		return( pid );
	}

	int getYmax(){
		return( ymax );
	}

	int getEmptyCnt(){
		return( emptyCnt );
	}
	
	int[][] getProc(){
		return( proc );
	}

	int[][] getBan(){
		return( ban );
	}

	//---以下､解法ルーチン---

	//16進表示（デバッグ用）
	String hex(int n){
		String s = "0123456789ABCDEF";
		String ret = "";
		int i,j;
		
		for(i = 28 ; i >= 0 ; i -= 4){
			j = (n >> i) & 0xf;
			ret += s.substring(j,j+1);
		}
		return( ret );
	}

	void dispBase()
	{
		int x,y;

		for(y = 1 ; y <= H ; y++){
			for(x = 1 ; x <= W ; x++)
				System.out.print(hex(base[y][x])+" ");
			System.out.println("");
		}
		System.out.println("");
	}
	
	void dispBan()
	{
		int x,y;

		for(y = 1 ; y <= H ; y++){
			for(x = 1 ; x <= W ; x++)
				if(ban[y][x] > 0) System.out.print(ban[y][x]+" ");
				else System.out.print("  ");
			System.out.println("");
		}
		System.out.println("");
	}

	int checkOneBit(int n)
	{
		int s[] = {0x1,0x2,0x4,0x8,0x10,0x20,0x40,0x80,0x100};
		int i;

		for(i = 0 ; i < 9 ; i++)
			if(s[i] == n) return(i + 1);
		return 0;
	}

	/* 組み合わせの生成 */
	void Comb(int n,int bit,int sum,int max)
	{
		int i,b;

		if(n >= max){
			if(sum != ansSum) return;
			for(i = 0 ; i < max ; i++) ansBit[i] |= bits[ans[i]];
			return;
		}

		for(i = 1 ; i <= 9 ; i++){
			b = bits[i];
			if((koho[n] & b) == 0) continue;  /* 候補にない数字は× */
			if((bit & b) != 0) continue; /* 数字は１つずつ */
			ans[n] = i;
			Comb(n + 1, bit | b , sum + i ,max);
		}
	}

	/* 探索ルーチン */
	void sub()
	{
		int i,j,dx,dy,x,y,max,wa,rest,rest2;

		for(y = 1 ; y <= H ; y++)
			for(x = 1 ; x <= W ; x++)
				ban[y][x] = 0;
		rest = emptyCnt;

		while(rest > 0){
			rest2 = rest;  /* 直前の値を記録 */
			for(i = 0 ; i < pid ; i++){
				if(i < ymax){
					dx = 1; dy = 0;
				}else{
					dx = 0; dy = 1;
				}
				y = proc[i][0];
				x = proc[i][1];
				max = proc[i][2];
				/* after...global variable */
				ansSum = proc[i][3];
				for(j = 1 ; j <= max ; j++)
					koho[j-1] = base[y+j*dy][x+j*dx];
				for(j = 0 ; j < max ; j++) ansBit[j] = 0;  /* 候補 */
				Comb(0,0,0,max);   /* 組み合わせ */

				/* 絞り込み */
				for(j = 1 ; j <= max ; j++){
					int tx,ty,n;

					tx = x + j*dx; ty = y + j*dy;
					base[ty][tx] &= ansBit[j - 1];

					if(ban[ty][tx] == 0){
						/* ビットが1になれば解 */
						if((n = checkOneBit(base[ty][tx])) != 0){
							ban[ty][tx] = n;  /* 解 */
							rest--;
						}
					}
				}
			}
			if(rest == rest2){
				System.out.println("Can't solve this program!");
				System.exit(2);
			}
			//System.out.println("rest="+rest+" /"+emptyCnt);
		}
		//以下はデバッグ用の解表示。実際の使用についてはコメントアウト
		//すること。さもないと、答えがユーザに分かってしまうから:-)
		//dispBan();
	}

	void init2()
	{
		int i,j,x,y,max,wa,low,high,bit;

		for(y = 1 ; y <= H ; y++)
			for(x = 1 ; x <= W ; x++)
				ban[y][x] = 0;

		/* 各集合の上限、下限を決める。*/
		for(i = 0 ; i < pid ; i++){
			y = proc[i][0]; x = proc[i][1];
			max = proc[i][2]; wa = proc[i][3];

			/* 1/2*max*(19-max)はマスmaxでの最大値。lowはマスの左端。 */
			low = wa - max * (19-max) / 2 + (10 - max);
			if(low < 1) low = 1;

			/* 1/2*max*(max+1)はマスmaxでの最小値。*/
			high = wa - max * (max + 1) / 2 + max;
			if(high > 9) high = 9;

			bit = (1<<high) - (1<<(low-1));
			/* 枝刈 */
			if(max == 2 && (wa % 2 == 0)){
				bit &= ~bits[wa/2];  /* 除去 */
			}
			if(high - low == max)
				if(low == 1) bit &= ~bits[high-1];
				else bit &= ~bits[low+1];

			/* 候補ビットの記録 */
			for(j = 1 ; j <= max ; j++){
				if(i >= ymax){
					base[y+j][x] = bit & base[y+j][x];
					ban[y+j][x] = 1;
				}else{
					base[y][x+j] = bit;
					ban[y][x+j] = 1;
				}
			}
		}
		/* 空の数を数える。*/
		emptyCnt = 0;
		for(y = 1 ; y <= H ; y++)
			for(x = 1 ; x <= W ; x++)
				if(ban[y][x] > 0) emptyCnt++;
		//System.out.println("Emptycnt="+emptyCnt);
	}

	void init()
	{
		int i;

		/* 2進ビット */
		for(i = 1 ; i <= 9 ; i++) bits[i] = 1 << (i-1);

		pid = 0;
		for(i = 0 ; yoko[i][0] > 0 ; i++){
			proc[pid][0] = yoko[i][0];
			proc[pid][1] = yoko[i][1];
			proc[pid][2] = yoko[i][2];
			proc[pid][3] = yoko[i][3];
			pid++;
		}
		ymax = pid;
		for(i = 0 ; tate[i][0] > 0 ; i++){
			proc[pid][0] = tate[i][0];
			proc[pid][1] = tate[i][1];
			proc[pid][2] = tate[i][2];
			proc[pid][3] = tate[i][3];
			pid++;
		}
		//System.out.println("pid="+pid+" ymax="+ymax);
	}

}

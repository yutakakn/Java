/* 2001/1/6 - 2/24
 *
 * Bine.java - [Java Binary Editor]
 *   with Visual Cafe 4.1(JDK 1.1/Java2)
 *
 * Copyright (C)2001 YUTAKA
 * All rights reserved.
 */
 
import java.awt.*;
import java.io.*;
 
public class Bine 
{
    //エディタのデフォルト設定情報（変更不可）
    static final int defFontSize = 16;                   //フォントサイズ
    static final String defFontName = "Monospaced";      //フォント名
    static final int defAppWidth = 640, defAppHeight = 400; //Windowサイズ
    static final Color defDefColor = Color.lightGray;        //初期背景色
    static final int defAppLeftX = 150, defAppLeftY = 100;  //初期配置位置
    static final int defScrollLine = 2;        //スクロール行数(=1画面/ScrollLine)
    
    static final int ASCII_MODE = 0, SJIS_MODE = 1, EUC_MODE = 2;
    static final int defCharCode = ASCII_MODE;  //コード種別
    
    static final int maxAppWidth = 1280, maxAppHeight = 1024; //最大解像度
    
    //エディタの設定情報（変更可）
    static int FontSize = defFontSize;         
    static String FontName = defFontName;   
    static int AppWidth = defAppWidth, AppHeight = defAppHeight; 
    static Color DefColor = defDefColor;      
    static int AppLeftX = defAppLeftX, AppLeftY = defAppLeftY; 
    static int ScrollLine = defScrollLine;       
    static int CharCode = defCharCode;  
    
    static final String Version = "Ver 1.0";    //バージョン
    static final String Title = "Java Binary Editor";
    
    static boolean Java2VM = false;
    
    
    //一番始めに呼ばれるエントリポイント
    public static void main(String args[]) {
        BineFrame app = new BineFrame();
        String s;
        
        app.setVisible(true);
        app.init_app();
        
        s = System.getProperty("java.version");
        if (s.indexOf("1.3.0") != -1) { //Java2 SDK1.3.0の場合
            Java2VM = true;
        } else {
            Java2VM = false;
        }
    }
    
    //初期設定値のロード
    public static void load_init_config() {
    	Bine.FontSize = Bine.defFontSize;
	    Bine.FontName = Bine.defFontName;
	    Bine.AppWidth = Bine.defAppWidth;
	    Bine.AppHeight = Bine.defAppHeight;
	    Bine.DefColor = Bine.defDefColor;
	    Bine.AppLeftX = Bine.defAppLeftX;
	    Bine.AppLeftY = Bine.defAppLeftY;
	    Bine.ScrollLine = Bine.defScrollLine;
	    //Bine.CharCode = Bine.defCharCode;
    }

    //デバッグ用
    public static void debugPrint(String str) {
        System.out.println(str);
    }
    
    public static void debugPrint(int num) {
        System.out.println(num + "");   
    }
    
    public static void debugPrint(char num) {
        System.out.println(num + "");   
    }
    
    public static void debugPrint(String id, int num) {
        System.out.println(id + ":" + num);   
    }
    
    public static void debugPrint(int num, int num2) {
        System.out.println(num + " " + num2);   
    }
    
}

/* 
 ★JAR化と起動方法
 
 　通常、javaコマンドでコンパイルした場合はclassファイルが
 　作られる。classの数が多い場合はJAR化して1本にしておくと
 　よい。
 
 　　Main-Class: Bine
　 とだけ記述したテキストファイル（Manifestファイル）を用意
　 しておき、コマンドラインから
 
 　　>jar cvfm Bine.jar MANIFEST.MF *.class
 
  とすれば、Bine.jarが出来上がる。
  
  JDK1.1では無理だが、Java2からはJARの起動に対応しているため、
  
  　>java -jar Bine.jar
  
  とJavaアプリケーションを起動できる。DOS窓が表示されるのが嫌
  なら、
  
  　>javaw -jar Bine.jar
  
  とする手もある。
  
 */

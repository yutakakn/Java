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
    //�G�f�B�^�̃f�t�H���g�ݒ���i�ύX�s�j
    static final int defFontSize = 16;                   //�t�H���g�T�C�Y
    static final String defFontName = "Monospaced";      //�t�H���g��
    static final int defAppWidth = 640, defAppHeight = 400; //Window�T�C�Y
    static final Color defDefColor = Color.lightGray;        //�����w�i�F
    static final int defAppLeftX = 150, defAppLeftY = 100;  //�����z�u�ʒu
    static final int defScrollLine = 2;        //�X�N���[���s��(=1���/ScrollLine)
    
    static final int ASCII_MODE = 0, SJIS_MODE = 1, EUC_MODE = 2;
    static final int defCharCode = ASCII_MODE;  //�R�[�h���
    
    static final int maxAppWidth = 1280, maxAppHeight = 1024; //�ő�𑜓x
    
    //�G�f�B�^�̐ݒ���i�ύX�j
    static int FontSize = defFontSize;         
    static String FontName = defFontName;   
    static int AppWidth = defAppWidth, AppHeight = defAppHeight; 
    static Color DefColor = defDefColor;      
    static int AppLeftX = defAppLeftX, AppLeftY = defAppLeftY; 
    static int ScrollLine = defScrollLine;       
    static int CharCode = defCharCode;  
    
    static final String Version = "Ver 1.0";    //�o�[�W����
    static final String Title = "Java Binary Editor";
    
    static boolean Java2VM = false;
    
    
    //��Ԏn�߂ɌĂ΂��G���g���|�C���g
    public static void main(String args[]) {
        BineFrame app = new BineFrame();
        String s;
        
        app.setVisible(true);
        app.init_app();
        
        s = System.getProperty("java.version");
        if (s.indexOf("1.3.0") != -1) { //Java2 SDK1.3.0�̏ꍇ
            Java2VM = true;
        } else {
            Java2VM = false;
        }
    }
    
    //�����ݒ�l�̃��[�h
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

    //�f�o�b�O�p
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
 ��JAR���ƋN�����@
 
 �@�ʏ�Ajava�R�}���h�ŃR���p�C�������ꍇ��class�t�@�C����
 �@�����Bclass�̐��������ꍇ��JAR������1�{�ɂ��Ă�����
 �@�悢�B
 
 �@�@Main-Class: Bine
�@ �Ƃ����L�q�����e�L�X�g�t�@�C���iManifest�t�@�C���j��p��
�@ ���Ă����A�R�}���h���C������
 
 �@�@>jar cvfm Bine.jar MANIFEST.MF *.class
 
  �Ƃ���΁ABine.jar���o���オ��B
  
  JDK1.1�ł͖��������AJava2�����JAR�̋N���ɑΉ����Ă��邽�߁A
  
  �@>java -jar Bine.jar
  
  ��Java�A�v���P�[�V�������N���ł���BDOS�����\�������̂���
  �Ȃ�A
  
  �@>javaw -jar Bine.jar
  
  �Ƃ���������B
  
 */

//'97/10/16 - 10/19
//
//Stereo.java
//�y�����_���E�h�b�g�E�X�e���I�O�����z�@with JDK 1.0.2
//
//�s����m�F�t
//�@Appletviewer(JDK 1.0.2�t���j
//�@Internet Explorer 4.0
//
//�s�Q�l�����t
//�@Internet Language(3)�i����������,�͐����Y,�Z�p�]�_��,1980�~
//�@�A���S���Y�����ȏ�,�O�㒼��,�b�p�o�Ŏ�,2700�~
//�@�̂�N88-BASIC�̃v���O����
//
//Copyright (C)1997 Yutaka Hirata
//All rights reserved.
/*
���g����
<applet code="Stereo.class" width=500 height=350>
</applet>

���p�����[�^
�@���ɂȂ��B
�@
���V�т���
�@����͂��ׂă}�E�X�ōs���܂��B
�@�v���_�E�����j���[���猩������ނ̃X�e���I�O������I�����܂��B
�@start�{�^���������ƃ����_���h�b�g�X�e���I�O�������\������͂�
�@�߂܂��B�����͕��s�@�ƌĂ΂����̂ł��B

 */

import java.applet.Applet;
import java.awt.*;

public class Stereo extends Applet implements Runnable
{
	//�_�u���o�b�t�@
	Image offImg;
	Graphics offG;
	int width,height;

	Thread thread = null;
	
	int selectNumber = 1;  //�X�e���I�O�����̎�ނ̑I��
	int Loop;
	
	Button startBtn,stopBtn;
	Choice L;

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

	public void init(){
		//�f�t�h�z�u
		L = new Choice();
		L.addItem("TYPE1"); L.addItem("TYPE2"); L.addItem("TYPE3");
		L.addItem("TYPE4"); L.addItem("TYPE5"); L.addItem("TYPE6");
		L.addItem("TYPE7"); L.addItem("TYPE8");
		Panel p = new Panel();
		p.add(L);
		p.add(startBtn = new Button("start"));
		p.add(stopBtn = new Button("stop"));
		p.setBackground(Color.black);
		stopBtn.disable();
		setLayout(new BorderLayout());
		add("South",p); //�擪�͑啶���I
		
		width = size().width; height = size().height;
		offImg = createImage(width,height);
		offG = offImg.getGraphics();
		offG.setColor(Color.black);
		offG.fillRect(0,0,width,height);
		repaint();
	}

	public void y_start(){
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
	
 	public void run(){
		while( true ){
			try{
				thread.sleep( 50 );
			}catch(InterruptedException e){};
			
			if(Loop < 1000){
				Loop++;
				sub();
			}
		}
	}

	public void paint(Graphics g){
		g.drawImage(offImg,0,0,this); //���̈�
	}

	public void update(Graphics g){
		paint(g);
	}
	
	void fastPaint(){
		paint(getGraphics());
	}
	
	//�C�x���g�n���h��
	public boolean action(Event e,Object o){
		if(e.target == startBtn){
			startBtn.disable();
			stopBtn.enable();
			L.disable();
			offG.setColor(Color.black); offG.fillRect(0,0,width,height);
			Loop = 0;
			thread = null;
			y_start(); //�X���b�h�J�n
			
			sub();
			
			return true;
		}else if(e.target == stopBtn){
			startBtn.enable();
			stopBtn.disable();
			L.enable();
			stop(); //�X���b�h��~
			
			return true;
		}else if(e.target instanceof Choice){
			String s = (String)o; //�I�����𓾂�B
			selectNumber = Integer.parseInt( s.substring(4,5) );
			//System.out.println("select="+selectNumber);
			
			return true;
		}
		
		return false;
	}
	
	//---�ȉ��A�`��֐��Q---

	//��Ֆڏ󂾂Ǝv��:-)
	double func8(double x,double y){
		return( 
			( (y/2 + 5) % 2 + (x/2 + 5) % 2 ) / 3
		);
	}

	//�ۂ����̂��ڂ��ڂ���яo������ւ��񂾂肵�Ă���悤�ȁB
	double func7(double x,double y){
		return( Math.cos(x) * Math.cos(y/2) );
	}

	//�~�}�[�N���E�֌X�΂��Ă���悤�ȁB
	double func6(double x,double y){
		int t;
		
		t = (Math.abs(y + x) < 2 || Math.abs(y - x) < 2 ) ? 1:0;
		return( - t * x / 10 );
	}

	//�h�[�i�b�c�������֌X�΂��Ă���悤�ȁB
	double func5(double x,double y){
		double r;
		int t;
		
		r = Math.sqrt(x*x + y*y);
		t = (r > 5 && r < 8) ? 1 : 0;
		return( - t * y / 8 );
	}

	//�h�[�i�b�c�����݂ɌX�΂��čL�����Ă���悤�ȁi���t�ł̓��Y�C�c�j�B
	double func4(double x,double y){
		double r;
		r = Math.sqrt(x*x + y*y);
		if(r >= 10.0) return( 0.0 );
		if(r % 4.0 > 2.0) return( y / 10.0 );
		else return( - y / 10.0 );
	}

	//�g��
	double func3(double x,double y){
		return( Math.sin(x + Math.sin(y)) );
	}
	
	//�~�����˂��o�Ă���悤�ȁB
	double func2(double x,double y){
		double r;
		
		r = Math.sqrt(x*x + y*y);
		if(r == 0.0) return( 1.0 );
		else return( Math.sin(r) / r );
	}
	
	//�~����яo���悤�ȁB
	double func1(double x,double y){
		return( 5.0 - Math.sqrt(x*x + y*y) );
	}
	
	double func(double x,double y){
		double ret;
		
		switch(selectNumber){
		case 1:
			ret = func1(x,y);
			break;
		case 2:
			ret = func2(x,y);
			break;
		case 3:
			ret = func3(x,y);
			break;
		case 4:
			ret = func4(x,y);
			break;
		case 5:
			ret = func5(x,y);
			break;
		case 6:
			ret = func6(x,y);
			break;
		case 7:
			ret = func7(x,y);
			break;
		case 8:
			ret = func8(x,y);
			break;
		default:
			ret = 0.0;
		}
		
		//-1.0�`1.0�ɐ��K��
		if(ret > 1.0) ret = 1.0;
		if(ret < -1.0) ret = -1.0;
		return( ret );
	}
	
	//�X�e���I�O�����`�惋�[�`��
	void sub(){
		double xmin,xmax,ymin,ymax;
		double period,r,x,y,z;

		//���̕ӂ̒l�͎��R�ɕύX�B
		xmax = 10; xmin = -10;
		ymax = 10; ymin = -10;
		period = (xmax - xmin) / 6.0; //����
		r = 0.1 * period;
		y = Math.random() * (ymax - ymin) + ymin;
		z = func(xmin,y);
		x = xmin + Math.random() * (Math.abs(period + r*z));
		do{
			offG.setColor(Color.white);
			offG.fillRect((int)(x*15 + width/2),(int)(y*15 + height/2),2,2);
			fastPaint();
			z = func(x+period/2,y);
			x = x + period - r * z;
		}while(x <= xmax);
	}
}


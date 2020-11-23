import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.imageio.ImageIO;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;

import ai.Main;
import ai.Structure;

public class Dino {

	
	
	public static void main(String[] args) throws AWTException {
		// TODO Auto-generated method stub
		Structure str = new Structure();
		str.inputX = 14; str.inputY = 1; str.inputZ = 1; str.input = new float[str.inputX*str.inputY*str.inputZ];
		//input: 0-obstaclesize,1obst0x,2obst0y,3obst0w, 4/5/6, 8/9/10, 11/12/13, 14-runningTime
		
		Main main = new Main(str);
		
		main.ANN(str.inputX*str.inputY*str.inputZ);
		main.addHidden(128, 0);
		main.addHidden(128, 0);
		main.addHidden(2, 2);//3 te olur
		main.create();
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		WebDriver driver = new ChromeDriver();
		
		try {
		driver.get("chrome://dino/");
		}catch(WebDriverException e) {
			
		}
		Robot robot = new Robot();
	
		JavascriptExecutor js = (JavascriptExecutor)driver;
		
	//	js.executeScript("Runner.config.ACCELERATION=0");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		int oldstatus = 0;
		long time;
		int iter = 0;
		
		
		float[] oldinput = null;
		
		ArrayList<Float> reward = new ArrayList<>();
		ArrayList<Float> Q = new ArrayList<>();
		ArrayList<Integer> S = new ArrayList<>();

		
		
		
		while(true) {
	//		System.out.println(js.executeScript("return Runner.instance_.tRex"));
			
			/*
				if((boolean)js.executeScript("return Runner.instance_.crashed") == true) {	
			robot.keyPress(KeyEvent.VK_SPACE);
			robot.keyRelease(KeyEvent.VK_SPACE);
		}
		
		Image img = new Robot().createScreenCapture(new Rectangle(0,360,1360,160)).getScaledInstance(272, 32, Image.SCALE_DEFAULT);
		BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
		Graphics g = bi.getGraphics();
		g.drawImage(img, 0,0,null);
		g.dispose();
		
		for(int y = 0; y < 32; y++) {
			for(int x = 0; x < 272; x++) {
				Color color = new Color( bi.getRGB(x, y));
				
				str.input[x + y*272] = (color.getRed()+color.getGreen()+color.getBlue())/255;
			}		
		}
		
		
		int sonuc = main.SonucuAl();
		System.out.println(sonuc);
		if(sonuc  == 1) {
			robot.keyPress(KeyEvent.VK_SPACE);
			robot.keyRelease(KeyEvent.VK_SPACE);
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if((boolean)js.executeScript("return Runner.instance_.crashed") == true) {
				main.besle(0);
			} 
			
			
			
		} else {
			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if((boolean)js.executeScript("return Runner.instance_.crashed") == true) {
				main.besle(1);
			}
			
			
			
		}
		
		*/
		
			int newstatus = 0;
			float jumpvelocity = 0;
			int[] bp = {1,0};
			
			
			String newSS = (String)js.executeScript("return Runner.instance_.tRex.status");
			if(newSS.equals("CRASHED")) {
				newstatus = -1;
			} else if(newSS.equals("RUNNING")) {
				newstatus = 0;
			} else if(newSS.equals("JUMPING")) {
				newstatus = 1;
			}
			
			if(newstatus == 0) {
			
			int sayi = 0;
			for(int i = 0; i < 3; i++) {
			try {
				String st1 = "return Runner.instance_.horizon.obstacles["+i+"].xPos";
				String st2 = "return Runner.instance_.horizon.obstacles["+i+"].yPos";
				String st3 = "return Runner.instance_.horizon.obstacles["+i+"].width";
				
				long l = (long) js.executeScript(st1);
				str.input[1+i*3] = (float)l/100;
				l = (long) js.executeScript(st2);
				str.input[2+i*3] = (float)l/100;
				l = (long) js.executeScript(st3);
				str.input[3+i*3] =	(float)l/100;
				sayi++;
			} catch(JavascriptException e) {
				str.input[1+i*3] = 0;
				str.input[2+i*3] = 0;
				str.input[3+i*3] = 0;
			}
			}
			
			str.input[0] = sayi;
			
			str.input[13] = (float)((double)js.executeScript("return Runner.instance_.currentSpeed")/6);
			
			
			
			int sonuc = main.SonucuAl();
			oldstatus = sonuc;
			System.out.println(sonuc);
			
//			if(sonuc != 2) robot.keyRelease(KeyEvent.VK_DOWN);
			
			if(sonuc  == 1) {
				
				robot.keyPress(KeyEvent.VK_SPACE);
				robot.keyRelease(KeyEvent.VK_SPACE);
				
			} /*else if(sonuc == 2) {//3 te
				robot.keyPress(KeyEvent.VK_DOWN);
				
			}*/                                          
			
			oldinput = str.input.clone();
			
			} else if(newstatus == -1) {
				
				
		//		str.lr = (float) ( 0.002/(Math.sqrt(highest/(1000))));
		//		System.out.println("str:"+str.lr);
				
				if(oldstatus == 1) {
					jumpvelocity = Float.parseFloat(((js.executeScript("return Runner.instance_.tRex.jumpVelocity").toString())));
					System.out.println(jumpvelocity);
					if(jumpvelocity <= 0) { 
						System.out.println("Rechanged "+ oldstatus);
						oldstatus = 0;
						str.input = oldinput.clone();
						main.SonucuAl();
					}
				}
				main.besle(bp[oldstatus]);
				System.out.println("old: "+oldstatus);
				
				
				
				while(!((String)js.executeScript("return Runner.instance_.tRex.status")).equals("RUNNING")) {
					//PASS
					robot.keyPress(KeyEvent.VK_SPACE);
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					robot.keyRelease(KeyEvent.VK_SPACE);
				}
				
			}
			
			
			
			
			
			
		/*	int newstatus = 0;
			float jumpvelocity = 0;
			int[] bp = {1,0};
			
			
			
			
			String newSS = (String)js.executeScript("return Runner.instance_.tRex.status");
			if(newSS.equals("CRASHED")) {
				newstatus = -1;
				if(iter>1) reward.add(-4f);
			} else if(newSS.equals("RUNNING")) {
				newstatus = 0;
				if(iter>1) reward.add(0.03f);
			} else if(newSS.equals("JUMPING")) {
				newstatus = 1;
				if(iter>1) reward.add(0.03f);
			}
			
			if(newstatus == 0) {
			
			int sayi = 0;
			for(int i = 0; i < 3; i++) {
			try {
				String st1 = "return Runner.instance_.horizon.obstacles["+i+"].xPos";
				String st2 = "return Runner.instance_.horizon.obstacles["+i+"].yPos";
				String st3 = "return Runner.instance_.horizon.obstacles["+i+"].width";
				
				long l = (long) js.executeScript(st1);
				str.input[1+i*3] = (float)l/100;
				l = (long) js.executeScript(st2);
				str.input[2+i*3] = (float)l/100;
				l = (long) js.executeScript(st3);
				str.input[3+i*3] =	(float)l/100;
				sayi++;
			} catch(JavascriptException e) {
				str.input[1+i*3] = 0;
				str.input[2+i*3] = 0;
				str.input[3+i*3] = 0;
			}
			}
			
			str.input[0] = sayi;
			
			str.input[13] = (float)((double)js.executeScript("return Runner.instance_.currentSpeed")/6);
			
			
			
			int sonuc = main.SonucuAl();
			oldstatus = sonuc;
			if(sonuc  == 1) {
				robot.keyPress(KeyEvent.VK_SPACE);
				robot.keyRelease(KeyEvent.VK_SPACE);
	
			}
			Q.add(str.layers.get(str.layers.size()-1)[sonuc]);
			S.add(sonuc);
			
			System.out.println("Q:"+Q.get(iter)+" S:"+S.get(iter));
			
			oldinput = str.input.clone();
			iter++;
			} else if(newstatus == -1) {
				
				
		//		str.lr = (float) ( 0.002/(Math.sqrt(highest/(1000))));
		//		System.out.println("str:"+str.lr);
				
				float Rt = 0;
				
				for(int i = 0; i <  reward.size(); i++) {
					Rt += (float)reward.get(i)*Math.pow(0.999f, reward.size()-1-i);
				}
				
				System.out.println("Rt:"+Rt);
				main.Rl(Rt, Q.get(Q.size()-1), S.get(S.size()-1));
				
				reward = new ArrayList<>();
				Q = new ArrayList<>();
				S = new ArrayList<>();
				iter = 0;
				
				
				while(!((String)js.executeScript("return Runner.instance_.tRex.status")).equals("RUNNING")) {
					//PASS
					robot.keyPress(KeyEvent.VK_SPACE);
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					robot.keyRelease(KeyEvent.VK_SPACE);
				}
			}
			
			
			
			
			*/
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			try {
				Thread.sleep(17);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
		}
	}

}

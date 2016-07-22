/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.image.film;

import core.color.Color;
import core.color.RGBSpace;
import core.color.XYZ;
import java.nio.IntBuffer;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;

/**
 *
 * @author user
 */
public class Framebuffer {
    private final int w, h;
    
    private Color[] colorAccum = null;
    public  Color[] tonePixels = null;
    
    private final int size;
    
    public Framebuffer(int width, int height)
    {
        this.w = width;
        this.h = height;
        this.colorAccum = new Color[width * height];
        this.tonePixels = new Color[width * height];
        
        this.size = w * h;
        
        for(int i = 0; i< size; i++)
        {
            colorAccum[i] = new Color();
            tonePixels[i] = new Color();
        }
    }
    
    public Framebuffer copy()
    {
        return null;
    }
        
    public void clear()            
    {
        for(int i = 0; i<size; i++)
        {
            colorAccum[i].setBlack();
            tonePixels[i].setBlack();
        }
    }
    
    public void add(Color color, int x, int y)
    {       
        if(!color.isBad())
            colorAccum[index(x, y)].addAssign(color);
        else
            System.out.println("Color is bad " +color);
    }
    
    public void add(Color color, float x, float y)
    {
        add(color, (int)x, (int)y);
    }
    
    public int getWidth()
    {
        return w;
    }
    
    public int getHeight()
    {
        return h;
    }
    
    public void updatePixels(float scale)
    {    
        //Scale accumulation
        for(int index = 0; index<size; index++)                  
            tonePixels[index].setColor(colorAccum[index].mul(scale));
        
        //Average luminance
        float aveLum = 0f;
        float N = 0f;
        for(int index = 0; index<size; index++)
            if(tonePixels[index].luminance()> 0)
            {
                aveLum += Math.log(0.01 + tonePixels[index].luminance());
                N++;
            }
        aveLum /= N;
        aveLum = (float)Math.exp(aveLum);
                 
        System.out.println(aveLum);
               
        for(Color c : tonePixels)   
        {
            float L = c.luminance();
            XYZ xyz = RGBSpace.convertRGBtoXYZ(c);
            float Lscaled = 0.18f * (L/aveLum);
            
            xyz.xyz();
            xyz.setY(Lscaled);
            xyz.xyYtoXYZ();
            
            c.setColor(RGBSpace.convertXYZtoRGB(xyz));
            c.setColor(c.simpleGamma());
        }                     
    }
    
     public Image getImage()
    {
        int[] arrayBuffer = new int[w * h];
        
        WritableImage wImage = new WritableImage(w, h); 
        WritablePixelFormat<IntBuffer> format = WritablePixelFormat.getIntArgbInstance(); 
                
        for(int i = 0; i < arrayBuffer.length; i++)
            arrayBuffer[i] = tonePixels[i].toRGBA(1);
                   
        wImage.getPixelWriter().setPixels(0, 0, w, h, format, arrayBuffer, 0, w);
        
        return wImage;
    }
     
    public Image getImageNull()
    {
        WritableImage wImage = new WritableImage(w, h); 
       
        for(int j = 0; j<h; j++)
            for(int i = 0; i<w; i++)
                wImage.getPixelWriter().setColor(i, j, javafx.scene.paint.Color.BLACK);
        
        return wImage;
    }
    
    private int index(int x, int y)
    {
        return w * y + x;
    }
    
}

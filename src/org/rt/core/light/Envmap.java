/* 
 * The MIT License
 *
 * Copyright 2016 user.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.rt.core.light;

import org.rt.core.AbstractBackground;
import org.rt.core.Scene;
import org.rt.core.coordinates.Point2f;
import org.rt.core.coordinates.Point3f;
import org.rt.core.coordinates.Vector3f;
import org.rt.core.color.Color;
import org.rt.core.coordinates.Point2i;
import org.rt.core.image.HDR;
import org.rt.core.image.reader.HDRBitmapReader;
import org.rt.core.math.BoundingSphere;
import org.rt.core.math.Distribution2D;
import org.rt.core.math.FloatValue;
import org.rt.core.math.Frame;
import org.rt.core.math.Ray;
import org.rt.core.math.Rng;
import org.rt.core.math.SphericalCoordinate;
import org.rt.core.math.Utility;
import static org.rt.core.math.Utility.PI_F;
import static org.rt.core.math.Utility.PI_F_TWO;
import static org.rt.core.math.Utility.cosf;
import static org.rt.core.math.Utility.sinf;
import static org.rt.core.math.Utility.sqrtf;
import java.io.File;
import static java.lang.Math.max;
import java.net.URI;

/**
 *
 * @author user
 */
public class Envmap extends AbstractBackground
{    
    public HDR hdr;    
    public Distribution2D distribution2D;
    
    public float power = 1f;
    
    public Envmap(URI uri)
    {
        super();
        hdr = HDRBitmapReader.load(uri.toString()).bestResizeFit(2000);
        init();
    }
    
    public Envmap(File file)
    {
        super();
        hdr = HDRBitmapReader.load(file.toURI()).bestResizeFit(2000);
        init();
    }
    
    public Envmap(HDR hdr)
    {
        super();
        this.hdr = hdr;
        init();
    }
    
    public Envmap()
    {
        //super();
        String fileName = "schoenbrunn.hdr";
        String filePath = "C:\\Users\\user\\Documents\\hdr\\";
        String file = filePath+fileName;
        
        hdr = HDRBitmapReader.load(file).bestResizeFit(2000).tonemap();
        init();
    }
    
    public final void init()
    {        
        int nu = (int) hdr.getWidth();
        int nv = (int) hdr.getHeight();
        
        distribution2D = new Distribution2D(hdr.getLuminanceArray(), nu, nv);
    }
    
    @Override
    public Color illuminate(Scene scene, Point3f receivingPosition, Point2f rndTuple, Ray rayToLight, FloatValue cosAtLight) {
       
        Vector3f directionToLight = sampleDirection(null);
           
        rayToLight.d.set(directionToLight);
        rayToLight.o.set(receivingPosition);
        rayToLight.setMax(1e36f);
        rayToLight.init();
        
        Color radiance = getColor(directionToLight);
        
        if(cosAtLight != null)
            cosAtLight.value = 1.f;
       
        return radiance.mul(power);
    }

    //Please FIX ME: Sampling is wrong
    @Override
    public Color emit(Scene scene, Point2f dirRndTuple, Point2f posRndTuple, Ray rayFromLight, FloatValue cosAtLight) {
        //direction from light
        Vector3f direction = sampleDirection(null);
        
        //radiance
        Color radiance = getColor(direction);
        
        //Sample uniform disk
        Point2f xy = Utility.sampleConcentricDisc(posRndTuple.x, posRndTuple.y);
        
        Frame frame = new Frame();
        frame.setFromZ(direction);
        
        Vector3f a = direction.neg().add(frame.binormal().mul(xy.x)).add(frame.tangent().mul(xy.y));
        
        //Position from light
        Point3f position = scene.getBoundingSphere().c.add(a.mul(scene.getBoundingSphere().r));
        
        rayFromLight.d.set(direction);
        rayFromLight.o.set(position);
        rayFromLight.init();
        
        // Not used for infinite or delta lights
        if(cosAtLight != null)
            cosAtLight.value = 1.f;

        return radiance.mul(power);
    }

    @Override
    public Color radiance(Scene scene, Point3f hitPoint, Vector3f direction, FloatValue cosAtLight) {
        
        //System.out.println("bu");
        return getColor(direction).mul(power);
    }

    @Override
    public boolean isFinite() {
        return false;
    }

    @Override
    public boolean isDelta() {
         return false;
    }

    @Override
    public boolean isAreaLight() {
        return false;
    }

    @Override
    public float directPdfW(Scene scene, Point3f p, Vector3f w) {
        return pdfW(w);
    }

    @Override
    public float directPdfA(Scene scene, Vector3f w) {
        return pdfW(w);
    }

    @Override
    public float emissionPdfW(Scene scene, Vector3f w, float cosAtLight) {
        float directPdf = pdfW(w);
        float positionPdf = Utility.concentricDiscPdfA() /
            scene.getBoundingSphere().radiusSqr;
        
        return directPdf * positionPdf;
    }
    
    public float pdfW(Vector3f d)
    {
        float sinTheta = sinTheta(d);
        if(sinTheta == 0f)
            return 0;
        
        Point2i uv = getUV(d);
        Point2f uv_f = new Point2f(uv.x / hdr.getWidth(), uv.y / hdr.getHeight());
        
        float pdfW =  distribution2D.pdfContinuous(uv_f) / (2f * PI_F_TWO * sinTheta );   
        
        //System.out.println(pdfW);
        
        return pdfW;
        //return 1f;
    }
    
    private float sinTheta(Vector3f v)
    {
        float cosTheta = v.y;
        float sinTheta2 = max(0f, 1f - cosTheta * cosTheta);
        return sqrtf(sinTheta2);
    }
    
    public Color getColor(Point2i uv)
    {
        return hdr.getColor(uv.x, uv.y);
    }
            
    public Point2i getUV(Vector3f d)
    {
        return SphericalCoordinate.getRange2i(d, hdr.getWidth(), hdr.getHeight());        
    }
    
    public Color getColor(Vector3f d)
    {       
        Point2i uv = getUV(d);                
        return hdr.getColor(uv.x, uv.y);
    }
    
    public Point2f toSpherical(Point2i uv)
    {
        Point2f phitheta = new Point2f();      
        
        // transform to range [0, 1]
        float scaleY = uv.y / hdr.getHeight();
        float scaleX = uv.x / hdr.getWidth();
        
        phitheta.x = PI_F * scaleY;                     // phi
        phitheta.y = PI_F * (2 * scaleX - 1f);          // theta
        
        return phitheta;
    }
    
    public Vector3f toDirection(Point2i uv)
    {
        Vector3f dest = new Vector3f();
        Point2f phitheta = toSpherical(uv);
        
        float phi = phitheta.x;
        float theta = phitheta.y;
                
        dest.x = sinf(phi) * sinf(theta);
        dest.y = cosf(phi);
        dest.z = -sinf(phi) * cosf(theta);
                    
        return dest;
    }
        
    public Vector3f sampleDirection(FloatValue pdf)
    {       
        Point2i uv = sampleUV(pdf);        
        return toDirection(uv);
    }

    public Point2i sampleUV(FloatValue pdf)
    {        
       return distribution2D.sampleDiscrete(Rng.getFloat(), Rng.getFloat(), pdf);        
    }    

    @Override
    public boolean isCompound() {
        return false;
    }
}

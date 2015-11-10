/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import core.coordinates.Normal3f;
import core.math.BoundingBox;
import core.math.BoundingSphere;
import core.math.Ray;
import java.util.ArrayList;

/**
 *
 * @author user
 */
public abstract class AbstractScene 
{    
    public Camera camera = null;
    public AbstractAccelerator accelerator = null;
    public ArrayList<AbstractPrimitive> primitives = null;
    public LightCache lights = null;    
    
    public abstract void init();
    
    public boolean intersect(Ray ray, Intersection isect)
    {
        boolean hit = accelerator.intersect(ray, isect);
        
        return hit;
    }
    
    public boolean intersectP(Ray ray)
    {
        return accelerator.intersectP(ray);
    }
    
    public boolean occluded(Ray ray)
    {
        return accelerator.intersectP(ray);
    }
    
    public BoundingBox getWorldBounds()
    {
        return accelerator.getWorldBounds();
    }
    
    public BoundingSphere getBoundingSphere()
    {
        return getWorldBounds().getBoundingSphere();
    }
    
    public void initLights()
    {
        lights.clear();
        
        for(AbstractPrimitive prim : primitives)
           lights.add(prim);
        
        lights.addBackgroundLight();
    }
}

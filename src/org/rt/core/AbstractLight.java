/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rt.core;

import org.rt.core.coordinates.Point2f;
import org.rt.core.coordinates.Point3f;
import org.rt.core.coordinates.Vector3f;
import org.rt.core.color.Color;
import org.rt.core.math.FloatValue;
import org.rt.core.math.Ray;
import org.rt.core.math.Transform;
import java.util.ArrayList;

/**
 *
 * @author user
 */
public abstract class AbstractLight 
{    
    protected final Transform lightToWorld;    
    protected final Transform worldToLight;
    
    public AbstractLight(Transform l2w)
    {
        lightToWorld = l2w;
        worldToLight = l2w.inverse();
    }
    
    /* \brief Illuminates a given point in the scene.
     *
     * Given a point and two random samples (e.g., for position on area lights),
     * this method returns direction from point to light, distance,
     * pdf of having chosen this direction (e.g., 1 / area).
     * Optionally also returns pdf of emitting particle in this direction,
     * and cosine from lights normal (helps with PDF of hitting the light,
     * but set to 1 for point lights).
     *
     * Returns radiance.
     */
    
    public abstract Color illuminate(
        Scene               scene,
        Point3f             receivingPosition,
        Point2f             rndTuple,
        Ray                 rayToLight,        
        FloatValue          cosAtLight);
    
    /* \brief Emits particle from the light.
     *
     * Given two sets of random numbers (e.g., position and direction on area light),
     * this method generates a position and direction for light particle, along
     * with the pdf.
     *
     * Can also supply pdf (w.r.t. area) of choosing this position when calling
     * Illuminate. Also provides cosine on the light (this is 1 for point lights etc.).
     *
     * Returns "energy" that particle carries
     */
    public abstract Color emit(
        Scene               scene,
        Point2f             dirRndTuple,
        Point2f             posRndTuple,
        Ray                 rayFromLight,        
        FloatValue          cosAtLight);
    
    /* \brief Returns radiance for ray randomly hitting the light
     *
     * Given ray direction and hitpoint, it returns radiance.
     * Can also provide area pdf of sampling hitpoint in Illuminate,
     * and of emitting particle along the ray (in opposite direction).
     */    
    public abstract Color radiance(
        Scene               scene,
        Point3f             hitPoint,
        Vector3f            direction,
        FloatValue          cosAtLight);
    
     // Whether the light has a finite extent (area, point) or not (directional, env. map)
    public abstract boolean isFinite();
    
    // Whether the light has delta function (point, directional) or not (area)
    public abstract boolean isDelta();
    
    // Is a primitive as a light source
    public abstract boolean isAreaLight();
    
    public abstract float directPdfW(Scene scene, Point3f p, Vector3f w);
    public abstract float directPdfA(Scene scene, Vector3f w);
    public abstract float emissionPdfW(Scene scene, Vector3f w, float cosAtLight);
    
    // Is this a compound emitter consisting of several sub-objects
    public abstract boolean isCompound();
    
    public ArrayList<AbstractLight> getLights()
    {
        return null;
    }  
}
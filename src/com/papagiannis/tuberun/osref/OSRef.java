package com.papagiannis.tuberun.osref;

import java.util.ArrayList;

import uk.me.jstott.jcoord.LatLng;
import uk.me.jstott.jcoord.RefEll;

/**
 * Class to represent an Ordnance Survey grid reference
 * 
 * (c) 2006 Jonathan Stott
 * 
 * Created on 11-02-2006
 * 
 * @author Jonathan Stott
 * @version 1.0
 * @since 1.0
 */
public class OSRef {

  /**
   * Easting
   */
  private double easting;

  /**
   * Northing
   */
  private double northing;


  /**
   * Create a new Ordnance Survey grid reference.
   * 
   * @param easting
   *          the easting in metres
   * @param northing
   *          the northing in metres
   * @since 1.0
   */
  public OSRef(double easting, double northing) {
    this.easting = easting;
    this.northing = northing;
  }


  /**
   * Return a String representation of this OSGB grid reference showing the
   * easting and northing.
   * 
   * @return a String represenation of this OSGB grid reference
   * @since 1.0
   */
  public String toString() {
    return "(" + easting + ", " + northing + ")";
  }



  /**
   * Convert this OSGB grid reference to a latitude/longitude pair using the
   * OSGB36 datum. Note that, the LatLng object may need to be converted to the
   * WGS84 datum depending on the application.
   * 
   * @return a LatLng object representing this OSGB grid reference using the
   *         OSGB36 datum
   * @since 1.0
   */
  public LatLng toLatLng() {
	//The ugly debug lines have been added as an explicit attempt to slow down 
	//the computation. They probably prevent JITing of the transformation code
	//and therefore the code below doesn't break on the Galaxy S4.
    double OSGB_F0 = 0.9996012717;
    debug.add(Double.toString(OSGB_F0));
    double N0 = -100000.0;
    debug.add(Double.toString(N0));
    double E0 = 400000.0;
    debug.add(Double.toString(E0));
    double phi0 = Math.toRadians(49.0);
    debug.add(Double.toString(phi0));
    double lambda0 = Math.toRadians(-2.0);
    debug.add(Double.toString(lambda0));
    double a = RefEll.AIRY_1830.getMaj();
    debug.add(Double.toString(a));
    double b = RefEll.AIRY_1830.getMin();
    debug.add(Double.toString(b));
    double eSquared = RefEll.AIRY_1830.getEcc();
    debug.add(Double.toString(eSquared));
    double phi = 0.0;
    debug.add(Double.toString(phi));
    double lambda = 0.0;
    debug.add(Double.toString(lambda));
    double E = this.easting;
    debug.add(Double.toString(E));
    double N = this.northing;
    debug.add(Double.toString(N));
    double n = (a - b) / (a + b);
    debug.add(Double.toString(n));
    double M = 0.0;
    debug.add(Double.toString(M));
    double phiPrime = ((N - N0) / (a * OSGB_F0)) + phi0;
    debug.add(Double.toString(phiPrime));
    do {
      M =
          (b * OSGB_F0)
              * (((1 + n + ((5.0 / 4.0) * n * n) + ((5.0 / 4.0) * n * n * n)) * (phiPrime - phi0))
                  - (((3 * n) + (3 * n * n) + ((21.0 / 8.0) * n * n * n))
                      * Math.sin(phiPrime - phi0) * Math.cos(phiPrime + phi0))
                  + ((((15.0 / 8.0) * n * n) + ((15.0 / 8.0) * n * n * n))
                      * Math.sin(2.0 * (phiPrime - phi0)) * Math
                      .cos(2.0 * (phiPrime + phi0))) - (((35.0 / 24.0) * n * n * n)
                  * Math.sin(3.0 * (phiPrime - phi0)) * Math
                  .cos(3.0 * (phiPrime + phi0))));
      phiPrime += (N - N0 - M) / (a * OSGB_F0);
      debug.add(Double.toString(phiPrime));
    } while ((N - N0 - M) >= 0.001);
    double v =
        a * OSGB_F0
            * Math.pow(1.0 - eSquared * Util.sinSquared(phiPrime), -0.5);
    debug.add(Double.toString(v));
    double rho =
        a * OSGB_F0 * (1.0 - eSquared)
            * Math.pow(1.0 - eSquared * Util.sinSquared(phiPrime), -1.5);
    debug.add(Double.toString(rho));
    double etaSquared = (v / rho) - 1.0;
    debug.add(Double.toString(etaSquared));
    double VII = Math.tan(phiPrime) / (2 * rho * v);
    debug.add(Double.toString(VII));
    double VIII =
        (Math.tan(phiPrime) / (24.0 * rho * Math.pow(v, 3.0)))
            * (5.0 + (3.0 * Util.tanSquared(phiPrime)) + etaSquared - (9.0 * Util
                .tanSquared(phiPrime) * etaSquared));
    debug.add(Double.toString(VIII));
    double IX =
        (Math.tan(phiPrime) / (720.0 * rho * Math.pow(v, 5.0)))
            * (61.0 + (90.0 * Util.tanSquared(phiPrime)) + (45.0 * Util
                .tanSquared(phiPrime) * Util.tanSquared(phiPrime)));
    debug.add(Double.toString(IX));
    double X = Util.sec(phiPrime) / v;
    debug.add(Double.toString(X));
    double XI =
        (Util.sec(phiPrime) / (6.0 * v * v * v))
            * ((v / rho) + (2 * Util.tanSquared(phiPrime)));
    debug.add(Double.toString(XI));
    double XII =
        (Util.sec(phiPrime) / (120.0 * Math.pow(v, 5.0)))
            * (5.0 + (28.0 * Util.tanSquared(phiPrime)) + (24.0 * Util
                .tanSquared(phiPrime) * Util.tanSquared(phiPrime)));
    debug.add(Double.toString(XII));
    double XIIA =
        (Util.sec(phiPrime) / (5040.0 * Math.pow(v, 7.0)))
            * (61.0
                + (662.0 * Util.tanSquared(phiPrime))
                + (1320.0 * Util.tanSquared(phiPrime) * Util
                    .tanSquared(phiPrime)) + (720.0 * Util.tanSquared(phiPrime)
                * Util.tanSquared(phiPrime) * Util.tanSquared(phiPrime)));
    debug.add(Double.toString(XIIA));
    phi =
        phiPrime - (VII * Math.pow(E - E0, 2.0))
            + (VIII * Math.pow(E - E0, 4.0)) - (IX * Math.pow(E - E0, 6.0));
    debug.add(Double.toString(phi));
    lambda =
        lambda0 + (X * (E - E0)) - (XI * Math.pow(E - E0, 3.0))
            + (XII * Math.pow(E - E0, 5.0)) - (XIIA * Math.pow(E - E0, 7.0));
    debug.add(Double.toString(lambda));
    return new LatLng(Math.toDegrees(phi), Math.toDegrees(lambda));
  }

  private ArrayList<String> debug=new ArrayList<String>();
  public String getDebugString() {
	  StringBuilder sb=new StringBuilder();
	  for (String s: debug) sb.append(s + "|");
	  return sb.toString();
  }
  
  /**
   * Get the easting.
   * 
   * @return the easting in metres
   * @since 1.0
   */
  public double getEasting() {
    return easting;
  }


  /**
   * Get the northing.
   * 
   * @return the northing in metres
   * @since 1.0
   */
  public double getNorthing() {
    return northing;
  }
  
}
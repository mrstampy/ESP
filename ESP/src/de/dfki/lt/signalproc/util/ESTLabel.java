/**
 * Copyright 2007 DFKI GmbH.
 * All Rights Reserved.  Use is subject to license terms.
 * 
 * Permission is hereby granted, free of charge, to use and distribute
 * this software and its documentation without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of this work, and to
 * permit persons to whom this work is furnished to do so, subject to
 * the following conditions:
 * 
 * 1. The code must retain the above copyright notice, this list of
 *    conditions and the following disclaimer.
 * 2. Any modifications must be clearly marked as such.
 * 3. Original authors' names are not deleted.
 * 4. The authors' names are not used to endorse or promote products
 *    derived from this software without specific prior written
 *    permission.
 *
 * DFKI GMBH AND THE CONTRIBUTORS TO THIS WORK DISCLAIM ALL WARRANTIES WITH
 * REGARD TO THIS SOFTWARE, INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS, IN NO EVENT SHALL DFKI GMBH NOR THE
 * CONTRIBUTORS BE LIABLE FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL
 * DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 * PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS
 * ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF
 * THIS SOFTWARE.
 */

package de.dfki.lt.signalproc.util;

// TODO: Auto-generated Javadoc
/**
 * The Class ESTLabel.
 *
 * @author oytun.turk
 * 
 * A class to keep all information on a single EST format label
 */
public class ESTLabel {
    
    /** The time. */
    public double time; //Ending time of phonetic label
    
    /** The status. */
    public int status; //Status
    
    /** The phn. */
    public String phn; //Phoneme
    
    /** The ll. */
    public double ll; //log likelihood
    
    /** The rest. */
    public String[] rest; //If the label contains more fields, get them as text
    
    /** The values rest. */
    public double[] valuesRest; //If some of the <rest> are numbers, convert them to doubles and keep
    
    /**
     * Instantiates a new EST label.
     *
     * @param newTime the new time
     * @param newStatus the new status
     * @param newPhn the new phn
     * @param newll the newll
     */
    public ESTLabel(double newTime, int newStatus, String newPhn, double newll)
    {
        this(newTime, newStatus, newPhn, newll, null, null);
    }
    
    /**
     * Instantiates a new EST label.
     */
    public ESTLabel()
    {
        this(-1.0, 0, "", Double.NEGATIVE_INFINITY, null, null);
    }
    
    /**
     * Instantiates a new EST label.
     *
     * @param newTime the new time
     * @param newStatus the new status
     * @param newPhn the new phn
     * @param newll the newll
     * @param restIn the rest in
     */
    public ESTLabel(double newTime, int newStatus, String newPhn, double newll, String[] restIn)
    {
        this(newTime, newStatus, newPhn, newll, restIn, null);
    }
    
    /**
     * Instantiates a new EST label.
     *
     * @param newTime the new time
     * @param newStatus the new status
     * @param newPhn the new phn
     * @param newll the newll
     * @param restIn the rest in
     * @param valuesRestIn the values rest in
     */
    public ESTLabel(double newTime, int newStatus, String newPhn, double newll, String[] restIn, double[] valuesRestIn)
    {
        time = newTime;
        status = newStatus;
        phn = newPhn;
        ll = newll;
        
        if (restIn!=null && restIn.length>0)
        {
            rest = new String[restIn.length];
            for (int i=0; i<restIn.length; i++)
                rest[i] = restIn[i];
        }
        else
            rest = null;
        
        if (valuesRestIn!=null && valuesRestIn.length>0)
        {
            valuesRest = new double[valuesRestIn.length];
            for (int i=0; i<valuesRestIn.length; i++)
                valuesRest[i] = valuesRestIn[i];
        }
        else
            valuesRest = null;
    }
    
    /**
     * Instantiates a new EST label.
     *
     * @param lab the lab
     */
    public ESTLabel(ESTLabel lab)
    {
        copyFrom(lab);
    }
    
    /**
     * Copy from.
     *
     * @param lab the lab
     */
    public void copyFrom(ESTLabel lab)
    {
        time = lab.time;
        status = lab.status;
        phn = lab.phn;
        ll = lab.ll;
        if (lab.rest!=null && lab.rest.length>0)
        {
            rest = new String[lab.rest.length];
            for (int i=0; i<lab.rest.length; i++)
                rest[i] = lab.rest[i];
        }
        else
            rest = null;
        
        if (lab.valuesRest!=null && lab.valuesRest.length>0)
        {
            valuesRest = new double[lab.valuesRest.length];
            for (int i=0; i<lab.valuesRest.length; i++)
                valuesRest[i] = lab.valuesRest[i];
        }
        else
            valuesRest = null;
    }
    
    //Display label entries
    /**
     * Prints the.
     */
    public void print()
    {
        System.out.println("Time=" + String.valueOf(time) + " s. " + 
                           "Stat=" + String.valueOf(status) + " " +
                           "Phoneme=" + phn + " " +
                           "Log-likelihood=" + String.valueOf(ll));
    }
}

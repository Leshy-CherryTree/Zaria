/****************************************/
/* StateMachineInterface.java			*/
/* Created on: Feb 2, 2010				*/
/* Copyright Cherry Tree Studio 2010	*/
/* Released under EUPL v1.1				*/
/* Ported from the jPAJ project.		*/
/****************************************/

package eu.cherrytree.zaria.base;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 *
 * @param <State> Enum defining the possible states.
 */
public interface StateMachineInterface <State extends Enum, Params>
{
    //--------------------------------------------------------------------------

    public void setState(State s, Params params);
    public State getState();
    public State getPreviousState();

    //--------------------------------------------------------------------------
}

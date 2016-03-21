/****************************************/
/* StateMachine.java						*/
/* Created on: Feb 2, 2012				*/
/* Copyright Cherry Tree Studio 2012		*/
/* Released under EUPL v1.1				*/
/* Ported from the jPAJ project.			*/
/****************************************/

package eu.cherrytree.zaria.base;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 *
 * @param <States> Enum defining the possible states.
 */
public class StateMachine <States extends Enum, Params> implements StateMachineInterface<States, Params>
{
    //--------------------------------------------------------------------------

    private States currentState;
    private States previousState;
    private boolean ended = true;
    private boolean started = false;
	private Params startParams;

    //--------------------------------------------------------------------------

    public StateMachine(States initialState, Params initialParams)
    {
        currentState = initialState;
		startParams = initialParams;
    }

    //--------------------------------------------------------------------------

    @Override
    public void setState(States state, Params params)
    {
        assert ended;
		assert started;
		
		previousState = currentState;

        currentState = state;
		startParams = params;
		
        ended = false;
        started = false;
    }

    //--------------------------------------------------------------------------

    @Override
    public States getState()
    {
        return currentState;
    }

	//--------------------------------------------------------------------------
	
	public Params getStartParams()
	{
		return startParams;
	}
	
	//--------------------------------------------------------------------------

    @Override
    public States getPreviousState()
    {
        return previousState;
    }

    //--------------------------------------------------------------------------

    public void setStateEnded()
    {
        ended = true;		
    }

    //--------------------------------------------------------------------------

    public boolean isStateEnded()
    {
        return ended;
    }

    //--------------------------------------------------------------------------

    public void setStateStarted()
    {
        started = true;
		startParams = null;
    }

    //--------------------------------------------------------------------------

    public boolean isStateStarted()
    {
        return started;
    }

    //--------------------------------------------------------------------------
}

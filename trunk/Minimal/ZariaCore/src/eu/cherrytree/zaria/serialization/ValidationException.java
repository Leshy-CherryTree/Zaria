/****************************************/
/* ValidationException.java             */
/* Created on: 11-May-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.serialization;

import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ValidationException extends IOException
{
	//--------------------------------------------------------------------------
	
	private ArrayList<DefinitionValidation> validations;
	
	//--------------------------------------------------------------------------
	
    public ValidationException(String msg, ArrayList<DefinitionValidation> validations)
	{				
        super(msg + generateMessage(validations));
		
		assert !validations.isEmpty();
			
		this.validations = validations;
    }
	
	//--------------------------------------------------------------------------
	
	private static String generateMessage(ArrayList<DefinitionValidation> validations)
	{
		String ret = "";
		
		for(DefinitionValidation validation : validations)
			ret += "Defintion " + validation.getClassName() + " [" + validation.getID() + "] was not validated properly.\n" + validation;
		
		if(!ret.isEmpty())
			ret = "\n\n" + ret;
		
		return ret;
	}
	
	//--------------------------------------------------------------------------
				
	public ArrayList<DefinitionValidation> getValidation()
	{
		return validations;
	}
	
	//--------------------------------------------------------------------------
}

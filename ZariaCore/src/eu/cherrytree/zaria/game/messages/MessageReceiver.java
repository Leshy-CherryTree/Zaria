/****************************************/
/* MessageReceiver.java				*/
/* Created on: 20-10-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.game.messages;

import eu.cherrytree.zaria.game.messages.annotations.HandleMessage;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class MessageReceiver
{
	//--------------------------------------------------------------------------
	
	private HashMap<Class<? extends Message>, ArrayList<MessageHandler>> messageHandlers = new HashMap<>();
	
	//--------------------------------------------------------------------------
	
	public final void addMessageHandler(MessageHandler handler)
	{
		Class cls = handler.getClass();
		
		while (cls != null)
		{
			@SuppressWarnings("unchecked")
			HandleMessage msg_annotation = (HandleMessage) cls.getAnnotation(HandleMessage.class);
			
			if (msg_annotation != null)
			{
				for (Class<? extends Message> msg : msg_annotation.value())
				{
					if (!messageHandlers.containsKey(msg))
						messageHandlers.put(msg, new ArrayList<MessageHandler>());

					// So that last added message handlers would handle the message first.
					messageHandlers.get(msg).add(0, handler);
				}	
			}
			
			cls = cls.getSuperclass();
		}
	}
	
	//--------------------------------------------------------------------------
	
	public final void removeMessageHandler(MessageHandler handler)
	{
		Class cls = handler.getClass();
		
		while (cls != null)
		{
			@SuppressWarnings("unchecked")
			HandleMessage msg_annotation = (HandleMessage) cls.getAnnotation(HandleMessage.class);
			
			if (msg_annotation != null)
			{
				for (Class<? extends Message> msg : msg_annotation.value())
				{
					assert messageHandlers.containsKey(msg);
				
					messageHandlers.get(msg).remove(handler);

					if (messageHandlers.get(msg).isEmpty())
						messageHandlers.remove(msg);
				}	
			}
			
			cls = cls.getSuperclass();
		}
	}
	
	//--------------------------------------------------------------------------
	
	public final <MsgType extends Message> MsgType sendMessage(MsgType message)
	{
		assert message != null;
		
		message.startedProcessing();
		
		ArrayList<MessageHandler> handlers = messageHandlers.get(message.getClass());
		
		if (handlers != null)
		{
			assert !handlers.isEmpty();
			
			for (MessageHandler handler : handlers)
			{
				if (message.isContinueProcessing())
					handler.handleMessage(message);
				else
					break;
			}
		}		

		message.finishedProcessing();
		
		return message;		
	}
	
	//--------------------------------------------------------------------------
}

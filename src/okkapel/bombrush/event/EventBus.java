package okkapel.bombrush.event;

import java.lang.reflect.Method;
import java.util.List;

public class EventBus {
	
	private List<Method> eventHandlers;
	
	public EventBus() {
		
	}
	
	@SuppressWarnings(value={"rawtypes", "unchecked"})
	public void registerEventHandler(Class<? extends Object> cls) {
		Method[] mets = cls.getMethods();
		for(Method m : mets) {
			if(m.getAnnotation(EventHandler.class) != null) {
				Class[] paramTypes = m.getParameterTypes();
				if(paramTypes.length == 1) {
					System.out.println("Method: " + m.getName());
					if(paramTypes[0].isAssignableFrom(Event.class)) {
						System.out.println("woot!");
					}
				}
			}
		}
	}
	
}

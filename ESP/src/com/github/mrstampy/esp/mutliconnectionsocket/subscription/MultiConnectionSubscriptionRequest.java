package com.github.mrstampy.esp.mutliconnectionsocket.subscription;

import java.io.Serializable;

public interface MultiConnectionSubscriptionRequest<E extends Enum<E>> extends Serializable {

	E[] getEventTypes();

	boolean containsEventType(E eventType);

}

# ESP - An EEG Signal Processing Library

This library provides the ability to connect to the [NeuroSky](http://www.neurosky.com) MindWave ThinkGear connector. As of version 1.0.0 the core functionality has been abstracted, allowing implementations for other devices to be created. It is designed to be fast and efficient, using high	performance Java libraries:

* [Disruptor](https://code.google.com/p/disruptor/)
* [Javolution](http://javolution.org/)
* [Apache MINA](http://mina.apache.org/)

## Maven Dependency
       <dependency>
           <groupId>com.github.mrstampy</groupId>
           <artifactId>esp</artifactId>
           <version>1.1</version>
       </dependency>

Usage of the library is straight-forward. One only needs to instantiate the MultiConnectionThinkGearSocket, add a listener, start the MultiConnectionThinkGearSocket and deal with the events as they occur.

## Multiple Connections

As implied by the name of the class, the MultiConnectionThinkGearSocket is capable of not only processing ThinkGear messages for a single application but also of broadcasting the messages to separate applications, even on separate machines and devices! This functionality is disabled by default. To enable it either set the System property 'broadcast.messages' to true or instantiate the MultiConnectionThinkGearSocket using one of its other constructors:

	MultiConnectionThinkGearSocket socket = new MultiConnectionThinkGearSocket("ThinkGear socket hostname", true);
	...
	socket.start();

	...and then in another application:

ThinkGearSocketConnector connector = new ThinkGearSocketConnector("Hostname running MultiConnectionThinkGearSocket");
		
	connector.addThinkGearEventListener(new ThinkGearEventListenerAdapter() {
		// overriding methods as appropriate
	});
	
	connector.connect();
	
	//after successful connection...
	connector.subscribe(EventType...);

## Raw Signal Processing

Additional classes have been added to assist with raw signal processing. The examples shown will work with the NeuroSky raw output however these classes can be used for any DSP work with low sample sizes (ie. < 1000 samples / sec).  For greater sample sizes the utilization of	primitive arrays is recommended.

Additional functionality is described in the JavaDocs. This work is	released under the GPL 3.0 license. No warranty of any kind is offered.

[Entrainer](http://entrainer.sourceforge.net) Copyright (C) 2008 - 2014 Burton Alexander. 

eSense, ThinkGear, MDT, NeuroBoy and NeuroSky are trademarks of [NeuroSky Inc](http://www.neurosky.com).
